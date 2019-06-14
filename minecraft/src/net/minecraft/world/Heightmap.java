package net.minecraft.world;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.util.PackedIntegerArray;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

public class Heightmap {
	private static final Predicate<BlockState> ALWAYS_TRUE = blockState -> !blockState.isAir();
	private static final Predicate<BlockState> SUFFOCATES = blockState -> blockState.method_11620().blocksMovement();
	private final PackedIntegerArray storage = new PackedIntegerArray(9, 256);
	private final Predicate<BlockState> blockPredicate;
	private final Chunk chunk;

	public Heightmap(Chunk chunk, Heightmap.Type type) {
		this.blockPredicate = type.getBlockPredicate();
		this.chunk = chunk;
	}

	public static void populateHeightmaps(Chunk chunk, Set<Heightmap.Type> set) {
		int i = set.size();
		ObjectList<Heightmap> objectList = new ObjectArrayList<>(i);
		ObjectListIterator<Heightmap> objectListIterator = objectList.iterator();
		int j = chunk.getHighestNonEmptySectionYOffset() + 16;

		try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
			for (int k = 0; k < 16; k++) {
				for (int l = 0; l < 16; l++) {
					for (Heightmap.Type type : set) {
						objectList.add(chunk.method_12032(type));
					}

					for (int m = j - 1; m >= 0; m--) {
						pooledMutable.method_10113(k, m, l);
						BlockState blockState = chunk.method_8320(pooledMutable);
						if (blockState.getBlock() != Blocks.field_10124) {
							while (objectListIterator.hasNext()) {
								Heightmap heightmap = (Heightmap)objectListIterator.next();
								if (heightmap.blockPredicate.test(blockState)) {
									heightmap.set(k, l, m + 1);
									objectListIterator.remove();
								}
							}

							if (objectList.isEmpty()) {
								break;
							}

							objectListIterator.back(i);
						}
					}
				}
			}
		}
	}

	public boolean trackUpdate(int i, int j, int k, BlockState blockState) {
		int l = this.get(i, k);
		if (j <= l - 2) {
			return false;
		} else {
			if (this.blockPredicate.test(blockState)) {
				if (j >= l) {
					this.set(i, k, j + 1);
					return true;
				}
			} else if (l - 1 == j) {
				BlockPos.Mutable mutable = new BlockPos.Mutable();

				for (int m = j - 1; m >= 0; m--) {
					mutable.set(i, m, k);
					if (this.blockPredicate.test(this.chunk.method_8320(mutable))) {
						this.set(i, k, m + 1);
						return true;
					}
				}

				this.set(i, k, 0);
				return true;
			}

			return false;
		}
	}

	public int get(int i, int j) {
		return this.get(toIndex(i, j));
	}

	private int get(int i) {
		return this.storage.get(i);
	}

	private void set(int i, int j, int k) {
		this.storage.set(toIndex(i, j), k);
	}

	public void setTo(long[] ls) {
		System.arraycopy(ls, 0, this.storage.getStorage(), 0, ls.length);
	}

	public long[] asLongArray() {
		return this.storage.getStorage();
	}

	private static int toIndex(int i, int j) {
		return i + j * 16;
	}

	public static enum Purpose {
		field_13207,
		field_13206,
		field_16424;
	}

	public static enum Type {
		field_13194("WORLD_SURFACE_WG", Heightmap.Purpose.field_13207, Heightmap.ALWAYS_TRUE),
		field_13202("WORLD_SURFACE", Heightmap.Purpose.field_16424, Heightmap.ALWAYS_TRUE),
		field_13195("OCEAN_FLOOR_WG", Heightmap.Purpose.field_13207, Heightmap.SUFFOCATES),
		field_13200("OCEAN_FLOOR", Heightmap.Purpose.field_13206, Heightmap.SUFFOCATES),
		field_13197(
			"MOTION_BLOCKING", Heightmap.Purpose.field_16424, blockState -> blockState.method_11620().blocksMovement() || !blockState.method_11618().isEmpty()
		),
		field_13203(
			"MOTION_BLOCKING_NO_LEAVES",
			Heightmap.Purpose.field_13206,
			blockState -> (blockState.method_11620().blocksMovement() || !blockState.method_11618().isEmpty()) && !(blockState.getBlock() instanceof LeavesBlock)
		);

		private final String name;
		private final Heightmap.Purpose purpose;
		private final Predicate<BlockState> blockPredicate;
		private static final Map<String, Heightmap.Type> BY_NAME = SystemUtil.consume(Maps.<String, Heightmap.Type>newHashMap(), hashMap -> {
			for (Heightmap.Type type : values()) {
				hashMap.put(type.name, type);
			}
		});

		private Type(String string2, Heightmap.Purpose purpose, Predicate<BlockState> predicate) {
			this.name = string2;
			this.purpose = purpose;
			this.blockPredicate = predicate;
		}

		public String getName() {
			return this.name;
		}

		public boolean shouldSendToClient() {
			return this.purpose == Heightmap.Purpose.field_16424;
		}

		@Environment(EnvType.CLIENT)
		public boolean isStoredServerSide() {
			return this.purpose != Heightmap.Purpose.field_13207;
		}

		public static Heightmap.Type byName(String string) {
			return (Heightmap.Type)BY_NAME.get(string);
		}

		public Predicate<BlockState> getBlockPredicate() {
			return this.blockPredicate;
		}
	}
}
