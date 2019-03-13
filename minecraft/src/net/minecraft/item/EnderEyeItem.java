package net.minecraft.item;

import net.minecraft.advancement.criterion.Criterions;
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
	public ActionResult method_7884(ItemUsageContext itemUsageContext) {
		World world = itemUsageContext.method_8045();
		BlockPos blockPos = itemUsageContext.method_8037();
		BlockState blockState = world.method_8320(blockPos);
		if (blockState.getBlock() != Blocks.field_10398 || (Boolean)blockState.method_11654(EndPortalFrameBlock.field_10958)) {
			return ActionResult.PASS;
		} else if (world.isClient) {
			return ActionResult.field_5812;
		} else {
			BlockState blockState2 = blockState.method_11657(EndPortalFrameBlock.field_10958, Boolean.valueOf(true));
			Block.method_9582(blockState, blockState2, world, blockPos);
			world.method_8652(blockPos, blockState2, 2);
			world.method_8455(blockPos, Blocks.field_10398);
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

			world.method_8396(null, blockPos, SoundEvents.field_14874, SoundCategory.field_15245, 1.0F, 1.0F);
			BlockPattern.Result result = EndPortalFrameBlock.method_10054().method_11708(world, blockPos);
			if (result != null) {
				BlockPos blockPos2 = result.method_11715().add(-3, 0, -3);

				for (int k = 0; k < 3; k++) {
					for (int l = 0; l < 3; l++) {
						world.method_8652(blockPos2.add(k, 0, l), Blocks.field_10027.method_9564(), 2);
					}
				}

				world.method_8474(1038, blockPos2.add(1, 0, 1), 0);
			}

			return ActionResult.field_5812;
		}
	}

	@Override
	public TypedActionResult<ItemStack> method_7836(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.method_5998(hand);
		HitResult hitResult = method_7872(world, playerEntity, RayTraceContext.FluidHandling.NONE);
		if (hitResult.getType() == HitResult.Type.BLOCK && world.method_8320(((BlockHitResult)hitResult).method_17777()).getBlock() == Blocks.field_10398) {
			return new TypedActionResult<>(ActionResult.PASS, itemStack);
		} else {
			playerEntity.setCurrentHand(hand);
			if (!world.isClient) {
				BlockPos blockPos = world.method_8398().getChunkGenerator().method_12103(world, "Stronghold", new BlockPos(playerEntity), 100, false);
				if (blockPos != null) {
					EnderEyeEntity enderEyeEntity = new EnderEyeEntity(world, playerEntity.x, playerEntity.y + (double)(playerEntity.getHeight() / 2.0F), playerEntity.z);
					enderEyeEntity.method_16933(itemStack);
					enderEyeEntity.method_7478(blockPos);
					world.spawnEntity(enderEyeEntity);
					if (playerEntity instanceof ServerPlayerEntity) {
						Criterions.USED_ENDER_EYE.method_9157((ServerPlayerEntity)playerEntity, blockPos);
					}

					world.method_8465(
						null, playerEntity.x, playerEntity.y, playerEntity.z, SoundEvents.field_15155, SoundCategory.field_15254, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F)
					);
					world.method_8444(null, 1003, new BlockPos(playerEntity), 0);
					if (!playerEntity.abilities.creativeMode) {
						itemStack.subtractAmount(1);
					}

					playerEntity.method_7259(Stats.field_15372.getOrCreateStat(this));
					return new TypedActionResult<>(ActionResult.field_5812, itemStack);
				}
			}

			return new TypedActionResult<>(ActionResult.field_5812, itemStack);
		}
	}
}
