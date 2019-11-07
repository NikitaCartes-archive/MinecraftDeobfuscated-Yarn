package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.passive.PolarBearEntity;

@Environment(EnvType.CLIENT)
public class PolarBearEntityModel<T extends PolarBearEntity> extends QuadrupedEntityModel<T> {
	public PolarBearEntityModel() {
		super(12, 0.0F, true, 16.0F, 4.0F, 2.25F, 2.0F, 24);
		this.textureWidth = 128;
		this.textureHeight = 64;
		this.head = new ModelPart(this, 0, 0);
		this.head.addCuboid(-3.5F, -3.0F, -3.0F, 7.0F, 7.0F, 7.0F, 0.0F);
		this.head.setPivot(0.0F, 10.0F, -16.0F);
		this.head.setTextureOffset(0, 44).addCuboid(-2.5F, 1.0F, -6.0F, 5.0F, 3.0F, 3.0F, 0.0F);
		this.head.setTextureOffset(26, 0).addCuboid(-4.5F, -4.0F, -1.0F, 2.0F, 2.0F, 1.0F, 0.0F);
		ModelPart modelPart = this.head.setTextureOffset(26, 0);
		modelPart.mirror = true;
		modelPart.addCuboid(2.5F, -4.0F, -1.0F, 2.0F, 2.0F, 1.0F, 0.0F);
		this.body = new ModelPart(this);
		this.body.setTextureOffset(0, 19).addCuboid(-5.0F, -13.0F, -7.0F, 14.0F, 14.0F, 11.0F, 0.0F);
		this.body.setTextureOffset(39, 0).addCuboid(-4.0F, -25.0F, -7.0F, 12.0F, 12.0F, 10.0F, 0.0F);
		this.body.setPivot(-2.0F, 9.0F, 12.0F);
		int i = 10;
		this.leg1 = new ModelPart(this, 50, 22);
		this.leg1.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 8.0F, 0.0F);
		this.leg1.setPivot(-3.5F, 14.0F, 6.0F);
		this.leg2 = new ModelPart(this, 50, 22);
		this.leg2.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 8.0F, 0.0F);
		this.leg2.setPivot(3.5F, 14.0F, 6.0F);
		this.leg3 = new ModelPart(this, 50, 40);
		this.leg3.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 6.0F, 0.0F);
		this.leg3.setPivot(-2.5F, 14.0F, -7.0F);
		this.leg4 = new ModelPart(this, 50, 40);
		this.leg4.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 6.0F, 0.0F);
		this.leg4.setPivot(2.5F, 14.0F, -7.0F);
		this.leg1.pivotX--;
		this.leg2.pivotX++;
		this.leg1.pivotZ += 0.0F;
		this.leg2.pivotZ += 0.0F;
		this.leg3.pivotX--;
		this.leg4.pivotX++;
		this.leg3.pivotZ--;
		this.leg4.pivotZ--;
	}

	public void method_17114(T polarBearEntity, float f, float g, float h, float i, float j) {
		super.setAngles(polarBearEntity, f, g, h, i, j);
		float k = h - (float)polarBearEntity.age;
		float l = polarBearEntity.getWarningAnimationProgress(k);
		l *= l;
		float m = 1.0F - l;
		this.body.pitch = (float) (Math.PI / 2) - l * (float) Math.PI * 0.35F;
		this.body.pivotY = 9.0F * m + 11.0F * l;
		this.leg3.pivotY = 14.0F * m - 6.0F * l;
		this.leg3.pivotZ = -8.0F * m - 4.0F * l;
		this.leg3.pitch -= l * (float) Math.PI * 0.45F;
		this.leg4.pivotY = this.leg3.pivotY;
		this.leg4.pivotZ = this.leg3.pivotZ;
		this.leg4.pitch -= l * (float) Math.PI * 0.45F;
		if (this.isChild) {
			this.head.pivotY = 10.0F * m - 9.0F * l;
			this.head.pivotZ = -16.0F * m - 7.0F * l;
		} else {
			this.head.pivotY = 10.0F * m - 14.0F * l;
			this.head.pivotZ = -16.0F * m - 3.0F * l;
		}

		this.head.pitch += l * (float) Math.PI * 0.15F;
	}
}
