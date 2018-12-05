package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.model.Model;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ShulkerEntityModel extends Model {
	private final Cuboid field_3553;
	private final Cuboid field_3555;
	private final Cuboid field_3554;

	public ShulkerEntityModel() {
		this.textureHeight = 64;
		this.textureWidth = 64;
		this.field_3555 = new Cuboid(this);
		this.field_3553 = new Cuboid(this);
		this.field_3554 = new Cuboid(this);
		this.field_3555.setTextureOffset(0, 0).addBox(-8.0F, -16.0F, -8.0F, 16, 12, 16);
		this.field_3555.setRotationPoint(0.0F, 24.0F, 0.0F);
		this.field_3553.setTextureOffset(0, 28).addBox(-8.0F, -8.0F, -8.0F, 16, 8, 16);
		this.field_3553.setRotationPoint(0.0F, 24.0F, 0.0F);
		this.field_3554.setTextureOffset(0, 52).addBox(-3.0F, 0.0F, -3.0F, 6, 6, 6);
		this.field_3554.setRotationPoint(0.0F, 12.0F, 0.0F);
	}

	@Override
	public void setRotationAngles(float f, float g, float h, float i, float j, float k, Entity entity) {
		ShulkerEntity shulkerEntity = (ShulkerEntity)entity;
		float l = h - (float)shulkerEntity.age;
		float m = (0.5F + shulkerEntity.method_7116(l)) * (float) Math.PI;
		float n = -1.0F + MathHelper.sin(m);
		float o = 0.0F;
		if (m > (float) Math.PI) {
			o = MathHelper.sin(h * 0.1F) * 0.7F;
		}

		this.field_3555.setRotationPoint(0.0F, 16.0F + MathHelper.sin(m) * 8.0F + o, 0.0F);
		if (shulkerEntity.method_7116(l) > 0.3F) {
			this.field_3555.yaw = n * n * n * n * (float) Math.PI * 0.125F;
		} else {
			this.field_3555.yaw = 0.0F;
		}

		this.field_3554.pitch = j * (float) (Math.PI / 180.0);
		this.field_3554.yaw = i * (float) (Math.PI / 180.0);
	}

	@Override
	public void render(Entity entity, float f, float g, float h, float i, float j, float k) {
		this.field_3553.render(k);
		this.field_3555.render(k);
	}

	public Cuboid method_2831() {
		return this.field_3553;
	}

	public Cuboid method_2829() {
		return this.field_3555;
	}

	public Cuboid method_2830() {
		return this.field_3554;
	}
}
