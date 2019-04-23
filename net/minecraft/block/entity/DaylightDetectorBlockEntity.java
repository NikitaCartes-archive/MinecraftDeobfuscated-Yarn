/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DaylightDetectorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Tickable;

public class DaylightDetectorBlockEntity
extends BlockEntity
implements Tickable {
    public DaylightDetectorBlockEntity() {
        super(BlockEntityType.DAYLIGHT_DETECTOR);
    }

    @Override
    public void tick() {
        BlockState blockState;
        Block block;
        if (this.world != null && !this.world.isClient && this.world.getTime() % 20L == 0L && (block = (blockState = this.getCachedState()).getBlock()) instanceof DaylightDetectorBlock) {
            DaylightDetectorBlock.updateState(blockState, this.world, this.pos);
        }
    }
}

