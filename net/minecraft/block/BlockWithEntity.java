/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class BlockWithEntity
extends Block
implements BlockEntityProvider {
    protected BlockWithEntity(Block.Settings settings) {
        super(settings);
    }

    @Override
    public BlockRenderType getRenderType(BlockState blockState) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public boolean onBlockAction(BlockState blockState, World world, BlockPos blockPos, int i, int j) {
        super.onBlockAction(blockState, world, blockPos, i, j);
        BlockEntity blockEntity = world.getBlockEntity(blockPos);
        if (blockEntity == null) {
            return false;
        }
        return blockEntity.onBlockAction(i, j);
    }

    @Override
    @Nullable
    public NameableContainerProvider createContainerProvider(BlockState blockState, World world, BlockPos blockPos) {
        BlockEntity blockEntity = world.getBlockEntity(blockPos);
        return blockEntity instanceof NameableContainerProvider ? (NameableContainerProvider)((Object)blockEntity) : null;
    }
}

