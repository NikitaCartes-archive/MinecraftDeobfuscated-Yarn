package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SalmonEntityModel<T extends Entity> extends EntityModel<T> {
	private final Cuboid field_3546;
	private final Cuboid field_3548;
	private final Cuboid field_3547;
	private final Cuboid field_3545;
	private final Cuboid field_3543;
	private final Cuboid field_3549;
	private final Cuboid field_3542;
	private final Cuboid field_3544;

	public SalmonEntityModel() {
		this.textureWidth = 32;
		this.textureHeight = 32;
		int i = 20;
		this.field_3546 = new Cuboid(this, 0, 0);
		this.field_3546.addBox(-1.5F, -2.5F, 0.0F, 3, 5, 8);
		this.field_3546.setRotationPoint(0.0F, 20.0F, 0.0F);
		this.field_3548 = new Cuboid(this, 0, 13);
		this.field_3548.addBox(-1.5F, -2.5F, 0.0F, 3, 5, 8);
		this.field_3548.setRotationPoint(0.0F, 20.0F, 8.0F);
		this.field_3547 = new Cuboid(this, 22, 0);
		this.field_3547.addBox(-1.0F, -2.0F, -3.0F, 2, 4, 3);
		this.field_3547.setRotationPoint(0.0F, 20.0F, 0.0F);
		this.field_3549 = new Cuboid(this, 20, 10);
		this.field_3549.addBox(0.0F, -2.5F, 0.0F, 0, 5, 6);
		this.field_3549.setRotationPoint(0.0F, 0.0F, 8.0F);
		this.field_3548.addChild(this.field_3549);
		this.field_3545 = new Cuboid(this, 2, 1);
		this.field_3545.addBox(0.0F, 0.0F, 0.0F, 0, 2, 3);
		this.field_3545.setRotationPoint(0.0F, -4.5F, 5.0F);
		this.field_3546.addChild(this.field_3545);
		this.field_3543 = new Cuboid(this, 0, 2);
		this.field_3543.addBox(0.0F, 0.0F, 0.0F, 0, 2, 4);
		this.field_3543.setRotationPoint(0.0F, -4.5F, -1.0F);
		this.field_3548.addChild(this.field_3543);
		this.field_3542 = new Cuboid(this, -4, 0);
		this.field_3542.addBox(-2.0F, 0.0F, 0.0F, 2, 0, 2);
		this.field_3542.setRotationPoint(-1.5F, 21.5F, 0.0F);
		this.field_3542.roll = (float) (-Math.PI / 4);
		this.field_3544 = new Cuboid(this, 0, 0);
		this.field_3544.addBox(0.0F, 0.0F, 0.0F, 2, 0, 2);
		this.field_3544.setRotationPoint(1.5F, 21.5F, 0.0F);
		this.field_3544.roll = (float) (Math.PI / 4);
	}

	@Override
	public void render(T entity, float f, float g, float h, float i, float j, float k) {
		this.setAngles(entity, f, g, h, i, j, k);
		this.field_3546.render(k);
		this.field_3548.render(k);
		this.field_3547.render(k);
		this.field_3542.render(k);
		this.field_3544.render(k);
	}

	@Override
	public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
		float l = 1.0F;
		float m = 1.0F;
		if (!entity.isInsideWater()) {
			l = 1.3F;
			m = 1.7F;
		}

		this.field_3548.yaw = -l * 0.25F * MathHelper.sin(m * 0.6F * h);
	}
}
