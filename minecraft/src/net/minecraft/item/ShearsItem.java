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
	public boolean postMine(ItemStack itemStack, World world, BlockState blockState, BlockPos blockPos, LivingEntity livingEntity) {
		if (!world.isClient) {
			itemStack.damage(1, livingEntity, livingEntityx -> livingEntityx.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
		}

		Block block = blockState.getBlock();
		return !blockState.matches(BlockTags.LEAVES)
				&& block != Blocks.COBWEB
				&& block != Blocks.GRASS
				&& block != Blocks.FERN
				&& block != Blocks.DEAD_BUSH
				&& block != Blocks.VINE
				&& block != Blocks.TRIPWIRE
				&& !block.matches(BlockTags.WOOL)
			? super.postMine(itemStack, world, blockState, blockPos, livingEntity)
			: true;
	}

	@Override
	public boolean isEffectiveOn(BlockState blockState) {
		Block block = blockState.getBlock();
		return block == Blocks.COBWEB || block == Blocks.REDSTONE_WIRE || block == Blocks.TRIPWIRE;
	}

	@Override
	public float getMiningSpeed(ItemStack itemStack, BlockState blockState) {
		Block block = blockState.getBlock();
		if (block == Blocks.COBWEB || blockState.matches(BlockTags.LEAVES)) {
			return 15.0F;
		} else {
			return block.matches(BlockTags.WOOL) ? 5.0F : super.getMiningSpeed(itemStack, blockState);
		}
	}
}
