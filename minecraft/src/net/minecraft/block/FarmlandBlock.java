package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
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

public class FarmlandBlock extends Block {
	public static final IntegerProperty field_11009 = Properties.MOISTURE;
	protected static final VoxelShape field_11010 = Block.createCubeShape(0.0, 0.0, 0.0, 16.0, 15.0, 16.0);

	protected FarmlandBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateFactory.getDefaultState().with(field_11009, Integer.valueOf(0)));
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		if (direction == Direction.UP && !blockState.canPlaceAt(iWorld, blockPos)) {
			iWorld.getBlockTickScheduler().schedule(blockPos, this, 1);
		}

		return super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		BlockState blockState2 = viewableWorld.getBlockState(blockPos.up());
		return !blockState2.getMaterial().method_15799() || blockState2.getBlock() instanceof FenceGateBlock;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		return !this.getDefaultState().canPlaceAt(itemPlacementContext.getWorld(), itemPlacementContext.getPos())
			? Blocks.field_10566.getDefaultState()
			: super.getPlacementState(itemPlacementContext);
	}

	@Override
	public boolean method_9526(BlockState blockState) {
		return true;
	}

	@Override
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return field_11010;
	}

	@Override
	public void scheduledTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		if (!blockState.canPlaceAt(world, blockPos)) {
			method_10125(blockState, world, blockPos);
		} else {
			int i = (Integer)blockState.get(field_11009);
			if (!method_10126(world, blockPos) && !world.method_8520(blockPos.up())) {
				if (i > 0) {
					world.setBlockState(blockPos, blockState.with(field_11009, Integer.valueOf(i - 1)), 2);
				} else if (!method_10124(world, blockPos)) {
					method_10125(blockState, world, blockPos);
				}
			} else if (i < 7) {
				world.setBlockState(blockPos, blockState.with(field_11009, Integer.valueOf(7)), 2);
			}
		}
	}

	@Override
	public void onLandedUpon(World world, BlockPos blockPos, Entity entity, float f) {
		if (!world.isRemote
			&& world.random.nextFloat() < f - 0.5F
			&& entity instanceof LivingEntity
			&& (entity instanceof PlayerEntity || world.getGameRules().getBoolean("mobGriefing"))
			&& entity.width * entity.width * entity.height > 0.512F) {
			method_10125(world.getBlockState(blockPos), world, blockPos);
		}

		super.onLandedUpon(world, blockPos, entity, f);
	}

	public static void method_10125(BlockState blockState, World world, BlockPos blockPos) {
		world.setBlockState(blockPos, method_9582(blockState, Blocks.field_10566.getDefaultState(), world, blockPos));
	}

	private static boolean method_10124(BlockView blockView, BlockPos blockPos) {
		Block block = blockView.getBlockState(blockPos.up()).getBlock();
		return block instanceof CropBlock || block instanceof StemBlock || block instanceof StemAttachedBlock;
	}

	private static boolean method_10126(ViewableWorld viewableWorld, BlockPos blockPos) {
		for (BlockPos.Mutable mutable : BlockPos.iterateBoxPositionsMutable(blockPos.add(-4, 0, -4), blockPos.add(4, 1, 4))) {
			if (viewableWorld.getFluidState(mutable).matches(FluidTags.field_15517)) {
				return true;
			}
		}

		return false;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.with(field_11009);
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, PlacementEnvironment placementEnvironment) {
		return false;
	}
}
