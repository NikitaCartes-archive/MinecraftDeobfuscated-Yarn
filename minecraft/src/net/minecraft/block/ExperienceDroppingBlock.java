package net.minecraft.block;

import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;

public class ExperienceDroppingBlock extends Block {
	private final IntProvider experience;

	public ExperienceDroppingBlock(AbstractBlock.Settings settings) {
		this(settings, ConstantIntProvider.create(0));
	}

	public ExperienceDroppingBlock(AbstractBlock.Settings settings, IntProvider experience) {
		super(settings);
		this.experience = experience;
	}

	@Override
	public void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack stack) {
		super.onStacksDropped(state, world, pos, stack);
		this.dropExperienceWhenMined(world, pos, stack, this.experience);
	}
}
