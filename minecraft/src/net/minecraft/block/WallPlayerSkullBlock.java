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
		super(SkullBlock.Type.PLAYER, settings);
	}

	@Override
	public void method_9567(World world, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
		Blocks.field_10432.method_9567(world, blockPos, blockState, livingEntity, itemStack);
	}

	@Override
	public List<ItemStack> method_9560(BlockState blockState, LootContext.Builder builder) {
		return Blocks.field_10432.method_9560(blockState, builder);
	}
}
