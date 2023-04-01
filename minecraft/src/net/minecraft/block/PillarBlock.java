package net.minecraft.block;

import net.minecraft.class_8293;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class PillarBlock extends Block {
	public static final EnumProperty<Direction.Axis> AXIS = Properties.AXIS;

	public PillarBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState().with(AXIS, Direction.Axis.Y));
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return changeRotation(state, rotation);
	}

	public static BlockState changeRotation(BlockState state, BlockRotation rotation) {
		switch (rotation) {
			case COUNTERCLOCKWISE_90:
			case CLOCKWISE_90:
				switch ((Direction.Axis)state.get(AXIS)) {
					case X:
						return state.with(AXIS, Direction.Axis.Z);
					case Z:
						return state.with(AXIS, Direction.Axis.X);
					default:
						return state;
				}
			default:
				return state;
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(AXIS);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(AXIS, ctx.getSide().getAxis());
	}

	@Override
	public void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack tool, boolean dropExperience) {
		if (state.isIn(BlockTags.LOGS) && class_8293.field_43670.method_50116()) {
			BlockState blockState = world.getBlockState(pos.up());
			boolean bl = blockState.isIn(BlockTags.LOGS) || blockState.isOf(Blocks.END_ROD);
			BlockState blockState2 = world.getBlockState(pos.down());
			boolean bl2 = blockState2.isIn(BlockTags.LOGS) || blockState.isOf(Blocks.END_ROD);
			if (bl || bl2) {
				world.setBlockState(pos, Blocks.END_ROD.getDefaultState(), Block.FORCE_STATE);
			}
		}

		super.onStacksDropped(state, world, pos, tool, dropExperience);
	}
}
