/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FireChargeItem
extends Item {
    public FireChargeItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        boolean bl = false;
        if (blockState.method_27851(BlockTags.CAMPFIRES, abstractBlockState -> abstractBlockState.contains(CampfireBlock.LIT) && abstractBlockState.contains(CampfireBlock.WATERLOGGED))) {
            if (!blockState.get(CampfireBlock.LIT).booleanValue() && !blockState.get(CampfireBlock.WATERLOGGED).booleanValue()) {
                this.playUseSound(world, blockPos);
                world.setBlockState(blockPos, (BlockState)blockState.with(CampfireBlock.LIT, true));
                bl = true;
            }
        } else if (world.getBlockState(blockPos = blockPos.offset(context.getSide())).isAir()) {
            this.playUseSound(world, blockPos);
            world.setBlockState(blockPos, AbstractFireBlock.getState(world, blockPos));
            bl = true;
        }
        if (bl) {
            context.getStack().decrement(1);
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    private void playUseSound(World world, BlockPos pos) {
        world.playSound(null, pos, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 1.0f, (RANDOM.nextFloat() - RANDOM.nextFloat()) * 0.2f + 1.0f);
    }
}

