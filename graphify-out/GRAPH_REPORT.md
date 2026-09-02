# Graph Report - .  (2026-09-02)

## Corpus Check
- cluster-only mode — file stats not available

## Summary
- 239 nodes · 515 edges · 28 communities (14 shown, 14 thin omitted)
- Extraction: 100% EXTRACTED · 0% INFERRED · 0% AMBIGUOUS
- Token cost: 713 input · 275 output

## Graph Freshness
- Built from commit: `78111217`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- GUI Components
- Screen Layout
- Loading Screen
- Theme Configuration
- Mod Configuration
- Menu Configuration
- Mod Initialization
- Loading Configuration
- Texture Handling
- Mod Event Handling
- Screen Invocation
- Build Scripts
- Gradle Properties
- Project Variables
- SKD Menu Version
- Bar Track Image
- Main Background Image
- Default Button Image
- Top Left Logo Image
- Top Right Logo Image
- Title Image
- Early Display Background Image
- Early Display Top Left Logo Image
- Progress Bar Background Image
- Progress Bar Foreground Image
- SKD Menu Image

## God Nodes (most connected - your core abstractions)
1. `TitleScreenMixin` - 34 edges
2. `MenuConfig` - 28 edges
3. `LoadingConfig` - 17 edges
4. `LoadingOverlayMixin` - 17 edges
5. `TextureResolver` - 13 edges
6. `background` - 12 edges
7. `skdBanner` - 10 edges
8. `loadingScreen` - 7 edges
9. `EarlyDisplayInstaller` - 6 edges
10. `BackgroundConfig` - 6 edges

## Surprising Connections (you probably didn't know these)
- `MenuConfig` --references--> `BackgroundConfig`  [EXTRACTED]
  src/main/java/com/skd/menu/MenuConfig.java → src/main/java/com/skd/menu/LoadingConfig.java
- `BackgroundConfig` --references--> `ImageConfig`  [EXTRACTED]
  src/main/java/com/skd/menu/MenuConfig.java → src/main/java/com/skd/menu/LoadingConfig.java
- `BackgroundConfig` --references--> `ColorConfig`  [EXTRACTED]
  src/main/java/com/skd/menu/MenuConfig.java → src/main/java/com/skd/menu/LoadingConfig.java
- `LogosConfig` --references--> `LogoConfig`  [EXTRACTED]
  src/main/java/com/skd/menu/MenuConfig.java → src/main/java/com/skd/menu/LoadingConfig.java
- `TitleScreenMixin` --references--> `Position`  [EXTRACTED]
  src/main/java/com/skd/menu/mixin/TitleScreenMixin.java → src/main/java/com/skd/menu/MenuConfig.java

## Import Cycles
- None detected.

## Communities (28 total, 14 thin omitted)

### Community 0 - "GUI Components"
Cohesion: 0.17
Nodes (11): AbstractWidget, DefaultButton, Position, GuiGraphics, ImageConfig, LogoConfig, Minecraft, ResourceLocation (+3 more)

### Community 1 - "Screen Layout"
Cohesion: 0.06
Nodes (33): bottom, centerHorizontally, centerVertically, font, left, maintainAspectRatio, right, top (+25 more)

### Community 2 - "Loading Screen"
Cohesion: 0.21
Nodes (10): Override, ReloadInstance, CallbackInfo, GuiGraphics, ImageConfig, Inject, Minecraft, Mixin (+2 more)

### Community 3 - "Theme Configuration"
Cohesion: 0.12
Nodes (24): default, texture, colorScheme, screenBackground, text, extends, resource, scaling (+16 more)

### Community 4 - "Mod Configuration"
Cohesion: 0.15
Nodes (11): BooleanValue, Builder, Button, Component, ModConfigSpec, OnPress, Config, ImageButton (+3 more)

### Community 5 - "Menu Configuration"
Cohesion: 0.23
Nodes (16): AnimationConfig, BackgroundConfig, ButtonAnimation, ButtonsConfig, ColorConfig, CustomButton, EffectConfig, ImageConfig (+8 more)

### Community 6 - "Mod Initialization"
Cohesion: 0.19
Nodes (8): EventBusSubscriber, FMLClientSetupEvent, EarlyDisplayInstaller, Logger, Mod, ModContainer, SkdMenuClient, SubscribeEvent

### Community 7 - "Loading Configuration"
Cohesion: 0.31
Nodes (10): BackgroundConfig, BannerConfig, BarConfig, ColorConfig, FillConfig, ImageConfig, Gson, Logger (+2 more)

### Community 8 - "Texture Handling"
Cohesion: 0.26
Nodes (5): HttpClient, NativeImage, Logger, ResourceLocation, TextureResolver

### Community 9 - "Mod Event Handling"
Cohesion: 0.48
Nodes (5): IEventBus, Logger, Mod, ModContainer, SkdMenu

### Community 10 - "Screen Invocation"
Cohesion: 0.60
Nodes (3): Invoker, Mixin, ScreenInvoker

### Community 11 - "Build Scripts"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

## Knowledge Gaps
- **47 isolated node(s):** `version`, `extends`, `default`, `screenBackground`, `text` (+42 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **14 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `MenuConfig` connect `Menu Configuration` to `GUI Components`, `Texture Handling`, `Mod Configuration`, `Loading Configuration`?**
  _High betweenness centrality (0.116) - this node is a cross-community bridge._
- **Why does `LoadingConfig` connect `Loading Configuration` to `Loading Screen`?**
  _High betweenness centrality (0.067) - this node is a cross-community bridge._
- **Why does `TitleScreenMixin` connect `GUI Components` to `Texture Handling`, `Mod Configuration`?**
  _High betweenness centrality (0.043) - this node is a cross-community bridge._
- **What connects `version`, `extends`, `default` to the rest of the system?**
  _47 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Screen Layout` be split into smaller, more focused modules?**
  _Cohesion score 0.06060606060606061 - nodes in this community are weakly interconnected._
- **Should `Theme Configuration` be split into smaller, more focused modules?**
  _Cohesion score 0.12 - nodes in this community are weakly interconnected._
- **Should `Mod Configuration` be split into smaller, more focused modules?**
  _Cohesion score 0.14619883040935672 - nodes in this community are weakly interconnected._