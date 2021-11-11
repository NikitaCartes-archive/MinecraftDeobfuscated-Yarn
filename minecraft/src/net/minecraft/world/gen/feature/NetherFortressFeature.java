package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.entity.EntityType;
import net.minecraft.structure.NetherFortressGenerator;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
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
		super(configCodec, NetherFortressFeature::addPieces);
	}

	protected boolean shouldStartAt(
		ChunkGenerator chunkGenerator, BiomeSource biomeSource, long l, ChunkPos chunkPos, DefaultFeatureConfig defaultFeatureConfig, HeightLimitView heightLimitView
	) {
		ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(0L));
		chunkRandom.setCarverSeed(l, chunkPos.x, chunkPos.z);
		return chunkRandom.nextInt(5) < 2;
	}

	private static void addPieces(StructurePiecesCollector collector, DefaultFeatureConfig config, StructurePiecesGenerator.Context context) {
		if (context.biomeLimit()
			.test(
				context.chunkGenerator()
					.getBiomeForNoiseGen(
						BiomeCoords.fromBlock(context.chunkPos().getCenterX()), BiomeCoords.fromBlock(64), BiomeCoords.fromBlock(context.chunkPos().getCenterZ())
					)
			)) {
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
}
