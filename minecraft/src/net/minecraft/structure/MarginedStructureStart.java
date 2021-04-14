package net.minecraft.structure;

import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public abstract class MarginedStructureStart<C extends FeatureConfig> extends StructureStart<C> {
	public MarginedStructureStart(StructureFeature<C> structureFeature, ChunkPos chunkPos, int i, long l) {
		super(structureFeature, chunkPos, i, l);
	}

	@Override
	protected BlockBox calculateBoundingBox() {
		return super.calculateBoundingBox().expand(12);
	}
}
