package com.bedcoords;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterGuiOverlaysEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(BedCoordsMod.MOD_ID)
public class BedCoordsMod {
    public static final String MOD_ID = "bedcoords";

    public BedCoordsMod(IEventBus modBus) {
        modBus.addListener(this::onRegisterOverlays);
    }

    private void onRegisterOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("bed_coords", new BedCoordsOverlay());
    }
}
