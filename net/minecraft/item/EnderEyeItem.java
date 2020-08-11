/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.EndPortalFrameBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.EyeOfEnderEntity;
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
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.StructureFeature;

public class EnderEyeItem
extends Item {
    public EnderEyeItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos blockPos;
        World world = context.getWorld();
        BlockState blockState = world.getBlockState(blockPos = context.getBlockPos());
        if (!blockState.isOf(Blocks.END_PORTAL_FRAME) || blockState.get(EndPortalFrameBlock.EYE).booleanValue()) {
            return ActionResult.PASS;
        }
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        BlockState blockState2 = (BlockState)blockState.with(EndPortalFrameBlock.EYE, true);
        Block.pushEntitiesUpBeforeBlockChange(blockState, blockState2, world, blockPos);
        world.setBlockState(blockPos, blockState2, 2);
        world.updateComparators(blockPos, Blocks.END_PORTAL_FRAME);
        context.getStack().decrement(1);
        world.syncWorldEvent(1503, blockPos, 0);
        BlockPattern.Result result = EndPortalFrameBlock.getCompletedFramePattern().searchAround(world, blockPos);
        if (result != null) {
            BlockPos blockPos2 = result.getFrontTopLeft().add(-3, 0, -3);
            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 3; ++j) {
                    world.setBlockState(blockPos2.add(i, 0, j), Blocks.END_PORTAL.getDefaultState(), 2);
                }
            }
            world.syncGlobalEvent(1038, blockPos2.add(1, 0, 1), 0);
        }
        return ActionResult.CONSUME;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        BlockPos blockPos;
        ItemStack itemStack = user.getStackInHand(hand);
        BlockHitResult hitResult = EnderEyeItem.raycast(world, user, RaycastContext.FluidHandling.NONE);
        if (((HitResult)hitResult).getType() == HitResult.Type.BLOCK && world.getBlockState(hitResult.getBlockPos()).isOf(Blocks.END_PORTAL_FRAME)) {
            return TypedActionResult.pass(itemStack);
        }
        user.setCurrentHand(hand);
        if (world instanceof ServerWorld && (blockPos = ((ServerWorld)world).getChunkManager().getChunkGenerator().locateStructure((ServerWorld)world, StructureFeature.STRONGHOLD, user.getBlockPos(), 100, false)) != null) {
            EyeOfEnderEntity eyeOfEnderEntity = new EyeOfEnderEntity(world, user.getX(), user.getBodyY(0.5), user.getZ());
            eyeOfEnderEntity.setItem(itemStack);
            eyeOfEnderEntity.initTargetPos(blockPos);
            world.spawnEntity(eyeOfEnderEntity);
            if (user instanceof ServerPlayerEntity) {
                Criteria.USED_ENDER_EYE.trigger((ServerPlayerEntity)user, blockPos);
            }
            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_ENDER_EYE_LAUNCH, SoundCategory.NEUTRAL, 0.5f, 0.4f / (RANDOM.nextFloat() * 0.4f + 0.8f));
            world.syncWorldEvent(null, 1003, user.getBlockPos(), 0);
            if (!user.abilities.creativeMode) {
                itemStack.decrement(1);
            }
            user.incrementStat(Stats.USED.getOrCreateStat(this));
            user.swingHand(hand, true);
            return TypedActionResult.success(itemStack);
        }
        return TypedActionResult.consume(itemStack);
    }
}

