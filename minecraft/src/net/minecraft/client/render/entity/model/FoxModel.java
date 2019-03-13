package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class FoxModel<T extends FoxEntity> extends EntityModel<T> {
	private final Cuboid field_18015;
	private final Cuboid field_18016;
	private final Cuboid field_18017;
	private final Cuboid field_18018;
	private final Cuboid field_18019;
	private final Cuboid field_18020;
	private final Cuboid field_18021;
	private final Cuboid field_18022;
	private final Cuboid field_18023;
	private final Cuboid field_18024;
	private float field_18025;

	public FoxModel() {
		this.textureWidth = 48;
		this.textureHeight = 32;
		this.field_18015 = new Cuboid(this, 1, 5);
		this.field_18015.addBox(-5.0F, -2.0F, -5.0F, 8, 6, 6);
		this.field_18015.setRotationPoint(-1.0F, 16.5F, -3.0F);
		this.field_18016 = new Cuboid(this, 8, 1);
		this.field_18016.addBox(-5.0F, -4.0F, -4.0F, 2, 2, 1);
		this.field_18017 = new Cuboid(this, 15, 1);
		this.field_18017.addBox(1.0F, -4.0F, -4.0F, 2, 2, 1);
		this.field_18018 = new Cuboid(this, 6, 18);
		this.field_18018.addBox(-3.0F, 2.01F, -8.0F, 4, 2, 3);
		this.field_18015.addChild(this.field_18016);
		this.field_18015.addChild(this.field_18017);
		this.field_18015.addChild(this.field_18018);
		this.field_18019 = new Cuboid(this, 24, 15);
		this.field_18019.addBox(-5.0F, 3.999F, -3.5F, 6, 11, 6);
		this.field_18019.setRotationPoint(0.0F, 16.0F, -6.0F);
		float f = 0.001F;
		this.field_18020 = new Cuboid(this, 13, 24);
		this.field_18020.addBox(0.0F, 0.5F, -1.0F, 2, 6, 2, 0.001F);
		this.field_18020.setRotationPoint(-5.0F, 17.5F, 7.0F);
		this.field_18021 = new Cuboid(this, 4, 24);
		this.field_18021.addBox(0.0F, 0.5F, -1.0F, 2, 6, 2, 0.001F);
		this.field_18021.setRotationPoint(-1.0F, 17.5F, 7.0F);
		this.field_18022 = new Cuboid(this, 13, 24);
		this.field_18022.addBox(0.0F, 0.5F, -1.0F, 2, 6, 2, 0.001F);
		this.field_18022.setRotationPoint(-5.0F, 17.5F, 0.0F);
		this.field_18023 = new Cuboid(this, 4, 24);
		this.field_18023.addBox(0.0F, 0.5F, -1.0F, 2, 6, 2, 0.001F);
		this.field_18023.setRotationPoint(-1.0F, 17.5F, 0.0F);
		this.field_18024 = new Cuboid(this, 30, 0);
		this.field_18024.addBox(0.0F, 0.0F, -1.0F, 4, 9, 5);
		this.field_18024.setRotationPoint(-4.0F, 15.0F, -1.0F);
		this.field_18019.addChild(this.field_18024);
	}

	public void method_18330(T foxEntity, float f, float g, float h) {
		this.field_18019.pitch = (float) (Math.PI / 2);
		this.field_18024.pitch = -0.05235988F;
		this.field_18020.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
		this.field_18021.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
		this.field_18022.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
		this.field_18023.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
		this.field_18015.roll = foxEntity.getHeadRoll(h);
		this.field_18020.visible = true;
		this.field_18021.visible = true;
		this.field_18022.visible = true;
		this.field_18023.visible = true;
		this.field_18015.setRotationPoint(-1.0F, 16.5F, -3.0F);
		this.field_18019.setRotationPoint(0.0F, 16.0F, -6.0F);
		this.field_18019.roll = 0.0F;
		this.field_18015.yaw = 0.0F;
		this.field_18020.setRotationPoint(-5.0F, 17.5F, 7.0F);
		this.field_18021.setRotationPoint(-1.0F, 17.5F, 7.0F);
		if (foxEntity.isCrouching()) {
			this.field_18019.pitch = 1.6755161F;
			float i = foxEntity.getBodyRotationHeightOffset(h);
			this.field_18019.setRotationPoint(0.0F, 16.0F + foxEntity.getBodyRotationHeightOffset(h), -6.0F);
			this.field_18015.setRotationPoint(-1.0F, 16.5F + i, -3.0F);
		} else if (foxEntity.isSleeping()) {
			this.field_18019.roll = (float) (-Math.PI / 2);
			this.field_18019.setRotationPoint(0.0F, 19.0F + foxEntity.getBodyRotationHeightOffset(h), -6.0F);
			this.field_18024.pitch = (float) (-Math.PI * 5.0 / 6.0);
			if (this.isChild) {
				this.field_18024.pitch = -2.1816616F;
				this.field_18019.setRotationPoint(0.0F, 19.0F + foxEntity.getBodyRotationHeightOffset(h), -2.0F);
			}

			this.field_18015.setRotationPoint(1.0F, 19.49F, -3.0F);
			this.field_18015.yaw = (float) (-Math.PI * 2.0 / 3.0);
			this.field_18020.visible = false;
			this.field_18021.visible = false;
			this.field_18022.visible = false;
			this.field_18023.visible = false;
		} else if (foxEntity.isSitting()) {
			this.field_18019.pitch = (float) (Math.PI / 6);
			this.field_18019.setRotationPoint(0.0F, 9.0F, -3.0F);
			this.field_18024.pitch = (float) (Math.PI / 4);
			this.field_18024.setRotationPoint(-4.0F, 15.0F, -2.0F);
			this.field_18015.setRotationPoint(-1.0F, 10.0F, -0.25F);
			this.field_18015.pitch = 0.0F;
			if (this.isChild) {
				this.field_18015.setRotationPoint(-1.0F, 13.0F, -3.75F);
			}

			this.field_18020.pitch = (float) (-Math.PI * 5.0 / 12.0);
			this.field_18020.setRotationPoint(-5.0F, 21.5F, 6.75F);
			this.field_18021.pitch = (float) (-Math.PI * 5.0 / 12.0);
			this.field_18021.setRotationPoint(-1.0F, 21.5F, 6.75F);
			this.field_18022.pitch = (float) (-Math.PI / 12);
			this.field_18023.pitch = (float) (-Math.PI / 12);
		}
	}

	public void method_18331(T foxEntity, float f, float g, float h, float i, float j, float k) {
		super.render(foxEntity, f, g, h, i, j, k);
		this.method_18332(foxEntity, f, g, h, i, j, k);
		if (this.isChild) {
			GlStateManager.pushMatrix();
			float l = 0.75F;
			GlStateManager.scalef(0.75F, 0.75F, 0.75F);
			GlStateManager.translatef(2.0F * k, 8.0F * k, 3.35F * k);
			this.field_18015.render(k);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			float m = 0.5F;
			GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			GlStateManager.translatef(2.0F * k, 24.0F * k, 0.0F);
			this.field_18019.render(k);
			this.field_18020.render(k);
			this.field_18021.render(k);
			this.field_18022.render(k);
			this.field_18023.render(k);
			GlStateManager.popMatrix();
		} else {
			GlStateManager.pushMatrix();
			this.field_18015.render(k);
			this.field_18019.render(k);
			this.field_18020.render(k);
			this.field_18021.render(k);
			this.field_18022.render(k);
			this.field_18023.render(k);
			GlStateManager.popMatrix();
		}
	}

	public void method_18332(T foxEntity, float f, float g, float h, float i, float j, float k) {
		super.setAngles(foxEntity, f, g, h, i, j, k);
		if (foxEntity.isSleeping()) {
			this.field_18015.roll = MathHelper.cos(h * 0.027F) / 22.0F;
		}

		if (foxEntity.isCrouching()) {
			float l = MathHelper.cos(h) * 0.01F;
			this.field_18019.yaw = l;
			this.field_18020.roll = l;
			this.field_18021.roll = l;
			this.field_18022.roll = l / 2.0F;
			this.field_18023.roll = l / 2.0F;
		}

		if (foxEntity.isWalking()) {
			float l = 0.1F;
			this.field_18025 += 0.67F;
			this.field_18020.pitch = MathHelper.cos(this.field_18025 * 0.4662F) * 0.1F;
			this.field_18021.pitch = MathHelper.cos(this.field_18025 * 0.4662F + (float) Math.PI) * 0.1F;
			this.field_18022.pitch = MathHelper.cos(this.field_18025 * 0.4662F + (float) Math.PI) * 0.1F;
			this.field_18023.pitch = MathHelper.cos(this.field_18025 * 0.4662F) * 0.1F;
		}
	}
}
