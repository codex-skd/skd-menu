# SKD Menu

Fully customize your Minecraft main menu. Change backgrounds, reposition buttons, add custom buttons, and overlay images — all through an easy JSON config file.

## Features

- **Fixed Background with Zoom Effect** — a single background image with a smooth "breathing" zoom animation, aspect-ratio preserving (`cover` fit) so it never looks distorted on small windows; panorama, solid color, or the legacy rotating-frame background are also available
- **Two Fixed Logos** — top-left and top-right corner logos, each independently configurable
- **Image Sources** — every image field (background, logos, title, overlays, button images) accepts a mod-bundled asset, a local file path, or an `http(s)://` URL
- **Button Layout** — reposition every vanilla button (singleplayer, multiplayer, realms, options, quit, language, accessibility, mods); the config is pre-populated with all 9 recognized IDs out of the box, with `realms` and the dev-only `tw` badge hidden by default
- **Hide Buttons** — remove any vanilla button from the menu
- **Custom Buttons** — add new buttons that execute commands, open screens, open URLs, or quit
- **Button Images** — assign a custom image to any button, per-button or globally
- **Image Overlays** — render images at any position
- **JSON Config** — all settings in `config/skd_menu/menu.json`, auto-generated on first launch. See [`docs/CONFIG.md`](docs/CONFIG.md) for the full reference.

## Requirements

| Dependency | Version |
|---|---|
| Minecraft | 26.2 |
| NeoForge | 26.2.0.35-beta |
| Java | 25 |

## Installation

1. Download the JAR from CurseForge
2. Place it in your `mods/` folder
3. Launch Minecraft
4. Edit `config/skd_menu/menu.json` to customize the menu (see [`docs/CONFIG.md`](docs/CONFIG.md))

## Links

- [CurseForge]()
- [GitHub](https://github.com/santiagolosadaborrajo/skd-menu)
