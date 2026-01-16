package com.starshootercity;

import com.starshootercity.abilities.types.Ability;
import com.starshootercity.abilities.types.MultiAbility;
import com.starshootercity.abilities.types.VisibleAbility;
import com.starshootercity.util.AbilityRegister;
import com.starshootercity.util.config.ConfigManager;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import java.util.ArrayList;
import java.util.List;

public class Origin {

    public Integer getCost() {
        return null;
    }

    public boolean isUnchoosable(Player player) {
        return false;
    }

    public int getPriority() {
        return 0;
    }

    public Team getTeam() {
        return null;
    }

    public String getPermission() {
        return null;
    }

    public boolean hasPermission() {
        return false;
    }

    public String getLayer() {
        return null;
    }

    public String getNameForDisplay() {
        return null;
    }

    static {
        Translator.registerTranslation("team_format", "§7[§r%s§7] ");
    }

    public Origin(String name, ItemStack icon, int position, @Range(from = 0, to = 3) int impact, @NotNull String displayName, List<Key> abilities, String description, OriginsAddon addon, boolean unchoosable, int priority, String permission, Integer cost, int max, String layer) {
    }

    public List<VisibleAbility> getVisibleAbilities() {
        return null;
    }

    public OriginsAddon getAddon() {
        return null;
    }

    public List<Ability> getAbilities() {
        return null;
    }

    public boolean hasAbility(Key key) {
        return false;
    }

    public char getImpact() {
        return ' ';
    }

    public int getPosition() {
        return 0;
    }

    public String getName() {
        return null;
    }

    public String getDescription() {
        return null;
    }

    public ItemStack getIcon() {
        return null;
    }

    public String getResourceURL() {
        return null;
    }
}
