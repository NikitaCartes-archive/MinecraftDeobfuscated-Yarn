package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.util.math.BlockPos;

public class JigsawFeature extends MarginedStructureFeature<StructurePoolFeatureConfig> {
	public JigsawFeature(Codec<StructurePoolFeatureConfig> codec, int structureStartY, boolean modifyBoundingBox, boolean surface) {
		super(
			codec,
			(collector, config, context) -> {
				BlockPos blockPos = new BlockPos(context.chunkPos().getStartX(), structureStartY, context.chunkPos().getStartZ());
				StructurePools.initDefaultPools();
				StructurePoolBasedGenerator.generate(
					context.registryManager(),
					config,
					PoolStructurePiece::new,
					context.chunkGenerator(),
					context.structureManager(),
					blockPos,
					collector,
					context.random(),
					modifyBoundingBox,
					surface,
					context.world(),
					context.biomeLimit()
				);
			}
		);
	}
}
