package com.skd.menu;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class MenuConfig {
    private static final Logger LOGGER = SKDMenu.LOGGER;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static MenuConfig instance;
    private static Path configPath;

    public BackgroundConfig background = new BackgroundConfig();
    public TitleConfig title = new TitleConfig();
    public LogosConfig logos = new LogosConfig();
    public ButtonsConfig buttons = new ButtonsConfig();
    public List<ImageOverlay> images = new ArrayList<>();
    public ButtonAnimation buttonAnimation = new ButtonAnimation();

    public static MenuConfig getInstance() {
        if (instance == null) load();
        return instance;
    }

    public static void load() {
        configPath = Path.of("config", "skd_menu", "menu.json");
        if (Files.exists(configPath)) {
            try (Reader reader = Files.newBufferedReader(configPath)) {
                instance = GSON.fromJson(reader, MenuConfig.class);
                LOGGER.info("Loaded menu config from {}", configPath);
            } catch (Exception e) {
                LOGGER.error("Failed to load menu config, using defaults", e);
                instance = new MenuConfig();
            }
        } else {
            instance = new MenuConfig();
            save();
        }
        if (instance == null) instance = new MenuConfig();
        initDefaults();
    }

    private static void initDefaults() {
        if (instance.background == null) instance.background = new BackgroundConfig();
        if (instance.background.animation == null) instance.background.animation = new AnimationConfig();
        if (instance.background.image == null) instance.background.image = new ImageConfig();
        if (instance.background.color == null) instance.background.color = new ColorConfig();
        if (instance.background.panorama == null) instance.background.panorama = new PanoramaConfig();
        if (instance.background.panorama.files == null) instance.background.panorama.files = new ArrayList<>();
        if (instance.background.image.effect == null) instance.background.image.effect = new EffectConfig();
        if (instance.title == null) instance.title = new TitleConfig();
        if (instance.logos == null) instance.logos = new LogosConfig();
        if (instance.logos.topLeft == null) instance.logos.topLeft = new LogosConfig().topLeft;
        if (instance.logos.topRight == null) instance.logos.topRight = new LogosConfig().topRight;
        if (instance.buttons == null) instance.buttons = new ButtonsConfig();
        if (instance.buttons.defaults == null) instance.buttons.defaults = new ArrayList<>();
        if (instance.buttons.custom == null) instance.buttons.custom = new ArrayList<>();
        if (instance.images == null) instance.images = new ArrayList<>();
        if (instance.buttonAnimation == null) instance.buttonAnimation = new ButtonAnimation();
    }

    public static void save() {
        if (configPath == null) configPath = Path.of("config", "skd_menu", "menu.json");
        try {
            Files.createDirectories(configPath.getParent());
            try (Writer writer = Files.newBufferedWriter(configPath)) {
                GSON.toJson(instance, writer);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to save menu config", e);
        }
    }

    public static class BackgroundConfig {
        public String type = "image";
        public ImageConfig image = new ImageConfig();
        public ColorConfig color = new ColorConfig();
        public AnimationConfig animation = new AnimationConfig();
        public PanoramaConfig panorama = new PanoramaConfig();
    }

    public static class ImageConfig {
        public String path = "skd_menu:textures/gui/bg_main_default.png";
        public String fit = "cover";
        public EffectConfig effect = new EffectConfig();
    }

    public static class EffectConfig {
        public String type = "zoom";
        public int durationMs = 20000;
        public float minScale = 1.0f;
        public float maxScale = 1.15f;
    }

    public static class ColorConfig {
        public int color = 0xFF000000;
    }

    public static class AnimationConfig {
        public List<String> frames = new ArrayList<>(List.of(
            "skd_menu:textures/gui/bg_default_1.png",
            "skd_menu:textures/gui/bg_default_2.png",
            "skd_menu:textures/gui/bg_default_3.png"
        ));
        public int frameTimeMs = 8000;
        public boolean loop = true;
    }

    public static class PanoramaConfig {
        public List<String> files = new ArrayList<>();
    }

    public static class LogosConfig {
        public LogoConfig topLeft = new LogoConfig("skd_menu:textures/gui/logo_top_left_default.png", 2f);
        public LogoConfig topRight = new LogoConfig("skd_menu:textures/gui/logo_top_right_default.png", -1f);
    }

    public static class LogoConfig {
        public boolean enabled = true;
        public String image = "";
        /** Percentage of screen width/height (0-100), not pixels. {@code -1} anchors to the edge (left for topLeft, right for topRight). */
        public float x = 2f;
        public float y = 3f;
        public int width = -1;
        public int height = 48;

        public LogoConfig() {}
        public LogoConfig(String defaultImage, float defaultX) { this.image = defaultImage; this.x = defaultX; }
    }

    public static class TitleConfig {
        public String type = "hidden";
        public String image = "skd_menu:textures/gui/title_default.png";
        public boolean visible = true;
        /** Percentage of screen width/height (0-100), not pixels. */
        public float x = 3f;
        public float y = 3f;
        public float scale = 1.0f;
        public int color = 0xFFFFFFFF;
        public boolean animated = false;
        public float animRange = 0.05f;
        public float animSpeed = 1.5f;
    }

    public static class ButtonsConfig {
        public List<DefaultButton> defaults = defaultButtonList();
        public List<CustomButton> custom = new ArrayList<>();
        public String globalImage = "skd_menu:textures/gui/button_default.png";
    }

    /** {id, x, y, width, hide, image} — default layout. -1 = automatic, matches DefaultButton sentinels. */
    private static final Object[][] DEFAULT_BUTTON_LAYOUT = {
        {"singleplayer",  5f,   82f,   75,  false, ""},
        {"multiplayer",   10f,  90f,   75,  false, ""},
        {"realms",        -1f,  -1f,   -1,  true,  ""},
        {"options",       90f,  81f,   75,  false, ""},
        {"quit",          80f,  16f,   92,  false, ""},
        {"friends",       90f,  66f,   75,  false, ""},
        {"language",      -1f,  -1f,   -1,  true,  ""},
        {"accessibility", -1f,  -1f,   -1,  true,  ""},
        {"mods",          90f,  73.5f, 75,  false, ""},
    };

    private static List<DefaultButton> defaultButtonList() {
        List<DefaultButton> list = new ArrayList<>();
        for (Object[] row : DEFAULT_BUTTON_LAYOUT) {
            DefaultButton db = new DefaultButton();
            db.id = (String) row[0];
            db.x = (Float) row[1];
            db.y = (Float) row[2];
            db.width = (Integer) row[3];
            db.hide = (Boolean) row[4];
            db.image = (String) row[5];
            list.add(db);
        }
        return list;
    }

    public static class DefaultButton {
        public String id = "";
        /** Percentage of screen width/height (0-100), not pixels. {@code -1} = automatic (vanilla-like layout). */
        public float x = -1;
        public float y = -1;
        /** Button width in pixels. {@code -1} = automatic default width for this button's row. */
        public int width = -1;
        public boolean hide = false;
        public String image = "";
        /** Hides the button's label text, showing only the image (own or {@code buttons.globalImage}). */
        public boolean hideText = false;
    }

    public static class Position {
        public int x = -1;
        public int y = -1;
        public Position() {}
        public Position(int x, int y) { this.x = x; this.y = y; }
    }

    public static class CustomButton {
        public String text = "";
        public String action = "";
        public String command = "";
        /** Percentage of screen width/height (0-100), not pixels. {@code -999} (either one) = not defined -> hidden. */
        public float x = -999;
        public float y = -999;
        public int width = 200;
        public int height = 20;
        public String image = "";
        public boolean hideText = false;
    }

    public static class ImageOverlay {
        public String path = "";
        public boolean enabled = false;
        /** Percentage of screen width/height (0-100), not pixels. {@code -1} = automatic (top-right corner). */
        public float x = -1;
        public float y = -1;
        public int width = 0;
        public int height = 0;
        public float scale = 1.0f;
    }

    public static class ButtonAnimation {
        public String type = "slide_right";
        public int duration = 600;
        public int offset = 300;
    }
}
