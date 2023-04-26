package net.minecraft.world.chunk.light;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ChunkToNibbleArrayMap;

public class SkyLightStorage extends LightStorage<SkyLightStorage.Data> {
	protected SkyLightStorage(ChunkProvider chunkProvider) {
		super(LightType.SKY, chunkProvider, new SkyLightStorage.Data(new Long2ObjectOpenHashMap<>(), new Long2IntOpenHashMap(), Integer.MAX_VALUE));
	}

	@Override
	protected int getLight(long blockPos) {
		return this.getLight(blockPos, false);
	}

	protected int getLight(long blockPos, boolean cached) {
		long l = ChunkSectionPos.fromBlockPos(blockPos);
		int i = ChunkSectionPos.unpackY(l);
		SkyLightStorage.Data data = cached ? this.storage : this.uncachedStorage;
		int j = data.columnToTopSection.get(ChunkSectionPos.withZeroY(l));
		if (j != data.minSectionY && i < j) {
			ChunkNibbleArray chunkNibbleArray = this.getLightSection(data, l);
			if (chunkNibbleArray == null) {
				for (blockPos = BlockPos.removeChunkSectionLocalY(blockPos); chunkNibbleArray == null; chunkNibbleArray = this.getLightSection(data, l)) {
					if (++i >= j) {
						return 15;
					}

					l = ChunkSectionPos.offset(l, Direction.UP);
				}
			}

			return chunkNibbleArray.get(
				ChunkSectionPos.getLocalCoord(BlockPos.unpackLongX(blockPos)),
				ChunkSectionPos.getLocalCoord(BlockPos.unpackLongY(blockPos)),
				ChunkSectionPos.getLocalCoord(BlockPos.unpackLongZ(blockPos))
			);
		} else {
			return cached && !this.isSectionInEnabledColumn(l) ? 0 : 15;
		}
	}

	@Override
	protected void onLoadSection(long sectionPos) {
		int i = ChunkSectionPos.unpackY(sectionPos);
		if (this.storage.minSectionY > i) {
			this.storage.minSectionY = i;
			this.storage.columnToTopSection.defaultReturnValue(this.storage.minSectionY);
		}

		long l = ChunkSectionPos.withZeroY(sectionPos);
		int j = this.storage.columnToTopSection.get(l);
		if (j < i + 1) {
			this.storage.columnToTopSection.put(l, i + 1);
		}
	}

	@Override
	protected void onUnloadSection(long sectionPos) {
		long l = ChunkSectionPos.withZeroY(sectionPos);
		int i = ChunkSectionPos.unpackY(sectionPos);
		if (this.storage.columnToTopSection.get(l) == i + 1) {
			long m;
			for (m = sectionPos; !this.hasSection(m) && this.isAboveMinHeight(i); m = ChunkSectionPos.offset(m, Direction.DOWN)) {
				i--;
			}

			if (this.hasSection(m)) {
				this.storage.columnToTopSection.put(l, i + 1);
			} else {
				this.storage.columnToTopSection.remove(l);
			}
		}
	}

	@Override
	protected ChunkNibbleArray createSection(long sectionPos) {
		ChunkNibbleArray chunkNibbleArray = this.queuedSections.get(sectionPos);
		if (chunkNibbleArray != null) {
			return chunkNibbleArray;
		} else {
			int i = this.storage.columnToTopSection.get(ChunkSectionPos.withZeroY(sectionPos));
			if (i != this.storage.minSectionY && ChunkSectionPos.unpackY(sectionPos) < i) {
				long l = ChunkSectionPos.offset(sectionPos, Direction.UP);

				ChunkNibbleArray chunkNibbleArray2;
				while ((chunkNibbleArray2 = this.getLightSection(l, true)) == null) {
					l = ChunkSectionPos.offset(l, Direction.UP);
				}

				return copy(chunkNibbleArray2);
			} else {
				return this.isSectionInEnabledColumn(sectionPos) ? new ChunkNibbleArray(15) : new ChunkNibbleArray();
			}
		}
	}

	private static ChunkNibbleArray copy(ChunkNibbleArray source) {
		if (source.isArrayUninitialized()) {
			return source.copy();
		} else {
			byte[] bs = source.asByteArray();
			byte[] cs = new byte[2048];

			for (int i = 0; i < 16; i++) {
				System.arraycopy(bs, 0, cs, i * 128, 128);
			}

			return new ChunkNibbleArray(cs);
		}
	}

	protected boolean isAboveMinHeight(int sectionY) {
		return sectionY >= this.storage.minSectionY;
	}

	protected boolean isAtOrAboveTopmostSection(long sectionPos) {
		long l = ChunkSectionPos.withZeroY(sectionPos);
		int i = this.storage.columnToTopSection.get(l);
		return i == this.storage.minSectionY || ChunkSectionPos.unpackY(sectionPos) >= i;
	}

	protected int getTopSectionForColumn(long columnPos) {
		return this.storage.columnToTopSection.get(columnPos);
	}

	protected int getMinSectionY() {
		return this.storage.minSectionY;
	}

	protected static final class Data extends ChunkToNibbleArrayMap<SkyLightStorage.Data> {
		int minSectionY;
		final Long2IntOpenHashMap columnToTopSection;

		public Data(Long2ObjectOpenHashMap<ChunkNibbleArray> arrays, Long2IntOpenHashMap columnToTopSection, int minSectionY) {
			super(arrays);
			this.columnToTopSection = columnToTopSection;
			columnToTopSection.defaultReturnValue(minSectionY);
			this.minSectionY = minSectionY;
		}

		public SkyLightStorage.Data copy() {
			return new SkyLightStorage.Data(this.arrays.clone(), this.columnToTopSection.clone(), this.minSectionY);
		}
	}
}
