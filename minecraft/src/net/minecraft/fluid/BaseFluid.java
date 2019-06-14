package net.minecraft.fluid;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.Material;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public abstract class BaseFluid extends Fluid {
	public static final BooleanProperty FALLING = Properties.field_12480;
	public static final IntProperty LEVEL = Properties.field_12490;
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
	protected void appendProperties(StateFactory.Builder<Fluid, FluidState> builder) {
		builder.method_11667(FALLING);
	}

	@Override
	public Vec3d method_15782(BlockView blockView, BlockPos blockPos, FluidState fluidState) {
		double d = 0.0;
		double e = 0.0;

		Vec3d var28;
		try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
			for (Direction direction : Direction.Type.field_11062) {
				pooledMutable.method_10114(blockPos).method_10118(direction);
				FluidState fluidState2 = blockView.method_8316(pooledMutable);
				if (this.method_15748(fluidState2)) {
					float f = fluidState2.method_20785();
					float g = 0.0F;
					if (f == 0.0F) {
						if (!blockView.method_8320(pooledMutable).method_11620().blocksMovement()) {
							BlockPos blockPos2 = pooledMutable.down();
							FluidState fluidState3 = blockView.method_8316(blockPos2);
							if (this.method_15748(fluidState3)) {
								f = fluidState3.method_20785();
								if (f > 0.0F) {
									g = fluidState.method_20785() - (f - 0.8888889F);
								}
							}
						}
					} else if (f > 0.0F) {
						g = fluidState.method_20785() - f;
					}

					if (g != 0.0F) {
						d += (double)((float)direction.getOffsetX() * g);
						e += (double)((float)direction.getOffsetZ() * g);
					}
				}
			}

			Vec3d vec3d = new Vec3d(d, 0.0, e);
			if ((Boolean)fluidState.method_11654(FALLING)) {
				for (Direction direction2 : Direction.Type.field_11062) {
					pooledMutable.method_10114(blockPos).method_10118(direction2);
					if (this.method_15749(blockView, pooledMutable, direction2) || this.method_15749(blockView, pooledMutable.up(), direction2)) {
						vec3d = vec3d.normalize().add(0.0, -6.0, 0.0);
						break;
					}
				}
			}

			var28 = vec3d.normalize();
		}

		return var28;
	}

	private boolean method_15748(FluidState fluidState) {
		return fluidState.isEmpty() || fluidState.getFluid().matchesType(this);
	}

	protected boolean method_15749(BlockView blockView, BlockPos blockPos, Direction direction) {
		BlockState blockState = blockView.method_8320(blockPos);
		FluidState fluidState = blockView.method_8316(blockPos);
		if (fluidState.getFluid().matchesType(this)) {
			return false;
		} else if (direction == Direction.field_11036) {
			return true;
		} else {
			return blockState.method_11620() == Material.ICE ? false : Block.method_20045(blockState, blockView, blockPos, direction);
		}
	}

	protected void method_15725(IWorld iWorld, BlockPos blockPos, FluidState fluidState) {
		if (!fluidState.isEmpty()) {
			BlockState blockState = iWorld.method_8320(blockPos);
			BlockPos blockPos2 = blockPos.down();
			BlockState blockState2 = iWorld.method_8320(blockPos2);
			FluidState fluidState2 = this.method_15727(iWorld, blockPos2, blockState2);
			if (this.method_15738(iWorld, blockPos, blockState, Direction.field_11033, blockPos2, blockState2, iWorld.method_8316(blockPos2), fluidState2.getFluid())) {
				this.method_15745(iWorld, blockPos2, blockState2, Direction.field_11033, fluidState2);
				if (this.method_15740(iWorld, blockPos) >= 3) {
					this.method_15744(iWorld, blockPos, fluidState, blockState);
				}
			} else if (fluidState.isStill() || !this.method_15736(iWorld, fluidState2.getFluid(), blockPos, blockState, blockPos2, blockState2)) {
				this.method_15744(iWorld, blockPos, fluidState, blockState);
			}
		}
	}

	private void method_15744(IWorld iWorld, BlockPos blockPos, FluidState fluidState, BlockState blockState) {
		int i = fluidState.getLevel() - this.getLevelDecreasePerBlock(iWorld);
		if ((Boolean)fluidState.method_11654(FALLING)) {
			i = 7;
		}

		if (i > 0) {
			Map<Direction, FluidState> map = this.method_15726(iWorld, blockPos, blockState);

			for (Entry<Direction, FluidState> entry : map.entrySet()) {
				Direction direction = (Direction)entry.getKey();
				FluidState fluidState2 = (FluidState)entry.getValue();
				BlockPos blockPos2 = blockPos.offset(direction);
				BlockState blockState2 = iWorld.method_8320(blockPos2);
				if (this.method_15738(iWorld, blockPos, blockState, direction, blockPos2, blockState2, iWorld.method_8316(blockPos2), fluidState2.getFluid())) {
					this.method_15745(iWorld, blockPos2, blockState2, direction, fluidState2);
				}
			}
		}
	}

	protected FluidState method_15727(ViewableWorld viewableWorld, BlockPos blockPos, BlockState blockState) {
		int i = 0;
		int j = 0;

		for (Direction direction : Direction.Type.field_11062) {
			BlockPos blockPos2 = blockPos.offset(direction);
			BlockState blockState2 = viewableWorld.method_8320(blockPos2);
			FluidState fluidState = blockState2.method_11618();
			if (fluidState.getFluid().matchesType(this) && this.receivesFlow(direction, viewableWorld, blockPos, blockState, blockPos2, blockState2)) {
				if (fluidState.isStill()) {
					j++;
				}

				i = Math.max(i, fluidState.getLevel());
			}
		}

		if (this.isInfinite() && j >= 2) {
			BlockState blockState3 = viewableWorld.method_8320(blockPos.down());
			FluidState fluidState2 = blockState3.method_11618();
			if (blockState3.method_11620().isSolid() || this.method_15752(fluidState2)) {
				return this.method_15729(false);
			}
		}

		BlockPos blockPos3 = blockPos.up();
		BlockState blockState4 = viewableWorld.method_8320(blockPos3);
		FluidState fluidState3 = blockState4.method_11618();
		if (!fluidState3.isEmpty()
			&& fluidState3.getFluid().matchesType(this)
			&& this.receivesFlow(Direction.field_11036, viewableWorld, blockPos, blockState, blockPos3, blockState4)) {
			return this.method_15728(8, true);
		} else {
			int k = i - this.getLevelDecreasePerBlock(viewableWorld);
			return k <= 0 ? Fluids.field_15906.method_15785() : this.method_15728(k, false);
		}
	}

	private boolean receivesFlow(Direction direction, BlockView blockView, BlockPos blockPos, BlockState blockState, BlockPos blockPos2, BlockState blockState2) {
		Object2ByteLinkedOpenHashMap<Block.NeighborGroup> object2ByteLinkedOpenHashMap;
		if (!blockState.getBlock().hasDynamicBounds() && !blockState2.getBlock().hasDynamicBounds()) {
			object2ByteLinkedOpenHashMap = (Object2ByteLinkedOpenHashMap<Block.NeighborGroup>)field_15901.get();
		} else {
			object2ByteLinkedOpenHashMap = null;
		}

		Block.NeighborGroup neighborGroup;
		if (object2ByteLinkedOpenHashMap != null) {
			neighborGroup = new Block.NeighborGroup(blockState, blockState2, direction);
			byte b = object2ByteLinkedOpenHashMap.getAndMoveToFirst(neighborGroup);
			if (b != 127) {
				return b != 0;
			}
		} else {
			neighborGroup = null;
		}

		VoxelShape voxelShape = blockState.method_11628(blockView, blockPos);
		VoxelShape voxelShape2 = blockState2.method_11628(blockView, blockPos2);
		boolean bl = !VoxelShapes.method_1080(voxelShape, voxelShape2, direction);
		if (object2ByteLinkedOpenHashMap != null) {
			if (object2ByteLinkedOpenHashMap.size() == 200) {
				object2ByteLinkedOpenHashMap.removeLastByte();
			}

			object2ByteLinkedOpenHashMap.putAndMoveToFirst(neighborGroup, (byte)(bl ? 1 : 0));
		}

		return bl;
	}

	public abstract Fluid method_15750();

	public FluidState method_15728(int i, boolean bl) {
		return this.method_15750().method_15785().method_11657(LEVEL, Integer.valueOf(i)).method_11657(FALLING, Boolean.valueOf(bl));
	}

	public abstract Fluid method_15751();

	public FluidState method_15729(boolean bl) {
		return this.method_15751().method_15785().method_11657(FALLING, Boolean.valueOf(bl));
	}

	protected abstract boolean isInfinite();

	protected void method_15745(IWorld iWorld, BlockPos blockPos, BlockState blockState, Direction direction, FluidState fluidState) {
		if (blockState.getBlock() instanceof FluidFillable) {
			((FluidFillable)blockState.getBlock()).method_10311(iWorld, blockPos, blockState, fluidState);
		} else {
			if (!blockState.isAir()) {
				this.beforeBreakingBlock(iWorld, blockPos, blockState);
			}

			iWorld.method_8652(blockPos, fluidState.getBlockState(), 3);
		}
	}

	protected abstract void beforeBreakingBlock(IWorld iWorld, BlockPos blockPos, BlockState blockState);

	private static short method_15747(BlockPos blockPos, BlockPos blockPos2) {
		int i = blockPos2.getX() - blockPos.getX();
		int j = blockPos2.getZ() - blockPos.getZ();
		return (short)((i + 128 & 0xFF) << 8 | j + 128 & 0xFF);
	}

	protected int method_15742(
		ViewableWorld viewableWorld,
		BlockPos blockPos,
		int i,
		Direction direction,
		BlockState blockState,
		BlockPos blockPos2,
		Short2ObjectMap<Pair<BlockState, FluidState>> short2ObjectMap,
		Short2BooleanMap short2BooleanMap
	) {
		int j = 1000;

		for (Direction direction2 : Direction.Type.field_11062) {
			if (direction2 != direction) {
				BlockPos blockPos3 = blockPos.offset(direction2);
				short s = method_15747(blockPos2, blockPos3);
				Pair<BlockState, FluidState> pair = short2ObjectMap.computeIfAbsent(s, ix -> {
					BlockState blockStatex = viewableWorld.method_8320(blockPos3);
					return Pair.of(blockStatex, blockStatex.method_11618());
				});
				BlockState blockState2 = pair.getFirst();
				FluidState fluidState = pair.getSecond();
				if (this.method_15746(viewableWorld, this.method_15750(), blockPos, blockState, direction2, blockPos3, blockState2, fluidState)) {
					boolean bl = short2BooleanMap.computeIfAbsent(s, ix -> {
						BlockPos blockPos2x = blockPos3.down();
						BlockState blockState2x = viewableWorld.method_8320(blockPos2x);
						return this.method_15736(viewableWorld, this.method_15750(), blockPos3, blockState2, blockPos2x, blockState2x);
					});
					if (bl) {
						return i;
					}

					if (i < this.method_15733(viewableWorld)) {
						int k = this.method_15742(viewableWorld, blockPos3, i + 1, direction2.getOpposite(), blockState2, blockPos2, short2ObjectMap, short2BooleanMap);
						if (k < j) {
							j = k;
						}
					}
				}
			}
		}

		return j;
	}

	private boolean method_15736(BlockView blockView, Fluid fluid, BlockPos blockPos, BlockState blockState, BlockPos blockPos2, BlockState blockState2) {
		if (!this.receivesFlow(Direction.field_11033, blockView, blockPos, blockState, blockPos2, blockState2)) {
			return false;
		} else {
			return blockState2.method_11618().getFluid().matchesType(this) ? true : this.method_15754(blockView, blockPos2, blockState2, fluid);
		}
	}

	private boolean method_15746(
		BlockView blockView,
		Fluid fluid,
		BlockPos blockPos,
		BlockState blockState,
		Direction direction,
		BlockPos blockPos2,
		BlockState blockState2,
		FluidState fluidState
	) {
		return !this.method_15752(fluidState)
			&& this.receivesFlow(direction, blockView, blockPos, blockState, blockPos2, blockState2)
			&& this.method_15754(blockView, blockPos2, blockState2, fluid);
	}

	private boolean method_15752(FluidState fluidState) {
		return fluidState.getFluid().matchesType(this) && fluidState.isStill();
	}

	protected abstract int method_15733(ViewableWorld viewableWorld);

	private int method_15740(ViewableWorld viewableWorld, BlockPos blockPos) {
		int i = 0;

		for (Direction direction : Direction.Type.field_11062) {
			BlockPos blockPos2 = blockPos.offset(direction);
			FluidState fluidState = viewableWorld.method_8316(blockPos2);
			if (this.method_15752(fluidState)) {
				i++;
			}
		}

		return i;
	}

	protected Map<Direction, FluidState> method_15726(ViewableWorld viewableWorld, BlockPos blockPos, BlockState blockState) {
		int i = 1000;
		Map<Direction, FluidState> map = Maps.newEnumMap(Direction.class);
		Short2ObjectMap<Pair<BlockState, FluidState>> short2ObjectMap = new Short2ObjectOpenHashMap<>();
		Short2BooleanMap short2BooleanMap = new Short2BooleanOpenHashMap();

		for (Direction direction : Direction.Type.field_11062) {
			BlockPos blockPos2 = blockPos.offset(direction);
			short s = method_15747(blockPos, blockPos2);
			Pair<BlockState, FluidState> pair = short2ObjectMap.computeIfAbsent(s, ix -> {
				BlockState blockStatex = viewableWorld.method_8320(blockPos2);
				return Pair.of(blockStatex, blockStatex.method_11618());
			});
			BlockState blockState2 = pair.getFirst();
			FluidState fluidState = pair.getSecond();
			FluidState fluidState2 = this.method_15727(viewableWorld, blockPos2, blockState2);
			if (this.method_15746(viewableWorld, fluidState2.getFluid(), blockPos, blockState, direction, blockPos2, blockState2, fluidState)) {
				BlockPos blockPos3 = blockPos2.down();
				boolean bl = short2BooleanMap.computeIfAbsent(s, ix -> {
					BlockState blockState2x = viewableWorld.method_8320(blockPos3);
					return this.method_15736(viewableWorld, this.method_15750(), blockPos2, blockState2, blockPos3, blockState2x);
				});
				int j;
				if (bl) {
					j = 0;
				} else {
					j = this.method_15742(viewableWorld, blockPos2, 1, direction.getOpposite(), blockState2, blockPos, short2ObjectMap, short2BooleanMap);
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

	private boolean method_15754(BlockView blockView, BlockPos blockPos, BlockState blockState, Fluid fluid) {
		Block block = blockState.getBlock();
		if (block instanceof FluidFillable) {
			return ((FluidFillable)block).method_10310(blockView, blockPos, blockState, fluid);
		} else if (!(block instanceof DoorBlock)
			&& !block.matches(BlockTags.field_15500)
			&& block != Blocks.field_9983
			&& block != Blocks.field_10424
			&& block != Blocks.field_10422) {
			Material material = blockState.method_11620();
			return material != Material.PORTAL && material != Material.STRUCTURE_VOID && material != Material.UNDERWATER_PLANT && material != Material.SEAGRASS
				? !material.blocksMovement()
				: false;
		} else {
			return false;
		}
	}

	protected boolean method_15738(
		BlockView blockView,
		BlockPos blockPos,
		BlockState blockState,
		Direction direction,
		BlockPos blockPos2,
		BlockState blockState2,
		FluidState fluidState,
		Fluid fluid
	) {
		return fluidState.method_15764(blockView, blockPos2, fluid, direction)
			&& this.receivesFlow(direction, blockView, blockPos, blockState, blockPos2, blockState2)
			&& this.method_15754(blockView, blockPos2, blockState2, fluid);
	}

	protected abstract int getLevelDecreasePerBlock(ViewableWorld viewableWorld);

	protected int method_15753(World world, BlockPos blockPos, FluidState fluidState, FluidState fluidState2) {
		return this.getTickRate(world);
	}

	@Override
	public void method_15778(World world, BlockPos blockPos, FluidState fluidState) {
		if (!fluidState.isStill()) {
			FluidState fluidState2 = this.method_15727(world, blockPos, world.method_8320(blockPos));
			int i = this.method_15753(world, blockPos, fluidState, fluidState2);
			if (fluidState2.isEmpty()) {
				fluidState = fluidState2;
				world.method_8652(blockPos, Blocks.field_10124.method_9564(), 3);
			} else if (!fluidState2.equals(fluidState)) {
				fluidState = fluidState2;
				BlockState blockState = fluidState2.getBlockState();
				world.method_8652(blockPos, blockState, 2);
				world.method_8405().schedule(blockPos, fluidState2.getFluid(), i);
				world.method_8452(blockPos, blockState.getBlock());
			}
		}

		this.method_15725(world, blockPos, fluidState);
	}

	protected static int method_15741(FluidState fluidState) {
		return fluidState.isStill() ? 0 : 8 - Math.min(fluidState.getLevel(), 8) + (fluidState.method_11654(FALLING) ? 8 : 0);
	}

	private static boolean method_17774(FluidState fluidState, BlockView blockView, BlockPos blockPos) {
		return fluidState.getFluid().matchesType(blockView.method_8316(blockPos.up()).getFluid());
	}

	@Override
	public float method_15788(FluidState fluidState, BlockView blockView, BlockPos blockPos) {
		return method_17774(fluidState, blockView, blockPos) ? 1.0F : fluidState.method_20785();
	}

	@Override
	public float method_20784(FluidState fluidState) {
		return (float)fluidState.getLevel() / 9.0F;
	}

	@Override
	public VoxelShape method_17775(FluidState fluidState, BlockView blockView, BlockPos blockPos) {
		return fluidState.getLevel() == 9 && method_17774(fluidState, blockView, blockPos)
			? VoxelShapes.method_1077()
			: (VoxelShape)this.shapeCache
				.computeIfAbsent(fluidState, fluidStatex -> VoxelShapes.method_1081(0.0, 0.0, 0.0, 1.0, (double)fluidStatex.getHeight(blockView, blockPos), 1.0));
	}
}
