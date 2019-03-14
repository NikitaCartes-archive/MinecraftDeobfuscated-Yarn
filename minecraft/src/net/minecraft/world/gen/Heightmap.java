package net.minecraft.world.gen;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.util.PackedIntegerArray;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

public class Heightmap {
	private static final Predicate<BlockState> ALWAYS_TRUE = blockState -> true;
	private static final Predicate<BlockState> SUFFOCATES = blockState -> blockState.getMaterial().suffocates();
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
						objectList.add(chunk.getHeightmap(type));
					}

					for (int m = j - 1; m >= 0; m--) {
						pooledMutable.method_10113(k, m, l);
						BlockState blockState = chunk.getBlockState(pooledMutable);
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
					if (this.blockPredicate.test(this.chunk.getBlockState(mutable))) {
						this.set(i, k, m + 1);
						break;
					}

					if (m == 0) {
						this.set(i, k, 0);
					}
				}

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

	public static enum Type {
		WORLD_SURFACE_WG("WORLD_SURFACE_WG", Heightmap.class_2904.field_13207, Heightmap.ALWAYS_TRUE),
		WORLD_SURFACE("WORLD_SURFACE", Heightmap.class_2904.field_13206, Heightmap.ALWAYS_TRUE),
		OCEAN_FLOOR_WG("OCEAN_FLOOR_WG", Heightmap.class_2904.field_13207, Heightmap.SUFFOCATES),
		OCEAN_FLOOR("OCEAN_FLOOR", Heightmap.class_2904.field_13206, Heightmap.SUFFOCATES),
		MOTION_BLOCKING(
			"MOTION_BLOCKING", Heightmap.class_2904.field_16424, blockState -> blockState.getMaterial().suffocates() || !blockState.getFluidState().isEmpty()
		),
		MOTION_BLOCKING_NO_LEAVES(
			"MOTION_BLOCKING_NO_LEAVES",
			Heightmap.class_2904.field_13206,
			blockState -> (blockState.getMaterial().suffocates() || !blockState.getFluidState().isEmpty()) && !(blockState.getBlock() instanceof LeavesBlock)
		);

		private final String name;
		private final Heightmap.class_2904 field_13198;
		private final Predicate<BlockState> blockPredicate;
		private static final Map<String, Heightmap.Type> BY_NAME = SystemUtil.consume(Maps.<String, Heightmap.Type>newHashMap(), hashMap -> {
			for (Heightmap.Type type : values()) {
				hashMap.put(type.name, type);
			}
		});

		private Type(String string2, Heightmap.class_2904 arg, Predicate<BlockState> predicate) {
			this.name = string2;
			this.field_13198 = arg;
			this.blockPredicate = predicate;
		}

		public String getName() {
			return this.name;
		}

		public boolean method_16136() {
			return this.field_13198 != Heightmap.class_2904.field_13207;
		}

		public boolean method_16137() {
			return this.field_13198 == Heightmap.class_2904.field_16424;
		}

		public static Heightmap.Type byName(String string) {
			return (Heightmap.Type)BY_NAME.get(string);
		}

		public Predicate<BlockState> getBlockPredicate() {
			return this.blockPredicate;
		}
	}

	public static enum class_2904 {
		field_13207,
		field_13206,
		field_16424;
	}
}
