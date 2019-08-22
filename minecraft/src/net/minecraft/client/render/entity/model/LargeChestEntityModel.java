package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;

@Environment(EnvType.CLIENT)
public class LargeChestEntityModel extends ChestEntityModel {
	public LargeChestEntityModel() {
		this.lid = new ModelPart(this, 0, 0).setTextureSize(128, 64);
		this.lid.addCuboid(0.0F, -5.0F, -14.0F, 30, 5, 14, 0.0F);
		this.lid.rotationPointX = 1.0F;
		this.lid.rotationPointY = 7.0F;
		this.lid.rotationPointZ = 15.0F;
		this.hatch = new ModelPart(this, 0, 0).setTextureSize(128, 64);
		this.hatch.addCuboid(-1.0F, -2.0F, -15.0F, 2, 4, 1, 0.0F);
		this.hatch.rotationPointX = 16.0F;
		this.hatch.rotationPointY = 7.0F;
		this.hatch.rotationPointZ = 15.0F;
		this.base = new ModelPart(this, 0, 19).setTextureSize(128, 64);
		this.base.addCuboid(0.0F, 0.0F, 0.0F, 30, 10, 14, 0.0F);
		this.base.rotationPointX = 1.0F;
		this.base.rotationPointY = 6.0F;
		this.base.rotationPointZ = 1.0F;
	}
}
