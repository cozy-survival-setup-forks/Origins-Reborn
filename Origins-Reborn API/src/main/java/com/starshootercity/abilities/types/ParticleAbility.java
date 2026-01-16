package com.starshootercity.abilities.types;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.starshootercity.Origin;
import com.starshootercity.OriginSwapper;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import java.util.ArrayList;
import java.util.List;

/**
 * Ability that creates particle effects around a player
 */
public interface ParticleAbility extends Ability {

    /**
     * @return The particle type that should be displayed
     */
    Particle getParticle();

    /**
     * @return The frequency, in ticks, that the particle should be applied
     */
    default int getFrequency() {
        return 0;
    }

    /**
     * @return The extra data for this particle, depends on the particle used (normally speed)
     */
    default int getExtra() {
        return 0;
    }

    /**
     * @return The data to use for the particle or null, the type of this depends on the particle
     */
    default Object getData() {
        return null;
    }

    class ParticleAbilityListener implements Listener {

        @EventHandler
        public void onServerTickEnd(ServerTickEndEvent event) {
        }
    }
}
