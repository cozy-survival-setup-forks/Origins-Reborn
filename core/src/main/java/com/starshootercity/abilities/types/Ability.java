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
import com.starshootercity.util.WorldGuardHook;
import com.starshootercity.util.config.ConfigManager;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.starshootercity.util.AbilityRegister.abilityMap;
import static com.starshootercity.util.AbilityRegister.invertedAbilityMap;

/**
 * The default ability interface, used for basic abilities
 * <br><br>
 * Must be registered in the main class
 * @see OriginsAddon#getRegisteredAbilities()
 */
@APITarget
public interface Ability {

    /**
     * Used for registering abilities. This should not be used when retrieving an ability's key, use getRegisteredKey()
     * @return The key used to register the ability
     * @see Ability#getRegisteredKey()
     */
    @NotNull
    Key getKey();

    @NotNull default Key getRegisteredKey() {
        return invertedAbilityMap.get(this);
    }

    /**
     * Run code if a player has an ability
     * @param entity The entity to check (code will only execute if this is a player)
     * @param runner Function that executes if the player has the ability
     */
    default void runForAbility(Entity entity, @NotNull AbilityRunner runner) {
        runForAbility(entity, runner, (AbilityRunner) null);
    }

    /**
     * Run code if a player has an ability or if they do not
     * @param entity The entity to check (code will only execute if this is a player)
     * @param has Function that executes if the player has the ability
     * @param other Function that executes if the player does not have the ability
     */
    default void runForAbility(Entity entity, @Nullable AbilityRunner has, @Nullable AbilityRunner other) {
        if (entity instanceof Player player) {
            if (hasAbility(player)) {
                if (has != null) has.run(player);
            } else if (other != null) other.run(player);
        }
    }

    /**
     * Run code if a player has an ability or if they do not
     * @param entity The entity to check (code will only execute if this is a player)
     * @param has Function that executes if the player has the ability
     * @param other Function that executes if the player does not have the ability
     */
    default void runForAbility(Entity entity, @Nullable AbilityRunner has, @Nullable Runnable other) {
        if (entity instanceof Player player) {
            if (hasAbility(player)) {
                if (has != null) has.run(player);
            } else if (other != null) other.run();
        } else if (other != null) other.run();
    }

    /**
     * Checks if a player has the ability
     * @param player The player to check
     * @return Whether the player has the ability
     */
    default boolean hasAbility(Player player) {
        if (AbilityRegister.disabledWorlds.contains(player.getWorld().getName())) return false;

        for (OriginsAddon.KeyStateGetter keyStateGetter : AddonLoader.abilityOverrideChecks) {
            OriginsAddon.State state = keyStateGetter.get(player, getRegisteredKey());
            if (state == OriginsAddon.State.DENY) return false;
            else if (state == OriginsAddon.State.ALLOW) return true;
        }

        if (OriginsReborn.isWorldGuardHookInitialized()) {
            if (WorldGuardHook.isAbilityDisabled(player.getLocation(), this)) return false;

            ConfigurationSection section = OriginsReborn.getInstance().getConfig().getConfigurationSection("prevent-abilities-in");
            Location loc = BukkitAdapter.adapt(player.getLocation());
            if (section != null) {
                RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                RegionQuery query = container.createQuery();
                ApplicableRegionSet set = query.getApplicableRegions(loc);
                for (ProtectedRegion region : set) {
                    for (String sectionKey : section.getKeys(false)) {
                        if (!section.getStringList(sectionKey).contains(getRegisteredKey().toString()) && !section.getStringList(sectionKey).contains("all"))
                            continue;
                        if (region.getId().equalsIgnoreCase(sectionKey)) {
                            return false;
                        }
                    }
                }
            }
        }

        List<Origin> origins = OriginSwapper.getOrigins(player);

        boolean hasAbility = false;
        for (Origin origin : origins) {
            if (origin.hasAbility(this)) {
                hasAbility = true;
                break;
            }
        }

        if (abilityMap.get(getRegisteredKey()) instanceof DependantAbility dependantAbility) {
            return hasAbility && ((dependantAbility.getDependencyType() == DependantAbility.DependencyType.REGULAR) == dependantAbility.getDependency().isEnabled(player));
        }

        return hasAbility;
    }

    interface AbilityRunner {
        void run(Player player);
    }

    /**
     * Used for things like initializing config options or translatable pieces of text
     * @param plugin The plugin used to register the ability
     * @see Ability#registerTranslation(String, String)
     * @see Ability#registerConfigOption(JavaPlugin, String, List, ConfigManager.SettingType, Object)
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
        Translator.registerTranslation("abilities." + getRegisteredKey() + "." + key, def);
    }


    /**
     * Retrieves a translation in the translations.yml file associated with this ability
     * @param key Key of the translation
     * @see Ability#registerTranslation(String, String)
     */
    default String translate(String key) {
        return Translator.translate("abilities." + getRegisteredKey() + "." + key);
    }

    /**
     * Registers a config option with this ability under a certain Origins-Reborn addon
     * @param plugin The plugin to register the config option under
     * @param path Path to the config option in ability-config.yml
     * @param comments A description for the config option
     * @param settingType Object type for the setting
     * @param defaultValue Default value for the setting
     * @see com.starshootercity.util.config.ConfigManager.SettingType
     * @see Ability#getConfigOption(JavaPlugin, String, ConfigManager.SettingType)
     */
    default <T> T registerConfigOption(JavaPlugin plugin, String path, List<String> comments, ConfigManager.SettingType<T> settingType, T defaultValue) {
        AbilityRegister.registerConfigOption(plugin, this, settingType, path, comments, defaultValue);
        return getConfigOption(plugin, path, settingType);
    }

    /**
     * Registers a config option under the addon this ability was initialized by,
     * this should only be used with absolutely necessary as it is slower than specifying an addon
     * @param path Path to the config option in ability-config.yml
     * @param comments A description for the config option
     * @param settingType Object type for the setting
     * @param defaultValue Default value for the setting
     * @see com.starshootercity.util.config.ConfigManager.SettingType
     */
    default <T> void registerConfigOption(String path, List<String> comments, ConfigManager.SettingType<T> settingType, T defaultValue) {
        registerConfigOption(AbilityRegister.pluginMap.get(getRegisteredKey()), path, comments, settingType, defaultValue);
    }

    /**
     * Gets a config value under a specified addon
     * @param plugin The plugin this config option is registered under
     * @param path Path to the config option in ability-config.yml
     * @param settingType Object type for the setting
     * @return The value of the config option
     * @see com.starshootercity.util.config.ConfigManager.SettingType
     * @see Ability#registerConfigOption(JavaPlugin, String, List, ConfigManager.SettingType, Object)
     */
    default <T> T getConfigOption(JavaPlugin plugin, String path, ConfigManager.SettingType<T> settingType) {
        return AbilityRegister.getConfigOption(plugin, this, settingType, path);
    }

    /**
     * @deprecated getConfigOption can now take the JavaPlugin given in initialize
     * @see Ability#initialize(JavaPlugin)
     * @see Ability#registerConfigOption(JavaPlugin, String, List, ConfigManager.SettingType, Object)
     */
    @Deprecated(forRemoval = true)
    default <T> T getConfigOption(OriginsAddon addon, String path, ConfigManager.SettingType<T> settingType) {
        return AbilityRegister.getConfigOption(addon, this, settingType, path);
    }

    /**
     * @deprecated registerConfigOption can now take the JavaPlugin given in initialize
     * @see Ability#initialize(JavaPlugin) 
     * @see Ability#registerConfigOption(JavaPlugin, String, List, ConfigManager.SettingType, Object)
     */
    @Deprecated(forRemoval = true)
    default <T> void registerConfigOption(OriginsAddon addon, String path, List<String> comments, ConfigManager.SettingType<T> settingType, T defaultValue) {
        AbilityRegister.registerConfigOption(addon, this, settingType, path, comments, defaultValue);
    }

    /**
     * Gets a config value under the addon this ability was initialized by,
     * this should only be used with absolutely necessary as it is slower than specifying an addon
     * @param path Path to the config option in ability-config.yml
     * @param settingType Object type for the setting
     * @return The value of the config option
     * @see com.starshootercity.util.config.ConfigManager.SettingType
     * @see Ability#registerConfigOption(JavaPlugin, String, List, ConfigManager.SettingType, Object)
     */
    default <T> T getConfigOption(String path, ConfigManager.SettingType<T> settingType) {
        return getConfigOption(AbilityRegister.pluginMap.get(getRegisteredKey()), path, settingType);
    }
}
