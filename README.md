# SKD Menu

Fully customize your Minecraft main menu for 1.21.1. Change backgrounds, reposition buttons, add custom buttons, overlay images, and restyle the loading screen — all through an easy JSON config file.

## Status

Beta (`0.0.0-beta.1`). API port of the stable 26.2 line (1.2.4) to the 1.21.1 client render API — 10 classes, 3 client mixins, no dependencies. `./gradlew build` OK; `./gradlew runClient` loads with both client mixins applied into `TitleScreen` / `LoadingOverlay`, all injects recognized, mod initializes, 0 FATAL. Not yet reviewed visually on screen.

## Features

- **Fixed Background with Zoom Effect** — a single background image with a smooth "breathing" zoom, aspect-ratio preserving (`cover` fit); panorama, solid colour, or the legacy rotating-frame background also available
- **Two Fixed Logos + Title Image** — top-left and top-right corner logos, plus a custom title image
- **Image Sources** — every image field (background, logos, title, overlays, button images) accepts a mod-bundled asset, a local file path, or an `http(s)://` URL
- **Button Layout** — reposition every vanilla button; the config is pre-populated with all recognized IDs, with `realms` and the dev-only `tw` badge hidden by default
- **Hide Buttons** — remove any vanilla button from the menu
- **Custom Buttons** — buttons that execute commands, open screens, open URLs, or quit
- **Button Images** — assign a custom image to any button, per-button or globally
- **Image Overlays** — render images at any position
- **Loading Screen** — the custom background and a themed progress bar on the resource-reload screen, plus the FML early-window theme
- **JSON Config** — all settings in `config/skd_menu/menu.json`, auto-generated on first launch. See [`docs/CONFIG.md`](docs/CONFIG.md) for the full reference.

## Requirements

| Component | Version |
|---|---|
| Minecraft | 1.21.1 |
| NeoForge | 21.1.249+ |
| Java | 21+ |

Client-only — nothing to install on the server.

## Installation

1. Download the JAR from CurseForge.
2. Place it in your `mods/` folder.
3. Launch Minecraft.
4. Edit `config/skd_menu/menu.json` to customize the menu (see [`docs/CONFIG.md`](docs/CONFIG.md)).

## Building

```bash
./gradlew build
```

The JAR is generated at `build/libs/skd_menu-1.21.1-neoforge-21.1.249-<version>.jar`.

## Credits

**Stalking Dragons** — development and design.
