package net.minecraft.item;

import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.EndPortalFrameBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.EnderEyeEntity;
import net.minecraft.entity.player.PlayerEntity;
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

public class EnderEyeItem extends Item {
	public EnderEyeItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		World world = itemUsageContext.getWorld();
		BlockPos blockPos = itemUsageContext.getBlockPos();
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getBlock() != Blocks.END_PORTAL_FRAME || (Boolean)blockState.get(EndPortalFrameBlock.EYE)) {
			return ActionResult.PASS;
		} else if (world.isClient) {
			return ActionResult.SUCCESS;
		} else {
			BlockState blockState2 = blockState.with(EndPortalFrameBlock.EYE, Boolean.valueOf(true));
			Block.pushEntitiesUpBeforeBlockChange(blockState, blockState2, world, blockPos);
			world.setBlockState(blockPos, blockState2, 2);
			world.updateHorizontalAdjacent(blockPos, Blocks.END_PORTAL_FRAME);
			itemUsageContext.getStack().decrement(1);
			world.playLevelEvent(1503, blockPos, 0);
			BlockPattern.Result result = EndPortalFrameBlock.getCompletedFramePattern().searchAround(world, blockPos);
			if (result != null) {
				BlockPos blockPos2 = result.getFrontTopLeft().add(-3, 0, -3);

				for (int i = 0; i < 3; i++) {
					for (int j = 0; j < 3; j++) {
						world.setBlockState(blockPos2.add(i, 0, j), Blocks.END_PORTAL.getDefaultState(), 2);
					}
				}

				world.playGlobalEvent(1038, blockPos2.add(1, 0, 1), 0);
			}

			return ActionResult.SUCCESS;
		}
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		HitResult hitResult = rayTrace(world, playerEntity, RayTraceContext.FluidHandling.NONE);
		if (hitResult.getType() == HitResult.Type.BLOCK && world.getBlockState(((BlockHitResult)hitResult).getBlockPos()).getBlock() == Blocks.END_PORTAL_FRAME) {
			return TypedActionResult.pass(itemStack);
		} else {
			playerEntity.setCurrentHand(hand);
			if (world instanceof ServerWorld) {
				BlockPos blockPos = ((ServerWorld)world).method_14178().getChunkGenerator().locateStructure(world, "Stronghold", new BlockPos(playerEntity), 100, false);
				if (blockPos != null) {
					EnderEyeEntity enderEyeEntity = new EnderEyeEntity(world, playerEntity.getX(), playerEntity.method_23323(0.5), playerEntity.getZ());
					enderEyeEntity.setItem(itemStack);
					enderEyeEntity.moveTowards(blockPos);
					world.spawnEntity(enderEyeEntity);
					if (playerEntity instanceof ServerPlayerEntity) {
						Criterions.USED_ENDER_EYE.trigger((ServerPlayerEntity)playerEntity, blockPos);
					}

					world.playSound(
						null,
						playerEntity.getX(),
						playerEntity.getY(),
						playerEntity.getZ(),
						SoundEvents.ENTITY_ENDER_EYE_LAUNCH,
						SoundCategory.NEUTRAL,
						0.5F,
						0.4F / (RANDOM.nextFloat() * 0.4F + 0.8F)
					);
					world.playLevelEvent(null, 1003, new BlockPos(playerEntity), 0);
					if (!playerEntity.abilities.creativeMode) {
						itemStack.decrement(1);
					}

					playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
					return TypedActionResult.successWithSwing(itemStack);
				}
			}

			return TypedActionResult.successWithSwing(itemStack);
		}
	}
}
