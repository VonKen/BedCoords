package com.bedcoords;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

public class WaypointRenderer {
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;

        var mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        if (mc.options.hideGui) return;

        var waypoints = WaypointStore.getWaypoints();
        if (waypoints.isEmpty()) return;

        var camera = event.getCamera();
        var camPos = camera.getPosition();
        var currentDim = mc.level.dimension().location().toString();
        var font = mc.font;
        var bufferSource = mc.renderBuffers().bufferSource();
        var poseStack = event.getPoseStack();

        for (var wp : waypoints) {
            if (!wp.dimension().equals(currentDim)) continue;

            double dx = wp.x() + 0.5 - camPos.x;
            double dy = wp.y() + 1.5 - camPos.y;
            double dz = wp.z() + 0.5 - camPos.z;

            if (dx * dx + dy * dy + dz * dz > 4096.0) continue;

            renderLabel(poseStack, bufferSource, font, camera, wp.name(), dx, dy, dz);
        }

        bufferSource.endBatch();
    }

    private static void renderLabel(PoseStack poseStack, MultiBufferSource bufferSource, Font font,
                                     net.minecraft.client.Camera camera, String text,
                                     double dx, double dy, double dz) {
        poseStack.pushPose();
        poseStack.translate(dx, dy, dz);
        poseStack.mulPose(camera.rotation());
        poseStack.scale(-0.025f, -0.025f, 0.025f);

        var matrices = poseStack.last();
        float halfWidth = font.width(text) / 2f;
        int bgColor = (int)(Minecraft.getInstance().options.getBackgroundOpacity(0.25f) * 255f) << 24;

        font.drawInBatch(text, -halfWidth, -4, 0xFFFFFFFF, false,
            matrices.pose(), bufferSource, Font.DisplayMode.SEE_THROUGH,
            bgColor, 0xF000F0);

        poseStack.popPose();
    }
}
