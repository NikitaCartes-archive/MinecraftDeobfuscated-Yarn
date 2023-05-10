package net.minecraft.fluid;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanFunction;
import it.unimi.dsi.fastutil.shorts.Short2BooleanMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectFunction;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.IceBlock;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

/**
 * Represents a fluid which can flow.
 */
public abstract class FlowableFluid extends Fluid {
	public static final BooleanProperty FALLING = Properties.FALLING;
	public static final IntProperty LEVEL = Properties.LEVEL_1_8;
	private static final int field_31726 = 200;
	private static final ThreadLocal<Object2ByteLinkedOpenHashMap<Block.NeighborGroup>> field_15901 = ThreadLocal.withInitial(() -> {
		Object2ByteLinkedOpenHashMap<Block.NeighborGroup> object2ByteLinkedOpenHashMap = new Object2ByteLinkedOpenHashMap<Block.NeighborGroup>(200) {
			@Override
			protected void rehash(int i) {
			}
		};
		object2ByteLinkedOpenHashMap.defaultReturnValue((byte)127);
		return object2ByteLinkedOpenHashMap;
	});
	private final Map<FluidState, VoxelShape> shapeCache = Maps.<FluidState, VoxelShape>newIdentityHashMap();

	@Override
	protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
		builder.add(FALLING);
	}

	@Override
	public Vec3d getVelocity(BlockView world, BlockPos pos, FluidState state) {
		double d = 0.0;
		double e = 0.0;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (Direction direction : Direction.Type.HORIZONTAL) {
			mutable.set(pos, direction);
			FluidState fluidState = world.getFluidState(mutable);
			if (this.isEmptyOrThis(fluidState)) {
				float f = fluidState.getHeight();
				float g = 0.0F;
				if (f == 0.0F) {
					if (!world.getBlockState(mutable).blocksMovement()) {
						BlockPos blockPos = mutable.down();
						FluidState fluidState2 = world.getFluidState(blockPos);
						if (this.isEmptyOrThis(fluidState2)) {
							f = fluidState2.getHeight();
							if (f > 0.0F) {
								g = state.getHeight() - (f - 0.8888889F);
							}
						}
					}
				} else if (f > 0.0F) {
					g = state.getHeight() - f;
				}

				if (g != 0.0F) {
					d += (double)((float)direction.getOffsetX() * g);
					e += (double)((float)direction.getOffsetZ() * g);
				}
			}
		}

		Vec3d vec3d = new Vec3d(d, 0.0, e);
		if ((Boolean)state.get(FALLING)) {
			for (Direction direction2 : Direction.Type.HORIZONTAL) {
				mutable.set(pos, direction2);
				if (this.isFlowBlocked(world, mutable, direction2) || this.isFlowBlocked(world, mutable.up(), direction2)) {
					vec3d = vec3d.normalize().add(0.0, -6.0, 0.0);
					break;
				}
			}
		}

		return vec3d.normalize();
	}

	private boolean isEmptyOrThis(FluidState state) {
		return state.isEmpty() || state.getFluid().matchesType(this);
	}

	protected boolean isFlowBlocked(BlockView world, BlockPos pos, Direction direction) {
		BlockState blockState = world.getBlockState(pos);
		FluidState fluidState = world.getFluidState(pos);
		if (fluidState.getFluid().matchesType(this)) {
			return false;
		} else if (direction == Direction.UP) {
			return true;
		} else {
			return blockState.getBlock() instanceof IceBlock ? false : blockState.isSideSolidFullSquare(world, pos, direction);
		}
	}

	protected void tryFlow(World world, BlockPos fluidPos, FluidState state) {
		if (!state.isEmpty()) {
			BlockState blockState = world.getBlockState(fluidPos);
			BlockPos blockPos = fluidPos.down();
			BlockState blockState2 = world.getBlockState(blockPos);
			FluidState fluidState = this.getUpdatedState(world, blockPos, blockState2);
			if (this.canFlow(world, fluidPos, blockState, Direction.DOWN, blockPos, blockState2, world.getFluidState(blockPos), fluidState.getFluid())) {
				this.flow(world, blockPos, blockState2, Direction.DOWN, fluidState);
				if (this.countNeighboringSources(world, fluidPos) >= 3) {
					this.flowToSides(world, fluidPos, state, blockState);
				}
			} else if (state.isStill() || !this.canFlowDownTo(world, fluidState.getFluid(), fluidPos, blockState, blockPos, blockState2)) {
				this.flowToSides(world, fluidPos, state, blockState);
			}
		}
	}

	private void flowToSides(World world, BlockPos pos, FluidState fluidState, BlockState blockState) {
		int i = fluidState.getLevel() - this.getLevelDecreasePerBlock(world);
		if ((Boolean)fluidState.get(FALLING)) {
			i = 7;
		}

		if (i > 0) {
			Map<Direction, FluidState> map = this.getSpread(world, pos, blockState);

			for (Entry<Direction, FluidState> entry : map.entrySet()) {
				Direction direction = (Direction)entry.getKey();
				FluidState fluidState2 = (FluidState)entry.getValue();
				BlockPos blockPos = pos.offset(direction);
				BlockState blockState2 = world.getBlockState(blockPos);
				if (this.canFlow(world, pos, blockState, direction, blockPos, blockState2, world.getFluidState(blockPos), fluidState2.getFluid())) {
					this.flow(world, blockPos, blockState2, direction, fluidState2);
				}
			}
		}
	}

	protected FluidState getUpdatedState(World world, BlockPos pos, BlockState state) {
		int i = 0;
		int j = 0;

		for (Direction direction : Direction.Type.HORIZONTAL) {
			BlockPos blockPos = pos.offset(direction);
			BlockState blockState = world.getBlockState(blockPos);
			FluidState fluidState = blockState.getFluidState();
			if (fluidState.getFluid().matchesType(this) && this.receivesFlow(direction, world, pos, state, blockPos, blockState)) {
				if (fluidState.isStill()) {
					j++;
				}

				i = Math.max(i, fluidState.getLevel());
			}
		}

		if (this.isInfinite(world) && j >= 2) {
			BlockState blockState2 = world.getBlockState(pos.down());
			FluidState fluidState2 = blockState2.getFluidState();
			if (blockState2.isSolid() || this.isMatchingAndStill(fluidState2)) {
				return this.getStill(false);
			}
		}

		BlockPos blockPos2 = pos.up();
		BlockState blockState3 = world.getBlockState(blockPos2);
		FluidState fluidState3 = blockState3.getFluidState();
		if (!fluidState3.isEmpty() && fluidState3.getFluid().matchesType(this) && this.receivesFlow(Direction.UP, world, pos, state, blockPos2, blockState3)) {
			return this.getFlowing(8, true);
		} else {
			int k = i - this.getLevelDecreasePerBlock(world);
			return k <= 0 ? Fluids.EMPTY.getDefaultState() : this.getFlowing(k, false);
		}
	}

	private boolean receivesFlow(Direction face, BlockView world, BlockPos pos, BlockState state, BlockPos fromPos, BlockState fromState) {
		Object2ByteLinkedOpenHashMap<Block.NeighborGroup> object2ByteLinkedOpenHashMap;
		if (!state.getBlock().hasDynamicBounds() && !fromState.getBlock().hasDynamicBounds()) {
			object2ByteLinkedOpenHashMap = (Object2ByteLinkedOpenHashMap<Block.NeighborGroup>)field_15901.get();
		} else {
			object2ByteLinkedOpenHashMap = null;
		}

		Block.NeighborGroup neighborGroup;
		if (object2ByteLinkedOpenHashMap != null) {
			neighborGroup = new Block.NeighborGroup(state, fromState, face);
			byte b = object2ByteLinkedOpenHashMap.getAndMoveToFirst(neighborGroup);
			if (b != 127) {
				return b != 0;
			}
		} else {
			neighborGroup = null;
		}

		VoxelShape voxelShape = state.getCollisionShape(world, pos);
		VoxelShape voxelShape2 = fromState.getCollisionShape(world, fromPos);
		boolean bl = !VoxelShapes.adjacentSidesCoverSquare(voxelShape, voxelShape2, face);
		if (object2ByteLinkedOpenHashMap != null) {
			if (object2ByteLinkedOpenHashMap.size() == 200) {
				object2ByteLinkedOpenHashMap.removeLastByte();
			}

			object2ByteLinkedOpenHashMap.putAndMoveToFirst(neighborGroup, (byte)(bl ? 1 : 0));
		}

		return bl;
	}

	public abstract Fluid getFlowing();

	public FluidState getFlowing(int level, boolean falling) {
		return this.getFlowing().getDefaultState().with(LEVEL, Integer.valueOf(level)).with(FALLING, Boolean.valueOf(falling));
	}

	public abstract Fluid getStill();

	public FluidState getStill(boolean falling) {
		return this.getStill().getDefaultState().with(FALLING, Boolean.valueOf(falling));
	}

	protected abstract boolean isInfinite(World world);

	protected void flow(WorldAccess world, BlockPos pos, BlockState state, Direction direction, FluidState fluidState) {
		if (state.getBlock() instanceof FluidFillable) {
			((FluidFillable)state.getBlock()).tryFillWithFluid(world, pos, state, fluidState);
		} else {
			if (!state.isAir()) {
				this.beforeBreakingBlock(world, pos, state);
			}

			world.setBlockState(pos, fluidState.getBlockState(), Block.NOTIFY_ALL);
		}
	}

	protected abstract void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state);

	private static short packXZOffset(BlockPos from, BlockPos to) {
		int i = to.getX() - from.getX();
		int j = to.getZ() - from.getZ();
		return (short)((i + 128 & 0xFF) << 8 | j + 128 & 0xFF);
	}

	protected int getFlowSpeedBetween(
		WorldView world,
		BlockPos pos,
		int i,
		Direction direction,
		BlockState state,
		BlockPos fromPos,
		Short2ObjectMap<Pair<BlockState, FluidState>> stateCache,
		Short2BooleanMap flowDownCache
	) {
		int j = 1000;

		for (Direction direction2 : Direction.Type.HORIZONTAL) {
			if (direction2 != direction) {
				BlockPos blockPos = pos.offset(direction2);
				short s = packXZOffset(fromPos, blockPos);
				Pair<BlockState, FluidState> pair = stateCache.computeIfAbsent(s, (Short2ObjectFunction<? extends Pair<BlockState, FluidState>>)(sx -> {
					BlockState blockStatex = world.getBlockState(blockPos);
					return Pair.of(blockStatex, blockStatex.getFluidState());
				}));
				BlockState blockState = pair.getFirst();
				FluidState fluidState = pair.getSecond();
				if (this.canFlowThrough(world, this.getFlowing(), pos, state, direction2, blockPos, blockState, fluidState)) {
					boolean bl = flowDownCache.computeIfAbsent(s, (Short2BooleanFunction)(sx -> {
						BlockPos blockPos2 = blockPos.down();
						BlockState blockState2 = world.getBlockState(blockPos2);
						return this.canFlowDownTo(world, this.getFlowing(), blockPos, blockState, blockPos2, blockState2);
					}));
					if (bl) {
						return i;
					}

					if (i < this.getFlowSpeed(world)) {
						int k = this.getFlowSpeedBetween(world, blockPos, i + 1, direction2.getOpposite(), blockState, fromPos, stateCache, flowDownCache);
						if (k < j) {
							j = k;
						}
					}
				}
			}
		}

		return j;
	}

	private boolean canFlowDownTo(BlockView world, Fluid fluid, BlockPos pos, BlockState state, BlockPos fromPos, BlockState fromState) {
		if (!this.receivesFlow(Direction.DOWN, world, pos, state, fromPos, fromState)) {
			return false;
		} else {
			return fromState.getFluidState().getFluid().matchesType(this) ? true : this.canFill(world, fromPos, fromState, fluid);
		}
	}

	private boolean canFlowThrough(
		BlockView world, Fluid fluid, BlockPos pos, BlockState state, Direction face, BlockPos fromPos, BlockState fromState, FluidState fluidState
	) {
		return !this.isMatchingAndStill(fluidState)
			&& this.receivesFlow(face, world, pos, state, fromPos, fromState)
			&& this.canFill(world, fromPos, fromState, fluid);
	}

	private boolean isMatchingAndStill(FluidState state) {
		return state.getFluid().matchesType(this) && state.isStill();
	}

	protected abstract int getFlowSpeed(WorldView world);

	private int countNeighboringSources(WorldView world, BlockPos pos) {
		int i = 0;

		for (Direction direction : Direction.Type.HORIZONTAL) {
			BlockPos blockPos = pos.offset(direction);
			FluidState fluidState = world.getFluidState(blockPos);
			if (this.isMatchingAndStill(fluidState)) {
				i++;
			}
		}

		return i;
	}

	protected Map<Direction, FluidState> getSpread(World world, BlockPos pos, BlockState state) {
		int i = 1000;
		Map<Direction, FluidState> map = Maps.newEnumMap(Direction.class);
		Short2ObjectMap<Pair<BlockState, FluidState>> short2ObjectMap = new Short2ObjectOpenHashMap<>();
		Short2BooleanMap short2BooleanMap = new Short2BooleanOpenHashMap();

		for (Direction direction : Direction.Type.HORIZONTAL) {
			BlockPos blockPos = pos.offset(direction);
			short s = packXZOffset(pos, blockPos);
			Pair<BlockState, FluidState> pair = short2ObjectMap.computeIfAbsent(s, (Short2ObjectFunction<? extends Pair<BlockState, FluidState>>)(sx -> {
				BlockState blockStatex = world.getBlockState(blockPos);
				return Pair.of(blockStatex, blockStatex.getFluidState());
			}));
			BlockState blockState = pair.getFirst();
			FluidState fluidState = pair.getSecond();
			FluidState fluidState2 = this.getUpdatedState(world, blockPos, blockState);
			if (this.canFlowThrough(world, fluidState2.getFluid(), pos, state, direction, blockPos, blockState, fluidState)) {
				BlockPos blockPos2 = blockPos.down();
				boolean bl = short2BooleanMap.computeIfAbsent(s, (Short2BooleanFunction)(sx -> {
					BlockState blockState2 = world.getBlockState(blockPos2);
					return this.canFlowDownTo(world, this.getFlowing(), blockPos, blockState, blockPos2, blockState2);
				}));
				int j;
				if (bl) {
					j = 0;
				} else {
					j = this.getFlowSpeedBetween(world, blockPos, 1, direction.getOpposite(), blockState, pos, short2ObjectMap, short2BooleanMap);
				}

				if (j < i) {
					map.clear();
				}

				if (j <= i) {
					map.put(direction, fluidState2);
					i = j;
				}
			}
		}

		return map;
	}

	private boolean canFill(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
		Block block = state.getBlock();
		if (block instanceof FluidFillable) {
			return ((FluidFillable)block).canFillWithFluid(world, pos, state, fluid);
		} else if (block instanceof DoorBlock
			|| state.isIn(BlockTags.SIGNS)
			|| state.isOf(Blocks.LADDER)
			|| state.isOf(Blocks.SUGAR_CANE)
			|| state.isOf(Blocks.BUBBLE_COLUMN)) {
			return false;
		} else {
			return !state.isOf(Blocks.NETHER_PORTAL) && !state.isOf(Blocks.END_PORTAL) && !state.isOf(Blocks.END_GATEWAY) && !state.isOf(Blocks.STRUCTURE_VOID)
				? !state.blocksMovement()
				: false;
		}
	}

	protected boolean canFlow(
		BlockView world,
		BlockPos fluidPos,
		BlockState fluidBlockState,
		Direction flowDirection,
		BlockPos flowTo,
		BlockState flowToBlockState,
		FluidState fluidState,
		Fluid fluid
	) {
		return fluidState.canBeReplacedWith(world, flowTo, fluid, flowDirection)
			&& this.receivesFlow(flowDirection, world, fluidPos, fluidBlockState, flowTo, flowToBlockState)
			&& this.canFill(world, flowTo, flowToBlockState, fluid);
	}

	protected abstract int getLevelDecreasePerBlock(WorldView world);

	protected int getNextTickDelay(World world, BlockPos pos, FluidState oldState, FluidState newState) {
		return this.getTickRate(world);
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, FluidState state) {
		if (!state.isStill()) {
			FluidState fluidState = this.getUpdatedState(world, pos, world.getBlockState(pos));
			int i = this.getNextTickDelay(world, pos, state, fluidState);
			if (fluidState.isEmpty()) {
				state = fluidState;
				world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
			} else if (!fluidState.equals(state)) {
				state = fluidState;
				BlockState blockState = fluidState.getBlockState();
				world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS);
				world.scheduleFluidTick(pos, fluidState.getFluid(), i);
				world.updateNeighborsAlways(pos, blockState.getBlock());
			}
		}

		this.tryFlow(world, pos, state);
	}

	protected static int getBlockStateLevel(FluidState state) {
		return state.isStill() ? 0 : 8 - Math.min(state.getLevel(), 8) + (state.get(FALLING) ? 8 : 0);
	}

	private static boolean isFluidAboveEqual(FluidState state, BlockView world, BlockPos pos) {
		return state.getFluid().matchesType(world.getFluidState(pos.up()).getFluid());
	}

	@Override
	public float getHeight(FluidState state, BlockView world, BlockPos pos) {
		return isFluidAboveEqual(state, world, pos) ? 1.0F : state.getHeight();
	}

	@Override
	public float getHeight(FluidState state) {
		return (float)state.getLevel() / 9.0F;
	}

	@Override
	public abstract int getLevel(FluidState state);

	@Override
	public VoxelShape getShape(FluidState state, BlockView world, BlockPos pos) {
		return state.getLevel() == 9 && isFluidAboveEqual(state, world, pos)
			? VoxelShapes.fullCube()
			: (VoxelShape)this.shapeCache.computeIfAbsent(state, state2 -> VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, (double)state2.getHeight(world, pos), 1.0));
	}
}
