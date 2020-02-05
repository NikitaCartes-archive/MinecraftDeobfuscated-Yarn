/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.mob;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShapes;

public class ShulkerLidCollisions {
    public static Box getLidCollisionBox(BlockPos pos, Direction direction) {
        return VoxelShapes.fullCube().getBoundingBox().stretch(0.5f * (float)direction.getOffsetX(), 0.5f * (float)direction.getOffsetY(), 0.5f * (float)direction.getOffsetZ()).shrink(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ()).offset(pos.offset(direction));
    }
}

