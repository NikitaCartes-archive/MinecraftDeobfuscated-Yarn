package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class DecoratedFeature extends Feature<DecoratedFeatureConfig> {
	public DecoratedFeature(Function<Dynamic<?>, ? extends DecoratedFeatureConfig> function) {
		super(function);
	}

	public boolean generate(
		IWorld iWorld,
		StructureAccessor structureAccessor,
		ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator,
		Random random,
		BlockPos blockPos,
		DecoratedFeatureConfig decoratedFeatureConfig
	) {
		return decoratedFeatureConfig.decorator.generate(iWorld, structureAccessor, chunkGenerator, random, blockPos, decoratedFeatureConfig.feature);
	}

	public String toString() {
		return String.format("< %s [%s] >", this.getClass().getSimpleName(), Registry.FEATURE.getId(this));
	}
}
