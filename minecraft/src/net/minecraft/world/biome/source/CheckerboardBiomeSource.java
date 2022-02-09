package net.minecraft.world.biome.source;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

public class CheckerboardBiomeSource extends BiomeSource {
	public static final Codec<CheckerboardBiomeSource> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Biome.field_26750.fieldOf("biomes").forGetter(checkerboardBiomeSource -> checkerboardBiomeSource.biomeArray),
					Codec.intRange(0, 62).fieldOf("scale").orElse(2).forGetter(checkerboardBiomeSource -> checkerboardBiomeSource.scale)
				)
				.apply(instance, CheckerboardBiomeSource::new)
	);
	private final RegistryEntryList<Biome> biomeArray;
	private final int gridSize;
	private final int scale;

	public CheckerboardBiomeSource(RegistryEntryList<Biome> registryEntryList, int size) {
		super(registryEntryList.stream());
		this.biomeArray = registryEntryList;
		this.gridSize = size + 2;
		this.scale = size;
	}

	@Override
	protected Codec<? extends BiomeSource> getCodec() {
		return CODEC;
	}

	@Override
	public BiomeSource withSeed(long seed) {
		return this;
	}

	@Override
	public RegistryEntry<Biome> getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise) {
		return this.biomeArray.get(Math.floorMod((x >> this.gridSize) + (z >> this.gridSize), this.biomeArray.size()));
	}
}
