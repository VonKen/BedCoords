package com.bedcoords;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.common.NeoForge;

@net.neoforged.fml.common.Mod(BedCoordsMod.MOD_ID)
public class BedCoordsMod {
    public static final String MOD_ID = "bedcoords";

    public BedCoordsMod(IEventBus modBus) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            WaypointStore.load();

            modBus.addListener(RegisterKeyMappingsEvent.class, event -> {
                event.register(BedCoordsOverlay.ADD_WAYPOINT);
                event.register(BedCoordsOverlay.TOGGLE_LIST);
                event.register(BedCoordsOverlay.REMOVE_NEAREST);
            });

            NeoForge.EVENT_BUS.addListener(RenderGuiLayerEvent.Post.class, BedCoordsOverlay::onRenderGui);
        }
    }
}
