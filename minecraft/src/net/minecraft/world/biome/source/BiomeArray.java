package net.minecraft.world.biome.source;

import java.util.Arrays;
import javax.annotation.Nullable;
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
	private final IndexedIterable<Biome> biomes;
	private final Biome[] data;
	private final int field_28126;
	private final int field_28127;

	protected BiomeArray(IndexedIterable<Biome> biomes, HeightLimitView world, Biome[] data) {
		this.biomes = biomes;
		this.data = data;
		this.field_28126 = BiomeCoords.fromBlock(world.getBottomY());
		this.field_28127 = BiomeCoords.fromBlock(world.getHeight()) - 1;
	}

	public BiomeArray(IndexedIterable<Biome> biomes, HeightLimitView world, int[] ids) {
		this(biomes, world, new Biome[ids.length]);
		int i = -1;

		for (int j = 0; j < this.data.length; j++) {
			int k = ids[j];
			Biome biome = biomes.get(k);
			if (biome == null) {
				if (i == -1) {
					i = j;
				}

				this.data[j] = biomes.get(0);
			} else {
				this.data[j] = biome;
			}
		}

		if (i != -1) {
			LOGGER.warn("Invalid biome data received, starting from {}: {}", i, Arrays.toString(ids));
		}
	}

	public BiomeArray(IndexedIterable<Biome> biomes, HeightLimitView world, ChunkPos chunkPos, BiomeSource biomeSource) {
		this(biomes, world, chunkPos, biomeSource, null);
	}

	public BiomeArray(IndexedIterable<Biome> biomes, HeightLimitView world, ChunkPos chunkPos, BiomeSource biomeSource, @Nullable int[] is) {
		this(biomes, world, new Biome[(1 << HORIZONTAL_SECTION_COUNT + HORIZONTAL_SECTION_COUNT) * method_32915(world.getHeight(), 4)]);
		int i = BiomeCoords.fromBlock(chunkPos.getStartX());
		int j = this.field_28126;
		int k = BiomeCoords.fromBlock(chunkPos.getStartZ());

		for (int l = 0; l < this.data.length; l++) {
			if (is != null && l < is.length) {
				this.data[l] = biomes.get(is[l]);
			}

			if (this.data[l] == null) {
				this.data[l] = method_32916(biomeSource, i, j, k, l);
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
			is[i] = this.biomes.getRawId(this.data[i]);
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
