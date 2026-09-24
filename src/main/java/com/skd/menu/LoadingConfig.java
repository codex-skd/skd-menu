package com.skd.menu;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class LoadingConfig {
    private static final Logger LOGGER = SkdMenu.LOGGER;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static LoadingConfig instance;
    private static Path configPath;

    public BackgroundConfig background = new BackgroundConfig();
    public LogoConfig logo = new LogoConfig();
    public BannerConfig banner = new BannerConfig();
    public BarConfig bar = new BarConfig();

    public static LoadingConfig getInstance() {
        if (instance == null) load();
        return instance;
    }

    public static void load() {
        configPath = Path.of("config", "skd_menu", "loading.json");
        if (Files.exists(configPath)) {
            try (Reader reader = Files.newBufferedReader(configPath)) {
                instance = GSON.fromJson(reader, LoadingConfig.class);
                LOGGER.info("Loaded loading config from {}", configPath);
            } catch (Exception e) {
                LOGGER.error("Failed to load loading config, using defaults", e);
                instance = new LoadingConfig();
            }
        } else {
            instance = new LoadingConfig();
            save();
        }
        if (instance == null) instance = new LoadingConfig();
        initDefaults();
    }

    private static void initDefaults() {
        if (instance.background == null) instance.background = new BackgroundConfig();
        if (instance.background.image == null) instance.background.image = new ImageConfig();
        if (instance.background.color == null) instance.background.color = new ColorConfig();
        if (instance.logo == null) instance.logo = new LogoConfig();
        if (instance.banner == null) instance.banner = new BannerConfig();
        if (instance.bar == null) instance.bar = new BarConfig();
        if (instance.bar.fill == null) instance.bar.fill = new FillConfig();
    }

    public static void save() {
        if (configPath == null) configPath = Path.of("config", "skd_menu", "loading.json");
        try {
            Files.createDirectories(configPath.getParent());
            try (Writer writer = Files.newBufferedWriter(configPath)) {
                GSON.toJson(instance, writer);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to save loading config", e);
        }
    }

    public static class BackgroundConfig {
        /** "image" (static image, default) or "color". */
        public String type = "image";
        public ImageConfig image = new ImageConfig();
        public ColorConfig color = new ColorConfig();
    }

    public static class ImageConfig {
        /** Asset identifier ("skd_menu:textures/gui/x.png"), filesystem path, or http(s) URL. */
        public String path = "skd_menu:textures/gui/bg_main_default.png";
        /** "cover" (default, no distortion) or "stretch". */
        public String fit = "cover";
    }

    public static class ColorConfig {
        public int color = 0xFF000000;
    }

    public static class LogoConfig {
        public boolean enabled = true;
        /** Asset identifier, filesystem path, or http(s) URL. Defaults to the same top-right logo used on the menu. */
        public String image = "skd_menu:textures/gui/logo_top_right_default.png";
        /** Percentage of screen width (0-100). {@code -1} anchors to the right edge, matching the menu logo. */
        public float x = -1f;
        /** Percentage of screen height (0-100). {@code -1} = 10px from the top. */
        public float y = 3f;
        public int width = -1;
        public int height = 48;
    }

    /** Wide mod banner shown top-left, matching the menu's top-left logo. */
    public static class BannerConfig {
        public boolean enabled = true;
        /** Asset identifier, filesystem path, or http(s) URL. */
        public String image = "skd_menu:textures/gui/logo_top_left_default.png";
        /** Percentage of screen width (0-100). {@code -1} anchors to the left edge. */
        public float x = 2f;
        /** Percentage of screen height (0-100). {@code -1} = 10px from the top. */
        public float y = 3f;
        public int width = -1;
        public int height = 48;
    }

    public static class BarConfig {
        public boolean enabled = true;
        /** Track (frame) texture: asset identifier, filesystem path, or http(s) URL. */
        public String track = "skd_menu:textures/gui/bar_track.png";
        /** Center X of the bar, percentage of screen width (0-100). */
        public float x = 50f;
        /** Center Y of the bar, percentage of screen height (0-100). */
        public float y = 85f;
        /** Bar width, percentage of screen width (0-100). */
        public float width = 40f;
        /** Bar height, percentage of screen height (0-100). {@code -1} = auto from the track texture's aspect ratio. */
        public float height = -1f;
        public FillConfig fill = new FillConfig();
    }

    public static class FillConfig {
        /** Fill horizontal inset, percentage of bar width (each side). Keeps the fill inside the track groove. */
        public float xInset = 16f;
        /** Fill vertical top inset, percentage of bar height. */
        public float yInset = 39f;
        /** Fill height, percentage of bar height. */
        public float height = 23f;
        /** Start color of the horizontal gradient (hex "#RRGGBB"). */
        public String colorStart = "#7B2CBF";
        /** End color of the horizontal gradient (hex "#RRGGBB"). */
        public String colorEnd = "#FF4D6D";
        /** Draw a soft halo behind the fill. */
        public boolean glow = true;
        /** Glow intensity (0-255). */
        public int glowAlpha = 40;
    }
}
