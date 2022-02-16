package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.function.Predicate;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryCodecs;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class ConfiguredStructureFeature<FC extends FeatureConfig, F extends StructureFeature<FC>> {
	public static final Codec<ConfiguredStructureFeature<?, ?>> CODEC = Registry.STRUCTURE_FEATURE
		.getCodec()
		.dispatch(configuredStructureFeature -> configuredStructureFeature.feature, StructureFeature::getCodec);
	public static final Codec<RegistryEntry<ConfiguredStructureFeature<?, ?>>> REGISTRY_CODEC = RegistryElementCodec.of(
		Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, CODEC
	);
	public static final Codec<RegistryEntryList<ConfiguredStructureFeature<?, ?>>> REGISTRY_ELEMENT_CODEC = RegistryCodecs.entryList(
		Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, CODEC
	);
	public final F feature;
	public final FC config;
	public final RegistryEntryList<Biome> field_36629;

	public ConfiguredStructureFeature(F feature, FC config, RegistryEntryList<Biome> registryEntryList) {
		this.feature = feature;
		this.config = config;
		this.field_36629 = registryEntryList;
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
		int structureReferences,
		HeightLimitView heightLimitView,
		Predicate<RegistryEntry<Biome>> predicate
	) {
		return this.feature
			.tryPlaceStart(
				registryManager, chunkGenerator, biomeSource, structureManager, worldSeed, chunkPos, structureReferences, this.config, heightLimitView, predicate
			);
	}

	public RegistryEntryList<Biome> method_40549() {
		return this.field_36629;
	}
}
