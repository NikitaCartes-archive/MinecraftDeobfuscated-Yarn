package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.class_6834;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.util.math.BlockPos;

public class JigsawFeature extends MarginedStructureFeature<StructurePoolFeatureConfig> {
	public JigsawFeature(
		Codec<StructurePoolFeatureConfig> codec,
		int structureStartY,
		boolean modifyBoundingBox,
		boolean surface,
		Predicate<class_6834.class_6835<StructurePoolFeatureConfig>> predicate
	) {
		super(codec, arg -> {
			if (!predicate.test(arg)) {
				return Optional.empty();
			} else {
				BlockPos blockPos = new BlockPos(arg.chunkPos().getStartX(), structureStartY, arg.chunkPos().getStartZ());
				StructurePools.initDefaultPools();
				return StructurePoolBasedGenerator.generate(arg, PoolStructurePiece::new, blockPos, modifyBoundingBox, surface);
			}
		});
	}
}
