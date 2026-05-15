package com.tributes;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class DeathListener implements Listener {

    private final TributesPlugin plugin;

    public DeathListener(TributesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        plugin.addDeathRecord(new DeathRecord(
                player.getUniqueId(),
                player.getName(),
                net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(event.deathMessage()),
                java.time.Instant.now()
        ));

        // Play cannon sound for all online players
        for (Player online : plugin.getServer().getOnlinePlayers()) {
            online.playSound(online.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.5f);
        }

        // Broadcast tribute announcement
        Component tributeMsg = Component.text("A tribute has fallen: ")
                .color(NamedTextColor.RED)
                .decorate(TextDecoration.BOLD)
                .append(player.displayName()
                        .color(NamedTextColor.GOLD)
                        .decorate(TextDecoration.BOLD));
        plugin.getServer().broadcast(tributeMsg);
    }
}
