package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SnowmanEntityModel<T extends Entity> extends EntityModel<T> {
	private final Cuboid field_3567;
	private final Cuboid field_3569;
	private final Cuboid field_3568;
	private final Cuboid field_3566;
	private final Cuboid field_3565;

	public SnowmanEntityModel() {
		float f = 4.0F;
		float g = 0.0F;
		this.field_3568 = new Cuboid(this, 0, 0).setTextureSize(64, 64);
		this.field_3568.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, -0.5F);
		this.field_3568.setRotationPoint(0.0F, 4.0F, 0.0F);
		this.field_3566 = new Cuboid(this, 32, 0).setTextureSize(64, 64);
		this.field_3566.addBox(-1.0F, 0.0F, -1.0F, 12, 2, 2, -0.5F);
		this.field_3566.setRotationPoint(0.0F, 6.0F, 0.0F);
		this.field_3565 = new Cuboid(this, 32, 0).setTextureSize(64, 64);
		this.field_3565.addBox(-1.0F, 0.0F, -1.0F, 12, 2, 2, -0.5F);
		this.field_3565.setRotationPoint(0.0F, 6.0F, 0.0F);
		this.field_3567 = new Cuboid(this, 0, 16).setTextureSize(64, 64);
		this.field_3567.addBox(-5.0F, -10.0F, -5.0F, 10, 10, 10, -0.5F);
		this.field_3567.setRotationPoint(0.0F, 13.0F, 0.0F);
		this.field_3569 = new Cuboid(this, 0, 36).setTextureSize(64, 64);
		this.field_3569.addBox(-6.0F, -12.0F, -6.0F, 12, 12, 12, -0.5F);
		this.field_3569.setRotationPoint(0.0F, 24.0F, 0.0F);
	}

	@Override
	public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
		super.setAngles(entity, f, g, h, i, j, k);
		this.field_3568.yaw = i * (float) (Math.PI / 180.0);
		this.field_3568.pitch = j * (float) (Math.PI / 180.0);
		this.field_3567.yaw = i * (float) (Math.PI / 180.0) * 0.25F;
		float l = MathHelper.sin(this.field_3567.yaw);
		float m = MathHelper.cos(this.field_3567.yaw);
		this.field_3566.roll = 1.0F;
		this.field_3565.roll = -1.0F;
		this.field_3566.yaw = 0.0F + this.field_3567.yaw;
		this.field_3565.yaw = (float) Math.PI + this.field_3567.yaw;
		this.field_3566.rotationPointX = m * 5.0F;
		this.field_3566.rotationPointZ = -l * 5.0F;
		this.field_3565.rotationPointX = -m * 5.0F;
		this.field_3565.rotationPointZ = l * 5.0F;
	}

	@Override
	public void render(T entity, float f, float g, float h, float i, float j, float k) {
		this.setAngles(entity, f, g, h, i, j, k);
		this.field_3567.render(k);
		this.field_3569.render(k);
		this.field_3568.render(k);
		this.field_3566.render(k);
		this.field_3565.render(k);
	}

	public Cuboid method_2834() {
		return this.field_3568;
	}
}
