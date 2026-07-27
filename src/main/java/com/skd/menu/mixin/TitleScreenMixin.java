package com.skd.menu.mixin;

import com.mojang.blaze3d.platform.NativeImage;
import com.skd.menu.Config;
import com.skd.menu.MenuConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    @Unique private static final Identifier[] PANORAMA_PATHS = new Identifier[]{
        Identifier.parse("textures/gui/title/background/panorama_0.png"),
        Identifier.parse("textures/gui/title/background/panorama_1.png"),
        Identifier.parse("textures/gui/title/background/panorama_2.png"),
        Identifier.parse("textures/gui/title/background/panorama_3.png"),
        Identifier.parse("textures/gui/title/background/panorama_4.png"),
        Identifier.parse("textures/gui/title/background/panorama_5.png")
    };
    @Unique private long animationStartTime = 0;
    @Unique private final Map<AbstractWidget, MenuConfig.Position> animatedWidgets = new HashMap<>();

    @Inject(method = "init()V", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        if (!Config.ENABLE_MOD.getAsBoolean()) return;
        if (Config.RELOAD_JSON_ON_TITLE.getAsBoolean()) MenuConfig.load();

        MenuConfig config = MenuConfig.getInstance();
        TitleScreen screen = (TitleScreen)(Object)this;

        loadCustomPanoramaTextures(config);

        List<AbstractWidget> widgets = new ArrayList<>();
        for (Renderable r : screen.renderables) {
            if (r instanceof AbstractWidget w) {
                widgets.add(w);
            }
        }

        if (!config.buttons.hide.isEmpty()) {
            for (AbstractWidget widget : widgets) {
                Component msg = widget.getMessage();
                if (msg != null) {
                    String text = msg.getString().toLowerCase();
                    for (String hidden : config.buttons.hide) {
                        if (text.contains(hidden.toLowerCase())) {
                            widget.visible = false;
                        }
                    }
                }
            }
        }

        for (Map.Entry<String, MenuConfig.Position> entry : config.buttons.positions.entrySet()) {
            String btnId = entry.getKey();
            MenuConfig.Position pos = entry.getValue();
            if (pos.x == -1 && pos.y == -1) continue;

            String matchKey = switch (btnId) {
                case "singleplayer" -> "singleplayer";
                case "multiplayer" -> "multiplayer";
                case "options" -> "options";
                case "quit" -> "quit";
                case "language" -> "language";
                case "accessibility" -> "accessibility";
                case "mods" -> "mods";
                default -> btnId;
            };

            for (AbstractWidget widget : widgets) {
                Component msg = widget.getMessage();
                if (msg != null && msg.getString().toLowerCase().contains(matchKey)) {
                    widget.setX(pos.x == -1 ? widget.getX() : pos.x);
                    widget.setY(pos.y == -1 ? widget.getY() : pos.y);
                }
            }
        }

        for (MenuConfig.CustomButton customBtn : config.buttons.custom) {
            Button button = Button.builder(
                Component.literal(customBtn.text),
                btn -> handleCustomAction(customBtn)
            ).bounds(customBtn.x, customBtn.y, customBtn.width, customBtn.height).build();
            ((ScreenInvoker)(Object)this).invokeAddRenderableWidget(button);
        }

        setupButtonAnimation(config, widgets);
    }

    @Inject(method = "extractBackground", at = @At("HEAD"))
    private void onExtractBackground(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!Config.ENABLE_MOD.getAsBoolean()) return;
        renderImageOverlays(extractor, MenuConfig.getInstance());
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        if (!Config.ENABLE_MOD.getAsBoolean()) return;
        MenuConfig config = MenuConfig.getInstance();
        if (!"slide_right".equals(config.buttonAnimation.type)) return;
        if (animationStartTime == 0 || animatedWidgets.isEmpty()) return;

        long elapsed = System.currentTimeMillis() - animationStartTime;
        float progress = Math.min(1.0f, (float)elapsed / config.buttonAnimation.duration);
        float eased = 1.0f - (1.0f - progress) * (1.0f - progress);

        for (Map.Entry<AbstractWidget, MenuConfig.Position> e : animatedWidgets.entrySet()) {
            AbstractWidget w = e.getKey();
            MenuConfig.Position target = e.getValue();
            w.setX((int)(target.x - config.buttonAnimation.offset * (1.0f - eased)));
        }

        if (progress >= 1.0f) {
            for (Map.Entry<AbstractWidget, MenuConfig.Position> e : animatedWidgets.entrySet()) {
                AbstractWidget w = e.getKey();
                MenuConfig.Position target = e.getValue();
                w.setX(target.x);
            }
            animatedWidgets.clear();
            animationStartTime = 0;
        }
    }

    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    private void onExtractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!Config.ENABLE_MOD.getAsBoolean()) return;

        MenuConfig config = MenuConfig.getInstance();
        String type = config.background.type;

        if ("custom_panorama".equals(type)) {
            return;
        }

        if (!"panorama".equals(type)) {
            renderCustomBackground(extractor, config);
            renderTitleWidgets(extractor, mouseX, mouseY, partialTick);
            ci.cancel();
        }
    }

    @Unique
    private void loadCustomPanoramaTextures(MenuConfig config) {
        if (!"custom_panorama".equals(config.background.type)) return;
        List<String> files = config.background.panorama.files;
        if (files == null || files.size() < 6) return;

        Minecraft mc = Minecraft.getInstance();
        for (int i = 0; i < 6; i++) {
            String path = files.get(i);
            if (path == null || path.isEmpty()) continue;
            try {
                NativeImage image = loadNativeImage(path);
                if (image != null) {
                    String debugName = "skd_menu_panorama_" + i;
                    mc.getTextureManager().register(PANORAMA_PATHS[i], new DynamicTexture(() -> debugName, image));
                }
            } catch (Exception ignored) {}
        }
    }

    @Unique
    private NativeImage loadNativeImage(String path) {
        try {
            Path filePath = Path.of(path);
            if (Files.exists(filePath)) {
                try (InputStream is = Files.newInputStream(filePath)) {
                    return NativeImage.read(is);
                }
            }
        } catch (Exception ignored) {}
        try {
            Identifier loc = Identifier.parse(path);
            var resource = Minecraft.getInstance().getResourceManager().getResource(loc).orElse(null);
            if (resource != null) {
                try (InputStream is = resource.open()) {
                    return NativeImage.read(is);
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    @Unique
    private void setupButtonAnimation(MenuConfig config, List<AbstractWidget> widgets) {
        String animType = config.buttonAnimation.type;
        if (animType == null || "none".equals(animType)) return;

        animationStartTime = System.currentTimeMillis();
        animatedWidgets.clear();

        for (AbstractWidget w : widgets) {
            if (!w.visible) continue;
            animatedWidgets.put(w, new MenuConfig.Position(w.getX(), w.getY()));
            if ("slide_right".equals(animType)) {
                w.setX(w.getX() - config.buttonAnimation.offset);
            }
        }
    }

    @Unique
    private void renderCustomBackground(GuiGraphicsExtractor extractor, MenuConfig config) {
        TitleScreen screen = (TitleScreen)(Object)this;
        switch (config.background.type) {
            case "image" -> renderImageBackground(extractor, screen, config.background.image.path);
            case "color" -> extractor.fill(0, 0, screen.width, screen.height, config.background.color.color);
            case "animated" -> renderAnimatedBackground(extractor, screen, config);
        }
    }

    @Unique
    private void renderImageBackground(GuiGraphicsExtractor extractor, TitleScreen screen, String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            renderFallback(extractor, screen);
            return;
        }
        try {
            Identifier loc = Identifier.parse(imagePath);
            extractor.blit(loc, 0, 0, 0, 0, screen.width, screen.height, screen.width, screen.height);
        } catch (Exception e) {
            renderFallback(extractor, screen);
        }
    }

    @Unique
    private void renderAnimatedBackground(GuiGraphicsExtractor extractor, TitleScreen screen, MenuConfig config) {
        List<String> frames = config.background.animation.frames;
        if (frames == null || frames.isEmpty()) {
            renderFallback(extractor, screen);
            return;
        }
        int frameIndex = (int)((System.currentTimeMillis() / config.background.animation.frameTimeMs) % frames.size());
        if (!config.background.animation.loop && frameIndex >= frames.size()) {
            frameIndex = frames.size() - 1;
        }
        String framePath = frames.get(Math.min(frameIndex, Math.max(0, frames.size() - 1)));
        renderImageBackground(extractor, screen, framePath);
    }

    @Unique
    private void renderFallback(GuiGraphicsExtractor extractor, TitleScreen screen) {
        extractor.fill(0, 0, screen.width, screen.height, 0xFF000000);
    }

    @Unique
    private void renderTitleWidgets(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
        TitleScreen screen = (TitleScreen)(Object)this;
        for (Renderable renderable : screen.renderables) {
            renderable.extractRenderState(extractor, mouseX, mouseY, partialTick);
        }
    }

    @Unique
    private void renderImageOverlays(GuiGraphicsExtractor extractor, MenuConfig config) {
        for (MenuConfig.ImageOverlay overlay : config.images) {
            if (overlay.path == null || overlay.path.isEmpty()) continue;
            try {
                Identifier loc = Identifier.parse(overlay.path);
                int w = overlay.width > 0 ? overlay.width : 256;
                int h = overlay.height > 0 ? overlay.height : 256;
                extractor.blit(loc, overlay.x, overlay.y, 0, 0, w, h, w, h);
            } catch (Exception ignored) {}
        }
    }

    @Unique
    private void handleCustomAction(MenuConfig.CustomButton btn) {
        if (btn.action == null || btn.action.isEmpty()) return;
        Minecraft mc = Minecraft.getInstance();
        TitleScreen screen = (TitleScreen)(Object)this;
        switch (btn.action) {
            case "command" -> {
                if (mc.player != null && btn.command != null && !btn.command.isEmpty()) {
                    mc.player.connection.sendCommand(btn.command);
                }
            }
            case "open_singleplayer" -> mc.setScreenAndShow(new SelectWorldScreen(screen));
            case "open_multiplayer" -> mc.setScreenAndShow(new JoinMultiplayerScreen(screen));
            case "open_options" -> mc.setScreenAndShow(new OptionsScreen(screen, mc.options, false));
            case "quit" -> mc.stop();
            case "open_url" -> {
                if (btn.command != null && !btn.command.isEmpty()) {
                    try {
                        java.awt.Desktop.getDesktop().browse(new java.net.URI(btn.command));
                    } catch (Exception ignored) {}
                }
            }
        }
    }
}
