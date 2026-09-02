package com.skd.menu.mixin;

import com.skd.menu.Config;
import com.skd.menu.LoadingConfig;
import com.skd.menu.TextureResolver;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Replaces the vanilla loading overlay (Mojang logo, red background and progress bar) with a fully
 * custom loading screen driven by {@code config/skd_menu/loading.json}: static background image,
 * top-left banner, top-right mod logo and a custom progress bar whose fill is drawn procedurally.
 * Everything fades in/out exactly like the vanilla overlay.
 */
@Mixin(LoadingOverlay.class)
public abstract class LoadingOverlayMixin {
    @Shadow @Final private Minecraft minecraft;
    @Shadow @Final private ReloadInstance reload;
    @Shadow @Final private boolean fadeIn;
    @Shadow private float currentProgress;
    @Shadow protected long fadeOutStart;
    @Shadow private long fadeInStart;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!Config.ENABLE_MOD.getAsBoolean()) return;
        LoadingConfig config = LoadingConfig.getInstance();

        int width = graphics.guiWidth();
        int height = graphics.guiHeight();
        long now = Util.getMillis();
        if (this.fadeIn && this.fadeInStart == -1L) {
            this.fadeInStart = now;
        }
        float fadeOutAnim = this.fadeOutStart > -1L ? (float)(now - this.fadeOutStart) / 1000.0F : -1.0F;
        float fadeInAnim = this.fadeInStart > -1L ? (float)(now - this.fadeInStart) / 500.0F : -1.0F;

        int bgColor = backgroundColor(config);
        int bgAlpha;
        float logoAlpha;
        if (fadeOutAnim >= 1.0F) {
            if (this.minecraft.screen != null) {
                this.minecraft.screen.render(graphics, 0, 0, partialTick);
            }
            int alpha = Mth.ceil((1.0F - Mth.clamp(fadeOutAnim - 1.0F, 0.0F, 1.0F)) * 255.0F);
            graphics.fill(0, 0, width, height, replaceAlpha(bgColor, alpha));
            bgAlpha = alpha;
            logoAlpha = 1.0F - Mth.clamp(fadeOutAnim - 1.0F, 0.0F, 1.0F);
        } else if (this.fadeIn) {
            if (this.minecraft.screen != null && fadeInAnim < 1.0F) {
                this.minecraft.screen.render(graphics, mouseX, mouseY, partialTick);
            }
            int alpha = Mth.ceil(Mth.clamp(fadeInAnim, 0.15F, 1.0F) * 255.0F);
            graphics.fill(0, 0, width, height, replaceAlpha(bgColor, alpha));
            bgAlpha = alpha;
            logoAlpha = Mth.clamp(fadeInAnim, 0.0F, 1.0F);
        } else {
            bgAlpha = 255;
            logoAlpha = 1.0F;
        }

        renderBackground(graphics, width, height, config, bgAlpha);
        renderBanner(graphics, width, height, config, logoAlpha);
        renderLogo(graphics, width, height, config, logoAlpha);

        float actualProgress = this.reload.getActualProgress();
        this.currentProgress = Mth.clamp(this.currentProgress * 0.95F + actualProgress * 0.05F, 0.0F, 1.0F);
        if (fadeOutAnim < 1.0F) {
            renderProgressBar(graphics, width, height, config, 1.0F - Mth.clamp(fadeOutAnim, 0.0F, 1.0F));
        }
        if (fadeOutAnim >= 2.0F) {
            this.minecraft.setOverlay(null);
        }
        ci.cancel();
    }

    /** Fallback color painted behind the background image (used while a URL image is still downloading and during fades). */
    @Unique
    private int backgroundColor(LoadingConfig config) {
        if (config.background != null && "color".equals(config.background.type) && config.background.color != null) {
            return config.background.color.color;
        }
        return 0xFF000000;
    }

    @Unique
    private void renderBackground(GuiGraphics g, int width, int height, LoadingConfig config, int alpha) {
        if (config.background == null) return;
        if ("image".equals(config.background.type)) {
            renderCoverImage(g, width, height, config.background.image, alpha);
        }
    }

    /** Blits the background with "cover" fit and a fade alpha, without distorting the image. */
    @Unique
    private void renderCoverImage(GuiGraphics g, int width, int height, LoadingConfig.ImageConfig imgCfg, int alpha) {
        String p = imgCfg != null ? imgCfg.path : null;
        if (p == null || p.isEmpty()) return;
        ResourceLocation tex = TextureResolver.resolve(p);
        if (tex == null) return;

        int[] dims = TextureResolver.dimensions(p);
        int texW = dims != null && dims[0] > 0 ? dims[0] : 1;
        int texH = dims != null && dims[1] > 0 ? dims[1] : 1;

        int srcW = texW;
        int srcH = texH;
        int u = 0;
        int v = 0;
        if (!"stretch".equals(imgCfg.fit) && dims != null) {
            float imgAspect = dims[0] / (float) dims[1];
            float screenAspect = width / (float) Math.max(1, height);
            float visW, visH;
            if (imgAspect > screenAspect) { visW = screenAspect / imgAspect; visH = 1f; }
            else { visW = 1f; visH = imgAspect / screenAspect; }
            float u0 = (1f - visW) / 2f;
            float v0 = (1f - visH) / 2f;
            u = Math.round(u0 * texW);
            v = Math.round(v0 * texH);
            srcW = Math.max(1, Math.round(visW * texW));
            srcH = Math.max(1, Math.round(visH * texH));
        }
        g.blit(tex, 0, 0, u, v, width, height, srcW, srcH, texW, texH);
    }

    /** Wide mod banner, top-left, mirroring the main menu's top-left logo placement. */
    @Unique
    private void renderBanner(GuiGraphics g, int width, int height, LoadingConfig config, float logoAlpha) {
        if (config.banner == null || !config.banner.enabled) return;
        String ref = config.banner.image;
        if (ref == null || ref.isEmpty()) return;
        ResourceLocation tex = TextureResolver.resolve(ref);
        if (tex == null) return;

        int h = config.banner.height > 0 ? config.banner.height : 48;
        int w = config.banner.width;
        int[] dims = TextureResolver.dimensions(ref);
        if (w <= 0) {
            w = (dims != null && dims[1] > 0) ? Math.round(h * (dims[0] / (float) dims[1])) : h;
        }
        int y = (config.banner.y == -1) ? 10 : percentToPixels(config.banner.y, height);
        int x;
        if (config.banner.x == -1) x = 10;
        else x = percentToPixels(config.banner.x, width);

        int texW = dims != null && dims[0] > 0 ? dims[0] : w;
        int texH = dims != null && dims[1] > 0 ? dims[1] : h;
        g.blit(tex, x, y, 0, 0, w, h, texW, texH, texW, texH);
    }

    /** Mod logo, top-right, mirroring the main menu's top-right logo placement. */
    @Unique
    private void renderLogo(GuiGraphics g, int width, int height, LoadingConfig config, float logoAlpha) {
        if (config.logo == null || !config.logo.enabled) return;
        String ref = config.logo.image;
        if (ref == null || ref.isEmpty()) return;
        ResourceLocation tex = TextureResolver.resolve(ref);
        if (tex == null) return;

        int h = config.logo.height > 0 ? config.logo.height : 48;
        int w = config.logo.width;
        int[] dims = TextureResolver.dimensions(ref);
        if (w <= 0) {
            w = (dims != null && dims[1] > 0) ? Math.round(h * (dims[0] / (float) dims[1])) : h;
        }
        int y = (config.logo.y == -1) ? 10 : percentToPixels(config.logo.y, height);
        int x;
        if (config.logo.x == -1) x = width - w - 10;
        else x = percentToPixels(config.logo.x, width);

        int texW = dims != null && dims[0] > 0 ? dims[0] : w;
        int texH = dims != null && dims[1] > 0 ? dims[1] : h;
        g.blit(tex, x, y, 0, 0, w, h, texW, texH, texW, texH);
    }

    /** Draws the track texture and then a procedural gradient fill cropped to the current progress. */
    @Unique
    private void renderProgressBar(GuiGraphics g, int width, int height, LoadingConfig config, float fade) {
        if (config.bar == null || !config.bar.enabled) return;

        int barW = Math.max(20, percentToPixels(config.bar.width, width));
        int barH;
        if (config.bar.height > 0) {
            barH = Math.max(4, percentToPixels(config.bar.height, height));
        } else {
            int[] tDims = TextureResolver.dimensions(config.bar.track);
            barH = (tDims != null && tDims[0] > 0)
                ? Math.max(4, Math.round(barW * (tDims[1] / (float) tDims[0])))
                : Math.max(4, percentToPixels(3f, height));
        }
        int barX = percentToPixels(config.bar.x, width) - barW / 2;
        int barY = percentToPixels(config.bar.y, height) - barH / 2;
        barX = clampOnScreen(barX, barW, width);
        barY = clampOnScreen(barY, barH, height);

        int alpha = Math.round(fade * 255.0F);

        // The track frame has a transparent interior, so the fill is drawn FIRST (behind) and the
        // track on top: the frame's opaque ornate ends/rails never get covered by the fill color.
        if (config.bar.fill != null && this.currentProgress > 0f) {
            LoadingConfig.FillConfig fill = config.bar.fill;
            int fx0 = barX + percentOf(fill.xInset, barW);
            int fx1 = barX + barW - percentOf(fill.xInset, barW);
            int fy0 = barY + percentOf(fill.yInset, barH);
            int fy1 = Math.min(barY + barH, fy0 + Math.max(1, percentOf(fill.height, barH)));
            if (fx1 > fx0 && fy1 > fy0) {
                int progressW = Math.min(fx1 - fx0, Math.round((fx1 - fx0) * this.currentProgress));
                int px1 = fx0 + progressW;
                if (px1 > fx0) {
                    int c0 = parseColor(fill.colorStart, 0xFF7B2CBF);
                    int c1 = parseColor(fill.colorEnd, 0xFFFF4D6D);
                    if (fill.glow) {
                        int pad = Math.max(2, barH / 5);
                        g.fill(fx0 - pad, fy0 - pad, px1 + pad, fy1 + pad,
                            FastColor.ARGB32.color(Math.round(alpha * Math.min(255, fill.glowAlpha) / 255.0F),
                                FastColor.ARGB32.red(c1), FastColor.ARGB32.green(c1), FastColor.ARGB32.blue(c1)));
                    }
                    renderHorizontalGradient(g, fx0, fy0, px1, fy1, c0, c1, alpha);
                }
            }
        }

        ResourceLocation track = TextureResolver.resolve(config.bar.track);
        if (track != null) {
            int[] tDims = TextureResolver.dimensions(config.bar.track);
            int texW = tDims != null && tDims[0] > 0 ? tDims[0] : 1;
            int texH = tDims != null && tDims[1] > 0 ? tDims[1] : 1;
            g.blit(track, barX, barY, 0, 0, barW, barH, texW, texH, texW, texH);
        }
    }

    /** Horizontal color gradient drawn as thin vertical strips (keeps the leading progress edge clean). */
    @Unique
    private void renderHorizontalGradient(GuiGraphics g, int x0, int y0, int x1, int y1, int c0, int c1, int alpha) {
        if (x1 <= x0 || y1 <= y0) return;
        int segments = Math.max(16, (x1 - x0) / 8);
        int r0 = FastColor.ARGB32.red(c0), g0 = FastColor.ARGB32.green(c0), b0 = FastColor.ARGB32.blue(c0);
        int r1 = FastColor.ARGB32.red(c1), g1 = FastColor.ARGB32.green(c1), b1 = FastColor.ARGB32.blue(c1);
        for (int i = 0; i < segments; i++) {
            int sx = x0 + (x1 - x0) * i / segments;
            int ex = x0 + (x1 - x0) * (i + 1) / segments;
            float t = (i + 0.5F) / segments;
            g.fill(sx, y0, ex, y1, FastColor.ARGB32.color(alpha,
                Math.round(r0 + (r1 - r0) * t), Math.round(g0 + (g1 - g0) * t), Math.round(b0 + (b1 - b0) * t)));
        }
    }

    @Unique
    private static int replaceAlpha(int color, int alpha) {
        return (color & 0xFFFFFF) | (alpha << 24);
    }

    @Unique
    private static int percentToPixels(float percent, int total) {
        return Math.round(percent / 100f * total);
    }

    @Unique
    private static int percentOf(float percent, int total) {
        return Math.round(percent / 100f * total);
    }

    @Unique
    private static int clampOnScreen(int pos, int size, int screenTotal) {
        return Math.max(0, Math.min(pos, Math.max(0, screenTotal - size)));
    }

    /** Parses "#RRGGBB", "#AARRGGBB", "0x..." or a decimal int; returns {@code def} on failure. */
    @Unique
    private static int parseColor(String s, int def) {
        if (s == null || s.isBlank()) return def;
        s = s.trim();
        try {
            if (s.startsWith("#")) {
                String hex = s.substring(1);
                if (hex.length() == 6) return 0xFF000000 | Integer.parseInt(hex, 16);
                if (hex.length() == 8) return (int) Long.parseLong(hex, 16);
            } else if (s.startsWith("0x")) {
                long v = Long.parseLong(s.substring(2), 16);
                return v >= 0xFF000000L ? (int) v : (int) (0xFF000000L | v);
            } else {
                return Integer.parseInt(s);
            }
        } catch (Exception ignored) {}
        return def;
    }
}
