package com.starshootercity.abilities.types;

import com.starshootercity.APITarget;
import com.starshootercity.OriginsReborn;
import com.starshootercity.util.TriggerManager;
import com.starshootercity.util.config.ConfigManager;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;

/**
 * An ability that can be triggered with multiple 'triggers' such as left click, right click etc.
 * <br><br>
 * Triggers can be configured in the ability-config.yml
 */
@APITarget
public interface TriggerableAbility extends Ability {

    char PRIMARY_KEYBIND = 'G';

    Set<Material> SWORDS = Set.of(
            Material.WOODEN_SWORD,
            Material.STONE_SWORD,
            Material.IRON_SWORD,
            Material.GOLDEN_SWORD,
            Material.DIAMOND_SWORD,
            Material.NETHERITE_SWORD
    );

    /** Used for when the server and player have keybind modifications set up */
    default char getDefaultKeybind() {
        return PRIMARY_KEYBIND;
    }

    enum TriggerType {
        /** Player left clicks */
        LEFT_CLICK,
        /** Player right clicks */
        RIGHT_CLICK,
        /** Player pressed offhand swap key */
        OFFHAND_SWAP,
        /** Player jumps */
        JUMP,
        /** Player enables sprinting */
        SPRINT_ON,
        /** Player disables sprinting */
        SPRINT_OFF,
        /** Player toggles sprinting */
        SPRINT_TOGGLE,
        /** Player enables sneaking */
        SNEAK_ON,
        /** Player disables sneaking */
        SNEAK_OFF,
        /** Player toggles sneaking */
        SNEAK_TOGGLE,
        /** Player double taps sneak quickly */
        DOUBLE_TAP_SNEAK,
        /** Player right-clicks their chestplate slot */
        RIGHT_CLICK_CHESTPLATE
    }

    enum Condition {

        EMPTY_HAND(event -> event.itemType(Material::isAir)),
        NO_BLOCK(event -> !event.hasBlock()),
        GLIDING(event -> event.player().isGliding()),
        CROUCHING(event -> event.player().isSneaking()),
        DUMMY(event -> true),
        HOLDING_SWORD(event -> event.itemType(SWORDS::contains));

        private final Predicate<TriggerManager.TriggerEvent> predicate;

        Condition(Predicate<TriggerManager.TriggerEvent> predicate) {
            this.predicate = predicate;
        }
    }

    interface TriggerRunner {
        void run(TriggerManager.TriggerEvent event);
    }

    class TriggerBuilder {

        private final TriggerType type;
        private final Set<Condition> conditions;
        private final Ability ability;

        public Trigger build(TriggerRunner runner) {
            String typePath = "trigger.type";
            String conditionPath = "trigger.conditions";

            ability.registerConfigOption(
                    typePath,
                    List.of("The action (e.g. LEFT_CLICK, RIGHT_CLICK) that triggers this ability", "You can see a full list of actions at https://starshooter.gitbook.io/origins-reborn/configuration#triggerable-ability-configuration"),
                    ConfigManager.SettingType.STRING,
                    type.toString()
            );

            List<String> conditionList = new ArrayList<>();
            for (Condition condition : conditions) {
                conditionList.add(condition.toString());
            }

            ability.registerConfigOption(
                    conditionPath,
                    List.of("The conditions (e.g. EMPTY_HAND, NO_BLOCK) that are required to trigger this ability", "You can see a full list of conditions at https://starshooter.gitbook.io/origins-reborn/configuration#triggerable-ability-configuration"),
                    ConfigManager.SettingType.STRING_LIST,
                    conditionList
            );

            TriggerType usedType = type;
            try {
                usedType = TriggerType.valueOf(ability.getConfigOption(typePath, ConfigManager.SettingType.STRING));
            } catch (IllegalArgumentException e) {
                OriginsReborn.getInstance().getLogger().warning("Malformed trigger configuration for ability %s, using default values".formatted(ability.getRegisteredKey()));
            }

            Set<Condition> usedConditions = conditions;
            try {
                Set<Condition> newConds = new HashSet<>();
                for (String s : ability.getConfigOption(conditionPath, ConfigManager.SettingType.STRING_LIST)) {
                    newConds.add(Condition.valueOf(s));
                }
                usedConditions = newConds;
            } catch (IllegalArgumentException e) {
                OriginsReborn.getInstance().getLogger().warning("Malformed trigger configuration for ability %s, using default values".formatted(ability.getRegisteredKey()));
            }

            return new Trigger(usedType, usedConditions, runner, ability);
        }

        public TriggerBuilder addConditions(Condition... conditions) {
            this.conditions.addAll(Arrays.stream(conditions).toList());
            return this;
        }

        private TriggerBuilder(TriggerType type, Ability ability) {
            this.type = type;
            this.ability = ability;
            this.conditions = new HashSet<>();
        }
    }

    class Trigger {

        private final TriggerType type;
        private final Set<Condition> conditions;
        private final TriggerRunner runner;
        private final Ability ability;

        private Trigger(TriggerType defaultType, Set<Condition> defaultConditions, TriggerRunner runner, Ability ability) {
            this.type = defaultType;
            this.conditions = defaultConditions;
            this.runner = runner;
            this.ability = ability;
        }

        public TriggerType getType() {
            return type;
        }

        public static TriggerBuilder builder(TriggerType type, Ability ability) {
            return new TriggerBuilder(type, ability);
        }

        public void run(TriggerManager.TriggerEvent event) {
            for (Condition condition : conditions) {
                if (!condition.predicate.test(event)) return;
            }
            ability.runForAbility(event.player(), player -> runner.run(event));
        }
    }

    @NotNull
    Trigger getTrigger();
}
