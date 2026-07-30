# Changelog — SKD Menu

## 0.0.0-beta.15 (2026-07-30)

### Fix
- Fixed HTTP(S) image URLs never actually applying to buttons/backgrounds/logos: the async download callback was registering the GPU texture off the render thread, which silently fails (wrong GL context). Texture registration now runs on the render thread.

### Features
- `hideText` on `buttons.defaults`/`buttons.custom`: hides the button's label, showing only its image
- Buttons with a custom image now react to hover/focus (translucent highlight) and disabled state (darken), matching vanilla button feedback — previously a custom image looked static regardless of mouse state
- New default button layout and images: fixed positions/sizes for singleplayer, multiplayer, options, quit, friends, mods (percentage-based, matching a reference in-game layout); realms/language/accessibility hidden by default; `buttons.globalImage` now defaults to a bundled button texture (`button_default.png`)

## 0.0.0-beta.14 (2026-07-29)

### Fix
- Fixed a client crash (`IllegalArgumentException: Scissor ... is out of bounds`) that could happen with `buttonAnimation.type: "slide_right"` (the default) when a button's configured width is too narrow for its label. Minecraft auto-scrolls text that doesn't fit a button, and if that button is also positioned off the left edge of the screen (as it briefly is during the slide-in animation), the resulting clipping rectangle went negative, which this Minecraft version rejects outright instead of clamping. The slide animation now never pushes a button's X below 0. Also enforced a 20px minimum button width (`buttons.defaults[].width`, `buttons.custom[].width`) as a second safety net.

## 0.0.0-beta.13 (2026-07-29)

### Changes
- **Breaking**: `x`/`y` fields (logos, title, image overlays, default buttons, custom buttons) are now a **percentage (0-100)** of the current window width/height instead of pixels. A fixed position now scales proportionally when the window is resized, instead of staying at a fixed pixel that ends up out of place at a different window size. `-1`/`-999` sentinel values ("automatic"/"hidden") are unchanged. `width`/`height` fields remain in pixels.
- Added `width` to `buttons.defaults` (pixels, `-1` = automatic default width per button)

## 0.0.0-beta.12 (2026-07-29)

### Fix
- Default buttons (`buttons.defaults`) no longer depend on the game's language to be identified. Previously they were matched by comparing the vanilla button's *translated* text against an English substring (e.g. `"singleplayer"`), which silently failed in any non-English locale. Now the mod hides every vanilla title-screen button by class (`Button`/`SpriteIconButton`) regardless of text, and builds its own replacement buttons using Minecraft's own translation keys (`menu.singleplayer`, `menu.multiplayer`, `menu.online`, `menu.options`, `menu.quit`, `gui.friends.open`, `options.language`, `accessibility.onboarding.accessibility.button`, `fml.menu.mods`) and the exact vanilla actions, so labels stay correctly localized and positioning/hiding/custom images work in any language

### Changes
- Added `friends` as a 9th recognized default button ID; dropped the dev-only `tw` badge (only ever shown when running from an IDE, never in a real build) from the recognized ID list — no longer needed since it's never present for players
- `realms` is no longer hidden by default (the overlap bug that caused this in beta.11 no longer exists now that vanilla buttons are always fully replaced)

## 0.0.0-beta.11 (2026-07-29)

### Features
- Fixed single background image with a smooth zoom ("breathing") effect, replacing the rotating-frame background as the default (`background.type: "image"`, `background.image.effect`)
- Aspect-ratio preserving `cover` fit for the background image (`background.image.fit`), avoiding distortion on small/uncommon window sizes
- Two fixed corner logos, independently configurable (`logos.topLeft`, `logos.topRight`)
- Image references (background, logos, title, overlays, button images) now support 3 sources: mod-bundled asset, local file path, or `http(s)://` URL (`TextureResolver`)
- `buttons.defaults` is now pre-populated with all 8 recognized button IDs (`singleplayer`, `multiplayer`, `realms`, `options`, `quit`, `language`, `accessibility`, `mods`), each defaulting to automatic positioning (`x`/`y`: `-1`) and `hide: false`
- Per-button custom image (`buttons.defaults[].image`, `buttons.custom[].image`) and a generic fallback image for all buttons (`buttons.globalImage`)
- New `docs/CONFIG.md` with the full config reference

### Changes
- Custom buttons (`buttons.custom`) without both `x` and `y` explicitly defined are now hidden (not created), instead of auto-stacking below the default buttons
- New default images: fixed background, top-left logo, and top-right logo (AI-generated)
- Center title is now hidden by default (`title.type: "hidden"`) instead of showing a leftover placeholder image
- Recognized default button IDs now include `realms` and `tw` (a NeoForge dev/test badge); both are hidden by default, fixing an overlap where these untracked vanilla widgets sat on top of the repositioned Multiplayer/Realms buttons

## 0.0.0-beta.10 (2026-07-27)

### Features
- New default background images: Codex epic landscape, dragon storm, ancient library

## 0.0.0-beta.9 (2026-07-27)

### Features
- Custom Stalking Dragons logo as default title image
- Title image animated zoom/pulse effect (`title.animated`, `animRange`, `animSpeed`)
- Graphify knowledge graph generated

## 0.0.0-beta.8 (2026-07-27)

### Features
- Title system: `type` can be `"image"`, `"vanilla"`, or `"hidden"`. Default: custom image with dragon logo at top-left
- Image overlays now support `enabled` flag and auto top-right positioning (x=-1, y=-1)
- Button system restructured: `buttons.defaults` list with per-button hide/position, custom buttons auto-place below defaults

## 0.0.0-beta.7 (2026-07-27)

### Changes
- Configs reorganizados: `config/skd_menu/menu.json` + `config/skd_menu/menu-common.toml`
- Template `neoforge.mods.toml` con descripción real, autores y créditos
- Añadido `temp/` y `graphify-out/`
- Audit items marcados como completados

## 0.0.0-beta.6 (2026-07-27)

### Fix
- Crash on launch: `extractPanorama` method doesn't exist on `TitleScreen`. Removed injection, now cancels `extractRenderState` directly instead.

## 0.0.0-beta.5 (2026-07-27)

### Fix
- Corrected `blit()` UV coordinates for `GuiGraphicsExtractor` (new 26.2 API uses normalized float UV 0.0–1.0, not pixel coords)

## 0.0.0-beta.4 (2026-07-27)

### Features
- Default animated background with 3 AI-generated fantasy images (cycling every 8s)
- Custom panorama config structure

## 0.0.0-beta.3 (2026-07-27)

### Features
- Custom 360° panorama: 6-image cube panorama (`background.type: "custom_panorama"`)
- Button slide-right animation (`buttonAnimation.type: "slide_right"`)
- External image loading for panorama faces and backgrounds

### Changes
- Config renamed from `skd_menu_menu.json` to `skd_menu.json`

## 0.0.0-beta.2 (2026-07-27)

### Fix
- NeoForge dependency changed from 26.2.0.35-beta to 26.2.0.32-beta for compatibility

## 0.0.0-beta.1 (2026-07-27)

### Features
- Custom main menu with JSON config (`config/skd_menu.json`)
- Background types: panorama, image, solid color, animated frames
- Button position customization for all vanilla buttons
- Hide unwanted buttons
- Custom buttons with actions (command, open screen, URL, quit)
- Image overlays with configurable position and scale
- NeoForge 26.2 / Minecraft 26.2
- Client-side only
