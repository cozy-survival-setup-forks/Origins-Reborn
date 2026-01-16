package com.starshootercity;

import com.starshootercity.abilities.types.Ability;
import com.starshootercity.events.PlayerSwapOriginEvent;
import com.starshootercity.version.ResourcePackInfo;
import com.starshootercity.util.AbilityRegister;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.File;
import java.util.List;

/**
 * This is the class you should extend to make an Origins-Reborn addon.
 * <br><br>
 * This should be your main class in plugin.yml, OriginsAddon extends JavaPlugin.
 */
public abstract class OriginsAddon extends JavaPlugin {

    @Nullable
    public SwapStateGetter shouldOpenSwapMenu() {
        return null;
    }

    @Nullable
    public SwapStateGetter shouldAllowOriginSwapCommand() {
        return null;
    }

    @Nullable
    public KeyStateGetter getAbilityOverride() {
        return null;
    }

    public interface SwapStateGetter {

        State get(Player player, PlayerSwapOriginEvent.SwapReason reason);
    }

    public interface KeyStateGetter {

        State get(Player player, Key key);
    }

    @SuppressWarnings("unused")
    public enum State {

        ALLOW, DEFAULT, DENY
    }

    @Override
    public final void onEnable() {
    }

    @Nullable
    public ResourcePackInfo getResourcePackInfo() {
        return null;
    }

    @Override
    @NotNull
    public File getFile() {
        return null;
    }

    /**
     * Runs at the start of onEnable, before addon configuration
     * <br><br>
     * This should be used instead of onEnable in your addon
     */
    public void onRegister() {
    }

    /**
     * Runs at the end of onEnable, after addon configuration
     */
    public void afterRegister() {
    }

    @NotNull
    public abstract String getNamespace();

    /**
     * Used to return a list of abilities the plugin should register
     * @return List of abilities the plugin will register
     */
    @NotNull
    public List<Ability> getRegisteredAbilities() {
        return null;
    }
}
