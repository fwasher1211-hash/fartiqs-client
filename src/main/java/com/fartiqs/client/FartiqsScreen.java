package com.fartiqs.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

/** The Fartiqs Client menu. Mouse + keys are polled through GLFW so it does not depend on input class names. */
public class FartiqsScreen extends Screen {
    private record Mod(String id, String name, String desc, boolean hasMenu) {}

    private static final Mod[] MODS = {
        new Mod("profit", "Profit HUD", "Live profit pill at the top of the screen", false),
        new Mod("pay2x", "2x Pay", "Adds a 2x button to payments you get", false),
        new Mod("auction", "Auction", "Auctions: highest /pay wins", true),
    };

    private static final int W = 360;
    private static final int HEADER = 46;
    private static final int ROW_H = 38;
    private static final int GAP = 8;
    private static final int FOOTER = 26;

    private static final int ACCENT = 0xFF7C5CFF;
    private static final int OFF = 0xFF3A3F4F;

    private final float[] anim = new float[MODS.length];
    private boolean wasMouseDown = true;
    private boolean wasKeyDown = true; // key is held when the menu opens

    public FartiqsScreen() {
        super(Component.literal("Fartiqs Client"));
        for (int i = 0; i < MODS.length; i++) anim[i] = FartiqsConfig.isOn(MODS[i].id()) ? 1f : 0f;
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;
        long win = GLFW.glfwGetCurrentContext();

        boolean mouseDown = win != 0L && GLFW.glfwGetMouseButton(win, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean clicked = mouseDown && !wasMouseDown;
        wasMouseDown = mouseDown;

        boolean keyDown = win != 0L && GLFW.glfwGetKey(win, FartiqsConfig.openKey()) == GLFW.GLFW_PRESS;
        if (keyDown && !wasKeyDown) {
            mc.setScreen(null);
            return;
        }
        wasKeyDown = keyDown;

        int h = HEADER + MODS.length * (ROW_H + GAP) + FOOTER - GAP + 4;
        int x0 = (this.width - W) / 2;
        int y0 = (this.height - h) / 2;

        // dim the game behind the menu
        g.fill(0, 0, this.width, this.height, 0x99000000);

        // panel
        rr(g, x0 - 1, y0 - 1, W + 2, h + 2, 8, 0xFF2A2F45);
        rr(g, x0, y0, W, h, 7, 0xFF0E1016);

        // header
        g.drawString(font, "\u00a7lFARTIQS \u00a7r\u00a77CLIENT", x0 + 16, y0 + 14, ACCENT, false);
        g.fill(x0 + 16, y0 + 29, x0 + 44, y0 + 31, ACCENT);
        String ver = "v1.0.0";
        g.drawString(font, ver, x0 + W - 16 - font.width(ver), y0 + 14, 0xFF5B6178, false);

        // module cards
        int enabled = 0;
        for (int i = 0; i < MODS.length; i++) {
            Mod m = MODS[i];
            boolean on = FartiqsConfig.isOn(m.id());
            if (on) enabled++;

            int cx = x0 + 12;
            int cw = W - 24;
            int cy = y0 + HEADER + i * (ROW_H + GAP);

            // animate toward the current state
            anim[i] += ((on ? 1f : 0f) - anim[i]) * 0.3f;
            if (Math.abs((on ? 1f : 0f) - anim[i]) < 0.01f) anim[i] = on ? 1f : 0f;
            float a = anim[i];

            int swW = 30, swH = 14;
            int swX = cx + cw - 14 - swW;
            int swY = cy + (ROW_H - swH) / 2;
            int btnW = 50, btnH = 16;
            int btnX = swX - 10 - btnW;
            int btnY = cy + (ROW_H - btnH) / 2;

            boolean cardHover = inside(mouseX, mouseY, cx, cy, cw, ROW_H);
            boolean btnHover = m.hasMenu() && inside(mouseX, mouseY, btnX, btnY, btnW, btnH);

            rr(g, cx, cy, cw, ROW_H, 5, cardHover ? 0xFF1E222D : 0xFF171A23);
            // accent strip on the left, fades in when enabled
            rr(g, cx, cy + 6, 3, ROW_H - 12, 1, mix(0x00000000, ACCENT, a));

            g.drawString(font, m.name(), cx + 14, cy + 8, 0xFFFFFFFF, false);
            g.drawString(font, m.desc(), cx + 14, cy + 22, 0xFF8A90A0, false);

            if (m.hasMenu()) {
                int bg = on ? (btnHover ? 0xFF3A3F5C : 0xFF2B2F46) : 0xFF1E212B;
                int fg = on ? 0xFFFFFFFF : 0xFF4E5366;
                rr(g, btnX, btnY, btnW, btnH, 4, bg);
                String t = "Open";
                g.drawString(font, t, btnX + (btnW - font.width(t)) / 2, btnY + 4, fg, false);
            }

            // switch
            rr(g, swX, swY, swW, swH, 7, mix(OFF, ACCENT, a));
            int knob = 10;
            int kx = swX + 2 + Math.round(a * (swW - knob - 4));
            rr(g, kx, swY + 2, knob, knob, 5, 0xFFFFFFFF);

            if (clicked && cardHover) {
                if (btnHover) {
                    if (on) openModMenu(m.id());
                } else {
                    FartiqsConfig.set(m.id(), !on);
                }
            }
        }

        // footer
        int fy = y0 + h - 18;
        g.drawString(font, enabled + "/" + MODS.length + " enabled", x0 + 16, fy, 0xFF5B6178, false);
        String hint = "Right Shift to close";
        g.drawString(font, hint, x0 + W - 16 - font.width(hint), fy, 0xFF5B6178, false);
    }

    private void openModMenu(String id) {
        if (!"auction".equals(id)) return;
        try {
            Class<?> c = Class.forName("com.example.auction.client.AuctionScreen");
            Minecraft.getInstance().setScreen((Screen) c.getConstructor().newInstance());
        } catch (Throwable t) {
            System.err.println("[FartiqsClient] could not open Auction menu: " + t);
        }
    }

    private static boolean inside(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    /** Rounded rectangle built from one-pixel-high rows. */
    private static void rr(GuiGraphics g, int x, int y, int w, int h, int r, int color) {
        r = Math.max(0, Math.min(r, Math.min(w, h) / 2));
        for (int i = 0; i < h; i++) {
            int k = Math.min(i, h - 1 - i);
            int inset = 0;
            if (k < r) {
                double dy = r - k - 0.5;
                inset = (int) Math.round(r - Math.sqrt(Math.max(0.0, r * r - dy * dy)));
            }
            g.fill(x + inset, y + i, x + w - inset, y + i + 1, color);
        }
    }

    private static int mix(int c1, int c2, float t) {
        int a = lerp(c1 >>> 24, c2 >>> 24, t);
        int r = lerp((c1 >> 16) & 255, (c2 >> 16) & 255, t);
        int gr = lerp((c1 >> 8) & 255, (c2 >> 8) & 255, t);
        int b = lerp(c1 & 255, c2 & 255, t);
        return (a << 24) | (r << 16) | (gr << 8) | b;
    }

    private static int lerp(int a, int b, float t) {
        return Math.round(a + (b - a) * t);
    }
}
