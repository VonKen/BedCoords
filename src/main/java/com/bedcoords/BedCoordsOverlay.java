package com.bedcoords;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public class BedCoordsOverlay {
    @Nullable
    private static BlockPos respawnPos;
    @Nullable
    private static ResourceKey<Level> respawnDim;

    public static void onRenderGui(RenderGuiLayerEvent.Post event) {
        var mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // Try to find the respawn position via reflection (works with any mappings)
        if (respawnPos == null) {
            findRespawnData(mc.player);
        }

        var font = mc.font;
        var guiGraphics = event.getGuiGraphics();
        String text;

        if (respawnPos != null) {
            var dimName = respawnDim != null
                ? respawnDim.location().toShortLanguageKey()
                : mc.level.dimension().location().toShortLanguageKey();
            text = String.format("Bed [%s] %d, %d, %d", dimName, respawnPos.getX(), respawnPos.getY(), respawnPos.getZ());
        } else {
            text = "No bed set";
        }

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int textWidth = font.width(text);
        int x = screenWidth - textWidth - 4;
        int y = 4;

        guiGraphics.fill(x - 1, y - 1, x + textWidth + 1, y + font.lineHeight + 1, 0xAA000000);
        guiGraphics.drawString(font, text, x, y, 0xFFFFFFFF, false);
    }

    private static void findRespawnData(Player player) {
        var sleepingPos = player.getSleepingPos().orElse(null);
        var currentPos = player.blockPosition();

        for (var f : Player.class.getDeclaredFields()) {
            f.setAccessible(true);
            try {
                if (f.getType() == BlockPos.class) {
                    var val = (BlockPos) f.get(player);
                    if (val != null && !val.equals(sleepingPos) && !val.equals(currentPos)) {
                        respawnPos = val;
                    }
                } else if (f.getType() == ResourceKey.class) {
                    var val = f.get(player);
                    if (val instanceof ResourceKey key) {
                        respawnDim = key;
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }
}
