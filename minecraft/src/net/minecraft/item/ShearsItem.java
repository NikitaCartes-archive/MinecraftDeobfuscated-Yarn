package net.minecraft.item;

import net.minecraft.block.Block;
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
		if (!world.isClient) {
			stack.damage(1, miner, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
		}

		Block block = state.getBlock();
		return !state.matches(BlockTags.LEAVES)
				&& block != Blocks.COBWEB
				&& block != Blocks.GRASS
				&& block != Blocks.FERN
				&& block != Blocks.DEAD_BUSH
				&& block != Blocks.VINE
				&& block != Blocks.TRIPWIRE
				&& !block.matches(BlockTags.WOOL)
			? super.postMine(stack, world, state, pos, miner)
			: true;
	}

	@Override
	public boolean isEffectiveOn(BlockState state) {
		Block block = state.getBlock();
		return block == Blocks.COBWEB || block == Blocks.REDSTONE_WIRE || block == Blocks.TRIPWIRE;
	}

	@Override
	public float getMiningSpeed(ItemStack stack, BlockState state) {
		Block block = state.getBlock();
		if (block == Blocks.COBWEB || state.matches(BlockTags.LEAVES)) {
			return 15.0F;
		} else {
			return block.matches(BlockTags.WOOL) ? 5.0F : super.getMiningSpeed(stack, state);
		}
	}
}
