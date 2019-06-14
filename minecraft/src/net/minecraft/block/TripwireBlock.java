package net.minecraft.block;

import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Items;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class TripwireBlock extends Block {
	public static final BooleanProperty field_11680 = Properties.field_12484;
	public static final BooleanProperty field_11683 = Properties.field_12493;
	public static final BooleanProperty field_11679 = Properties.field_12553;
	public static final BooleanProperty field_11675 = ConnectedPlantBlock.field_11332;
	public static final BooleanProperty field_11673 = ConnectedPlantBlock.field_11335;
	public static final BooleanProperty field_11678 = ConnectedPlantBlock.field_11331;
	public static final BooleanProperty field_11674 = ConnectedPlantBlock.field_11328;
	private static final Map<Direction, BooleanProperty> FACING_PROPERTIES = HorizontalConnectedBlock.FACING_PROPERTIES;
	protected static final VoxelShape field_11682 = Block.method_9541(0.0, 1.0, 0.0, 16.0, 2.5, 16.0);
	protected static final VoxelShape field_11681 = Block.method_9541(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
	private final TripwireHookBlock field_11677;

	public TripwireBlock(TripwireHookBlock tripwireHookBlock, Block.Settings settings) {
		super(settings);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_11680, Boolean.valueOf(false))
				.method_11657(field_11683, Boolean.valueOf(false))
				.method_11657(field_11679, Boolean.valueOf(false))
				.method_11657(field_11675, Boolean.valueOf(false))
				.method_11657(field_11673, Boolean.valueOf(false))
				.method_11657(field_11678, Boolean.valueOf(false))
				.method_11657(field_11674, Boolean.valueOf(false))
		);
		this.field_11677 = tripwireHookBlock;
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return blockState.method_11654(field_11683) ? field_11682 : field_11681;
	}

	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		BlockView blockView = itemPlacementContext.method_8045();
		BlockPos blockPos = itemPlacementContext.getBlockPos();
		return this.method_9564()
			.method_11657(field_11675, Boolean.valueOf(this.method_10778(blockView.method_8320(blockPos.north()), Direction.field_11043)))
			.method_11657(field_11673, Boolean.valueOf(this.method_10778(blockView.method_8320(blockPos.east()), Direction.field_11034)))
			.method_11657(field_11678, Boolean.valueOf(this.method_10778(blockView.method_8320(blockPos.south()), Direction.field_11035)))
			.method_11657(field_11674, Boolean.valueOf(this.method_10778(blockView.method_8320(blockPos.west()), Direction.field_11039)));
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		return direction.getAxis().isHorizontal()
			? blockState.method_11657((Property)FACING_PROPERTIES.get(direction), Boolean.valueOf(this.method_10778(blockState2, direction)))
			: super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.field_9179;
	}

	@Override
	public void method_9615(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState2.getBlock() != blockState.getBlock()) {
			this.method_10779(world, blockPos, blockState);
		}
	}

	@Override
	public void method_9536(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (!bl && blockState.getBlock() != blockState2.getBlock()) {
			this.method_10779(world, blockPos, blockState.method_11657(field_11680, Boolean.valueOf(true)));
		}
	}

	@Override
	public void method_9576(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
		if (!world.isClient && !playerEntity.getMainHandStack().isEmpty() && playerEntity.getMainHandStack().getItem() == Items.field_8868) {
			world.method_8652(blockPos, blockState.method_11657(field_11679, Boolean.valueOf(true)), 4);
		}

		super.method_9576(world, blockPos, blockState, playerEntity);
	}

	private void method_10779(World world, BlockPos blockPos, BlockState blockState) {
		for (Direction direction : new Direction[]{Direction.field_11035, Direction.field_11039}) {
			for (int i = 1; i < 42; i++) {
				BlockPos blockPos2 = blockPos.offset(direction, i);
				BlockState blockState2 = world.method_8320(blockPos2);
				if (blockState2.getBlock() == this.field_11677) {
					if (blockState2.method_11654(TripwireHookBlock.field_11666) == direction.getOpposite()) {
						this.field_11677.method_10776(world, blockPos2, blockState2, false, true, i, blockState);
					}
					break;
				}

				if (blockState2.getBlock() != this) {
					break;
				}
			}
		}
	}

	@Override
	public void method_9548(BlockState blockState, World world, BlockPos blockPos, Entity entity) {
		if (!world.isClient) {
			if (!(Boolean)blockState.method_11654(field_11680)) {
				this.updatePowered(world, blockPos);
			}
		}
	}

	@Override
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!world.isClient) {
			if ((Boolean)world.method_8320(blockPos).method_11654(field_11680)) {
				this.updatePowered(world, blockPos);
			}
		}
	}

	private void updatePowered(World world, BlockPos blockPos) {
		BlockState blockState = world.method_8320(blockPos);
		boolean bl = (Boolean)blockState.method_11654(field_11680);
		boolean bl2 = false;
		List<? extends Entity> list = world.method_8335(null, blockState.method_17770(world, blockPos).getBoundingBox().offset(blockPos));
		if (!list.isEmpty()) {
			for (Entity entity : list) {
				if (!entity.canAvoidTraps()) {
					bl2 = true;
					break;
				}
			}
		}

		if (bl2 != bl) {
			blockState = blockState.method_11657(field_11680, Boolean.valueOf(bl2));
			world.method_8652(blockPos, blockState, 3);
			this.method_10779(world, blockPos, blockState);
		}

		if (bl2) {
			world.method_8397().schedule(new BlockPos(blockPos), this, this.getTickRate(world));
		}
	}

	public boolean method_10778(BlockState blockState, Direction direction) {
		Block block = blockState.getBlock();
		return block == this.field_11677 ? blockState.method_11654(TripwireHookBlock.field_11666) == direction.getOpposite() : block == this;
	}

	@Override
	public BlockState method_9598(BlockState blockState, BlockRotation blockRotation) {
		switch (blockRotation) {
			case field_11464:
				return blockState.method_11657(field_11675, blockState.method_11654(field_11678))
					.method_11657(field_11673, blockState.method_11654(field_11674))
					.method_11657(field_11678, blockState.method_11654(field_11675))
					.method_11657(field_11674, blockState.method_11654(field_11673));
			case field_11465:
				return blockState.method_11657(field_11675, blockState.method_11654(field_11673))
					.method_11657(field_11673, blockState.method_11654(field_11678))
					.method_11657(field_11678, blockState.method_11654(field_11674))
					.method_11657(field_11674, blockState.method_11654(field_11675));
			case field_11463:
				return blockState.method_11657(field_11675, blockState.method_11654(field_11674))
					.method_11657(field_11673, blockState.method_11654(field_11675))
					.method_11657(field_11678, blockState.method_11654(field_11673))
					.method_11657(field_11674, blockState.method_11654(field_11678));
			default:
				return blockState;
		}
	}

	@Override
	public BlockState method_9569(BlockState blockState, BlockMirror blockMirror) {
		switch (blockMirror) {
			case field_11300:
				return blockState.method_11657(field_11675, blockState.method_11654(field_11678)).method_11657(field_11678, blockState.method_11654(field_11675));
			case field_11301:
				return blockState.method_11657(field_11673, blockState.method_11654(field_11674)).method_11657(field_11674, blockState.method_11654(field_11673));
			default:
				return super.method_9569(blockState, blockMirror);
		}
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11680, field_11683, field_11679, field_11675, field_11673, field_11674, field_11678);
	}
}
