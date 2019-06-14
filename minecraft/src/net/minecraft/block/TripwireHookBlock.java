package net.minecraft.block;

import com.google.common.base.MoreObjects;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class TripwireHookBlock extends Block {
	public static final DirectionProperty field_11666 = HorizontalFacingBlock.field_11177;
	public static final BooleanProperty field_11671 = Properties.field_12484;
	public static final BooleanProperty field_11669 = Properties.field_12493;
	protected static final VoxelShape field_11665 = Block.method_9541(5.0, 0.0, 10.0, 11.0, 10.0, 16.0);
	protected static final VoxelShape field_11668 = Block.method_9541(5.0, 0.0, 0.0, 11.0, 10.0, 6.0);
	protected static final VoxelShape field_11670 = Block.method_9541(10.0, 0.0, 5.0, 16.0, 10.0, 11.0);
	protected static final VoxelShape field_11667 = Block.method_9541(0.0, 0.0, 5.0, 6.0, 10.0, 11.0);

	public TripwireHookBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(
			this.field_10647
				.method_11664()
				.method_11657(field_11666, Direction.field_11043)
				.method_11657(field_11671, Boolean.valueOf(false))
				.method_11657(field_11669, Boolean.valueOf(false))
		);
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		switch ((Direction)blockState.method_11654(field_11666)) {
			case field_11034:
			default:
				return field_11667;
			case field_11039:
				return field_11670;
			case field_11035:
				return field_11668;
			case field_11043:
				return field_11665;
		}
	}

	@Override
	public boolean method_9558(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		Direction direction = blockState.method_11654(field_11666);
		BlockPos blockPos2 = blockPos.offset(direction.getOpposite());
		BlockState blockState2 = viewableWorld.method_8320(blockPos2);
		return direction.getAxis().isHorizontal() && Block.method_20045(blockState2, viewableWorld, blockPos2, direction) && !blockState2.emitsRedstonePower();
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		return direction.getOpposite() == blockState.method_11654(field_11666) && !blockState.canPlaceAt(iWorld, blockPos)
			? Blocks.field_10124.method_9564()
			: super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Nullable
	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		BlockState blockState = this.method_9564().method_11657(field_11671, Boolean.valueOf(false)).method_11657(field_11669, Boolean.valueOf(false));
		ViewableWorld viewableWorld = itemPlacementContext.method_8045();
		BlockPos blockPos = itemPlacementContext.getBlockPos();
		Direction[] directions = itemPlacementContext.getPlacementDirections();

		for (Direction direction : directions) {
			if (direction.getAxis().isHorizontal()) {
				Direction direction2 = direction.getOpposite();
				blockState = blockState.method_11657(field_11666, direction2);
				if (blockState.canPlaceAt(viewableWorld, blockPos)) {
					return blockState;
				}
			}
		}

		return null;
	}

	@Override
	public void method_9567(World world, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
		this.method_10776(world, blockPos, blockState, false, false, -1, null);
	}

	public void method_10776(World world, BlockPos blockPos, BlockState blockState, boolean bl, boolean bl2, int i, @Nullable BlockState blockState2) {
		Direction direction = blockState.method_11654(field_11666);
		boolean bl3 = (Boolean)blockState.method_11654(field_11669);
		boolean bl4 = (Boolean)blockState.method_11654(field_11671);
		boolean bl5 = !bl;
		boolean bl6 = false;
		int j = 0;
		BlockState[] blockStates = new BlockState[42];

		for (int k = 1; k < 42; k++) {
			BlockPos blockPos2 = blockPos.offset(direction, k);
			BlockState blockState3 = world.method_8320(blockPos2);
			if (blockState3.getBlock() == Blocks.field_10348) {
				if (blockState3.method_11654(field_11666) == direction.getOpposite()) {
					j = k;
				}
				break;
			}

			if (blockState3.getBlock() != Blocks.field_10589 && k != i) {
				blockStates[k] = null;
				bl5 = false;
			} else {
				if (k == i) {
					blockState3 = MoreObjects.firstNonNull(blockState2, blockState3);
				}

				boolean bl7 = !(Boolean)blockState3.method_11654(TripwireBlock.field_11679);
				boolean bl8 = (Boolean)blockState3.method_11654(TripwireBlock.field_11680);
				bl6 |= bl7 && bl8;
				blockStates[k] = blockState3;
				if (k == i) {
					world.method_8397().schedule(blockPos, this, this.getTickRate(world));
					bl5 &= bl7;
				}
			}
		}

		bl5 &= j > 1;
		bl6 &= bl5;
		BlockState blockState4 = this.method_9564().method_11657(field_11669, Boolean.valueOf(bl5)).method_11657(field_11671, Boolean.valueOf(bl6));
		if (j > 0) {
			BlockPos blockPos2x = blockPos.offset(direction, j);
			Direction direction2 = direction.getOpposite();
			world.method_8652(blockPos2x, blockState4.method_11657(field_11666, direction2), 3);
			this.updateNeighborsOnAxis(world, blockPos2x, direction2);
			this.playSound(world, blockPos2x, bl5, bl6, bl3, bl4);
		}

		this.playSound(world, blockPos, bl5, bl6, bl3, bl4);
		if (!bl) {
			world.method_8652(blockPos, blockState4.method_11657(field_11666, direction), 3);
			if (bl2) {
				this.updateNeighborsOnAxis(world, blockPos, direction);
			}
		}

		if (bl3 != bl5) {
			for (int l = 1; l < j; l++) {
				BlockPos blockPos3 = blockPos.offset(direction, l);
				BlockState blockState5 = blockStates[l];
				if (blockState5 != null) {
					world.method_8652(blockPos3, blockState5.method_11657(field_11669, Boolean.valueOf(bl5)), 3);
					if (!world.method_8320(blockPos3).isAir()) {
					}
				}
			}
		}
	}

	@Override
	public void method_9588(BlockState blockState, World world, BlockPos blockPos, Random random) {
		this.method_10776(world, blockPos, blockState, false, true, -1, null);
	}

	private void playSound(World world, BlockPos blockPos, boolean bl, boolean bl2, boolean bl3, boolean bl4) {
		if (bl2 && !bl4) {
			world.playSound(null, blockPos, SoundEvents.field_14674, SoundCategory.field_15245, 0.4F, 0.6F);
		} else if (!bl2 && bl4) {
			world.playSound(null, blockPos, SoundEvents.field_14787, SoundCategory.field_15245, 0.4F, 0.5F);
		} else if (bl && !bl3) {
			world.playSound(null, blockPos, SoundEvents.field_14859, SoundCategory.field_15245, 0.4F, 0.7F);
		} else if (!bl && bl3) {
			world.playSound(null, blockPos, SoundEvents.field_14595, SoundCategory.field_15245, 0.4F, 1.2F / (world.random.nextFloat() * 0.2F + 0.9F));
		}
	}

	private void updateNeighborsOnAxis(World world, BlockPos blockPos, Direction direction) {
		world.method_8452(blockPos, this);
		world.method_8452(blockPos.offset(direction.getOpposite()), this);
	}

	@Override
	public void method_9536(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (!bl && blockState.getBlock() != blockState2.getBlock()) {
			boolean bl2 = (Boolean)blockState.method_11654(field_11669);
			boolean bl3 = (Boolean)blockState.method_11654(field_11671);
			if (bl2 || bl3) {
				this.method_10776(world, blockPos, blockState, true, false, -1, null);
			}

			if (bl3) {
				world.method_8452(blockPos, this);
				world.method_8452(blockPos.offset(((Direction)blockState.method_11654(field_11666)).getOpposite()), this);
			}

			super.method_9536(blockState, world, blockPos, blockState2, bl);
		}
	}

	@Override
	public int method_9524(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		return blockState.method_11654(field_11671) ? 15 : 0;
	}

	@Override
	public int method_9603(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		if (!(Boolean)blockState.method_11654(field_11671)) {
			return 0;
		} else {
			return blockState.method_11654(field_11666) == direction ? 15 : 0;
		}
	}

	@Override
	public boolean method_9506(BlockState blockState) {
		return true;
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	@Override
	public BlockState method_9598(BlockState blockState, BlockRotation blockRotation) {
		return blockState.method_11657(field_11666, blockRotation.rotate(blockState.method_11654(field_11666)));
	}

	@Override
	public BlockState method_9569(BlockState blockState, BlockMirror blockMirror) {
		return blockState.rotate(blockMirror.method_10345(blockState.method_11654(field_11666)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_11666, field_11671, field_11669);
	}
}
