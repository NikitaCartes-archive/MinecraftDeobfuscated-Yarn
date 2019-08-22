package net.minecraft.block;

import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class BedrockBlock extends Block {
	public BedrockBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public boolean allowsSpawning(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityType<?> entityType) {
		return false;
	}
}
