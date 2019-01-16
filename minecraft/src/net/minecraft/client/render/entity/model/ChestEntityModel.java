package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.model.Model;

@Environment(EnvType.CLIENT)
public class ChestEntityModel extends Model {
	protected Cuboid lid = new Cuboid(this, 0, 0).setTextureSize(64, 64);
	protected Cuboid base;
	protected Cuboid hatch;

	public ChestEntityModel() {
		this.lid.addBox(0.0F, -5.0F, -14.0F, 14, 5, 14, 0.0F);
		this.lid.rotationPointX = 1.0F;
		this.lid.rotationPointY = 7.0F;
		this.lid.rotationPointZ = 15.0F;
		this.hatch = new Cuboid(this, 0, 0).setTextureSize(64, 64);
		this.hatch.addBox(-1.0F, -2.0F, -15.0F, 2, 4, 1, 0.0F);
		this.hatch.rotationPointX = 8.0F;
		this.hatch.rotationPointY = 7.0F;
		this.hatch.rotationPointZ = 15.0F;
		this.base = new Cuboid(this, 0, 19).setTextureSize(64, 64);
		this.base.addBox(0.0F, 0.0F, 0.0F, 14, 10, 14, 0.0F);
		this.base.rotationPointX = 1.0F;
		this.base.rotationPointY = 6.0F;
		this.base.rotationPointZ = 1.0F;
	}

	public void method_2799() {
		this.hatch.pitch = this.lid.pitch;
		this.lid.render(0.0625F);
		this.hatch.render(0.0625F);
		this.base.render(0.0625F);
	}

	public Cuboid method_2798() {
		return this.lid;
	}
}
