package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ChickenEntityModel<T extends Entity> extends EntityModel<T> {
	private final Cuboid field_3344;
	private final Cuboid field_3346;
	private final Cuboid field_3345;
	private final Cuboid field_3343;
	private final Cuboid field_3341;
	private final Cuboid field_3347;
	private final Cuboid field_3340;
	private final Cuboid field_3342;

	public ChickenEntityModel() {
		int i = 16;
		this.field_3344 = new Cuboid(this, 0, 0);
		this.field_3344.addBox(-2.0F, -6.0F, -2.0F, 4, 6, 3, 0.0F);
		this.field_3344.setRotationPoint(0.0F, 15.0F, -4.0F);
		this.field_3340 = new Cuboid(this, 14, 0);
		this.field_3340.addBox(-2.0F, -4.0F, -4.0F, 4, 2, 2, 0.0F);
		this.field_3340.setRotationPoint(0.0F, 15.0F, -4.0F);
		this.field_3342 = new Cuboid(this, 14, 4);
		this.field_3342.addBox(-1.0F, -2.0F, -3.0F, 2, 2, 2, 0.0F);
		this.field_3342.setRotationPoint(0.0F, 15.0F, -4.0F);
		this.field_3346 = new Cuboid(this, 0, 9);
		this.field_3346.addBox(-3.0F, -4.0F, -3.0F, 6, 8, 6, 0.0F);
		this.field_3346.setRotationPoint(0.0F, 16.0F, 0.0F);
		this.field_3345 = new Cuboid(this, 26, 0);
		this.field_3345.addBox(-1.0F, 0.0F, -3.0F, 3, 5, 3);
		this.field_3345.setRotationPoint(-2.0F, 19.0F, 1.0F);
		this.field_3343 = new Cuboid(this, 26, 0);
		this.field_3343.addBox(-1.0F, 0.0F, -3.0F, 3, 5, 3);
		this.field_3343.setRotationPoint(1.0F, 19.0F, 1.0F);
		this.field_3341 = new Cuboid(this, 24, 13);
		this.field_3341.addBox(0.0F, 0.0F, -3.0F, 1, 4, 6);
		this.field_3341.setRotationPoint(-4.0F, 13.0F, 0.0F);
		this.field_3347 = new Cuboid(this, 24, 13);
		this.field_3347.addBox(-1.0F, 0.0F, -3.0F, 1, 4, 6);
		this.field_3347.setRotationPoint(4.0F, 13.0F, 0.0F);
	}

	@Override
	public void render(T entity, float f, float g, float h, float i, float j, float k) {
		this.setAngles(entity, f, g, h, i, j, k);
		if (this.isChild) {
			float l = 2.0F;
			GlStateManager.pushMatrix();
			GlStateManager.translatef(0.0F, 5.0F * k, 2.0F * k);
			this.field_3344.render(k);
			this.field_3340.render(k);
			this.field_3342.render(k);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			GlStateManager.translatef(0.0F, 24.0F * k, 0.0F);
			this.field_3346.render(k);
			this.field_3345.render(k);
			this.field_3343.render(k);
			this.field_3341.render(k);
			this.field_3347.render(k);
			GlStateManager.popMatrix();
		} else {
			this.field_3344.render(k);
			this.field_3340.render(k);
			this.field_3342.render(k);
			this.field_3346.render(k);
			this.field_3345.render(k);
			this.field_3343.render(k);
			this.field_3341.render(k);
			this.field_3347.render(k);
		}
	}

	@Override
	public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
		this.field_3344.pitch = j * (float) (Math.PI / 180.0);
		this.field_3344.yaw = i * (float) (Math.PI / 180.0);
		this.field_3340.pitch = this.field_3344.pitch;
		this.field_3340.yaw = this.field_3344.yaw;
		this.field_3342.pitch = this.field_3344.pitch;
		this.field_3342.yaw = this.field_3344.yaw;
		this.field_3346.pitch = (float) (Math.PI / 2);
		this.field_3345.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
		this.field_3343.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
		this.field_3341.roll = h;
		this.field_3347.roll = -h;
	}
}
