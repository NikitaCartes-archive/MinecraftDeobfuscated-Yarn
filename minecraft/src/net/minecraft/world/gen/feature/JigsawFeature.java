package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.structure.MarginedStructureStart;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.util.math.BlockPos;

public class JigsawFeature extends MarginedStructureStart<StructurePoolFeatureConfig> {
	public JigsawFeature(Codec<StructurePoolFeatureConfig> codec, int structureStartY, boolean modifyBoundingBox, boolean surface) {
		super(
			codec,
			(arg, structurePoolFeatureConfig, arg2) -> {
				BlockPos blockPos = new BlockPos(arg2.chunkPos().getStartX(), structureStartY, arg2.chunkPos().getStartZ());
				StructurePools.initDefaultPools();
				StructurePoolBasedGenerator.generate(
					arg2.registryAccess(),
					structurePoolFeatureConfig,
					PoolStructurePiece::new,
					arg2.chunkGenerator(),
					arg2.structureManager(),
					blockPos,
					arg,
					arg2.random(),
					modifyBoundingBox,
					surface,
					arg2.heightAccessor(),
					arg2.validBiome()
				);
			}
		);
	}
}
