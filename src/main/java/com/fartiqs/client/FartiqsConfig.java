package com.fartiqs.client;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/** Stores which mods are switched on. Everything starts OFF until you enable it in the menu. */
public final class FartiqsConfig {
    private static final Map<String, Boolean> STATE = new ConcurrentHashMap<>();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("fartiqs-client.properties");
    private static int openKey = 344; // GLFW_KEY_RIGHT_SHIFT

    private FartiqsConfig() {}

    public static boolean isOn(String id) {
        return Boolean.TRUE.equals(STATE.get(id));
    }

    public static void set(String id, boolean on) {
        STATE.put(id, on);
        save();
    }

    public static int openKey() {
        return openKey;
    }

    public static void load() {
        if (!Files.exists(FILE)) return;
        try (InputStream in = Files.newInputStream(FILE)) {
            Properties p = new Properties();
            p.load(in);
            for (String k : p.stringPropertyNames()) {
                if (k.startsWith("mod.")) STATE.put(k.substring(4), Boolean.parseBoolean(p.getProperty(k)));
            }
            try {
                openKey = Integer.parseInt(p.getProperty("openKey", "344").trim());
            } catch (NumberFormatException ignored) {
                openKey = 344;
            }
        } catch (IOException e) {
            System.err.println("[FartiqsClient] could not read config: " + e);
        }
    }

    private static synchronized void save() {
        Properties p = new Properties();
        for (Map.Entry<String, Boolean> e : STATE.entrySet()) p.setProperty("mod." + e.getKey(), String.valueOf(e.getValue()));
        p.setProperty("openKey", String.valueOf(openKey));
        try (OutputStream out = Files.newOutputStream(FILE)) {
            p.store(out, "Fartiqs Client (openKey = GLFW key code, 344 = Right Shift)");
        } catch (IOException e) {
            System.err.println("[FartiqsClient] could not save config: " + e);
        }
    }
}
