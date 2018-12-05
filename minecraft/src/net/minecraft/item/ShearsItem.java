package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ShearsItem extends Item {
	public ShearsItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public boolean onBlockBroken(ItemStack itemStack, World world, BlockState blockState, BlockPos blockPos, LivingEntity livingEntity) {
		if (!world.isRemote) {
			itemStack.applyDamage(1, livingEntity);
		}

		Block block = blockState.getBlock();
		return !blockState.matches(BlockTags.field_15503)
				&& block != Blocks.field_10343
				&& block != Blocks.field_10479
				&& block != Blocks.field_10112
				&& block != Blocks.field_10428
				&& block != Blocks.field_10597
				&& block != Blocks.field_10589
				&& !block.matches(BlockTags.field_15481)
			? super.onBlockBroken(itemStack, world, blockState, blockPos, livingEntity)
			: true;
	}

	@Override
	public boolean isEffectiveOn(BlockState blockState) {
		Block block = blockState.getBlock();
		return block == Blocks.field_10343 || block == Blocks.field_10091 || block == Blocks.field_10589;
	}

	@Override
	public float getBlockBreakingSpeed(ItemStack itemStack, BlockState blockState) {
		Block block = blockState.getBlock();
		if (block == Blocks.field_10343 || blockState.matches(BlockTags.field_15503)) {
			return 15.0F;
		} else {
			return block.matches(BlockTags.field_15481) ? 5.0F : super.getBlockBreakingSpeed(itemStack, blockState);
		}
	}
}
