package com.skd.menu;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Resolves an image reference (mod asset identifier, filesystem path, or http(s) URL)
 * into a registered {@link Identifier} usable with {@code GuiGraphicsExtractor.blit}.
 */
public class TextureResolver {
    private static final Logger LOGGER = SKDMenu.LOGGER;
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build();
    private static final AtomicInteger COUNTER = new AtomicInteger();

    private static final Map<String, Identifier> RESOLVED = new ConcurrentHashMap<>();
    private static final Map<String, int[]> DIMENSIONS = new ConcurrentHashMap<>();
    private static final Map<String, Boolean> DOWNLOADING = new ConcurrentHashMap<>();

    private TextureResolver() {}

    /** Returns the registered texture identifier for {@code ref}, or null if not yet available (e.g. URL still downloading). */
    public static Identifier resolve(String ref) {
        if (ref == null || ref.isBlank()) return null;

        // Called every frame for backgrounds/logos, so a successful resolution short-circuits everything below.
        Identifier cached = RESOLVED.get(ref);
        if (cached != null) return cached;

        if (isHttpUrl(ref)) return resolveHttp(ref);

        // Filesystem path takes priority (matches the pre-existing custom_panorama loader behavior);
        // falls back to a mod asset / resource pack identifier (e.g. "skd_menu:textures/gui/x.png").
        Identifier fromFile = resolveFile(ref);
        if (fromFile != null) return fromFile;

        try {
            Identifier id = Identifier.parse(ref);
            cacheDimensionsFromIdentifier(ref, id);
            RESOLVED.put(ref, id);
            return id;
        } catch (Exception e) {
            return null;
        }
    }

    /** Returns [width, height] for {@code ref}, or null if not yet available. */
    public static int[] dimensions(String ref) {
        if (ref == null || ref.isBlank()) return null;
        resolve(ref);
        return DIMENSIONS.get(ref);
    }

    private static boolean isHttpUrl(String ref) {
        return ref.startsWith("http://") || ref.startsWith("https://");
    }

    private static Identifier resolveHttp(String ref) {
        if (Boolean.TRUE.equals(DOWNLOADING.putIfAbsent(ref, Boolean.TRUE))) return null;

        HTTP_CLIENT.sendAsync(
            HttpRequest.newBuilder(URI.create(ref)).GET().build(),
            HttpResponse.BodyHandlers.ofInputStream()
        ).thenAccept(response -> {
            try (InputStream is = response.body()) {
                if (response.statusCode() / 100 != 2) {
                    LOGGER.warn("Failed to download texture {}: HTTP {}", ref, response.statusCode());
                    DOWNLOADING.remove(ref);
                    return;
                }
                // NativeImage decode is safe off-thread, but registering a GPU texture is not:
                // GL calls must run on the render thread, or they silently fail (wrong GL context).
                NativeImage image = NativeImage.read(is);
                Minecraft.getInstance().execute(() -> {
                    registerImage(ref, image);
                    DOWNLOADING.remove(ref);
                });
            } catch (Exception e) {
                LOGGER.warn("Failed to decode texture from {}", ref, e);
                DOWNLOADING.remove(ref);
            }
        }).exceptionally(e -> {
            LOGGER.warn("Failed to download texture {}", ref, e);
            DOWNLOADING.remove(ref);
            return null;
        });

        return null;
    }

    private static Identifier resolveFile(String ref) {
        Path path;
        try {
            path = Path.of(ref);
        } catch (Exception e) {
            return null; // not valid filesystem path syntax - likely a "modid:path" asset identifier
        }
        if (!Files.exists(path)) return null;
        try (InputStream is = Files.newInputStream(path)) {
            return registerImage(ref, NativeImage.read(is));
        } catch (Exception e) {
            LOGGER.warn("Failed to load texture from {}", ref, e);
            return null;
        }
    }

    private static Identifier registerImage(String ref, NativeImage image) {
        Identifier id = Identifier.fromNamespaceAndPath(SKDMenu.MODID, "dynamic/" + COUNTER.incrementAndGet());
        Minecraft.getInstance().getTextureManager().register(id, new DynamicTexture(() -> ref, image));
        DIMENSIONS.put(ref, new int[]{image.getWidth(), image.getHeight()});
        RESOLVED.put(ref, id);
        return id;
    }

    private static void cacheDimensionsFromIdentifier(String ref, Identifier id) {
        if (DIMENSIONS.containsKey(ref)) return;
        try {
            var resource = Minecraft.getInstance().getResourceManager().getResource(id).orElse(null);
            if (resource == null) return;
            try (InputStream is = resource.open(); NativeImage image = NativeImage.read(is)) {
                DIMENSIONS.put(ref, new int[]{image.getWidth(), image.getHeight()});
            }
        } catch (Exception ignored) {}
    }

    /** Loads a raw {@link NativeImage} from a mod asset identifier or filesystem path (no registration/caching). */
    public static NativeImage loadNativeImage(String ref) {
        try {
            Path fp = Path.of(ref);
            if (Files.exists(fp)) {
                try (InputStream is = Files.newInputStream(fp)) { return NativeImage.read(is); }
            }
        } catch (Exception ignored) {}
        try {
            Identifier loc = Identifier.parse(ref);
            var res = Minecraft.getInstance().getResourceManager().getResource(loc).orElse(null);
            if (res != null) {
                try (InputStream is = res.open()) { return NativeImage.read(is); }
            }
        } catch (Exception ignored) {}
        return null;
    }
}
