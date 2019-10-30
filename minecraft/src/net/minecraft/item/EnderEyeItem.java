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
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
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
			context.getStack().decrement(1);
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
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		HitResult hitResult = rayTrace(world, user, RayTraceContext.FluidHandling.NONE);
		if (hitResult.getType() == HitResult.Type.BLOCK && world.getBlockState(((BlockHitResult)hitResult).getBlockPos()).getBlock() == Blocks.END_PORTAL_FRAME) {
			return TypedActionResult.pass(itemStack);
		} else {
			user.setCurrentHand(hand);
			if (world instanceof ServerWorld) {
				BlockPos blockPos = ((ServerWorld)world).method_14178().getChunkGenerator().locateStructure(world, "Stronghold", new BlockPos(user), 100, false);
				if (blockPos != null) {
					EnderEyeEntity enderEyeEntity = new EnderEyeEntity(world, user.getX(), user.getHeightAt(0.5), user.getZ());
					enderEyeEntity.setItem(itemStack);
					enderEyeEntity.moveTowards(blockPos);
					world.spawnEntity(enderEyeEntity);
					if (user instanceof ServerPlayerEntity) {
						Criterions.USED_ENDER_EYE.trigger((ServerPlayerEntity)user, blockPos);
					}

					world.playSound(
						null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_ENDER_EYE_LAUNCH, SoundCategory.NEUTRAL, 0.5F, 0.4F / (RANDOM.nextFloat() * 0.4F + 0.8F)
					);
					world.playLevelEvent(null, 1003, new BlockPos(user), 0);
					if (!user.abilities.creativeMode) {
						itemStack.decrement(1);
					}

					user.incrementStat(Stats.USED.getOrCreateStat(this));
					return TypedActionResult.success(itemStack);
				}
			}

			return TypedActionResult.success(itemStack);
		}
	}
}
