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
import net.minecraft.block.StairsBlock;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntegerProperty;
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
	public static final BooleanProperty STILL = Properties.FALLING;
	public static final IntegerProperty LEVEL = Properties.BLOCK_LEVEL;
	private static final ThreadLocal<Object2ByteLinkedOpenHashMap<Block.NeighborGroup>> field_15901 = ThreadLocal.withInitial(() -> {
		Object2ByteLinkedOpenHashMap<Block.NeighborGroup> object2ByteLinkedOpenHashMap = new Object2ByteLinkedOpenHashMap<Block.NeighborGroup>(200) {
			@Override
			protected void rehash(int i) {
			}
		};
		object2ByteLinkedOpenHashMap.defaultReturnValue((byte)127);
		return object2ByteLinkedOpenHashMap;
	});

	@Override
	protected void appendProperties(StateFactory.Builder<Fluid, FluidState> builder) {
		builder.with(STILL);
	}

	@Override
	public Vec3d method_15782(BlockView blockView, BlockPos blockPos, FluidState fluidState) {
		double d = 0.0;
		double e = 0.0;

		Vec3d var27;
		try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
			for (Direction direction : Direction.class_2353.HORIZONTAL) {
				pooledMutable.set(blockPos).setOffset(direction);
				FluidState fluidState2 = blockView.getFluidState(pooledMutable);
				if (this.method_15748(fluidState2)) {
					float f = fluidState2.method_15763();
					float g = 0.0F;
					if (f == 0.0F) {
						if (!blockView.getBlockState(pooledMutable).getMaterial().suffocates()) {
							FluidState fluidState3 = blockView.getFluidState(pooledMutable.down());
							if (this.method_15748(fluidState3)) {
								f = fluidState3.method_15763();
								if (f > 0.0F) {
									g = fluidState.method_15763() - (f - 0.8888889F);
								}
							}
						}
					} else if (f > 0.0F) {
						g = fluidState.method_15763() - f;
					}

					if (g != 0.0F) {
						d += (double)((float)direction.getOffsetX() * g);
						e += (double)((float)direction.getOffsetZ() * g);
					}
				}
			}

			Vec3d vec3d = new Vec3d(d, 0.0, e);
			if ((Boolean)fluidState.get(STILL)) {
				for (Direction direction2 : Direction.class_2353.HORIZONTAL) {
					pooledMutable.set(blockPos).setOffset(direction2);
					if (this.method_15749(blockView, pooledMutable, direction2) || this.method_15749(blockView, pooledMutable.up(), direction2)) {
						vec3d = vec3d.normalize().add(0.0, -6.0, 0.0);
						break;
					}
				}
			}

			var27 = vec3d.normalize();
		}

		return var27;
	}

	private boolean method_15748(FluidState fluidState) {
		return fluidState.isEmpty() || fluidState.getFluid().matchesType(this);
	}

	protected boolean method_15749(BlockView blockView, BlockPos blockPos, Direction direction) {
		BlockState blockState = blockView.getBlockState(blockPos);
		Block block = blockState.getBlock();
		FluidState fluidState = blockView.getFluidState(blockPos);
		if (fluidState.getFluid().matchesType(this)) {
			return false;
		} else if (direction == Direction.UP) {
			return true;
		} else if (blockState.getMaterial() == Material.ICE) {
			return false;
		} else {
			boolean bl = Block.method_9581(block) || block instanceof StairsBlock;
			return !bl && Block.isFaceFullCube(blockState.getCollisionShape(blockView, blockPos), direction);
		}
	}

	protected void method_15725(IWorld iWorld, BlockPos blockPos, FluidState fluidState) {
		if (!fluidState.isEmpty()) {
			BlockState blockState = iWorld.getBlockState(blockPos);
			BlockPos blockPos2 = blockPos.down();
			BlockState blockState2 = iWorld.getBlockState(blockPos2);
			FluidState fluidState2 = this.method_15727(iWorld, blockPos2, blockState2);
			if (this.method_15738(iWorld, blockPos, blockState, Direction.DOWN, blockPos2, blockState2, iWorld.getFluidState(blockPos2), fluidState2.getFluid())) {
				this.method_15745(iWorld, blockPos2, blockState2, Direction.DOWN, fluidState2);
				if (this.method_15740(iWorld, blockPos) >= 3) {
					this.method_15744(iWorld, blockPos, fluidState, blockState);
				}
			} else if (fluidState.isStill() || !this.method_15736(iWorld, fluidState2.getFluid(), blockPos, blockState, blockPos2, blockState2)) {
				this.method_15744(iWorld, blockPos, fluidState, blockState);
			}
		}
	}

	private void method_15744(IWorld iWorld, BlockPos blockPos, FluidState fluidState, BlockState blockState) {
		int i = fluidState.method_15761() - this.method_15739(iWorld);
		if ((Boolean)fluidState.get(STILL)) {
			i = 7;
		}

		if (i > 0) {
			Map<Direction, FluidState> map = this.method_15726(iWorld, blockPos, blockState);

			for (Entry<Direction, FluidState> entry : map.entrySet()) {
				Direction direction = (Direction)entry.getKey();
				FluidState fluidState2 = (FluidState)entry.getValue();
				BlockPos blockPos2 = blockPos.offset(direction);
				BlockState blockState2 = iWorld.getBlockState(blockPos2);
				if (this.method_15738(iWorld, blockPos, blockState, direction, blockPos2, blockState2, iWorld.getFluidState(blockPos2), fluidState2.getFluid())) {
					this.method_15745(iWorld, blockPos2, blockState2, direction, fluidState2);
				}
			}
		}
	}

	protected FluidState method_15727(ViewableWorld viewableWorld, BlockPos blockPos, BlockState blockState) {
		int i = 0;
		int j = 0;

		for (Direction direction : Direction.class_2353.HORIZONTAL) {
			BlockPos blockPos2 = blockPos.offset(direction);
			BlockState blockState2 = viewableWorld.getBlockState(blockPos2);
			FluidState fluidState = blockState2.getFluidState();
			if (fluidState.getFluid().matchesType(this) && this.method_15732(direction, viewableWorld, blockPos, blockState, blockPos2, blockState2)) {
				if (fluidState.isStill()) {
					j++;
				}

				i = Math.max(i, fluidState.method_15761());
			}
		}

		if (this.method_15737() && j >= 2) {
			BlockState blockState3 = viewableWorld.getBlockState(blockPos.down());
			FluidState fluidState2 = blockState3.getFluidState();
			if (blockState3.getMaterial().method_15799() || this.method_15752(fluidState2)) {
				return this.getState(false);
			}
		}

		BlockPos blockPos3 = blockPos.up();
		BlockState blockState4 = viewableWorld.getBlockState(blockPos3);
		FluidState fluidState3 = blockState4.getFluidState();
		if (!fluidState3.isEmpty()
			&& fluidState3.getFluid().matchesType(this)
			&& this.method_15732(Direction.UP, viewableWorld, blockPos, blockState, blockPos3, blockState4)) {
			return this.method_15728(8, true);
		} else {
			int k = i - this.method_15739(viewableWorld);
			return k <= 0 ? Fluids.EMPTY.getDefaultState() : this.method_15728(k, false);
		}
	}

	private boolean method_15732(Direction direction, BlockView blockView, BlockPos blockPos, BlockState blockState, BlockPos blockPos2, BlockState blockState2) {
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

		VoxelShape voxelShape = blockState.getCollisionShape(blockView, blockPos);
		VoxelShape voxelShape2 = blockState2.getCollisionShape(blockView, blockPos2);
		boolean bl = !VoxelShapes.method_1080(voxelShape, voxelShape2, direction);
		if (object2ByteLinkedOpenHashMap != null) {
			if (object2ByteLinkedOpenHashMap.size() == 200) {
				object2ByteLinkedOpenHashMap.removeLastByte();
			}

			object2ByteLinkedOpenHashMap.putAndMoveToFirst(neighborGroup, (byte)(bl ? 1 : 0));
		}

		return bl;
	}

	public abstract Fluid getFlowing();

	public FluidState method_15728(int i, boolean bl) {
		return this.getFlowing().getDefaultState().with(LEVEL, Integer.valueOf(i)).with(STILL, Boolean.valueOf(bl));
	}

	public abstract Fluid getStill();

	public FluidState getState(boolean bl) {
		return this.getStill().getDefaultState().with(STILL, Boolean.valueOf(bl));
	}

	protected abstract boolean method_15737();

	protected void method_15745(IWorld iWorld, BlockPos blockPos, BlockState blockState, Direction direction, FluidState fluidState) {
		if (blockState.getBlock() instanceof FluidFillable) {
			((FluidFillable)blockState.getBlock()).tryFillWithFluid(iWorld, blockPos, blockState, fluidState);
		} else {
			if (!blockState.isAir()) {
				this.method_15730(iWorld, blockPos, blockState);
			}

			iWorld.setBlockState(blockPos, fluidState.getBlockState(), 3);
		}
	}

	protected abstract void method_15730(IWorld iWorld, BlockPos blockPos, BlockState blockState);

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

		for (Direction direction2 : Direction.class_2353.HORIZONTAL) {
			if (direction2 != direction) {
				BlockPos blockPos3 = blockPos.offset(direction2);
				short s = method_15747(blockPos2, blockPos3);
				Pair<BlockState, FluidState> pair = short2ObjectMap.computeIfAbsent(s, ix -> {
					BlockState blockStatex = viewableWorld.getBlockState(blockPos3);
					return Pair.of(blockStatex, blockStatex.getFluidState());
				});
				BlockState blockState2 = pair.getFirst();
				FluidState fluidState = pair.getSecond();
				if (this.method_15746(viewableWorld, this.getFlowing(), blockPos, blockState, direction2, blockPos3, blockState2, fluidState)) {
					boolean bl = short2BooleanMap.computeIfAbsent(s, ix -> {
						BlockPos blockPos2x = blockPos3.down();
						BlockState blockState2x = viewableWorld.getBlockState(blockPos2x);
						return this.method_15736(viewableWorld, this.getFlowing(), blockPos3, blockState2, blockPos2x, blockState2x);
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
		if (!this.method_15732(Direction.DOWN, blockView, blockPos, blockState, blockPos2, blockState2)) {
			return false;
		} else {
			return blockState2.getFluidState().getFluid().matchesType(this) ? true : this.method_15754(blockView, blockPos2, blockState2, fluid);
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
			&& this.method_15732(direction, blockView, blockPos, blockState, blockPos2, blockState2)
			&& this.method_15754(blockView, blockPos2, blockState2, fluid);
	}

	private boolean method_15752(FluidState fluidState) {
		return fluidState.getFluid().matchesType(this) && fluidState.isStill();
	}

	protected abstract int method_15733(ViewableWorld viewableWorld);

	private int method_15740(ViewableWorld viewableWorld, BlockPos blockPos) {
		int i = 0;

		for (Direction direction : Direction.class_2353.HORIZONTAL) {
			BlockPos blockPos2 = blockPos.offset(direction);
			FluidState fluidState = viewableWorld.getFluidState(blockPos2);
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

		for (Direction direction : Direction.class_2353.HORIZONTAL) {
			BlockPos blockPos2 = blockPos.offset(direction);
			short s = method_15747(blockPos, blockPos2);
			Pair<BlockState, FluidState> pair = short2ObjectMap.computeIfAbsent(s, ix -> {
				BlockState blockStatex = viewableWorld.getBlockState(blockPos2);
				return Pair.of(blockStatex, blockStatex.getFluidState());
			});
			BlockState blockState2 = pair.getFirst();
			FluidState fluidState = pair.getSecond();
			FluidState fluidState2 = this.method_15727(viewableWorld, blockPos2, blockState2);
			if (this.method_15746(viewableWorld, fluidState2.getFluid(), blockPos, blockState, direction, blockPos2, blockState2, fluidState)) {
				BlockPos blockPos3 = blockPos2.down();
				boolean bl = short2BooleanMap.computeIfAbsent(s, ix -> {
					BlockState blockState2x = viewableWorld.getBlockState(blockPos3);
					return this.method_15736(viewableWorld, this.getFlowing(), blockPos2, blockState2, blockPos3, blockState2x);
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
			return ((FluidFillable)block).canFillWithFluid(blockView, blockPos, blockState, fluid);
		} else if (!(block instanceof DoorBlock)
			&& !block.matches(BlockTags.field_15500)
			&& block != Blocks.field_9983
			&& block != Blocks.field_10424
			&& block != Blocks.field_10422) {
			Material material = blockState.getMaterial();
			return material != Material.PORTAL && material != Material.STRUCTURE_VOID && material != Material.UNDERWATER_PLANT && material != Material.SEAGRASS
				? !material.suffocates()
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
		return fluidState.method_15764(fluid, direction)
			&& this.method_15732(direction, blockView, blockPos, blockState, blockPos2, blockState2)
			&& this.method_15754(blockView, blockPos2, blockState2, fluid);
	}

	protected abstract int method_15739(ViewableWorld viewableWorld);

	protected int method_15753(World world, FluidState fluidState, FluidState fluidState2) {
		return this.method_15789(world);
	}

	@Override
	public void method_15778(World world, BlockPos blockPos, FluidState fluidState) {
		if (!fluidState.isStill()) {
			FluidState fluidState2 = this.method_15727(world, blockPos, world.getBlockState(blockPos));
			int i = this.method_15753(world, fluidState, fluidState2);
			if (fluidState2.isEmpty()) {
				fluidState = fluidState2;
				world.setBlockState(blockPos, Blocks.field_10124.getDefaultState(), 3);
			} else if (!fluidState2.equals(fluidState)) {
				fluidState = fluidState2;
				BlockState blockState = fluidState2.getBlockState();
				world.setBlockState(blockPos, blockState, 2);
				world.getFluidTickScheduler().schedule(blockPos, fluidState2.getFluid(), i);
				world.updateNeighborsAlways(blockPos, blockState.getBlock());
			}
		}

		this.method_15725(world, blockPos, fluidState);
	}

	protected static int method_15741(FluidState fluidState) {
		return fluidState.isStill() ? 0 : 8 - Math.min(fluidState.method_15761(), 8) + (fluidState.get(STILL) ? 8 : 0);
	}

	@Override
	public float method_15788(FluidState fluidState) {
		return (float)fluidState.method_15761() / 9.0F;
	}
}
