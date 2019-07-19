package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class SquidEntityModel<T extends Entity> extends EntityModel<T> {
	private final ModelPart field_3575;
	private final ModelPart[] field_3574 = new ModelPart[8];

	public SquidEntityModel() {
		int i = -16;
		this.field_3575 = new ModelPart(this, 0, 0);
		this.field_3575.addCuboid(-6.0F, -8.0F, -6.0F, 12, 16, 12);
		this.field_3575.pivotY += 8.0F;

		for (int j = 0; j < this.field_3574.length; j++) {
			this.field_3574[j] = new ModelPart(this, 48, 0);
			double d = (double)j * Math.PI * 2.0 / (double)this.field_3574.length;
			float f = (float)Math.cos(d) * 5.0F;
			float g = (float)Math.sin(d) * 5.0F;
			this.field_3574[j].addCuboid(-1.0F, 0.0F, -1.0F, 2, 18, 2);
			this.field_3574[j].pivotX = f;
			this.field_3574[j].pivotZ = g;
			this.field_3574[j].pivotY = 15.0F;
			d = (double)j * Math.PI * -2.0 / (double)this.field_3574.length + (Math.PI / 2);
			this.field_3574[j].yaw = (float)d;
		}
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
		for (ModelPart modelPart : this.field_3574) {
			modelPart.pitch = age;
		}
	}

	@Override
	public void render(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
		this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
		this.field_3575.render(scale);

		for (ModelPart modelPart : this.field_3574) {
			modelPart.render(scale);
		}
	}
}
