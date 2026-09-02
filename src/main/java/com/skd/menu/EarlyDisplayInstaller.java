package com.skd.menu;

import org.slf4j.Logger;

/**
 * No-op on this branch. NeoForge 21.1.x / FancyModLoader 1.21.1 has no {@code earlyLoadingScreenTheme}
 * config key and no JSON early-display theme system (that arrived in a later NeoForge), so the FML
 * "early window" shown while mods load cannot be themed here. The 26.2 tree keeps the full installer.
 */
public final class EarlyDisplayInstaller {
    private static final Logger LOGGER = SkdMenu.LOGGER;

    private EarlyDisplayInstaller() {}

    public static void install() {
        LOGGER.info("SKD Menu: FML early-window theming is not supported on NeoForge 21.1.x (MC 1.21.1); early-display theme not installed.");
    }
}
