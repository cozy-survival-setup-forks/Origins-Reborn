package com.starshootercity.abilities;

import com.starshootercity.OriginsReborn;
import com.starshootercity.abilities.types.Ability;
import com.starshootercity.util.config.ConfigManager;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class DamageFromSnowballs implements Ability, Listener {
    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:damage_from_snowballs");
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity().getType() != EntityType.SNOWBALL) return;
        runForAbility(event.getHitEntity(), player -> {
            OriginsReborn.getMVE().dealFreezeDamage(player, damageAmount);
            Vector vector = event.getEntity().getLocation().getDirection();

            if (knockback) {
                OriginsReborn.getMVE().knockback(player, 0.5, -vector.getX(), -vector.getZ());
            }
        });
    }

    private int damageAmount;
    private boolean knockback;

    @Override
    public void initialize(JavaPlugin plugin) {
        damageAmount = registerConfigOption(plugin, "damage_amount", Collections.singletonList("Amount of damage the player should take when hit with a snowball"), ConfigManager.SettingType.INTEGER, 3);
        knockback = registerConfigOption(plugin, "knockback", Collections.singletonList("Whether snowballs should deal knockback too"), ConfigManager.SettingType.BOOLEAN, true);
    }
}
