package net.minecraft.block;

import net.minecraft.block.enums.RailShape;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RailBlock extends AbstractRailBlock {
	public static final EnumProperty<RailShape> field_11369 = Properties.field_12507;

	protected RailBlock(Block.Settings settings) {
		super(false, settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11369, RailShape.field_12665));
	}

	@Override
	protected void method_9477(BlockState blockState, World world, BlockPos blockPos, Block block) {
		if (block.method_9564().emitsRedstonePower() && new RailPlacementHelper(world, blockPos, blockState).method_10460() == 3) {
			this.method_9475(world, blockPos, blockState, false);
		}
	}

	@Override
	public Property<RailShape> method_9474() {
		return field_11369;
	}

	@Override
	public BlockState method_9598(BlockState blockState, BlockRotation blockRotation) {
		switch (blockRotation) {
			case field_11464:
				switch ((RailShape)blockState.method_11654(field_11369)) {
					case field_12667:
						return blockState.method_11657(field_11369, RailShape.field_12666);
					case field_12666:
						return blockState.method_11657(field_11369, RailShape.field_12667);
					case field_12670:
						return blockState.method_11657(field_11369, RailShape.field_12668);
					case field_12668:
						return blockState.method_11657(field_11369, RailShape.field_12670);
					case field_12664:
						return blockState.method_11657(field_11369, RailShape.field_12672);
					case field_12671:
						return blockState.method_11657(field_11369, RailShape.field_12663);
					case field_12672:
						return blockState.method_11657(field_11369, RailShape.field_12664);
					case field_12663:
						return blockState.method_11657(field_11369, RailShape.field_12671);
				}
			case field_11465:
				switch ((RailShape)blockState.method_11654(field_11369)) {
					case field_12667:
						return blockState.method_11657(field_11369, RailShape.field_12670);
					case field_12666:
						return blockState.method_11657(field_11369, RailShape.field_12668);
					case field_12670:
						return blockState.method_11657(field_11369, RailShape.field_12666);
					case field_12668:
						return blockState.method_11657(field_11369, RailShape.field_12667);
					case field_12664:
						return blockState.method_11657(field_11369, RailShape.field_12663);
					case field_12671:
						return blockState.method_11657(field_11369, RailShape.field_12664);
					case field_12672:
						return blockState.method_11657(field_11369, RailShape.field_12671);
					case field_12663:
						return blockState.method_11657(field_11369, RailShape.field_12672);
					case field_12665:
						return blockState.method_11657(field_11369, RailShape.field_12674);
					case field_12674:
						return blockState.method_11657(field_11369, RailShape.field_12665);
				}
			case field_11463:
				switch ((RailShape)blockState.method_11654(field_11369)) {
					case field_12667:
						return blockState.method_11657(field_11369, RailShape.field_12668);
					case field_12666:
						return blockState.method_11657(field_11369, RailShape.field_12670);
					case field_12670:
						return blockState.method_11657(field_11369, RailShape.field_12667);
					case field_12668:
						return blockState.method_11657(field_11369, RailShape.field_12666);
					case field_12664:
						return blockState.method_11657(field_11369, RailShape.field_12671);
					case field_12671:
						return blockState.method_11657(field_11369, RailShape.field_12672);
					case field_12672:
						return blockState.method_11657(field_11369, RailShape.field_12663);
					case field_12663:
						return blockState.method_11657(field_11369, RailShape.field_12664);
					case field_12665:
						return blockState.method_11657(field_11369, RailShape.field_12674);
					case field_12674:
						return blockState.method_11657(field_11369, RailShape.field_12665);
				}
			default:
				return blockState;
		}
	}

	@Override
	public BlockState method_9569(BlockState blockState, BlockMirror blockMirror) {
		RailShape railShape = blockState.method_11654(field_11369);
		switch (blockMirror) {
			case field_11300:
				switch (railShape) {
					case field_12670:
						return blockState.method_11657(field_11369, RailShape.field_12668);
					case field_12668:
						return blockState.method_11657(field_11369, RailShape.field_12670);
					case field_12664:
						return blockState.method_11657(field_11369, RailShape.field_12663);
					case field_12671:
						return blockState.method_11657(field_11369, RailShape.field_12672);
					case field_12672:
						return blockState.method_11657(field_11369, RailShape.field_12671);
					case field_12663:
						return blockState.method_11657(field_11369, RailShape.field_12664);
					default:
						return super.method_9569(blockState, blockMirror);
				}
			case field_11301:
				switch (railShape) {
					case field_12667:
						return blockState.method_11657(field_11369, RailShape.field_12666);
					case field_12666:
						return blockState.method_11657(field_11369, RailShape.field_12667);
					case field_12670:
					case field_12668:
					default:
						break;
					case field_12664:
						return blockState.method_11657(field_11369, RailShape.field_12671);
					case field_12671:
						return blockState.method_11657(field_11369, RailShape.field_12664);
					case field_12672:
						return blockState.method_11657(field_11369, RailShape.field_12663);
					case field_12663:
						return blockState.method_11657(field_11369, RailShape.field_12672);
				}
		}

		return super.method_9569(blockState, blockMirror);
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11369);
	}
}
