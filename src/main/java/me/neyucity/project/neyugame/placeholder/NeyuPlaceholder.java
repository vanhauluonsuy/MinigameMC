package me.neyucity.project.neyugame.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.neyucity.project.neyugame.Neyugame;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import java.util.*;

public class NeyuPlaceholder extends PlaceholderExpansion {
    private final Neyugame plugin;
    public NeyuPlaceholder(Neyugame plugin) { this.plugin = plugin; }

    // Đổi từ "neyu" thành "neyugame" để khớp với cấu hình Topper của bạn
    @Override public @NotNull String getIdentifier() { return "neyugame"; }
    @Override public @NotNull String getAuthor() { return "neyucity"; }
    @Override public @NotNull String getVersion() { return "1.0"; }
    @Override public boolean persist() { return true; }

    @Override
    public String onRequest(OfflinePlayer p, @NotNull String params) {
        if (p == null) return "";

        // %neyugame_balance%
        if (params.equalsIgnoreCase("balance")) {
            return String.valueOf((long)plugin.getMoneyManager().getBalance(p.getUniqueId()));
        }

        // Hỗ trợ lấy Top trực tiếp từ PAPI: %neyugame_top_name_1% hoặc %neyugame_top_balance_1%
        if (params.startsWith("top_name_") || params.startsWith("top_balance_")) {
            try {
                String[] split = params.split("_");
                if (split.length < 3) return "";

                int rank = Integer.parseInt(split[2]);
                var top = plugin.getMoneyManager().getTop(rank);
                List<String> keys = new ArrayList<>(top.keySet());

                if (keys.size() < rank) return "---";

                String uuid = keys.get(rank - 1);
                if (params.contains("name")) {
                    String name = Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName();
                    return name != null ? name : "Ẩn danh";
                }

                return String.format("%,d", top.get(uuid).longValue());
            } catch (Exception e) {
                return "Lỗi";
            }
        }
        return null;
    }
}