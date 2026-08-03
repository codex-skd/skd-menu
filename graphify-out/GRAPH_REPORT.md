# Graph Report - 26.2  (2026-08-03)

## Corpus Check
- 43 files · ~427,071 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 336 nodes · 518 edges · 52 communities (39 shown, 13 thin omitted)
- Extraction: 100% EXTRACTED · 0% INFERRED · 0% AMBIGUOUS
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `875a5134`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- MenuConfig
- TitleScreenMixin
- Unique
- SKDMenuClient.java
- SKDMenu.java
- Config
- ScreenInvoker.java
- gradlew
- build.gradle
- settings.gradle
- Changelog — SKD Menu
- Configuración de `skd_menu`
- TextureResolver
- CurseForge — Variables del proyecto
- CLAUDE.md — skd_menu (26.2)
- Builder
- Mixin
- Builder
- CallbackInfo
- GuiGraphicsExtractor
- Identifier
- ImageConfig
- Inject
- LogoConfig
- Minecraft
- Mixin
- Unique
- background
- theme-skd.json

## God Nodes (most connected - your core abstractions)
1. `TitleScreenMixin` - 31 edges
2. `MenuConfig` - 28 edges
3. `Changelog — SKD Menu` - 21 edges
4. `LoadingConfig` - 17 edges
5. `LoadingOverlayMixin` - 17 edges
6. `TextureResolver` - 13 edges
7. `background` - 12 edges
8. `Flujo de trabajo — SKD Menu (NeoForge)` - 11 edges
9. `skdBanner` - 10 edges
10. `Configuración de `skd_menu`` - 10 edges

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

## Communities (52 total, 13 thin omitted)

### Community 0 - "MenuConfig"
Cohesion: 0.19
Nodes (17): AnimationConfig, BackgroundConfig, ButtonAnimation, ButtonsConfig, ColorConfig, CustomButton, DefaultButton, EffectConfig (+9 more)

### Community 1 - "TitleScreenMixin"
Cohesion: 0.14
Nodes (4): AbstractWidget, TitleScreenMixin, Position, TitleScreen

### Community 2 - "Unique"
Cohesion: 0.17
Nodes (11): Buenas prácticas, Commits (Conventional Commits), Convenciones de nomenclatura, Específico del mod, Estructura del proyecto, Flujo de trabajo — SKD Menu (NeoForge), Flujo por tarea, Idioma (+3 more)

### Community 3 - "SKDMenuClient.java"
Cohesion: 0.19
Nodes (8): EventBusSubscriber, FMLClientSetupEvent, EarlyDisplayInstaller, Logger, Mod, ModContainer, SkdMenuClient, SubscribeEvent

### Community 4 - "SKDMenu.java"
Cohesion: 0.48
Nodes (5): IEventBus, Logger, Mod, ModContainer, SkdMenu

### Community 5 - "Config"
Cohesion: 0.67
Nodes (3): BooleanValue, Config, ModConfigSpec

### Community 7 - "gradlew"
Cohesion: 0.23
Nodes (9): ReloadInstance, CallbackInfo, GuiGraphicsExtractor, ImageConfig, Inject, Minecraft, Mixin, Unique (+1 more)

### Community 8 - "build.gradle"
Cohesion: 0.10
Nodes (19): `background`, `buttonAnimation`, `buttons`, `buttons.custom`, `buttons.defaults`, `buttons.globalImage`, Configuración de `skd_menu`, Cuándo se aplican los cambios (+11 more)

### Community 9 - "settings.gradle"
Cohesion: 0.31
Nodes (10): BackgroundConfig, BannerConfig, BarConfig, ColorConfig, FillConfig, ImageConfig, Gson, Logger (+2 more)

### Community 10 - "Changelog — SKD Menu"
Cohesion: 0.04
Nodes (45): 0.0.0-beta.10 (2026-07-27), 0.0.0-beta.11 (2026-07-29), 0.0.0-beta.12 (2026-07-29), 0.0.0-beta.13 (2026-07-29), 0.0.0-beta.14 (2026-07-29), 0.0.0-beta.15 (2026-07-30), 0.0.0-beta.1 (2026-07-27), 0.0.0-beta.2 (2026-07-27) (+37 more)

### Community 11 - "Configuración de `skd_menu`"
Cohesion: 0.22
Nodes (8): Changelog, CurseForge — Variables del proyecto, Proyecto, Rama, Tag, Tokens, Variables para script (lectura automática), Verificación con GET

### Community 12 - "TextureResolver"
Cohesion: 0.22
Nodes (8): Button, ImageButton, HttpClient, NativeImage, Override, Identifier, Logger, TextureResolver

### Community 13 - "CurseForge — Variables del proyecto"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 15 - "CLAUDE.md — skd_menu (26.2)"
Cohesion: 0.50
Nodes (3): CLAUDE.md — skd_menu (26.2), Prioridad de instrucciones, Workflow del mod

### Community 49 - "background"
Cohesion: 0.06
Nodes (33): bottom, centerHorizontally, centerVertically, font, left, maintainAspectRatio, right, top (+25 more)

### Community 50 - "theme-skd.json"
Cohesion: 0.12
Nodes (24): default, texture, colorScheme, screenBackground, text, extends, resource, scaling (+16 more)

## Knowledge Gaps
- **93 isolated node(s):** `version`, `extends`, `default`, `screenBackground`, `text` (+88 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **13 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `MenuConfig` connect `MenuConfig` to `TitleScreenMixin`, `TextureResolver`, `settings.gradle`?**
  _High betweenness centrality (0.051) - this node is a cross-community bridge._
- **Why does `LoadingConfig` connect `settings.gradle` to `gradlew`?**
  _High betweenness centrality (0.034) - this node is a cross-community bridge._
- **Why does `TitleScreenMixin` connect `TitleScreenMixin` to `TextureResolver`?**
  _High betweenness centrality (0.023) - this node is a cross-community bridge._
- **What connects `version`, `extends`, `default` to the rest of the system?**
  _93 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `TitleScreenMixin` be split into smaller, more focused modules?**
  _Cohesion score 0.14285714285714285 - nodes in this community are weakly interconnected._
- **Should `build.gradle` be split into smaller, more focused modules?**
  _Cohesion score 0.09523809523809523 - nodes in this community are weakly interconnected._
- **Should `Changelog — SKD Menu` be split into smaller, more focused modules?**
  _Cohesion score 0.043478260869565216 - nodes in this community are weakly interconnected._