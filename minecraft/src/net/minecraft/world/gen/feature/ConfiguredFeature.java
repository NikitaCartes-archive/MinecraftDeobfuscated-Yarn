package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.stream.Stream;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public record ConfiguredFeature<FC extends FeatureConfig, F extends Feature<FC>>(F feature, FC config) {
	public static final Codec<ConfiguredFeature<?, ?>> CODEC = Registries.FEATURE
		.getCodec()
		.dispatch(configuredFeature -> configuredFeature.feature, Feature::getCodec);
	public static final Codec<RegistryEntry<ConfiguredFeature<?, ?>>> REGISTRY_CODEC = RegistryElementCodec.of(RegistryKeys.CONFIGURED_FEATURE, CODEC);
	public static final Codec<RegistryEntryList<ConfiguredFeature<?, ?>>> LIST_CODEC = RegistryCodecs.entryList(RegistryKeys.CONFIGURED_FEATURE, CODEC);

	public boolean generate(StructureWorldAccess world, ChunkGenerator chunkGenerator, Random random, BlockPos origin) {
		return this.feature.generateIfValid(this.config, world, chunkGenerator, random, origin);
	}

	public Stream<ConfiguredFeature<?, ?>> getDecoratedFeatures() {
		return Stream.concat(Stream.of(this), this.config.getDecoratedFeatures());
	}

	public String toString() {
		return "Configured: " + this.feature + ": " + this.config;
	}
}
