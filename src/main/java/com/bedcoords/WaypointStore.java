package com.bedcoords;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WaypointStore {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE_PATH = Path.of("config", "bedcoords", "waypoints.json");
    private static final Map<String, List<Waypoint>> STORE = new HashMap<>();

    public static void load() {
        try {
            if (Files.exists(FILE_PATH)) {
                var content = Files.readString(FILE_PATH);
                var map = GSON.fromJson(content, new TypeToken<Map<String, List<Waypoint>>>() {}.getType());
                if (map != null) {
                    STORE.clear();
                    STORE.putAll(map);
                    return;
                }
            }
        } catch (IOException e) {
            System.err.println("BedCoords: Failed to load waypoints: " + e.getMessage());
        }
        STORE.clear();
    }

    public static void save() {
        try {
            Files.createDirectories(FILE_PATH.getParent());
            Files.writeString(FILE_PATH, GSON.toJson(STORE));
        } catch (IOException e) {
            System.err.println("BedCoords: Failed to save waypoints: " + e.getMessage());
        }
    }

    public static List<Waypoint> getWaypoints() {
        return STORE.getOrDefault(contextKey(), List.of());
    }

    public static void add(Waypoint wp) {
        STORE.computeIfAbsent(contextKey(), k -> new ArrayList<>()).add(wp);
        save();
    }

    public static boolean remove(String name) {
        var list = STORE.get(contextKey());
        if (list == null) return false;
        var removed = list.removeIf(w -> w.name().equals(name));
        if (removed) save();
        return removed;
    }

    public static boolean remove(Waypoint wp) {
        var list = STORE.get(contextKey());
        if (list == null) return false;
        var removed = list.remove(wp);
        if (removed) save();
        return removed;
    }

    private static String contextKey() {
        var mc = Minecraft.getInstance();
        if (mc.getCurrentServer() != null) {
            return "mp:" + mc.getCurrentServer().ip;
        }
        if (mc.getSingleplayerServer() != null) {
            return "sp:" + mc.getSingleplayerServer().getWorldData().getLevelName();
        }
        return "unknown";
    }
}
