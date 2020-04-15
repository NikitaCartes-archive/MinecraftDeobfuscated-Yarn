package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public abstract class AbstractTempleFeature<C extends FeatureConfig> extends StructureFeature<C> {
	public AbstractTempleFeature(Function<Dynamic<?>, ? extends C> function) {
		super(function);
	}

	@Override
	protected int getSpacing(DimensionType dimensionType, ChunkGeneratorConfig chunkGeneratorConfig) {
		return chunkGeneratorConfig.getTempleDistance();
	}

	@Override
	protected int getSeparation(DimensionType dimensionType, ChunkGeneratorConfig chunkGenerationConfig) {
		return chunkGenerationConfig.getTempleSeparation();
	}

	@Override
	protected abstract int getSeedModifier(ChunkGeneratorConfig chunkGeneratorConfig);
}
