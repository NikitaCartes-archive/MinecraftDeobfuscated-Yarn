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
	public boolean method_7879(ItemStack itemStack, World world, BlockState blockState, BlockPos blockPos, LivingEntity livingEntity) {
		if (!world.isClient) {
			itemStack.applyDamage(1, livingEntity);
		}

		Block block = blockState.getBlock();
		return !blockState.method_11602(BlockTags.field_15503)
				&& block != Blocks.field_10343
				&& block != Blocks.field_10479
				&& block != Blocks.field_10112
				&& block != Blocks.field_10428
				&& block != Blocks.field_10597
				&& block != Blocks.field_10589
				&& !block.method_9525(BlockTags.field_15481)
			? super.method_7879(itemStack, world, blockState, blockPos, livingEntity)
			: true;
	}

	@Override
	public boolean method_7856(BlockState blockState) {
		Block block = blockState.getBlock();
		return block == Blocks.field_10343 || block == Blocks.field_10091 || block == Blocks.field_10589;
	}

	@Override
	public float method_7865(ItemStack itemStack, BlockState blockState) {
		Block block = blockState.getBlock();
		if (block == Blocks.field_10343 || blockState.method_11602(BlockTags.field_15503)) {
			return 15.0F;
		} else {
			return block.method_9525(BlockTags.field_15481) ? 5.0F : super.method_7865(itemStack, blockState);
		}
	}
}
