package net.minecraft;

import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.StructureFeature;

public abstract class class_4183 extends StructureStart {
	public class_4183(StructureFeature<?> structureFeature, int i, int j, Biome biome, MutableIntBoundingBox mutableIntBoundingBox, int k, long l) {
		super(structureFeature, i, j, biome, mutableIntBoundingBox, k, l);
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
