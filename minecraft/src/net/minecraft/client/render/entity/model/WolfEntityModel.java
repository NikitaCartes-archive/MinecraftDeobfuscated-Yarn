package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class WolfEntityModel<T extends WolfEntity> extends EntityModel<T> {
	private final Cuboid field_3621;
	private final Cuboid field_3623;
	private final Cuboid field_3622;
	private final Cuboid field_3620;
	private final Cuboid field_3618;
	private final Cuboid field_3624;
	private final Cuboid field_3617;
	private final Cuboid field_3619;

	public WolfEntityModel() {
		float f = 0.0F;
		float g = 13.5F;
		this.field_3621 = new Cuboid(this, 0, 0);
		this.field_3621.addBox(-2.0F, -3.0F, -2.0F, 6, 6, 4, 0.0F);
		this.field_3621.setRotationPoint(-1.0F, 13.5F, -7.0F);
		this.field_3623 = new Cuboid(this, 18, 14);
		this.field_3623.addBox(-3.0F, -2.0F, -3.0F, 6, 9, 6, 0.0F);
		this.field_3623.setRotationPoint(0.0F, 14.0F, 2.0F);
		this.field_3619 = new Cuboid(this, 21, 0);
		this.field_3619.addBox(-3.0F, -3.0F, -3.0F, 8, 6, 7, 0.0F);
		this.field_3619.setRotationPoint(-1.0F, 14.0F, 2.0F);
		this.field_3622 = new Cuboid(this, 0, 18);
		this.field_3622.addBox(0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
		this.field_3622.setRotationPoint(-2.5F, 16.0F, 7.0F);
		this.field_3620 = new Cuboid(this, 0, 18);
		this.field_3620.addBox(0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
		this.field_3620.setRotationPoint(0.5F, 16.0F, 7.0F);
		this.field_3618 = new Cuboid(this, 0, 18);
		this.field_3618.addBox(0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
		this.field_3618.setRotationPoint(-2.5F, 16.0F, -4.0F);
		this.field_3624 = new Cuboid(this, 0, 18);
		this.field_3624.addBox(0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
		this.field_3624.setRotationPoint(0.5F, 16.0F, -4.0F);
		this.field_3617 = new Cuboid(this, 9, 18);
		this.field_3617.addBox(0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
		this.field_3617.setRotationPoint(-1.0F, 12.0F, 8.0F);
		this.field_3621.setTextureOffset(16, 14).addBox(-2.0F, -5.0F, 0.0F, 2, 2, 1, 0.0F);
		this.field_3621.setTextureOffset(16, 14).addBox(2.0F, -5.0F, 0.0F, 2, 2, 1, 0.0F);
		this.field_3621.setTextureOffset(0, 10).addBox(-0.5F, 0.0F, -5.0F, 3, 3, 4, 0.0F);
	}

	public void method_17132(T wolfEntity, float f, float g, float h, float i, float j, float k) {
		super.render(wolfEntity, f, g, h, i, j, k);
		this.method_17133(wolfEntity, f, g, h, i, j, k);
		if (this.isChild) {
			float l = 2.0F;
			GlStateManager.pushMatrix();
			GlStateManager.translatef(0.0F, 5.0F * k, 2.0F * k);
			this.field_3621.method_2852(k);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			GlStateManager.translatef(0.0F, 24.0F * k, 0.0F);
			this.field_3623.render(k);
			this.field_3622.render(k);
			this.field_3620.render(k);
			this.field_3618.render(k);
			this.field_3624.render(k);
			this.field_3617.method_2852(k);
			this.field_3619.render(k);
			GlStateManager.popMatrix();
		} else {
			this.field_3621.method_2852(k);
			this.field_3623.render(k);
			this.field_3622.render(k);
			this.field_3620.render(k);
			this.field_3618.render(k);
			this.field_3624.render(k);
			this.field_3617.method_2852(k);
			this.field_3619.render(k);
		}
	}

	public void method_17131(T wolfEntity, float f, float g, float h) {
		if (wolfEntity.isAngry()) {
			this.field_3617.yaw = 0.0F;
		} else {
			this.field_3617.yaw = MathHelper.cos(f * 0.6662F) * 1.4F * g;
		}

		if (wolfEntity.isSitting()) {
			this.field_3619.setRotationPoint(-1.0F, 16.0F, -3.0F);
			this.field_3619.pitch = (float) (Math.PI * 2.0 / 5.0);
			this.field_3619.yaw = 0.0F;
			this.field_3623.setRotationPoint(0.0F, 18.0F, 0.0F);
			this.field_3623.pitch = (float) (Math.PI / 4);
			this.field_3617.setRotationPoint(-1.0F, 21.0F, 6.0F);
			this.field_3622.setRotationPoint(-2.5F, 22.0F, 2.0F);
			this.field_3622.pitch = (float) (Math.PI * 3.0 / 2.0);
			this.field_3620.setRotationPoint(0.5F, 22.0F, 2.0F);
			this.field_3620.pitch = (float) (Math.PI * 3.0 / 2.0);
			this.field_3618.pitch = 5.811947F;
			this.field_3618.setRotationPoint(-2.49F, 17.0F, -4.0F);
			this.field_3624.pitch = 5.811947F;
			this.field_3624.setRotationPoint(0.51F, 17.0F, -4.0F);
		} else {
			this.field_3623.setRotationPoint(0.0F, 14.0F, 2.0F);
			this.field_3623.pitch = (float) (Math.PI / 2);
			this.field_3619.setRotationPoint(-1.0F, 14.0F, -3.0F);
			this.field_3619.pitch = this.field_3623.pitch;
			this.field_3617.setRotationPoint(-1.0F, 12.0F, 8.0F);
			this.field_3622.setRotationPoint(-2.5F, 16.0F, 7.0F);
			this.field_3620.setRotationPoint(0.5F, 16.0F, 7.0F);
			this.field_3618.setRotationPoint(-2.5F, 16.0F, -4.0F);
			this.field_3624.setRotationPoint(0.5F, 16.0F, -4.0F);
			this.field_3622.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
			this.field_3620.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
			this.field_3618.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
			this.field_3624.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
		}

		this.field_3621.roll = wolfEntity.method_6719(h) + wolfEntity.method_6715(h, 0.0F);
		this.field_3619.roll = wolfEntity.method_6715(h, -0.08F);
		this.field_3623.roll = wolfEntity.method_6715(h, -0.16F);
		this.field_3617.roll = wolfEntity.method_6715(h, -0.2F);
	}

	public void method_17133(T wolfEntity, float f, float g, float h, float i, float j, float k) {
		super.setAngles(wolfEntity, f, g, h, i, j, k);
		this.field_3621.pitch = j * (float) (Math.PI / 180.0);
		this.field_3621.yaw = i * (float) (Math.PI / 180.0);
		this.field_3617.pitch = h;
	}
}
