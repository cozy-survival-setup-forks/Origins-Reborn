package com.starshootercity.util;

import com.starshootercity.OriginsAddon;
import com.starshootercity.abilities.types.*;
import com.starshootercity.commands.FlightToggleCommand;
import com.starshootercity.abilities.types.CooldownAbility;
import com.starshootercity.util.config.ConfigManager;
import com.starshootercity.version.MVAccessor;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.util.TriState;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class AbilityRegister {

    public static Map<Key, Ability> abilityMap = null;

    public static Map<Ability, Key> invertedAbilityMap = null;

    public static Map<Key, OriginsAddon> pluginMap = null;

    public static Map<Key, DependencyAbility> dependencyAbilityMap = null;

    public static Map<Key, List<MultiAbility>> multiAbilityMap = null;

    public static List<BreakSpeedModifierAbility> breakSpeedModifierAbilities = null;

    public static List<AttributeModifierAbility> attributeModifierAbilities = null;

    public static List<SkinChangingAbility> skinChangingAbilities = null;

    public static List<AsyncRepeatingAbility> asyncRepeatingAbilities = null;

    public static List<FlightAllowingAbility> flightAllowingAbilities = null;

    public static Map<Key, List<AbilityRunnable>> runOnRegisters = null;

    public static <T> void registerConfigOption(OriginsAddon addon, Ability ability, ConfigManager.SettingType<T> settingType, String path, List<String> comments, T defaultValue) {
    }

    @SuppressWarnings("unchecked")
    public static <T> T getConfigOption(OriginsAddon addon, Ability ability, ConfigManager.SettingType<T> settingType, String path) {
        return null;
    }

    public static void reloadAbilityConfig() {
    }

    public static void setupAbilityConfig(JavaPlugin plugin) {
    }

    public static void initializeRepeatingTasks(JavaPlugin plugin) {
    }

    public static void setSkinManagerEnabled() {
    }

    public static boolean isSkinManagerEnabled() {
        return false;
    }

    public static void registerAbility(Ability ability, Key key, JavaPlugin instance) {
    }

    /**
     * @deprecated Testing abilities is now contained in the Ability interface
     */
    @Deprecated(forRemoval = true)
    public static void runForAbility(Entity entity, Key key, Runnable runnable) {
    }

    /**
     * @deprecated Testing abilities is now contained in the Ability interface
     */
    @Deprecated(forRemoval = true)
    public static boolean hasAbility(Player player, Key key) {
        return false;
    }

    /**
     * @deprecated Testing abilities is now contained in the Ability interface
     */
    @Deprecated(forRemoval = true)
    public static boolean hasAbility(Player player, Key key, boolean ignoreOverrides) {
        return false;
    }

    /**
     * @deprecated Testing abilities is now contained in the Ability interface
     */
    @Deprecated(forRemoval = true)
    public static void runForAbility(Entity entity, Key key, Runnable runnable, Runnable other) {
    }

    /**
     * @deprecated Testing abilities is now contained in the Ability interface
     */
    @Deprecated(forRemoval = true)
    public static void runWithoutAbility(Entity entity, Key key, Runnable runnable) {
    }

    public static FlyData flyData(Player player, boolean disabledWorld) {
        return null;
    }

    public record FlyData(boolean canFly, boolean forceFly) {
    }

    public static boolean isInvisible(Player player) {
        return false;
    }

    public static void updateFlight(Player player, boolean inDisabledWorld) {
    }

    public static void updateEntity(Player player, Entity target) {
    }

    public interface AbilityRunnable {

        void run(Ability ability);
    }
}
