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
import net.minecraft.util.HitResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LilyPadItem extends BlockItem {
	public LilyPadItem(Block block, Item.Settings settings) {
		super(block, settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
		return ActionResult.PASS;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		HitResult hitResult = this.getHitResult(world, playerEntity, true);
		if (hitResult == null) {
			return new TypedActionResult<>(ActionResult.PASS, itemStack);
		} else {
			if (hitResult.type == HitResult.Type.BLOCK) {
				BlockPos blockPos = hitResult.getBlockPos();
				if (!world.canPlayerModifyAt(playerEntity, blockPos) || !playerEntity.canPlaceBlock(blockPos.offset(hitResult.side), hitResult.side, itemStack)) {
					return new TypedActionResult<>(ActionResult.FAILURE, itemStack);
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
						itemStack.subtractAmount(1);
					}

					playerEntity.incrementStat(Stats.field_15372.method_14956(this));
					world.playSound(playerEntity, blockPos, SoundEvents.field_15173, SoundCategory.field_15245, 1.0F, 1.0F);
					return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
				}
			}

			return new TypedActionResult<>(ActionResult.FAILURE, itemStack);
		}
	}
}
