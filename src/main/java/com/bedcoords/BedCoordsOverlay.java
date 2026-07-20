package com.bedcoords;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;

public class BedCoordsOverlay {
    public static void onRenderGui(RenderGuiLayerEvent.Post event) {
        var mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        var font = mc.font;
        var guiGraphics = event.getGuiGraphics();
        String text;

        BlockPos pos = mc.player.getRespawnPosition();
        ResourceKey<Level> dim = mc.player.getRespawnDimension();
        if (pos != null) {
            var dimName = dim.location().toShortLanguageKey();
            text = String.format("Bed [%s] %d, %d, %d", dimName, pos.getX(), pos.getY(), pos.getZ());
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
}
