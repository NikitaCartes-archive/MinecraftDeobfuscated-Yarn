package net.minecraft;

import javax.annotation.Nullable;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;

public class class_4548 implements class_4543.class_4544 {
	private static final int field_20652 = (int)Math.round(Math.log(16.0) / Math.log(2.0)) - 2;
	private static final int field_20653 = (int)Math.round(Math.log(256.0) / Math.log(2.0)) - 2;
	public static final int field_20649 = 1 << field_20652 + field_20652 + field_20653;
	public static final int field_20650 = (1 << field_20652) - 1;
	public static final int field_20651 = (1 << field_20653) - 1;
	private final Biome[] field_20654;

	public class_4548(Biome[] biomes) {
		this.field_20654 = biomes;
	}

	private class_4548() {
		this(new Biome[field_20649]);
	}

	public class_4548(PacketByteBuf packetByteBuf) {
		this();

		for (int i = 0; i < this.field_20654.length; i++) {
			this.field_20654[i] = Registry.BIOME.get(packetByteBuf.readInt());
		}
	}

	public class_4548(ChunkPos chunkPos, BiomeSource biomeSource) {
		this();
		int i = chunkPos.getStartX() >> 2;
		int j = chunkPos.getStartZ() >> 2;

		for (int k = 0; k < this.field_20654.length; k++) {
			int l = k & field_20650;
			int m = k >> field_20652 + field_20652 & field_20651;
			int n = k >> field_20652 & field_20650;
			this.field_20654[k] = biomeSource.getBiome(i + l, m, j + n);
		}
	}

	public class_4548(ChunkPos chunkPos, BiomeSource biomeSource, @Nullable int[] is) {
		this();
		int i = chunkPos.getStartX() >> 2;
		int j = chunkPos.getStartZ() >> 2;
		if (is != null) {
			for (int k = 0; k < is.length; k++) {
				this.field_20654[k] = Registry.BIOME.get(is[k]);
				if (this.field_20654[k] == null) {
					int l = k & field_20650;
					int m = k >> field_20652 + field_20652 & field_20651;
					int n = k >> field_20652 & field_20650;
					this.field_20654[k] = biomeSource.getBiome(i + l, m, j + n);
				}
			}
		} else {
			for (int kx = 0; kx < this.field_20654.length; kx++) {
				int l = kx & field_20650;
				int m = kx >> field_20652 + field_20652 & field_20651;
				int n = kx >> field_20652 & field_20650;
				this.field_20654[kx] = biomeSource.getBiome(i + l, m, j + n);
			}
		}
	}

	public int[] method_22401() {
		int[] is = new int[this.field_20654.length];

		for (int i = 0; i < this.field_20654.length; i++) {
			is[i] = Registry.BIOME.getRawId(this.field_20654[i]);
		}

		return is;
	}

	public void method_22402(PacketByteBuf packetByteBuf) {
		for (Biome biome : this.field_20654) {
			packetByteBuf.writeInt(Registry.BIOME.getRawId(biome));
		}
	}

	public class_4548 method_22403() {
		return new class_4548((Biome[])this.field_20654.clone());
	}

	@Override
	public Biome getBiome(int i, int j, int k) {
		int l = i & field_20650;
		int m = MathHelper.clamp(j, 0, field_20651);
		int n = k & field_20650;
		return this.field_20654[m << field_20652 + field_20652 | n << field_20652 | l];
	}
}
