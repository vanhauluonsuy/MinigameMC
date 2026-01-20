package me.neyucity.project.neyugame.commands;

import me.neyucity.project.neyugame.Neyugame;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public class NeyuCommand implements CommandExecutor {
    private final Neyugame plugin;

    public NeyuCommand(Neyugame plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(s);
            return true;
        }
        String sub = args[0].toLowerCase();

        // Các lệnh không cần là người chơi
        if (sub.equals("baltop")) {
            handleTop(s);
            return true;
        }

        if (sub.equals("reload")) {
            if (s.hasPermission("neyu.admin")) {
                plugin.reloadConfig();
                s.sendMessage(ChatColor.GREEN + "Đã reload cấu hình Neyugame!");
            } else {
                s.sendMessage(ChatColor.RED + "Bạn không có quyền!");
            }
            return true;
        }

        // Các lệnh bắt buộc phải là người chơi
        if (!(s instanceof Player p)) {
            s.sendMessage("Lệnh này chỉ dành cho người chơi.");
            return true;
        }

        switch (sub) {
            case "bal" -> p.sendMessage(ChatColor.GREEN + "Số dư: " + ChatColor.YELLOW + (long) plugin.getMoneyManager().getBalance(p.getUniqueId()) + " xu");
            case "sell" -> handleSell(p);
            case "quydoi" -> handleExchange(p, args);
            default -> sendHelp(s);
        }
        return true;
    }

    private void handleTop(CommandSender s) {
        s.sendMessage(ChatColor.GOLD + "--- [ BẢNG XẾP HẠNG TOP 10 ] ---");
        Map<String, Double> topData = plugin.getMoneyManager().getTop(10);

        if (topData.isEmpty()) {
            s.sendMessage(ChatColor.GRAY + "Chưa có dữ liệu xếp hạng.");
            return;
        }

        int rank = 1;
        for (Map.Entry<String, Double> entry : topData.entrySet()) {
            String uuidStr = entry.getKey();
            double balance = entry.getValue();
            String name = Bukkit.getOfflinePlayer(UUID.fromString(uuidStr)).getName();
            if (name == null) name = "Ẩn danh";

            s.sendMessage(ChatColor.YELLOW + "" + rank + ". " + ChatColor.WHITE + name +
                    ChatColor.GRAY + " - " + ChatColor.AQUA + String.format("%,d", (long) balance) + " xu");
            rank++;
        }
        s.sendMessage(ChatColor.GOLD + "--------------------------------");
    }

    private void handleSell(Player p) {
        // Lấy tên vật phẩm từ config, mặc định là SNOWBALL
        String materialName = plugin.getConfig().getString("sell-item", "SNOWBALL");
        Material sellMaterial = Material.matchMaterial(materialName);

        // Lấy giá mỗi vật phẩm từ config, mặc định là 1.0
        double pricePerItem = plugin.getConfig().getDouble("item-price", 1.0);

        if (sellMaterial == null) {
            p.sendMessage(ChatColor.RED + "Lỗi: Vật phẩm '" + materialName + "' trong config không hợp lệ!");
            return;
        }

        int count = 0;
        for (ItemStack i : p.getInventory().getContents()) {
            if (i != null && i.getType() == sellMaterial) {
                count += i.getAmount();
                i.setAmount(0);
            }
        }

        if (count > 0) {
            double multi = plugin.getConfig().getBoolean("happy-hour-enabled") ? plugin.getConfig().getDouble("happy-hour-multiplier") : 1.0;

            // Công thức: Số lượng * Giá mỗi cái * Hệ số event
            long earned = (long) (count * pricePerItem * multi);

            plugin.getMoneyManager().addBalance(p.getUniqueId(), earned);

            p.sendMessage(ChatColor.GREEN + "Đã bán " + ChatColor.WHITE + count + " " + sellMaterial.name());
            p.sendMessage(ChatColor.GREEN + "Nhận được: " + ChatColor.YELLOW + String.format("%,d", earned) + " xu" + (multi > 1 ? " (X" + multi + ")" : ""));
            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
        } else {
            p.sendMessage(ChatColor.RED + "Bạn không có " + sellMaterial.name() + " để bán!");
        }
    }

    private void handleExchange(Player p, String[] args) {
        if (args.length < 2) {
            p.sendMessage(ChatColor.RED + "Sử dụng: /neyu quydoi <số tiền>");
            return;
        }
        try {
            long amt = Long.parseLong(args[1]);
            if (amt <= 0) {
                p.sendMessage(ChatColor.RED + "Số tiền phải lớn hơn 0!");
                return;
            }
            if (plugin.getMoneyManager().getBalance(p.getUniqueId()) < amt) {
                p.sendMessage(ChatColor.RED + "Không đủ xu!");
                return;
            }

            plugin.getMoneyManager().addBalance(p.getUniqueId(), -amt);
            double vaultAmount = amt * plugin.getConfig().getDouble("conversion-rate");
            Neyugame.getVaultEconomy().depositPlayer(p, vaultAmount);

            p.sendMessage(ChatColor.GREEN + "Đã đổi " + ChatColor.YELLOW + amt + " xu " + ChatColor.GREEN + "sang " + ChatColor.GOLD + String.format("%.2f", vaultAmount) + " tiền Server!");
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.2f);
        } catch (NumberFormatException e) {
            p.sendMessage(ChatColor.RED + "Số tiền không hợp lệ.");
        }
    }

    private void sendHelp(CommandSender s) {
        s.sendMessage(ChatColor.GOLD + "===== [ HỆ THỐNG NEYUGAME ] =====");
        s.sendMessage(ChatColor.AQUA + "/neyu bal" + ChatColor.GRAY + " - Xem số dư xu.");
        s.sendMessage(ChatColor.AQUA + "/neyu sell" + ChatColor.GRAY + " - Bán vật phẩm trong túi.");
        s.sendMessage(ChatColor.AQUA + "/neyu baltop" + ChatColor.GRAY + " - Xem top 10 đại gia.");
        s.sendMessage(ChatColor.AQUA + "/neyu quydoi <số>" + ChatColor.GRAY + " - Đổi xu sang tiền Server.");
        if (s.hasPermission("neyu.admin")) {
            s.sendMessage(ChatColor.RED + "/neyu reload" + ChatColor.GRAY + " - Tải lại config.");
        }
        s.sendMessage(ChatColor.GOLD + "================================");
    }
}