package com.starshootercity.abilities.types;

import com.starshootercity.OriginSwapper;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import java.util.List;

/**
 * An ability that shows up in the Origin selection GUI
 */
public interface VisibleAbility extends Ability {

    /**
     * Sets up the translated texts in the translations.yml file
     * Should not be used outside the main Origins-Reborn plugin
     */
    @ApiStatus.Internal
    default void setupTranslatedText() {
    }

    @NotNull
    default List<OriginSwapper.LineData.LineComponent> getDescription() {
        return null;
    }

    /**
     * Default description of the ability, can be configured in translations.yml
     * @return The default description
     */
    String description();

    @NotNull
    default List<OriginSwapper.LineData.LineComponent> getTitle() {
        return null;
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
        return null;
    }

    /**
     * Modify the title after translation
     * @param title Translated title
     * @return Modified title
     */
    default String modifyTitle(String title) {
        return null;
    }

    default boolean useTranslator() {
        return false;
    }
}
