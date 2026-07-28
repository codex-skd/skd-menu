# Graph Report - .  (2026-07-28)

## Corpus Check
- cluster-only mode — file stats not available

## Summary
- 80 nodes · 179 edges · 10 communities
- Extraction: 100% EXTRACTED · 0% INFERRED · 0% AMBIGUOUS
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `efa35434`
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

## God Nodes (most connected - your core abstractions)
1. `MenuConfig` - 21 edges
2. `TitleScreenMixin` - 21 edges
3. `BackgroundConfig` - 7 edges
4. `ButtonsConfig` - 5 edges
5. `SKDMenuClient` - 5 edges
6. `Config` - 4 edges
7. `Position` - 4 edges
8. `SKDMenu` - 4 edges
9. `ImageConfig` - 3 edges
10. `ColorConfig` - 3 edges

## Surprising Connections (you probably didn't know these)
- `TitleScreenMixin` --references--> `Position`  [EXTRACTED]
  src/main/java/com/skd/menu/mixin/TitleScreenMixin.java → src/main/java/com/skd/menu/MenuConfig.java

## Import Cycles
- None detected.

## Communities (10 total, 0 thin omitted)

### Community 0 - "MenuConfig"
Cohesion: 0.25
Nodes (14): Gson, AnimationConfig, BackgroundConfig, ButtonAnimation, ButtonsConfig, ColorConfig, CustomButton, DefaultButton (+6 more)

### Community 1 - "TitleScreenMixin"
Cohesion: 0.23
Nodes (8): AbstractWidget, CallbackInfo, Identifier, Inject, NativeImage, Position, Mixin, TitleScreenMixin

### Community 2 - "Unique"
Cohesion: 0.49
Nodes (3): GuiGraphicsExtractor, TitleScreen, Unique

### Community 3 - "SKDMenuClient.java"
Cohesion: 0.36
Nodes (6): EventBusSubscriber, FMLClientSetupEvent, Mod, ModContainer, SKDMenuClient, SubscribeEvent

### Community 4 - "SKDMenu.java"
Cohesion: 0.48
Nodes (5): IEventBus, Logger, Mod, ModContainer, SKDMenu

### Community 5 - "Config"
Cohesion: 0.50
Nodes (4): BooleanValue, Builder, ModConfigSpec, Config

### Community 6 - "ScreenInvoker.java"
Cohesion: 0.60
Nodes (3): Invoker, Mixin, ScreenInvoker

### Community 7 - "gradlew"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `MenuConfig` connect `MenuConfig` to `TitleScreenMixin`, `Unique`?**
  _High betweenness centrality (0.256) - this node is a cross-community bridge._
- **Why does `TitleScreenMixin` connect `TitleScreenMixin` to `Unique`?**
  _High betweenness centrality (0.091) - this node is a cross-community bridge._