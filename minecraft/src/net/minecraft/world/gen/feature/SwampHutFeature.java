package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.entity.EntityType;
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
		super(configCodec, SwampHutFeature::addPieces);
	}

	private static void addPieces(StructurePiecesCollector collector, DefaultFeatureConfig config, StructurePiecesGenerator.Context context) {
		if (context.isBiomeValid(Heightmap.Type.WORLD_SURFACE_WG)) {
			collector.addPiece(new SwampHutGenerator(context.random(), context.chunkPos().getStartX(), context.chunkPos().getStartZ()));
		}
	}
}
