package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.class_6622;
import net.minecraft.class_6626;
import net.minecraft.entity.EntityType;
import net.minecraft.structure.NetherFortressGenerator;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.random.ChunkRandom;

public class NetherFortressFeature extends StructureFeature<DefaultFeatureConfig> {
	public static final Pool<SpawnSettings.SpawnEntry> MONSTER_SPAWNS = Pool.of(
		new SpawnSettings.SpawnEntry(EntityType.BLAZE, 10, 2, 3),
		new SpawnSettings.SpawnEntry(EntityType.ZOMBIFIED_PIGLIN, 5, 4, 4),
		new SpawnSettings.SpawnEntry(EntityType.WITHER_SKELETON, 8, 5, 5),
		new SpawnSettings.SpawnEntry(EntityType.SKELETON, 2, 5, 5),
		new SpawnSettings.SpawnEntry(EntityType.MAGMA_CUBE, 3, 4, 4)
	);

	public NetherFortressFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec, NetherFortressFeature::method_38679);
	}

	protected boolean shouldStartAt(
		ChunkGenerator chunkGenerator,
		BiomeSource biomeSource,
		long l,
		ChunkRandom chunkRandom,
		ChunkPos chunkPos,
		ChunkPos chunkPos2,
		DefaultFeatureConfig defaultFeatureConfig,
		HeightLimitView heightLimitView
	) {
		return chunkRandom.nextInt(5) < 2;
	}

	private static void method_38679(class_6626 arg, DefaultFeatureConfig defaultFeatureConfig, class_6622.class_6623 arg2) {
		if (arg2.validBiome()
			.test(
				arg2.chunkGenerator()
					.getBiomeForNoiseGen(BiomeCoords.fromBlock(arg2.chunkPos().getCenterX()), BiomeCoords.fromBlock(64), BiomeCoords.fromBlock(arg2.chunkPos().getCenterZ()))
			)) {
			NetherFortressGenerator.Start start = new NetherFortressGenerator.Start(arg2.random(), arg2.chunkPos().getOffsetX(2), arg2.chunkPos().getOffsetZ(2));
			arg.addPiece(start);
			start.fillOpenings(start, arg, arg2.random());
			List<StructurePiece> list = start.pieces;

			while (!list.isEmpty()) {
				int i = arg2.random().nextInt(list.size());
				StructurePiece structurePiece = (StructurePiece)list.remove(i);
				structurePiece.fillOpenings(start, arg, arg2.random());
			}

			arg.method_38718(arg2.random(), 48, 70);
		}
	}
}
