package com.bedcoords;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class BedCoordsData {
    @Nullable
    public static BlockPos respawnPosition;

    public static ResourceKey<Level> respawnDimension;
}
