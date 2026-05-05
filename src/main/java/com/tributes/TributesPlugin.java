package com.tributes;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.bukkit.plugin.java.JavaPlugin;

public class TributesPlugin extends JavaPlugin {

    private final List<DeathRecord> deathRecords = new ArrayList<>();
    private File motdFile;
    private File deathsFile;
    private Gson gson;

    @Override
    public void onEnable() {
        gson = new GsonBuilder()
                .registerTypeAdapter(java.time.Instant.class, new DeathRecord.InstantAdapter())
                .setPrettyPrinting()
                .create();

        // Ensure data folder exists
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        motdFile = new File(getDataFolder(), "motd.txt");
        if (!motdFile.exists()) {
            saveResource("motd.txt", false);
        }

        deathsFile = new File(getDataFolder(), "deaths.json");
        loadDeathsFromFile();

        // Register listeners
        getServer().getPluginManager().registerEvents(new DeathListener(this), this);
        getServer().getPluginManager().registerEvents(new LoginListener(this), this);

        // Start periodic cleanup task (every 5 minutes)
        getServer().getScheduler().runTaskTimer(this, this::pruneOldRecords,
                5 * 60 * 20L, 5 * 60 * 20L);

        getLogger().info("Tributes plugin enabled - may the odds be ever in your favour!");
    }

    @Override
    public void onDisable() {
        saveDeathsToFile();
        getLogger().info("Tributes plugin disabled.");
    }

    public void addDeathRecord(DeathRecord record) {
        synchronized (deathRecords) {
            deathRecords.add(record);
        }
        saveDeathsToFile();
    }

    public List<DeathRecord> getRecentDeaths() {
        pruneOldRecords();
        synchronized (deathRecords) {
            return new ArrayList<>(deathRecords);
        }
    }

    private void pruneOldRecords() {
        boolean changed;
        synchronized (deathRecords) {
            int before = deathRecords.size();
            deathRecords.removeIf(DeathRecord::isOlderThan24Hours);
            changed = deathRecords.size() != before;
        }
        if (changed) {
            saveDeathsToFile();
        }
    }

    private void loadDeathsFromFile() {
        if (!deathsFile.exists()) {
            return;
        }
        try (Reader reader = Files.newBufferedReader(deathsFile.toPath(), StandardCharsets.UTF_8)) {
            List<DeathRecord> loaded = gson.fromJson(reader,
                    new TypeToken<List<DeathRecord>>() {}.getType());
            if (loaded != null) {
                synchronized (deathRecords) {
                    deathRecords.clear();
                    // Only load records within the last 24h
                    for (DeathRecord record : loaded) {
                        if (!record.isOlderThan24Hours()) {
                            deathRecords.add(record);
                        }
                    }
                }
            }
            getLogger().info("Loaded " + deathRecords.size() + " death record(s) from deaths.json");
        } catch (IOException e) {
            getLogger().log(Level.WARNING, "Could not read deaths.json", e);
        }
    }

    private void saveDeathsToFile() {
        synchronized (deathRecords) {
            try {
                String json = gson.toJson(deathRecords);
                Files.writeString(deathsFile.toPath(), json, StandardCharsets.UTF_8);
            } catch (IOException e) {
                getLogger().log(Level.WARNING, "Could not write deaths.json", e);
            }
        }
    }

    public String getMotd(String playerName) {
        try (BufferedReader reader = Files.newBufferedReader(motdFile.toPath())) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line.replace("{player}", playerName)).append("\n");
            }
            // Remove trailing newline
            if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '\n') {
                sb.setLength(sb.length() - 1);
            }
            return sb.toString();
        } catch (IOException e) {
            getLogger().log(Level.WARNING, "Could not read motd.txt", e);
            return "Welcome, " + playerName + "!";
        }
    }
}
