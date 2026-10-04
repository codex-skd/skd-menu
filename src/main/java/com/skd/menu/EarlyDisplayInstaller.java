package com.skd.menu;

import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Installs the bundled early-display theme (NeoForge "early window" shown while mods load) into the
 * game config, so the loading screen is branded from the very first phase without manual setup.
 *
 * <p>Runs after the early window has already decided its theme for the current boot, so changes take
 * effect from the next launch (that is inherent to the early display, which runs before any mod).
 */
public final class EarlyDisplayInstaller {
    private static final Logger LOGGER = SkdMenu.LOGGER;
    private static final String[] THEME_FILES = {
        "theme-skd.json",
        "progress_bar_bg.png",
        "progress_bar_fg.png",
        "bg_main_default.png",
        "logo_top_left_default.png"
    };

    private EarlyDisplayInstaller() {}

    public static void install() {
        try {
            Path fmlDir = Path.of("config", "fml");
            Files.createDirectories(fmlDir);
            for (String file : THEME_FILES) {
                copyBundled(file, fmlDir.resolve(file));
            }
            configureFmlToml(Path.of("config", "fml.toml"));
            LOGGER.info("SKD Menu early display theme installed to {}", fmlDir);
        } catch (Exception e) {
            LOGGER.warn("Failed to install SKD Menu early display theme", e);
        }
    }

    private static void copyBundled(String name, Path target) throws IOException {
        try (InputStream in = EarlyDisplayInstaller.class.getResourceAsStream("/earlydisplay/" + name)) {
            if (in == null) {
                LOGGER.warn("Bundled early display file missing: earlydisplay/{}", name);
                return;
            }
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /** Sets the early-window keys in {@code config/fml.toml} (theme id + 16:9 window so the background fills the screen). */
    private static void configureFmlToml(Path fmlToml) throws IOException {
        Map<String, String> wanted = new LinkedHashMap<>();
        wanted.put("earlyWindowControl", "true");
        wanted.put("earlyLoadingScreenTheme", "\"skd\"");
        wanted.put("earlyWindowWidth", "1280");
        wanted.put("earlyWindowHeight", "720");

        List<String> lines = Files.exists(fmlToml) ? Files.readAllLines(fmlToml, StandardCharsets.UTF_8) : new ArrayList<>();
        List<String> out = new ArrayList<>(lines);
        for (Map.Entry<String, String> entry : wanted.entrySet()) {
            String key = entry.getKey();
            boolean found = false;
            for (int i = 0; i < out.size(); i++) {
                String line = out.get(i);
                if (line.matches("\\s*" + key + "\\s*=.*")) {
                    out.set(i, key + " = " + entry.getValue());
                    found = true;
                    break;
                }
            }
            if (!found) {
                out.add(key + " = " + entry.getValue());
            }
        }
        Files.write(fmlToml, out, StandardCharsets.UTF_8);
    }
}
