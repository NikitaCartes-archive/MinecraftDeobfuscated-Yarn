/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.OreBlock;
import net.minecraft.block.Waterloggable;
import net.minecraft.class_7124;
import net.minecraft.class_7128;
import net.minecraft.fluid.Fluids;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.world.WorldAccess;

public class SculkBlock
extends OreBlock
implements class_7124 {
    public SculkBlock(AbstractBlock.Settings settings) {
        super(settings, ConstantIntProvider.create(1));
    }

    @Override
    public int method_41471(class_7128.class_7129 arg, WorldAccess worldAccess, BlockPos blockPos, Random random, class_7128 arg2, boolean bl) {
        int i = arg.method_41508();
        if (i == 0 || random.nextInt(arg2.method_41490()) != 0) {
            return i;
        }
        BlockPos blockPos2 = arg.method_41495();
        boolean bl2 = blockPos2.isWithinDistance(blockPos, (double)arg2.method_41489());
        if (bl2 || !SculkBlock.method_41474(worldAccess, blockPos2)) {
            if (random.nextInt(arg2.method_41491()) != 0) {
                return i;
            }
            return i - (bl2 ? 1 : SculkBlock.method_41476(arg2, blockPos2, blockPos, i));
        }
        int j = arg2.method_41488();
        if (random.nextInt(j) < i) {
            BlockPos blockPos3 = blockPos2.up();
            BlockState blockState = this.method_41475(worldAccess, blockPos3, random, arg2.method_41492());
            worldAccess.setBlockState(blockPos3, blockState, Block.NOTIFY_ALL);
            worldAccess.playSound(null, blockPos2, blockState.getSoundGroup().getPlaceSound(), SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
        return Math.max(0, i - j);
    }

    private static int method_41476(class_7128 arg, BlockPos blockPos, BlockPos blockPos2, int i) {
        int j = arg.method_41489();
        float f = MathHelper.square((float)Math.sqrt(blockPos.getSquaredDistance(blockPos2)) - (float)j);
        int k = MathHelper.square(24 - j);
        float g = Math.min(1.0f, f / (float)k);
        return Math.max(1, (int)((float)i * g * 0.5f));
    }

    private BlockState method_41475(WorldAccess worldAccess, BlockPos blockPos, Random random, boolean bl) {
        Block block = bl ? (random.nextInt(11) == 0 ? Blocks.SCULK_SHRIEKER : Blocks.SCULK_SENSOR) : Blocks.SCULK_SENSOR;
        BlockState blockState = block.getDefaultState();
        if (!worldAccess.getFluidState(blockPos).isEmpty() && block instanceof Waterloggable) {
            return (BlockState)blockState.with(Properties.WATERLOGGED, true);
        }
        return blockState;
    }

    private static boolean method_41474(WorldAccess worldAccess, BlockPos blockPos) {
        BlockState blockState = worldAccess.getBlockState(blockPos.up());
        if (!(blockState.isAir() || blockState.isOf(Blocks.WATER) && blockState.getFluidState().isOf(Fluids.WATER))) {
            return false;
        }
        int i = 0;
        for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-4, 0, -4), blockPos.add(4, 2, 4))) {
            BlockState blockState2 = worldAccess.getBlockState(blockPos2);
            if (blockState2.isOf(Blocks.SCULK_SENSOR) || blockState2.isOf(Blocks.SCULK_SHRIEKER)) {
                ++i;
            }
            if (i <= 2) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean method_41472() {
        return false;
    }
}

