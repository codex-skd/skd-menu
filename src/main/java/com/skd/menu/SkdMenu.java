package com.skd.menu;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(SkdMenu.MODID)
public class SkdMenu {
    public static final String MODID = "skd_menu";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SkdMenu(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC, "skd_menu/menu-common.toml");
        LOGGER.info("SKD Menu initialized");
    }
}
