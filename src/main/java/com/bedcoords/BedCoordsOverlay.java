package com.bedcoords;

import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;

public class BedCoordsOverlay {
    public static void onRenderGui(RenderGuiLayerEvent.Post event) {
        var mc = Minecraft.getInstance();
        if (mc.player == null) return;

        var font = mc.font;
        var guiGraphics = event.getGuiGraphics();
        String text;

        var pos = BedCoordsData.respawnPosition;
        if (pos != null) {
            var dim = BedCoordsData.respawnDimension;
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
