package net.minecraft.world.biome.source;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BiomeArray implements BiomeAccess.Storage {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final int HORIZONTAL_SECTION_COUNT = MathHelper.log2DeBruijn(16) - 2;
	private static final int HORIZONTAL_BIT_MASK = (1 << HORIZONTAL_SECTION_COUNT) - 1;
	public static final int DEFAULT_LENGTH = 1 << HORIZONTAL_SECTION_COUNT + HORIZONTAL_SECTION_COUNT + DimensionType.SIZE_BITS_Y - 2;
	private final IndexedIterable<Biome> field_25831;
	private final Biome[] data;
	private final int field_28126;
	private final int field_28127;

	protected BiomeArray(IndexedIterable<Biome> indexedIterable, HeightLimitView heightLimitView, Biome[] biomes) {
		this.field_25831 = indexedIterable;
		this.data = biomes;
		this.field_28126 = BiomeCoords.fromBlock(heightLimitView.getBottomY());
		this.field_28127 = BiomeCoords.fromBlock(heightLimitView.getHeight()) - 1;
	}

	@Environment(EnvType.CLIENT)
	public BiomeArray(IndexedIterable<Biome> indexedIterable, HeightLimitView heightLimitView, int[] is) {
		this(indexedIterable, heightLimitView, new Biome[is.length]);

		for (int i = 0; i < this.data.length; i++) {
			int j = is[i];
			Biome biome = indexedIterable.get(j);
			if (biome == null) {
				LOGGER.warn("Received invalid biome id: {}", j);
				this.data[i] = indexedIterable.get(0);
			} else {
				this.data[i] = biome;
			}
		}
	}

	public BiomeArray(IndexedIterable<Biome> indexedIterable, HeightLimitView heightLimitView, ChunkPos chunkPos, BiomeSource biomeSource) {
		this(indexedIterable, heightLimitView, chunkPos, biomeSource, null);
	}

	public BiomeArray(IndexedIterable<Biome> indexedIterable, HeightLimitView heightLimitView, ChunkPos chunkPos, BiomeSource biomeSource, @Nullable int[] is) {
		this(indexedIterable, heightLimitView, new Biome[(1 << HORIZONTAL_SECTION_COUNT + HORIZONTAL_SECTION_COUNT) * method_32915(heightLimitView.getHeight(), 4)]);
		int i = BiomeCoords.fromBlock(chunkPos.getStartX());
		int j = this.field_28126;
		int k = BiomeCoords.fromBlock(chunkPos.getStartZ());
		if (is != null) {
			for (int l = 0; l < is.length; l++) {
				this.data[l] = indexedIterable.get(is[l]);
				if (this.data[l] == null) {
					this.data[l] = method_32916(biomeSource, i, j, k, l);
				}
			}
		} else {
			for (int lx = 0; lx < this.data.length; lx++) {
				this.data[lx] = method_32916(biomeSource, i, j, k, lx);
			}
		}
	}

	private static int method_32915(int i, int j) {
		return (i + j - 1) / j;
	}

	private static Biome method_32916(BiomeSource biomeSource, int i, int j, int k, int l) {
		int m = l & HORIZONTAL_BIT_MASK;
		int n = l >> HORIZONTAL_SECTION_COUNT + HORIZONTAL_SECTION_COUNT;
		int o = l >> HORIZONTAL_SECTION_COUNT & HORIZONTAL_BIT_MASK;
		return biomeSource.getBiomeForNoiseGen(i + m, j + n, k + o);
	}

	public int[] toIntArray() {
		int[] is = new int[this.data.length];

		for (int i = 0; i < this.data.length; i++) {
			is[i] = this.field_25831.getRawId(this.data[i]);
		}

		return is;
	}

	@Override
	public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
		int i = biomeX & HORIZONTAL_BIT_MASK;
		int j = MathHelper.clamp(biomeY - this.field_28126, 0, this.field_28127);
		int k = biomeZ & HORIZONTAL_BIT_MASK;
		return this.data[j << HORIZONTAL_SECTION_COUNT + HORIZONTAL_SECTION_COUNT | k << HORIZONTAL_SECTION_COUNT | i];
	}
}
