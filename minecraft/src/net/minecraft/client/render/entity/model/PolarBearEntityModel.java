package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.passive.PolarBearEntity;

@Environment(EnvType.CLIENT)
public class PolarBearEntityModel<T extends PolarBearEntity> extends QuadrupedEntityModel<T> {
	public PolarBearEntityModel() {
		super(12, 0.0F);
		this.textureWidth = 128;
		this.textureHeight = 64;
		this.head = new ModelPart(this, 0, 0);
		this.head.addCuboid(-3.5F, -3.0F, -3.0F, 7.0F, 7.0F, 7.0F, 0.0F);
		this.head.setRotationPoint(0.0F, 10.0F, -16.0F);
		this.head.setTextureOffset(0, 44).addCuboid(-2.5F, 1.0F, -6.0F, 5.0F, 3.0F, 3.0F, 0.0F);
		this.head.setTextureOffset(26, 0).addCuboid(-4.5F, -4.0F, -1.0F, 2.0F, 2.0F, 1.0F, 0.0F);
		ModelPart modelPart = this.head.setTextureOffset(26, 0);
		modelPart.mirror = true;
		modelPart.addCuboid(2.5F, -4.0F, -1.0F, 2.0F, 2.0F, 1.0F, 0.0F);
		this.body = new ModelPart(this);
		this.body.setTextureOffset(0, 19).addCuboid(-5.0F, -13.0F, -7.0F, 14.0F, 14.0F, 11.0F, 0.0F);
		this.body.setTextureOffset(39, 0).addCuboid(-4.0F, -25.0F, -7.0F, 12.0F, 12.0F, 10.0F, 0.0F);
		this.body.setRotationPoint(-2.0F, 9.0F, 12.0F);
		int i = 10;
		this.leg1 = new ModelPart(this, 50, 22);
		this.leg1.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 8.0F, 0.0F);
		this.leg1.setRotationPoint(-3.5F, 14.0F, 6.0F);
		this.leg2 = new ModelPart(this, 50, 22);
		this.leg2.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 8.0F, 0.0F);
		this.leg2.setRotationPoint(3.5F, 14.0F, 6.0F);
		this.leg3 = new ModelPart(this, 50, 40);
		this.leg3.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 6.0F, 0.0F);
		this.leg3.setRotationPoint(-2.5F, 14.0F, -7.0F);
		this.leg4 = new ModelPart(this, 50, 40);
		this.leg4.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 6.0F, 0.0F);
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
			RenderSystem.pushMatrix();
			RenderSystem.scalef(0.6666667F, 0.6666667F, 0.6666667F);
			RenderSystem.translatef(0.0F, this.field_3540 * k, this.field_3537 * k);
			this.head.render(k);
			RenderSystem.popMatrix();
			RenderSystem.pushMatrix();
			RenderSystem.scalef(0.5F, 0.5F, 0.5F);
			RenderSystem.translatef(0.0F, 24.0F * k, 0.0F);
			this.body.render(k);
			this.leg1.render(k);
			this.leg2.render(k);
			this.leg3.render(k);
			this.leg4.render(k);
			RenderSystem.popMatrix();
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
		float m = polarBearEntity.getWarningAnimationProgress(l);
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
		if (this.isChild) {
			this.head.rotationPointY = 10.0F * n - 9.0F * m;
			this.head.rotationPointZ = -16.0F * n - 7.0F * m;
		} else {
			this.head.rotationPointY = 10.0F * n - 14.0F * m;
			this.head.rotationPointZ = -16.0F * n - 3.0F * m;
		}

		this.head.pitch += m * (float) Math.PI * 0.15F;
	}
}
