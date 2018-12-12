package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.passive.PolarBearEntity;

@Environment(EnvType.CLIENT)
public class PolarBearEntityModel<T extends PolarBearEntity> extends QuadrupedEntityModel<T> {
	public PolarBearEntityModel() {
		super(12, 0.0F);
		this.textureWidth = 128;
		this.textureHeight = 64;
		this.head = new Cuboid(this, 0, 0);
		this.head.addBox(-3.5F, -3.0F, -3.0F, 7, 7, 7, 0.0F);
		this.head.setRotationPoint(0.0F, 10.0F, -16.0F);
		this.head.setTextureOffset(0, 44).addBox(-2.5F, 1.0F, -6.0F, 5, 3, 3, 0.0F);
		this.head.setTextureOffset(26, 0).addBox(-4.5F, -4.0F, -1.0F, 2, 2, 1, 0.0F);
		Cuboid cuboid = this.head.setTextureOffset(26, 0);
		cuboid.mirror = true;
		cuboid.addBox(2.5F, -4.0F, -1.0F, 2, 2, 1, 0.0F);
		this.body = new Cuboid(this);
		this.body.setTextureOffset(0, 19).addBox(-5.0F, -13.0F, -7.0F, 14, 14, 11, 0.0F);
		this.body.setTextureOffset(39, 0).addBox(-4.0F, -25.0F, -7.0F, 12, 12, 10, 0.0F);
		this.body.setRotationPoint(-2.0F, 9.0F, 12.0F);
		int i = 10;
		this.leg1 = new Cuboid(this, 50, 22);
		this.leg1.addBox(-2.0F, 0.0F, -2.0F, 4, 10, 8, 0.0F);
		this.leg1.setRotationPoint(-3.5F, 14.0F, 6.0F);
		this.leg2 = new Cuboid(this, 50, 22);
		this.leg2.addBox(-2.0F, 0.0F, -2.0F, 4, 10, 8, 0.0F);
		this.leg2.setRotationPoint(3.5F, 14.0F, 6.0F);
		this.leg3 = new Cuboid(this, 50, 40);
		this.leg3.addBox(-2.0F, 0.0F, -2.0F, 4, 10, 6, 0.0F);
		this.leg3.setRotationPoint(-2.5F, 14.0F, -7.0F);
		this.leg4 = new Cuboid(this, 50, 40);
		this.leg4.addBox(-2.0F, 0.0F, -2.0F, 4, 10, 6, 0.0F);
		this.leg4.setRotationPoint(2.5F, 14.0F, -7.0F);
		this.leg1.rotationPointX--;
		this.leg2.rotationPointX++;
		this.leg1.rotationPointZ += 0.0F;
		this.leg2.rotationPointZ += 0.0F;
		this.leg3.rotationPointX--;
		this.leg4.rotationPointX++;
		this.leg3.rotationPointZ--;
		this.leg4.rotationPointZ--;
		this.field_3537 += 2.0F;
	}

	public void method_17113(T polarBearEntity, float f, float g, float h, float i, float j, float k) {
		this.method_17114(polarBearEntity, f, g, h, i, j, k);
		if (this.isChild) {
			float l = 2.0F;
			this.field_3540 = 16.0F;
			this.field_3537 = 4.0F;
			GlStateManager.pushMatrix();
			GlStateManager.scalef(0.6666667F, 0.6666667F, 0.6666667F);
			GlStateManager.translatef(0.0F, this.field_3540 * k, this.field_3537 * k);
			this.head.render(k);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			GlStateManager.translatef(0.0F, 24.0F * k, 0.0F);
			this.body.render(k);
			this.leg1.render(k);
			this.leg2.render(k);
			this.leg3.render(k);
			this.leg4.render(k);
			GlStateManager.popMatrix();
		} else {
			this.head.render(k);
			this.body.render(k);
			this.leg1.render(k);
			this.leg2.render(k);
			this.leg3.render(k);
			this.leg4.render(k);
		}
	}

	public void method_17114(T polarBearEntity, float f, float g, float h, float i, float j, float k) {
		super.setAngles(polarBearEntity, f, g, h, i, j, k);
		float l = h - (float)polarBearEntity.age;
		float m = polarBearEntity.method_6601(l);
		m *= m;
		float n = 1.0F - m;
		this.body.pitch = (float) (Math.PI / 2) - m * (float) Math.PI * 0.35F;
		this.body.rotationPointY = 9.0F * n + 11.0F * m;
		this.leg3.rotationPointY = 14.0F * n - 6.0F * m;
		this.leg3.rotationPointZ = -8.0F * n - 4.0F * m;
		this.leg3.pitch -= m * (float) Math.PI * 0.45F;
		this.leg4.rotationPointY = this.leg3.rotationPointY;
		this.leg4.rotationPointZ = this.leg3.rotationPointZ;
		this.leg4.pitch -= m * (float) Math.PI * 0.45F;
		this.head.rotationPointY = 10.0F * n - 12.0F * m;
		this.head.rotationPointZ = -16.0F * n - 3.0F * m;
		this.head.pitch += m * (float) Math.PI * 0.15F;
	}
}
