package net.minecraft.world.biome.source;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.gen.densityfunction.DensityFunction;

public class TheEndBiomeSource extends BiomeSource {
	public static final Codec<TheEndBiomeSource> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					RegistryOps.getEntryCodec(BiomeKeys.THE_END),
					RegistryOps.getEntryCodec(BiomeKeys.END_HIGHLANDS),
					RegistryOps.getEntryCodec(BiomeKeys.END_MIDLANDS),
					RegistryOps.getEntryCodec(BiomeKeys.SMALL_END_ISLANDS),
					RegistryOps.getEntryCodec(BiomeKeys.END_BARRENS)
				)
				.apply(instance, instance.stable(TheEndBiomeSource::new))
	);
	private final RegistryEntry<Biome> centerBiome;
	private final RegistryEntry<Biome> highlandsBiome;
	private final RegistryEntry<Biome> midlandsBiome;
	private final RegistryEntry<Biome> smallIslandsBiome;
	private final RegistryEntry<Biome> barrensBiome;

	public static TheEndBiomeSource createVanilla(RegistryEntryLookup<Biome> biomeLookup) {
		return new TheEndBiomeSource(
			biomeLookup.getOrThrow(BiomeKeys.THE_END),
			biomeLookup.getOrThrow(BiomeKeys.END_HIGHLANDS),
			biomeLookup.getOrThrow(BiomeKeys.END_MIDLANDS),
			biomeLookup.getOrThrow(BiomeKeys.SMALL_END_ISLANDS),
			biomeLookup.getOrThrow(BiomeKeys.END_BARRENS)
		);
	}

	private TheEndBiomeSource(
		RegistryEntry<Biome> centerBiome,
		RegistryEntry<Biome> highlandsBiome,
		RegistryEntry<Biome> midlandsBiome,
		RegistryEntry<Biome> smallIslandsBiome,
		RegistryEntry<Biome> barrensBiome
	) {
		this.centerBiome = centerBiome;
		this.highlandsBiome = highlandsBiome;
		this.midlandsBiome = midlandsBiome;
		this.smallIslandsBiome = smallIslandsBiome;
		this.barrensBiome = barrensBiome;
	}

	@Override
	protected Stream<RegistryEntry<Biome>> biomeStream() {
		return Stream.of(this.centerBiome, this.highlandsBiome, this.midlandsBiome, this.smallIslandsBiome, this.barrensBiome);
	}

	@Override
	protected Codec<? extends BiomeSource> getCodec() {
		return CODEC;
	}

	@Override
	public RegistryEntry<Biome> getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise) {
		int i = BiomeCoords.toBlock(x);
		int j = BiomeCoords.toBlock(y);
		int k = BiomeCoords.toBlock(z);
		int l = ChunkSectionPos.getSectionCoord(i);
		int m = ChunkSectionPos.getSectionCoord(k);
		if ((long)l * (long)l + (long)m * (long)m <= 4096L) {
			return this.centerBiome;
		} else {
			int n = (ChunkSectionPos.getSectionCoord(i) * 2 + 1) * 8;
			int o = (ChunkSectionPos.getSectionCoord(k) * 2 + 1) * 8;
			double d = noise.erosion().sample(new DensityFunction.UnblendedNoisePos(n, j, o));
			if (d > 0.25) {
				return this.highlandsBiome;
			} else if (d >= -0.0625) {
				return this.midlandsBiome;
			} else {
				return d < -0.21875 ? this.smallIslandsBiome : this.barrensBiome;
			}
		}
	}
}
