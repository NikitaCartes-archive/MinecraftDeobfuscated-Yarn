package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BoatEntityModel extends EntityModel<BoatEntity> {
	private final Cuboid[] field_3327 = new Cuboid[5];
	private final Cuboid[] field_3325 = new Cuboid[2];
	private final Cuboid field_3326;

	public BoatEntityModel() {
		this.field_3327[0] = new Cuboid(this, 0, 0).setTextureSize(128, 64);
		this.field_3327[1] = new Cuboid(this, 0, 19).setTextureSize(128, 64);
		this.field_3327[2] = new Cuboid(this, 0, 27).setTextureSize(128, 64);
		this.field_3327[3] = new Cuboid(this, 0, 35).setTextureSize(128, 64);
		this.field_3327[4] = new Cuboid(this, 0, 43).setTextureSize(128, 64);
		int i = 32;
		int j = 6;
		int k = 20;
		int l = 4;
		int m = 28;
		this.field_3327[0].addBox(-14.0F, -9.0F, -3.0F, 28, 16, 3, 0.0F);
		this.field_3327[0].setRotationPoint(0.0F, 3.0F, 1.0F);
		this.field_3327[1].addBox(-13.0F, -7.0F, -1.0F, 18, 6, 2, 0.0F);
		this.field_3327[1].setRotationPoint(-15.0F, 4.0F, 4.0F);
		this.field_3327[2].addBox(-8.0F, -7.0F, -1.0F, 16, 6, 2, 0.0F);
		this.field_3327[2].setRotationPoint(15.0F, 4.0F, 0.0F);
		this.field_3327[3].addBox(-14.0F, -7.0F, -1.0F, 28, 6, 2, 0.0F);
		this.field_3327[3].setRotationPoint(0.0F, 4.0F, -9.0F);
		this.field_3327[4].addBox(-14.0F, -7.0F, -1.0F, 28, 6, 2, 0.0F);
		this.field_3327[4].setRotationPoint(0.0F, 4.0F, 9.0F);
		this.field_3327[0].pitch = (float) (Math.PI / 2);
		this.field_3327[1].yaw = (float) (Math.PI * 3.0 / 2.0);
		this.field_3327[2].yaw = (float) (Math.PI / 2);
		this.field_3327[3].yaw = (float) Math.PI;
		this.field_3325[0] = this.method_2796(true);
		this.field_3325[0].setRotationPoint(3.0F, -5.0F, 9.0F);
		this.field_3325[1] = this.method_2796(false);
		this.field_3325[1].setRotationPoint(3.0F, -5.0F, -9.0F);
		this.field_3325[1].yaw = (float) Math.PI;
		this.field_3325[0].roll = (float) (Math.PI / 16);
		this.field_3325[1].roll = (float) (Math.PI / 16);
		this.field_3326 = new Cuboid(this, 0, 0).setTextureSize(128, 64);
		this.field_3326.addBox(-14.0F, -9.0F, -3.0F, 28, 16, 3, 0.0F);
		this.field_3326.setRotationPoint(0.0F, -3.0F, 1.0F);
		this.field_3326.pitch = (float) (Math.PI / 2);
	}

	public void method_17071(BoatEntity boatEntity, float f, float g, float h, float i, float j, float k) {
		GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
		this.setAngles(boatEntity, f, g, h, i, j, k);

		for (int l = 0; l < 5; l++) {
			this.field_3327[l].render(k);
		}

		this.method_2797(boatEntity, 0, k, f);
		this.method_2797(boatEntity, 1, k, f);
	}

	public void renderPass(Entity entity, float f, float g, float h, float i, float j, float k) {
		GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.colorMask(false, false, false, false);
		this.field_3326.render(k);
		GlStateManager.colorMask(true, true, true, true);
	}

	protected Cuboid method_2796(boolean bl) {
		Cuboid cuboid = new Cuboid(this, 62, bl ? 0 : 20).setTextureSize(128, 64);
		int i = 20;
		int j = 7;
		int k = 6;
		float f = -5.0F;
		cuboid.addBox(-1.0F, 0.0F, -5.0F, 2, 2, 18);
		cuboid.addBox(bl ? -1.001F : 0.001F, -3.0F, 8.0F, 1, 6, 7);
		return cuboid;
	}

	protected void method_2797(BoatEntity boatEntity, int i, float f, float g) {
		float h = boatEntity.method_7551(i, g);
		Cuboid cuboid = this.field_3325[i];
		cuboid.pitch = (float)MathHelper.lerpClamped((float) (-Math.PI / 3), (float) (-Math.PI / 12), (double)((MathHelper.sin(-h) + 1.0F) / 2.0F));
		cuboid.yaw = (float)MathHelper.lerpClamped((float) (-Math.PI / 4), (float) (Math.PI / 4), (double)((MathHelper.sin(-h + 1.0F) + 1.0F) / 2.0F));
		if (i == 1) {
			cuboid.yaw = (float) Math.PI - cuboid.yaw;
		}

		cuboid.render(f);
	}
}
