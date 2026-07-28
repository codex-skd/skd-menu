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
        if (instance.title == null) instance.title = new TitleConfig();
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
        public String type = "animated";
        public ImageConfig image = new ImageConfig();
        public ColorConfig color = new ColorConfig();
        public AnimationConfig animation = new AnimationConfig();
        public PanoramaConfig panorama = new PanoramaConfig();
    }

    public static class ImageConfig {
        public String path = "skd_menu:textures/gui/bg_default_1.png";
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

    public static class TitleConfig {
        public String type = "image";
        public String image = "skd_menu:textures/gui/title_default.png";
        public boolean visible = true;
        public int x = 10;
        public int y = 10;
        public float scale = 1.0f;
        public int color = 0xFFFFFFFF;
    }

    public static class ButtonsConfig {
        public List<DefaultButton> defaults = new ArrayList<>();
        public List<CustomButton> custom = new ArrayList<>();
    }

    public static class DefaultButton {
        public String id = "";
        public int x = -1;
        public int y = -1;
        public boolean hide = false;
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
        public int x = -999;
        public int y = -999;
        public int width = 200;
        public int height = 20;
    }

    public static class ImageOverlay {
        public String path = "";
        public boolean enabled = false;
        public int x = -1;
        public int y = -1;
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
