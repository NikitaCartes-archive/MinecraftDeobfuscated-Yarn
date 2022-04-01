package net.minecraft;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;

public class class_7321 extends Block {
	private final IntProvider field_38552;

	public class_7321(AbstractBlock.Settings settings) {
		this(settings, ConstantIntProvider.create(0));
	}

	public class_7321(AbstractBlock.Settings settings, IntProvider intProvider) {
		super(settings);
		this.field_38552 = intProvider;
	}

	@Override
	public void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack stack) {
		super.onStacksDropped(state, world, pos, stack);
		this.method_42872(world, pos, stack, this.field_38552);
	}
}
