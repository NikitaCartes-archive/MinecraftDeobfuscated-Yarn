package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.Direction;

public class PoisonousPotatoZombieHeadBlock extends HorizontalFacingBlock {
	public static final MapCodec<PoisonousPotatoZombieHeadBlock> CODEC = createCodec(PoisonousPotatoZombieHeadBlock::new);
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

	@Override
	public MapCodec<? extends PoisonousPotatoZombieHeadBlock> getCodec() {
		return CODEC;
	}

	protected PoisonousPotatoZombieHeadBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
}
