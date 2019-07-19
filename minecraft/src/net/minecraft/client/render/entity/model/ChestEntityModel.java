package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;

@Environment(EnvType.CLIENT)
public class ChestEntityModel extends Model {
	protected ModelPart lid = new ModelPart(this, 0, 0).setTextureSize(64, 64);
	protected ModelPart base;
	protected ModelPart hatch;

	public ChestEntityModel() {
		this.lid.addCuboid(0.0F, -5.0F, -14.0F, 14, 5, 14, 0.0F);
		this.lid.pivotX = 1.0F;
		this.lid.pivotY = 7.0F;
		this.lid.pivotZ = 15.0F;
		this.hatch = new ModelPart(this, 0, 0).setTextureSize(64, 64);
		this.hatch.addCuboid(-1.0F, -2.0F, -15.0F, 2, 4, 1, 0.0F);
		this.hatch.pivotX = 8.0F;
		this.hatch.pivotY = 7.0F;
		this.hatch.pivotZ = 15.0F;
		this.base = new ModelPart(this, 0, 19).setTextureSize(64, 64);
		this.base.addCuboid(0.0F, 0.0F, 0.0F, 14, 10, 14, 0.0F);
		this.base.pivotX = 1.0F;
		this.base.pivotY = 6.0F;
		this.base.pivotZ = 1.0F;
	}

	public void method_2799() {
		this.hatch.pitch = this.lid.pitch;
		this.lid.render(0.0625F);
		this.hatch.render(0.0625F);
		this.base.render(0.0625F);
	}

	public ModelPart method_2798() {
		return this.lid;
	}
}
