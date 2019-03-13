package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WallWitherSkullBlock extends WallSkullBlock {
	protected WallWitherSkullBlock(Block.Settings settings) {
		super(SkullBlock.Type.WITHER_SKELETON, settings);
	}

	@Override
	public void method_9567(World world, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
		Blocks.field_10177.method_9567(world, blockPos, blockState, livingEntity, itemStack);
	}
}
