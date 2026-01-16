package com.starshootercity.geysermc;

import org.bukkit.plugin.java.JavaPlugin;
import org.geysermc.event.subscribe.Subscribe;
import org.geysermc.geyser.api.GeyserApi;
import org.geysermc.geyser.api.event.EventRegistrar;
import org.geysermc.geyser.api.event.lifecycle.GeyserDefineCustomItemsEvent;
import org.geysermc.geyser.api.event.lifecycle.GeyserDefineResourcePacksEvent;
import org.geysermc.geyser.api.item.custom.CustomItemData;
import org.geysermc.geyser.api.item.custom.CustomItemOptions;
import org.geysermc.geyser.api.pack.PackCodec;
import org.geysermc.geyser.api.pack.ResourcePack;

import java.io.File;
import java.nio.file.Path;

@SuppressWarnings("unused")
public class OREventRegistrar implements EventRegistrar {
    @Subscribe
    public void onGeyserDefineCustomItems(GeyserDefineCustomItemsEvent event) {
        CustomItemOptions itemOptions = CustomItemOptions.builder()
                .customModelData(1)
                .build();
        CustomItemData data = CustomItemData.builder()
                .name("orb_of_origin")
                .customItemOptions(itemOptions)
                .build();

        event.register("minecraft:nautilus_shell", data);
    }

    private static ResourcePack resourcePack;

    @Subscribe
    public void onGeyserLoadResourcePacks(GeyserDefineResourcePacksEvent event) {
        event.register(resourcePack);
    }

    public static void initialize(JavaPlugin plugin) {
        OREventRegistrar orEventRegistrar = new OREventRegistrar();
        GeyserApi.api().eventBus().subscribe(orEventRegistrar, GeyserDefineCustomItemsEvent.class, orEventRegistrar::onGeyserDefineCustomItems);
        GeyserApi.api().eventBus().subscribe(orEventRegistrar, GeyserDefineResourcePacksEvent.class, orEventRegistrar::onGeyserLoadResourcePacks);

        plugin.saveResource("bedrock.mcpack", true);
        Path path = new File(plugin.getDataFolder(), "bedrock.mcpack").toPath();

        resourcePack = ResourcePack.create(PackCodec.path(path));
    }
}
