/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FireChargeItem
extends Item {
    public FireChargeItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
        World world = itemUsageContext.getWorld();
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        BlockPos blockPos = itemUsageContext.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.getBlock() == Blocks.CAMPFIRE) {
            if (!blockState.get(CampfireBlock.LIT).booleanValue() && !blockState.get(CampfireBlock.WATERLOGGED).booleanValue()) {
                this.playUseSound(world, blockPos);
                world.setBlockState(blockPos, (BlockState)blockState.with(CampfireBlock.LIT, true));
            }
        } else if (world.getBlockState(blockPos = blockPos.offset(itemUsageContext.getFacing())).isAir()) {
            this.playUseSound(world, blockPos);
            world.setBlockState(blockPos, ((FireBlock)Blocks.FIRE).getStateForPosition(world, blockPos));
        }
        itemUsageContext.getItemStack().subtractAmount(1);
        return ActionResult.SUCCESS;
    }

    private void playUseSound(World world, BlockPos blockPos) {
        world.playSound(null, blockPos, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 1.0f, (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f);
    }
}

