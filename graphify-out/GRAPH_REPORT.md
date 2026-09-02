# Graph Report - 1.21.1  (2026-09-02)

## Corpus Check
- 28 files · ~429,478 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 328 nodes · 603 edges · 38 communities (25 shown, 13 thin omitted)
- Extraction: 100% EXTRACTED · 0% INFERRED · 0% AMBIGUOUS · INFERRED: 1 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `d8601356`
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
- Port Report: SKD Menu 26.2 → 1.21.1 / NeoForge 21.1.249
- Flujo de trabajo — SKD Menu (NeoForge)
- .onScreenInitPost
- [0.0.0-beta.2] - 2026-09-02
- Project Variables — SKD Menu (1.21.1)
- CLAUDE.md — skd_menu (26.2)

## God Nodes (most connected - your core abstractions)
1. `TitleScreenMixin` - 34 edges
2. `MenuConfig` - 28 edges
3. `LoadingConfig` - 17 edges
4. `LoadingOverlayMixin` - 17 edges
5. `TextureResolver` - 13 edges
6. `background` - 12 edges
7. `Flujo de trabajo — SKD Menu (NeoForge)` - 11 edges
8. `skdBanner` - 10 edges
9. `Configuración de `skd_menu`` - 10 edges
10. `Port Report: SKD Menu 26.2 → 1.21.1 / NeoForge 21.1.249` - 9 edges

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

## Communities (38 total, 13 thin omitted)

### Community 0 - "GUI Components"
Cohesion: 0.14
Nodes (18): AbstractWidget, Button, Component, OnPress, DefaultButton, MenuConfig, ImageButton, CallbackInfo (+10 more)

### Community 1 - "Screen Layout"
Cohesion: 0.06
Nodes (33): bottom, centerHorizontally, centerVertically, font, left, maintainAspectRatio, right, top (+25 more)

### Community 2 - "Loading Screen"
Cohesion: 0.23
Nodes (9): ReloadInstance, CallbackInfo, GuiGraphics, ImageConfig, Inject, Minecraft, Mixin, Unique (+1 more)

### Community 3 - "Theme Configuration"
Cohesion: 0.12
Nodes (24): default, texture, colorScheme, screenBackground, text, extends, resource, scaling (+16 more)

### Community 4 - "Mod Configuration"
Cohesion: 0.50
Nodes (4): BooleanValue, Builder, ModConfigSpec, Config

### Community 5 - "Menu Configuration"
Cohesion: 0.16
Nodes (18): AnimationConfig, BackgroundConfig, ButtonAnimation, ButtonsConfig, ColorConfig, CustomButton, EffectConfig, ImageConfig (+10 more)

### Community 6 - "Mod Initialization"
Cohesion: 0.21
Nodes (8): FMLClientSetupEvent, EarlyDisplayInstaller, Logger, EventBusSubscriber, Mod, ModContainer, SubscribeEvent, SkdMenuClient

### Community 7 - "Loading Configuration"
Cohesion: 0.31
Nodes (10): BackgroundConfig, BannerConfig, BarConfig, ColorConfig, FillConfig, ImageConfig, Gson, Logger (+2 more)

### Community 8 - "Texture Handling"
Cohesion: 0.26
Nodes (6): HttpClient, NativeImage, Override, Logger, ResourceLocation, TextureResolver

### Community 9 - "Mod Event Handling"
Cohesion: 0.48
Nodes (5): IEventBus, Logger, Mod, ModContainer, SkdMenu

### Community 10 - "Screen Invocation"
Cohesion: 0.60
Nodes (3): Invoker, Mixin, ScreenInvoker

### Community 11 - "Build Scripts"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 14 - "Project Variables"
Cohesion: 0.08
Nodes (22): `background`, `buttonAnimation`, `buttons`, `buttons.custom`, `buttons.defaults`, `buttons.globalImage`, `buttons.thirdParty`, Configuración de `skd_menu` (+14 more)

### Community 28 - "Port Report: SKD Menu 26.2 → 1.21.1 / NeoForge 21.1.249"
Cohesion: 0.12
Nodes (15): 1. `src/main/java/com/skd/menu/TextureResolver.java`, 1. `src/main/java/com/skd/menu/TextureResolver.java`, 2. `src/main/java/com/skd/menu/mixin/TitleScreenMixin.java`, 2. `src/main/java/com/skd/menu/mixin/TitleScreenMixin.java`, 3. `src/main/java/com/skd/menu/mixin/LoadingOverlayMixin.java`, 3. `src/main/java/com/skd/menu/mixin/LoadingOverlayMixin.java`, Dropped Behaviour, Files Changed (+7 more)

### Community 29 - "Flujo de trabajo — SKD Menu (NeoForge)"
Cohesion: 0.17
Nodes (11): Buenas prácticas, Commits (Conventional Commits), Convenciones de nomenclatura, Específico del mod, Estructura del proyecto, Flujo de trabajo — SKD Menu (NeoForge), Flujo por tarea, Idioma (+3 more)

### Community 30 - ".onScreenInitPost"
Cohesion: 0.27
Nodes (4): Post, EventBusSubscriber, SubscribeEvent, ThirdPartyButtons

### Community 31 - "[0.0.0-beta.2] - 2026-09-02"
Cohesion: 0.17
Nodes (11): [0.0.0-beta.1] - 2026-09-02, [0.0.0-beta.2] - 2026-09-02, [0.0.0-beta.3] - 2026-09-02, Added, Added, Changed, Fixed, Removed (+3 more)

### Community 32 - "Project Variables — SKD Menu (1.21.1)"
Cohesion: 0.25
Nodes (7): Historial, Optional, Project Variables — SKD Menu (1.21.1), Rama, Repo GitLab, Required, Tag

### Community 33 - "CLAUDE.md — skd_menu (26.2)"
Cohesion: 0.50
Nodes (3): CLAUDE.md — skd_menu (26.2), Prioridad de instrucciones, Workflow del mod

## Knowledge Gaps
- **102 isolated node(s):** `version`, `extends`, `default`, `screenBackground`, `text` (+97 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **13 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `MenuConfig` connect `GUI Components` to `Menu Configuration`, `.onScreenInitPost`, `Loading Configuration`?**
  _High betweenness centrality (0.067) - this node is a cross-community bridge._
- **Why does `LoadingConfig` connect `Loading Configuration` to `Loading Screen`?**
  _High betweenness centrality (0.035) - this node is a cross-community bridge._
- **Why does `TitleScreenMixin` connect `GUI Components` to `Menu Configuration`?**
  _High betweenness centrality (0.023) - this node is a cross-community bridge._
- **What connects `version`, `extends`, `default` to the rest of the system?**
  _102 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `GUI Components` be split into smaller, more focused modules?**
  _Cohesion score 0.13650075414781296 - nodes in this community are weakly interconnected._
- **Should `Screen Layout` be split into smaller, more focused modules?**
  _Cohesion score 0.06060606060606061 - nodes in this community are weakly interconnected._
- **Should `Theme Configuration` be split into smaller, more focused modules?**
  _Cohesion score 0.12 - nodes in this community are weakly interconnected._