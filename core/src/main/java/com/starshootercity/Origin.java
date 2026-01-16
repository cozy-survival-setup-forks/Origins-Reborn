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
import java.util.stream.Stream;

@APITarget
public class Origin {
    private final ItemStack icon;
    private final int position;
    private final char impact;
    private final String name;
    private final @NotNull String displayName;
    private final int priority;
    private final String unchoosable;
    private final OriginsAddon addon;
    private final List<Key> abilityKeys;
    private List<Ability> abilities = null;


    private final String description;
    private final String permission;
    private final Integer cost;
    private final int max;
    private final String layer;

    public Integer getCost() {
        return cost;
    }

    public boolean isUnchoosable(Player player) {
        return isUnchoosable(player, false);
    }

    public boolean isUnchoosable(Player player, boolean random) {
        if (unchoosable.equals("true")) return true;
        if (!random && unchoosable.equals("random")) return true;
        String mode = ConfigManager.getConfigValue(ConfigManager.Option.REUSING_ORIGINS);
        boolean same = ConfigManager.getConfigValue(ConfigManager.Option.PREVENT_SAME_ORIGINS);
        if (max != -1) {
            int num = 0;
            for (String p : OriginSwapper.getOriginFileConfiguration().getKeys(false)) {
                if (OriginSwapper.getOriginFileConfiguration().getString(p + "." + layer, "").equals(getName().toLowerCase())) {
                    num++;
                }
            }
            if (num >= max) return true;
        }
        if (same) {
            for (String p : OriginSwapper.getOriginFileConfiguration().getKeys(false)) {
                if (OriginSwapper.getOriginFileConfiguration().getString(p + "." + layer, "").equals(getName().toLowerCase())) {
                    return true;
                }
            }
        }
        if (mode.equals("PERPLAYER")) {
            return OriginSwapper.getUsedOriginFileConfiguration().getStringList(player.getUniqueId().toString()).contains(getName().toLowerCase());
        } else if (mode.equals("ALL")) {
            for (String p : OriginSwapper.getUsedOriginFileConfiguration().getKeys(false)) {
                if (OriginSwapper.getUsedOriginFileConfiguration().getStringList(p).contains(getName().toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    public int getPriority() {
        return priority;
    }

    private final Team team;

    public Team getTeam() {
        return team;
    }

    public String getPermission() {
        return permission;
    }

    public boolean hasPermission() {
        return permission != null;
    }

    public String getLayer() {
        return layer;
    }

    public String getNameForDisplay() {
        return displayName;
    }

    static {
        Translator.registerTranslation("team_format", "§7[§r%s§7] ");
    }

    public Origin(String name, ItemStack icon, int position, @Range(from = 0, to = 3) int impact, @NotNull String displayName, List<Key> abilities, String description, OriginsAddon addon, String unchoosable, int priority, String permission, Integer cost, int max, String layer) {
        this.displayName = displayName;
        this.description = description;
        this.name = name;
        this.permission = permission;
        this.cost = cost;
        this.max = max;
        this.layer = layer;
        if (ConfigManager.getConfigValue(ConfigManager.Option.DISPLAY_ENABLE_PREFIXES)) {
            Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
            Team oldTeam = scoreboard.getTeam(name);
            if (oldTeam != null) oldTeam.unregister();
            team = scoreboard.registerNewTeam(name);
            team.displayName(Component.text(Translator.translate("team_format").formatted(name)));
        } else team = null;
        this.abilityKeys = abilities;
        this.icon = icon;
        this.position = position;
        this.unchoosable = unchoosable;
        this.impact = switch (impact) {
            case 0 -> '\uE002';
            case 1 -> '\uE003';
            case 2 -> '\uE004';
            default -> '\uE005';
        };
        this.addon = addon;
        this.priority = priority;
    }

    public List<VisibleAbility> getVisibleAbilities() {
        List<VisibleAbility> result = new ArrayList<>();
        for (Ability ability : getAbilities()) {
            if (ability instanceof VisibleAbility visibleAbility) {
                result.add(visibleAbility);
            }
        }
        return result;
    }

    public OriginsAddon getAddon() {
        return addon;
    }

    public List<Ability> getAbilities() {
        if (abilities == null) {
            abilities = abilityKeys.stream()
                    .map(AbilityRegister.abilityMap::get)
                    .flatMap(ability -> {
                        if (ability instanceof MultiAbility multiAbility) {
                            return Stream.concat(Stream.of(ability), multiAbility.getAbilities().stream());
                        } else {
                            return Stream.of(ability);
                        }
                    }).toList();
        }
        return abilities;
    }

    public boolean hasAbility(Ability ability) {
        return getAbilities().contains(ability);
    }

    public char getImpact() {
        return impact;
    }

    public int getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public String getResourceURL() {
        String key = icon.getType().getKey().value();
        return "https://assets.mcasset.cloud/1.21.4/assets/minecraft/textures/%s/%s.png".formatted(icon.getType().isBlock() ? "block" : "item", key);
    }
}
