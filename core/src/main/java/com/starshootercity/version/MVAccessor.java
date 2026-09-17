package com.starshootercity.version;

import com.starshootercity.APITarget;
import com.starshootercity.AbstractScheduleManager;
import com.starshootercity.FoliaScheduleManager;
import com.starshootercity.ScheduleManager;

/**
 * Used for the MultiVersionExecutor, allowing for functions whose code differs across versions to be used
 * @see MultiVersionExecutor
 */
@APITarget
public class MVAccessor {

    private static MultiVersionExecutor mve = null;
    private static AbstractScheduleManager sm = null;

    public static void startInitialize(String version) {
        if (mve != null) return;
        mve = switch (version) {
            case "1.21.10", "1.21.11" -> new MVE_V1_21_11();
            default -> getLatestMVE();
        };
        if (isFolia()) sm = new FoliaScheduleManager();
        else sm = new ScheduleManager();
    }

    private static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static MultiVersionExecutor getLatestMVE() {
        return new MVE_V1_21_11();
    }

    public static MultiVersionExecutor get() {
        return mve;
    }

    public static AbstractScheduleManager scheduleManager() {
        return sm;
    }
}
