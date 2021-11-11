package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.structure.PostPlacementProcessor;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.util.math.BlockBox;

public abstract class MarginedStructureFeature<C extends FeatureConfig> extends StructureFeature<C> {
	public MarginedStructureFeature(Codec<C> codec, StructurePiecesGenerator<C> structurePiecesGenerator) {
		super(codec, structurePiecesGenerator);
	}

	public MarginedStructureFeature(Codec<C> codec, StructurePiecesGenerator<C> structurePiecesGenerator, PostPlacementProcessor postPlacementProcessor) {
		super(codec, structurePiecesGenerator, postPlacementProcessor);
	}

	@Override
	public BlockBox calculateBoundingBox(BlockBox box) {
		return super.calculateBoundingBox(box).expand(12);
	}
}
