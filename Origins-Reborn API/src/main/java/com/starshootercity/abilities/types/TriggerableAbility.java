package com.starshootercity.abilities.types;

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
public interface TriggerableAbility extends Ability {

    char PRIMARY_KEYBIND = null;

    Set<Material> SWORDS = null;

    /**
     * Used for when the server and player have keybind modifications set up
     */
    default char getDefaultKeybind() {
        return ' ';
    }

    enum TriggerType {

        /**
         * Player left clicks
         */
        LEFT_CLICK,
        /**
         * Player right clicks
         */
        RIGHT_CLICK,
        /**
         * Player pressed offhand swap key
         */
        OFFHAND_SWAP,
        /**
         * Player jumps
         */
        JUMP,
        /**
         * Player enables sprinting
         */
        SPRINT_ON,
        /**
         * Player disables sprinting
         */
        SPRINT_OFF,
        /**
         * Player toggles sprinting
         */
        SPRINT_TOGGLE,
        /**
         * Player enables sneaking
         */
        SNEAK_ON,
        /**
         * Player disables sneaking
         */
        SNEAK_OFF,
        /**
         * Player toggles sneaking
         */
        SNEAK_TOGGLE,
        /**
         * Player double taps sneak quickly
         */
        DOUBLE_TAP_SNEAK,
        /**
         * Player right-clicks their chestplate slot
         */
        RIGHT_CLICK_CHESTPLATE
    }

    enum Condition {

        EMPTY_HAND(event -> event.itemType(Material::isAir)), NO_BLOCK(event -> !event.hasBlock()), GLIDING(event -> event.player().isGliding()), DUMMY(event -> true), HOLDING_SWORD(event -> event.itemType(SWORDS::contains));

        Condition(Predicate<TriggerManager.TriggerEvent> predicate) {
        }
    }

    interface TriggerRunner {

        void run(TriggerManager.TriggerEvent event);
    }

    class TriggerBuilder {

        public Trigger build(TriggerRunner runner) {
            return null;
        }

        public TriggerBuilder addConditions(Condition... conditions) {
            return null;
        }
    }

    class Trigger {

        public TriggerType getType() {
            return null;
        }

        public static TriggerBuilder builder(TriggerType type, Ability ability) {
            return null;
        }

        public void run(TriggerManager.TriggerEvent event) {
        }
    }

    @NotNull
    Trigger getTrigger();
}
