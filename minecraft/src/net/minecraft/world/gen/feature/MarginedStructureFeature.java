package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.structure.PostPlacementProcessor;
import net.minecraft.structure.StructureGeneratorFactory;
import net.minecraft.util.math.BlockBox;

public abstract class MarginedStructureFeature<C extends FeatureConfig> extends StructureFeature<C> {
	public MarginedStructureFeature(Codec<C> codec, StructureGeneratorFactory<C> structureGeneratorFactory) {
		super(codec, structureGeneratorFactory);
	}

	public MarginedStructureFeature(Codec<C> codec, StructureGeneratorFactory<C> structureGeneratorFactory, PostPlacementProcessor postPlacementProcessor) {
		super(codec, structureGeneratorFactory, postPlacementProcessor);
	}

	@Override
	public BlockBox calculateBoundingBox(BlockBox box) {
		return super.calculateBoundingBox(box).expand(12);
	}
}
