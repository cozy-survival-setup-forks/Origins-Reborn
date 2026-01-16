package com.starshootercity;

import com.starshootercity.commands.DiscordCommand;
import com.starshootercity.commands.FlightToggleCommand;
import com.starshootercity.commands.OriginCommand;
import com.starshootercity.version.MVEnchantment;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.EnchantmentKeys;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;

@SuppressWarnings("UnstableApiUsage")
public class ModernRegister {
    public static void setup(BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register("origin", new CommandAdapter("origin", new OriginCommand()));
            commands.registrar().register("origindiscord", new CommandAdapter("origindiscord", new DiscordCommand()));
            commands.registrar().register("fly", new CommandAdapter("fly", new FlightToggleCommand()));
        });

        TypedKey<Enchantment> waterProtectionKey = EnchantmentKeys.create(Key.key("origins:water_protection"));
        context.getLifecycleManager().registerEventHandler(RegistryEvents.ENCHANTMENT.freeze().newHandler(event -> {
            event.registry().register(
                    waterProtectionKey,
                    b -> b.description(Component.text("Water Protection"))
                            .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_ARMOR))
                            .anvilCost(1)
                            .maxLevel(4)
                            .weight(10)
                            .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                            .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                            .activeSlots(EquipmentSlotGroup.ANY)
            );
        }));

        MVEnchantment.WATER_PROTECTION.set(executor -> RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(waterProtectionKey));
    }
}
