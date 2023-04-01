package net.minecraft.world.biome.source;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

public class TheMoonBiomeSource extends BiomeSource {
	public static final Codec<TheMoonBiomeSource> field_44212 = RecordCodecBuilder.create(
		instance -> instance.group(RegistryOps.getEntryCodec(BiomeKeys.THE_MOON)).apply(instance, instance.stable(TheMoonBiomeSource::new))
	);
	private final RegistryEntry<Biome> moonBiome;

	public static TheMoonBiomeSource createVanilla(RegistryEntryLookup<Biome> biomeLookup) {
		return new TheMoonBiomeSource(biomeLookup.getOrThrow(BiomeKeys.THE_MOON));
	}

	private TheMoonBiomeSource(RegistryEntry<Biome> moonBiome) {
		this.moonBiome = moonBiome;
	}

	@Override
	protected Stream<RegistryEntry<Biome>> biomeStream() {
		return Stream.of(this.moonBiome);
	}

	@Override
	protected Codec<? extends BiomeSource> getCodec() {
		return field_44212;
	}

	@Override
	public RegistryEntry<Biome> getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise) {
		return this.moonBiome;
	}
}
