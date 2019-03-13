package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class SeaPickleBlock extends PlantBlock implements Fertilizable, Waterloggable {
	public static final IntegerProperty field_11472 = Properties.field_12543;
	public static final BooleanProperty field_11475 = Properties.field_12508;
	protected static final VoxelShape field_11473 = Block.method_9541(6.0, 0.0, 6.0, 10.0, 6.0, 10.0);
	protected static final VoxelShape field_11470 = Block.method_9541(3.0, 0.0, 3.0, 13.0, 6.0, 13.0);
	protected static final VoxelShape field_11471 = Block.method_9541(2.0, 0.0, 2.0, 14.0, 6.0, 14.0);
	protected static final VoxelShape field_11474 = Block.method_9541(2.0, 0.0, 2.0, 14.0, 7.0, 14.0);

	protected SeaPickleBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_11472, Integer.valueOf(1)).method_11657(field_11475, Boolean.valueOf(true)));
	}

	@Override
	public int method_9593(BlockState blockState) {
		return this.method_10506(blockState) ? 0 : super.method_9593(blockState) + 3 * (Integer)blockState.method_11654(field_11472);
	}

	@Nullable
	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		BlockState blockState = itemPlacementContext.method_8045().method_8320(itemPlacementContext.method_8037());
		if (blockState.getBlock() == this) {
			return blockState.method_11657(field_11472, Integer.valueOf(Math.min(4, (Integer)blockState.method_11654(field_11472) + 1)));
		} else {
			FluidState fluidState = itemPlacementContext.method_8045().method_8316(itemPlacementContext.method_8037());
			boolean bl = fluidState.method_15767(FluidTags.field_15517) && fluidState.getLevel() == 8;
			return super.method_9605(itemPlacementContext).method_11657(field_11475, Boolean.valueOf(bl));
		}
	}

	private boolean method_10506(BlockState blockState) {
		return !(Boolean)blockState.method_11654(field_11475);
	}

	@Override
	protected boolean method_9695(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return !blockState.method_11628(blockView, blockPos).method_1098(Direction.UP).isEmpty();
	}

	@Override
	public boolean method_9558(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.down();
		return this.method_9695(viewableWorld.method_8320(blockPos2), viewableWorld, blockPos2);
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		if (!blockState.method_11591(iWorld, blockPos)) {
			return Blocks.field_10124.method_9564();
		} else {
			if ((Boolean)blockState.method_11654(field_11475)) {
				iWorld.method_8405().method_8676(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(iWorld));
			}

			return super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		}
	}

	@Override
	public boolean method_9616(BlockState blockState, ItemPlacementContext itemPlacementContext) {
		return itemPlacementContext.getItemStack().getItem() == this.getItem() && blockState.method_11654(field_11472) < 4
			? true
			: super.method_9616(blockState, itemPlacementContext);
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, VerticalEntityPosition verticalEntityPosition) {
		switch (blockState.method_11654(field_11472)) {
			case 1:
			default:
				return field_11473;
			case 2:
				return field_11470;
			case 3:
				return field_11471;
			case 4:
				return field_11474;
		}
	}

	@Override
	public FluidState method_9545(BlockState blockState) {
		return blockState.method_11654(field_11475) ? Fluids.WATER.method_15729(false) : super.method_9545(blockState);
	}

	@Override
	protected void method_9515(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11472, field_11475);
	}

	@Override
	public boolean method_9651(BlockView blockView, BlockPos blockPos, BlockState blockState, boolean bl) {
		return true;
	}

	@Override
	public boolean method_9650(World world, Random random, BlockPos blockPos, BlockState blockState) {
		return true;
	}

	@Override
	public void method_9652(World world, Random random, BlockPos blockPos, BlockState blockState) {
		if (!this.method_10506(blockState) && world.method_8320(blockPos.down()).method_11602(BlockTags.field_15461)) {
			int i = 5;
			int j = 1;
			int k = 2;
			int l = 0;
			int m = blockPos.getX() - 2;
			int n = 0;

			for (int o = 0; o < 5; o++) {
				for (int p = 0; p < j; p++) {
					int q = 2 + blockPos.getY() - 1;

					for (int r = q - 2; r < q; r++) {
						BlockPos blockPos2 = new BlockPos(m + o, r, blockPos.getZ() - n + p);
						if (blockPos2 != blockPos && random.nextInt(6) == 0 && world.method_8320(blockPos2).getBlock() == Blocks.field_10382) {
							BlockState blockState2 = world.method_8320(blockPos2.down());
							if (blockState2.method_11602(BlockTags.field_15461)) {
								world.method_8652(blockPos2, Blocks.field_10476.method_9564().method_11657(field_11472, Integer.valueOf(random.nextInt(4) + 1)), 3);
							}
						}
					}
				}

				if (l < 2) {
					j += 2;
					n++;
				} else {
					j -= 2;
					n--;
				}

				l++;
			}

			world.method_8652(blockPos, blockState.method_11657(field_11472, Integer.valueOf(4)), 2);
		}
	}
}
