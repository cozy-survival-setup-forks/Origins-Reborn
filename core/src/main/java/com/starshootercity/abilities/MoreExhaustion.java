package com.starshootercity.abilities;

import com.starshootercity.abilities.types.VisibleAbility;
import com.starshootercity.util.config.ConfigManager;
import net.kyori.adventure.key.Key;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExhaustionEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class MoreExhaustion implements Listener, VisibleAbility {
    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:more_exhaustion");
    }

    @Override
    public String description() {
        return "You exhaust much quicker than others, thus requiring you to eat more.";
    }

    @Override
    public String title() {
        return "Large Appetite";
    }

    @EventHandler
    public void onEntityExhaustion(EntityExhaustionEvent event) {
        runForAbility(event.getEntity(), player -> event.setExhaustion(event.getExhaustion() * exhaustionMultiplier));
    }

    private float exhaustionMultiplier;

    @Override
    public void initialize(JavaPlugin plugin) {
        exhaustionMultiplier = registerConfigOption(plugin, "exhaustion_multiplier", Collections.singletonList("Amount to multiply exhaustion by"), ConfigManager.SettingType.FLOAT, 1.6f);
    }
}
