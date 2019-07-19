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
	public ActionResult useOnBlock(ItemUsageContext context) {
		return ActionResult.PASS;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);
		HitResult hitResult = rayTrace(world, user, RayTraceContext.FluidHandling.SOURCE_ONLY);
		if (hitResult.getType() == HitResult.Type.MISS) {
			return new TypedActionResult<>(ActionResult.PASS, itemStack);
		} else {
			if (hitResult.getType() == HitResult.Type.BLOCK) {
				BlockHitResult blockHitResult = (BlockHitResult)hitResult;
				BlockPos blockPos = blockHitResult.getBlockPos();
				Direction direction = blockHitResult.getSide();
				if (!world.canPlayerModifyAt(user, blockPos) || !user.canPlaceOn(blockPos.offset(direction), direction, itemStack)) {
					return new TypedActionResult<>(ActionResult.FAIL, itemStack);
				}

				BlockPos blockPos2 = blockPos.up();
				BlockState blockState = world.getBlockState(blockPos);
				Material material = blockState.getMaterial();
				FluidState fluidState = world.getFluidState(blockPos);
				if ((fluidState.getFluid() == Fluids.WATER || material == Material.ICE) && world.isAir(blockPos2)) {
					world.setBlockState(blockPos2, Blocks.LILY_PAD.getDefaultState(), 11);
					if (user instanceof ServerPlayerEntity) {
						Criterions.PLACED_BLOCK.trigger((ServerPlayerEntity)user, blockPos2, itemStack);
					}

					if (!user.abilities.creativeMode) {
						itemStack.decrement(1);
					}

					user.incrementStat(Stats.USED.getOrCreateStat(this));
					world.playSound(user, blockPos, SoundEvents.BLOCK_LILY_PAD_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
					return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
				}
			}

			return new TypedActionResult<>(ActionResult.FAIL, itemStack);
		}
	}
}
