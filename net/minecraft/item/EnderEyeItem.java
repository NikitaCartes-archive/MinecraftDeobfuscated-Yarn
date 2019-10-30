/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.EndPortalFrameBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.EnderEyeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public class EnderEyeItem
extends Item {
    public EnderEyeItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
        BlockPos blockPos;
        World world = itemUsageContext.getWorld();
        BlockState blockState = world.getBlockState(blockPos = itemUsageContext.getBlockPos());
        if (blockState.getBlock() != Blocks.END_PORTAL_FRAME || blockState.get(EndPortalFrameBlock.EYE).booleanValue()) {
            return ActionResult.PASS;
        }
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        BlockState blockState2 = (BlockState)blockState.with(EndPortalFrameBlock.EYE, true);
        Block.pushEntitiesUpBeforeBlockChange(blockState, blockState2, world, blockPos);
        world.setBlockState(blockPos, blockState2, 2);
        world.updateHorizontalAdjacent(blockPos, Blocks.END_PORTAL_FRAME);
        itemUsageContext.getStack().decrement(1);
        world.playLevelEvent(1503, blockPos, 0);
        BlockPattern.Result result = EndPortalFrameBlock.getCompletedFramePattern().searchAround(world, blockPos);
        if (result != null) {
            BlockPos blockPos2 = result.getFrontTopLeft().add(-3, 0, -3);
            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 3; ++j) {
                    world.setBlockState(blockPos2.add(i, 0, j), Blocks.END_PORTAL.getDefaultState(), 2);
                }
            }
            world.playGlobalEvent(1038, blockPos2.add(1, 0, 1), 0);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        BlockPos blockPos;
        ItemStack itemStack = playerEntity.getStackInHand(hand);
        HitResult hitResult = EnderEyeItem.rayTrace(world, playerEntity, RayTraceContext.FluidHandling.NONE);
        if (hitResult.getType() == HitResult.Type.BLOCK && world.getBlockState(((BlockHitResult)hitResult).getBlockPos()).getBlock() == Blocks.END_PORTAL_FRAME) {
            return TypedActionResult.pass(itemStack);
        }
        playerEntity.setCurrentHand(hand);
        if (world instanceof ServerWorld && (blockPos = ((ServerWorld)world).method_14178().getChunkGenerator().locateStructure(world, "Stronghold", new BlockPos(playerEntity), 100, false)) != null) {
            EnderEyeEntity enderEyeEntity = new EnderEyeEntity(world, playerEntity.getX(), playerEntity.getHeightAt(0.5), playerEntity.getZ());
            enderEyeEntity.setItem(itemStack);
            enderEyeEntity.moveTowards(blockPos);
            world.spawnEntity(enderEyeEntity);
            if (playerEntity instanceof ServerPlayerEntity) {
                Criterions.USED_ENDER_EYE.trigger((ServerPlayerEntity)playerEntity, blockPos);
            }
            world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_ENDER_EYE_LAUNCH, SoundCategory.NEUTRAL, 0.5f, 0.4f / (RANDOM.nextFloat() * 0.4f + 0.8f));
            world.playLevelEvent(null, 1003, new BlockPos(playerEntity), 0);
            if (!playerEntity.abilities.creativeMode) {
                itemStack.decrement(1);
            }
            playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
            return TypedActionResult.success(itemStack);
        }
        return TypedActionResult.success(itemStack);
    }
}

