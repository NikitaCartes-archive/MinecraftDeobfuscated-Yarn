package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;

@Environment(EnvType.CLIENT)
public class ChestDoubleEntityModel extends ChestEntityModel {
	public ChestDoubleEntityModel() {
		this.field_3330 = new Cuboid(this, 0, 0).setTextureSize(128, 64);
		this.field_3330.addBox(0.0F, -5.0F, -14.0F, 30, 5, 14, 0.0F);
		this.field_3330.rotationPointX = 1.0F;
		this.field_3330.rotationPointY = 7.0F;
		this.field_3330.rotationPointZ = 15.0F;
		this.field_3331 = new Cuboid(this, 0, 0).setTextureSize(128, 64);
		this.field_3331.addBox(-1.0F, -2.0F, -15.0F, 2, 4, 1, 0.0F);
		this.field_3331.rotationPointX = 16.0F;
		this.field_3331.rotationPointY = 7.0F;
		this.field_3331.rotationPointZ = 15.0F;
		this.field_3332 = new Cuboid(this, 0, 19).setTextureSize(128, 64);
		this.field_3332.addBox(0.0F, 0.0F, 0.0F, 30, 10, 14, 0.0F);
		this.field_3332.rotationPointX = 1.0F;
		this.field_3332.rotationPointY = 6.0F;
		this.field_3332.rotationPointZ = 1.0F;
	}
}
