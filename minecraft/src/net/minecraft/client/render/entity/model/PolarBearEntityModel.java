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
		this.field_3535 = new Cuboid(this, 0, 0);
		this.field_3535.addBox(-3.5F, -3.0F, -3.0F, 7, 7, 7, 0.0F);
		this.field_3535.setRotationPoint(0.0F, 10.0F, -16.0F);
		this.field_3535.setTextureOffset(0, 44).addBox(-2.5F, 1.0F, -6.0F, 5, 3, 3, 0.0F);
		this.field_3535.setTextureOffset(26, 0).addBox(-4.5F, -4.0F, -1.0F, 2, 2, 1, 0.0F);
		Cuboid cuboid = this.field_3535.setTextureOffset(26, 0);
		cuboid.mirror = true;
		cuboid.addBox(2.5F, -4.0F, -1.0F, 2, 2, 1, 0.0F);
		this.field_3538 = new Cuboid(this);
		this.field_3538.setTextureOffset(0, 19).addBox(-5.0F, -13.0F, -7.0F, 14, 14, 11, 0.0F);
		this.field_3538.setTextureOffset(39, 0).addBox(-4.0F, -25.0F, -7.0F, 12, 12, 10, 0.0F);
		this.field_3538.setRotationPoint(-2.0F, 9.0F, 12.0F);
		int i = 10;
		this.field_3536 = new Cuboid(this, 50, 22);
		this.field_3536.addBox(-2.0F, 0.0F, -2.0F, 4, 10, 8, 0.0F);
		this.field_3536.setRotationPoint(-3.5F, 14.0F, 6.0F);
		this.field_3534 = new Cuboid(this, 50, 22);
		this.field_3534.addBox(-2.0F, 0.0F, -2.0F, 4, 10, 8, 0.0F);
		this.field_3534.setRotationPoint(3.5F, 14.0F, 6.0F);
		this.field_3533 = new Cuboid(this, 50, 40);
		this.field_3533.addBox(-2.0F, 0.0F, -2.0F, 4, 10, 6, 0.0F);
		this.field_3533.setRotationPoint(-2.5F, 14.0F, -7.0F);
		this.field_3539 = new Cuboid(this, 50, 40);
		this.field_3539.addBox(-2.0F, 0.0F, -2.0F, 4, 10, 6, 0.0F);
		this.field_3539.setRotationPoint(2.5F, 14.0F, -7.0F);
		this.field_3536.rotationPointX--;
		this.field_3534.rotationPointX++;
		this.field_3536.rotationPointZ += 0.0F;
		this.field_3534.rotationPointZ += 0.0F;
		this.field_3533.rotationPointX--;
		this.field_3539.rotationPointX++;
		this.field_3533.rotationPointZ--;
		this.field_3539.rotationPointZ--;
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
			this.field_3535.render(k);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			GlStateManager.translatef(0.0F, 24.0F * k, 0.0F);
			this.field_3538.render(k);
			this.field_3536.render(k);
			this.field_3534.render(k);
			this.field_3533.render(k);
			this.field_3539.render(k);
			GlStateManager.popMatrix();
		} else {
			this.field_3535.render(k);
			this.field_3538.render(k);
			this.field_3536.render(k);
			this.field_3534.render(k);
			this.field_3533.render(k);
			this.field_3539.render(k);
		}
	}

	public void method_17114(T polarBearEntity, float f, float g, float h, float i, float j, float k) {
		super.setAngles(polarBearEntity, f, g, h, i, j, k);
		float l = h - (float)polarBearEntity.age;
		float m = polarBearEntity.method_6601(l);
		m *= m;
		float n = 1.0F - m;
		this.field_3538.pitch = (float) (Math.PI / 2) - m * (float) Math.PI * 0.35F;
		this.field_3538.rotationPointY = 9.0F * n + 11.0F * m;
		this.field_3533.rotationPointY = 14.0F * n - 6.0F * m;
		this.field_3533.rotationPointZ = -8.0F * n - 4.0F * m;
		this.field_3533.pitch -= m * (float) Math.PI * 0.45F;
		this.field_3539.rotationPointY = this.field_3533.rotationPointY;
		this.field_3539.rotationPointZ = this.field_3533.rotationPointZ;
		this.field_3539.pitch -= m * (float) Math.PI * 0.45F;
		this.field_3535.rotationPointY = 10.0F * n - 12.0F * m;
		this.field_3535.rotationPointZ = -16.0F * n - 3.0F * m;
		this.field_3535.pitch += m * (float) Math.PI * 0.15F;
	}
}
