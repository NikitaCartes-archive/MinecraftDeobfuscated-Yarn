package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.model.Model;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class LargePufferfishEntityModel extends Model {
	private final Cuboid field_3493;
	private final Cuboid field_3499;
	private final Cuboid field_3494;
	private final Cuboid field_3490;
	private final Cuboid field_3496;
	private final Cuboid field_3495;
	private final Cuboid field_3489;
	private final Cuboid field_3497;
	private final Cuboid field_3491;
	private final Cuboid field_3487;
	private final Cuboid field_3492;
	private final Cuboid field_3498;
	private final Cuboid field_3488;

	public LargePufferfishEntityModel() {
		this.textureWidth = 32;
		this.textureHeight = 32;
		int i = 22;
		this.field_3493 = new Cuboid(this, 0, 0);
		this.field_3493.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8);
		this.field_3493.setRotationPoint(0.0F, 22.0F, 0.0F);
		this.field_3499 = new Cuboid(this, 24, 0);
		this.field_3499.addBox(-2.0F, 0.0F, -1.0F, 2, 1, 2);
		this.field_3499.setRotationPoint(-4.0F, 15.0F, -2.0F);
		this.field_3494 = new Cuboid(this, 24, 3);
		this.field_3494.addBox(0.0F, 0.0F, -1.0F, 2, 1, 2);
		this.field_3494.setRotationPoint(4.0F, 15.0F, -2.0F);
		this.field_3490 = new Cuboid(this, 15, 17);
		this.field_3490.addBox(-4.0F, -1.0F, 0.0F, 8, 1, 0);
		this.field_3490.setRotationPoint(0.0F, 14.0F, -4.0F);
		this.field_3490.pitch = (float) (Math.PI / 4);
		this.field_3496 = new Cuboid(this, 14, 16);
		this.field_3496.addBox(-4.0F, -1.0F, 0.0F, 8, 1, 1);
		this.field_3496.setRotationPoint(0.0F, 14.0F, 0.0F);
		this.field_3495 = new Cuboid(this, 23, 18);
		this.field_3495.addBox(-4.0F, -1.0F, 0.0F, 8, 1, 0);
		this.field_3495.setRotationPoint(0.0F, 14.0F, 4.0F);
		this.field_3495.pitch = (float) (-Math.PI / 4);
		this.field_3489 = new Cuboid(this, 5, 17);
		this.field_3489.addBox(-1.0F, -8.0F, 0.0F, 1, 8, 0);
		this.field_3489.setRotationPoint(-4.0F, 22.0F, -4.0F);
		this.field_3489.yaw = (float) (-Math.PI / 4);
		this.field_3497 = new Cuboid(this, 1, 17);
		this.field_3497.addBox(0.0F, -8.0F, 0.0F, 1, 8, 0);
		this.field_3497.setRotationPoint(4.0F, 22.0F, -4.0F);
		this.field_3497.yaw = (float) (Math.PI / 4);
		this.field_3491 = new Cuboid(this, 15, 20);
		this.field_3491.addBox(-4.0F, 0.0F, 0.0F, 8, 1, 0);
		this.field_3491.setRotationPoint(0.0F, 22.0F, -4.0F);
		this.field_3491.pitch = (float) (-Math.PI / 4);
		this.field_3492 = new Cuboid(this, 15, 20);
		this.field_3492.addBox(-4.0F, 0.0F, 0.0F, 8, 1, 0);
		this.field_3492.setRotationPoint(0.0F, 22.0F, 0.0F);
		this.field_3487 = new Cuboid(this, 15, 20);
		this.field_3487.addBox(-4.0F, 0.0F, 0.0F, 8, 1, 0);
		this.field_3487.setRotationPoint(0.0F, 22.0F, 4.0F);
		this.field_3487.pitch = (float) (Math.PI / 4);
		this.field_3498 = new Cuboid(this, 9, 17);
		this.field_3498.addBox(-1.0F, -8.0F, 0.0F, 1, 8, 0);
		this.field_3498.setRotationPoint(-4.0F, 22.0F, 4.0F);
		this.field_3498.yaw = (float) (Math.PI / 4);
		this.field_3488 = new Cuboid(this, 9, 17);
		this.field_3488.addBox(0.0F, -8.0F, 0.0F, 1, 8, 0);
		this.field_3488.setRotationPoint(4.0F, 22.0F, 4.0F);
		this.field_3488.yaw = (float) (-Math.PI / 4);
	}

	@Override
	public void render(Entity entity, float f, float g, float h, float i, float j, float k) {
		this.setRotationAngles(f, g, h, i, j, k, entity);
		this.field_3493.render(k);
		this.field_3499.render(k);
		this.field_3494.render(k);
		this.field_3490.render(k);
		this.field_3496.render(k);
		this.field_3495.render(k);
		this.field_3489.render(k);
		this.field_3497.render(k);
		this.field_3491.render(k);
		this.field_3492.render(k);
		this.field_3487.render(k);
		this.field_3498.render(k);
		this.field_3488.render(k);
	}

	@Override
	public void setRotationAngles(float f, float g, float h, float i, float j, float k, Entity entity) {
		this.field_3499.roll = -0.2F + 0.4F * MathHelper.sin(h * 0.2F);
		this.field_3494.roll = 0.2F - 0.4F * MathHelper.sin(h * 0.2F);
	}
}
