package com.starshootercity.geysermc;

import org.geysermc.event.subscribe.Subscribe;
import org.geysermc.geyser.api.event.lifecycle.GeyserDefineCustomItemsEvent;
import org.geysermc.geyser.api.event.lifecycle.GeyserDefineResourcePacksEvent;
import org.geysermc.geyser.api.event.lifecycle.GeyserPreInitializeEvent;
import org.geysermc.geyser.api.extension.Extension;
import org.geysermc.geyser.api.item.custom.CustomItemData;
import org.geysermc.geyser.api.item.custom.CustomItemOptions;
import org.geysermc.geyser.api.pack.PackCodec;
import org.geysermc.geyser.api.pack.ResourcePack;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@SuppressWarnings("unused")
public class ORExtension implements Extension {

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

    private static ResourcePack pack;

    @Subscribe
    public void onGeyserDefineResourcePacks(GeyserDefineResourcePacksEvent event) {
        event.register(pack);
    }

    @Subscribe
    public void onGeyserPreInitialize(GeyserPreInitializeEvent event) throws URISyntaxException {
        File file = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation()
                .toURI());
        logger().info("Loading Origins-Reborn extension");

        boolean ignored = dataFolder().toFile().mkdirs();
        try (ZipInputStream inputStream = new ZipInputStream(new FileInputStream(file))) {
            ZipEntry entry = inputStream.getNextEntry();
            while (entry != null) {
                if (entry.getName().equalsIgnoreCase("bedrock.mcpack")) {
                    extractFile(inputStream, dataFolder() + "/bedrock.mcpack");
                }
                entry = inputStream.getNextEntry();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        pack = ResourcePack.create(
                PackCodec.path(Path.of(dataFolder() + "/bedrock.mcpack"))
        );
    }

    private static final int BUFFER_SIZE = 4096;

    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }
}
