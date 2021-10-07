package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;
import net.minecraft.world.gen.random.RandomSeed;

public class ConfiguredStructureFeature<FC extends FeatureConfig, F extends StructureFeature<FC>> {
	public static final Codec<ConfiguredStructureFeature<?, ?>> CODEC = Registry.STRUCTURE_FEATURE
		.dispatch(configuredStructureFeature -> configuredStructureFeature.feature, StructureFeature::getCodec);
	public static final Codec<Supplier<ConfiguredStructureFeature<?, ?>>> REGISTRY_CODEC = RegistryElementCodec.of(
		Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, CODEC
	);
	public static final Codec<List<Supplier<ConfiguredStructureFeature<?, ?>>>> REGISTRY_ELEMENT_CODEC = RegistryElementCodec.method_31194(
		Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, CODEC
	);
	public final F feature;
	public final FC config;

	public ConfiguredStructureFeature(F feature, FC config) {
		this.feature = feature;
		this.config = config;
	}

	/**
	 * @see StructureFeature#tryPlaceStart
	 */
	public StructureStart<?> tryPlaceStart(
		DynamicRegistryManager registryManager,
		ChunkGenerator chunkGenerator,
		BiomeSource biomeSource,
		StructureManager structureManager,
		long worldSeed,
		ChunkPos chunkPos,
		int i,
		StructureConfig structureConfig,
		HeightLimitView heightLimitView,
		Predicate<Biome> predicate
	) {
		return this.feature
			.tryPlaceStart(
				registryManager,
				chunkGenerator,
				biomeSource,
				structureManager,
				worldSeed,
				chunkPos,
				i,
				new ChunkRandom(new AtomicSimpleRandom(RandomSeed.getSeed())),
				structureConfig,
				this.config,
				heightLimitView,
				predicate
			);
	}
}
