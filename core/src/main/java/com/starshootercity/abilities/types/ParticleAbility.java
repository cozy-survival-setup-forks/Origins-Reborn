package com.starshootercity.abilities.types;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.starshootercity.APITarget;
import com.starshootercity.Origin;
import com.starshootercity.OriginSwapper;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

/**
 * Ability that creates particle effects around a player
 */
@APITarget
public interface ParticleAbility extends Ability {

    /**
     * @return The particle type that should be displayed
     */
    Particle getParticle();

    /**
     * @return The frequency, in ticks, that the particle should be applied
     */
    default int getFrequency() {
        return 4;
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
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getGameMode().equals(GameMode.SPECTATOR)) continue;
                List<Origin> origins = OriginSwapper.getOrigins(player);
                List<Ability> abilities = new ArrayList<>();
                for (Origin origin : origins) abilities.addAll(origin.getAbilities());
                for (Ability ability : abilities) {
                    if (ability instanceof ParticleAbility particleAbility) {
                        if (event.getTickNumber() % particleAbility.getFrequency() == 0) {
                            player.getWorld().spawnParticle(particleAbility.getParticle(), player.getLocation(), 1, 0.5, 1, 0.5, particleAbility.getExtra(), particleAbility.getData());
                        }
                    }
                }
            }
        }
    }
}
