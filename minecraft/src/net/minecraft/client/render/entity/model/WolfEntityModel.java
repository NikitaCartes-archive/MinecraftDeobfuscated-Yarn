package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class WolfEntityModel<T extends WolfEntity> extends EntityModel<T> {
	private final ModelPart head;
	private final ModelPart field_3623;
	private final ModelPart field_3622;
	private final ModelPart field_3620;
	private final ModelPart field_3618;
	private final ModelPart field_3624;
	private final ModelPart field_3617;
	private final ModelPart field_3619;

	public WolfEntityModel() {
		float f = 0.0F;
		float g = 13.5F;
		this.head = new ModelPart(this, 0, 0);
		this.head.addCuboid(-2.0F, -3.0F, -2.0F, 6, 6, 4, 0.0F);
		this.head.setPivot(-1.0F, 13.5F, -7.0F);
		this.field_3623 = new ModelPart(this, 18, 14);
		this.field_3623.addCuboid(-3.0F, -2.0F, -3.0F, 6, 9, 6, 0.0F);
		this.field_3623.setPivot(0.0F, 14.0F, 2.0F);
		this.field_3619 = new ModelPart(this, 21, 0);
		this.field_3619.addCuboid(-3.0F, -3.0F, -3.0F, 8, 6, 7, 0.0F);
		this.field_3619.setPivot(-1.0F, 14.0F, 2.0F);
		this.field_3622 = new ModelPart(this, 0, 18);
		this.field_3622.addCuboid(0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
		this.field_3622.setPivot(-2.5F, 16.0F, 7.0F);
		this.field_3620 = new ModelPart(this, 0, 18);
		this.field_3620.addCuboid(0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
		this.field_3620.setPivot(0.5F, 16.0F, 7.0F);
		this.field_3618 = new ModelPart(this, 0, 18);
		this.field_3618.addCuboid(0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
		this.field_3618.setPivot(-2.5F, 16.0F, -4.0F);
		this.field_3624 = new ModelPart(this, 0, 18);
		this.field_3624.addCuboid(0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
		this.field_3624.setPivot(0.5F, 16.0F, -4.0F);
		this.field_3617 = new ModelPart(this, 9, 18);
		this.field_3617.addCuboid(0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
		this.field_3617.setPivot(-1.0F, 12.0F, 8.0F);
		this.head.setTextureOffset(16, 14).addCuboid(-2.0F, -5.0F, 0.0F, 2, 2, 1, 0.0F);
		this.head.setTextureOffset(16, 14).addCuboid(2.0F, -5.0F, 0.0F, 2, 2, 1, 0.0F);
		this.head.setTextureOffset(0, 10).addCuboid(-0.5F, 0.0F, -5.0F, 3, 3, 4, 0.0F);
	}

	public void render(T wolfEntity, float f, float g, float h, float i, float j, float k) {
		super.render(wolfEntity, f, g, h, i, j, k);
		this.setAngles(wolfEntity, f, g, h, i, j, k);
		if (this.child) {
			float l = 2.0F;
			GlStateManager.pushMatrix();
			GlStateManager.translatef(0.0F, 5.0F * k, 2.0F * k);
			this.head.method_2852(k);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			GlStateManager.translatef(0.0F, 24.0F * k, 0.0F);
			this.field_3623.render(k);
			this.field_3622.render(k);
			this.field_3620.render(k);
			this.field_3618.render(k);
			this.field_3624.render(k);
			this.field_3617.method_2852(k);
			this.field_3619.render(k);
			GlStateManager.popMatrix();
		} else {
			this.head.method_2852(k);
			this.field_3623.render(k);
			this.field_3622.render(k);
			this.field_3620.render(k);
			this.field_3618.render(k);
			this.field_3624.render(k);
			this.field_3617.method_2852(k);
			this.field_3619.render(k);
		}
	}

	public void animateModel(T wolfEntity, float f, float g, float h) {
		if (wolfEntity.isAngry()) {
			this.field_3617.yaw = 0.0F;
		} else {
			this.field_3617.yaw = MathHelper.cos(f * 0.6662F) * 1.4F * g;
		}

		if (wolfEntity.isSitting()) {
			this.field_3619.setPivot(-1.0F, 16.0F, -3.0F);
			this.field_3619.pitch = (float) (Math.PI * 2.0 / 5.0);
			this.field_3619.yaw = 0.0F;
			this.field_3623.setPivot(0.0F, 18.0F, 0.0F);
			this.field_3623.pitch = (float) (Math.PI / 4);
			this.field_3617.setPivot(-1.0F, 21.0F, 6.0F);
			this.field_3622.setPivot(-2.5F, 22.0F, 2.0F);
			this.field_3622.pitch = (float) (Math.PI * 3.0 / 2.0);
			this.field_3620.setPivot(0.5F, 22.0F, 2.0F);
			this.field_3620.pitch = (float) (Math.PI * 3.0 / 2.0);
			this.field_3618.pitch = 5.811947F;
			this.field_3618.setPivot(-2.49F, 17.0F, -4.0F);
			this.field_3624.pitch = 5.811947F;
			this.field_3624.setPivot(0.51F, 17.0F, -4.0F);
		} else {
			this.field_3623.setPivot(0.0F, 14.0F, 2.0F);
			this.field_3623.pitch = (float) (Math.PI / 2);
			this.field_3619.setPivot(-1.0F, 14.0F, -3.0F);
			this.field_3619.pitch = this.field_3623.pitch;
			this.field_3617.setPivot(-1.0F, 12.0F, 8.0F);
			this.field_3622.setPivot(-2.5F, 16.0F, 7.0F);
			this.field_3620.setPivot(0.5F, 16.0F, 7.0F);
			this.field_3618.setPivot(-2.5F, 16.0F, -4.0F);
			this.field_3624.setPivot(0.5F, 16.0F, -4.0F);
			this.field_3622.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
			this.field_3620.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
			this.field_3618.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g;
			this.field_3624.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g;
		}

		this.head.roll = wolfEntity.getBegAnimationProgress(h) + wolfEntity.getShakeAnimationProgress(h, 0.0F);
		this.field_3619.roll = wolfEntity.getShakeAnimationProgress(h, -0.08F);
		this.field_3623.roll = wolfEntity.getShakeAnimationProgress(h, -0.16F);
		this.field_3617.roll = wolfEntity.getShakeAnimationProgress(h, -0.2F);
	}

	public void setAngles(T wolfEntity, float f, float g, float h, float i, float j, float k) {
		super.setAngles(wolfEntity, f, g, h, i, j, k);
		this.head.pitch = j * (float) (Math.PI / 180.0);
		this.head.yaw = i * (float) (Math.PI / 180.0);
		this.field_3617.pitch = h;
	}
}
