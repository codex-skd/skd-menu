# Configuración de `skd_menu`

Archivo: `config/skd_menu/menu.json`. Se genera automáticamente con los valores por defecto la primera vez que se lanza el juego con el mod instalado.

## Cuándo se aplican los cambios

Por defecto (`reloadJsonOnTitle=false` en `config/skd_menu/menu-common.toml`) el JSON se lee **una sola vez, al arrancar el juego** — para ver un cambio hay que reiniciar Minecraft.

Si pones `reloadJsonOnTitle=true` en ese `.toml`, el mod relee `menu.json` cada vez que la pantalla de título se reinicializa, lo cual ocurre al:
- redimensionar la ventana del juego, o
- volver a la pantalla de título desde otra pantalla (salir de una partida/servidor).

No se recarga "en caliente" mientras miras la pantalla de título ya abierta sin ninguno de esos dos eventos — la forma más rápida de probar cambios con `reloadJsonOnTitle=true` es redimensionar un poco la ventana.

Si `enableMod=false` en ese mismo `.toml`, el mod no toca nada de la pantalla de título (todo vainilla).

## Sistema de coordenadas (`x`, `y`)

Todas las coordenadas `x`/`y` de este mod (fondo, logos, título, overlays, botones) son **píxeles**, no porcentajes, y usan el mismo sistema que el resto de la GUI de Minecraft:

- **Origen (0,0) = esquina superior izquierda** de la ventana. `x` crece hacia la derecha, `y` crece hacia **abajo** (no hacia arriba).
- Son los **píxeles lógicos de la GUI**, no píxeles físicos de pantalla — dependen del tamaño de ventana **y** de la opción "GUI Scale" de Minecraft (Opciones > Vídeo). El propio código usa `width`/`height` de la pantalla (en estas mismas unidades) para calcular el centro, por ejemplo `centerX = width / 2`.
- Por eso una coordenada **fija** (`x`/`y` distinto de `-1`/`-999` según el campo) se queda siempre en ese punto exacto sin importar el tamaño de ventana — puede quedar descuadrada si cambias de resolución o de GUI Scale. La alternativa "automática" (`-1` en botones, `cover` en el fondo) recalcula la posición/ajuste cada vez según el tamaño actual.

## Formas de referenciar una imagen

Todos los campos de imagen del mod (`background.image.path`, `logos.topLeft.image`, `logos.topRight.image`, `title.image`, `images[].path`, `buttons.defaults[].image`, `buttons.custom[].image`, `buttons.globalImage`) aceptan **3 formatos** intercambiables:

1. **Asset propio del mod / resource pack** — `modid:ruta/al/archivo.png`, por ejemplo `skd_menu:textures/gui/bg_main_default.png`. Es una textura empaquetada dentro del JAR del mod (en `src/main/resources/assets/skd_menu/textures/gui/`) o servida por un resource pack activo. Es la forma más eficiente (no requiere E/S de disco ni red en cada frame).
2. **Ruta de archivo en disco** — ruta absoluta (o relativa al directorio de lanzamiento del juego) a un `.png` en el sistema de archivos, por ejemplo `C:\Users\tú\Pictures\fondo.png`. Útil para imágenes personalizadas que no quieres empaquetar dentro de un resource pack.
3. **URL externa** — `http://` o `https://` apuntando directamente a un archivo de imagen. Se descarga una vez en segundo plano (sin bloquear el render) y se cachea en memoria durante la sesión; si la descarga falla o aún no ha terminado, se usa el fondo de respaldo (color negro) hasta que esté disponible.

El mod detecta automáticamente cuál de los 3 formatos es cada cadena: primero prueba si es una URL `http(s)`, luego si es una ruta de archivo existente en disco, y si no, la interpreta como identificador `modid:ruta`.

## `background`

```jsonc
"background": {
  "type": "image",           // "image" | "color" | "animated" | "panorama" | "custom_panorama"
  "image": {
    "path": "skd_menu:textures/gui/bg_main_default.png",
    "fit": "cover",           // "cover" (mantiene proporción, recorta sobrante) | "stretch" (estira, puede deformar)
    "effect": {
      "type": "zoom",         // "zoom" | "none"
      "durationMs": 20000,    // duración de un ciclo completo de zoom in -> zoom out
      "minScale": 1.0,
      "maxScale": 1.15
    }
  },
  "color": { "color": -16777216 },
  "animation": { "frames": [...], "frameTimeMs": 8000, "loop": true },
  "panorama": { "files": [] }
}
```

- **`type: "image"`** (por defecto): un único fondo fijo. Con `fit: "cover"` (por defecto) la imagen siempre llena la pantalla completa sin deformarse, recortando el sobrante por los bordes según haga falta — así no se rompe la estética en ventanas pequeñas o con proporción distinta a la imagen original. Con `fit: "stretch"` se estira exactamente a `width×height`, pudiendo deformar la imagen si la proporción no coincide.
- **`effect.type: "zoom"`**: aplica un efecto de "respiración" (zoom in/out continuo y suave, sin saltos) sobre el fondo, oscilando entre `minScale` y `maxScale` a lo largo de `durationMs` milisegundos. `effect.type: "none"` lo desactiva y muestra el fondo estático.
- **`type: "color"`**: fondo de color sólido (`color.color`, ARGB con signo, ej. `-16777216` = negro opaco).
- **`type: "animated"`**: comportamiento heredado — rota entre varias imágenes (`animation.frames`) cada `animation.frameTimeMs` ms. Se mantiene disponible para quien lo prefiera, pero **ya no es el valor por defecto** (el mod usa `"image"` con zoom por defecto para evitar el salto entre imágenes).
- **`type: "panorama"`**: panorama vanilla de Minecraft sin modificar.
- **`type: "custom_panorama"`**: panorama vanilla sustituido por 6 imágenes propias (`panorama.files`, exactamente 6 rutas en orden, cualquiera de los 3 formatos de imagen soportados).

## `logos`

Dos logos de posición fija, siempre visibles encima del fondo: uno arriba a la izquierda y otro arriba a la derecha.

```jsonc
"logos": {
  "topLeft":  { "enabled": true, "image": "skd_menu:textures/gui/logo_top_left_default.png",  "x": 10, "y": 10, "width": -1, "height": 48 },
  "topRight": { "enabled": true, "image": "skd_menu:textures/gui/logo_top_right_default.png", "x": -1, "y": 10, "width": -1, "height": 48 }
}
```

- `enabled`: muestra/oculta el logo.
- `image`: cualquiera de los 3 formatos soportados (ver arriba).
- `x`, `y`: posición en píxeles. `x: -1` es un valor especial: ancla el logo al borde izquierdo (10px) si es `topLeft`, o al borde derecho (`width - ancho - 10px`) si es `topRight`. `y: -1` equivale a `10`.
- `width`, `height`: tamaño en píxeles. `height` por defecto `48`. `width: -1` (por defecto) calcula el ancho automáticamente a partir de la proporción real de la imagen, para no deformarla — solo defínelo si quieres forzar un ancho concreto.

## `title`

Logo/título central superior, independiente de los dos logos fijos de esquina. `type: "image" | "hidden"`. **Por defecto está oculto (`"hidden"`)**, ya que la imagen de ejemplo incluida no encaja con la estética por defecto del mod (fondo + logos de esquina); actívalo con `type: "image"` y tu propia imagen si quieres un logo central también. Con `animated: true` aplica un efecto de escala senoidal (`animRange`, `animSpeed`).

## `buttons`

```jsonc
"buttons": {
  "defaults": [
    { "id": "singleplayer",  "x": -1, "y": -1, "hide": false, "image": "" },
    { "id": "multiplayer",   "x": -1, "y": -1, "hide": false, "image": "" },
    { "id": "realms",        "x": -1, "y": -1, "hide": true,  "image": "" },
    { "id": "tw",            "x": -1, "y": -1, "hide": true,  "image": "" },
    { "id": "options",       "x": -1, "y": -1, "hide": false, "image": "" },
    { "id": "quit",          "x": -1, "y": -1, "hide": false, "image": "" },
    { "id": "language",      "x": -1, "y": -1, "hide": false, "image": "" },
    { "id": "accessibility", "x": -1, "y": -1, "hide": false, "image": "" },
    { "id": "mods",          "x": -1, "y": -1, "hide": false, "image": "" }
  ],
  "custom": [],
  "globalImage": ""
}
```

### `buttons.defaults`

Estos son los 9 botones que Minecraft/NeoForge colocan en el menú (los que estén realmente presentes según tu versión y mods instalados). El mod los genera automáticamente con esta lista completa la primera vez, para que sea evidente qué IDs existen sin tener que adivinarlos.

- `id`: identificador del botón (coincide de forma parcial e insensible a mayúsculas con el texto del botón vanilla, incluido el texto usado solo para narración en los botones-icono como `language`/`accessibility`/`mods`). Los 9 reconocidos son exactamente los de la lista de arriba.
- `realms` y `tw` vienen **ocultos por defecto** (`hide: true`): `realms` es el botón "Minecraft Realms" (redundante para la mayoría, y en la fila de iconos pequeños de NeoForge su posición vanilla puede solaparse con otros botones); `tw` es una insignia de NeoForge para builds de desarrollo/test, no relevante para el jugador final. Pon `hide: false` si quieres recuperarlos.
- No existe (todavía) un ID reconocido para el botón "Friends" (multijugador social) — si tu versión lo muestra, queda en su posición vanilla sin gestionar.
- `x`, `y`: posición en píxeles. **`-1` es el valor especial "automático"**: el mod calcula la posición dinámicamente según el tamaño actual de la ventana (igual que el layout vanilla, centrado y apilado). Si defines un valor distinto de `-1`, esa coordenada queda **fija** en esa posición sin importar el tamaño de ventana.
- `hide`: `true` oculta el botón por completo.
- `image`: imagen propia para este botón (cualquiera de los 3 formatos). Si se define, sustituye el sprite vanilla del botón por la imagen (estirada a su tamaño), manteniendo el texto encima. Si se deja vacío, se usa `buttons.globalImage` como respaldo; si tampoco hay `globalImage`, se muestra el botón vanilla normal.

Si borras una entrada de la lista o dejas `"defaults": []`, ese/esos botones simplemente no tienen configuración aplicada y se comportan con el layout vanilla dinámico de siempre.

### `buttons.custom`

Botones adicionales, no vanilla.

```jsonc
{ "text": "Discord", "action": "open_url", "command": "https://discord.gg/...", "x": 10, "y": 10, "width": 200, "height": 20, "image": "" }
```

- `x`, `y`: **posición obligatoria** para que el botón se muestre. Si dejas `x` o `y` en su valor por defecto (`-999`), el botón **no se crea** (equivale a estar oculto). Solo se muestra cuando defines explícitamente ambas coordenadas.
- `width`, `height`: tamaño en píxeles.
- `action`: `"command"` (ejecuta `command` como comando de chat/consola), `"open_singleplayer"`, `"open_multiplayer"`, `"open_options"`, `"quit"`, `"open_url"` (abre `command` como URL en el navegador del sistema).
- `image`: igual que en `buttons.defaults` — imagen propia, con `buttons.globalImage` como respaldo genérico.

### `buttons.globalImage`

Imagen aplicada a **todos** los botones (por defecto y personalizados) que no tengan su propio campo `image` definido. Déjalo vacío (`""`) para no aplicar ninguna imagen genérica y usar el aspecto vanilla/normal en los botones sin imagen propia.

## `images`

Overlays de imagen libres (sin cambios): `path`, `enabled`, `x`, `y` (`-1` = esquina superior derecha por defecto), `width`, `height`, `scale`.

## `buttonAnimation`

```jsonc
"buttonAnimation": {
  "type": "slide_right",
  "duration": 600,
  "offset": 300
}
```

- `type`: **hoy solo `"slide_right"` produce animación real** — los botones visibles entran deslizándose desde la derecha (`offset` píxeles) hasta su posición final, con una curva ease-out, a lo largo de `duration` milisegundos. `type: "none"` (o `null`) desactiva la animación por completo (los botones aparecen directamente en su posición final). Cualquier otro valor de `type` queda reservado para el futuro y hoy se comporta como `"none"` (sin animación).
- `duration`: duración en milisegundos de la animación de entrada.
- `offset`: desplazamiento inicial en píxeles desde el que parten los botones.

La animación se dispara una vez al abrir el menú, sobre todos los widgets visibles en ese momento (botones por defecto no ocultos + botones custom con posición definida).
