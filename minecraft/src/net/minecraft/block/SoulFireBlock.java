package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class SoulFireBlock extends AbstractFireBlock {
	public static final MapCodec<SoulFireBlock> CODEC = createCodec(SoulFireBlock::new);

	@Override
	public MapCodec<SoulFireBlock> getCodec() {
		return CODEC;
	}

	public SoulFireBlock(AbstractBlock.Settings settings) {
		super(settings, 2.0F);
	}

	@Override
	protected BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		return this.canPlaceAt(state, world, pos) ? this.getDefaultState() : Blocks.AIR.getDefaultState();
	}

	@Override
	protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return isSoulBase(world.getBlockState(pos.down()));
	}

	public static boolean isSoulBase(BlockState state) {
		return state.isIn(BlockTags.SOUL_FIRE_BASE_BLOCKS);
	}

	@Override
	protected boolean isFlammable(BlockState state) {
		return true;
	}
}
