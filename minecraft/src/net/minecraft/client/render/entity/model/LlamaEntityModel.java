package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.passive.AbstractDonkeyEntity;

@Environment(EnvType.CLIENT)
public class LlamaEntityModel<T extends AbstractDonkeyEntity> extends QuadrupedEntityModel<T> {
	private final Cuboid field_3430;
	private final Cuboid field_3429;

	public LlamaEntityModel(float f) {
		super(15, f);
		this.textureWidth = 128;
		this.textureHeight = 64;
		this.head = new Cuboid(this, 0, 0);
		this.head.addBox(-2.0F, -14.0F, -10.0F, 4, 4, 9, f);
		this.head.setRotationPoint(0.0F, 7.0F, -6.0F);
		this.head.setTextureOffset(0, 14).addBox(-4.0F, -16.0F, -6.0F, 8, 18, 6, f);
		this.head.setTextureOffset(17, 0).addBox(-4.0F, -19.0F, -4.0F, 3, 3, 2, f);
		this.head.setTextureOffset(17, 0).addBox(1.0F, -19.0F, -4.0F, 3, 3, 2, f);
		this.body = new Cuboid(this, 29, 0);
		this.body.addBox(-6.0F, -10.0F, -7.0F, 12, 18, 10, f);
		this.body.setRotationPoint(0.0F, 5.0F, 2.0F);
		this.field_3430 = new Cuboid(this, 45, 28);
		this.field_3430.addBox(-3.0F, 0.0F, 0.0F, 8, 8, 3, f);
		this.field_3430.setRotationPoint(-8.5F, 3.0F, 3.0F);
		this.field_3430.yaw = (float) (Math.PI / 2);
		this.field_3429 = new Cuboid(this, 45, 41);
		this.field_3429.addBox(-3.0F, 0.0F, 0.0F, 8, 8, 3, f);
		this.field_3429.setRotationPoint(5.5F, 3.0F, 3.0F);
		this.field_3429.yaw = (float) (Math.PI / 2);
		int i = 4;
		int j = 14;
		this.leg1 = new Cuboid(this, 29, 29);
		this.leg1.addBox(-2.0F, 0.0F, -2.0F, 4, 14, 4, f);
		this.leg1.setRotationPoint(-2.5F, 10.0F, 6.0F);
		this.leg2 = new Cuboid(this, 29, 29);
		this.leg2.addBox(-2.0F, 0.0F, -2.0F, 4, 14, 4, f);
		this.leg2.setRotationPoint(2.5F, 10.0F, 6.0F);
		this.leg3 = new Cuboid(this, 29, 29);
		this.leg3.addBox(-2.0F, 0.0F, -2.0F, 4, 14, 4, f);
		this.leg3.setRotationPoint(-2.5F, 10.0F, -4.0F);
		this.leg4 = new Cuboid(this, 29, 29);
		this.leg4.addBox(-2.0F, 0.0F, -2.0F, 4, 14, 4, f);
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
		boolean bl = !abstractDonkeyEntity.isChild() && abstractDonkeyEntity.hasChest();
		this.setAngles(abstractDonkeyEntity, f, g, h, i, j, k);
		if (this.isChild) {
			float l = 2.0F;
			GlStateManager.pushMatrix();
			GlStateManager.translatef(0.0F, this.field_3540 * k, this.field_3537 * k);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			float m = 0.7F;
			GlStateManager.scalef(0.71428573F, 0.64935064F, 0.7936508F);
			GlStateManager.translatef(0.0F, 21.0F * k, 0.22F);
			this.head.render(k);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			float n = 1.1F;
			GlStateManager.scalef(0.625F, 0.45454544F, 0.45454544F);
			GlStateManager.translatef(0.0F, 33.0F * k, 0.0F);
			this.body.render(k);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scalef(0.45454544F, 0.41322312F, 0.45454544F);
			GlStateManager.translatef(0.0F, 33.0F * k, 0.0F);
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

		if (bl) {
			this.field_3430.render(k);
			this.field_3429.render(k);
		}
	}
}
