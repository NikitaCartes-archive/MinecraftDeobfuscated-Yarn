package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.class_5314;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class ConfiguredStructureFeature<FC extends FeatureConfig, F extends StructureFeature<FC>> {
	public static final Codec<ConfiguredStructureFeature<?, ?>> field_24834 = Registry.STRUCTURE_FEATURE
		.dispatch("name", configuredStructureFeature -> configuredStructureFeature.field_24835, StructureFeature::method_28665);
	public final F field_24835;
	public final FC field_24836;

	public ConfiguredStructureFeature(F structureFeature, FC featureConfig) {
		this.field_24835 = structureFeature;
		this.field_24836 = featureConfig;
	}

	public StructureStart<?> method_28622(
		ChunkGenerator chunkGenerator, BiomeSource biomeSource, StructureManager structureManager, long l, ChunkPos chunkPos, Biome biome, int i, class_5314 arg
	) {
		return this.field_24835.method_28657(chunkGenerator, biomeSource, structureManager, l, chunkPos, biome, i, new ChunkRandom(), arg, this.field_24836);
	}
}
