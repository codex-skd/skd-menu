package com.skd.menu.mixin;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.realmsclient.RealmsMainScreen;
import com.skd.menu.Config;
import com.skd.menu.MenuConfig;
import com.skd.menu.TextureResolver;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.SafetyScreen;
import net.minecraft.client.gui.screens.options.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screens.options.LanguageSelectScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.gui.ModListScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    @Unique private static final ResourceLocation[] PANORAMA_PATHS = new ResourceLocation[]{
        ResourceLocation.parse("textures/gui/title/background/panorama_0.png"),
        ResourceLocation.parse("textures/gui/title/background/panorama_1.png"),
        ResourceLocation.parse("textures/gui/title/background/panorama_2.png"),
        ResourceLocation.parse("textures/gui/title/background/panorama_3.png"),
        ResourceLocation.parse("textures/gui/title/background/panorama_4.png"),
        ResourceLocation.parse("textures/gui/title/background/panorama_5.png")
    };
    @Unique private long animationStartTime = 0;
    @Unique private Map<AbstractWidget, MenuConfig.Position> animatedWidgets;
    @Unique private static final String[] MAIN_ROW_IDS = {"singleplayer", "multiplayer", "realms"};
    @Unique private static final String[] ICON_ROW_IDS = {"language", "accessibility", "mods"};
    @Unique private static final String[] BOTTOM_ROW_IDS = {"options", "quit"};
    /**
     * Minimum allowed button width in pixels. A narrower button forces vanilla's label to switch to
     * horizontal auto-scrolling, whose clipping scissor rectangle can end up out of the render area
     * (especially combined with the slide-in button animation) and crash the game with an
     * IllegalArgumentException in GuiRenderer.enableScissor.
     */
    @Unique private static final int MIN_BUTTON_WIDTH = 20;

    @Inject(method = "init()V", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        if (!Config.ENABLE_MOD.getAsBoolean()) return;
        if (Config.RELOAD_JSON_ON_TITLE.getAsBoolean()) MenuConfig.load();

        MenuConfig config = MenuConfig.getInstance();
        TitleScreen screen = (TitleScreen)(Object)this;
        int width = screen.width;
        int height = screen.height;

        loadCustomPanoramaTextures(config);
        animatedWidgets = new HashMap<>();

        List<AbstractWidget> vanillaWidgets = new ArrayList<>();
        for (Renderable r : screen.renderables) {
            if (r instanceof AbstractWidget w) vanillaWidgets.add(w);
        }
        hideVanillaWidgets(vanillaWidgets);

        createDefaultButtons(config, screen, width, height);
        addCustomButtons(config, width, height);

        List<AbstractWidget> finalWidgets = new ArrayList<>();
        for (Renderable r : screen.renderables) {
            if (r instanceof AbstractWidget w) finalWidgets.add(w);
        }
        setupButtonAnimation(config, finalWidgets);
    }

    /**
     * Hides every vanilla menu button (Singleplayer/Multiplayer/Realms/Options/Quit as {@link Button},
     * Mods/Language/Accessibility as {@link SpriteIconButton}) unconditionally, by class rather
     * than by matching their (locale-dependent) rendered text. {@link #createDefaultButtons} then adds
     * our own equivalents built from Minecraft's translation keys, which works in any language.
     */
    @Unique
    private void hideVanillaWidgets(List<AbstractWidget> widgets) {
        for (AbstractWidget w : widgets) {
            if (w instanceof Button || w instanceof SpriteIconButton) w.visible = false;
        }
    }

    @Unique
    private void createDefaultButtons(MenuConfig config, TitleScreen screen, int width, int height) {
        List<MenuConfig.DefaultButton> defaults = config.buttons.defaults;
        if (defaults == null || defaults.isEmpty()) return;

        Map<String, MenuConfig.DefaultButton> btnMap = new HashMap<>();
        for (MenuConfig.DefaultButton db : defaults) {
            btnMap.put(db.id.toLowerCase(), db);
        }

        Minecraft mc = Minecraft.getInstance();
        int centerX = width / 2;
        int centerY = height / 4;
        int baseY = centerY + 48;
        int gap = 24;
        int idx = 0;

        for (String id : MAIN_ROW_IDS) {
            idx = addDefaultButton(config, btnMap, screen, mc, id, 200, width, height, centerX, baseY, gap, idx);
        }
        idx = addIconRowButtons(config, btnMap, screen, mc, width, height, centerX, baseY, gap, idx);
        for (String id : BOTTOM_ROW_IDS) {
            idx = addDefaultButton(config, btnMap, screen, mc, id, 98, width, height, centerX, baseY, gap, idx);
        }
    }

    /** Converts a percentage (0-100) of a screen dimension into pixels, so fixed positions stay proportional on resize. */
    @Unique
    private static int percentToPixels(float percent, int total) {
        return Math.round(percent / 100f * total);
    }

    /** Keeps a widget's resting position fully on-screen, in case an out-of-[0,100] percentage was configured. */
    @Unique
    private static int clampOnScreen(int pos, int size, int screenTotal) {
        return Math.max(0, Math.min(pos, Math.max(0, screenTotal - size)));
    }

    /**
     * Stretches the texture sub-region [u0..u1]x[v0..v1] (normalized) into the screen rect
     * [x0..x1]x[y0..y1]. 1.21.1 {@link GuiGraphics} exposes no stretching blit publicly, so
     * scale the pose and draw the region at its native pixel size.
     */
    @Unique
    private static void blitStretch(GuiGraphics g, ResourceLocation tex,
            int x0, int y0, int x1, int y1,
            float u0, float u1, float v0, float v1, int texW, int texH) {
        float regionW = Math.max(1.0F, (u1 - u0) * texW);
        float regionH = Math.max(1.0F, (v1 - v0) * texH);
        int destW = x1 - x0;
        int destH = y1 - y0;
        if (destW <= 0 || destH <= 0) return;
        g.pose().pushPose();
        g.pose().translate(x0, y0, 0.0F);
        g.pose().scale(destW / regionW, destH / regionH, 1.0F);
        g.blit(tex, 0, 0, u0 * texW, v0 * texH, Math.round(regionW), Math.round(regionH), texW, texH);
        g.pose().popPose();
    }

    @Unique
    private int addDefaultButton(MenuConfig config, Map<String, MenuConfig.DefaultButton> btnMap, TitleScreen screen, Minecraft mc,
                                  String id, int defaultWidth, int screenWidth, int screenHeight, int centerX, int baseY, int gap, int idx) {
        MenuConfig.DefaultButton cfg = btnMap.get(id);
        if (cfg != null && cfg.hide) return idx;

        int w = (cfg != null && cfg.width != -1) ? cfg.width : defaultWidth;
        int bx = (cfg != null && cfg.x != -1) ? percentToPixels(cfg.x, screenWidth) : centerX - w / 2;
        int by = (cfg != null && cfg.y != -1) ? percentToPixels(cfg.y, screenHeight) : baseY + gap * idx;
        addBuiltButton(config, screen, mc, id, cfg, bx, by, w);
        return idx + 1;
    }

    /** Lays out Friends/Language/Accessibility/Mods as a single horizontal row, centered, reserving one row slot. */
    @Unique
    private int addIconRowButtons(MenuConfig config, Map<String, MenuConfig.DefaultButton> btnMap, TitleScreen screen, Minecraft mc,
                                   int screenWidth, int screenHeight, int centerX, int baseY, int gap, int idx) {
        List<String> autoIds = new ArrayList<>();
        List<Integer> autoWidths = new ArrayList<>();

        for (String id : ICON_ROW_IDS) {
            MenuConfig.DefaultButton cfg = btnMap.get(id);
            if (cfg != null && cfg.hide) continue;

            int w = (cfg != null && cfg.width != -1) ? cfg.width : mc.font.width(Component.translatable(vanillaKey(id))) + 16;
            if (cfg != null && cfg.x != -1 && cfg.y != -1) {
                addBuiltButton(config, screen, mc, id, cfg, percentToPixels(cfg.x, screenWidth), percentToPixels(cfg.y, screenHeight), w);
                continue;
            }
            autoIds.add(id);
            autoWidths.add(w);
        }
        if (autoIds.isEmpty()) return idx;

        int spacing = 4;
        int totalWidth = autoWidths.stream().mapToInt(Integer::intValue).sum() + spacing * (autoIds.size() - 1);
        int x = centerX - totalWidth / 2;
        int y = baseY + gap * idx;
        for (int i = 0; i < autoIds.size(); i++) {
            String id = autoIds.get(i);
            int w = autoWidths.get(i);
            addBuiltButton(config, screen, mc, id, btnMap.get(id), x, y, w);
            x += w + spacing;
        }
        return idx + 1;
    }

    @Unique
    private void addBuiltButton(MenuConfig config, TitleScreen screen, Minecraft mc, String id, MenuConfig.DefaultButton cfg,
                                 int x, int y, int width) {
        width = Math.max(width, MIN_BUTTON_WIDTH);
        x = clampOnScreen(x, width, screen.width);
        y = clampOnScreen(y, 20, screen.height);

        Component text = Component.translatable(vanillaKey(id));
        String image = resolveButtonImage(cfg != null ? cfg.image : null, config);
        boolean hideText = cfg != null && cfg.hideText;
        Button button;
        if (image != null) {
            button = new ImageButton(text, btn -> runDefaultAction(mc, screen, id), x, y, width, 20, image, hideText);
        } else {
            button = Button.builder(text, btn -> runDefaultAction(mc, screen, id)).bounds(x, y, width, 20).build();
        }
        ((ScreenInvoker)(Object)screen).invokeAddRenderableWidget(button);
    }

    /** Vanilla translation key for each recognized default button id (see TitleScreen/CommonButtons/ModsButton). */
    @Unique
    private static String vanillaKey(String id) {
        return switch (id) {
            case "singleplayer" -> "menu.singleplayer";
            case "multiplayer" -> "menu.multiplayer";
            case "realms" -> "menu.online";
            case "options" -> "menu.options";
            case "quit" -> "menu.quit";
            case "language" -> "options.language";
            case "accessibility" -> "accessibility.onboarding.accessibility.button";
            case "mods" -> "fml.menu.mods";
            default -> id;
        };
    }

    /** Replicates the vanilla action for each recognized default button id (see TitleScreen.init()). */
    @Unique
    private void runDefaultAction(Minecraft mc, TitleScreen screen, String id) {
        switch (id) {
            case "singleplayer" -> mc.setScreen(new SelectWorldScreen(screen));
            case "multiplayer" -> mc.setScreen(mc.options.skipMultiplayerWarning ? new JoinMultiplayerScreen(screen) : new SafetyScreen(screen));
            case "realms" -> mc.setScreen(new RealmsMainScreen(screen));
            case "options" -> mc.setScreen(new OptionsScreen(screen, mc.options));
            case "quit" -> mc.stop();
            case "language" -> mc.setScreen(new LanguageSelectScreen(screen, mc.options, mc.getLanguageManager()));
            case "accessibility" -> mc.setScreen(new AccessibilityOptionsScreen(screen, mc.options));
            case "mods" -> mc.setScreen(new ModListScreen(screen));
        }
    }

    @Unique
    private String resolveButtonImage(String ownImage, MenuConfig config) {
        if (ownImage != null && !ownImage.isEmpty()) return ownImage;
        String fallback = config.buttons.globalImage;
        return (fallback != null && !fallback.isEmpty()) ? fallback : null;
    }

    @Unique
    private void addCustomButtons(MenuConfig config, int screenWidth, int screenHeight) {
        for (MenuConfig.CustomButton cb : config.buttons.custom) {
            if (cb.x == -999 || cb.y == -999) continue; // no position defined -> hidden

            int w = Math.max(cb.width, MIN_BUTTON_WIDTH);
            int bx = clampOnScreen(percentToPixels(cb.x, screenWidth), w, screenWidth);
            int by = clampOnScreen(percentToPixels(cb.y, screenHeight), cb.height, screenHeight);

            String image = resolveButtonImage(cb.image, config);
            Button button;
            if (image != null) {
                button = new ImageButton(Component.literal(cb.text), btn -> handleCustomAction(cb), bx, by, w, cb.height, image, cb.hideText);
            } else {
                button = Button.builder(Component.literal(cb.text), btn -> handleCustomAction(cb)).bounds(bx, by, w, cb.height).build();
            }
            ((ScreenInvoker)(Object)this).invokeAddRenderableWidget(button);
        }
    }

    /** A vanilla {@link Button} that renders a custom image instead of the vanilla sprite behind its label. */
    private static class ImageButton extends Button {
        private final String imageRef;
        private final boolean hideText;

        protected ImageButton(Component message, OnPress onPress, int x, int y, int width, int height, String imageRef, boolean hideText) {
            super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
            this.imageRef = imageRef;
            this.hideText = hideText;
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            ResourceLocation tex = TextureResolver.resolve(imageRef);
            if (tex != null) {
                int[] dims = TextureResolver.dimensions(imageRef);
                int tw = dims != null ? dims[0] : getWidth();
                int th = dims != null ? dims[1] : getHeight();
                blitStretch(graphics, tex, getX(), getY(), getX() + getWidth(), getY() + getHeight(), 0.0F, 1.0F, 0.0F, 1.0F, tw, th);
            } else {
                super.renderWidget(graphics, mouseX, mouseY, partialTick);
                return;
            }
            if (!isActive()) {
                graphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), 0x80000000);
            } else if (isHovered()) {
                graphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), 0x40FFFFFF);
            }
            if (!hideText) {
                graphics.drawCenteredString(Minecraft.getInstance().font, getMessage(), getX() + getWidth() / 2, getY() + (getHeight() - 8) / 2, 0xFFFFFFFF);
            }
        }
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!Config.ENABLE_MOD.getAsBoolean()) return;

        MenuConfig config = MenuConfig.getInstance();
        String titleType = config.title != null ? config.title.type : "vanilla";
        String bgType = config.background.type;

        if ("vanilla".equals(titleType) && "panorama".equals(bgType)) return;
        if ("vanilla".equals(titleType) && "custom_panorama".equals(bgType)) return;
        if (!"vanilla".equals(titleType) && ("panorama".equals(bgType) || "custom_panorama".equals(bgType))) {
            renderCustomTitle(graphics, config);
            renderLogos(graphics, config);
            return;
        }

        renderCustomBackground(graphics, config);
        renderCustomTitle(graphics, config);
        renderLogos(graphics, config);
        renderTitleWidgets(graphics, mouseX, mouseY, partialTick);
        renderImageOverlays(graphics, config);
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
            int x = (int)(e.getValue().x - config.buttonAnimation.offset * (1.0f - eased));
            e.getKey().setX(Math.max(0, x));
        }

        if (progress >= 1.0f) {
            for (Map.Entry<AbstractWidget, MenuConfig.Position> e : animatedWidgets.entrySet())
                e.getKey().setX(e.getValue().x);
            animatedWidgets.clear();
            animationStartTime = 0;
        }
    }

    @Unique
    private void renderCustomTitle(GuiGraphics graphics, MenuConfig config) {
        if (config.title == null || "hidden".equals(config.title.type)) return;
        TitleScreen screen = (TitleScreen)(Object)this;

        if ("image".equals(config.title.type)) {
            try {
                ResourceLocation loc = ResourceLocation.parse(config.title.image);
                float s = config.title.scale;
                if (config.title.animated) {
                    float phase = (float)(System.currentTimeMillis() / 1000.0 * config.title.animSpeed);
                    s *= 1.0f + (float)Math.sin(phase) * config.title.animRange;
                }
                int tw = 310;
                int th = 44;
                int tx = percentToPixels(config.title.x, screen.width);
                int ty = percentToPixels(config.title.y, screen.height);
                int sw = (int)(tw * s);
                int sh = (int)(th * s);
                blitStretch(graphics, loc, tx, ty, tx + sw, ty + sh, 0.0F, 1.0F, 0.0F, 1.0F, tw, th);
            } catch (Exception ignored) {}
        }
    }

    @Unique
    private void renderLogos(GuiGraphics graphics, MenuConfig config) {
        if (config.logos == null) return;
        TitleScreen screen = (TitleScreen)(Object)this;
        renderLogo(graphics, screen, config.logos.topLeft, true);
        renderLogo(graphics, screen, config.logos.topRight, false);
    }

    @Unique
    private void renderLogo(GuiGraphics graphics, TitleScreen screen, MenuConfig.LogoConfig logo, boolean anchorLeft) {
        if (logo == null || !logo.enabled || logo.image == null || logo.image.isEmpty()) return;
        ResourceLocation tex = TextureResolver.resolve(logo.image);
        if (tex == null) return;

        int h = logo.height > 0 ? logo.height : 48;
        int w = logo.width;
        int[] dims = TextureResolver.dimensions(logo.image);
        if (w <= 0) {
            w = (dims != null && dims[1] > 0) ? Math.round(h * (dims[0] / (float) dims[1])) : h;
        }
        int y = (logo.y == -1) ? 10 : percentToPixels(logo.y, screen.height);
        int x;
        if (logo.x == -1) x = anchorLeft ? 10 : screen.width - w - 10;
        else x = percentToPixels(logo.x, screen.width);

        int texW = dims != null ? dims[0] : w;
        int texH = dims != null ? dims[1] : h;
        blitStretch(graphics, tex, x, y, x + w, y + h, 0.0F, 1.0F, 0.0F, 1.0F, texW, texH);
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
                NativeImage image = TextureResolver.loadNativeImage(path);
                if (image != null)
                    mc.getTextureManager().register(PANORAMA_PATHS[idx], new DynamicTexture(image));
            } catch (Exception ignored) {}
        }
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
            // Never push a widget to a negative X: on this MC version, GuiRenderer's scissor rejects
            // any out-of-bounds rectangle, which crashes the game for widgets whose label needs to
            // auto-scroll (narrow buttons with long text) while positioned off the left edge.
            if ("slide_right".equals(t)) w.setX(Math.max(0, w.getX() - config.buttonAnimation.offset));
        }
    }

    @Unique
    private void renderCustomBackground(GuiGraphics graphics, MenuConfig config) {
        TitleScreen s = (TitleScreen)(Object)this;
        switch (config.background.type) {
            case "image" -> renderFixedImageBackground(graphics, s, config.background.image);
            case "color" -> graphics.fill(0, 0, s.width, s.height, config.background.color.color);
            case "animated" -> renderAnimatedBackground(graphics, s, config);
        }
    }

    /** Renders a single fixed background image, preserving aspect ratio ("cover" fit) and applying the configured zoom effect. */
    @Unique
    private void renderFixedImageBackground(GuiGraphics e, TitleScreen s, MenuConfig.ImageConfig imgCfg) {
        String p = imgCfg.path;
        if (p == null || p.isEmpty()) { renderFallback(e, s); return; }
        ResourceLocation tex = TextureResolver.resolve(p);
        if (tex == null) { renderFallback(e, s); return; }

        float u0 = 0f, u1 = 1f, v0 = 0f, v1 = 1f;
        int[] dims = TextureResolver.dimensions(p);
        int texW = dims != null ? dims[0] : s.width;
        int texH = dims != null ? dims[1] : s.height;
        if (!"stretch".equals(imgCfg.fit) && dims != null && dims[0] > 0 && dims[1] > 0) {
            float imgAspect = dims[0] / (float) dims[1];
            float screenAspect = s.width / (float) Math.max(1, s.height);

            float visW, visH;
            if (imgAspect > screenAspect) { visW = screenAspect / imgAspect; visH = 1f; }
            else { visW = 1f; visH = imgAspect / screenAspect; }

            float scale = computeZoomScale(imgCfg.effect);
            visW = Math.min(1f, visW / scale);
            visH = Math.min(1f, visH / scale);

            u0 = (1f - visW) / 2f; u1 = 1f - u0;
            v0 = (1f - visH) / 2f; v1 = 1f - v0;
        }
        blitStretch(e, tex, 0, 0, s.width, s.height, u0, u1, v0, v1, texW, texH);
    }

    /** Smooth breathing zoom oscillating between minScale and maxScale over durationMs, no jump cuts. */
    @Unique
    private float computeZoomScale(MenuConfig.EffectConfig effect) {
        if (effect == null || !"zoom".equals(effect.type) || effect.durationMs <= 0) return 1f;
        double t = (System.currentTimeMillis() % effect.durationMs) / (double) effect.durationMs;
        double wave = 0.5 - 0.5 * Math.cos(t * Math.PI * 2);
        return (float) (effect.minScale + (effect.maxScale - effect.minScale) * wave);
    }

    @Unique
    private void renderImageBackground(GuiGraphics e, TitleScreen s, String p) {
        if (p == null || p.isEmpty()) { renderFallback(e, s); return; }
        ResourceLocation tex = TextureResolver.resolve(p);
        if (tex == null) { renderFallback(e, s); return; }
        int[] dims = TextureResolver.dimensions(p);
        int texW = dims != null ? dims[0] : s.width;
        int texH = dims != null ? dims[1] : s.height;
        blitStretch(e, tex, 0, 0, s.width, s.height, 0.0F, 1.0F, 0.0F, 1.0F, texW, texH);
    }

    @Unique
    private void renderAnimatedBackground(GuiGraphics e, TitleScreen s, MenuConfig c) {
        List<String> fr = c.background.animation.frames;
        if (fr == null || fr.isEmpty()) { renderFallback(e, s); return; }
        int fi = (int)((System.currentTimeMillis() / c.background.animation.frameTimeMs) % fr.size());
        if (!c.background.animation.loop && fi >= fr.size()) fi = fr.size() - 1;
        renderImageBackground(e, s, fr.get(Math.min(fi, Math.max(0, fr.size() - 1))));
    }

    @Unique
    private void renderFallback(GuiGraphics e, TitleScreen s) {
        e.fill(0, 0, s.width, s.height, 0xFF000000);
    }

    @Unique
    private void renderTitleWidgets(GuiGraphics e, int mx, int my, float pt) {
        for (Renderable r : ((TitleScreen)(Object)this).renderables)
            r.render(e, mx, my, pt);
    }

    @Unique
    private void renderImageOverlays(GuiGraphics e, MenuConfig c) {
        for (MenuConfig.ImageOverlay o : c.images) {
            if (o.path == null || o.path.isEmpty()) continue;
            ResourceLocation loc;
            try { loc = ResourceLocation.parse(o.path); } catch (Exception ex) { continue; }
            int w = o.width > 0 ? o.width : 64;
            int h = o.height > 0 ? o.height : 64;
            TitleScreen screen = (TitleScreen)(Object)this;
            int x = (o.x == -1) ? screen.width - w - 10 : percentToPixels(o.x, screen.width);
            int y = (o.y == -1) ? 10 : percentToPixels(o.y, screen.height);
            int[] dims = TextureResolver.dimensions(o.path);
            int texW = dims != null ? dims[0] : w;
            int texH = dims != null ? dims[1] : h;
            blitStretch(e, loc, x, y, x + w, y + h, 0.0F, 1.0F, 0.0F, 1.0F, texW, texH);
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
            case "open_singleplayer" -> mc.setScreen(new SelectWorldScreen(screen));
            case "open_multiplayer" -> mc.setScreen(new JoinMultiplayerScreen(screen));
            case "open_options" -> mc.setScreen(new OptionsScreen(screen, mc.options));
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
