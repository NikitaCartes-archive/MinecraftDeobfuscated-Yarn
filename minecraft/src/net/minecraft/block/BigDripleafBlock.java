package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.enums.Tilt;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;

public class BigDripleafBlock extends HorizontalFacingBlock implements Fertilizable, Waterloggable {
	private static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	private static final EnumProperty<Tilt> TILT = Properties.TILT;
	private static final Object2IntMap<Tilt> NEXT_TILT_DELAYS = Util.make(new Object2IntArrayMap<>(), object2IntArrayMap -> {
		object2IntArrayMap.defaultReturnValue(-1);
		object2IntArrayMap.put(Tilt.UNSTABLE, 20);
		object2IntArrayMap.put(Tilt.PARTIAL, 10);
		object2IntArrayMap.put(Tilt.FULL, 100);
	});
	private static final Box UNTILTED_SHAPE = Block.createCuboidShape(0.0, 11.0, 0.0, 16.0, 16.0, 16.0).getBoundingBox();
	private static final Map<Tilt, VoxelShape> SHAPES_FOR_TILT = ImmutableMap.of(
		Tilt.NONE,
		Block.createCuboidShape(0.0, 11.0, 0.0, 16.0, 15.0, 16.0),
		Tilt.UNSTABLE,
		Block.createCuboidShape(0.0, 11.0, 0.0, 16.0, 15.0, 16.0),
		Tilt.PARTIAL,
		Block.createCuboidShape(0.0, 11.0, 0.0, 16.0, 13.0, 16.0),
		Tilt.FULL,
		VoxelShapes.empty()
	);
	private static final Map<Direction, VoxelShape> SHAPES_FOR_DIRECTION = ImmutableMap.of(
		Direction.NORTH,
		Block.createCuboidShape(5.0, 0.0, 8.0, 11.0, 11.0, 14.0),
		Direction.SOUTH,
		Block.createCuboidShape(5.0, 0.0, 2.0, 11.0, 11.0, 8.0),
		Direction.EAST,
		Block.createCuboidShape(2.0, 0.0, 5.0, 8.0, 11.0, 11.0),
		Direction.WEST,
		Block.createCuboidShape(8.0, 0.0, 5.0, 14.0, 11.0, 11.0)
	);
	private final Map<BlockState, VoxelShape> shapes;

	protected BigDripleafBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(WATERLOGGED, Boolean.valueOf(false)).with(FACING, Direction.NORTH).with(TILT, Tilt.NONE));
		this.shapes = this.getShapesForStates(BigDripleafBlock::getShapeForState);
	}

	protected static VoxelShape getShapeForState(BlockState state) {
		return VoxelShapes.union(getShapeForStateTilt(state), getShapeForStateDirection(state));
	}

	private static VoxelShape getShapeForStateDirection(BlockState state) {
		return (VoxelShape)SHAPES_FOR_DIRECTION.get(state.get(FACING));
	}

	private static VoxelShape getShapeForStateTilt(BlockState state) {
		return (VoxelShape)SHAPES_FOR_TILT.get(state.get(TILT));
	}

	protected static void grow(World world, Random random, BlockPos pos) {
		int i = world.getTopHeightLimit() - pos.getY();
		int j = 1 + random.nextInt(5);
		int k = Math.min(j, i);
		Direction direction = Direction.Type.HORIZONTAL.random(random);
		BlockPos.Mutable mutable = pos.mutableCopy();

		for (int l = 0; l < k; l++) {
			Block block = l == k - 1 ? Blocks.BIG_DRIPLEAF : Blocks.BIG_DRIPLEAF_STEM;
			BlockState blockState = block.getDefaultState()
				.with(WATERLOGGED, Boolean.valueOf(world.getFluidState(mutable).getFluid() == Fluids.WATER))
				.with(HorizontalFacingBlock.FACING, direction);
			world.setBlockState(mutable, blockState, 2);
			mutable.move(Direction.UP);
		}
	}

	@Override
	public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
		world.breakBlock(hit.getBlockPos(), true, projectile);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		BlockPos blockPos = pos.down();
		BlockState blockState = world.getBlockState(blockPos);
		return blockState.isOf(Blocks.BIG_DRIPLEAF_STEM) || blockState.isSideSolidFullSquare(world, blockPos, Direction.UP);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
		if (!state.canPlaceAt(world, pos)) {
			world.breakBlock(pos, true);
		}

		if ((Boolean)state.get(WATERLOGGED)) {
			world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
	}

	@Override
	public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
		BlockState blockState = world.getBlockState(pos.up());
		return blockState.isAir() || blockState.getFluidState().isIn(FluidTags.WATER);
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
		BlockPos blockPos = pos.up();
		if (world.isInBuildLimit(blockPos)) {
			BlockState blockState = world.getBlockState(blockPos);
			Fluid fluid = blockState.getFluidState().getFluid();
			boolean bl;
			if (!blockState.isAir() && fluid != Fluids.FLOWING_WATER) {
				if (fluid != Fluids.WATER) {
					return;
				}

				bl = true;
			} else {
				bl = false;
			}

			world.setBlockState(blockPos, Blocks.BIG_DRIPLEAF.getDefaultState().with(FACING, state.get(FACING)).with(WATERLOGGED, Boolean.valueOf(bl)), 2);
			world.setBlockState(pos, Blocks.BIG_DRIPLEAF_STEM.getDefaultState().with(FACING, state.get(FACING)).with(WATERLOGGED, state.get(WATERLOGGED)), 2);
		}
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (!world.isClient) {
			if (state.get(TILT) == Tilt.NONE && shouldEntityTilt(pos, entity, true)) {
				this.changeTilt(state, world, pos, Tilt.UNSTABLE, null);
			}
		}
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		Tilt tilt = state.get(TILT);
		if (tilt == Tilt.UNSTABLE) {
			if (shouldTilt(world, pos, true)) {
				this.changeTilt(state, world, pos, Tilt.PARTIAL, SoundEvents.BLOCK_BIG_DRIPLEAF_TILT_DOWN);
			} else {
				this.resetTilt(state, world, pos);
			}
		} else if (tilt == Tilt.PARTIAL) {
			if (shouldTilt(world, pos, false)) {
				this.changeTilt(state, world, pos, Tilt.FULL, SoundEvents.BLOCK_BIG_DRIPLEAF_TILT_DOWN);
			} else {
				this.changeTilt(state, world, pos, Tilt.UNSTABLE, SoundEvents.BLOCK_BIG_DRIPLEAF_TILT_UP);
			}
		} else if (tilt == Tilt.FULL) {
			this.resetTilt(state, world, pos);
		}
	}

	private void playTiltSound(World world, BlockPos pos, SoundEvent sound) {
		float f = MathHelper.nextBetween(world.random, 0.8F, 1.2F);
		world.playSound(null, pos, sound, SoundCategory.BLOCKS, 1.0F, f);
	}

	private static boolean shouldTilt(World world, BlockPos pos, boolean bl) {
		Predicate<Entity> predicate = EntityPredicates.EXCEPT_SPECTATOR.and(entity -> shouldEntityTilt(pos, entity, bl));
		return !world.getOtherEntities((Entity)null, UNTILTED_SHAPE.offset(pos), predicate).isEmpty();
	}

	private static boolean shouldEntityTilt(BlockPos pos, Entity entity, boolean bl) {
		return bl && entity.bypassesSteppingEffects() ? false : isEntityAbove(pos, entity);
	}

	private static boolean isEntityAbove(BlockPos pos, Entity entity) {
		return entity.getPos().y > (double)((float)pos.getY() + 0.6875F);
	}

	private void changeTilt(BlockState state, World world, BlockPos pos, Tilt tilt, @Nullable SoundEvent sound) {
		this.changeTilt(state, world, pos, tilt);
		if (sound != null) {
			this.playTiltSound(world, pos, sound);
		}

		int i = NEXT_TILT_DELAYS.getInt(tilt);
		if (i != -1) {
			world.getBlockTickScheduler().schedule(pos, this, i);
		}
	}

	private void resetTilt(BlockState state, World world, BlockPos pos) {
		this.changeTilt(state, world, pos, Tilt.NONE);
		this.playTiltSound(world, pos, SoundEvents.BLOCK_BIG_DRIPLEAF_TILT_UP);
	}

	private void changeTilt(BlockState state, World world, BlockPos pos, Tilt tilt) {
		world.setBlockState(pos, state.with(TILT, tilt), 2);
		if (tilt.isStable()) {
			world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos);
		}
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return getShapeForStateTilt(state);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return (VoxelShape)this.shapes.get(state);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
		return this.getDefaultState().with(WATERLOGGED, Boolean.valueOf(fluidState.isEqualAndStill(Fluids.WATER))).with(FACING, ctx.getPlayerFacing().getOpposite());
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(WATERLOGGED, FACING, TILT);
	}
}
