package com.skd.menu;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLE_MOD = BUILDER
            .comment("Enable the SKD Menu modifications to the main menu")
            .define("enableMod", true);

    public static final ModConfigSpec.BooleanValue RELOAD_JSON_ON_TITLE = BUILDER
            .comment("Reload the menu JSON config every time the title screen opens")
            .define("reloadJsonOnTitle", false);

    static final ModConfigSpec SPEC = BUILDER.build();
}
