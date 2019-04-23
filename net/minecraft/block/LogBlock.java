/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MaterialColor;
import net.minecraft.block.PillarBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public class LogBlock
extends PillarBlock {
    private final MaterialColor endMaterialColor;

    public LogBlock(MaterialColor materialColor, Block.Settings settings) {
        super(settings);
        this.endMaterialColor = materialColor;
    }

    @Override
    public MaterialColor getMapColor(BlockState blockState, BlockView blockView, BlockPos blockPos) {
        return blockState.get(AXIS) == Direction.Axis.Y ? this.endMaterialColor : this.materialColor;
    }
}

