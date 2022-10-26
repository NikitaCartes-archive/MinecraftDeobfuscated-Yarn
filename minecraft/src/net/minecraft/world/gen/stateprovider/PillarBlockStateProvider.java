package net.minecraft.world.gen.stateprovider;

import com.mojang.serialization.Codec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

public class PillarBlockStateProvider extends BlockStateProvider {
	public static final Codec<PillarBlockStateProvider> CODEC = BlockState.CODEC
		.fieldOf("state")
		.xmap(AbstractBlock.AbstractBlockState::getBlock, Block::getDefaultState)
		.<PillarBlockStateProvider>xmap(PillarBlockStateProvider::new, provider -> provider.block)
		.codec();
	private final Block block;

	public PillarBlockStateProvider(Block block) {
		this.block = block;
	}

	@Override
	protected BlockStateProviderType<?> getType() {
		return BlockStateProviderType.ROTATED_BLOCK_PROVIDER;
	}

	@Override
	public BlockState get(Random random, BlockPos pos) {
		Direction.Axis axis = Direction.Axis.pickRandomAxis(random);
		return this.block.getDefaultState().with(PillarBlock.AXIS, axis);
	}
}
