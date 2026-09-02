# Project Variables — SKD Menu (1.21.1)

> **Rama 1.21.1**: `game_versions = 9638, 9639, 11779, 10150` (Client, Server, **1.21.1** id `11779`, NeoForge). `release_type = beta`. JAR `skd_menu-1.21.1-neoforge-21.1.249-<version>.jar`. Tag `1.21.1-neoforge-<version>`. Proyecto CurseForge compartido con la rama 26.2 (`1626937`).

## Required
project_id = 1626937
api_token = ee776b0a-ee95-4850-b554-06be02a8657f
game_versions = 9638, 9639, 11779, 10150
release_type = beta

## Optional
relations =

---

El script lee `project_id`, `api_token` y `game_versions` de este archivo, y `mod_id`, `mod_name`,
`minecraft_version`, `mod_version` de `gradle.properties`. Sube el JAR desde `build/libs/` con el
changelog de `docs/curseforge/versions/<version>.md`.

**game_versions**: `9638` Client · `9639` Server · `11779` Minecraft 1.21.1 · `10150` NeoForge.

## Rama
minecraft/1.21.1/neoforge-21.1.249/production

## Tag
Formato: `<mc-version>-<framework>-<version>` — Ejemplo: `1.21.1-neoforge-0.0.0-beta.1`

## Repo GitLab
git@gitlab.com:stalking-dragons/minecraft/skd-menu.git

## Historial

- v0.0.0-beta.1: CurseForge file 8789225 (project 1626937, beta). Port de API de la línea 26.2 (`1.2.4`) a Minecraft 1.21.1 / NeoForge 21.1.249.
  10 clases, 3 mixins de cliente (`TitleScreenMixin`, `LoadingOverlayMixin`, `ScreenInvoker`),
  sin dependencias. Reversiones 26.2→1.21.1: `Identifier`→`ResourceLocation`,
  `GuiGraphicsExtractor`→`GuiGraphics` (el @Inject de `extractRenderState` pasa a `render`),
  `net.minecraft.util.ARGB`→`FastColor.ARGB32`, `RenderPipelines`→blit sin pipeline,
  `net.minecraft.util.Util`→`net.minecraft.Util`, `Minecraft.screen()`→campo `Minecraft.screen`,
  `setScreenAndShow`→`setScreen`, `Minecraft.hud`→`Minecraft.gui`, ruta friends/`FriendsOverlayScreen`
  → `RealmsMainScreen` directo, `ModListScreen` de `.modlist.` → `.gui.` (constructor público),
  `DynamicTexture(Supplier<String>,img)`→`DynamicTexture(img)`, helper `blitStretch` (pose scale +
  blit público) para el blit estirado 8-arg de 26.2 que no existe en 1.21.1. `clean build` LIMPIO ·
  `runClient` carga, ambos mixins aplican (`Mixing ...Mixin into ...TitleScreen`/`...LoadingOverlay`,
  todos los @Inject reconocidos), `SkdMenu` inicializa (`client setup complete`, early display theme
  instalado), 0 FATAL, apagado limpio. No revisado visualmente en pantalla.
