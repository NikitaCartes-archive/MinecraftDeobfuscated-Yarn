package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public enum SideShapeType {
	FULL {
		@Override
		public boolean matches(BlockState state, BlockView world, BlockPos pos, Direction direction) {
			return Block.isFaceFullSquare(state.getSidesShape(world, pos), direction);
		}
	},
	CENTER {
		private final int radius = 1;
		private final VoxelShape squareCuboid = Block.createCuboidShape(7.0, 0.0, 7.0, 9.0, 10.0, 9.0);
		private static final List<TagKey<Block>> HANGING_SIGNS_TAGS = Lists.<TagKey<Block>>newArrayList(BlockTags.CEILING_HANGING_SIGNS, BlockTags.WALL_HANGING_SIGNS);

		@Override
		public boolean matches(BlockState state, BlockView world, BlockPos pos, Direction direction) {
			for (TagKey<Block> tagKey : HANGING_SIGNS_TAGS) {
				if (state.isIn(tagKey)) {
					return true;
				}
			}

			return !VoxelShapes.matchesAnywhere(state.getSidesShape(world, pos).getFace(direction), this.squareCuboid, BooleanBiFunction.ONLY_SECOND);
		}
	},
	RIGID {
		private final int ringWidth = 2;
		private final VoxelShape hollowSquareCuboid = VoxelShapes.combineAndSimplify(
			VoxelShapes.fullCube(), Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 16.0, 14.0), BooleanBiFunction.ONLY_FIRST
		);

		@Override
		public boolean matches(BlockState state, BlockView world, BlockPos pos, Direction direction) {
			return !VoxelShapes.matchesAnywhere(state.getSidesShape(world, pos).getFace(direction), this.hollowSquareCuboid, BooleanBiFunction.ONLY_SECOND);
		}
	};

	public abstract boolean matches(BlockState state, BlockView world, BlockPos pos, Direction direction);
}
