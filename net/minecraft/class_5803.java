/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

public interface class_5803 {
    public static final VoxelShape field_28687 = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);
    public static final BooleanProperty field_28688 = Properties.field_28716;

    public static ActionResult method_33619(BlockState blockState, World world, BlockPos blockPos) {
        if (blockState.get(field_28688).booleanValue()) {
            Block.dropStack(world, blockPos, new ItemStack(Items.GLOW_BERRIES, 1));
            float f = MathHelper.nextBetween(world.random, 0.8f, 1.2f);
            world.playSound(null, blockPos, SoundEvents.BLOCK_CAVE_VINES_PICK_BERRIES, SoundCategory.BLOCKS, 1.0f, f);
            world.setBlockState(blockPos, (BlockState)blockState.with(field_28688, false), 2);
            return ActionResult.success(world.isClient);
        }
        return ActionResult.PASS;
    }

    public static boolean method_33618(BlockState blockState) {
        return blockState.contains(field_28688) && blockState.get(field_28688) != false;
    }
}

