package com.starshootercity.util;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {

    public static boolean setupEconomy(Server server) {
        try {
            RegisteredServiceProvider<Economy> economyProvider = server.getServicesManager().getRegistration(Economy.class);
            if (economyProvider != null) {
                economy = economyProvider.getProvider();
            }
            return (economy != null);
        } catch (NoClassDefFoundError e) {
            return false;
        }
    }

    private static Economy economy;

    public static boolean has(Player player, int amount) {

        if (economy == null) return true;
        return economy.has(player, amount);
    }

    public static void withdraw(Player player, int amount) {
        economy.withdrawPlayer(player, amount);
    }
}
