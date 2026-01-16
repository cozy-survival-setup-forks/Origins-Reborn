package com.starshootercity.abilities;

import com.starshootercity.abilities.types.ParticleAbility;
import com.starshootercity.util.config.ConfigManager;
import net.kyori.adventure.key.Key;
import org.bukkit.Particle;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class FlameParticles implements ParticleAbility {
    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:flame_particles");
    }

    @Override
    public Particle getParticle() {
        return Particle.FLAME;
    }

    @Override
    public int getFrequency() {
        return frequency;
    }

    private int frequency;

    @Override
    public void initialize(JavaPlugin plugin) {
        frequency = registerConfigOption(plugin, "frequency", Collections.singletonList("How often (in ticks) the particles should appear"), ConfigManager.SettingType.INTEGER, 4);
    }
}
