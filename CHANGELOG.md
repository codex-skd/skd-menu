# Changelog — SKD Menu

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
