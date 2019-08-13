package net.minecraft.block;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.loot.context.LootContext;

public class WallPlayerSkullBlock extends WallSkullBlock {
	protected WallPlayerSkullBlock(Block.Settings settings) {
		super(SkullBlock.Type.field_11510, settings);
	}

	@Override
	public void onPlaced(World world, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
		Blocks.field_10432.onPlaced(world, blockPos, blockState, livingEntity, itemStack);
	}

	@Override
	public List<ItemStack> getDroppedStacks(BlockState blockState, LootContext.Builder builder) {
		return Blocks.field_10432.getDroppedStacks(blockState, builder);
	}
}
