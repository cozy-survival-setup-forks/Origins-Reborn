package com.starshootercity;

import com.starshootercity.abilities.*;
import com.starshootercity.abilities.types.Ability;
import com.starshootercity.abilities.types.BreakSpeedModifierAbility;
import com.starshootercity.abilities.types.ParticleAbility;
import com.starshootercity.commands.DiscordCommand;
import com.starshootercity.commands.FlightToggleCommand;
import com.starshootercity.commands.OriginCommand;
import com.starshootercity.cooldowns.Cooldowns;
import com.starshootercity.events.PlayerLeftClickEvent;
import com.starshootercity.geysermc.OREventRegistrar;
import com.starshootercity.packetsenders.NMSInvoker;
import com.starshootercity.skript.SkriptInitializer;
import com.starshootercity.util.*;
import com.starshootercity.util.config.ConfigManager;
import com.starshootercity.util.hooks.GSitHook;
import com.starshootercity.version.*;
import dev.triumphteam.gui.TriumphGui;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.PluginCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OriginsReborn extends OriginsAddon {

    private static OriginsReborn instance;

    // Used for the legacy updater
    public static FreshAir freshAir;

    public static OriginsReborn getInstance() {
        return instance;
    }

    private static Cooldowns cooldowns;

    public static Cooldowns getCooldowns() {
        return cooldowns;
    }

    public static MultiVersionExecutor getMVE() {
        return MVAccessor.get();
    }

    /**
     * @deprecated NMS Invoker code has been renamed to the MultiVersionExecutor
     * @see OriginsReborn#getMVE()
     * @return The Multi version executor, used to run code that is dependent on the server's version
     */
    @Deprecated(forRemoval = true)
    public static NMSInvoker getNMSInvoker() {
        return getMVE();
    }

    private static void initializeMVE(OriginsReborn instance) {
        MVAccessor.startInitialize(Bukkit.getBukkitVersion().split("-")[0]);
        Bukkit.getPluginManager().registerEvents(MVAccessor.get(), instance);
        getMVE().initialize();
    }

    private boolean vaultEnabled;

    public boolean isVaultEnabled() {
        return vaultEnabled;
    }

    public void setComments(String path, List<String> comments) {
        getMVE().setComments(getConfig(), path, comments);
    }

    private static boolean worldGuardHookInitialized;
    public static boolean usesSkriptAbilities = false;

    public static boolean isWorldGuardHookInitialized() {
        return worldGuardHookInitialized;
    }

    @Override
    public void onLoad() {
        try {
            worldGuardHookInitialized = WorldGuardHook.tryInitialize();
        }
        catch (Throwable t) {
            worldGuardHookInitialized = false;
        }
    }

    @Override
    public void onDisable() {
        if (!AbilityRegister.isSkinManagerEnabled()) return;
        SkinManager.unload();
    }

    @Override
    public void onRegister() {
        instance = this;
        TriumphGui.init(this);
        if (Bukkit.getPluginManager().isPluginEnabled("Geyser-Spigot")) {
            OREventRegistrar.initialize(this);
        }

        if (Bukkit.getPluginManager().isPluginEnabled("GSit")) {
            GSitHook.Setup(this);
        }

        AbilityRegister.setupAbilityConfig(this);

        int pluginId = 25114;
        Metrics metrics = new Metrics(this, pluginId);

        metrics.addCustomChart(new Metrics.SimplePie(
                "addon_count",
                () -> String.valueOf(Math.max(0, AddonLoader.registeredAddons.size()-1))
        ));

        metrics.addCustomChart(new Metrics.AdvancedPie(
                "addons",
                () -> {
                    Map<String, Integer> data = new HashMap<>();
                    for (OriginsAddon addon : AddonLoader.registeredAddons) {
                        if (addon == this) continue;
                        data.put(addon.getName(), 1);
                    }
                    return data;
                }
        ));

        metrics.addCustomChart(new Metrics.AdvancedPie(
                "origins",
                () -> {
                    Map<String, Integer> data = new HashMap<>();
                    for (String layer : AddonLoader.layers) {
                        for (Origin origin : AddonLoader.getOrigins(layer)) {
                            String info = "%s - %s".formatted(origin.getNameForDisplay(), layer);
                            data.put(info, 1);
                        }
                    }
                    return data;
                }
        ));

        // Used for legacy updater
        freshAir = new FreshAir();

        initializeMVE(this);
        saveDefaultConfig();
        ConfigManager.Option.initialize();

        if (ConfigManager.getConfigValue(ConfigManager.Option.SKINSRESTORER_HOOK_ENABLED)) {
            if (Bukkit.getPluginManager().isPluginEnabled("SkinsRestorer")) {
                AbilityRegister.setSkinManagerEnabled();
                Bukkit.getPluginManager().registerEvents(new SkinManager(), this);
            } else {
                getLogger().warning("The SkinsRestorer hook is enabled in the config, however SkinsRestorer is not installed.");
            }
        }

        metrics.addCustomChart(new Metrics.SimplePie(
                "skinsrestorer_hook_enabled",
                () -> String.valueOf(AbilityRegister.isSkinManagerEnabled())
        ));

        if (worldGuardHookInitialized) WorldGuardHook.completeInitialize();

        Translator.initialize(this);

        if (ConfigManager.getConfigValue(ConfigManager.Option.SWAP_COMMAND_VAULT_ENABLED)) {
            vaultEnabled = VaultHook.setupEconomy(getServer());
            if (!vaultEnabled) {
                getLogger().warning("Vault is missing, origin swaps will not cost currency");
            }
        } else vaultEnabled = false;

        metrics.addCustomChart(new Metrics.SimplePie(
                "vault_hook_enabled",
                () -> String.valueOf(vaultEnabled)
        ));

        metrics.addCustomChart(new Metrics.SimplePie(
                "uses_skript_abilities",
                () -> String.valueOf(usesSkriptAbilities)
        ));

        metrics.addCustomChart(new Metrics.SimplePie(
                "worldguard_hook_enabled",
                () -> String.valueOf(worldGuardHookInitialized)
        ));

        cooldowns = new Cooldowns();
        if (!ConfigManager.getConfigValue(ConfigManager.Option.DISABLE_ALL_COOLDOWNS) && ConfigManager.getConfigValue(ConfigManager.Option.SHOW_COOLDOWN_ICONS)) {
            Bukkit.getPluginManager().registerEvents(cooldowns, this);
        }
        SkriptInitializer.initialize(this);

        Bukkit.getPluginManager().registerEvents(TriggerManager.getInstance(), this);
        Bukkit.getPluginManager().registerEvents(new OriginSwapper(), this);
        Bukkit.getPluginManager().registerEvents(new OrbOfOrigin(), this);
        Bukkit.getPluginManager().registerEvents(new PackApplier(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerLeftClickEvent.PlayerLeftClickEventListener(), this);
        Bukkit.getPluginManager().registerEvents(new ParticleAbility.ParticleAbilityListener(), this);
        Bukkit.getPluginManager().registerEvents(new BreakSpeedModifierAbility.BreakSpeedModifierAbilityListener(), this);

        PluginCommand flightCommand = retrieveCommand("fly");
        if (flightCommand != null) flightCommand.setExecutor(new FlightToggleCommand());

        File export = new File(getDataFolder(), "export");
        if (!export.exists()) {
            boolean ignored = export.mkdir();
        }
        File imports = new File(getDataFolder(), "import");
        if (!imports.exists()) {
            boolean ignored = imports.mkdir();
        }

        PluginCommand command = retrieveCommand("origin");
        if (command != null) command.setExecutor(new OriginCommand());
        OriginCommand.initialize();

        PluginCommand discord = retrieveCommand("origindiscord");
        if (discord != null) discord.setExecutor(new DiscordCommand());

        AbilityRegister.initialize(this);
    }

    public @Nullable PluginCommand retrieveCommand(String command) {
        try {
            return getCommand(command);
        } catch (UnsupportedOperationException e) {
            return null;
        }
    }

    @Override
    public @NotNull String getNamespace() {
        return "origins";
    }

    @Override
    public @NotNull List<Ability> getRegisteredAbilities() {
        List<Ability> abilities = new ArrayList<>(List.of(
                new PumpkinHate(),
                new FallImmunity(),
                new WeakArms(),
                new Fragile(),
                new SlowFalling(),
                freshAir,
                new Vegetarian(),
                new LayEggs(),
                new NoShield(),
                new MasterOfWebs(),
                new Tailwind(),
                new Arthropod(),
                new Climbing(),
                new Carnivore(),
                new WaterBreathing(),
                new WaterVision(),
                new CatVision(),
                new NineLives(),
                new BurnInDaylight(),
                new WaterVulnerability(),
                new Phantomize(),
                new Invisibility(),
                new ThrowEnderPearl(),
                new PhantomizeOverlay(),
                new FireImmunity(),
                new AirFromPotions(),
                new SwimSpeed(),
                new LikeWater(),
                new LightArmor(),
                new MoreKineticDamage(),
                new DamageFromPotions(),
                new DamageFromSnowballs(),
                new Hotblooded(),
                new BurningWrath(),
                new SprintJump(),
                new AerialCombatant(),
                new Elytra(),
                new LaunchIntoAir(),
                new HungerOverTime(),
                new MoreExhaustion(),
                new Aquatic(),
                new NetherSpawn(),
                new Claustrophobia(),
                new VelvetPaws(),
                new AquaAffinity(),
                new FlameParticles(),
                new EnderParticles(),
                new Phasing(),
                new ScareCreepers(),
                new StrongArms(),
                StrongArms.StrongArmsBreakSpeed.strongArmsBreakSpeed,
                StrongArms.StrongArmsDrops.strongArmsDrops,
                new ShulkerInventory(),
                new NaturalArmor(),
                new ConduitPowerOnLand(),
                new Grayscale()
        ));
        Attribute blockInteractionRange = MVAttribute.BLOCK_INTERACTION_RANGE.get();
        Attribute entityInteractionRange = MVAttribute.ENTITY_INTERACTION_RANGE.get();
        if (blockInteractionRange != null && entityInteractionRange != null) {
            abilities.add(new ExtraReach(blockInteractionRange, entityInteractionRange));
            abilities.add(ExtraReach.ExtraReachBlocks.extraReachBlocks);
            abilities.add(ExtraReach.ExtraReachEntities.extraReachEntities);
        }
        return abilities;
    }
}
