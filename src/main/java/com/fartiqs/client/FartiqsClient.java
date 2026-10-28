package com.fartiqs.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class FartiqsClient implements ClientModInitializer {
    private static boolean wasDown = false;

    @Override
    public void onInitializeClient() {
        FartiqsConfig.load();
        ClientTickEvents.END_CLIENT_TICK.register(FartiqsClient::tick);
    }

    private static void tick(Minecraft mc) {
        long window = GLFW.glfwGetCurrentContext();
        if (window == 0L) return;
        boolean down = GLFW.glfwGetKey(window, FartiqsConfig.openKey()) == GLFW.GLFW_PRESS;
        if (down && !wasDown && mc.screen == null) {
            mc.setScreen(new FartiqsScreen());
        }
        wasDown = down;
    }
}
