package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.entity.EntityType;
import net.minecraft.structure.StructureGeneratorFactory;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.structure.SwampHutGenerator;
import net.minecraft.util.collection.Pool;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.SpawnSettings;

public class SwampHutFeature extends StructureFeature<DefaultFeatureConfig> {
	public static final Pool<SpawnSettings.SpawnEntry> MONSTER_SPAWNS = Pool.of(new SpawnSettings.SpawnEntry(EntityType.WITCH, 1, 1, 1));
	public static final Pool<SpawnSettings.SpawnEntry> CREATURE_SPAWNS = Pool.of(new SpawnSettings.SpawnEntry(EntityType.CAT, 1, 1, 1));

	public SwampHutFeature(Codec<DefaultFeatureConfig> configCodec) {
		super(
			configCodec, StructureGeneratorFactory.simple(StructureGeneratorFactory.checkForBiomeOnTop(Heightmap.Type.WORLD_SURFACE_WG), SwampHutFeature::addPieces)
		);
	}

	private static void addPieces(StructurePiecesCollector collector, StructurePiecesGenerator.Context<DefaultFeatureConfig> context) {
		collector.addPiece(new SwampHutGenerator(context.random(), context.chunkPos().getStartX(), context.chunkPos().getStartZ()));
	}
}
