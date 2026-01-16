package com.starshootercity.version;

import com.starshootercity.APITarget;

/**
 * Resource pack info so Origins-Reborn addons can add their own resource packs
 * <br><br>
 * Will be ignored on Minecraft versions where only one server resource pack is supported
 */
@APITarget
public class ResourcePackInfo {

    private final Object packInfo;

    public ResourcePackInfo(Object packInfo) {
        this.packInfo = packInfo;
    }

    public Object packInfo() {
        return packInfo;
    }
}
