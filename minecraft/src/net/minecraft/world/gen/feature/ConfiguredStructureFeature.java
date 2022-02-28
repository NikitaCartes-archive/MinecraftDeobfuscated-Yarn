package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.structure.StructureGeneratorFactory;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryCodecs;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.StructureSpawns;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;

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
	public final RegistryEntryList<Biome> biomes;
	public final Map<SpawnGroup, StructureSpawns> field_37143;
	public final boolean field_37144;

	public ConfiguredStructureFeature(F feature, FC config, RegistryEntryList<Biome> biomes, boolean bl, Map<SpawnGroup, StructureSpawns> map) {
		this.feature = feature;
		this.config = config;
		this.biomes = biomes;
		this.field_37144 = bl;
		this.field_37143 = map;
	}

	public StructureStart tryPlaceStart(
		DynamicRegistryManager registryManager,
		ChunkGenerator chunkGenerator,
		BiomeSource biomeSource,
		StructureManager structureManager,
		long worldSeed,
		ChunkPos chunkPos,
		int structureReferences,
		HeightLimitView world,
		Predicate<RegistryEntry<Biome>> biomePredicate
	) {
		Optional<StructurePiecesGenerator<FC>> optional = this.feature
			.method_41138()
			.createGenerator(
				new StructureGeneratorFactory.Context<>(
					chunkGenerator, biomeSource, worldSeed, chunkPos, this.config, world, biomePredicate, structureManager, registryManager
				)
			);
		if (optional.isPresent()) {
			StructurePiecesCollector structurePiecesCollector = new StructurePiecesCollector();
			ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(0L));
			chunkRandom.setCarverSeed(worldSeed, chunkPos.x, chunkPos.z);
			((StructurePiecesGenerator)optional.get())
				.generatePieces(
					structurePiecesCollector, new StructurePiecesGenerator.Context<>(this.config, chunkGenerator, structureManager, chunkPos, world, chunkRandom, worldSeed)
				);
			StructureStart structureStart = new StructureStart(this, chunkPos, structureReferences, structurePiecesCollector.toList());
			if (structureStart.hasChildren()) {
				return structureStart;
			}
		}

		return StructureStart.DEFAULT;
	}

	public RegistryEntryList<Biome> getBiomes() {
		return this.biomes;
	}

	public BlockBox method_41129(BlockBox blockBox) {
		return this.field_37144 ? blockBox.expand(12) : blockBox;
	}
}
