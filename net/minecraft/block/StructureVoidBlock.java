/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.EntityContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class StructureVoidBlock
extends Block {
    private static final VoxelShape SHAPE = Block.createCuboidShape(5.0, 5.0, 5.0, 11.0, 11.0, 11.0);

    protected StructureVoidBlock(Block.Settings settings) {
        super(settings);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
        return SHAPE;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView view, BlockPos pos) {
        return 1.0f;
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState state) {
        return PistonBehavior.DESTROY;
    }
}

