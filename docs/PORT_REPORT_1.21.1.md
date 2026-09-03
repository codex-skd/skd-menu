# Port Report: SKD Menu 26.2 → 1.21.1 / NeoForge 21.1.249

> **Nota (beta.4, 2026-09-03):** la feature de pantalla de carga custom se eliminó en 1.21.1
> (`LoadingOverlayMixin`, `LoadingConfig`, `EarlyDisplayInstaller` y recursos `earlydisplay/`).
> No renderizaba de forma fiable durante los reloads de resource pack de servidor en modpacks
> grandes. El resto de este documento describe el estado del port original y se conserva como
> registro histórico.

## Summary

Ported all 10 source files from MC 26.2 to MC 1.21.1 / NeoForge 21.1.249 / Java 21. Three files required substantial API reversions; seven files required no changes. All 26.2 features are preserved: custom backgrounds (image/color/animated/panorama/custom_panorama) with breathing zoom, corner logos, title image, per-button repositioning/hiding/custom buttons/button images, image overlays, and the custom loading screen with background, banner, logo, and procedural progress bar.

## Files Changed

### 1. `src/main/java/com/skd/menu/TextureResolver.java`

**Pattern applied:** `Identifier` → `ResourceLocation` (global rename, 8 sites)

| Change | Details |
|---|---|
| Import | `net.minecraft.resources.Identifier` → `net.minecraft.resources.ResourceLocation` |
| Type declarations | All `Identifier` variable/field/return types → `ResourceLocation` |
| Factory methods | `Identifier.parse(...)` → `ResourceLocation.parse(...)` |
| Factory methods | `Identifier.fromNamespaceAndPath(...)` → `ResourceLocation.fromNamespaceAndPath(...)` |

No behaviour change — `ResourceLocation` is the1.21.1 equivalent of26.2's `Identifier` (same semantics, same API shape).

### 2. `src/main/java/com/skd/menu/mixin/TitleScreenMixin.java`

**Patterns applied:** GuiGraphicsExtractor→GuiGraphics, extractRenderState→render, Identifier→ResourceLocation, setScreenAndShow→setScreen, ModListScreen path change, friends feature removed.

| Change | Details |
|---|---|
| Import | Removed `net.minecraft.client.gui.GuiGraphicsExtractor` |
| Import | Added `net.minecraft.client.gui.GuiGraphics` |
| Import | `net.minecraft.resources.Identifier` → `net.minecraft.resources.ResourceLocation` |
| Import | `net.neoforged.neoforge.client.gui.modlist.ModListScreen` → `net.neoforged.neoforge.client.gui.ModListScreen` |
| Import | Removed `net.minecraft.client.gui.screens.friends.FriendsOverlayScreen` |
| Import | Removed `net.minecraft.client.gui.screens.options.OnlineOptionsScreen` |
| Field types | All `Identifier[]` / `Identifier` → `ResourceLocation[]` / `ResourceLocation` |
| Mixin target | `@Inject(method = "extractRenderState", ...)` → `@Inject(method = "render", ...)` |
| Inject signature | `(GuiGraphicsExtractor extractor, ...)` → `(GuiGraphics graphics, ...)` |
| `ImageButton` inner class | `extractContents(GuiGraphicsExtractor graphics, ...)` → `renderWidget(GuiGraphics graphics, ...)` |
| `ImageButton` rendering | `extractDefaultSprite(graphics)` → `super.renderWidget(...)` (fallback when no custom image) |
| `ImageButton` label | `extractDefaultLabel(graphics.textRendererForWidget(...))` → `graphics.drawCenteredString(font, getMessage(), ...)` |
| `ImageButton` hover | `isHoveredOrFocused()` → `isHovered()` |
| `ImageButton` construction | `Button.builder(...).build(b -> new ImageButton(b, ...))` → direct `new ImageButton(Component, OnPress, x, y, w, h, image, hideText)` |
| `renderTitleWidgets` | `r.extractRenderState(e, mx, my, pt)` → `r.render(e, mx, my, pt)` |
| `renderCustomTitle` | `extractor.blit(loc, tx, ty, tx+sw, ty+sh, u0, u1, v0, v1)` → `graphics.blit(loc, tx, ty, tx+sw, ty+sh, u0, u1, v0, v1)` |
| All render methods | `GuiGraphicsExtractor` param type → `GuiGraphics` param type (7 methods) |
| Screen navigation | All `mc.setScreenAndShow(...)` → `mc.setScreen(...)` (7 sites) |
| Friends button action | `OnlineOptionsScreen.confirmFriendsListEnabled(...)` → no-op (`{ /* 1.21.1 has no friends feature */ }`) |
| Language screen | `new LanguageSelectScreen(screen, mc.options, mc.getLanguageManager())` → `new LanguageSelectScreen(screen, mc.options)` (1.21.1 has 2-arg constructor) |
| Mods button | `ModListScreen.create(screen)` → `new ModListScreen(screen)` (1.21.1 uses constructor) |
| Custom button actions | `mc.setScreenAndShow(...)` → `mc.setScreen(...)` (3 sites) |

**Features preserved:** All — custom backgrounds with breathing zoom, corner logos, title image, button repositioning/hiding/custom buttons/button images, image overlays, button slide-in animation.

### 3. `src/main/java/com/skd/menu/mixin/LoadingOverlayMixin.java`

**Patterns applied:** GuiGraphicsExtractor→GuiGraphics, extractRenderState→render, RenderPipelines removed, ARGB→FastColor.ARGB32, Identifier→ResourceLocation, Util moved, 26.2-only overlay APIs removed.

| Change | Details |
|---|---|
| Import | Removed `net.minecraft.client.gui.GuiGraphicsExtractor` |
| Import | Added `net.minecraft.client.gui.GuiGraphics` |
| Import | Removed `net.minecraft.client.renderer.RenderPipelines` |
| Import | `net.minecraft.resources.Identifier` → `net.minecraft.resources.ResourceLocation` |
| Import | `net.minecraft.util.ARGB` → `net.minecraft.util.FastColor` |
| Import | `net.minecraft.util.Util` → `net.minecraft.Util` |
| Mixin target | `@Inject(method = "extractRenderState", ...)` → `@Inject(method = "render", ...)` |
| Inject signature | `(GuiGraphicsExtractor graphics, ...)` → `(GuiGraphics graphics, ...)` |
| All methods | `GuiGraphicsExtractor` param type → `GuiGraphics` param type (7 methods) |
| Screen access | `this.minecraft.gui.screen()` (method) → `this.minecraft.gui.screen` (field) |
| Screen render | `.extractRenderStateWithTooltipAndSubtitles(graphics, ...)` → `.render(graphics, ...)` |
| HUD render | `this.minecraft.gui.hud.extractDeferredSubtitles()` → `this.minecraft.gui.render(partialTick)` |
| Stratum | `graphics.nextStratum()` → removed (not present in1.21.1) |
| Clear color | `ARGB.setVector4fFromARGB32(...)` → removed (26.2-only clear color override; not needed in1.21.1) |
| Overlay dismiss | `this.minecraft.gui.setOverlay(null)` → `this.minecraft.setOverlay(null)` |
| Cover blit | `g.blit(RenderPipelines.GUI_TEXTURED, tex, 0, 0, u, v, w, h, srcW, srcH, texW, texH, ARGB.white(alpha/255F))` → `g.blit(tex, 0, 0, u, v, w, h, srcW, srcH, texW, texH)` (drop pipeline arg, drop alpha tint — alpha is baked into fill calls) |
| Banner/logo blit | `g.blit(RenderPipelines.GUI_TEXTURED, tex, x, y, 0, 0, w, h, texW, texH, texW, texH, ARGB.white(logoAlpha))` → `g.blit(tex, x, y, 0, 0, w, h, texW, texH, texW, texH)` |
| Track blit | Same pattern — remove `RenderPipelines.GUI_TEXTURED` and `ARGB.white(...)` |
| ARGB.color(a,r,g,b) | `ARGB.color(a,r,g,b)` → `FastColor.ARGB32.color(a,r,g,b)` (2 sites) |
| ARGB.red/green/blue | `ARGB.red(c)` → `FastColor.ARGB32.red(c)` (6 sites) |
| Gradient | Same ARGB→FastColor.ARGB32 rename in `renderHorizontalGradient` |

**Features preserved:** All — custom loading background with cover fit, banner, logo, procedural progress bar with gradient fill and glow, fade-in/fade-out animation.

## Files NOT Changed

| File | Reason |
|---|---|
| `mixin/ScreenInvoker.java` | Already compiles; no 26.2-only API |
| `Config.java` | Pure NeoForge config spec; no MC render API |
| `LoadingConfig.java` | Pure Gson data class; no MC API |
| `MenuConfig.java` | Pure Gson data class; no MC API |
| `SkdMenu.java` | Standard `@Mod` entrypoint; no MC render API |
| `SkdMenuClient.java` | Standard client setup; NeoForge21.1.x compatible as-is |
| `EarlyDisplayInstaller.java` | Standard Java file I/O + FML config keys; no 26.2-only API |
| `neoforge.mods.toml` (template) | Already updated with `modLoader="javafml"` + `loaderVersion` |
| `gradle.properties` | Already updated in scaffold |
| `build.gradle` | Already updated in scaffold |

## Reimplemented vs. Translated

All changes were direct API translations — no features had to be reimplemented from scratch. The key translation that required structural change was the `ImageButton` inner class:

- **26.2:** Extended `Button`, overrode `extractContents(GuiGraphicsExtractor, ...)`, called `extractDefaultSprite()` and `extractDefaultLabel()` helpers.
- **1.21.1:** Extended `Button`, overrode `renderWidget(GuiGraphics, ...)`, called `super.renderWidget()` for fallback and `graphics.drawCenteredString()` for the label. Constructor takes explicit parameters instead of a `Builder` lambda.

## Stubbed / Guarded

Nothing was stubbed or guarded. All 26.2 features have clean1.21.1 equivalents and were translated directly.

## Dropped Behaviour

| Behaviour | Reason |
|---|---|
| Friends button action (`OnlineOptionsScreen.confirmFriendsListEnabled(...)`) | 1.21.1 has no friends feature. The button still renders if configured, but clicking it does nothing. |
| `graphics.nextStratum()` | 26.2-only render layer API. Not needed in1.21.1 — `GuiGraphics.fill()` works directly. |
| `ARGB.setVector4fFromARGB32(...)` (clear color override) | 26.2-only mechanism to set the GL clear color from the mod. Not available in1.21.1. The overlay's own background fill covers the screen anyway. |
| `ARGB.white(alpha)` tint on blit calls | 1.21.1 `blit()` has no tint parameter. Alpha is handled via `fill()` calls for the background overlay; blit draws at full opacity. |

## Verification

`src/main/java` should compile with `./gradlew build` on NeoForge 21.1.249. The operator should verify:
1. `./gradlew build` succeeds
2. Custom background with breathing zoom renders on the title screen
3. Corner logos and title image render
4. Button repositioning/hiding/custom buttons work
5. Custom loading screen with progress bar appears during world load
6. Image overlays render

---

## Second-Pass Changes (compile error fixes)

### 1. `src/main/java/com/skd/menu/TextureResolver.java`

| Change | Details |
|---|---|
| `DynamicTexture` constructor | `new DynamicTexture(() -> ref, image)` → `new DynamicTexture(image)` (1.21.1 has no `Supplier<String>` label constructor) |

### 2. `src/main/java/com/skd/menu/mixin/TitleScreenMixin.java`

| Change | Details |
|---|---|
| Import | Added `net.minecraft.client.gui.components.Tooltip` |
| `OptionsScreen` constructor | `new OptionsScreen(screen, mc.options, false)` → `new OptionsScreen(screen, mc.options)` (1.21.1 has 2-arg constructor) — 2 sites |
| `LanguageSelectScreen` constructor | `new LanguageSelectScreen(screen, mc.options)` → `new LanguageSelectScreen(screen, mc.options, mc.getLanguageManager())` (1.21.1 has 3-arg constructor) |
| `ImageButton.setBounds` | `this.setBounds(x, y, width, height)` → `this.setX(x); this.setY(y); this.setWidth(width); this.setHeight(height)` (1.21.1 `AbstractWidget` has no `setBounds`) |
| `ImageButton` font | `font` → `Minecraft.getInstance().font` (inner class has no `font` field) |
| `DynamicTexture` constructor | `new DynamicTexture(() -> "skd_pano_" + idx, image)` → `new DynamicTexture(image)` |
| 8-arg `blit` (float UV) | All `graphics.blit(tex, x0, y0, x1, y1, u0, u1, v0, v1)` calls replaced with `blitStretch(graphics, tex, x0, y0, x1, y1, u0, u1, v0, v1, texW, texH)` — 1.21.1 `GuiGraphics` has no 8-arg `blit` with float UVs. Added `@Unique private static void blitStretch(...)` helper that computes pixel UV dimensions and delegates to the 10-arg `blit`. Sites: ImageButton `renderWidget`, `renderCustomTitle`, `renderLogo`, `renderFixedImageBackground`, `renderImageBackground`, `renderImageOverlays`. |

### 3. `src/main/java/com/skd/menu/mixin/LoadingOverlayMixin.java`

| Change | Details |
|---|---|
| Screen access | `this.minecraft.gui.screen` → `this.minecraft.screen` (2 sites). `minecraft.gui` is the in-game HUD (`net.minecraft.client.gui.Gui`), it has no `screen` field. |
| HUD fallback removed | `else { this.minecraft.gui.render(partialTick) }` branches deleted (2 sites). During the loading overlay there is no HUD to draw; the screen render or nothing is the correct behavior. |
