package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructureGeneratorFactory;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.util.math.BlockPos;

public class JigsawFeature extends MarginedStructureFeature<StructurePoolFeatureConfig> {
	private static final Random field_36903 = new Random();

	public JigsawFeature(
		Codec<StructurePoolFeatureConfig> codec,
		int structureStartY,
		boolean modifyBoundingBox,
		boolean surface,
		Predicate<StructureGeneratorFactory.Context<StructurePoolFeatureConfig>> predicate
	) {
		this(codec, PoolStructurePiece::new, random -> structureStartY, modifyBoundingBox, surface, predicate, 80);
	}

	public JigsawFeature(
		Codec<StructurePoolFeatureConfig> codec,
		StructurePoolBasedGenerator.PieceFactory pieceFactory,
		Function<Random, Integer> function,
		boolean bl,
		boolean bl2,
		Predicate<StructureGeneratorFactory.Context<StructurePoolFeatureConfig>> predicate,
		int i
	) {
		super(codec, context -> {
			if (!predicate.test(context)) {
				return Optional.empty();
			} else {
				BlockPos blockPos = new BlockPos(context.chunkPos().getStartX(), (Integer)function.apply(field_36903), context.chunkPos().getStartZ());
				StructurePools.initDefaultPools();
				return StructurePoolBasedGenerator.generate(context, pieceFactory, blockPos, bl, bl2, i);
			}
		});
	}
}
