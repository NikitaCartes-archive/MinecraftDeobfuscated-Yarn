package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.entity.EntityType;
import net.minecraft.structure.NetherFortressGenerator;
import net.minecraft.structure.StructureGeneratorFactory;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.util.collection.Pool;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;

public class NetherFortressFeature extends StructureFeature<DefaultFeatureConfig> {
	public static final Pool<SpawnSettings.SpawnEntry> MONSTER_SPAWNS = Pool.of(
		new SpawnSettings.SpawnEntry(EntityType.BLAZE, 10, 2, 3),
		new SpawnSettings.SpawnEntry(EntityType.ZOMBIFIED_PIGLIN, 5, 4, 4),
		new SpawnSettings.SpawnEntry(EntityType.WITHER_SKELETON, 8, 5, 5),
		new SpawnSettings.SpawnEntry(EntityType.SKELETON, 2, 5, 5),
		new SpawnSettings.SpawnEntry(EntityType.MAGMA_CUBE, 3, 4, 4)
	);

	public NetherFortressFeature(Codec<DefaultFeatureConfig> configCodec) {
		super(configCodec, StructureGeneratorFactory.simple(NetherFortressFeature::method_28640, NetherFortressFeature::addPieces));
	}

	private static boolean method_28640(StructureGeneratorFactory.Context<DefaultFeatureConfig> context) {
		ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(0L));
		chunkRandom.setCarverSeed(context.seed(), context.chunkPos().x, context.chunkPos().z);
		return chunkRandom.nextInt(5) >= 2
			? false
			: context.validBiome()
				.test(
					context.chunkGenerator()
						.getBiomeForNoiseGen(
							BiomeCoords.fromBlock(context.chunkPos().getCenterX()), BiomeCoords.fromBlock(64), BiomeCoords.fromBlock(context.chunkPos().getCenterZ())
						)
				);
	}

	private static void addPieces(StructurePiecesCollector collector, StructurePiecesGenerator.Context<DefaultFeatureConfig> context) {
		NetherFortressGenerator.Start start = new NetherFortressGenerator.Start(context.random(), context.chunkPos().getOffsetX(2), context.chunkPos().getOffsetZ(2));
		collector.addPiece(start);
		start.fillOpenings(start, collector, context.random());
		List<StructurePiece> list = start.pieces;

		while (!list.isEmpty()) {
			int i = context.random().nextInt(list.size());
			StructurePiece structurePiece = (StructurePiece)list.remove(i);
			structurePiece.fillOpenings(start, collector, context.random());
		}

		collector.shiftInto(context.random(), 48, 70);
	}
}
