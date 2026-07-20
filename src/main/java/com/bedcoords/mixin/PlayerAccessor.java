package com.bedcoords.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Player.class)
public interface PlayerAccessor {
    @Accessor("respawnPosition")
    @Nullable
    BlockPos bedcoords$getRespawnPosition();

    @Accessor("respawnDimension")
    ResourceKey<Level> bedcoords$getRespawnDimension();
}
