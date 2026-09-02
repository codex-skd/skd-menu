# SKD Menu (1.21.1) — Changelog

Branch `minecraft/1.21.1/neoforge-21.1.249/production`. History independent of the 26.2 branch.

## [0.0.0-beta.3] - 2026-09-02

### Fixed

- **Custom loading screen never closed on an in-game resource reload** (server resource pack,
  `F3+T`, or changing a resource pack / shader pack from the options). `LoadingOverlayMixin`
  replaced `LoadingOverlay#render` wholesale and cancelled it, but left out the vanilla
  reload-finished block, so `fadeOutStart` was never set and the `onFinish` callback never fired:
  the overlay stayed up forever with a black background and a full progress bar, hanging the game.
  Restored that block (`reload.checkExceptions()` → `onFinish.accept(...)` → `fadeOutStart` →
  `screen.init`). The bug never showed at startup because NeoForge uses a `LoadingOverlay`
  subclass there that overrides `render`, so the mixin only takes effect on later plain-overlay
  reloads.
- **Black background on those reload screens.** `TextureResolver.dimensions()` returned `null`
  while the `ResourceManager` was mid-rebuild, and `renderCoverImage` then blitted a 1×1 source
  region stretched to full screen — a solid near-black rectangle. Dimensions are now read from the
  classpath first (`/assets/<namespace>/<path>`, reload-independent), and when they are still
  unknown the image is drawn with a plain full-texture stretch blit instead of the 1×1 path.

### Changed

- **`EarlyDisplayInstaller` is now a no-op on this branch.** NeoForge 21.1.x / FancyModLoader
  1.21.1 has no `earlyLoadingScreenTheme` config key and no JSON early-display theme system (that
  arrived in a later NeoForge), so the installer had been writing a key FML strips on every launch
  (`Incorrect key [earlyLoadingScreenTheme] was corrected from skd to null`) and copying unused
  files into `config/fml/`. The in-game loading / resource-reload screen is still fully themed; the
  FML "early window" shown while mods load is not themeable on this Minecraft version. The 26.2
  branch keeps the full installer.

## [0.0.0-beta.2] - 2026-09-02

### Added

- **Third-party title-screen button control** (`buttons.thirdParty` in `menu.json`). Buttons that
  other mods add to the vanilla title screen (Create, Quark, Configured, Catalogue, or any other)
  are now detected generically — any widget whose class is not in the `net.minecraft.` or
  `com.skd.menu.` package — and can be repositioned, resized or hidden. By default they are stacked
  in a vertical column on the left (`stackX` / `stackY` / `stackGap` / `stackFromBottom`, all
  percentage-based like the rest of the config). Per-button `entries` are auto-generated into
  `menu.json` the first time each button is seen, keyed by a slug derived from the owning mod
  (`create`, `quark`, …; `-2` / `-3` suffixes when one mod adds several). Setting both `x` and `y`
  `>= 0` pins a button and takes it out of the stack; `hide: true` removes it.

### Removed

- The dead **`friends`** default button. It was carried over from the 26.2 line, but Minecraft
  1.21.1 has no Friends feature, so the mod was generating a non-functional button that rendered the
  raw `gui.friends.open` translation key. Dropped from `MenuConfig` defaults, `TitleScreenMixin`
  (`ICON_ROW_IDS` / `vanillaKey` / `runDefaultAction`) and `docs/CONFIG.md`. An existing `menu.json`
  that still lists `friends` is silently ignored — no migration needed.

### Technical

- New `ThirdPartyButtons` class: a client `ScreenEvent.Init.Post` subscriber at `EventPriority.LOWEST`.
  Third-party mods add their title-screen buttons on that event, after `TitleScreen.init()` — i.e.
  after `TitleScreenMixin` has already placed the vanilla + custom buttons — so their handling runs
  as a separate low-priority pass over `Screen#children()`.

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
