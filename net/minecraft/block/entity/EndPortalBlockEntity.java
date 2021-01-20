/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class EndPortalBlockEntity
extends BlockEntity {
    protected EndPortalBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public EndPortalBlockEntity(BlockPos pos, BlockState state) {
        this(BlockEntityType.END_PORTAL, pos, state);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldDrawSide(Direction direction) {
        return direction == Direction.UP;
    }
}

