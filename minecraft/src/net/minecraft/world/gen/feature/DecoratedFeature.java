package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class DecoratedFeature extends Feature<DecoratedFeatureConfig> {
	public DecoratedFeature(Codec<DecoratedFeatureConfig> codec) {
		super(codec);
	}

	public boolean generate(
		ServerWorldAccess serverWorldAccess,
		StructureAccessor structureAccessor,
		ChunkGenerator chunkGenerator,
		Random random,
		BlockPos blockPos,
		DecoratedFeatureConfig decoratedFeatureConfig
	) {
		return decoratedFeatureConfig.decorator.generate(serverWorldAccess, structureAccessor, chunkGenerator, random, blockPos, decoratedFeatureConfig.feature);
	}

	public String toString() {
		return String.format("< %s [%s] >", this.getClass().getSimpleName(), Registry.FEATURE.getId(this));
	}
}
