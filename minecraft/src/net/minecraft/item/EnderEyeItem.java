package net.minecraft.item;

import net.minecraft.advancement.criterion.CriterionCriterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.EndPortalFrameBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.EnderEyeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.HitResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EnderEyeItem extends Item {
	public EnderEyeItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		World world = itemUsageContext.getWorld();
		BlockPos blockPos = itemUsageContext.getPos();
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getBlock() != Blocks.field_10398 || (Boolean)blockState.get(EndPortalFrameBlock.field_10958)) {
			return ActionResult.PASS;
		} else if (world.isRemote) {
			return ActionResult.SUCCESS;
		} else {
			BlockState blockState2 = blockState.with(EndPortalFrameBlock.field_10958, Boolean.valueOf(true));
			Block.method_9582(blockState, blockState2, world, blockPos);
			world.setBlockState(blockPos, blockState2, 2);
			world.updateHorizontalAdjacent(blockPos, Blocks.field_10398);
			itemUsageContext.getItemStack().subtractAmount(1);

			for (int i = 0; i < 16; i++) {
				double d = (double)((float)blockPos.getX() + (5.0F + random.nextFloat() * 6.0F) / 16.0F);
				double e = (double)((float)blockPos.getY() + 0.8125F);
				double f = (double)((float)blockPos.getZ() + (5.0F + random.nextFloat() * 6.0F) / 16.0F);
				double g = 0.0;
				double h = 0.0;
				double j = 0.0;
				world.method_8406(ParticleTypes.field_11251, d, e, f, 0.0, 0.0, 0.0);
			}

			world.playSound(null, blockPos, SoundEvents.field_14874, SoundCategory.field_15245, 1.0F, 1.0F);
			BlockPattern.Result result = EndPortalFrameBlock.getCompletedFramePattern().searchAround(world, blockPos);
			if (result != null) {
				BlockPos blockPos2 = result.getFrontTopLeft().add(-3, 0, -3);

				for (int k = 0; k < 3; k++) {
					for (int l = 0; l < 3; l++) {
						world.setBlockState(blockPos2.add(k, 0, l), Blocks.field_10027.getDefaultState(), 2);
					}
				}

				world.fireGlobalWorldEvent(1038, blockPos2.add(1, 0, 1), 0);
			}

			return ActionResult.SUCCESS;
		}
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		HitResult hitResult = this.getHitResult(world, playerEntity, false);
		if (hitResult != null && hitResult.type == HitResult.Type.BLOCK && world.getBlockState(hitResult.getBlockPos()).getBlock() == Blocks.field_10398) {
			return new TypedActionResult<>(ActionResult.PASS, itemStack);
		} else {
			playerEntity.setCurrentHand(hand);
			if (!world.isRemote) {
				BlockPos blockPos = world.getChunkManager().getChunkGenerator().locateStructure(world, "Stronghold", new BlockPos(playerEntity), 100, false);
				if (blockPos != null) {
					EnderEyeEntity enderEyeEntity = new EnderEyeEntity(world, playerEntity.x, playerEntity.y + (double)(playerEntity.height / 2.0F), playerEntity.z);
					enderEyeEntity.method_7478(blockPos);
					world.spawnEntity(enderEyeEntity);
					if (playerEntity instanceof ServerPlayerEntity) {
						CriterionCriterions.USED_ENDER_EYE.handle((ServerPlayerEntity)playerEntity, blockPos);
					}

					world.playSound(
						null, playerEntity.x, playerEntity.y, playerEntity.z, SoundEvents.field_15155, SoundCategory.field_15254, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F)
					);
					world.fireWorldEvent(null, 1003, new BlockPos(playerEntity), 0);
					if (!playerEntity.abilities.creativeMode) {
						itemStack.subtractAmount(1);
					}

					playerEntity.incrementStat(Stats.field_15372.method_14956(this));
					return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
				}
			}

			return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
		}
	}
}
