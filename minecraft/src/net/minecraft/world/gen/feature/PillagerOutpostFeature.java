package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.class_6834;
import net.minecraft.entity.EntityType;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;

public class PillagerOutpostFeature extends JigsawFeature {
	public static final Pool<SpawnSettings.SpawnEntry> MONSTER_SPAWNS = Pool.of(new SpawnSettings.SpawnEntry(EntityType.PILLAGER, 1, 1, 1));

	public PillagerOutpostFeature(Codec<StructurePoolFeatureConfig> configCodec) {
		super(configCodec, 0, true, true, PillagerOutpostFeature::method_28644);
	}

	private static boolean method_28644(class_6834.class_6835<StructurePoolFeatureConfig> arg) {
		int i = arg.chunkPos().x >> 4;
		int j = arg.chunkPos().z >> 4;
		ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(0L));
		chunkRandom.setSeed((long)(i ^ j << 4) ^ arg.seed());
		chunkRandom.nextInt();
		return chunkRandom.nextInt(5) != 0 ? false : !isVillageNearby(arg.chunkGenerator(), arg.seed(), arg.chunkPos());
	}

	private static boolean isVillageNearby(ChunkGenerator chunkGenerator2, long chunkGenerator, ChunkPos chunkPos) {
		StructureConfig structureConfig = chunkGenerator2.getStructuresConfig().getForType(StructureFeature.VILLAGE);
		if (structureConfig == null) {
			return false;
		} else {
			int i = chunkPos.x;
			int j = chunkPos.z;

			for (int k = i - 10; k <= i + 10; k++) {
				for (int l = j - 10; l <= j + 10; l++) {
					ChunkPos chunkPos2 = StructureFeature.VILLAGE.getStartChunk(structureConfig, chunkGenerator, k, l);
					if (k == chunkPos2.x && l == chunkPos2.z) {
						return true;
					}
				}
			}

			return false;
		}
	}
}
