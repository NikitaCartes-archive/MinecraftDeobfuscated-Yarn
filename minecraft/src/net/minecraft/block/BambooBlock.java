package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.enums.BambooLeaves;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.SwordItem;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class BambooBlock extends Block implements Fertilizable {
	protected static final VoxelShape SMALL_LEAVES_SHAPE = Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 16.0, 11.0);
	protected static final VoxelShape LARGE_LEAVES_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 16.0, 13.0);
	protected static final VoxelShape NO_LEAVES_SHAPE = Block.createCuboidShape(6.5, 0.0, 6.5, 9.5, 16.0, 9.5);
	public static final IntProperty AGE = Properties.AGE_1;
	public static final EnumProperty<BambooLeaves> LEAVES = Properties.BAMBOO_LEAVES;
	public static final IntProperty STAGE = Properties.STAGE;

	public BambooBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(AGE, Integer.valueOf(0)).with(LEAVES, BambooLeaves.field_12469).with(STAGE, Integer.valueOf(0)));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(AGE, LEAVES, STAGE);
	}

	@Override
	public Block.OffsetType getOffsetType() {
		return Block.OffsetType.field_10657;
	}

	@Override
	public boolean isTranslucent(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return true;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		VoxelShape voxelShape = blockState.get(LEAVES) == BambooLeaves.field_12468 ? LARGE_LEAVES_SHAPE : SMALL_LEAVES_SHAPE;
		Vec3d vec3d = blockState.getOffsetPos(blockView, blockPos);
		return voxelShape.offset(vec3d.x, vec3d.y, vec3d.z);
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		Vec3d vec3d = blockState.getOffsetPos(blockView, blockPos);
		return NO_LEAVES_SHAPE.offset(vec3d.x, vec3d.y, vec3d.z);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		FluidState fluidState = itemPlacementContext.getWorld().getFluidState(itemPlacementContext.getBlockPos());
		if (!fluidState.isEmpty()) {
			return null;
		} else {
			BlockState blockState = itemPlacementContext.getWorld().getBlockState(itemPlacementContext.getBlockPos().down());
			if (blockState.matches(BlockTags.field_15497)) {
				Block block = blockState.getBlock();
				if (block == Blocks.field_10108) {
					return this.getDefaultState().with(AGE, Integer.valueOf(0));
				} else if (block == Blocks.field_10211) {
					int i = blockState.get(AGE) > 0 ? 1 : 0;
					return this.getDefaultState().with(AGE, Integer.valueOf(i));
				} else {
					return Blocks.field_10108.getDefaultState();
				}
			} else {
				return null;
			}
		}
	}

	@Override
	public void onScheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!blockState.canPlaceAt(world, blockPos)) {
			world.breakBlock(blockPos, true);
		} else if ((Integer)blockState.get(STAGE) == 0) {
			if (random.nextInt(3) == 0 && world.isAir(blockPos.up()) && world.getLightLevel(blockPos.up(), 0) >= 9) {
				int i = this.countBambooBelow(world, blockPos) + 1;
				if (i < 16) {
					this.updateLeaves(blockState, world, blockPos, random, i);
				}
			}
		}
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		return viewableWorld.getBlockState(blockPos.down()).matches(BlockTags.field_15497);
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		if (!blockState.canPlaceAt(iWorld, blockPos)) {
			iWorld.getBlockTickScheduler().schedule(blockPos, this, 1);
		}

		if (direction == Direction.field_11036 && blockState2.getBlock() == Blocks.field_10211 && (Integer)blockState2.get(AGE) > (Integer)blockState.get(AGE)) {
			iWorld.setBlockState(blockPos, blockState.cycle(AGE), 2);
		}

		return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public boolean isFertilizable(BlockView blockView, BlockPos blockPos, BlockState blockState, boolean bl) {
		int i = this.countBambooAbove(blockView, blockPos);
		int j = this.countBambooBelow(blockView, blockPos);
		return i + j + 1 < 16 && (Integer)blockView.getBlockState(blockPos.up(i)).get(STAGE) != 1;
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos blockPos, BlockState blockState) {
		return true;
	}

	@Override
	public void grow(World world, Random random, BlockPos blockPos, BlockState blockState) {
		int i = this.countBambooAbove(world, blockPos);
		int j = this.countBambooBelow(world, blockPos);
		int k = i + j + 1;
		int l = 1 + random.nextInt(2);

		for (int m = 0; m < l; m++) {
			BlockPos blockPos2 = blockPos.up(i);
			BlockState blockState2 = world.getBlockState(blockPos2);
			if (k >= 16 || (Integer)blockState2.get(STAGE) == 1 || !world.isAir(blockPos2.up())) {
				return;
			}

			this.updateLeaves(blockState2, world, blockPos2, random, k);
			i++;
			k++;
		}
	}

	@Override
	public float calcBlockBreakingDelta(BlockState blockState, PlayerEntity playerEntity, BlockView blockView, BlockPos blockPos) {
		return playerEntity.getMainHandStack().getItem() instanceof SwordItem ? 1.0F : super.calcBlockBreakingDelta(blockState, playerEntity, blockView, blockPos);
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.field_9174;
	}

	protected void updateLeaves(BlockState blockState, World world, BlockPos blockPos, Random random, int i) {
		BlockState blockState2 = world.getBlockState(blockPos.down());
		BlockPos blockPos2 = blockPos.down(2);
		BlockState blockState3 = world.getBlockState(blockPos2);
		BambooLeaves bambooLeaves = BambooLeaves.field_12469;
		if (i >= 1) {
			if (blockState2.getBlock() != Blocks.field_10211 || blockState2.get(LEAVES) == BambooLeaves.field_12469) {
				bambooLeaves = BambooLeaves.field_12466;
			} else if (blockState2.getBlock() == Blocks.field_10211 && blockState2.get(LEAVES) != BambooLeaves.field_12469) {
				bambooLeaves = BambooLeaves.field_12468;
				if (blockState3.getBlock() == Blocks.field_10211) {
					world.setBlockState(blockPos.down(), blockState2.with(LEAVES, BambooLeaves.field_12466), 3);
					world.setBlockState(blockPos2, blockState3.with(LEAVES, BambooLeaves.field_12469), 3);
				}
			}
		}

		int j = blockState.get(AGE) != 1 && blockState3.getBlock() != Blocks.field_10211 ? 0 : 1;
		int k = (i < 11 || !(random.nextFloat() < 0.25F)) && i != 15 ? 0 : 1;
		world.setBlockState(blockPos.up(), this.getDefaultState().with(AGE, Integer.valueOf(j)).with(LEAVES, bambooLeaves).with(STAGE, Integer.valueOf(k)), 3);
	}

	protected int countBambooAbove(BlockView blockView, BlockPos blockPos) {
		int i = 0;

		while (i < 16 && blockView.getBlockState(blockPos.up(i + 1)).getBlock() == Blocks.field_10211) {
			i++;
		}

		return i;
	}

	protected int countBambooBelow(BlockView blockView, BlockPos blockPos) {
		int i = 0;

		while (i < 16 && blockView.getBlockState(blockPos.down(i + 1)).getBlock() == Blocks.field_10211) {
			i++;
		}

		return i;
	}
}
