/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.block.PortalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class FlintAndSteelItem
extends Item {
    public FlintAndSteelItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
        BlockPos blockPos;
        BlockPos blockPos2;
        PlayerEntity playerEntity2 = itemUsageContext.getPlayer();
        World iWorld = itemUsageContext.getWorld();
        if (FlintAndSteelItem.canIgnite(iWorld.getBlockState(blockPos2 = (blockPos = itemUsageContext.getBlockPos()).offset(itemUsageContext.getSide())), iWorld, blockPos2)) {
            iWorld.playSound(playerEntity2, blockPos2, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0f, RANDOM.nextFloat() * 0.4f + 0.8f);
            BlockState blockState = ((FireBlock)Blocks.FIRE).getStateForPosition(iWorld, blockPos2);
            iWorld.setBlockState(blockPos2, blockState, 11);
            ItemStack itemStack = itemUsageContext.getStack();
            if (playerEntity2 instanceof ServerPlayerEntity) {
                Criterions.PLACED_BLOCK.trigger((ServerPlayerEntity)playerEntity2, blockPos2, itemStack);
                itemStack.damage(1, playerEntity2, playerEntity -> playerEntity.sendToolBreakStatus(itemUsageContext.getHand()));
            }
            return ActionResult.SUCCESS;
        }
        BlockState blockState = iWorld.getBlockState(blockPos);
        if (FlintAndSteelItem.isIgnitable(blockState)) {
            iWorld.playSound(playerEntity2, blockPos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0f, RANDOM.nextFloat() * 0.4f + 0.8f);
            iWorld.setBlockState(blockPos, (BlockState)blockState.with(Properties.LIT, true), 11);
            if (playerEntity2 != null) {
                itemUsageContext.getStack().damage(1, playerEntity2, playerEntity -> playerEntity.sendToolBreakStatus(itemUsageContext.getHand()));
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    public static boolean isIgnitable(BlockState blockState) {
        return blockState.getBlock() == Blocks.CAMPFIRE && blockState.get(Properties.WATERLOGGED) == false && blockState.get(Properties.LIT) == false;
    }

    public static boolean canIgnite(BlockState blockState, IWorld iWorld, BlockPos blockPos) {
        BlockState blockState2 = ((FireBlock)Blocks.FIRE).getStateForPosition(iWorld, blockPos);
        boolean bl = false;
        for (Direction direction : Direction.Type.HORIZONTAL) {
            if (iWorld.getBlockState(blockPos.offset(direction)).getBlock() != Blocks.OBSIDIAN || ((PortalBlock)Blocks.NETHER_PORTAL).createAreaHelper(iWorld, blockPos) == null) continue;
            bl = true;
        }
        return blockState.isAir() && (blockState2.canPlaceAt(iWorld, blockPos) || bl);
    }
}

