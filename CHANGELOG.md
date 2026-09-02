# SKD Menu (1.21.1) — Changelog

Branch `minecraft/1.21.1/neoforge-21.1.249/production`. History independent of the 26.2 branch.

## [0.0.0-beta.1] - 2026-09-02

### Added

- **Initial port to Minecraft 1.21.1 / NeoForge 21.1.249** (Java 21). API port of the stable
  26.2 line (1.2.4): 10 classes, 3 client mixins (`TitleScreenMixin`, `LoadingOverlayMixin`,
  `ScreenInvoker`), no dependencies. Full feature set unchanged: custom / panorama / colour /
  animated background with the breathing zoom, corner logos + title image, per-button
  reposition / hide / custom buttons / button images, image overlays, the restyled loading
  screen + progress bar, and the FML early-window theme.

### Technical

- 26.2 → 1.21.1 client render API reversions, all in `TitleScreenMixin` / `LoadingOverlayMixin`
  / `TextureResolver`:
  - `net.minecraft.client.gui.GuiGraphicsExtractor` → `GuiGraphics`. 26.2 split GUI drawing
    into an "extract render state" pass; 1.21.1 has none. The `@Inject` targets move from
    `extractRenderState` to the 1.21.1 `render(GuiGraphics, int, int, float)` method.
  - `net.minecraft.util.ARGB` → `net.minecraft.util.FastColor.ARGB32`.
  - `net.minecraft.client.renderer.RenderPipelines` → plain `GuiGraphics` `blit` / `fill` /
    `fillGradient` (the pipeline argument is dropped).
  - The 26.2 8-arg stretching blit `blit(ResourceLocation, x0, y0, x1, y1, u0, u1, v0, v1)`
    does not exist in 1.21.1 `GuiGraphics` (the region-blit is not public). Replaced with a
    `@Unique blitStretch` helper that scales the `PoseStack` and draws the region at native
    pixel size via the public `blit(RL, x, y, uOff, vOff, w, h, texW, texH)` overload;
    texture dimensions come from `TextureResolver.dimensions(ref)`.
  - `net.minecraft.resources.Identifier` → `net.minecraft.resources.ResourceLocation`.
  - `net.minecraft.util.Util` → `net.minecraft.Util`.
  - `Minecraft.screen()` (method) → `Minecraft.screen` (field);
    `Minecraft.setScreenAndShow(...)` → `setScreen(...)`; `Minecraft.hud` → `Minecraft.gui`.
  - The 26.2 Friends feature (`net.minecraft.client.gui.screens.friends`, `FriendsOverlayScreen`,
    `Minecraft.confirmFriendsListEnabled`) has no 1.21.1 form — the realms button goes straight
    to `com.mojang.realmsclient.RealmsMainScreen`.
  - `net.neoforged.neoforge.client.gui.modlist.ModListScreen` (26.2.0.57 location) →
    `net.neoforged.neoforge.client.gui.ModListScreen` with the public `new ModListScreen(Screen)`.
  - `new DynamicTexture(Supplier<String>, NativeImage)` → `new DynamicTexture(NativeImage)`.
  - `OptionsScreen(Screen, Options, boolean)` → `OptionsScreen(Screen, Options)`;
    `LanguageSelectScreen(Screen, Options)` → `LanguageSelectScreen(Screen, Options, LanguageManager)`;
    `AbstractWidget.setBounds(...)` → the four `setX/setY/setWidth/setHeight`;
    `Button` inner-class ctor → the 1.21.1 `Button(int, int, int, int, Component, OnPress, CreateNarration)`.
- Build: `net.neoforged.moddev` `2.0.142` retargeted to NeoForge 21.1.249 / Java 21;
  `modLoader` / `loaderVersion` added to `neoforge.mods.toml`.
- Verified: `./gradlew build` OK; `./gradlew runClient` loads — `Mixing LoadingOverlayMixin into
  LoadingOverlay` and `Mixing TitleScreenMixin into TitleScreen`, all `@Inject` (onInit, onRender,
  onTick) recognized, the `ImageButton` inner class generated, `SkdMenu` logs "client setup
  complete" and installs the early-display theme, 0 FATAL, clean shutdown. Not yet reviewed
  visually on screen.
