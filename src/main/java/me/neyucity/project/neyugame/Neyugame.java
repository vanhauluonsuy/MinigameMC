package me.neyucity.project.neyugame;

import me.neyucity.project.neyugame.commands.NeyuCommand;
import me.neyucity.project.neyugame.managers.MoneyManager;
import me.neyucity.project.neyugame.placeholder.NeyuPlaceholder;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.UUID;

public class Neyugame extends JavaPlugin {
    private static Neyugame instance;
    private MoneyManager moneyManager;
    private static Economy econ = null;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        moneyManager = new MoneyManager(this);
        moneyManager.init();

        if (!setupEconomy()) {
            getLogger().severe("Khong tim thay Vault/Economy! Tinh nang quydoi se loi.");
        }

        getCommand("neyu").setExecutor(new NeyuCommand(this));

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new NeyuPlaceholder(this).register();
        }

        startTopAnnouncer();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        econ = rsp.getProvider(); // ĐÃ SỬA: Dùng getProvider() thay vì getEconomy()
        return econ != null;
    }

    private void startTopAnnouncer() {
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            var top = moneyManager.getTop(1);
            if (top.isEmpty()) return;
            String uuidStr = top.keySet().iterator().next();
            double bal = top.get(uuidStr);
            String name = Bukkit.getOfflinePlayer(UUID.fromString(uuidStr)).getName();
            if (name == null) name = "An danh";

            String title = ChatColor.GOLD + "⭐ " + ChatColor.BOLD + "ĐẠI GIA TOP 1" + ChatColor.GOLD + " ⭐";
            String sub = ChatColor.YELLOW + name + ChatColor.WHITE + " sở hữu " + ChatColor.GREEN + String.format("%,d", (long)bal) + " xu";

            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendTitle(title, sub, 10, 80, 10);
            }
        }, 1200L, 1200L);
    }

    @Override
    public void onDisable() {
        if (moneyManager != null) moneyManager.shutdown();
    }

    public static Neyugame getInstance() { return instance; }
    public MoneyManager getMoneyManager() { return moneyManager; }
    public static Economy getVaultEconomy() { return econ; }
}