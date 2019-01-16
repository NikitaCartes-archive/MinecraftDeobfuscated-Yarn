package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.LivingEntity;
import net.minecraft.sortme.OptionMainHand;

@Environment(EnvType.CLIENT)
public class PlayerEntityModel<T extends LivingEntity> extends BipedEntityModel<T> {
	public final Cuboid leftArmOverlay;
	public final Cuboid rightArmOverlay;
	public final Cuboid leftLegOverlay;
	public final Cuboid rightLegOverlay;
	public final Cuboid bodyOverlay;
	private final Cuboid cape;
	private final Cuboid mau5Ears;
	private final boolean thinArms;

	public PlayerEntityModel(float f, boolean bl) {
		super(f, 0.0F, 64, 64);
		this.thinArms = bl;
		this.mau5Ears = new Cuboid(this, 24, 0);
		this.mau5Ears.addBox(-3.0F, -6.0F, -1.0F, 6, 6, 1, f);
		this.cape = new Cuboid(this, 0, 0);
		this.cape.setTextureSize(64, 32);
		this.cape.addBox(-5.0F, 0.0F, -1.0F, 10, 16, 1, f);
		if (bl) {
			this.armLeft = new Cuboid(this, 32, 48);
			this.armLeft.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, f);
			this.armLeft.setRotationPoint(5.0F, 2.5F, 0.0F);
			this.armRight = new Cuboid(this, 40, 16);
			this.armRight.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, f);
			this.armRight.setRotationPoint(-5.0F, 2.5F, 0.0F);
			this.leftArmOverlay = new Cuboid(this, 48, 48);
			this.leftArmOverlay.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, f + 0.25F);
			this.leftArmOverlay.setRotationPoint(5.0F, 2.5F, 0.0F);
			this.rightArmOverlay = new Cuboid(this, 40, 32);
			this.rightArmOverlay.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, f + 0.25F);
			this.rightArmOverlay.setRotationPoint(-5.0F, 2.5F, 10.0F);
		} else {
			this.armLeft = new Cuboid(this, 32, 48);
			this.armLeft.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, f);
			this.armLeft.setRotationPoint(5.0F, 2.0F, 0.0F);
			this.leftArmOverlay = new Cuboid(this, 48, 48);
			this.leftArmOverlay.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, f + 0.25F);
			this.leftArmOverlay.setRotationPoint(5.0F, 2.0F, 0.0F);
			this.rightArmOverlay = new Cuboid(this, 40, 32);
			this.rightArmOverlay.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, f + 0.25F);
			this.rightArmOverlay.setRotationPoint(-5.0F, 2.0F, 10.0F);
		}

		this.legLeft = new Cuboid(this, 16, 48);
		this.legLeft.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
		this.legLeft.setRotationPoint(1.9F, 12.0F, 0.0F);
		this.leftLegOverlay = new Cuboid(this, 0, 48);
		this.leftLegOverlay.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, f + 0.25F);
		this.leftLegOverlay.setRotationPoint(1.9F, 12.0F, 0.0F);
		this.rightLegOverlay = new Cuboid(this, 0, 32);
		this.rightLegOverlay.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, f + 0.25F);
		this.rightLegOverlay.setRotationPoint(-1.9F, 12.0F, 0.0F);
		this.bodyOverlay = new Cuboid(this, 16, 32);
		this.bodyOverlay.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, f + 0.25F);
		this.bodyOverlay.setRotationPoint(0.0F, 0.0F, 0.0F);
	}

	@Override
	public void method_17088(T livingEntity, float f, float g, float h, float i, float j, float k) {
		super.method_17088(livingEntity, f, g, h, i, j, k);
		GlStateManager.pushMatrix();
		if (this.isChild) {
			float l = 2.0F;
			GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			GlStateManager.translatef(0.0F, 24.0F * k, 0.0F);
			this.leftLegOverlay.render(k);
			this.rightLegOverlay.render(k);
			this.leftArmOverlay.render(k);
			this.rightArmOverlay.render(k);
			this.bodyOverlay.render(k);
		} else {
			if (livingEntity.isSneaking()) {
				GlStateManager.translatef(0.0F, 0.2F, 0.0F);
			}

			this.leftLegOverlay.render(k);
			this.rightLegOverlay.render(k);
			this.leftArmOverlay.render(k);
			this.rightArmOverlay.render(k);
			this.bodyOverlay.render(k);
		}

		GlStateManager.popMatrix();
	}

	public void method_2824(float f) {
		this.mau5Ears.method_17138(this.head);
		this.mau5Ears.rotationPointX = 0.0F;
		this.mau5Ears.rotationPointY = 0.0F;
		this.mau5Ears.render(f);
	}

	public void method_2823(float f) {
		this.cape.render(f);
	}

	@Override
	public void method_17087(T livingEntity, float f, float g, float h, float i, float j, float k) {
		super.method_17087(livingEntity, f, g, h, i, j, k);
		this.leftLegOverlay.method_17138(this.legLeft);
		this.rightLegOverlay.method_17138(this.legRight);
		this.leftArmOverlay.method_17138(this.armLeft);
		this.rightArmOverlay.method_17138(this.armRight);
		this.bodyOverlay.method_17138(this.body);
		if (livingEntity.isSneaking()) {
			this.cape.rotationPointY = 2.0F;
		} else {
			this.cape.rotationPointY = 0.0F;
		}
	}

	@Override
	public void setVisible(boolean bl) {
		super.setVisible(bl);
		this.leftArmOverlay.visible = bl;
		this.rightArmOverlay.visible = bl;
		this.leftLegOverlay.visible = bl;
		this.rightLegOverlay.visible = bl;
		this.bodyOverlay.visible = bl;
		this.cape.visible = bl;
		this.mau5Ears.visible = bl;
	}

	@Override
	public void method_2803(float f, OptionMainHand optionMainHand) {
		Cuboid cuboid = this.getArm(optionMainHand);
		if (this.thinArms) {
			float g = 0.5F * (float)(optionMainHand == OptionMainHand.field_6183 ? 1 : -1);
			cuboid.rotationPointX += g;
			cuboid.method_2847(f);
			cuboid.rotationPointX -= g;
		} else {
			cuboid.method_2847(f);
		}
	}
}
