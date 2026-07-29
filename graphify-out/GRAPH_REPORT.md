# Graph Report - 26.2  (2026-07-29)

## Corpus Check
- 33 files · ~594,701 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 243 nodes · 413 edges · 32 communities
- Extraction: 100% EXTRACTED · 0% INFERRED · 0% AMBIGUOUS
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `1d81d845`
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
- Changelog — SKD Menu
- Configuración de `skd_menu`
- TextureResolver
- CurseForge — Variables del proyecto
- Formato de descripciones CurseForge
- CLAUDE.md — skd_menu (26.2)
- ImageButton

## God Nodes (most connected - your core abstractions)
1. `TitleScreenMixin` - 33 edges
2. `MenuConfig` - 28 edges
3. `Changelog — SKD Menu` - 15 edges
4. `TextureResolver` - 13 edges
5. `Flujo de trabajo — SKD Menu (NeoForge)` - 13 edges
6. `Publicación a GitHub (CI/CD)` - 11 edges
7. `Configuración de `skd_menu`` - 10 edges
8. `CurseForge — Variables del proyecto` - 8 edges
9. `BackgroundConfig` - 7 edges
10. `Formato de descripciones CurseForge` - 7 edges

## Surprising Connections (you probably didn't know these)
- `TitleScreenMixin` --references--> `Position`  [EXTRACTED]
  src/main/java/com/skd/menu/mixin/TitleScreenMixin.java → src/main/java/com/skd/menu/MenuConfig.java

## Import Cycles
- None detected.

## Communities (32 total, 0 thin omitted)

### Community 0 - "MenuConfig"
Cohesion: 0.25
Nodes (16): Gson, AnimationConfig, BackgroundConfig, ButtonAnimation, ButtonsConfig, ColorConfig, CustomButton, EffectConfig (+8 more)

### Community 1 - "TitleScreenMixin"
Cohesion: 0.19
Nodes (8): AbstractWidget, GuiGraphicsExtractor, Minecraft, DefaultButton, Position, TitleScreenMixin, TitleScreen, Unique

### Community 2 - "Unique"
Cohesion: 0.06
Nodes (34): 1. Desarrollo, 2. Copiar a instancia de pruebas, 3. Probar en instancia, 4. Preparar versión para CurseForge, 5. Release estable, 6. Actualizar Knowledge Graph (Graphify), Archivos que pasan a GitHub, Buenas prácticas (+26 more)

### Community 3 - "SKDMenuClient.java"
Cohesion: 0.36
Nodes (6): EventBusSubscriber, FMLClientSetupEvent, Mod, ModContainer, SKDMenuClient, SubscribeEvent

### Community 4 - "SKDMenu.java"
Cohesion: 0.48
Nodes (5): IEventBus, Logger, Mod, ModContainer, SKDMenu

### Community 5 - "Config"
Cohesion: 0.50
Nodes (4): BooleanValue, ModConfigSpec, Config, Builder

### Community 6 - "ScreenInvoker.java"
Cohesion: 0.60
Nodes (3): Invoker, Mixin, ScreenInvoker

### Community 7 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 10 - "Changelog — SKD Menu"
Cohesion: 0.06
Nodes (32): 0.0.0-beta.10 (2026-07-27), 0.0.0-beta.11 (2026-07-29), 0.0.0-beta.12 (2026-07-29), 0.0.0-beta.13 (2026-07-29), 0.0.0-beta.14 (2026-07-29), 0.0.0-beta.1 (2026-07-27), 0.0.0-beta.2 (2026-07-27), 0.0.0-beta.3 (2026-07-27) (+24 more)

### Community 11 - "Configuración de `skd_menu`"
Cohesion: 0.10
Nodes (18): `background`, `buttonAnimation`, `buttons`, `buttons.custom`, `buttons.defaults`, `buttons.globalImage`, Configuración de `skd_menu`, Cuándo se aplican los cambios (+10 more)

### Community 12 - "TextureResolver"
Cohesion: 0.27
Nodes (5): HttpClient, NativeImage, Identifier, Logger, TextureResolver

### Community 13 - "CurseForge — Variables del proyecto"
Cohesion: 0.22
Nodes (8): Changelog, CurseForge — Variables del proyecto, Proyecto, Rama, Tag, Tokens, Variables para script (lectura automática), Verificación con GET

### Community 14 - "Formato de descripciones CurseForge"
Cohesion: 0.22
Nodes (9): Archivos de CurseForge, Buenas prácticas, Ejemplo de estructura HTML para release notes, Elementos HTML disponibles, Elementos HTML permitidos, Estructura de la descripción general, Estructura del proyecto, Formato de descripciones CurseForge (+1 more)

### Community 15 - "CLAUDE.md — skd_menu (26.2)"
Cohesion: 0.50
Nodes (3): CLAUDE.md — skd_menu (26.2), Paso 0 obligatorio, Prioridad de instrucciones

### Community 28 - "ImageButton"
Cohesion: 0.17
Nodes (8): Button, CallbackInfo, Inject, Override, ImageButton, Builder, Identifier, Mixin

## Knowledge Gaps
- **75 isolated node(s):** `Paso 0 obligatorio`, `Prioridad de instrucciones`, `Fix`, `Changes`, `Fix` (+70 more)
  These have ≤1 connection - possible missing edges or undocumented components.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `MenuConfig` connect `MenuConfig` to `TitleScreenMixin`, `ImageButton`, `TextureResolver`?**
  _High betweenness centrality (0.049) - this node is a cross-community bridge._
- **Why does `Flujo de trabajo — SKD Menu (NeoForge)` connect `Unique` to `Formato de descripciones CurseForge`?**
  _High betweenness centrality (0.027) - this node is a cross-community bridge._
- **Why does `TitleScreenMixin` connect `TitleScreenMixin` to `ImageButton`, `TextureResolver`?**
  _High betweenness centrality (0.027) - this node is a cross-community bridge._
- **What connects `Paso 0 obligatorio`, `Prioridad de instrucciones`, `Fix` to the rest of the system?**
  _75 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Unique` be split into smaller, more focused modules?**
  _Cohesion score 0.05714285714285714 - nodes in this community are weakly interconnected._
- **Should `Changelog — SKD Menu` be split into smaller, more focused modules?**
  _Cohesion score 0.06060606060606061 - nodes in this community are weakly interconnected._
- **Should `Configuración de `skd_menu`` be split into smaller, more focused modules?**
  _Cohesion score 0.1 - nodes in this community are weakly interconnected._