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
import java.util.*;
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
    @Unique private int customButtonYStart = 0;

    @Inject(method = "init()V", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        if (!Config.ENABLE_MOD.getAsBoolean()) return;
        if (Config.RELOAD_JSON_ON_TITLE.getAsBoolean()) MenuConfig.load();

        MenuConfig config = MenuConfig.getInstance();
        TitleScreen screen = (TitleScreen)(Object)this;
        int width = screen.width;
        int height = screen.height;

        loadCustomPanoramaTextures(config);

        List<AbstractWidget> widgets = new ArrayList<>();
        for (Renderable r : screen.renderables) {
            if (r instanceof AbstractWidget w) widgets.add(w);
        }

        processDefaultButtons(config, widgets, width, height);
        addCustomButtons(config, widgets, width, height);
        setupButtonAnimation(config, widgets);
    }

    @Unique
    private void processDefaultButtons(MenuConfig config, List<AbstractWidget> widgets, int width, int height) {
        List<MenuConfig.DefaultButton> defaults = config.buttons.defaults;
        if (defaults == null || defaults.isEmpty()) return;

        Map<String, MenuConfig.DefaultButton> btnMap = new HashMap<>();
        for (MenuConfig.DefaultButton db : defaults) {
            btnMap.put(db.id.toLowerCase(), db);
        }

        String[] order = {"singleplayer", "multiplayer", "options", "quit", "language", "accessibility", "mods"};
        int centerX = width / 2;
        int centerY = height / 4;
        int baseY = centerY + 48;
        int gap = 24;
        int idx = 0;

        for (String id : order) {
            MenuConfig.DefaultButton cfg = btnMap.get(id);
            for (AbstractWidget w : widgets) {
                Component msg = w.getMessage();
                if (msg == null || !msg.getString().toLowerCase().contains(id)) continue;

                if (cfg != null && cfg.hide) {
                    w.visible = false;
                    continue;
                }
                int targetX = (cfg != null && cfg.x != -1) ? cfg.x : centerX - 100;
                int targetY = (cfg != null && cfg.y != -1) ? cfg.y : baseY + gap * idx;
                w.setX(targetX);
                w.setY(targetY);
                w.visible = true;
                idx++;
            }
        }
        customButtonYStart = baseY + gap * idx;
    }

    @Unique
    private void addCustomButtons(MenuConfig config, List<AbstractWidget> widgets, int width, int height) {
        int yOffset = customButtonYStart > 0 ? customButtonYStart : height / 4 + 144;
        int idx = 0;

        for (MenuConfig.CustomButton cb : config.buttons.custom) {
            int bx = cb.x;
            int by = cb.y;
            if (bx == -999) bx = width / 2 - cb.width / 2;
            if (by == -999) { by = yOffset + idx * (cb.height + 4); idx++; }

            Button button = Button.builder(
                Component.literal(cb.text),
                btn -> handleCustomAction(cb)
            ).bounds(bx, by, cb.width, cb.height).build();
            ((ScreenInvoker)(Object)this).invokeAddRenderableWidget(button);
        }
    }

    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    private void onExtractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!Config.ENABLE_MOD.getAsBoolean()) return;

        MenuConfig config = MenuConfig.getInstance();
        String titleType = config.title != null ? config.title.type : "vanilla";
        String bgType = config.background.type;

        if ("vanilla".equals(titleType) && "panorama".equals(bgType)) return;
        if ("vanilla".equals(titleType) && "custom_panorama".equals(bgType)) return;
        if (!"vanilla".equals(titleType) && ("panorama".equals(bgType) || "custom_panorama".equals(bgType))) {
            renderCustomTitle(extractor, config);
            return;
        }

        renderCustomBackground(extractor, config);
        renderCustomTitle(extractor, config);
        renderTitleWidgets(extractor, mouseX, mouseY, partialTick);
        renderImageOverlays(extractor, config);
        ci.cancel();
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
            e.getKey().setX((int)(e.getValue().x - config.buttonAnimation.offset * (1.0f - eased)));
        }

        if (progress >= 1.0f) {
            for (Map.Entry<AbstractWidget, MenuConfig.Position> e : animatedWidgets.entrySet())
                e.getKey().setX(e.getValue().x);
            animatedWidgets.clear();
            animationStartTime = 0;
        }
    }

    @Unique
    private void renderCustomTitle(GuiGraphicsExtractor extractor, MenuConfig config) {
        if (config.title == null || "hidden".equals(config.title.type)) return;
        TitleScreen screen = (TitleScreen)(Object)this;

        if ("image".equals(config.title.type)) {
            try {
                Identifier loc = Identifier.parse(config.title.image);
                float s = config.title.scale;
                if (config.title.animated) {
                    float phase = (float)(System.currentTimeMillis() / 1000.0 * config.title.animSpeed);
                    s *= 1.0f + (float)Math.sin(phase) * config.title.animRange;
                }
                int tw = 310;
                int th = 44;
                int tx = config.title.x;
                int ty = config.title.y;
                int sw = (int)(tw * s);
                int sh = (int)(th * s);
                int cx = tx + sw / 2;
                int cy = ty + sh / 2;
                extractor.blit(loc, tx, ty, tx + sw, ty + sh, 0.0F, 1.0F, 0.0F, 1.0F);
            } catch (Exception ignored) {}
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
            int idx = i;
            try {
                NativeImage image = loadNativeImage(path);
                if (image != null)
                    mc.getTextureManager().register(PANORAMA_PATHS[idx], new DynamicTexture(() -> "skd_pano_" + idx, image));
            } catch (Exception ignored) {}
        }
    }

    @Unique
    private NativeImage loadNativeImage(String path) {
        try {
            Path fp = Path.of(path);
            if (Files.exists(fp)) {
                try (InputStream is = Files.newInputStream(fp)) { return NativeImage.read(is); }
            }
        } catch (Exception ignored) {}
        try {
            Identifier loc = Identifier.parse(path);
            var res = Minecraft.getInstance().getResourceManager().getResource(loc).orElse(null);
            if (res != null) {
                try (InputStream is = res.open()) { return NativeImage.read(is); }
            }
        } catch (Exception ignored) {}
        return null;
    }

    @Unique
    private void setupButtonAnimation(MenuConfig config, List<AbstractWidget> widgets) {
        String t = config.buttonAnimation.type;
        if (t == null || "none".equals(t)) return;
        animationStartTime = System.currentTimeMillis();
        animatedWidgets.clear();
        for (AbstractWidget w : widgets) {
            if (!w.visible) continue;
            animatedWidgets.put(w, new MenuConfig.Position(w.getX(), w.getY()));
            if ("slide_right".equals(t)) w.setX(w.getX() - config.buttonAnimation.offset);
        }
    }

    @Unique
    private void renderCustomBackground(GuiGraphicsExtractor extractor, MenuConfig config) {
        TitleScreen s = (TitleScreen)(Object)this;
        switch (config.background.type) {
            case "image" -> renderImageBackground(extractor, s, config.background.image.path);
            case "color" -> extractor.fill(0, 0, s.width, s.height, config.background.color.color);
            case "animated" -> renderAnimatedBackground(extractor, s, config);
        }
    }

    @Unique
    private void renderImageBackground(GuiGraphicsExtractor e, TitleScreen s, String p) {
        if (p == null || p.isEmpty()) { renderFallback(e, s); return; }
        try { e.blit(Identifier.parse(p), 0, 0, s.width, s.height, 0.0F, 1.0F, 0.0F, 1.0F); }
        catch (Exception ex) { renderFallback(e, s); }
    }

    @Unique
    private void renderAnimatedBackground(GuiGraphicsExtractor e, TitleScreen s, MenuConfig c) {
        List<String> fr = c.background.animation.frames;
        if (fr == null || fr.isEmpty()) { renderFallback(e, s); return; }
        int fi = (int)((System.currentTimeMillis() / c.background.animation.frameTimeMs) % fr.size());
        if (!c.background.animation.loop && fi >= fr.size()) fi = fr.size() - 1;
        renderImageBackground(e, s, fr.get(Math.min(fi, Math.max(0, fr.size() - 1))));
    }

    @Unique
    private void renderFallback(GuiGraphicsExtractor e, TitleScreen s) {
        e.fill(0, 0, s.width, s.height, 0xFF000000);
    }

    @Unique
    private void renderTitleWidgets(GuiGraphicsExtractor e, int mx, int my, float pt) {
        for (Renderable r : ((TitleScreen)(Object)this).renderables)
            r.extractRenderState(e, mx, my, pt);
    }

    @Unique
    private void renderImageOverlays(GuiGraphicsExtractor e, MenuConfig c) {
        for (MenuConfig.ImageOverlay o : c.images) {
            if (o.path == null || o.path.isEmpty()) continue;
            Identifier loc;
            try { loc = Identifier.parse(o.path); } catch (Exception ex) { continue; }
            int w = o.width > 0 ? o.width : 64;
            int h = o.height > 0 ? o.height : 64;
            int x = o.x;
            int y = o.y;
            if (x == -1) x = ((TitleScreen)(Object)this).width - w - 10;
            if (y == -1) y = 10;
            e.blit(loc, x, y, x + w, y + h, 0.0F, 1.0F, 0.0F, 1.0F);
        }
    }

    @Unique
    private void handleCustomAction(MenuConfig.CustomButton btn) {
        if (btn.action == null || btn.action.isEmpty()) return;
        Minecraft mc = Minecraft.getInstance();
        TitleScreen screen = (TitleScreen)(Object)this;
        switch (btn.action) {
            case "command" -> {
                if (mc.player != null && btn.command != null && !btn.command.isEmpty())
                    mc.player.connection.sendCommand(btn.command);
            }
            case "open_singleplayer" -> mc.setScreenAndShow(new SelectWorldScreen(screen));
            case "open_multiplayer" -> mc.setScreenAndShow(new JoinMultiplayerScreen(screen));
            case "open_options" -> mc.setScreenAndShow(new OptionsScreen(screen, mc.options, false));
            case "quit" -> mc.stop();
            case "open_url" -> {
                if (btn.command != null && !btn.command.isEmpty()) {
                    try { java.awt.Desktop.getDesktop().browse(new java.net.URI(btn.command)); }
                    catch (Exception ignored) {}
                }
            }
        }
    }
}
