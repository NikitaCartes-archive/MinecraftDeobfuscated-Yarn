package net.minecraft.world.biome;

import javax.annotation.Nullable;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.source.BiomeSource;

public class BiomeArray implements BiomeAccess.Storage {
	private static final int HORIZONTAL_SECTION_COUNT = (int)Math.round(Math.log(16.0) / Math.log(2.0)) - 2;
	private static final int VERTICAL_SECTION_COUNT = (int)Math.round(Math.log(256.0) / Math.log(2.0)) - 2;
	public static final int DEFAULT_LENGTH = 1 << HORIZONTAL_SECTION_COUNT + HORIZONTAL_SECTION_COUNT + VERTICAL_SECTION_COUNT;
	public static final int HORIZONTAL_BIT_MASK = (1 << HORIZONTAL_SECTION_COUNT) - 1;
	public static final int VERTICAL_BIT_MASK = (1 << VERTICAL_SECTION_COUNT) - 1;
	private final Biome[] data;

	public BiomeArray(Biome[] biomes) {
		this.data = biomes;
	}

	private BiomeArray() {
		this(new Biome[DEFAULT_LENGTH]);
	}

	public BiomeArray(PacketByteBuf packetByteBuf) {
		this();

		for (int i = 0; i < this.data.length; i++) {
			this.data[i] = Registry.BIOME.get(packetByteBuf.readInt());
		}
	}

	public BiomeArray(ChunkPos chunkPos, BiomeSource biomeSource) {
		this();
		int i = chunkPos.getStartX() >> 2;
		int j = chunkPos.getStartZ() >> 2;

		for (int k = 0; k < this.data.length; k++) {
			int l = k & HORIZONTAL_BIT_MASK;
			int m = k >> HORIZONTAL_SECTION_COUNT + HORIZONTAL_SECTION_COUNT & VERTICAL_BIT_MASK;
			int n = k >> HORIZONTAL_SECTION_COUNT & HORIZONTAL_BIT_MASK;
			this.data[k] = biomeSource.getStoredBiome(i + l, m, j + n);
		}
	}

	public BiomeArray(ChunkPos chunkPos, BiomeSource biomeSource, @Nullable int[] is) {
		this();
		int i = chunkPos.getStartX() >> 2;
		int j = chunkPos.getStartZ() >> 2;
		if (is != null) {
			for (int k = 0; k < is.length; k++) {
				this.data[k] = Registry.BIOME.get(is[k]);
				if (this.data[k] == null) {
					int l = k & HORIZONTAL_BIT_MASK;
					int m = k >> HORIZONTAL_SECTION_COUNT + HORIZONTAL_SECTION_COUNT & VERTICAL_BIT_MASK;
					int n = k >> HORIZONTAL_SECTION_COUNT & HORIZONTAL_BIT_MASK;
					this.data[k] = biomeSource.getStoredBiome(i + l, m, j + n);
				}
			}
		} else {
			for (int kx = 0; kx < this.data.length; kx++) {
				int l = kx & HORIZONTAL_BIT_MASK;
				int m = kx >> HORIZONTAL_SECTION_COUNT + HORIZONTAL_SECTION_COUNT & VERTICAL_BIT_MASK;
				int n = kx >> HORIZONTAL_SECTION_COUNT & HORIZONTAL_BIT_MASK;
				this.data[kx] = biomeSource.getStoredBiome(i + l, m, j + n);
			}
		}
	}

	public int[] toIntArray() {
		int[] is = new int[this.data.length];

		for (int i = 0; i < this.data.length; i++) {
			is[i] = Registry.BIOME.getRawId(this.data[i]);
		}

		return is;
	}

	public void toPacket(PacketByteBuf packetByteBuf) {
		for (Biome biome : this.data) {
			packetByteBuf.writeInt(Registry.BIOME.getRawId(biome));
		}
	}

	public BiomeArray copy() {
		return new BiomeArray((Biome[])this.data.clone());
	}

	@Override
	public Biome getStoredBiome(int i, int j, int k) {
		int l = i & HORIZONTAL_BIT_MASK;
		int m = MathHelper.clamp(j, 0, VERTICAL_BIT_MASK);
		int n = k & HORIZONTAL_BIT_MASK;
		return this.data[m << HORIZONTAL_SECTION_COUNT + HORIZONTAL_SECTION_COUNT | n << HORIZONTAL_SECTION_COUNT | l];
	}
}
