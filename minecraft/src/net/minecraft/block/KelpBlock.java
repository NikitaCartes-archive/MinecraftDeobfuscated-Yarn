package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class KelpBlock extends Block implements FluidFillable {
	public static final IntegerProperty field_11194 = Properties.field_12517;
	protected static final VoxelShape field_11195 = Block.method_9541(0.0, 0.0, 0.0, 16.0, 9.0, 16.0);

	protected KelpBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11194, Integer.valueOf(0)));
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		return field_11195;
	}

	@Nullable
	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		FluidState fluidState = itemPlacementContext.method_8045().method_8316(itemPlacementContext.method_8037());
		return fluidState.method_15767(FluidTags.field_15517) && fluidState.getLevel() == 8 ? this.method_10292(itemPlacementContext.method_8045()) : null;
	}

	public BlockState method_10292(IWorld iWorld) {
		return this.method_9564().method_11657(field_11194, Integer.valueOf(iWorld.getRandom().nextInt(25)));
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public FluidState method_9545(BlockState blockState) {
		return Fluids.WATER.method_15729(false);
	}

	@Override
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!blockState.method_11591(world, blockPos)) {
			world.method_8651(blockPos, true);
		} else {
			BlockPos blockPos2 = blockPos.up();
			BlockState blockState2 = world.method_8320(blockPos2);
			if (blockState2.getBlock() == Blocks.field_10382 && (Integer)blockState.method_11654(field_11194) < 25 && random.nextDouble() < 0.14) {
				world.method_8501(blockPos2, blockState.method_11572(field_11194));
			}
		}
	}

	@Override
	public boolean method_9558(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.down();
		BlockState blockState2 = viewableWorld.method_8320(blockPos2);
		Block block = blockState2.getBlock();
		return block == Blocks.field_10092
			? false
			: block == this || block == Blocks.field_10463 || Block.method_9501(blockState2.method_11628(viewableWorld, blockPos2), Direction.UP);
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		if (!blockState.method_11591(iWorld, blockPos)) {
			if (direction == Direction.DOWN) {
				return Blocks.field_10124.method_9564();
			}

			iWorld.method_8397().method_8676(blockPos, this, 1);
		}

		if (direction == Direction.UP && blockState2.getBlock() == this) {
			return Blocks.field_10463.method_9564();
		} else {
			iWorld.method_8405().method_8676(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
			return super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		}
	}

	@Override
	protected void method_9515(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11194);
	}

	@Override
	public boolean method_10310(BlockView blockView, BlockPos blockPos, BlockState blockState, Fluid fluid) {
		return false;
	}

	@Override
	public boolean method_10311(IWorld iWorld, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
		return false;
	}
}
