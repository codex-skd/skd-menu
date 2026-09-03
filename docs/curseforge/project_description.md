<h1 align="center">&#128396;&#65039; SKD Menu</h1>

<p align="center"><strong>Fully customize your Minecraft main menu &mdash; backgrounds, buttons, logos, and overlays.</strong></p>

<p align="center">
<img src="https://img.shields.io/badge/loader-NeoForge-orange?style=plastic&logo=curseforge" alt="NeoForge">
<img src="https://img.shields.io/badge/minecraft-26.2%20%7C%201.21.1-blue?style=plastic" alt="Minecraft 26.2 and 1.21.1">
<img src="https://img.shields.io/badge/side-client-brightgreen?style=plastic" alt="Client only">
<img src="https://img.shields.io/badge/config-JSON-lightgrey?style=plastic" alt="JSON config">
</p>

<br>

---

<br>

<h2>&#10024; Overview</h2>

<table>
<tr>
<td width="65%">
<p>SKD Menu lets you completely redesign the Minecraft title screen without touching a single line of code &mdash; everything is driven by one JSON config file. Set a fixed background with a smooth "breathing" zoom (or a solid colour, the vanilla panorama, or an animated frame sequence); add two fixed corner logos and a title image; reposition or hide any vanilla button; add your own custom buttons with their own images and actions; and overlay images anywhere on the screen. It also restyles the loading screen with a matching background and progress bar.</p>

<p>A client-only mod by <strong>Stalking Dragons</strong>. It changes only the menu and loading screen &mdash; no gameplay content.</p>
</td>
<td width="35%" align="center">
<a href="https://codex.skdragons.com/" target="_blank"><img src="https://node-files.skdragons.com/uploads/MINECRAFT/Codex/logo_codex_stalking_dragons.png" alt="Codex Stalking Dragons" width="160"></a>
</td>
</tr>
</table>

<br>

<h2>&#127919; Features</h2>

<h3>&#128444;&#65039; Fixed Background with Zoom</h3>
<p>A single background image with a smooth, continuous zoom ("breathing") animation &mdash; no jarring slideshow. Aspect-ratio preserving (<code>cover</code> fit) so it never distorts on small windows. Solid colour, the vanilla panorama, or the classic animated frame sequence are also available.</p>

<h3>&#127991;&#65039; Two Corner Logos + Title Image</h3>
<p>Independently configurable top-left and top-right logos, plus a custom title image in place of the vanilla logo.</p>

<h3>&#127760; Flexible Image Sources</h3>
<p>Every image field &mdash; background, logos, title, overlays, button images &mdash; accepts a mod-bundled asset, a local file path, or a direct <code>http(s)://</code> URL.</p>

<h3>&#128280; Button Layout</h3>
<p>Reposition every vanilla button (singleplayer, multiplayer, realms, options, quit, language, accessibility, mods) with X/Y coordinates. Pre-filled with all recognized IDs out of the box; Realms and the dev-only "TW" badge hidden by default. Hide any button you don't want.</p>

<h3>&#10133; Custom Buttons &amp; Button Images</h3>
<p>Add your own buttons that run commands, open the singleplayer / multiplayer / options screens, open a URL, or quit. Assign a custom image to any button, per-button or globally.</p>

<h3>&#128444;&#65039; Image Overlays</h3>
<p>Render images at any position with configurable width, height and scale &mdash; watermarks, decorations, extra logos.</p>

<h3>&#9203; Loading Screen <em>(26.2 only)</em></h3>
<p>On Minecraft 26.2 the loading / resource-reload screen gets the same custom background plus a themed progress bar. On 1.21.1 the custom loading screen was removed in <code>beta.4</code> (it did not render reliably during server resource-pack reloads on large modpacks); NeoForge's vanilla loading screen is used there instead.</p>

<h3>&#9881;&#65039; JSON Config</h3>
<p>All settings live in <code>config/skd_menu/menu.json</code>, auto-generated on first launch. Edit with any text editor; re-open the title screen to reload. See <a href="https://gitlab.com/stalking-dragons/minecraft/skd-menu">the repository</a> for the full config reference.</p>

<br>

<h2>&#129521; Mod Structure</h2>

<table>
<tr><th align="left">Area</th><th align="left">What it provides</th></tr>
<tr><td><code>TitleScreenMixin</code></td><td>Button repositioning / hiding / custom buttons, the custom background, logos, title image and overlays on the title screen.</td></tr>
<tr><td><code>LoadingOverlayMixin</code></td><td>The custom loading-screen background and progress bar.</td></tr>
<tr><td><code>LoadingConfig</code></td><td>The loading-screen JSON config model (background, banner, logo, progress bar).</td></tr>
<tr><td><code>TextureResolver</code></td><td>Resolves image refs (bundled asset / file path / URL) to textures and caches their dimensions.</td></tr>
<tr><td><code>Config</code> / <code>MenuConfig</code></td><td>The JSON config model and its live reload.</td></tr>
</table>

<br>

<h2>&#128203; Requirements</h2>

<table>
<tr><td><strong>Minecraft / NeoForge / Java</strong></td><td>see <em>Available Versions</em> below</td></tr>
<tr><td><strong>Dependencies</strong></td><td>None</td></tr>
<tr><td><strong>Side</strong></td><td>Client only</td></tr>
</table>

<br>

<h2>&#128230; Available Versions</h2>

<table>
<tr><th align="left">Minecraft</th><th align="left">NeoForge</th><th align="left">Java</th><th align="left">Latest build</th><th align="left">Status</th></tr>
<tr><td>26.2</td><td>26.2.0.57+</td><td>25</td><td><code>1.2.4</code></td><td>Stable</td></tr>
<tr><td>1.21.1</td><td>21.1.249+</td><td>21</td><td><code>0.0.0-beta.4</code></td><td>Beta &mdash; API port from the 26.2 line (title-screen customization only)</td></tr>
</table>

<p><em>Both versions share this CurseForge project. Pick the file that matches your Minecraft version.</em></p>

<br>

<h2>&#127918; How to Use</h2>

<ol>
<li>Install the mod and launch Minecraft once to generate <code>config/skd_menu/menu.json</code>.</li>
<li>Edit that file in any text editor.</li>
<li>Set <code>background.type</code> to <code>"image"</code> (default, with zoom), <code>"color"</code>, <code>"panorama"</code> or <code>"animated"</code>.</li>
<li>Set button positions and images under <code>buttons.defaults</code>; add custom buttons under <code>buttons.custom</code>.</li>
<li>Re-open the title screen to see your changes.</li>
</ol>

<br>

---

<br>

<h2>&#128591; Credits</h2>

<p><strong>Stalking Dragons</strong> &mdash; development and design.</p>

<br>
<br>

<p align="center">
  <a href="https://codex.skdragons.com/" target="_blank">
    <img src="https://node-files.skdragons.com/uploads/MINECRAFT/Codex/logo_codex_stalking_dragons.png" alt="Codex Stalking Dragons" width="200">
  </a>
  <br>
  <a href="https://codex.skdragons.com/">https://codex.skdragons.com/</a>
  <br>
  <em>Codex Stalking Dragons &mdash; Minecraft Modding</em>
</p>
