package net.minecraft.world;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.collection.PackedIntegerArray;
import net.minecraft.util.collection.PaletteStorage;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Heightmap {
	private static final Logger LOGGER = LogManager.getLogger();
	static final Predicate<BlockState> NOT_AIR = state -> !state.isAir();
	static final Predicate<BlockState> SUFFOCATES = state -> state.getMaterial().blocksMovement();
	private final PaletteStorage storage;
	private final Predicate<BlockState> blockPredicate;
	private final Chunk chunk;

	public Heightmap(Chunk chunk, Heightmap.Type type) {
		this.blockPredicate = type.getBlockPredicate();
		this.chunk = chunk;
		int i = MathHelper.ceilLog2(chunk.getHeight() + 1);
		this.storage = new PackedIntegerArray(i, 256);
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

				for (int m = j - 1; m >= chunk.getBottomY(); m--) {
					mutable.set(k, m, l);
					BlockState blockState = chunk.getBlockState(mutable);
					if (!blockState.isOf(Blocks.AIR)) {
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

				for (int j = y - 1; j >= this.chunk.getBottomY(); j--) {
					mutable.set(x, j, z);
					if (this.blockPredicate.test(this.chunk.getBlockState(mutable))) {
						this.set(x, z, j + 1);
						return true;
					}
				}

				this.set(x, z, this.chunk.getBottomY());
				return true;
			}

			return false;
		}
	}

	public int get(int x, int z) {
		return this.get(toIndex(x, z));
	}

	public int method_35334(int i, int j) {
		return this.get(toIndex(i, j)) - 1;
	}

	private int get(int index) {
		return this.storage.get(index) + this.chunk.getBottomY();
	}

	private void set(int x, int z, int height) {
		this.storage.set(toIndex(x, z), height - this.chunk.getBottomY());
	}

	public void setTo(Chunk chunk, Heightmap.Type type, long[] ls) {
		long[] ms = this.storage.getData();
		if (ms.length == ls.length) {
			System.arraycopy(ls, 0, ms, 0, ls.length);
		} else {
			LOGGER.warn("Ignoring heightmap data for chunk " + chunk.getPos() + ", size does not match; expected: " + ms.length + ", got: " + ls.length);
			populateHeightmaps(chunk, EnumSet.of(type));
		}
	}

	public long[] asLongArray() {
		return this.storage.getData();
	}

	private static int toIndex(int x, int z) {
		return x + z * 16;
	}

	public static enum Purpose {
		WORLDGEN,
		LIVE_WORLD,
		CLIENT;
	}

	public static enum Type implements StringIdentifiable {
		WORLD_SURFACE_WG("WORLD_SURFACE_WG", Heightmap.Purpose.WORLDGEN, Heightmap.NOT_AIR),
		WORLD_SURFACE("WORLD_SURFACE", Heightmap.Purpose.CLIENT, Heightmap.NOT_AIR),
		OCEAN_FLOOR_WG("OCEAN_FLOOR_WG", Heightmap.Purpose.WORLDGEN, Heightmap.SUFFOCATES),
		OCEAN_FLOOR("OCEAN_FLOOR", Heightmap.Purpose.LIVE_WORLD, Heightmap.SUFFOCATES),
		MOTION_BLOCKING("MOTION_BLOCKING", Heightmap.Purpose.CLIENT, blockState -> blockState.getMaterial().blocksMovement() || !blockState.getFluidState().isEmpty()),
		MOTION_BLOCKING_NO_LEAVES(
			"MOTION_BLOCKING_NO_LEAVES",
			Heightmap.Purpose.LIVE_WORLD,
			blockState -> (blockState.getMaterial().blocksMovement() || !blockState.getFluidState().isEmpty()) && !(blockState.getBlock() instanceof LeavesBlock)
		);

		public static final Codec<Heightmap.Type> CODEC = StringIdentifiable.createCodec(Heightmap.Type::values, Heightmap.Type::byName);
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
			return this.purpose == Heightmap.Purpose.CLIENT;
		}

		public boolean isStoredServerSide() {
			return this.purpose != Heightmap.Purpose.WORLDGEN;
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
