package net.minecraft.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ShearsItem extends Item {
	public ShearsItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
		if (!world.isClient && !state.getBlock().isIn(BlockTags.field_21952)) {
			stack.damage(1, miner, e -> e.sendEquipmentBreakStatus(EquipmentSlot.field_6173));
		}

		return !state.isIn(BlockTags.field_15503)
				&& !state.isOf(Blocks.field_10343)
				&& !state.isOf(Blocks.field_10479)
				&& !state.isOf(Blocks.field_10112)
				&& !state.isOf(Blocks.field_10428)
				&& !state.isOf(Blocks.field_10597)
				&& !state.isOf(Blocks.field_10589)
				&& !state.isIn(BlockTags.field_15481)
			? super.postMine(stack, world, state, pos, miner)
			: true;
	}

	@Override
	public boolean isEffectiveOn(BlockState state) {
		return state.isOf(Blocks.field_10343) || state.isOf(Blocks.field_10091) || state.isOf(Blocks.field_10589);
	}

	@Override
	public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
		if (state.isOf(Blocks.field_10343) || state.isIn(BlockTags.field_15503)) {
			return 15.0F;
		} else {
			return state.isIn(BlockTags.field_15481) ? 5.0F : super.getMiningSpeedMultiplier(stack, state);
		}
	}
}
