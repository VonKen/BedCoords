package com.bedcoords;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.tick.ClientTickEvent;
import org.lwjgl.glfw.GLFW;

import java.util.Comparator;
import java.util.List;

public class BedCoordsOverlay {
    public static final KeyMapping ADD_WAYPOINT = new KeyMapping(
        "key.bedcoords.add_waypoint", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_P, "key.categories.bedcoords"
    );
    public static final KeyMapping TOGGLE_LIST = new KeyMapping(
        "key.bedcoords.toggle_list", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_U, "key.categories.bedcoords"
    );
    public static final KeyMapping REMOVE_NEAREST = new KeyMapping(
        "key.bedcoords.remove_nearest", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_K, "key.categories.bedcoords"
    );

    private static boolean showList = false;
    private static int nameCounter = 1;

    public static void onClientTick(ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        var mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        while (ADD_WAYPOINT.consumeClick()) {
            addWaypoint(mc);
        }
        while (TOGGLE_LIST.consumeClick()) {
            showList = !showList;
        }
        while (REMOVE_NEAREST.consumeClick()) {
            removeNearest(mc);
        }
    }

    private static void addWaypoint(Minecraft mc) {
        var pos = mc.player.blockPosition();
        var dim = mc.level.dimension().location().toString();
        var name = "Waypoint " + nameCounter++;
        WaypointStore.add(new Waypoint(name, pos.getX(), pos.getY(), pos.getZ(), dim));
    }

    private static void removeNearest(Minecraft mc) {
        var player = mc.player;
        Waypoint nearest = null;
        double nearestDist = 9.0;
        for (var wp : WaypointStore.getWaypoints()) {
            if (!wp.dimension().equals(mc.level.dimension().location().toString())) continue;
            double dist = player.distanceToSqr(wp.x() + 0.5, wp.y() + 0.5, wp.z() + 0.5);
            if (dist < nearestDist) {
                nearestDist = dist;
                nearest = wp;
            }
        }
        if (nearest != null) {
            WaypointStore.remove(nearest);
        }
    }

    public static void onRenderGui(RenderGuiLayerEvent.Post event) {
        var mc = Minecraft.getInstance();
        if (mc.player == null || !showList) return;

        var waypoints = WaypointStore.getWaypoints();
        if (waypoints.isEmpty()) {
            renderText(event, "No waypoints", 0xFFFFFF);
            return;
        }

        var playerPos = mc.player.position();
        var currentDim = mc.level.dimension().location().toString();

        var sorted = waypoints.stream()
            .filter(w -> w.dimension().equals(currentDim))
            .sorted(Comparator.comparingDouble(w -> w.x() * w.x() + w.z() * w.z()))
            .toList();

        int y = 4;
        int lineHeight = mc.font.lineHeight + 2;

        for (var wp : sorted) {
            double dx = wp.x() + 0.5 - playerPos.x;
            double dz = wp.z() + 0.5 - playerPos.z;
            double dist = Math.sqrt(dx * dx + dz * dz);
            String dir = direction(dx, dz);
            String text = String.format("%s  %s %.0fm", wp.name(), dir, dist);
            renderText(event, text, y, 0xFFFFFF);
            y += lineHeight;
        }
    }

    private static String direction(double dx, double dz) {
        float angle = (float) (Math.atan2(dz, dx) * 180 / Math.PI);
        var mc = Minecraft.getInstance();
        if (mc.player == null) return "";
        float yaw = mc.player.getYRot();
        float relative = (angle - yaw + 360) % 360;
        if (relative < 22.5 || relative >= 337.5) return "→";
        if (relative < 67.5) return "↗";
        if (relative < 112.5) return "↑";
        if (relative < 157.5) return "↖";
        if (relative < 202.5) return "←";
        if (relative < 247.5) return "↙";
        if (relative < 292.5) return "↓";
        return "↘";
    }

    private static void renderText(RenderGuiLayerEvent.Post event, String text, int color) {
        renderText(event, text, 4, color);
    }

    private static void renderText(RenderGuiLayerEvent.Post event, String text, int y, int color) {
        var mc = Minecraft.getInstance();
        var guiGraphics = event.getGuiGraphics();
        int x = 4;
        int textWidth = mc.font.width(text);
        guiGraphics.fill(x - 1, y - 1, x + textWidth + 1, y + mc.font.lineHeight + 1, 0xAA000000);
        guiGraphics.drawString(mc.font, text, x, y, color, false);
    }
}
