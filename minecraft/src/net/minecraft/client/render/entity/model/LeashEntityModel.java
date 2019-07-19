package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class LeashEntityModel<T extends Entity> extends EntityModel<T> {
	private final ModelPart field_3431;

	public LeashEntityModel() {
		this(0, 0, 32, 32);
	}

	public LeashEntityModel(int i, int j, int k, int l) {
		this.textureWidth = k;
		this.textureHeight = l;
		this.field_3431 = new ModelPart(this, i, j);
		this.field_3431.addCuboid(-3.0F, -6.0F, -3.0F, 6, 8, 6, 0.0F);
		this.field_3431.setPivot(0.0F, 0.0F, 0.0F);
	}

	@Override
	public void render(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
		this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
		this.field_3431.render(scale);
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
		super.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
		this.field_3431.yaw = headYaw * (float) (Math.PI / 180.0);
		this.field_3431.pitch = headPitch * (float) (Math.PI / 180.0);
	}
}
