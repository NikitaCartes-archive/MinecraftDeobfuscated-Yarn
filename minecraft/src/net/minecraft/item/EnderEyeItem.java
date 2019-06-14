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
		World world = itemUsageContext.method_8045();
		BlockPos blockPos = itemUsageContext.getBlockPos();
		BlockState blockState = world.method_8320(blockPos);
		if (blockState.getBlock() != Blocks.field_10398 || (Boolean)blockState.method_11654(EndPortalFrameBlock.field_10958)) {
			return ActionResult.field_5811;
		} else if (world.isClient) {
			return ActionResult.field_5812;
		} else {
			BlockState blockState2 = blockState.method_11657(EndPortalFrameBlock.field_10958, Boolean.valueOf(true));
			Block.method_9582(blockState, blockState2, world, blockPos);
			world.method_8652(blockPos, blockState2, 2);
			world.method_8455(blockPos, Blocks.field_10398);
			itemUsageContext.getStack().decrement(1);
			world.playLevelEvent(1503, blockPos, 0);
			BlockPattern.Result result = EndPortalFrameBlock.method_10054().searchAround(world, blockPos);
			if (result != null) {
				BlockPos blockPos2 = result.getFrontTopLeft().add(-3, 0, -3);

				for (int i = 0; i < 3; i++) {
					for (int j = 0; j < 3; j++) {
						world.method_8652(blockPos2.add(i, 0, j), Blocks.field_10027.method_9564(), 2);
					}
				}

				world.playGlobalEvent(1038, blockPos2.add(1, 0, 1), 0);
			}

			return ActionResult.field_5812;
		}
	}

	@Override
	public TypedActionResult<ItemStack> method_7836(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		HitResult hitResult = method_7872(world, playerEntity, RayTraceContext.FluidHandling.field_1348);
		if (hitResult.getType() == HitResult.Type.field_1332 && world.method_8320(((BlockHitResult)hitResult).getBlockPos()).getBlock() == Blocks.field_10398) {
			return new TypedActionResult<>(ActionResult.field_5811, itemStack);
		} else {
			playerEntity.setCurrentHand(hand);
			if (!world.isClient) {
				BlockPos blockPos = world.method_8398().getChunkGenerator().locateStructure(world, "Stronghold", new BlockPos(playerEntity), 100, false);
				if (blockPos != null) {
					EnderEyeEntity enderEyeEntity = new EnderEyeEntity(world, playerEntity.x, playerEntity.y + (double)(playerEntity.getHeight() / 2.0F), playerEntity.z);
					enderEyeEntity.setItem(itemStack);
					enderEyeEntity.moveTowards(blockPos);
					world.spawnEntity(enderEyeEntity);
					if (playerEntity instanceof ServerPlayerEntity) {
						Criterions.USED_ENDER_EYE.handle((ServerPlayerEntity)playerEntity, blockPos);
					}

					world.playSound(
						null, playerEntity.x, playerEntity.y, playerEntity.z, SoundEvents.field_15155, SoundCategory.field_15254, 0.5F, 0.4F / (RANDOM.nextFloat() * 0.4F + 0.8F)
					);
					world.playLevelEvent(null, 1003, new BlockPos(playerEntity), 0);
					if (!playerEntity.abilities.creativeMode) {
						itemStack.decrement(1);
					}

					playerEntity.incrementStat(Stats.field_15372.getOrCreateStat(this));
					return new TypedActionResult<>(ActionResult.field_5812, itemStack);
				}
			}

			return new TypedActionResult<>(ActionResult.field_5812, itemStack);
		}
	}
}
