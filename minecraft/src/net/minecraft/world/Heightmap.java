package net.minecraft.world;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.collection.PackedIntegerArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

public class Heightmap {
	private static final Predicate<BlockState> ALWAYS_TRUE = blockState -> !blockState.isAir();
	private static final Predicate<BlockState> SUFFOCATES = blockState -> blockState.getMaterial().blocksMovement();
	private final PackedIntegerArray storage = new PackedIntegerArray(9, 256);
	private final Predicate<BlockState> blockPredicate;
	private final Chunk chunk;

	public Heightmap(Chunk chunk, Heightmap.Type type) {
		this.blockPredicate = type.getBlockPredicate();
		this.chunk = chunk;
	}

	public static void populateHeightmaps(Chunk chunk, Set<Heightmap.Type> types) {
		int i = types.size();
		ObjectList<Heightmap> objectList = new ObjectArrayList<>(i);
		ObjectListIterator<Heightmap> objectListIterator = objectList.iterator();
		int j = chunk.getHighestNonEmptySectionYOffset() + 16;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int k = 0; k < 16; k++) {
			for (int l = 0; l < 16; l++) {
				for (Heightmap.Type type : types) {
					objectList.add(chunk.getHeightmap(type));
				}

				for (int m = j - 1; m >= 0; m--) {
					mutable.set(k, m, l);
					BlockState blockState = chunk.getBlockState(mutable);
					if (!blockState.isOf(Blocks.field_10124)) {
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

	public boolean trackUpdate(int x, int y, int z, BlockState state) {
		int i = this.get(x, z);
		if (y <= i - 2) {
			return false;
		} else {
			if (this.blockPredicate.test(state)) {
				if (y >= i) {
					this.set(x, z, y + 1);
					return true;
				}
			} else if (i - 1 == y) {
				BlockPos.Mutable mutable = new BlockPos.Mutable();

				for (int j = y - 1; j >= 0; j--) {
					mutable.set(x, j, z);
					if (this.blockPredicate.test(this.chunk.getBlockState(mutable))) {
						this.set(x, z, j + 1);
						return true;
					}
				}

				this.set(x, z, 0);
				return true;
			}

			return false;
		}
	}

	public int get(int x, int z) {
		return this.get(toIndex(x, z));
	}

	private int get(int index) {
		return this.storage.get(index);
	}

	private void set(int x, int z, int height) {
		this.storage.set(toIndex(x, z), height);
	}

	public void setTo(long[] heightmap) {
		System.arraycopy(heightmap, 0, this.storage.getStorage(), 0, heightmap.length);
	}

	public long[] asLongArray() {
		return this.storage.getStorage();
	}

	private static int toIndex(int x, int z) {
		return x + z * 16;
	}

	public static enum Purpose {
		field_13207,
		field_13206,
		field_16424;
	}

	public static enum Type implements StringIdentifiable {
		field_13194("WORLD_SURFACE_WG", Heightmap.Purpose.field_13207, Heightmap.ALWAYS_TRUE),
		field_13202("WORLD_SURFACE", Heightmap.Purpose.field_16424, Heightmap.ALWAYS_TRUE),
		field_13195("OCEAN_FLOOR_WG", Heightmap.Purpose.field_13207, Heightmap.SUFFOCATES),
		field_13200("OCEAN_FLOOR", Heightmap.Purpose.field_13206, Heightmap.SUFFOCATES),
		field_13197(
			"MOTION_BLOCKING", Heightmap.Purpose.field_16424, blockState -> blockState.getMaterial().blocksMovement() || !blockState.getFluidState().isEmpty()
		),
		field_13203(
			"MOTION_BLOCKING_NO_LEAVES",
			Heightmap.Purpose.field_13206,
			blockState -> (blockState.getMaterial().blocksMovement() || !blockState.getFluidState().isEmpty()) && !(blockState.getBlock() instanceof LeavesBlock)
		);

		public static final Codec<Heightmap.Type> field_24772 = StringIdentifiable.createCodec(Heightmap.Type::values, Heightmap.Type::byName);
		private final String name;
		private final Heightmap.Purpose purpose;
		private final Predicate<BlockState> blockPredicate;
		private static final Map<String, Heightmap.Type> BY_NAME = Util.make(Maps.<String, Heightmap.Type>newHashMap(), hashMap -> {
			for (Heightmap.Type type : values()) {
				hashMap.put(type.name, type);
			}
		});

		private Type(String name, Heightmap.Purpose purpose, Predicate<BlockState> blockPredicate) {
			this.name = name;
			this.purpose = purpose;
			this.blockPredicate = blockPredicate;
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

		@Nullable
		public static Heightmap.Type byName(String name) {
			return (Heightmap.Type)BY_NAME.get(name);
		}

		public Predicate<BlockState> getBlockPredicate() {
			return this.blockPredicate;
		}

		@Override
		public String asString() {
			return this.name;
		}
	}
}
