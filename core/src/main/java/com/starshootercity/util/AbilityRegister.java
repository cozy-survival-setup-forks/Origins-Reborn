package com.starshootercity.util;

import com.starshootercity.APITarget;
import com.starshootercity.OriginsAddon;
import com.starshootercity.OriginsReborn;
import com.starshootercity.abilities.types.*;
import com.starshootercity.commands.FlightToggleCommand;
import com.starshootercity.abilities.types.CooldownAbility;
import com.starshootercity.util.config.ConfigManager;
import com.starshootercity.util.hooks.GSitHook;
import com.starshootercity.version.MVAccessor;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.util.TriState;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;

@APITarget
public class AbilityRegister {
    public static Map<Key, Ability> abilityMap = new HashMap<>();
    public static Map<Ability, Key> invertedAbilityMap = new HashMap<>();

    public static Map<Key, OriginsAddon> pluginMap = new HashMap<>();
    public static Map<Key, DependencyAbility> dependencyAbilityMap = new HashMap<>();
    public static Map<Key, List<MultiAbility>> multiAbilityMap = new HashMap<>();
    public static List<BreakSpeedModifierAbility> breakSpeedModifierAbilities = new ArrayList<>();

    public static List<Pair<AttributeModifierAbility, NamespacedKey>> attributeModifierAbilities = new ArrayList<>();

    public static List<SkinChangingAbility> skinChangingAbilities = new ArrayList<>();
    public static List<AsyncRepeatingAbility> asyncRepeatingAbilities = new ArrayList<>();
    public static List<FlightAllowingAbility> flightAllowingAbilities = new ArrayList<>();

    public static Map<Key, List<AbilityRunnable>> runOnRegisters = new HashMap<>();

    private static File abilityFile;
    private static FileConfiguration abilityFileConfig;

    public static <T> void registerConfigOption(JavaPlugin plugin, Ability ability, ConfigManager.SettingType<T> settingType, String path, List<String> comments, T defaultValue) {
        String namespace = getNamespace(plugin);
        String pathToUse = namespace + "." + ability.getRegisteredKey().value() + "." + path;
        if (abilityFileConfig.contains(pathToUse)) return;

        settingType.set(abilityFileConfig, pathToUse, defaultValue);

        MVAccessor.get().setComments(abilityFileConfig, pathToUse, comments);

        try {
            abilityFileConfig.save(abilityFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getNamespace(JavaPlugin plugin) {
        if (plugin instanceof OriginsAddon addon) {
            return addon.getNamespace();
        } else return plugin.getName().toLowerCase();
    }

    private static final Map<String, Object> cache = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> T getConfigOption(JavaPlugin plugin, Ability ability, ConfigManager.SettingType<T> settingType, String path) {
        return (T) cache.computeIfAbsent(
                getNamespace(plugin) + "." + ability.getRegisteredKey().value() + "." + path,
                s -> settingType.get(abilityFileConfig, s));
    }

    public static void reloadAbilityConfig() {
        cache.clear();
        try {
            abilityFileConfig.load(abilityFile);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setupAbilityConfig(JavaPlugin plugin) {
        abilityFile = new File(plugin.getDataFolder(), "ability-config.yml");

        if (!abilityFile.exists()) {
            boolean ignored = abilityFile.getParentFile().mkdirs();
            plugin.saveResource("ability-config.yml", false);
        }

        abilityFileConfig = new YamlConfiguration();

        reloadAbilityConfig();
    }

    public static List<String> disabledWorlds;

    public static void initialize(JavaPlugin plugin) {
        disabledWorlds = new ArrayList<>(ConfigManager.getConfigValue(
                ConfigManager.Option.DISABLED_WORLDS
        ));

        // Waits until the first tick so all abilities are registered
        MVAccessor.scheduleManager().delayedTask(plugin, () -> {
            for (AsyncRepeatingAbility ability : asyncRepeatingAbilities) {
                ability.start();
            }
            // One element array used so that it can be incremented inside scheduler
            int[] tick = {0};
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (AsyncRepeatingAbility ability : asyncRepeatingAbilities) {
                        // Only runs the code at the required interval
                        if (tick[0] % ability.interval() == 0) ability.run();
                    }
                    // Increases tick number
                    tick[0]++;
                }
            }.runTaskTimerAsynchronously(plugin, 0, 1);
        });
    }

    private static boolean skinManagerEnabled = false;

    public static void setSkinManagerEnabled() {
        skinManagerEnabled = true;
    }

    public static boolean isSkinManagerEnabled() {
        return skinManagerEnabled;
    }

    public static void registerAbility(Ability ability, Key key, JavaPlugin instance) {

        if (ability instanceof SkinChangingAbility skinChangingAbility) {
            if (!isSkinManagerEnabled() && !skinChangingAbility.forceEnabled()) return;
            skinChangingAbilities.add(skinChangingAbility);
        }

        invertedAbilityMap.put(ability, key);

        if (instance instanceof OriginsAddon addon) {
            pluginMap.put(ability.getRegisteredKey(), addon);
        }

        if (runOnRegisters.containsKey(ability.getRegisteredKey())) {
            for (AbilityRunnable r : runOnRegisters.get(ability.getRegisteredKey())) r.run(ability);
        }

        ability.initialize(instance);

        if (ability instanceof DependencyAbility dependencyAbility) {
            dependencyAbilityMap.put(ability.getRegisteredKey(), dependencyAbility);
        }
        if (ability instanceof AsyncRepeatingAbility ara) {
            asyncRepeatingAbilities.add(ara);
        }
        if (ability instanceof FlightAllowingAbility flightAllowingAbility) {
            flightAllowingAbilities.add(flightAllowingAbility);
            flightAllowingAbilities.sort(Comparator.comparingInt(ab -> -ab.getPriority()));
        }
        if (ability instanceof TriggerableAbility triggerableAbility) {
            TriggerManager.getInstance().register(triggerableAbility);
        }
        if (ability instanceof MultiAbility multiAbility) {
            for (Ability a : multiAbility.getAbilities()) {
                multiAbilityMap.computeIfAbsent(a.getKey(), k -> new ArrayList<>()).add(multiAbility);
            }
        }
        if (ability instanceof CooldownAbility cooldownAbility) {
            cooldownAbility.setupCooldownConfig(instance);
        }
        if (ability instanceof ListenerAbility listenerAbility) {
            if (listenerAbility.shouldRegisterEvents()) Bukkit.getPluginManager().registerEvents(listenerAbility, instance);
        } else if (ability instanceof Listener listener) {
            Bukkit.getPluginManager().registerEvents(listener, instance);
        }
        if (ability instanceof VisibleAbility visibleAbility) {
            visibleAbility.setupTranslatedText();
        }
        if (ability instanceof AttributeModifierAbility ama) {
            ama.setupAttributeConfig();
            NamespacedKey amaKey = new NamespacedKey(OriginsReborn.getInstance(), ability.getRegisteredKey().asString().replace(":", "-"));
            attributeModifierAbilities.add(new Pair<>(ama, amaKey));
        }
        if (ability instanceof BreakSpeedModifierAbility breakSpeedModifierAbility) {
            breakSpeedModifierAbilities.add(breakSpeedModifierAbility);
        }
        abilityMap.put(ability.getRegisteredKey(), ability);
    }

    /**
     * @deprecated Testing abilities is now contained in the Ability interface
     */
    @Deprecated(forRemoval = true)
    public static void runForAbility(Entity entity, Key key, Runnable runnable) {
        runForAbility(entity, key, runnable, () -> {});
    }

    /**
     * @deprecated Testing abilities is now contained in the Ability interface
     */
    @Deprecated(forRemoval = true)
    public static boolean hasAbility(Player player, Key key) {
        return hasAbility(player, key, false);
    }

    /**
     * @deprecated Testing abilities is now contained in the Ability interface
     */
    @Deprecated(forRemoval = true)
    public static boolean hasAbility(Player player, Key key, boolean ignoreOverrides) {
        if (!abilityMap.containsKey(key)) return false;
        return abilityMap.get(key).hasAbility(player);
    }

    /**
     * @deprecated Testing abilities is now contained in the Ability interface
     */
    @Deprecated(forRemoval = true)
    public static void runForAbility(Entity entity, Key key, Runnable runnable, Runnable other) {
        if (entity == null) return;
        String worldId = entity.getWorld().getName();
        if (ConfigManager.getConfigValue(ConfigManager.Option.DISABLED_WORLDS).contains(worldId)) return;
        if (entity instanceof Player player) {
            if (hasAbility(player, key)) {
                runnable.run();
                return;
            }
        }
        other.run();
    }

    /**
     * @deprecated Testing abilities is now contained in the Ability interface
     */
    @Deprecated(forRemoval = true)
    public static void runWithoutAbility(Entity entity, Key key, Runnable runnable) {
        runForAbility(entity, key, () -> {}, runnable);
    }

    public static FlyData flyData(Player player, boolean disabledWorld) {
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR || FlightToggleCommand.canFly(player)) return new FlyData(true, false);
        if (disabledWorld) return new FlyData(false, false);
        for (FlightAllowingAbility ability : AbilityRegister.flightAllowingAbilities) {
            if (ability.hasAbility(player) && ability.canFly(player)) {
                return new FlyData(true, ability.forceFly(player));
            }
        }
        return new FlyData(false, false);
    }

    public record FlyData(boolean canFly, boolean forceFly) {

    }

    public static boolean isInvisible(Player player, boolean invisible) {
        if (invisible) return true;
        if (GSitHook.sitting.contains(player.getUniqueId())) return true;
        for (Ability ability : abilityMap.values()) {
            if (ability instanceof VisibilityChangingAbility visibilityChangingAbility) {
                if (ability.hasAbility(player) && visibilityChangingAbility.isInvisible(player)) return true;
            }
        }
        return false;
    }

    public static void updateFlight(Player player, boolean inDisabledWorld) {
        GameMode gm = player.getGameMode();
        if (gm == GameMode.SPECTATOR) return;
        if (gm == GameMode.CREATIVE || FlightToggleCommand.canFly(player) || FlightOwnership.ownedByOther(player)) {
            player.setFlySpeed(0.1f);
            return;
        }
        if (inDisabledWorld) return;
        TriState flyingFallDamage = TriState.FALSE;
        float speed = -1f;
        for (FlightAllowingAbility ability : flightAllowingAbilities) {
            if (ability.hasAbility(player) && ability.canFly(player)) {
                float abilitySpeed = ability.getFlightSpeed(player);
                speed = speed == -1 ? abilitySpeed : Math.min(speed, abilitySpeed);
                if (ability.getFlyingFallDamage(player) == TriState.TRUE) {
                    flyingFallDamage = TriState.TRUE;
                }
            }
        }
        MVAccessor.get().setFlyingFallDamage(player, flyingFallDamage);
        player.setFlySpeed(speed == -1 ? 0 : speed);
    }

    public static void updateEntity(Player player, Entity target) {
        byte data = 0;
        if (target.getFireTicks() > 0) {
            data += 0x01;
        }
        if (target.isGlowing()) {
            data += 0x40;
        }
        if (target instanceof LivingEntity entity) {
            if (entity.isInvisible()) data += 0x20;
        }
        if (target instanceof Player targetPlayer) {
            if (targetPlayer.isSneaking()) {
                data += 0x02;
            }
            if (targetPlayer.isSprinting()) {
                data += 0x08;
            }
            if (targetPlayer.isSwimming()) {
                data += 0x10;
            }
            if (targetPlayer.isGliding()) {
                data += (byte) 0x80;
            }
            for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                try {
                    ItemStack item = targetPlayer.getInventory().getItem(equipmentSlot);
                    if (item != null) {
                        player.sendEquipmentChange(targetPlayer, equipmentSlot, item);
                    }
                } catch (IllegalArgumentException ignored) {}
            }
        }

        MVAccessor.get().sendEntityData(player, target, data);
    }

    public interface AbilityRunnable {
        void run(Ability ability);
    }
}
