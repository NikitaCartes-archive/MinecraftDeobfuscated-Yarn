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
		if (!world.isClient && !state.getBlock().isIn(BlockTags.FIRE)) {
			stack.damage(1, miner, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
		}

		return !state.isIn(BlockTags.LEAVES)
				&& !state.isOf(Blocks.COBWEB)
				&& !state.isOf(Blocks.GRASS)
				&& !state.isOf(Blocks.FERN)
				&& !state.isOf(Blocks.DEAD_BUSH)
				&& !state.isOf(Blocks.VINE)
				&& !state.isOf(Blocks.TRIPWIRE)
				&& !state.isIn(BlockTags.WOOL)
			? super.postMine(stack, world, state, pos, miner)
			: true;
	}

	@Override
	public boolean isEffectiveOn(BlockState state) {
		return state.isOf(Blocks.COBWEB) || state.isOf(Blocks.REDSTONE_WIRE) || state.isOf(Blocks.TRIPWIRE);
	}

	@Override
	public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
		if (state.isOf(Blocks.COBWEB) || state.isIn(BlockTags.LEAVES)) {
			return 15.0F;
		} else {
			return state.isIn(BlockTags.WOOL) ? 5.0F : super.getMiningSpeedMultiplier(stack, state);
		}
	}
}
