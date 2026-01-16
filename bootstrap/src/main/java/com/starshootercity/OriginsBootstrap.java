package com.starshootercity;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"unused", "UnstableApiUsage"})
public class OriginsBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        try {
            ModernRegister.setup(context);
        } catch (Throwable ignored) {}
    }
}