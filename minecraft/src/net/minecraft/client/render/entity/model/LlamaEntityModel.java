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
		this.field_3535 = new Cuboid(this, 0, 0);
		this.field_3535.addBox(-2.0F, -14.0F, -10.0F, 4, 4, 9, f);
		this.field_3535.setRotationPoint(0.0F, 7.0F, -6.0F);
		this.field_3535.setTextureOffset(0, 14).addBox(-4.0F, -16.0F, -6.0F, 8, 18, 6, f);
		this.field_3535.setTextureOffset(17, 0).addBox(-4.0F, -19.0F, -4.0F, 3, 3, 2, f);
		this.field_3535.setTextureOffset(17, 0).addBox(1.0F, -19.0F, -4.0F, 3, 3, 2, f);
		this.field_3538 = new Cuboid(this, 29, 0);
		this.field_3538.addBox(-6.0F, -10.0F, -7.0F, 12, 18, 10, f);
		this.field_3538.setRotationPoint(0.0F, 5.0F, 2.0F);
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
		this.field_3536 = new Cuboid(this, 29, 29);
		this.field_3536.addBox(-2.0F, 0.0F, -2.0F, 4, 14, 4, f);
		this.field_3536.setRotationPoint(-2.5F, 10.0F, 6.0F);
		this.field_3534 = new Cuboid(this, 29, 29);
		this.field_3534.addBox(-2.0F, 0.0F, -2.0F, 4, 14, 4, f);
		this.field_3534.setRotationPoint(2.5F, 10.0F, 6.0F);
		this.field_3533 = new Cuboid(this, 29, 29);
		this.field_3533.addBox(-2.0F, 0.0F, -2.0F, 4, 14, 4, f);
		this.field_3533.setRotationPoint(-2.5F, 10.0F, -4.0F);
		this.field_3539 = new Cuboid(this, 29, 29);
		this.field_3539.addBox(-2.0F, 0.0F, -2.0F, 4, 14, 4, f);
		this.field_3539.setRotationPoint(2.5F, 10.0F, -4.0F);
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
			this.field_3535.render(k);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			float n = 1.1F;
			GlStateManager.scalef(0.625F, 0.45454544F, 0.45454544F);
			GlStateManager.translatef(0.0F, 33.0F * k, 0.0F);
			this.field_3538.render(k);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scalef(0.45454544F, 0.41322312F, 0.45454544F);
			GlStateManager.translatef(0.0F, 33.0F * k, 0.0F);
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

		if (bl) {
			this.field_3430.render(k);
			this.field_3429.render(k);
		}
	}
}
