package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.passive.AbstractDonkeyEntity;

@Environment(EnvType.CLIENT)
public class LlamaEntityModel<T extends AbstractDonkeyEntity> extends QuadrupedEntityModel<T> {
	private final ModelPart field_3430;
	private final ModelPart field_3429;

	public LlamaEntityModel(float f) {
		super(15, f);
		this.textureWidth = 128;
		this.textureHeight = 64;
		this.head = new ModelPart(this, 0, 0);
		this.head.addCuboid(-2.0F, -14.0F, -10.0F, 4.0F, 4.0F, 9.0F, f);
		this.head.setRotationPoint(0.0F, 7.0F, -6.0F);
		this.head.setTextureOffset(0, 14).addCuboid(-4.0F, -16.0F, -6.0F, 8.0F, 18.0F, 6.0F, f);
		this.head.setTextureOffset(17, 0).addCuboid(-4.0F, -19.0F, -4.0F, 3.0F, 3.0F, 2.0F, f);
		this.head.setTextureOffset(17, 0).addCuboid(1.0F, -19.0F, -4.0F, 3.0F, 3.0F, 2.0F, f);
		this.body = new ModelPart(this, 29, 0);
		this.body.addCuboid(-6.0F, -10.0F, -7.0F, 12.0F, 18.0F, 10.0F, f);
		this.body.setRotationPoint(0.0F, 5.0F, 2.0F);
		this.field_3430 = new ModelPart(this, 45, 28);
		this.field_3430.addCuboid(-3.0F, 0.0F, 0.0F, 8.0F, 8.0F, 3.0F, f);
		this.field_3430.setRotationPoint(-8.5F, 3.0F, 3.0F);
		this.field_3430.yaw = (float) (Math.PI / 2);
		this.field_3429 = new ModelPart(this, 45, 41);
		this.field_3429.addCuboid(-3.0F, 0.0F, 0.0F, 8.0F, 8.0F, 3.0F, f);
		this.field_3429.setRotationPoint(5.5F, 3.0F, 3.0F);
		this.field_3429.yaw = (float) (Math.PI / 2);
		int i = 4;
		int j = 14;
		this.leg1 = new ModelPart(this, 29, 29);
		this.leg1.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, f);
		this.leg1.setRotationPoint(-2.5F, 10.0F, 6.0F);
		this.leg2 = new ModelPart(this, 29, 29);
		this.leg2.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, f);
		this.leg2.setRotationPoint(2.5F, 10.0F, 6.0F);
		this.leg3 = new ModelPart(this, 29, 29);
		this.leg3.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, f);
		this.leg3.setRotationPoint(-2.5F, 10.0F, -4.0F);
		this.leg4 = new ModelPart(this, 29, 29);
		this.leg4.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, f);
		this.leg4.setRotationPoint(2.5F, 10.0F, -4.0F);
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

	public void method_17100(T abstractDonkeyEntity, float f, float g, float h, float i, float j, float k) {
		boolean bl = !abstractDonkeyEntity.isBaby() && abstractDonkeyEntity.hasChest();
		this.setAngles(abstractDonkeyEntity, f, g, h, i, j, k);
		if (this.isChild) {
			float l = 2.0F;
			RenderSystem.pushMatrix();
			RenderSystem.translatef(0.0F, this.field_3540 * k, this.field_3537 * k);
			RenderSystem.popMatrix();
			RenderSystem.pushMatrix();
			float m = 0.7F;
			RenderSystem.scalef(0.71428573F, 0.64935064F, 0.7936508F);
			RenderSystem.translatef(0.0F, 21.0F * k, 0.22F);
			this.head.render(k);
			RenderSystem.popMatrix();
			RenderSystem.pushMatrix();
			float n = 1.1F;
			RenderSystem.scalef(0.625F, 0.45454544F, 0.45454544F);
			RenderSystem.translatef(0.0F, 33.0F * k, 0.0F);
			this.body.render(k);
			RenderSystem.popMatrix();
			RenderSystem.pushMatrix();
			RenderSystem.scalef(0.45454544F, 0.41322312F, 0.45454544F);
			RenderSystem.translatef(0.0F, 33.0F * k, 0.0F);
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

		if (bl) {
			this.field_3430.render(k);
			this.field_3429.render(k);
		}
	}
}
