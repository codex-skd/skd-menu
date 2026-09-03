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

- v0.0.0-beta.4: CurseForge file 8798757 (project 1626937, beta). Eliminada la feature de pantalla
  de carga custom en esta rama. `LoadingOverlayMixin` reemplazaba el overlay de carga vanilla
  entero, pero en un modpack real sus dibujos con textura (fondo, banner, logo, marco de barra) no
  renderizaban nada durante el reload de recursos en caliente (aplicación del resource pack del
  servidor al entrar, y otra vez al desconectar): solo se veían las primitivas `GuiGraphics.fill`
  → pantalla negra con la barra pelada. La sonda de diagnóstico confirmó que la textura resolvía a
  un `DynamicTexture` registrado con estado GL limpio (`shaderColor` blanco, shader `position_tex`
  no null, `glGetError` 0); los `blit` se seguían tragando solo en la ruta del overlay (el mismo
  `blit` funciona en el title screen), apuntando a una interacción de batching de `GuiGraphics` con
  otro mod de cliente. Eliminados: `LoadingOverlayMixin` (fuera de `skd_menu.mixins.json`),
  `LoadingConfig` (`config/skd_menu/loading.json`), `EarlyDisplayInstaller` (ya no-op en 21.1.x),
  recursos `earlydisplay/` y `textures/gui/bar_track.png`. `TextureResolver` y `SkdMenuClient`
  vuelven a su estado de beta.3. Los reloads del pack de servidor ahora usan la pantalla de carga
  vanilla de NeoForge. La customización del title screen intacta. La rama 26.2 conserva la feature.
  `clean build` LIMPIO. Verificado in-game: title screen sigue con su fondo/botones custom, y el
  join/disconnect del servidor muestra la pantalla vanilla.

- v0.0.0-beta.3: CurseForge file 8795253 (project 1626937, beta). Fix del cuelgue de la pantalla
  de carga en reloads de recursos en caliente (resource pack del servidor, F3+T, cambio de pack /
  shader desde Opciones): `LoadingOverlayMixin` reemplazaba `LoadingOverlay#render` entero y
  cancelaba, pero omitía el bloque de finalización del vanilla → `fadeOutStart` nunca se ponía y
  `onFinish` nunca se llamaba → overlay eterno con fondo negro y barra al 100 %. Restaurado ese
  bloque. + Fondo negro en esas pantallas: `TextureResolver.dimensions()` devolvía `null` con el
  ResourceManager reconstruyéndose → `renderCoverImage` hacía un blit de región 1×1 estirada;
  ahora lee dimensiones del classpath primero y cae a stretch de textura completa si aún no las
  tiene. + `EarlyDisplayInstaller` pasa a no-op en 1.21.1 (NeoForge 21.1.x / FML 1.21.1 no tiene
  `earlyLoadingScreenTheme` ni sistema de themes JSON de early display; escribía una clave que FML
  borra cada arranque y copiaba ficheros sin uso a `config/fml/`). `clean build` LIMPIO en las dos
  ramas (recompilado forzado + AP de mixins + jar). No revisado in-game contra un resource pack de
  servidor.

- v0.0.0-beta.2: CurseForge file 8791065 (project 1626937, beta). Feature `buttons.thirdParty`:
  control genérico de los botones que otros mods (Create, Quark, Configured, Catalogue, cualquiera)
  añaden al TitleScreen — detección por clase no-`net.minecraft.`/no-`com.skd.menu.`, reposición /
  redimensión / ocultar vía `menu.json`, pila vertical a la izquierda por defecto, `entries`
  autogeneradas. Clase nueva `ThirdPartyButtons` (`ScreenEvent.Init.Post` a `EventPriority.LOWEST`,
  corre tras `TitleScreenMixin`). Eliminado el botón `friends` por defecto (sin equivalente en MC
  1.21.1; generaba un botón muerto con la clave cruda `gui.friends.open`). `clean build` LIMPIO.
  No revisado visualmente en pantalla con esos mods instalados.

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
