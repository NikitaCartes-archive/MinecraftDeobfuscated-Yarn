package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.structure.StructureGeneratorFactory;
import net.minecraft.structure.StructureSetKeys;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;

public class PillagerOutpostFeature extends JigsawFeature {
	public PillagerOutpostFeature(Codec<StructurePoolFeatureConfig> configCodec) {
		super(configCodec, 0, true, true, PillagerOutpostFeature::canGenerate);
	}

	private static boolean canGenerate(StructureGeneratorFactory.Context<StructurePoolFeatureConfig> context) {
		ChunkPos chunkPos = context.chunkPos();
		int i = chunkPos.x >> 4;
		int j = chunkPos.z >> 4;
		ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(0L));
		chunkRandom.setSeed((long)(i ^ j << 4) ^ context.seed());
		chunkRandom.nextInt();
		return chunkRandom.nextInt(5) != 0 ? false : !context.chunkGenerator().method_41053(StructureSetKeys.VILLAGES, chunkPos.x, chunkPos.z, 10);
	}
}
