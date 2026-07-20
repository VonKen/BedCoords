package com.bedcoords;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class BedCoordsOverlay {
    @Nullable
    private static List<Field> posFields;

    public static void onRenderGui(RenderGuiLayerEvent.Post event) {
        var mc = Minecraft.getInstance();
        if (mc.player == null) return;

        if (posFields == null) {
            posFields = new ArrayList<>();
            for (var f : Player.class.getDeclaredFields()) {
                if (f.getType() == BlockPos.class) {
                    f.setAccessible(true);
                    posFields.add(f);
                }
            }
        }

        var pos = findRespawnPos(mc.player);
        String text;
        if (pos != null) {
            var dimName = mc.level.dimension().location().toShortLanguageKey();
            text = String.format("Bed [%s] %d, %d, %d", dimName, pos.getX(), pos.getY(), pos.getZ());
        } else {
            text = "No bed set";
        }

        var font = mc.font;
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int textWidth = font.width(text);
        int x = screenWidth - textWidth - 4;
        int y = 4;

        var guiGraphics = event.getGuiGraphics();
        guiGraphics.fill(x - 1, y - 1, x + textWidth + 1, y + font.lineHeight + 1, 0xAA000000);
        guiGraphics.drawString(font, text, x, y, 0xFFFFFFFF, false);
    }

    @Nullable
    private static BlockPos findRespawnPos(Player player) {
        var sleepingPos = player.getSleepingPos().orElse(null);
        var currentPos = player.blockPosition();
        for (var f : posFields) {
            try {
                var val = (BlockPos) f.get(player);
                if (val != null && !val.equals(sleepingPos) && !val.equals(currentPos)) {
                    return val;
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }
}
