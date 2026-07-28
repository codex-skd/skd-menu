package com.skd.menu.mixin;

import com.mojang.blaze3d.platform.NativeImage;
import com.skd.menu.Config;
import com.skd.menu.MenuConfig;
import com.skd.menu.TextureResolver;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
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

import java.util.*;

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
    @Unique private Map<AbstractWidget, MenuConfig.Position> animatedWidgets;
    @Unique private Map<AbstractWidget, String> defaultButtonImages;
    @Unique private static final String[] ROW_1_IDS = {"singleplayer", "multiplayer"};
    @Unique private static final String[] ROW_2_IDS = {"realms", "tw", "options", "quit"};
    @Unique private static final Set<String> ICON_ROW_IDS = Set.of("language", "accessibility", "mods");

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
        defaultButtonImages = new HashMap<>();

        List<AbstractWidget> widgets = new ArrayList<>();
        for (Renderable r : screen.renderables) {
            if (r instanceof AbstractWidget w) widgets.add(w);
        }

        processDefaultButtons(config, widgets, width, height);
        addCustomButtons(config);
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

        int centerX = width / 2;
        int centerY = height / 4;
        int baseY = centerY + 48;
        int gap = 24;

        // Singleplayer/Multiplayer, then the small icon row (Mods/Friends/Language/Accessibility)
        // reserves its own slot between them and Options/Quit, matching the vanilla layout.
        int idx = positionOrderedButtons(config, btnMap, widgets, ROW_1_IDS, centerX, baseY, gap, 0);
        idx = positionIconRow(config, btnMap, widgets, centerX, baseY, gap, idx);
        positionOrderedButtons(config, btnMap, widgets, ROW_2_IDS, centerX, baseY, gap, idx);
    }

    @Unique
    private int positionOrderedButtons(MenuConfig config, Map<String, MenuConfig.DefaultButton> btnMap,
                                        List<AbstractWidget> widgets, String[] ids, int centerX, int baseY, int gap, int idx) {
        for (String id : ids) {
            MenuConfig.DefaultButton cfg = btnMap.get(id);
            for (AbstractWidget w : widgets) {
                Component msg = w.getMessage();
                if (msg == null || !msg.getString().toLowerCase().contains(id)) continue;

                if (cfg != null && cfg.hide) {
                    w.visible = false;
                    continue;
                }
                int targetX = (cfg != null && cfg.x != -1) ? cfg.x : centerX - w.getWidth() / 2;
                int targetY = (cfg != null && cfg.y != -1) ? cfg.y : baseY + gap * idx;
                w.setX(targetX);
                w.setY(targetY);
                w.visible = true;
                idx++;

                String image = resolveButtonImage(cfg != null ? cfg.image : null, config);
                if (image != null) defaultButtonImages.put(w, image);
            }
        }
        return idx;
    }

    /** Lays out Mods/Friends/Language/Accessibility as a single horizontal row, centered, reserving one row slot. */
    @Unique
    private int positionIconRow(MenuConfig config, Map<String, MenuConfig.DefaultButton> btnMap,
                                 List<AbstractWidget> widgets, int centerX, int baseY, int gap, int idx) {
        List<AbstractWidget> icons = new ArrayList<>();
        for (AbstractWidget w : widgets) {
            Component msg = w.getMessage();
            if (msg == null) continue;
            String text = msg.getString().toLowerCase();

            String matchedId = ICON_ROW_IDS.stream().filter(text::contains).findFirst().orElse(null);
            boolean isFriends = matchedId == null && text.contains("friends");
            if (matchedId == null && !isFriends) continue;

            MenuConfig.DefaultButton cfg = matchedId != null ? btnMap.get(matchedId) : null;
            if (cfg != null && cfg.hide) {
                w.visible = false;
                continue;
            }
            String image = resolveButtonImage(cfg != null ? cfg.image : null, config);
            if (image != null) defaultButtonImages.put(w, image);
            icons.add(w);
        }
        if (icons.isEmpty()) return idx;

        icons.sort(Comparator.comparingInt(AbstractWidget::getX));
        int spacing = 4;
        int totalWidth = icons.stream().mapToInt(AbstractWidget::getWidth).sum() + spacing * (icons.size() - 1);
        int rowX = centerX - totalWidth / 2;
        int rowY = baseY + gap * idx;
        for (AbstractWidget w : icons) {
            w.setX(rowX);
            w.setY(rowY);
            w.visible = true;
            rowX += w.getWidth() + spacing;
        }
        return idx + 1;
    }

    @Unique
    private String resolveButtonImage(String ownImage, MenuConfig config) {
        if (ownImage != null && !ownImage.isEmpty()) return ownImage;
        String fallback = config.buttons.globalImage;
        return (fallback != null && !fallback.isEmpty()) ? fallback : null;
    }

    @Unique
    private void addCustomButtons(MenuConfig config) {
        for (MenuConfig.CustomButton cb : config.buttons.custom) {
            if (cb.x == -999 || cb.y == -999) continue; // no position defined -> hidden

            String image = resolveButtonImage(cb.image, config);
            Button.Builder builder = Button.builder(Component.literal(cb.text), btn -> handleCustomAction(cb))
                .bounds(cb.x, cb.y, cb.width, cb.height);
            Button button = (image != null) ? builder.build(b -> new ImageButton(b, image)) : builder.build();
            ((ScreenInvoker)(Object)this).invokeAddRenderableWidget(button);
        }
    }

    /** A vanilla {@link Button} that renders a custom image instead of the vanilla sprite behind its label. */
    private static class ImageButton extends Button {
        private final String imageRef;

        protected ImageButton(Builder builder, String imageRef) {
            super(builder);
            this.imageRef = imageRef;
        }

        @Override
        protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
            Identifier tex = TextureResolver.resolve(imageRef);
            if (tex != null) {
                graphics.blit(tex, getX(), getY(), getX() + getWidth(), getY() + getHeight(), 0.0F, 1.0F, 0.0F, 1.0F);
            } else {
                extractDefaultSprite(graphics);
            }
            extractDefaultLabel(graphics.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE));
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
            renderLogos(extractor, config);
            return;
        }

        renderCustomBackground(extractor, config);
        renderCustomTitle(extractor, config);
        renderLogos(extractor, config);
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
    private void renderLogos(GuiGraphicsExtractor extractor, MenuConfig config) {
        if (config.logos == null) return;
        TitleScreen screen = (TitleScreen)(Object)this;
        renderLogo(extractor, screen, config.logos.topLeft, true);
        renderLogo(extractor, screen, config.logos.topRight, false);
    }

    @Unique
    private void renderLogo(GuiGraphicsExtractor extractor, TitleScreen screen, MenuConfig.LogoConfig logo, boolean anchorLeft) {
        if (logo == null || !logo.enabled || logo.image == null || logo.image.isEmpty()) return;
        Identifier tex = TextureResolver.resolve(logo.image);
        if (tex == null) return;

        int h = logo.height > 0 ? logo.height : 48;
        int w = logo.width;
        if (w <= 0) {
            int[] dims = TextureResolver.dimensions(logo.image);
            w = (dims != null && dims[1] > 0) ? Math.round(h * (dims[0] / (float) dims[1])) : h;
        }
        int x = logo.x;
        int y = logo.y == -1 ? 10 : logo.y;
        if (x == -1) x = anchorLeft ? 10 : screen.width - w - 10;

        extractor.blit(tex, x, y, x + w, y + h, 0.0F, 1.0F, 0.0F, 1.0F);
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
                    mc.getTextureManager().register(PANORAMA_PATHS[idx], new DynamicTexture(() -> "skd_pano_" + idx, image));
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
            if ("slide_right".equals(t)) w.setX(w.getX() - config.buttonAnimation.offset);
        }
    }

    @Unique
    private void renderCustomBackground(GuiGraphicsExtractor extractor, MenuConfig config) {
        TitleScreen s = (TitleScreen)(Object)this;
        switch (config.background.type) {
            case "image" -> renderFixedImageBackground(extractor, s, config.background.image);
            case "color" -> extractor.fill(0, 0, s.width, s.height, config.background.color.color);
            case "animated" -> renderAnimatedBackground(extractor, s, config);
        }
    }

    /** Renders a single fixed background image, preserving aspect ratio ("cover" fit) and applying the configured zoom effect. */
    @Unique
    private void renderFixedImageBackground(GuiGraphicsExtractor e, TitleScreen s, MenuConfig.ImageConfig imgCfg) {
        String p = imgCfg.path;
        if (p == null || p.isEmpty()) { renderFallback(e, s); return; }
        Identifier tex = TextureResolver.resolve(p);
        if (tex == null) { renderFallback(e, s); return; }

        float u0 = 0f, u1 = 1f, v0 = 0f, v1 = 1f;
        if (!"stretch".equals(imgCfg.fit)) {
            int[] dims = TextureResolver.dimensions(p);
            if (dims != null && dims[0] > 0 && dims[1] > 0) {
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
        }
        e.blit(tex, 0, 0, s.width, s.height, u0, u1, v0, v1);
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
    private void renderImageBackground(GuiGraphicsExtractor e, TitleScreen s, String p) {
        if (p == null || p.isEmpty()) { renderFallback(e, s); return; }
        Identifier tex = TextureResolver.resolve(p);
        if (tex == null) { renderFallback(e, s); return; }
        e.blit(tex, 0, 0, s.width, s.height, 0.0F, 1.0F, 0.0F, 1.0F);
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
        for (Renderable r : ((TitleScreen)(Object)this).renderables) {
            if (r instanceof AbstractWidget w && defaultButtonImages.containsKey(w)) {
                Identifier tex = TextureResolver.resolve(defaultButtonImages.get(w));
                if (tex != null) {
                    e.blit(tex, w.getX(), w.getY(), w.getX() + w.getWidth(), w.getY() + w.getHeight(), 0.0F, 1.0F, 0.0F, 1.0F);
                }
                e.textRendererForWidget(w, GuiGraphicsExtractor.HoveredTextEffects.NONE)
                    .accept(TextAlignment.CENTER, w.getX() + w.getWidth() / 2, w.getY() + (w.getHeight() - 8) / 2, w.getMessage());
                continue;
            }
            r.extractRenderState(e, mx, my, pt);
        }
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
