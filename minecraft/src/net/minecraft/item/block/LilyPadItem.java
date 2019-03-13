package net.minecraft.item.block;

import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
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
	public ActionResult method_7884(ItemUsageContext itemUsageContext) {
		return ActionResult.PASS;
	}

	@Override
	public TypedActionResult<ItemStack> method_7836(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.method_5998(hand);
		HitResult hitResult = method_7872(world, playerEntity, RayTraceContext.FluidHandling.field_1345);
		if (hitResult.getType() == HitResult.Type.NONE) {
			return new TypedActionResult<>(ActionResult.PASS, itemStack);
		} else {
			if (hitResult.getType() == HitResult.Type.BLOCK) {
				BlockHitResult blockHitResult = (BlockHitResult)hitResult;
				BlockPos blockPos = blockHitResult.method_17777();
				Direction direction = blockHitResult.method_17780();
				if (!world.method_8505(playerEntity, blockPos) || !playerEntity.method_7343(blockPos.method_10093(direction), direction, itemStack)) {
					return new TypedActionResult<>(ActionResult.field_5814, itemStack);
				}

				BlockPos blockPos2 = blockPos.up();
				BlockState blockState = world.method_8320(blockPos);
				Material material = blockState.method_11620();
				FluidState fluidState = world.method_8316(blockPos);
				if ((fluidState.getFluid() == Fluids.WATER || material == Material.ICE) && world.method_8623(blockPos2)) {
					world.method_8652(blockPos2, Blocks.field_10588.method_9564(), 11);
					if (playerEntity instanceof ServerPlayerEntity) {
						Criterions.PLACED_BLOCK.method_9087((ServerPlayerEntity)playerEntity, blockPos2, itemStack);
					}

					if (!playerEntity.abilities.creativeMode) {
						itemStack.subtractAmount(1);
					}

					playerEntity.method_7259(Stats.field_15372.getOrCreateStat(this));
					world.method_8396(playerEntity, blockPos, SoundEvents.field_15173, SoundCategory.field_15245, 1.0F, 1.0F);
					return new TypedActionResult<>(ActionResult.field_5812, itemStack);
				}
			}

			return new TypedActionResult<>(ActionResult.field_5814, itemStack);
		}
	}
}
