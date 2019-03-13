package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.model.Model;

@Environment(EnvType.CLIENT)
public class ChestEntityModel extends Model {
	protected Cuboid field_3330 = new Cuboid(this, 0, 0).setTextureSize(64, 64);
	protected Cuboid field_3332;
	protected Cuboid field_3331;

	public ChestEntityModel() {
		this.field_3330.addBox(0.0F, -5.0F, -14.0F, 14, 5, 14, 0.0F);
		this.field_3330.rotationPointX = 1.0F;
		this.field_3330.rotationPointY = 7.0F;
		this.field_3330.rotationPointZ = 15.0F;
		this.field_3331 = new Cuboid(this, 0, 0).setTextureSize(64, 64);
		this.field_3331.addBox(-1.0F, -2.0F, -15.0F, 2, 4, 1, 0.0F);
		this.field_3331.rotationPointX = 8.0F;
		this.field_3331.rotationPointY = 7.0F;
		this.field_3331.rotationPointZ = 15.0F;
		this.field_3332 = new Cuboid(this, 0, 19).setTextureSize(64, 64);
		this.field_3332.addBox(0.0F, 0.0F, 0.0F, 14, 10, 14, 0.0F);
		this.field_3332.rotationPointX = 1.0F;
		this.field_3332.rotationPointY = 6.0F;
		this.field_3332.rotationPointZ = 1.0F;
	}

	public void method_2799() {
		this.field_3331.pitch = this.field_3330.pitch;
		this.field_3330.render(0.0625F);
		this.field_3331.render(0.0625F);
		this.field_3332.render(0.0625F);
	}

	public Cuboid method_2798() {
		return this.field_3330;
	}
}
