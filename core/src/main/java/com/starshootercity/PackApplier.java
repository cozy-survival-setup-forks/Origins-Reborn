package com.starshootercity;

import com.starshootercity.version.ResourcePackInfo;
import com.starshootercity.util.ShortcutUtils;
import com.starshootercity.util.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class PackApplier implements Listener {
    private static final Map<Class<? extends OriginsAddon>, ResourcePackInfo> addonPacks = new HashMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (ConfigManager.getConfigValue(ConfigManager.Option.RESOURCE_PACK_ENABLED)) {
            if (ShortcutUtils.isBedrockPlayer(event.getPlayer().getUniqueId())) return;
            Bukkit.getScheduler().runTaskLaterAsynchronously(OriginsReborn.getInstance(), () -> sendPacks(event.getPlayer()), 120);
        }
    }

    public static void sendPacks(Player player) {
        OriginsReborn.getMVE().sendResourcePacks(player, getPackURL(), addonPacks);
    }

    public static String getPackURL() {
        return "https://github.com/cometcake575/Origins-Reborn/raw/refs/heads/main/OriginsPack.zip";
    }

    /*
    public static String getPackURL(Player player) {
        String[] ver = getVersion(player).split("-");
        return switch (ver[ver.length-1]) {
            case "1.19.1", "1.19.2" -> "https://github.com/cometcake575/Origins-Reborn/raw/main/packs/1.19.1-1.19.2.zip";
            case "1.19.3" -> "https://github.com/cometcake575/Origins-Reborn/raw/main/packs/1.19.3.zip";
            case "1.19.4" -> "https://github.com/cometcake575/Origins-Reborn/raw/main/packs/1.19.4.zip";
            case "1.20", "1.20.1" -> "https://github.com/cometcake575/Origins-Reborn/raw/main/packs/1.20-1.20.1.zip";
            case "1.20.2" -> "https://github.com/cometcake575/Origins-Reborn/raw/main/packs/1.20.2.zip";
            case "1.20.3", "1.20.4" -> "https://github.com/cometcake575/Origins-Reborn/raw/main/packs/1.20.3-1.20.4.zip";
            case "1.20.5", "1.20.6" -> "https://github.com/cometcake575/Origins-Reborn/raw/main/packs/1.20.5-1.20.6.zip";
            case "1.21", "1.21.1" -> "https://github.com/cometcake575/Origins-Reborn/raw/main/packs/1.21.zip";
            case "1.21.2", "1.21.3" -> "https://github.com/cometcake575/Origins-Reborn/raw/main/packs/1.21.3.zip";
            case "1.21.4" -> "https://github.com/cometcake575/Origins-Reborn/raw/main/packs/1.21.4.zip";
            default -> "https://github.com/cometcake575/Origins-Reborn/raw/main/src/main/Origins%20Pack.zip";
        };
    }
    public static String getVersion(Player player) {
        try {
            return Via.getAPI().getPlayerProtocolVersion(player.getUniqueId()).getName();
        } catch (NoClassDefFoundError e) {
            return Bukkit.getBukkitVersion().split("-")[0];
        }
    }

    @Subscribe
    public void onGeyserLoadResourcePacks(GeyserLoadResourcePacksEvent event) {
        event.resourcePacks().add(new File(OriginsReborn.getInstance().getDataFolder(), "bedrock-packs/bedrock.mcpack").toPath());
    }

    public PackApplier() {
        OriginsReborn.getInstance().saveResource("bedrock.mcpack", false);
    }

 */

    public static void addResourcePack(OriginsAddon addon, @NotNull ResourcePackInfo info) {
        addonPacks.put(addon.getClass(), info);
    }
}
