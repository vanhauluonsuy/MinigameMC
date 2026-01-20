package me.neyucity.project.neyugame.topper;

import me.neyucity.project.neyugame.Neyugame;
import org.bukkit.Bukkit;

public class TopperHook {
    private final Neyugame plugin;
    public TopperHook(Neyugame plugin) { this.plugin = plugin; }
    public void tryRegister() {
        if (Bukkit.getPluginManager().isPluginEnabled("Topper")) {
            plugin.getLogger().info("Topper found! Use %neyu_balance% for ranking.");
        }
    }
}