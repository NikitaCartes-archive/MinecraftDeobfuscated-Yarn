package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StructureConfig;

public class ConfiguredStructureFeature<FC extends FeatureConfig, F extends StructureFeature<FC>> {
	public static final Codec<ConfiguredStructureFeature<?, ?>> TYPE_CODEC = Registry.STRUCTURE_FEATURE
		.dispatch("name", configuredStructureFeature -> configuredStructureFeature.feature, StructureFeature::getCodec);
	public final F feature;
	public final FC config;

	public ConfiguredStructureFeature(F structureFeature, FC featureConfig) {
		this.feature = structureFeature;
		this.config = featureConfig;
	}

	public StructureStart<?> method_28622(
		ChunkGenerator chunkGenerator,
		BiomeSource biomeSource,
		StructureManager structureManager,
		long l,
		ChunkPos chunkPos,
		Biome biome,
		int i,
		StructureConfig structureConfig
	) {
		return this.feature.method_28657(chunkGenerator, biomeSource, structureManager, l, chunkPos, biome, i, new ChunkRandom(), structureConfig, this.config);
	}
}
