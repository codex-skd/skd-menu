# Graph Report - 1.21.1  (2026-09-08)

## Corpus Check
- 27 files · ~296,354 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 231 nodes · 421 edges · 37 communities (24 shown, 13 thin omitted)
- Extraction: 100% EXTRACTED · 0% INFERRED · 0% AMBIGUOUS · INFERRED: 1 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `6f682f05`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- GUI Components
- Screen Layout
- Mod Configuration
- Menu Configuration
- Mod Initialization
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
3. `TextureResolver` - 13 edges
4. `Flujo de trabajo — SKD Menu (NeoForge)` - 11 edges
5. `Configuración de `skd_menu`` - 10 edges
6. `Port Report: SKD Menu 26.2 → 1.21.1 / NeoForge 21.1.249` - 9 edges
7. `BackgroundConfig` - 7 edges
8. `ThirdPartyButtons` - 7 edges
9. `SKD Menu` - 7 edges
10. `Project Variables — SKD Menu (1.21.1)` - 7 edges

## Surprising Connections (you probably didn't know these)
- `TitleScreenMixin` --references--> `Position`  [EXTRACTED]
  src/main/java/com/skd/menu/mixin/TitleScreenMixin.java → src/main/java/com/skd/menu/MenuConfig.java

## Import Cycles
- None detected.

## Communities (37 total, 13 thin omitted)

### Community 0 - "GUI Components"
Cohesion: 0.22
Nodes (8): GuiGraphics, Minecraft, Override, DefaultButton, ResourceLocation, TitleScreenMixin, TitleScreen, Unique

### Community 1 - "Screen Layout"
Cohesion: 0.16
Nodes (9): AbstractWidget, Button, CallbackInfo, Component, Inject, OnPress, Position, ImageButton (+1 more)

### Community 4 - "Mod Configuration"
Cohesion: 0.50
Nodes (4): BooleanValue, Builder, ModConfigSpec, Config

### Community 5 - "Menu Configuration"
Cohesion: 0.21
Nodes (18): Gson, AnimationConfig, BackgroundConfig, ButtonAnimation, ButtonsConfig, ColorConfig, CustomButton, EffectConfig (+10 more)

### Community 6 - "Mod Initialization"
Cohesion: 0.36
Nodes (6): FMLClientSetupEvent, EventBusSubscriber, Mod, ModContainer, SubscribeEvent, SkdMenuClient

### Community 8 - "Texture Handling"
Cohesion: 0.29
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
Cohesion: 0.12
Nodes (15): [0.0.0-beta.1] - 2026-09-02, [0.0.0-beta.2] - 2026-09-02, [0.0.0-beta.3] - 2026-09-02, [0.0.0-beta.4] - 2026-09-03, [0.0.0-beta.5] - 2026-09-08, Added, Added, Added (+7 more)

### Community 32 - "Project Variables — SKD Menu (1.21.1)"
Cohesion: 0.25
Nodes (7): Historial, Optional, Project Variables — SKD Menu (1.21.1), Rama, Repo GitLab, Required, Tag

### Community 33 - "CLAUDE.md — skd_menu (26.2)"
Cohesion: 0.50
Nodes (3): CLAUDE.md — skd_menu (26.2), Prioridad de instrucciones, Workflow del mod

## Knowledge Gaps
- **71 isolated node(s):** `Workflow del mod`, `Prioridad de instrucciones`, `Added`, `Removed`, `Fixed` (+66 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **13 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `MenuConfig` connect `Menu Configuration` to `GUI Components`, `Screen Layout`, `.onScreenInitPost`, `Texture Handling`?**
  _High betweenness centrality (0.067) - this node is a cross-community bridge._
- **Why does `TitleScreenMixin` connect `GUI Components` to `Texture Handling`, `Screen Layout`?**
  _High betweenness centrality (0.033) - this node is a cross-community bridge._
- **What connects `Workflow del mod`, `Prioridad de instrucciones`, `Added` to the rest of the system?**
  _71 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Project Variables` be split into smaller, more focused modules?**
  _Cohesion score 0.08333333333333333 - nodes in this community are weakly interconnected._
- **Should `Port Report: SKD Menu 26.2 → 1.21.1 / NeoForge 21.1.249` be split into smaller, more focused modules?**
  _Cohesion score 0.125 - nodes in this community are weakly interconnected._
- **Should `[0.0.0-beta.2] - 2026-09-02` be split into smaller, more focused modules?**
  _Cohesion score 0.125 - nodes in this community are weakly interconnected._