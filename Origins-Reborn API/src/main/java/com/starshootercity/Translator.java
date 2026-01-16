package com.starshootercity;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Used to translate text into different languages
 */
public class Translator {

    /**
     * Returns the width of a character
     * @param character The character to get the width of
     * @return The width of the negative space required to move back to before the character
     */
    public static int getWidth(char character) {
        return 0;
    }

    /**
     * Registers a translation in translations.yml
     * @param key The key of the translation
     * @param def The default value of the translation
     * @see Translator#translate(String)
     */
    public static void registerTranslation(String key, String def) {
    }

    /**
     * Retrieves a translation from translations.yml
     * @param key The key of the translation
     * @return The value of the translation
     */
    public static String translate(String key) {
        return null;
    }

    public static void reloadTranslations() {
    }

    public static void initialize(JavaPlugin plugin) {
    }
}
