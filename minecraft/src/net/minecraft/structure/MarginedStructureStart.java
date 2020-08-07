package net.minecraft.structure;

import net.minecraft.util.math.BlockBox;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public abstract class MarginedStructureStart<C extends FeatureConfig> extends StructureStart<C> {
	public MarginedStructureStart(StructureFeature<C> structureFeature, int i, int j, BlockBox blockBox, int k, long l) {
		super(structureFeature, i, j, blockBox, k, l);
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
