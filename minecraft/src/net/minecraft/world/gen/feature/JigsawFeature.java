package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructureGeneratorFactory;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.util.math.BlockPos;

public class JigsawFeature extends StructureFeature<StructurePoolFeatureConfig> {
	public JigsawFeature(
		Codec<StructurePoolFeatureConfig> codec,
		int structureStartY,
		boolean modifyBoundingBox,
		boolean surface,
		Predicate<StructureGeneratorFactory.Context<StructurePoolFeatureConfig>> contextPredicate
	) {
		super(codec, context -> {
			if (!contextPredicate.test(context)) {
				return Optional.empty();
			} else {
				BlockPos blockPos = new BlockPos(context.chunkPos().getStartX(), structureStartY, context.chunkPos().getStartZ());
				StructurePools.initDefaultPools();
				return StructurePoolBasedGenerator.generate(context, PoolStructurePiece::new, blockPos, modifyBoundingBox, surface);
			}
		});
	}
}
