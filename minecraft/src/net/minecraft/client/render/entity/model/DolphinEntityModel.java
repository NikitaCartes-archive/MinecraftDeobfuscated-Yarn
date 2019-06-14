package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class DolphinEntityModel<T extends Entity> extends EntityModel<T> {
	private final Cuboid field_4656;
	private final Cuboid field_4658;
	private final Cuboid field_4657;
	private final Cuboid field_4655;

	public DolphinEntityModel() {
		this.textureWidth = 64;
		this.textureHeight = 64;
		float f = 18.0F;
		float g = -8.0F;
		this.field_4658 = new Cuboid(this, 22, 0);
		this.field_4658.addBox(-4.0F, -7.0F, 0.0F, 8, 7, 13);
		this.field_4658.setRotationPoint(0.0F, 22.0F, -5.0F);
		Cuboid cuboid = new Cuboid(this, 51, 0);
		cuboid.addBox(-0.5F, 0.0F, 8.0F, 1, 4, 5);
		cuboid.pitch = (float) (Math.PI / 3);
		this.field_4658.addChild(cuboid);
		Cuboid cuboid2 = new Cuboid(this, 48, 20);
		cuboid2.mirror = true;
		cuboid2.addBox(-0.5F, -4.0F, 0.0F, 1, 4, 7);
		cuboid2.setRotationPoint(2.0F, -2.0F, 4.0F);
		cuboid2.pitch = (float) (Math.PI / 3);
		cuboid2.roll = (float) (Math.PI * 2.0 / 3.0);
		this.field_4658.addChild(cuboid2);
		Cuboid cuboid3 = new Cuboid(this, 48, 20);
		cuboid3.addBox(-0.5F, -4.0F, 0.0F, 1, 4, 7);
		cuboid3.setRotationPoint(-2.0F, -2.0F, 4.0F);
		cuboid3.pitch = (float) (Math.PI / 3);
		cuboid3.roll = (float) (-Math.PI * 2.0 / 3.0);
		this.field_4658.addChild(cuboid3);
		this.field_4657 = new Cuboid(this, 0, 19);
		this.field_4657.addBox(-2.0F, -2.5F, 0.0F, 4, 5, 11);
		this.field_4657.setRotationPoint(0.0F, -2.5F, 11.0F);
		this.field_4657.pitch = -0.10471976F;
		this.field_4658.addChild(this.field_4657);
		this.field_4655 = new Cuboid(this, 19, 20);
		this.field_4655.addBox(-5.0F, -0.5F, 0.0F, 10, 1, 6);
		this.field_4655.setRotationPoint(0.0F, 0.0F, 9.0F);
		this.field_4655.pitch = 0.0F;
		this.field_4657.addChild(this.field_4655);
		this.field_4656 = new Cuboid(this, 0, 0);
		this.field_4656.addBox(-4.0F, -3.0F, -3.0F, 8, 7, 6);
		this.field_4656.setRotationPoint(0.0F, -4.0F, -3.0F);
		Cuboid cuboid4 = new Cuboid(this, 0, 13);
		cuboid4.addBox(-1.0F, 2.0F, -7.0F, 2, 2, 4);
		this.field_4656.addChild(cuboid4);
		this.field_4658.addChild(this.field_4656);
	}

	@Override
	public void render(T entity, float f, float g, float h, float i, float j, float k) {
		this.field_4658.render(k);
	}

	@Override
	public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
		this.field_4658.pitch = j * (float) (Math.PI / 180.0);
		this.field_4658.yaw = i * (float) (Math.PI / 180.0);
		if (Entity.method_17996(entity.method_18798()) > 1.0E-7) {
			this.field_4658.pitch = this.field_4658.pitch + -0.05F + -0.05F * MathHelper.cos(h * 0.3F);
			this.field_4657.pitch = -0.1F * MathHelper.cos(h * 0.3F);
			this.field_4655.pitch = -0.2F * MathHelper.cos(h * 0.3F);
		}
	}
}
