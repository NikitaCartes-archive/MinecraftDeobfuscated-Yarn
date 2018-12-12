package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class PhantomEntityModel<T extends Entity> extends EntityModel<T> {
	private final Cuboid field_3475;
	private final Cuboid field_3477;
	private final Cuboid field_3476;
	private final Cuboid field_3474;
	private final Cuboid field_3472;
	private final Cuboid field_3478;
	private final Cuboid field_3471;
	private final Cuboid field_3473;

	public PhantomEntityModel() {
		this.textureWidth = 64;
		this.textureHeight = 64;
		this.field_3475 = new Cuboid(this, 0, 8);
		this.field_3475.addBox(-3.0F, -2.0F, -8.0F, 5, 3, 9);
		this.field_3471 = new Cuboid(this, 3, 20);
		this.field_3471.addBox(-2.0F, 0.0F, 0.0F, 3, 2, 6);
		this.field_3471.setRotationPoint(0.0F, -2.0F, 1.0F);
		this.field_3475.addChild(this.field_3471);
		this.field_3473 = new Cuboid(this, 4, 29);
		this.field_3473.addBox(-1.0F, 0.0F, 0.0F, 1, 1, 6);
		this.field_3473.setRotationPoint(0.0F, 0.5F, 6.0F);
		this.field_3471.addChild(this.field_3473);
		this.field_3477 = new Cuboid(this, 23, 12);
		this.field_3477.addBox(0.0F, 0.0F, 0.0F, 6, 2, 9);
		this.field_3477.setRotationPoint(2.0F, -2.0F, -8.0F);
		this.field_3476 = new Cuboid(this, 16, 24);
		this.field_3476.addBox(0.0F, 0.0F, 0.0F, 13, 1, 9);
		this.field_3476.setRotationPoint(6.0F, 0.0F, 0.0F);
		this.field_3477.addChild(this.field_3476);
		this.field_3474 = new Cuboid(this, 23, 12);
		this.field_3474.mirror = true;
		this.field_3474.addBox(-6.0F, 0.0F, 0.0F, 6, 2, 9);
		this.field_3474.setRotationPoint(-3.0F, -2.0F, -8.0F);
		this.field_3472 = new Cuboid(this, 16, 24);
		this.field_3472.mirror = true;
		this.field_3472.addBox(-13.0F, 0.0F, 0.0F, 13, 1, 9);
		this.field_3472.setRotationPoint(-6.0F, 0.0F, 0.0F);
		this.field_3474.addChild(this.field_3472);
		this.field_3477.roll = 0.1F;
		this.field_3476.roll = 0.1F;
		this.field_3474.roll = -0.1F;
		this.field_3472.roll = -0.1F;
		this.field_3475.pitch = -0.1F;
		this.field_3478 = new Cuboid(this, 0, 0);
		this.field_3478.addBox(-4.0F, -2.0F, -5.0F, 7, 3, 5);
		this.field_3478.setRotationPoint(0.0F, 1.0F, -7.0F);
		this.field_3478.pitch = 0.2F;
		this.field_3475.addChild(this.field_3478);
		this.field_3475.addChild(this.field_3477);
		this.field_3475.addChild(this.field_3474);
	}

	@Override
	public void render(T entity, float f, float g, float h, float i, float j, float k) {
		this.field_3475.render(k);
	}

	@Override
	public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
		float l = ((float)(entity.getEntityId() * 3) + h) * 0.13F;
		float m = 16.0F;
		this.field_3477.roll = MathHelper.cos(l) * 16.0F * (float) (Math.PI / 180.0);
		this.field_3476.roll = MathHelper.cos(l) * 16.0F * (float) (Math.PI / 180.0);
		this.field_3474.roll = -this.field_3477.roll;
		this.field_3472.roll = -this.field_3476.roll;
		this.field_3471.pitch = -(5.0F + MathHelper.cos(l * 2.0F) * 5.0F) * (float) (Math.PI / 180.0);
		this.field_3473.pitch = -(5.0F + MathHelper.cos(l * 2.0F) * 5.0F) * (float) (Math.PI / 180.0);
	}
}
