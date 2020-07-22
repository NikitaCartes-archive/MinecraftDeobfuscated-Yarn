package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.decorator.DecoratorContext;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class DecoratedFeature extends Feature<DecoratedFeatureConfig> {
	public DecoratedFeature(Codec<DecoratedFeatureConfig> codec) {
		super(codec);
	}

	public boolean generate(
		StructureWorldAccess structureWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, DecoratedFeatureConfig decoratedFeatureConfig
	) {
		MutableBoolean mutableBoolean = new MutableBoolean();
		decoratedFeatureConfig.decorator.getPositions(new DecoratorContext(structureWorldAccess, chunkGenerator), random, blockPos).forEach(blockPosx -> {
			if (((ConfiguredFeature)decoratedFeatureConfig.feature.get()).generate(structureWorldAccess, chunkGenerator, random, blockPosx)) {
				mutableBoolean.setTrue();
			}
		});
		return mutableBoolean.isTrue();
	}

	public String toString() {
		return String.format("< %s [%s] >", this.getClass().getSimpleName(), Registry.FEATURE.getId(this));
	}
}
