package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class ShulkerBulletEntityModel<T extends Entity> extends EntityModel<T> {
	private final ModelPart field_3556;

	public ShulkerBulletEntityModel() {
		this.textureWidth = 64;
		this.textureHeight = 32;
		this.field_3556 = new ModelPart(this);
		this.field_3556.setTextureOffset(0, 0).addCuboid(-4.0F, -4.0F, -1.0F, 8, 8, 2, 0.0F);
		this.field_3556.setTextureOffset(0, 10).addCuboid(-1.0F, -4.0F, -4.0F, 2, 8, 8, 0.0F);
		this.field_3556.setTextureOffset(20, 0).addCuboid(-4.0F, -1.0F, -4.0F, 8, 2, 8, 0.0F);
		this.field_3556.setPivot(0.0F, 0.0F, 0.0F);
	}

	@Override
	public void render(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
		this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
		this.field_3556.render(scale);
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
		super.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
		this.field_3556.yaw = headYaw * (float) (Math.PI / 180.0);
		this.field_3556.pitch = headPitch * (float) (Math.PI / 180.0);
	}
}
