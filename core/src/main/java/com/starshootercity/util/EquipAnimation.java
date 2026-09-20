package com.starshootercity.util;

import com.starshootercity.OriginsReborn;
import com.starshootercity.util.config.ConfigManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * The title that types out the name of an origin when a player gets it, in the colour of that origin, followed by a
 * subtitle and a chime.
 */
public final class EquipAnimation {

    private static final long START_DELAY = 5L;
    private static final long TYPING_DELAY = 2L;
    private static final long SUBTITLE_GAP = 8L;
    private static final long REPEAT_WINDOW_MILLIS = 2000L;

    private static final Title.Times TIMES = Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(3000), Duration.ofMillis(500));

    /** Which animation each player is on. A newer one stops the tasks of the older one. */
    private static final Map<UUID, Integer> generations = new HashMap<>();
    private static final Map<UUID, Long> lastStarted = new HashMap<>();

    private EquipAnimation() {
    }

    /** Called when a player gets an origin. Skipped when the animation is off, or one just started for this player. */
    public static void onSwap(Player player, String originName) {
        if (!ConfigManager.getConfigValue(ConfigManager.Option.EQUIP_ANIMATION_ENABLED)) return;
        if (!ConfigManager.getConfigValue(ConfigManager.Option.EQUIP_ANIMATION_ON_SWAP)) return;

        long now = System.currentTimeMillis();
        Long previous = lastStarted.get(player.getUniqueId());
        if (previous != null && now - previous < REPEAT_WINDOW_MILLIS) return;
        play(player, originName);
    }

    public static void forget(Player player) {
        generations.remove(player.getUniqueId());
        lastStarted.remove(player.getUniqueId());
    }

    /** Plays the animation for an origin name. It does not have to be a real origin. */
    public static void play(Player player, String originName) {
        String title = originName.trim().toUpperCase(Locale.ROOT);
        if (title.isEmpty()) return;

        UUID id = player.getUniqueId();
        lastStarted.put(id, System.currentTimeMillis());
        int generation = generations.merge(id, 1, Integer::sum);

        TextColor color = colorFor(originName);
        String subtitle = ConfigManager.getConfigValue(ConfigManager.Option.EQUIP_ANIMATION_SUBTITLE);
        int[] letters = title.codePoints().toArray();

        schedule(player, generation, START_DELAY, () -> player.showTitle(Title.title(
                titleText(letters, 1, color), Component.empty(), TIMES)));

        for (int index = 1; index < letters.length; index++) {
            int shown = index + 1;
            long delay = START_DELAY + shown * TYPING_DELAY;
            schedule(player, generation, delay, () -> {
                player.sendTitlePart(TitlePart.TITLE, titleText(letters, shown, color));
                click(player, 0.3f, 1.6f + (shown % 3) * 0.1f);
            });
        }

        int[] subtitleLetters = subtitle.codePoints().toArray();
        long subtitleStart = START_DELAY + letters.length * TYPING_DELAY + SUBTITLE_GAP;
        for (int index = 0; index < subtitleLetters.length; index++) {
            int shown = index + 1;
            long delay = subtitleStart + shown * TYPING_DELAY;
            schedule(player, generation, delay, () -> {
                Component text = Component.text("★ ", color)
                        .append(Component.text(new String(subtitleLetters, 0, shown), NamedTextColor.WHITE))
                        .append(Component.text(" ★", color));
                player.sendTitlePart(TitlePart.SUBTITLE, text);
                click(player, 0.25f, 1.8f + (shown % 3) * 0.1f);
            });
        }

        schedule(player, generation, subtitleStart + subtitleLetters.length * TYPING_DELAY, () ->
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, SoundCategory.MASTER, 1.0f, 1.2f));
    }

    private static Component titleText(int[] letters, int shown, TextColor color) {
        return Component.text(new String(letters, 0, shown), color, TextDecoration.BOLD);
    }

    private static void click(Player player, float volume, float pitch) {
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.MASTER, volume, pitch);
    }

    private static void schedule(Player player, int generation, long delay, Runnable task) {
        Bukkit.getScheduler().runTaskLater(OriginsReborn.getInstance(), () -> {
            if (!player.isOnline()) return;
            if (generations.getOrDefault(player.getUniqueId(), 0) != generation) return;
            task.run();
        }, delay);
    }

    /** The colour of an origin: the name written in lower case without spaces, dashes or underscores. */
    static TextColor colorFor(String originName) {
        String key = originName.trim().toLowerCase(Locale.ROOT).replace(" ", "").replace("-", "").replace("_", "");
        Map<String, String> colors = ConfigManager.getConfigValue(ConfigManager.Option.EQUIP_ANIMATION_COLORS);
        TextColor own = parseColor(colors.get(key));
        if (own != null) return own;

        TextColor fallback = parseColor(ConfigManager.getConfigValue(ConfigManager.Option.EQUIP_ANIMATION_DEFAULT_COLOR));
        return fallback == null ? NamedTextColor.AQUA : fallback;
    }

    static TextColor parseColor(String text) {
        if (text == null) return null;
        String trimmed = text.trim();
        if (trimmed.startsWith("#")) return TextColor.fromHexString(trimmed);
        return NamedTextColor.NAMES.value(trimmed.toLowerCase(Locale.ROOT));
    }
}
