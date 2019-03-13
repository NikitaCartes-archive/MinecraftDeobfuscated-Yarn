package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.LivingEntity;
import net.minecraft.sortme.OptionMainHand;

@Environment(EnvType.CLIENT)
public class PlayerEntityModel<T extends LivingEntity> extends BipedEntityModel<T> {
	public final Cuboid field_3484;
	public final Cuboid field_3486;
	public final Cuboid field_3482;
	public final Cuboid field_3479;
	public final Cuboid field_3483;
	private final Cuboid field_3485;
	private final Cuboid field_3481;
	private final boolean thinArms;

	public PlayerEntityModel(float f, boolean bl) {
		super(f, 0.0F, 64, 64);
		this.thinArms = bl;
		this.field_3481 = new Cuboid(this, 24, 0);
		this.field_3481.addBox(-3.0F, -6.0F, -1.0F, 6, 6, 1, f);
		this.field_3485 = new Cuboid(this, 0, 0);
		this.field_3485.setTextureSize(64, 32);
		this.field_3485.addBox(-5.0F, 0.0F, -1.0F, 10, 16, 1, f);
		if (bl) {
			this.field_3390 = new Cuboid(this, 32, 48);
			this.field_3390.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, f);
			this.field_3390.setRotationPoint(5.0F, 2.5F, 0.0F);
			this.field_3401 = new Cuboid(this, 40, 16);
			this.field_3401.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, f);
			this.field_3401.setRotationPoint(-5.0F, 2.5F, 0.0F);
			this.field_3484 = new Cuboid(this, 48, 48);
			this.field_3484.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, f + 0.25F);
			this.field_3484.setRotationPoint(5.0F, 2.5F, 0.0F);
			this.field_3486 = new Cuboid(this, 40, 32);
			this.field_3486.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, f + 0.25F);
			this.field_3486.setRotationPoint(-5.0F, 2.5F, 10.0F);
		} else {
			this.field_3390 = new Cuboid(this, 32, 48);
			this.field_3390.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, f);
			this.field_3390.setRotationPoint(5.0F, 2.0F, 0.0F);
			this.field_3484 = new Cuboid(this, 48, 48);
			this.field_3484.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, f + 0.25F);
			this.field_3484.setRotationPoint(5.0F, 2.0F, 0.0F);
			this.field_3486 = new Cuboid(this, 40, 32);
			this.field_3486.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, f + 0.25F);
			this.field_3486.setRotationPoint(-5.0F, 2.0F, 10.0F);
		}

		this.field_3397 = new Cuboid(this, 16, 48);
		this.field_3397.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
		this.field_3397.setRotationPoint(1.9F, 12.0F, 0.0F);
		this.field_3482 = new Cuboid(this, 0, 48);
		this.field_3482.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, f + 0.25F);
		this.field_3482.setRotationPoint(1.9F, 12.0F, 0.0F);
		this.field_3479 = new Cuboid(this, 0, 32);
		this.field_3479.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, f + 0.25F);
		this.field_3479.setRotationPoint(-1.9F, 12.0F, 0.0F);
		this.field_3483 = new Cuboid(this, 16, 32);
		this.field_3483.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, f + 0.25F);
		this.field_3483.setRotationPoint(0.0F, 0.0F, 0.0F);
	}

	@Override
	public void method_17088(T livingEntity, float f, float g, float h, float i, float j, float k) {
		super.method_17088(livingEntity, f, g, h, i, j, k);
		GlStateManager.pushMatrix();
		if (this.isChild) {
			float l = 2.0F;
			GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			GlStateManager.translatef(0.0F, 24.0F * k, 0.0F);
			this.field_3482.render(k);
			this.field_3479.render(k);
			this.field_3484.render(k);
			this.field_3486.render(k);
			this.field_3483.render(k);
		} else {
			if (livingEntity.isSneaking()) {
				GlStateManager.translatef(0.0F, 0.2F, 0.0F);
			}

			this.field_3482.render(k);
			this.field_3479.render(k);
			this.field_3484.render(k);
			this.field_3486.render(k);
			this.field_3483.render(k);
		}

		GlStateManager.popMatrix();
	}

	public void method_2824(float f) {
		this.field_3481.copyRotation(this.field_3398);
		this.field_3481.rotationPointX = 0.0F;
		this.field_3481.rotationPointY = 0.0F;
		this.field_3481.render(f);
	}

	public void method_2823(float f) {
		this.field_3485.render(f);
	}

	@Override
	public void method_17087(T livingEntity, float f, float g, float h, float i, float j, float k) {
		super.method_17087(livingEntity, f, g, h, i, j, k);
		this.field_3482.copyRotation(this.field_3397);
		this.field_3479.copyRotation(this.field_3392);
		this.field_3484.copyRotation(this.field_3390);
		this.field_3486.copyRotation(this.field_3401);
		this.field_3483.copyRotation(this.field_3391);
		if (livingEntity.isSneaking()) {
			this.field_3485.rotationPointY = 2.0F;
		} else {
			this.field_3485.rotationPointY = 0.0F;
		}
	}

	@Override
	public void setVisible(boolean bl) {
		super.setVisible(bl);
		this.field_3484.visible = bl;
		this.field_3486.visible = bl;
		this.field_3482.visible = bl;
		this.field_3479.visible = bl;
		this.field_3483.visible = bl;
		this.field_3485.visible = bl;
		this.field_3481.visible = bl;
	}

	@Override
	public void method_2803(float f, OptionMainHand optionMainHand) {
		Cuboid cuboid = this.method_2808(optionMainHand);
		if (this.thinArms) {
			float g = 0.5F * (float)(optionMainHand == OptionMainHand.field_6183 ? 1 : -1);
			cuboid.rotationPointX += g;
			cuboid.applyTransform(f);
			cuboid.rotationPointX -= g;
		} else {
			cuboid.applyTransform(f);
		}
	}
}
