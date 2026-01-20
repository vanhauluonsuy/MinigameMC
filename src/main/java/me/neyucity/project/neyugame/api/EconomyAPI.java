package me.neyucity.project.neyugame.api;

import me.neyucity.project.neyugame.Neyugame;
import java.util.UUID;

public class EconomyAPI {
    public static double getBalance(UUID uuid) { return Neyugame.getInstance().getMoneyManager().getBalance(uuid); }
    public static void addBalance(UUID uuid, double amt) { Neyugame.getInstance().getMoneyManager().addBalance(uuid, amt); }
}