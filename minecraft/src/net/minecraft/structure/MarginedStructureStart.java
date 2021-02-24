package net.minecraft.structure;

import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public abstract class MarginedStructureStart<C extends FeatureConfig> extends StructureStart<C> {
	public MarginedStructureStart(StructureFeature<C> structureFeature, ChunkPos chunkPos, BlockBox blockBox, int i, long l) {
		super(structureFeature, chunkPos, blockBox, i, l);
	}

	@Override
	protected void setBoundingBoxFromChildren() {
		super.setBoundingBoxFromChildren();
		int i = 12;
		this.boundingBox.minX -= 12;
		this.boundingBox.minY -= 12;
		this.boundingBox.minZ -= 12;
		this.boundingBox.maxX += 12;
		this.boundingBox.maxY += 12;
		this.boundingBox.maxZ += 12;
	}
}
