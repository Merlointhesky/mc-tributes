package com.tributes;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class LoginListener implements Listener {

    private final TributesPlugin plugin;

    public LoginListener(TributesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Send MOTD
        String motd = plugin.getMotd(player.getName());
        for (String line : motd.split("\n")) {
            player.sendMessage(Component.text(line).color(NamedTextColor.GOLD));
        }

        // Send tributes list
        var recentDeaths = plugin.getRecentDeaths();
        if (recentDeaths.isEmpty()) {
            player.sendMessage(Component.text("No tributes have fallen in the last 24 hours.")
                    .color(NamedTextColor.GREEN));
        } else {
            player.sendMessage(Component.text("--- Tributes Fallen (Last 24h) ---")
                    .color(NamedTextColor.RED)
                    .decorate(TextDecoration.BOLD));
            for (DeathRecord record : recentDeaths) {
                player.sendMessage(Component.text("  - " + record.getPlayerName() + ": " + record.getDeathMessage())
                        .color(NamedTextColor.GRAY));
            }
            player.sendMessage(Component.text("Total: " + recentDeaths.size() + " tribute(s)")
                    .color(NamedTextColor.RED));
        }
    }
}
