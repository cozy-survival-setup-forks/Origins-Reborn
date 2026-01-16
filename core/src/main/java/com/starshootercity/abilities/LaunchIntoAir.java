package com.starshootercity.abilities;

import com.starshootercity.abilities.types.CooldownAbility;
import com.starshootercity.abilities.types.TriggerableAbility;
import com.starshootercity.abilities.types.VisibleAbility;
import com.starshootercity.cooldowns.Cooldowns;
import com.starshootercity.util.config.ConfigManager;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class LaunchIntoAir implements Listener, CooldownAbility, VisibleAbility, TriggerableAbility {
    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:launch_into_air");
    }

    @Override
    public String description() {
        return "Every 30 seconds, you are able to launch about 20 blocks up into the air.";
    }

    @Override
    public String title() {
        return "Gift of the Winds";
    }

    @Override
    public Cooldowns.CooldownInfo getCooldownInfo() {
        return new Cooldowns.CooldownInfo(600, "launch");
    }

    private float launchStrength;

    @Override
    public void initialize(JavaPlugin plugin) {
        launchStrength = registerConfigOption(plugin, "launch_strength", Collections.singletonList("How strong the launch effect should be"), ConfigManager.SettingType.FLOAT, 2f);
    }

    @Override
    public @NotNull Trigger getTrigger() {
        return Trigger.builder(
                TriggerType.SNEAK_ON,
                this
        ).addConditions(Condition.GLIDING)
                .build(event -> {
                    Player player = event.player();
                    if (hasCooldown(player)) return;
                    setCooldown(player);
                    player.setVelocity(player.getVelocity().add(new Vector(0, launchStrength, 0)));
                });
    }
}
