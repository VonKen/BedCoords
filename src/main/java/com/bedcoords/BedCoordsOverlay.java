package com.bedcoords;

import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.gui.overlay.ExtendedGui;
import net.neoforged.neoforge.client.gui.overlay.IGuiOverlay;

public class BedCoordsOverlay implements IGuiOverlay {
    private static final int COLOR = 0xFFFFFFFF;

    @Override
    public void render(ExtendedGui gui, net.minecraft.client.gui.GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        var mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        var font = mc.font;
        var spawn = mc.player.getRespawnPosition();

        String text;
        if (spawn.isPresent()) {
            var pos = spawn.get().pos();
            var dim = spawn.get().dimension();
            var dimName = dim.location().toShortLanguageKey();
            text = String.format("Bed [%s] %d, %d, %d", dimName, pos.getX(), pos.getY(), pos.getZ());
        } else {
            text = "No bed set";
        }

        int textWidth = font.width(text);
        int x = screenWidth - textWidth - 4;
        int y = 4;

        guiGraphics.fill(x - 1, y - 1, x + textWidth + 1, y + font.lineHeight + 1, 0xAA000000);
        guiGraphics.drawString(font, text, x, y, COLOR, false);
    }
}
