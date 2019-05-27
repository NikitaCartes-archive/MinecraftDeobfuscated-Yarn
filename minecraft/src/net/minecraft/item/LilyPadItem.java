package net.minecraft.item;

import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
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
import net.minecraft.util.math.Direction;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public class LilyPadItem extends BlockItem {
	public LilyPadItem(Block block, Item.Settings settings) {
		super(block, settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		return ActionResult.field_5811;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		HitResult hitResult = rayTrace(world, playerEntity, RayTraceContext.FluidHandling.field_1345);
		if (hitResult.getType() == HitResult.Type.field_1333) {
			return new TypedActionResult<>(ActionResult.field_5811, itemStack);
		} else {
			if (hitResult.getType() == HitResult.Type.field_1332) {
				BlockHitResult blockHitResult = (BlockHitResult)hitResult;
				BlockPos blockPos = blockHitResult.getBlockPos();
				Direction direction = blockHitResult.getSide();
				if (!world.canPlayerModifyAt(playerEntity, blockPos) || !playerEntity.canPlaceOn(blockPos.offset(direction), direction, itemStack)) {
					return new TypedActionResult<>(ActionResult.field_5814, itemStack);
				}

				BlockPos blockPos2 = blockPos.up();
				BlockState blockState = world.getBlockState(blockPos);
				Material material = blockState.getMaterial();
				FluidState fluidState = world.getFluidState(blockPos);
				if ((fluidState.getFluid() == Fluids.WATER || material == Material.ICE) && world.isAir(blockPos2)) {
					world.setBlockState(blockPos2, Blocks.field_10588.getDefaultState(), 11);
					if (playerEntity instanceof ServerPlayerEntity) {
						Criterions.PLACED_BLOCK.handle((ServerPlayerEntity)playerEntity, blockPos2, itemStack);
					}

					if (!playerEntity.abilities.creativeMode) {
						itemStack.decrement(1);
					}

					playerEntity.incrementStat(Stats.field_15372.getOrCreateStat(this));
					world.playSound(playerEntity, blockPos, SoundEvents.field_15173, SoundCategory.field_15245, 1.0F, 1.0F);
					return new TypedActionResult<>(ActionResult.field_5812, itemStack);
				}
			}

			return new TypedActionResult<>(ActionResult.field_5814, itemStack);
		}
	}
}
