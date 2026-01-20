package me.neyucity.project.neyugame.managers;

import me.neyucity.project.neyugame.Neyugame;
import org.bukkit.Bukkit;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MoneyManager {
    private final Neyugame plugin;
    private Connection conn;
    private final Map<UUID, Double> cache = new ConcurrentHashMap<>();
    private final String table;

    public MoneyManager(Neyugame plugin) {
        this.plugin = plugin;
        this.table = plugin.getConfig().getString("mysql.table", "neyugame_balances");
    }

    public void init() {
        try {
            if (plugin.getConfig().getBoolean("use-mysql")) {
                String host = plugin.getConfig().getString("mysql.host");
                String db = plugin.getConfig().getString("mysql.database");
                String user = plugin.getConfig().getString("mysql.username");
                String pass = plugin.getConfig().getString("mysql.password");
                conn = DriverManager.getConnection("jdbc:mysql://" + host + "/" + db, user, pass);
            } else {
                String fileName = plugin.getConfig().getString("sqlite.file", "neyudatabase.db");
                conn = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder() + "/" + fileName);
            }
            try (Statement s = conn.createStatement()) {
                s.executeUpdate("CREATE TABLE IF NOT EXISTS " + table + " (uuid VARCHAR(36) PRIMARY KEY, balance DOUBLE)");
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public double getBalance(UUID uuid) {
        if (cache.containsKey(uuid)) return cache.get(uuid);
        try (PreparedStatement ps = conn.prepareStatement("SELECT balance FROM " + table + " WHERE uuid = ?")) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            double bal = rs.next() ? rs.getDouble("balance") : 0.0;
            cache.put(uuid, bal);
            return bal;
        } catch (SQLException e) { return 0.0; }
    }

    public void addBalance(UUID uuid, double amount) {
        double newBal = getBalance(uuid) + amount;
        cache.put(uuid, newBal);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (PreparedStatement ps = conn.prepareStatement("REPLACE INTO " + table + " (uuid, balance) VALUES (?, ?)")) {
                ps.setString(1, uuid.toString());
                ps.setDouble(2, newBal);
                ps.executeUpdate();
            } catch (SQLException e) { e.printStackTrace(); }
        });
    }

    public Map<String, Double> getTop(int limit) {
        Map<String, Double> top = new LinkedHashMap<>();
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + table + " ORDER BY balance DESC LIMIT ?")) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) top.put(rs.getString("uuid"), rs.getDouble("balance"));
        } catch (SQLException e) { e.printStackTrace(); }
        return top;
    }

    public void shutdown() {
        try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
}