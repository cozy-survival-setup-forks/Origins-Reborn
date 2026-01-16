package com.starshootercity.abilities.types;

import com.starshootercity.APITarget;
import com.starshootercity.OriginSwapper;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * An ability that shows up in the Origin selection GUI
 */
@APITarget
public interface VisibleAbility extends Ability {

    /**
     * Sets up the translated texts in the translations.yml file
     * Should not be used outside the main Origins-Reborn plugin
     */
    @ApiStatus.Internal
    default void setupTranslatedText() {
        registerTranslation("title", title());
        registerTranslation("description", description());
    }

    @NotNull
    default List<OriginSwapper.LineData.LineComponent> getDescription() {
        if (!useTranslator()) return OriginSwapper.LineData.makeLineFor(description(), OriginSwapper.LineData.LineComponent.LineType.DESCRIPTION);
        else {
            String value = translate("description");
            try {
                value = modifyDescription(value);
            } catch (Throwable ignored) {}
            return OriginSwapper.LineData.makeLineFor(value, OriginSwapper.LineData.LineComponent.LineType.DESCRIPTION);
        }
    }

    /**
     * Default description of the ability, can be configured in translations.yml
     * @return The default description
     */
    String description();

    @NotNull
    default List<OriginSwapper.LineData.LineComponent> getTitle() {
        if (!useTranslator()) return OriginSwapper.LineData.makeLineFor(title(), OriginSwapper.LineData.LineComponent.LineType.TITLE);
        else {
            String value = translate("title");
            try {
                value = modifyDescription(value);
            } catch (Throwable ignored) {}
            return OriginSwapper.LineData.makeLineFor(value, OriginSwapper.LineData.LineComponent.LineType.TITLE);
        }
    }

    /**
     * Default title of the ability, can be configured in translations.yml
     * @return The default title
     */
    String title();

    /**
     * Modify the description after translation
     * @param description Translated description
     * @return Modified description
     */
    default String modifyDescription(String description) {
        return description;
    }

    /**
     * Modify the title after translation
     * @param title Translated title
     * @return Modified title
     */
    default String modifyTitle(String title) {
        return title;
    }

    default boolean useTranslator() {
        return true;
    }
}
