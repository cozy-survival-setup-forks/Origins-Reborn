package com.starshootercity.util.hooks;

import dev.geco.gsit.api.event.PlayerPoseEvent;
import dev.geco.gsit.api.event.PlayerStopPoseEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GSH implements Listener {

    @EventHandler
    public void onPlayerPose(PlayerPoseEvent event) {
        GSitHook.sitting.add(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerStopPose(PlayerStopPoseEvent event) {
        GSitHook.sitting.remove(event.getPlayer().getUniqueId());
    }
}
