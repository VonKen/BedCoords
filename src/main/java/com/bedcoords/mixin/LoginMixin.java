package com.bedcoords.mixin;

import com.bedcoords.BedCoordsData;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.common.ClientboundLoginPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class LoginMixin {
    @Inject(method = "handleLogin", at = @At("TAIL"))
    private void onHandleLogin(ClientboundLoginPacket packet, CallbackInfo ci) {
        var info = packet.commonPlayerSpawnInfo();
        BedCoordsData.respawnPosition = info.respawnPosition().orElse(null);
        BedCoordsData.respawnDimension = info.dimension();
    }
}
