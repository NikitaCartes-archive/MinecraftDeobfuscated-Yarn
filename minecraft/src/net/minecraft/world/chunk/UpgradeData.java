package net.minecraft.world.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.GourdBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.property.Properties;
import net.minecraft.util.EightWayDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UpgradeData {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final UpgradeData NO_UPGRADE_DATA = new UpgradeData();
	private static final EightWayDirection[] field_12952 = EightWayDirection.values();
	private final EnumSet<EightWayDirection> sidesToUpgrade = EnumSet.noneOf(EightWayDirection.class);
	private final int[][] centerIndicesToUpgrade = new int[16][];
	private static final Map<Block, UpgradeData.class_2844> field_12953 = new IdentityHashMap();
	private static final Set<UpgradeData.class_2844> field_12954 = Sets.<UpgradeData.class_2844>newHashSet();

	private UpgradeData() {
	}

	public UpgradeData(CompoundTag tag) {
		this();
		if (tag.contains("Indices", 10)) {
			CompoundTag compoundTag = tag.getCompound("Indices");

			for (int i = 0; i < this.centerIndicesToUpgrade.length; i++) {
				String string = String.valueOf(i);
				if (compoundTag.contains(string, 11)) {
					this.centerIndicesToUpgrade[i] = compoundTag.getIntArray(string);
				}
			}
		}

		int j = tag.getInt("Sides");

		for (EightWayDirection eightWayDirection : EightWayDirection.values()) {
			if ((j & 1 << eightWayDirection.ordinal()) != 0) {
				this.sidesToUpgrade.add(eightWayDirection);
			}
		}
	}

	public void method_12356(WorldChunk worldChunk) {
		this.method_12348(worldChunk);

		for (EightWayDirection eightWayDirection : field_12952) {
			method_12352(worldChunk, eightWayDirection);
		}

		World world = worldChunk.getWorld();
		field_12954.forEach(arg -> arg.method_12357(world));
	}

	private static void method_12352(WorldChunk worldChunk, EightWayDirection eightWayDirection) {
		World world = worldChunk.getWorld();
		if (worldChunk.getUpgradeData().sidesToUpgrade.remove(eightWayDirection)) {
			Set<Direction> set = eightWayDirection.getDirections();
			int i = 0;
			int j = 15;
			boolean bl = set.contains(Direction.EAST);
			boolean bl2 = set.contains(Direction.WEST);
			boolean bl3 = set.contains(Direction.SOUTH);
			boolean bl4 = set.contains(Direction.NORTH);
			boolean bl5 = set.size() == 1;
			ChunkPos chunkPos = worldChunk.getPos();
			int k = chunkPos.getStartX() + (!bl5 || !bl4 && !bl3 ? (bl2 ? 0 : 15) : 1);
			int l = chunkPos.getStartX() + (!bl5 || !bl4 && !bl3 ? (bl2 ? 0 : 15) : 14);
			int m = chunkPos.getStartZ() + (!bl5 || !bl && !bl2 ? (bl4 ? 0 : 15) : 1);
			int n = chunkPos.getStartZ() + (!bl5 || !bl && !bl2 ? (bl4 ? 0 : 15) : 14);
			Direction[] directions = Direction.values();
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (BlockPos blockPos : BlockPos.iterate(k, 0, m, l, world.getHeight() - 1, n)) {
				BlockState blockState = world.getBlockState(blockPos);
				BlockState blockState2 = blockState;

				for (Direction direction : directions) {
					mutable.set(blockPos).setOffset(direction);
					blockState2 = method_12351(blockState2, direction, world, blockPos, mutable);
				}

				Block.replaceBlock(blockState, blockState2, world, blockPos, 18);
			}
		}
	}

	private static BlockState method_12351(BlockState blockState, Direction direction, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		return ((UpgradeData.class_2844)field_12953.getOrDefault(blockState.getBlock(), UpgradeData.class_2845.field_12962))
			.method_12358(blockState, direction, iWorld.getBlockState(blockPos2), iWorld, blockPos, blockPos2);
	}

	private void method_12348(WorldChunk worldChunk) {
		try (
			BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get();
			BlockPos.PooledMutable pooledMutable2 = BlockPos.PooledMutable.get();
		) {
			ChunkPos chunkPos = worldChunk.getPos();
			IWorld iWorld = worldChunk.getWorld();

			for (int i = 0; i < 16; i++) {
				ChunkSection chunkSection = worldChunk.getSectionArray()[i];
				int[] is = this.centerIndicesToUpgrade[i];
				this.centerIndicesToUpgrade[i] = null;
				if (chunkSection != null && is != null && is.length > 0) {
					Direction[] directions = Direction.values();
					PalettedContainer<BlockState> palettedContainer = chunkSection.getContainer();

					for (int j : is) {
						int k = j & 15;
						int l = j >> 8 & 15;
						int m = j >> 4 & 15;
						pooledMutable.set(chunkPos.getStartX() + k, (i << 4) + l, chunkPos.getStartZ() + m);
						BlockState blockState = palettedContainer.get(j);
						BlockState blockState2 = blockState;

						for (Direction direction : directions) {
							pooledMutable2.set(pooledMutable).setOffset(direction);
							if (pooledMutable.getX() >> 4 == chunkPos.x && pooledMutable.getZ() >> 4 == chunkPos.z) {
								blockState2 = method_12351(blockState2, direction, iWorld, pooledMutable, pooledMutable2);
							}
						}

						Block.replaceBlock(blockState, blockState2, iWorld, pooledMutable, 18);
					}
				}
			}

			for (int ix = 0; ix < this.centerIndicesToUpgrade.length; ix++) {
				if (this.centerIndicesToUpgrade[ix] != null) {
					LOGGER.warn("Discarding update data for section {} for chunk ({} {})", ix, chunkPos.x, chunkPos.z);
				}

				this.centerIndicesToUpgrade[ix] = null;
			}
		}
	}

	public boolean method_12349() {
		for (int[] is : this.centerIndicesToUpgrade) {
			if (is != null) {
				return false;
			}
		}

		return this.sidesToUpgrade.isEmpty();
	}

	public CompoundTag toTag() {
		CompoundTag compoundTag = new CompoundTag();
		CompoundTag compoundTag2 = new CompoundTag();

		for (int i = 0; i < this.centerIndicesToUpgrade.length; i++) {
			String string = String.valueOf(i);
			if (this.centerIndicesToUpgrade[i] != null && this.centerIndicesToUpgrade[i].length != 0) {
				compoundTag2.putIntArray(string, this.centerIndicesToUpgrade[i]);
			}
		}

		if (!compoundTag2.isEmpty()) {
			compoundTag.put("Indices", compoundTag2);
		}

		int ix = 0;

		for (EightWayDirection eightWayDirection : this.sidesToUpgrade) {
			ix |= 1 << eightWayDirection.ordinal();
		}

		compoundTag.putByte("Sides", (byte)ix);
		return compoundTag;
	}

	public interface class_2844 {
		BlockState method_12358(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2);

		default void method_12357(IWorld iWorld) {
		}
	}

	static enum class_2845 implements UpgradeData.class_2844 {
		field_12957(
			Blocks.OBSERVER,
			Blocks.NETHER_PORTAL,
			Blocks.WHITE_CONCRETE_POWDER,
			Blocks.ORANGE_CONCRETE_POWDER,
			Blocks.MAGENTA_CONCRETE_POWDER,
			Blocks.LIGHT_BLUE_CONCRETE_POWDER,
			Blocks.YELLOW_CONCRETE_POWDER,
			Blocks.LIME_CONCRETE_POWDER,
			Blocks.PINK_CONCRETE_POWDER,
			Blocks.GRAY_CONCRETE_POWDER,
			Blocks.LIGHT_GRAY_CONCRETE_POWDER,
			Blocks.CYAN_CONCRETE_POWDER,
			Blocks.PURPLE_CONCRETE_POWDER,
			Blocks.BLUE_CONCRETE_POWDER,
			Blocks.BROWN_CONCRETE_POWDER,
			Blocks.GREEN_CONCRETE_POWDER,
			Blocks.RED_CONCRETE_POWDER,
			Blocks.BLACK_CONCRETE_POWDER,
			Blocks.ANVIL,
			Blocks.CHIPPED_ANVIL,
			Blocks.DAMAGED_ANVIL,
			Blocks.DRAGON_EGG,
			Blocks.GRAVEL,
			Blocks.SAND,
			Blocks.RED_SAND,
			Blocks.OAK_SIGN,
			Blocks.SPRUCE_SIGN,
			Blocks.BIRCH_SIGN,
			Blocks.ACACIA_SIGN,
			Blocks.JUNGLE_SIGN,
			Blocks.DARK_OAK_SIGN,
			Blocks.OAK_WALL_SIGN,
			Blocks.SPRUCE_WALL_SIGN,
			Blocks.BIRCH_WALL_SIGN,
			Blocks.ACACIA_WALL_SIGN,
			Blocks.JUNGLE_WALL_SIGN,
			Blocks.DARK_OAK_WALL_SIGN
		) {
			@Override
			public BlockState method_12358(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
				return blockState;
			}
		},
		field_12962 {
			@Override
			public BlockState method_12358(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
				return blockState.getStateForNeighborUpdate(direction, iWorld.getBlockState(blockPos2), iWorld, blockPos, blockPos2);
			}
		},
		field_12960(Blocks.CHEST, Blocks.TRAPPED_CHEST) {
			@Override
			public BlockState method_12358(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
				if (blockState2.getBlock() == blockState.getBlock()
					&& direction.getAxis().isHorizontal()
					&& blockState.get(ChestBlock.CHEST_TYPE) == ChestType.SINGLE
					&& blockState2.get(ChestBlock.CHEST_TYPE) == ChestType.SINGLE) {
					Direction direction2 = blockState.get(ChestBlock.FACING);
					if (direction.getAxis() != direction2.getAxis() && direction2 == blockState2.get(ChestBlock.FACING)) {
						ChestType chestType = direction == direction2.rotateYClockwise() ? ChestType.LEFT : ChestType.RIGHT;
						iWorld.setBlockState(blockPos2, blockState2.with(ChestBlock.CHEST_TYPE, chestType.getOpposite()), 18);
						if (direction2 == Direction.NORTH || direction2 == Direction.EAST) {
							BlockEntity blockEntity = iWorld.getBlockEntity(blockPos);
							BlockEntity blockEntity2 = iWorld.getBlockEntity(blockPos2);
							if (blockEntity instanceof ChestBlockEntity && blockEntity2 instanceof ChestBlockEntity) {
								ChestBlockEntity.copyInventory((ChestBlockEntity)blockEntity, (ChestBlockEntity)blockEntity2);
							}
						}

						return blockState.with(ChestBlock.CHEST_TYPE, chestType);
					}
				}

				return blockState;
			}
		},
		field_12963(true, Blocks.ACACIA_LEAVES, Blocks.BIRCH_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES) {
			private final ThreadLocal<List<ObjectSet<BlockPos>>> field_12964 = ThreadLocal.withInitial(() -> Lists.newArrayListWithCapacity(7));

			@Override
			public BlockState method_12358(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
				BlockState blockState3 = blockState.getStateForNeighborUpdate(direction, iWorld.getBlockState(blockPos2), iWorld, blockPos, blockPos2);
				if (blockState != blockState3) {
					int i = (Integer)blockState3.get(Properties.DISTANCE_1_7);
					List<ObjectSet<BlockPos>> list = (List<ObjectSet<BlockPos>>)this.field_12964.get();
					if (list.isEmpty()) {
						for (int j = 0; j < 7; j++) {
							list.add(new ObjectOpenHashSet());
						}
					}

					((ObjectSet)list.get(i)).add(blockPos.toImmutable());
				}

				return blockState;
			}

			@Override
			public void method_12357(IWorld iWorld) {
				BlockPos.Mutable mutable = new BlockPos.Mutable();
				List<ObjectSet<BlockPos>> list = (List<ObjectSet<BlockPos>>)this.field_12964.get();

				for (int i = 2; i < list.size(); i++) {
					int j = i - 1;
					ObjectSet<BlockPos> objectSet = (ObjectSet<BlockPos>)list.get(j);
					ObjectSet<BlockPos> objectSet2 = (ObjectSet<BlockPos>)list.get(i);

					for (BlockPos blockPos : objectSet) {
						BlockState blockState = iWorld.getBlockState(blockPos);
						if ((Integer)blockState.get(Properties.DISTANCE_1_7) >= j) {
							iWorld.setBlockState(blockPos, blockState.with(Properties.DISTANCE_1_7, Integer.valueOf(j)), 18);
							if (i != 7) {
								for (Direction direction : field_12959) {
									mutable.set(blockPos).setOffset(direction);
									BlockState blockState2 = iWorld.getBlockState(mutable);
									if (blockState2.contains(Properties.DISTANCE_1_7) && (Integer)blockState.get(Properties.DISTANCE_1_7) > i) {
										objectSet2.add(mutable.toImmutable());
									}
								}
							}
						}
					}
				}

				list.clear();
			}
		},
		field_12958(Blocks.MELON_STEM, Blocks.PUMPKIN_STEM) {
			@Override
			public BlockState method_12358(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
				if ((Integer)blockState.get(StemBlock.AGE) == 7) {
					GourdBlock gourdBlock = ((StemBlock)blockState.getBlock()).getGourdBlock();
					if (blockState2.getBlock() == gourdBlock) {
						return gourdBlock.getAttachedStem().getDefaultState().with(HorizontalFacingBlock.FACING, direction);
					}
				}

				return blockState;
			}
		};

		public static final Direction[] field_12959 = Direction.values();

		private class_2845(Block... blocks) {
			this(false, blocks);
		}

		private class_2845(boolean bl, Block... blocks) {
			for (Block block : blocks) {
				UpgradeData.field_12953.put(block, this);
			}

			if (bl) {
				UpgradeData.field_12954.add(this);
			}
		}
	}
}
