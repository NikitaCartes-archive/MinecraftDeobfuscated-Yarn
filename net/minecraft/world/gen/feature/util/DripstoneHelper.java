/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature.util;

import java.util.function.Consumer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PointedDripstoneBlock;
import net.minecraft.block.enums.Thickness;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;

public class DripstoneHelper {
    protected static double scaleHeightFromRadius(double radius, double scale, double heightScale, double bluntness) {
        if (radius < bluntness) {
            radius = bluntness;
        }
        double d = 0.384;
        double e = radius / scale * 0.384;
        double f = 0.75 * Math.pow(e, 1.3333333333333333);
        double g = Math.pow(e, 0.6666666666666666);
        double h = 0.3333333333333333 * Math.log(e);
        double i = heightScale * (f - g - h);
        i = Math.max(i, 0.0);
        return i / 0.384 * scale;
    }

    protected static boolean canGenerateBase(StructureWorldAccess world, BlockPos pos, int height) {
        if (DripstoneHelper.canGenerateOrLava(world, pos)) {
            return false;
        }
        float f = 6.0f;
        float g = 6.0f / (float)height;
        for (float h = 0.0f; h < (float)Math.PI * 2; h += g) {
            int j;
            int i = (int)(MathHelper.cos(h) * (float)height);
            if (!DripstoneHelper.canGenerateOrLava(world, pos.add(i, 0, j = (int)(MathHelper.sin(h) * (float)height)))) continue;
            return false;
        }
        return true;
    }

    protected static boolean canGenerate(WorldAccess world, BlockPos pos) {
        return world.testBlockState(pos, DripstoneHelper::canGenerate);
    }

    protected static boolean canGenerateOrLava(WorldAccess world, BlockPos pos) {
        return world.testBlockState(pos, DripstoneHelper::canGenerateOrLava);
    }

    protected static void getDripstoneThickness(Direction direction, int height, boolean merge, Consumer<BlockState> callback) {
        if (height >= 3) {
            callback.accept(DripstoneHelper.getState(direction, Thickness.BASE));
            for (int i = 0; i < height - 3; ++i) {
                callback.accept(DripstoneHelper.getState(direction, Thickness.MIDDLE));
            }
        }
        if (height >= 2) {
            callback.accept(DripstoneHelper.getState(direction, Thickness.FRUSTUM));
        }
        if (height >= 1) {
            callback.accept(DripstoneHelper.getState(direction, merge ? Thickness.TIP_MERGE : Thickness.TIP));
        }
    }

    protected static void generatePointedDripstone(WorldAccess worldAccess, BlockPos pos, Direction direction, int height, boolean merge) {
        BlockPos.Mutable mutable = pos.mutableCopy();
        DripstoneHelper.getDripstoneThickness(direction, height, merge, state -> {
            if (state.isOf(Blocks.POINTED_DRIPSTONE)) {
                state = (BlockState)state.with(PointedDripstoneBlock.WATERLOGGED, worldAccess.isWater(mutable));
            }
            worldAccess.setBlockState(mutable, (BlockState)state, Block.NOTIFY_LISTENERS);
            mutable.move(direction);
        });
    }

    protected static boolean generateDripstoneBlock(WorldAccess worldAccess, BlockPos pos) {
        BlockState blockState = worldAccess.getBlockState(pos);
        if (blockState.isIn(BlockTags.DRIPSTONE_REPLACEABLE_BLOCKS)) {
            worldAccess.setBlockState(pos, Blocks.DRIPSTONE_BLOCK.getDefaultState(), Block.NOTIFY_LISTENERS);
            return true;
        }
        return false;
    }

    private static BlockState getState(Direction direction, Thickness thickness) {
        return (BlockState)((BlockState)Blocks.POINTED_DRIPSTONE.getDefaultState().with(PointedDripstoneBlock.VERTICAL_DIRECTION, direction)).with(PointedDripstoneBlock.THICKNESS, thickness);
    }

    public static boolean canReplaceOrLava(BlockState state) {
        return DripstoneHelper.canReplace(state) || state.isOf(Blocks.LAVA);
    }

    public static boolean canReplace(BlockState state) {
        return state.isOf(Blocks.DRIPSTONE_BLOCK) || state.isIn(BlockTags.DRIPSTONE_REPLACEABLE_BLOCKS);
    }

    public static boolean canGenerate(BlockState state) {
        return state.isAir() || state.isOf(Blocks.WATER);
    }

    public static boolean canGenerateOrLava(BlockState state) {
        return state.isAir() || state.isOf(Blocks.WATER) || state.isOf(Blocks.LAVA);
    }
}

