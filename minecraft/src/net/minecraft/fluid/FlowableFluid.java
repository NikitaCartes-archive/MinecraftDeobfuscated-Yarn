package net.minecraft.fluid;

import com.google.common.collect.Maps;
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
	private static final ThreadLocal<Object2ByteLinkedOpenHashMap<FlowableFluid.NeighborGroup>> field_15901 = ThreadLocal.withInitial(() -> {
		Object2ByteLinkedOpenHashMap<FlowableFluid.NeighborGroup> object2ByteLinkedOpenHashMap = new Object2ByteLinkedOpenHashMap<FlowableFluid.NeighborGroup>(200) {
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

	protected void tryFlow(World world, BlockPos fluidPos, BlockState blockState, FluidState fluidState) {
		if (!fluidState.isEmpty()) {
			BlockPos blockPos = fluidPos.down();
			BlockState blockState2 = world.getBlockState(blockPos);
			FluidState fluidState2 = blockState2.getFluidState();
			if (this.canFlowThrough(world, fluidPos, blockState, Direction.DOWN, blockPos, blockState2, fluidState2)) {
				FluidState fluidState3 = this.getUpdatedState(world, blockPos, blockState2);
				Fluid fluid = fluidState3.getFluid();
				if (fluidState2.canBeReplacedWith(world, blockPos, fluid, Direction.DOWN) && canFillWithFluid(world, blockPos, blockState2, fluid)) {
					this.flow(world, blockPos, blockState2, Direction.DOWN, fluidState3);
					if (this.countNeighboringSources(world, fluidPos) >= 3) {
						this.flowToSides(world, fluidPos, fluidState, blockState);
					}

					return;
				}
			}

			if (fluidState.isStill() || !this.canFlowDownTo(world, fluidPos, blockState, blockPos, blockState2)) {
				this.flowToSides(world, fluidPos, fluidState, blockState);
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
				this.flow(world, blockPos, world.getBlockState(blockPos), direction, fluidState2);
			}
		}
	}

	protected FluidState getUpdatedState(World world, BlockPos pos, BlockState state) {
		int i = 0;
		int j = 0;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (Direction direction : Direction.Type.HORIZONTAL) {
			BlockPos blockPos = mutable.set(pos, direction);
			BlockState blockState = world.getBlockState(blockPos);
			FluidState fluidState = blockState.getFluidState();
			if (fluidState.getFluid().matchesType(this) && receivesFlow(direction, world, pos, state, blockPos, blockState)) {
				if (fluidState.isStill()) {
					j++;
				}

				i = Math.max(i, fluidState.getLevel());
			}
		}

		if (j >= 2 && this.isInfinite(world)) {
			BlockState blockState2 = world.getBlockState(mutable.set(pos, Direction.DOWN));
			FluidState fluidState2 = blockState2.getFluidState();
			if (blockState2.isSolid() || this.isMatchingAndStill(fluidState2)) {
				return this.getStill(false);
			}
		}

		BlockPos blockPos2 = mutable.set(pos, Direction.UP);
		BlockState blockState3 = world.getBlockState(blockPos2);
		FluidState fluidState3 = blockState3.getFluidState();
		if (!fluidState3.isEmpty() && fluidState3.getFluid().matchesType(this) && receivesFlow(Direction.UP, world, pos, state, blockPos2, blockState3)) {
			return this.getFlowing(8, true);
		} else {
			int k = i - this.getLevelDecreasePerBlock(world);
			return k <= 0 ? Fluids.EMPTY.getDefaultState() : this.getFlowing(k, false);
		}
	}

	private static boolean receivesFlow(Direction face, BlockView world, BlockPos pos, BlockState state, BlockPos fromPos, BlockState fromState) {
		VoxelShape voxelShape = fromState.getCollisionShape(world, fromPos);
		if (voxelShape == VoxelShapes.fullCube()) {
			return false;
		} else {
			VoxelShape voxelShape2 = state.getCollisionShape(world, pos);
			if (voxelShape2 == VoxelShapes.fullCube()) {
				return false;
			} else if (voxelShape2 == VoxelShapes.empty() && voxelShape == VoxelShapes.empty()) {
				return true;
			} else {
				Object2ByteLinkedOpenHashMap<FlowableFluid.NeighborGroup> object2ByteLinkedOpenHashMap;
				if (!state.getBlock().hasDynamicBounds() && !fromState.getBlock().hasDynamicBounds()) {
					object2ByteLinkedOpenHashMap = (Object2ByteLinkedOpenHashMap<FlowableFluid.NeighborGroup>)field_15901.get();
				} else {
					object2ByteLinkedOpenHashMap = null;
				}

				FlowableFluid.NeighborGroup neighborGroup;
				if (object2ByteLinkedOpenHashMap != null) {
					neighborGroup = new FlowableFluid.NeighborGroup(state, fromState, face);
					byte b = object2ByteLinkedOpenHashMap.getAndMoveToFirst(neighborGroup);
					if (b != 127) {
						return b != 0;
					}
				} else {
					neighborGroup = null;
				}

				boolean bl = !VoxelShapes.adjacentSidesCoverSquare(voxelShape2, voxelShape, face);
				if (object2ByteLinkedOpenHashMap != null) {
					if (object2ByteLinkedOpenHashMap.size() == 200) {
						object2ByteLinkedOpenHashMap.removeLastByte();
					}

					object2ByteLinkedOpenHashMap.putAndMoveToFirst(neighborGroup, (byte)(bl ? 1 : 0));
				}

				return bl;
			}
		}
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
		if (state.getBlock() instanceof FluidFillable fluidFillable) {
			fluidFillable.tryFillWithFluid(world, pos, state, fluidState);
		} else {
			if (!state.isAir()) {
				this.beforeBreakingBlock(world, pos, state);
			}

			world.setBlockState(pos, fluidState.getBlockState(), Block.NOTIFY_ALL);
		}
	}

	protected abstract void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state);

	/**
	 * Finds the distance to the closest hole the fluid can flow down into starting with the direction specified.
	 */
	protected int getMinFlowDownDistance(WorldView world, BlockPos pos, int i, Direction direction, BlockState state, FlowableFluid.SpreadCache spreadCache) {
		int j = 1000;

		for (Direction direction2 : Direction.Type.HORIZONTAL) {
			if (direction2 != direction) {
				BlockPos blockPos = pos.offset(direction2);
				BlockState blockState = spreadCache.getBlockState(blockPos);
				FluidState fluidState = blockState.getFluidState();
				if (this.canFlowThrough(world, this.getFlowing(), pos, state, direction2, blockPos, blockState, fluidState)) {
					if (spreadCache.canFlowDownTo(blockPos)) {
						return i;
					}

					if (i < this.getMaxFlowDistance(world)) {
						int k = this.getMinFlowDownDistance(world, blockPos, i + 1, direction2.getOpposite(), blockState, spreadCache);
						if (k < j) {
							j = k;
						}
					}
				}
			}
		}

		return j;
	}

	boolean canFlowDownTo(BlockView world, BlockPos pos, BlockState state, BlockPos fromPos, BlockState fromState) {
		if (!receivesFlow(Direction.DOWN, world, pos, state, fromPos, fromState)) {
			return false;
		} else {
			return fromState.getFluidState().getFluid().matchesType(this) ? true : canFill(world, fromPos, fromState, this.getFlowing());
		}
	}

	private boolean canFlowThrough(
		BlockView world, Fluid fluid, BlockPos pos, BlockState state, Direction face, BlockPos fromPos, BlockState fromState, FluidState fluidState
	) {
		return this.canFlowThrough(world, pos, state, face, fromPos, fromState, fluidState) && canFillWithFluid(world, fromPos, fromState, fluid);
	}

	private boolean canFlowThrough(BlockView world, BlockPos pos, BlockState state, Direction face, BlockPos fromPos, BlockState fromState, FluidState fluidState) {
		return !this.isMatchingAndStill(fluidState) && canFill(fromState) && receivesFlow(face, world, pos, state, fromPos, fromState);
	}

	private boolean isMatchingAndStill(FluidState state) {
		return state.getFluid().matchesType(this) && state.isStill();
	}

	/**
	 * {@return the maximum horizontal distance to check for holes the fluid can flow down into}
	 */
	protected abstract int getMaxFlowDistance(WorldView world);

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
		FlowableFluid.SpreadCache spreadCache = null;

		for (Direction direction : Direction.Type.HORIZONTAL) {
			BlockPos blockPos = pos.offset(direction);
			BlockState blockState = world.getBlockState(blockPos);
			FluidState fluidState = blockState.getFluidState();
			if (this.canFlowThrough(world, pos, state, direction, blockPos, blockState, fluidState)) {
				FluidState fluidState2 = this.getUpdatedState(world, blockPos, blockState);
				if (canFillWithFluid(world, blockPos, blockState, fluidState2.getFluid())) {
					if (spreadCache == null) {
						spreadCache = new FlowableFluid.SpreadCache(world, pos);
					}

					int j;
					if (spreadCache.canFlowDownTo(blockPos)) {
						j = 0;
					} else {
						j = this.getMinFlowDownDistance(world, blockPos, 1, direction.getOpposite(), blockState, spreadCache);
					}

					if (j < i) {
						map.clear();
					}

					if (j <= i) {
						if (fluidState.canBeReplacedWith(world, blockPos, fluidState2.getFluid(), direction)) {
							map.put(direction, fluidState2);
						}

						i = j;
					}
				}
			}
		}

		return map;
	}

	private static boolean canFill(BlockState state) {
		Block block = state.getBlock();
		if (block instanceof FluidFillable) {
			return true;
		} else {
			return state.blocksMovement()
				? false
				: !(block instanceof DoorBlock)
					&& !state.isIn(BlockTags.SIGNS)
					&& !state.isOf(Blocks.LADDER)
					&& !state.isOf(Blocks.SUGAR_CANE)
					&& !state.isOf(Blocks.BUBBLE_COLUMN)
					&& !state.isOf(Blocks.NETHER_PORTAL)
					&& !state.isOf(Blocks.END_PORTAL)
					&& !state.isOf(Blocks.END_GATEWAY)
					&& !state.isOf(Blocks.STRUCTURE_VOID);
		}
	}

	private static boolean canFill(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
		return canFill(state) && canFillWithFluid(world, pos, state, fluid);
	}

	private static boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
		return state.getBlock() instanceof FluidFillable fluidFillable ? fluidFillable.canFillWithFluid(null, world, pos, state, fluid) : true;
	}

	protected abstract int getLevelDecreasePerBlock(WorldView world);

	protected int getNextTickDelay(World world, BlockPos pos, FluidState oldState, FluidState newState) {
		return this.getTickRate(world);
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState blockState, FluidState fluidState) {
		if (!fluidState.isStill()) {
			FluidState fluidState2 = this.getUpdatedState(world, pos, world.getBlockState(pos));
			int i = this.getNextTickDelay(world, pos, fluidState, fluidState2);
			if (fluidState2.isEmpty()) {
				fluidState = fluidState2;
				blockState = Blocks.AIR.getDefaultState();
				world.setBlockState(pos, blockState, Block.NOTIFY_ALL);
			} else if (!fluidState2.equals(fluidState)) {
				fluidState = fluidState2;
				blockState = fluidState2.getBlockState();
				world.setBlockState(pos, blockState, Block.NOTIFY_ALL);
				world.scheduleFluidTick(pos, fluidState2.getFluid(), i);
			}
		}

		this.tryFlow(world, pos, blockState, fluidState);
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

	static record NeighborGroup(BlockState self, BlockState other, Direction facing) {
		public boolean equals(Object o) {
			if (o instanceof FlowableFluid.NeighborGroup neighborGroup
				&& this.self == neighborGroup.self
				&& this.other == neighborGroup.other
				&& this.facing == neighborGroup.facing) {
				return true;
			}

			return false;
		}

		public int hashCode() {
			int i = System.identityHashCode(this.self);
			i = 31 * i + System.identityHashCode(this.other);
			return 31 * i + this.facing.hashCode();
		}
	}

	protected class SpreadCache {
		private final BlockView world;
		private final BlockPos startPos;
		private final Short2ObjectMap<BlockState> stateCache = new Short2ObjectOpenHashMap<>();
		private final Short2BooleanMap flowDownCache = new Short2BooleanOpenHashMap();

		SpreadCache(final BlockView world, final BlockPos startPos) {
			this.world = world;
			this.startPos = startPos;
		}

		public BlockState getBlockState(BlockPos pos) {
			return this.getBlockState(pos, this.pack(pos));
		}

		private BlockState getBlockState(BlockPos pos, short packed) {
			return this.stateCache.computeIfAbsent(packed, (Short2ObjectFunction<? extends BlockState>)(packedPos -> this.world.getBlockState(pos)));
		}

		public boolean canFlowDownTo(BlockPos pos) {
			return this.flowDownCache.computeIfAbsent(this.pack(pos), (Short2BooleanFunction)(packed -> {
				BlockState blockState = this.getBlockState(pos, packed);
				BlockPos blockPos2 = pos.down();
				BlockState blockState2 = this.world.getBlockState(blockPos2);
				return FlowableFluid.this.canFlowDownTo(this.world, pos, blockState, blockPos2, blockState2);
			}));
		}

		private short pack(BlockPos pos) {
			int i = pos.getX() - this.startPos.getX();
			int j = pos.getZ() - this.startPos.getZ();
			return (short)((i + 128 & 0xFF) << 8 | j + 128 & 0xFF);
		}
	}
}
