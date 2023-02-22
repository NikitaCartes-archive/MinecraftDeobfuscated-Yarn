package net.minecraft.world.biome.source;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.biome.source.util.VanillaBiomeParameters;
import net.minecraft.world.gen.densityfunction.DensityFunctions;

public class MultiNoiseBiomeSource extends BiomeSource {
	private static final MapCodec<RegistryEntry<Biome>> BIOME_CODEC = Biome.REGISTRY_CODEC.fieldOf("biome");
	/**
	 * Used to parse a custom biome source, when a preset hasn't been provided.
	 */
	public static final MapCodec<MultiNoiseUtil.Entries<RegistryEntry<Biome>>> CUSTOM_CODEC = MultiNoiseUtil.Entries.createCodec(BIOME_CODEC).fieldOf("biomes");
	private static final MapCodec<RegistryEntry<MultiNoiseBiomeSourceParameterList>> PRESET_CODEC = MultiNoiseBiomeSourceParameterList.REGISTRY_CODEC
		.fieldOf("preset")
		.withLifecycle(Lifecycle.stable());
	public static final Codec<MultiNoiseBiomeSource> CODEC = Codec.mapEither(CUSTOM_CODEC, PRESET_CODEC)
		.<MultiNoiseBiomeSource>xmap(MultiNoiseBiomeSource::new, source -> source.biomeEntries)
		.codec();
	private final Either<MultiNoiseUtil.Entries<RegistryEntry<Biome>>, RegistryEntry<MultiNoiseBiomeSourceParameterList>> biomeEntries;

	private MultiNoiseBiomeSource(Either<MultiNoiseUtil.Entries<RegistryEntry<Biome>>, RegistryEntry<MultiNoiseBiomeSourceParameterList>> biomeEntries) {
		this.biomeEntries = biomeEntries;
	}

	public static MultiNoiseBiomeSource create(MultiNoiseUtil.Entries<RegistryEntry<Biome>> biomeEntries) {
		return new MultiNoiseBiomeSource(Either.left(biomeEntries));
	}

	public static MultiNoiseBiomeSource create(RegistryEntry<MultiNoiseBiomeSourceParameterList> biomeEntries) {
		return new MultiNoiseBiomeSource(Either.right(biomeEntries));
	}

	private MultiNoiseUtil.Entries<RegistryEntry<Biome>> getBiomeEntries() {
		return this.biomeEntries.map(entries -> entries, parameterListEntry -> ((MultiNoiseBiomeSourceParameterList)parameterListEntry.value()).getEntries());
	}

	@Override
	protected Stream<RegistryEntry<Biome>> biomeStream() {
		return this.getBiomeEntries().getEntries().stream().map(Pair::getSecond);
	}

	@Override
	protected Codec<? extends BiomeSource> getCodec() {
		return CODEC;
	}

	public boolean matchesInstance(RegistryKey<MultiNoiseBiomeSourceParameterList> parameterList) {
		Optional<RegistryEntry<MultiNoiseBiomeSourceParameterList>> optional = this.biomeEntries.right();
		return optional.isPresent() && ((RegistryEntry)optional.get()).matchesKey(parameterList);
	}

	@Override
	public RegistryEntry<Biome> getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise) {
		return this.getBiomeAtPoint(noise.sample(x, y, z));
	}

	@Debug
	public RegistryEntry<Biome> getBiomeAtPoint(MultiNoiseUtil.NoiseValuePoint point) {
		return this.getBiomeEntries().get(point);
	}

	@Override
	public void addDebugInfo(List<String> info, BlockPos pos, MultiNoiseUtil.MultiNoiseSampler noiseSampler) {
		int i = BiomeCoords.fromBlock(pos.getX());
		int j = BiomeCoords.fromBlock(pos.getY());
		int k = BiomeCoords.fromBlock(pos.getZ());
		MultiNoiseUtil.NoiseValuePoint noiseValuePoint = noiseSampler.sample(i, j, k);
		float f = MultiNoiseUtil.toFloat(noiseValuePoint.continentalnessNoise());
		float g = MultiNoiseUtil.toFloat(noiseValuePoint.erosionNoise());
		float h = MultiNoiseUtil.toFloat(noiseValuePoint.temperatureNoise());
		float l = MultiNoiseUtil.toFloat(noiseValuePoint.humidityNoise());
		float m = MultiNoiseUtil.toFloat(noiseValuePoint.weirdnessNoise());
		double d = (double)DensityFunctions.getPeaksValleysNoise(m);
		VanillaBiomeParameters vanillaBiomeParameters = new VanillaBiomeParameters();
		info.add(
			"Biome builder PV: "
				+ VanillaBiomeParameters.getPeaksValleysDescription(d)
				+ " C: "
				+ vanillaBiomeParameters.getContinentalnessDescription((double)f)
				+ " E: "
				+ vanillaBiomeParameters.getErosionDescription((double)g)
				+ " T: "
				+ vanillaBiomeParameters.getTemperatureDescription((double)h)
				+ " H: "
				+ vanillaBiomeParameters.getHumidityDescription((double)l)
		);
	}
}
