<h1 align="center">🖌️ SKD Menu</h1>

<p align="center"><strong>Fully customize your Minecraft main menu — backgrounds, buttons, and overlays.</strong></p>

<br>

---

<br>

<h2>✨ Overview</h2>

<p>SKD Menu lets you completely redesign the Minecraft title screen without touching a single line of code. Everything is controlled from a simple JSON config file: set a fixed background with a smooth zoom effect (or a solid color, the vanilla panorama, or an animated sequence); add two fixed corner logos; reposition or hide any button; add your own custom buttons with their own images; and overlay images anywhere on the screen.</p>

<br>

<h2>🎯 Features</h2>

<h3>🖼️ Fixed Background with Zoom Effect</h3>
<p>A single background image with a smooth, continuous zoom ("breathing") animation — no jarring slideshow. The image always keeps its proportions and fills the screen correctly, even on small or unusual window sizes. The vanilla panorama, a solid color, or the classic animated frame sequence are also available.</p>

<h3>🖼️ Two Fixed Logos</h3>
<p>Independently configurable top-left and top-right corner logos.</p>

<h3>🌐 Flexible Image Sources</h3>
<p>Every image field — background, logos, title, overlays, and button images — accepts a mod-bundled asset, a local file path, or a direct <code>http(s)://</code> URL.</p>

<h3>🔘 Button Layout</h3>
<p>Reposition every vanilla button — singleplayer, multiplayer, realms, options, quit, language, accessibility, and mods — by setting X/Y coordinates in the config. The config comes pre-filled with all 9 recognized IDs out of the box (Realms and the dev-only "TW" badge hidden by default). Hide any button you don't need.</p>

<h3>➕ Custom Buttons</h3>
<p>Add your own buttons with actions: execute in-game commands, open the singleplayer/multiplayer/options screens, open a URL in your browser, or quit the game.</p>

<h3>🖼️ Button Images</h3>
<p>Assign a custom image to any individual button, or set one generic image for every button that doesn't have its own.</p>

<h3>🖼️ Image Overlays</h3>
<p>Render images at any position on the screen with configurable width, height, and scale. Useful for logos, watermarks, or decorative elements.</p>

<h3>⚙️ JSON Config</h3>
<p>All settings live in a single <code>config/skd_menu/menu.json</code> file, auto-generated on first launch. Edit it with any text editor and reload by re-opening the title screen.</p>

<br>

<h2>📋 Requirements</h2>

<table>
<tr><td><strong>Minecraft</strong></td><td>26.2</td></tr>
<tr><td><strong>NeoForge</strong></td><td>26.2.0.35-beta</td></tr>
</table>

<br>

<h2>🎮 How to Use</h2>

<ol>
<li>Install the mod and launch Minecraft once to generate the config file.</li>
<li>Edit <code>config/skd_menu/menu.json</code> in any text editor.</li>
<li>Change the <code>background.type</code> to <code>"image"</code> (default, with zoom effect), <code>"color"</code>, <code>"panorama"</code>, or <code>"animated"</code>.</li>
<li>Set button positions and images under <code>buttons.defaults</code>.</li>
<li>Add custom buttons under <code>buttons.custom</code>.</li>
<li>Re-open the title screen to see your changes.</li>
</ol>

<br>

---

<br>

<h2>🙏 Credits</h2>

<p><strong>SKD</strong> — development and design.</p>

<br>
<br>

<p align="center">
  <a href="https://codex.skdragons.com/" target="_blank">
    <img src="https://node-files.skdragons.com/logo_codex_stalking_dragons.png" alt="Codex Stalking Dragons" width="200">
  </a>
  <br>
  <a href="https://codex.skdragons.com/">https://codex.skdragons.com/</a>
  <br>
  <em>Codex Stalking Dragons — Minecraft Modding</em>
</p>
