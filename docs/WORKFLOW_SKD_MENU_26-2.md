# Flujo de trabajo — SKD Menu (NeoForge)

> **Versión del workflow**: 1.0.0
> Basado en `codex-docs/WORKFLOW_GENERIC.md` v1.2.7

## Específico del proyecto

| Propiedad | Valor |
|---|---|
| Mod ID | `skd_menu` |
| Display name | `SKD Menu` |
| Minecraft | `26.2` |
| NeoForge | `26.2.0.32-beta` |
| Package | `com.skd.menu` |
| Rama production | `minecraft/26.2/neoforge-26.2.0.32-beta/production` |
| Rama main | `minecraft/26.2/neoforge-26.2.0.32-beta/main` |
| JAR | `skd_menu-26.2-neoforge-<version>.jar` |
| Tag | `26.2-neoforge-<version>` |

## Descripción

Mod que permite personalizar completamente el menú principal de Minecraft:
- Fondos personalizados (imagen, color, animado, panorama)
- Posicionamiento de botones (singleplayer, multiplayer, options, quit, etc.)
- Ocultar botones existentes
- Botones personalizados con acciones (comandos, abrir pantallas, URLs)
- Overlays de imágenes en posiciones configurables

## Archivos clave

| Ruta | Propósito |
|---|---|
| `src/main/java/com/skd/menu/mixin/TitleScreenMixin.java` | Mixin principal sobre TitleScreen |
| `src/main/java/com/skd/menu/mixin/ScreenInvoker.java` | Invoker para addRenderableWidget |
| `src/main/java/com/skd/menu/MenuConfig.java` | Carga JSON de configuración del menú |
| `src/main/java/com/skd/menu/Config.java` | Config spec de NeoForge (enableMod, reloadJsonOnTitle) |
| `config/skd_menu_menu.json` | Config del menú (se genera automáticamente) |

## APIs de Minecraft 26.2 usadas

| API | Propósito |
|---|---|
| `Identifier` (en lugar de `ResourceLocation`) | Identificación de recursos |
| `GuiGraphicsExtractor` (en lugar de `GuiGraphics`) | Renderizado en el nuevo pipeline de extracción |
| `extractRenderState()` | Reemplazo del renderizado del título/background |
| `extractBackground()` | Overlays de imágenes |
| `init()` | Modificación de botones |
| `ScreenEvent.Opening` | Hook para reemplazar TitleScreen (vía NeoForge) |
