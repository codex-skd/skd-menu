package com.skd.menu;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Repositions / resizes / hides buttons that OTHER mods add to the vanilla title screen.
 *
 * Third-party mods add their title-screen buttons through {@link ScreenEvent.Init.Post}, which fires
 * after {@code TitleScreen.init()} — i.e. after {@code TitleScreenMixin} has already placed the
 * vanilla + custom skd_menu buttons. This subscriber therefore runs on the same event at
 * {@link EventPriority#LOWEST}, sees the fully-populated widget list, and applies
 * {@code menu.json → buttons.thirdParty}.
 *
 * Detection is generic: any {@link AbstractWidget} on the screen whose class is not in the
 * {@code net.minecraft.} or {@code com.skd.menu.} package is treated as a third-party button.
 */
@EventBusSubscriber(modid = SkdMenu.MODID, value = Dist.CLIENT)
public final class ThirdPartyButtons {
    private ThirdPartyButtons() {}

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onScreenInitPost(ScreenEvent.Init.Post event) {
        if (!Config.ENABLE_MOD.getAsBoolean()) return;
        Screen screen = event.getScreen();
        if (!(screen instanceof TitleScreen)) return;

        MenuConfig config = MenuConfig.getInstance();
        MenuConfig.ThirdPartyConfig tp = config.buttons != null ? config.buttons.thirdParty : null;
        if (tp == null || !tp.enabled) return;

        int sw = screen.width;
        int sh = screen.height;

        // 1. Collect third-party widgets in discovery order, assigning a stable-ish slug id.
        List<AbstractWidget> widgets = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        Map<String, Integer> idCounts = new HashMap<>();
        for (GuiEventListener child : screen.children()) {
            if (!(child instanceof AbstractWidget w)) continue;
            String cn = w.getClass().getName();
            if (cn.startsWith("net.minecraft.") || cn.startsWith("com.skd.menu.")) continue;
            String base = slugFor(cn);
            int n = idCounts.merge(base, 1, Integer::sum);
            String id = n == 1 ? base : base + "-" + n;
            widgets.add(w);
            ids.add(id);
        }
        if (widgets.isEmpty()) return;

        // 2. Ensure an entry exists for every discovered id; persist once if new ids appeared.
        Map<String, MenuConfig.ThirdPartyButton> entryMap = new HashMap<>();
        for (MenuConfig.ThirdPartyButton e : tp.entries) entryMap.put(e.id, e);
        boolean dirty = false;
        for (String id : ids) {
            if (!entryMap.containsKey(id)) {
                MenuConfig.ThirdPartyButton e = new MenuConfig.ThirdPartyButton();
                e.id = id;
                tp.entries.add(e);
                entryMap.put(id, e);
                dirty = true;
            }
        }
        if (dirty) MenuConfig.save();

        // 3. Apply each entry; collect the auto-positioned ones for the left stack.
        List<AbstractWidget> stack = new ArrayList<>();
        for (int i = 0; i < widgets.size(); i++) {
            AbstractWidget w = widgets.get(i);
            MenuConfig.ThirdPartyButton e = entryMap.get(ids.get(i));
            if (e == null) continue;

            if (e.hide) {
                w.visible = false;
                continue;
            }
            if (e.width >= 0) w.setWidth(e.width);
            if (e.height >= 0) w.setHeight(e.height);

            if (e.x >= 0 && e.y >= 0) {
                w.setX(clamp(pct(e.x, sw), w.getWidth(), sw));
                w.setY(clamp(pct(e.y, sh), w.getHeight(), sh));
            } else if (tp.autoStack) {
                stack.add(w);
            }
        }

        // 4. Lay out the left vertical stack in discovery order.
        if (!stack.isEmpty()) {
            int anchor = pct(tp.stackY, sh);
            if (!tp.stackFromBottom) {
                int y = anchor;
                for (AbstractWidget w : stack) {
                    w.setX(clamp(pct(tp.stackX, sw), w.getWidth(), sw));
                    w.setY(clamp(y, w.getHeight(), sh));
                    y += w.getHeight() + tp.stackGap;
                }
            } else {
                int y = anchor;
                for (AbstractWidget w : stack) {
                    y -= w.getHeight();
                    w.setX(clamp(pct(tp.stackX, sw), w.getWidth(), sw));
                    w.setY(clamp(y, w.getHeight(), sh));
                    y -= tp.stackGap;
                }
            }
        }
    }

    /** Percentage (0-100) of a screen dimension → pixels. */
    private static int pct(float percent, int total) {
        return Math.round(percent / 100f * total);
    }

    /** Keeps a widget fully on screen even if an out-of-range percentage was configured. */
    private static int clamp(int pos, int size, int screenTotal) {
        return Math.max(0, Math.min(pos, Math.max(0, screenTotal - size)));
    }

    private static final Map<String, String> KNOWN = new HashMap<>();
    static {
        KNOWN.put("com.simibubi.create", "create");
        KNOWN.put("org.violetmoon.quark", "quark");
        KNOWN.put("vazkii.quark", "quark");
        KNOWN.put("com.mrcrayfish.configured", "configured");
        KNOWN.put("com.mrcrayfish.catalogue", "catalogue");
    }

    /** Best-effort mod slug from a widget class name: known-prefix map first, else the package segment most likely to be the mod id. */
    private static String slugFor(String className) {
        for (Map.Entry<String, String> k : KNOWN.entrySet()) {
            if (className.startsWith(k.getKey() + ".")) return k.getValue();
        }
        String pkg = className.contains(".") ? className.substring(0, className.lastIndexOf('.')) : className;
        String[] seg = pkg.split("\\.");
        if (seg.length == 0) return "mod";
        Set<String> generic = new HashSet<>(List.of("com", "org", "net", "io", "dev", "me", "app", "co", "gg", "xyz"));
        String s = (seg.length >= 2 && generic.contains(seg[0])) ? seg[1] : seg[0];
        s = s.toLowerCase().replaceAll("[^a-z0-9_]", "");
        return s.isEmpty() ? "mod" : s;
    }
}
