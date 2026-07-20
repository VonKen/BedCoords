package com.bedcoords;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;

public class BedCoordsOverlay {
    public static void onRenderGui(RenderGuiLayerEvent.Post event) {
        var mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        var font = mc.font;
        var guiGraphics = event.getGuiGraphics();

        String text = "No bed set";
        long dimColor = 0xFFFFFFFF;

        var sleepingPos = mc.player.getSleepingPos();
        if (sleepingPos.isPresent()) {
            BlockPos pos = sleepingPos.get();
            text = String.format("Bed %d, %d, %d", pos.getX(), pos.getY(), pos.getZ());
        }

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int textWidth = font.width(text);
        int x = screenWidth - textWidth - 4;
        int y = 4;

        guiGraphics.fill(x - 1, y - 1, x + textWidth + 1, y + font.lineHeight + 1, 0xAA000000);
        guiGraphics.drawString(font, text, x, y, (int)dimColor, false);
    }
}
