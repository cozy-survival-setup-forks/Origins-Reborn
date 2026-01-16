package com.starshootercity.abilities.types;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import com.starshootercity.*;
import com.starshootercity.util.AbilityRegister;
import com.starshootercity.util.config.ConfigManager;
import com.starshootercity.util.WorldGuardHook;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.*;
import static com.starshootercity.util.AbilityRegister.abilityMap;
import static com.starshootercity.util.AbilityRegister.invertedAbilityMap;

/**
 * The default ability interface, used for basic abilities
 * <br><br>
 * Must be registered in the main class
 * @see OriginsAddon#getRegisteredAbilities()
 */
public interface Ability {

    /**
     * Used for registering abilities. This should not be used when retrieving an ability's key, use getRegisteredKey()
     * @return The key used to register the ability
     * @see Ability#getRegisteredKey()
     */
    @NotNull
    Key getKey();

    @NotNull
    default Key getRegisteredKey() {
        return null;
    }

    /**
     * Run code if a player has an ability
     * @param entity The entity to check (code will only execute if this is a player)
     * @param runner Function that executes if the player has the ability
     */
    default void runForAbility(Entity entity, @NotNull AbilityRunner runner) {
    }

    /**
     * Run code if a player has an ability or if they do not
     * @param entity The entity to check (code will only execute if this is a player)
     * @param has Function that executes if the player has the ability
     * @param other Function that executes if the player does not have the ability
     */
    default void runForAbility(Entity entity, @Nullable AbilityRunner has, @Nullable AbilityRunner other) {
    }

    /**
     * Run code if a player has an ability or if they do not
     * @param entity The entity to check (code will only execute if this is a player)
     * @param has Function that executes if the player has the ability
     * @param other Function that executes if the player does not have the ability
     */
    default void runForAbility(Entity entity, @Nullable AbilityRunner has, @Nullable Runnable other) {
    }

    /**
     * Checks if a player has the ability
     * @param player The player to check
     * @return Whether the player has the ability
     */
    default boolean hasAbility(Player player) {
        return false;
    }

    /**
     * Checks if a player has the ability
     * @param player The player to check
     * @param checkMultiAbilities Whether to check if the player has a MultiAbility containing this ability
     * @return Whether the player has the ability
     */
    default boolean hasAbility(Player player, boolean checkMultiAbilities) {
        return false;
    }

    interface AbilityRunner {

        void run(Player player);
    }

    /**
     * Used for things like initializing config options or translatable pieces of text
     * @param plugin The plugin used to register the ability
     * @see Ability#registerTranslation(String, String)
     * @see Ability#registerConfigOption(OriginsAddon, String, List, ConfigManager.SettingType, Object)
     */
    default void initialize(JavaPlugin plugin) {
    }

    /**
     * Registers a translation in the translations.yml file associated with this ability
     * @param key Key of the translation
     * @param def Default value for the translatable ability
     * @see Ability#translate(String)
     */
    default void registerTranslation(String key, String def) {
    }

    /**
     * Retrieves a translation in the translations.yml file associated with this ability
     * @param key Key of the translation
     * @see Ability#registerTranslation(String, String)
     */
    default String translate(String key) {
        return null;
    }

    /**
     * Registers a config option with this ability under a certain Origins-Reborn addon
     * @param addon The addon to register the config option under
     * @param path Path to the config option in ability-config.yml
     * @param comments A description for the config option
     * @param settingType Object type for the setting
     * @param defaultValue Default value for the setting
     * @see com.starshootercity.util.config.ConfigManager.SettingType
     * @see Ability#getConfigOption(OriginsAddon, String, ConfigManager.SettingType)
     */
    default <T> void registerConfigOption(OriginsAddon addon, String path, List<String> comments, ConfigManager.SettingType<T> settingType, T defaultValue) {
    }

    /**
     * Registers a config option under the addon this ability was initialized by,
     * this should only be used with absolutely necessary as it is slower than specifying an addon
     * @param path Path to the config option in ability-config.yml
     * @param comments A description for the config option
     * @param settingType Object type for the setting
     * @param defaultValue Default value for the setting
     * @see com.starshootercity.util.config.ConfigManager.SettingType
     * @see Ability#getConfigOption(OriginsAddon, String, ConfigManager.SettingType)
     */
    default <T> void registerConfigOption(String path, List<String> comments, ConfigManager.SettingType<T> settingType, T defaultValue) {
    }

    /**
     * Gets a config value under a specified addon
     * @param addon The addon this config option is registered under
     * @param path Path to the config option in ability-config.yml
     * @param settingType Object type for the setting
     * @return The value of the config option
     * @see com.starshootercity.util.config.ConfigManager.SettingType
     * @see Ability#registerConfigOption(OriginsAddon, String, List, ConfigManager.SettingType, Object)
     */
    default <T> T getConfigOption(OriginsAddon addon, String path, ConfigManager.SettingType<T> settingType) {
        return null;
    }

    /**
     * Gets a config value under the addon this ability was initialized by,
     * this should only be used with absolutely necessary as it is slower than specifying an addon
     * @param path Path to the config option in ability-config.yml
     * @param settingType Object type for the setting
     * @return The value of the config option
     * @see com.starshootercity.util.config.ConfigManager.SettingType
     * @see Ability#registerConfigOption(OriginsAddon, String, List, ConfigManager.SettingType, Object)
     */
    default <T> T getConfigOption(String path, ConfigManager.SettingType<T> settingType) {
        return null;
    }
}
