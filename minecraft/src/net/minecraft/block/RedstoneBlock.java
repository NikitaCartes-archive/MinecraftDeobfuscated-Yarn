package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public class RedstoneBlock extends Block {
	public static final MapCodec<RedstoneBlock> CODEC = createCodec(RedstoneBlock::new);

	@Override
	public MapCodec<RedstoneBlock> getCodec() {
		return CODEC;
	}

	public RedstoneBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	protected boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	@Override
	protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return 15;
	}
}
