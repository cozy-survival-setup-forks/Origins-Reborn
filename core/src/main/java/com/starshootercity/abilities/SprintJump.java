package com.starshootercity.abilities;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.starshootercity.abilities.types.VisibleAbility;
import com.starshootercity.util.config.ConfigManager;
import com.starshootercity.version.MVPotionEffectType;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class SprintJump implements Listener, VisibleAbility {
    @EventHandler
    public void onServerTickEnd(ServerTickEndEvent event) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            runForAbility(p,
                    player -> {
                        if (player.isSprinting()) {
                            player.addPotionEffect(new PotionEffect(MVPotionEffectType.JUMP_BOOST.get(), 5, jumpStrength, false, false));
                        }
                    });
        }
    }
    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:sprint_jump");
    }

    @Override
    public String description() {
        return "You are able to jump higher by jumping while sprinting.";
    }

    @Override
    public String title() {
        return "Strong Ankles";
    }

    private int jumpStrength;

    @Override
    public void initialize(JavaPlugin plugin) {
        jumpStrength = registerConfigOption(plugin, "jump_strength", Collections.singletonList("Strength of the Jump Boost effect to give"), ConfigManager.SettingType.INTEGER, 1);
    }
}
