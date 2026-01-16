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
            case "1.18.1" -> new MVE_V1_18_1();
            case "1.18.2" -> new MVE_V1_18_2();
            case "1.19" -> new MVE_V1_19();
            case "1.19.1" -> new MVE_V1_19_1();
            case "1.19.2" -> new MVE_V1_19_2();
            case "1.19.3" -> new MVE_V1_19_3();
            case "1.19.4" -> new MVE_V1_19_4();
            case "1.20" -> new MVE_V1_20();
            case "1.20.1" -> new MVE_V1_20_1();
            case "1.20.2" -> new MVE_V1_20_2();
            case "1.20.3" -> new MVE_V1_20_3();
            case "1.20.4" -> new MVE_V1_20_4();
            case "1.20.5", "1.20.6" -> new MVE_V1_20_6();
            case "1.21" -> new MVE_V1_21();
            case "1.21.1" -> new MVE_V1_21_1();
            case "1.21.2", "1.21.3" -> new MVE_V1_21_3();
            case "1.21.4" -> new MVE_V1_21_4();
            case "1.21.5" -> new MVE_V1_21_5();
            case "1.21.6" -> new MVE_V1_21_6();
            case "1.21.7" -> new MVE_V1_21_7();
            case "1.21.8" -> new MVE_V1_21_8();
            case "1.21.9" -> new MVE_V1_21_9();
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
        return new MVE_V1_21_9();
    }

    public static MultiVersionExecutor get() {
        return mve;
    }

    public static AbstractScheduleManager scheduleManager() {
        return sm;
    }
}
