package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class QuadrupedEntityModel<T extends Entity> extends EntityModel<T> {
	protected ModelPart head;
	protected ModelPart torso;
	protected ModelPart backRightLeg;
	protected ModelPart backLeftLeg;
	protected ModelPart frontRightLeg;
	protected ModelPart frontLeftLeg;
	protected float field_3540 = 8.0F;
	protected float field_3537 = 4.0F;

	public QuadrupedEntityModel(int i, float f) {
		this.head = new ModelPart(this, 0, 0);
		this.head.addCuboid(-4.0F, -4.0F, -8.0F, 8, 8, 8, f);
		this.head.setPivot(0.0F, (float)(18 - i), -6.0F);
		this.torso = new ModelPart(this, 28, 8);
		this.torso.addCuboid(-5.0F, -10.0F, -7.0F, 10, 16, 8, f);
		this.torso.setPivot(0.0F, (float)(17 - i), 2.0F);
		this.backRightLeg = new ModelPart(this, 0, 16);
		this.backRightLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, i, 4, f);
		this.backRightLeg.setPivot(-3.0F, (float)(24 - i), 7.0F);
		this.backLeftLeg = new ModelPart(this, 0, 16);
		this.backLeftLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, i, 4, f);
		this.backLeftLeg.setPivot(3.0F, (float)(24 - i), 7.0F);
		this.frontRightLeg = new ModelPart(this, 0, 16);
		this.frontRightLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, i, 4, f);
		this.frontRightLeg.setPivot(-3.0F, (float)(24 - i), -5.0F);
		this.frontLeftLeg = new ModelPart(this, 0, 16);
		this.frontLeftLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, i, 4, f);
		this.frontLeftLeg.setPivot(3.0F, (float)(24 - i), -5.0F);
	}

	@Override
	public void render(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
		this.setAngles(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
		if (this.child) {
			float f = 2.0F;
			GlStateManager.pushMatrix();
			GlStateManager.translatef(0.0F, this.field_3540 * scale, this.field_3537 * scale);
			this.head.render(scale);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			GlStateManager.translatef(0.0F, 24.0F * scale, 0.0F);
			this.torso.render(scale);
			this.backRightLeg.render(scale);
			this.backLeftLeg.render(scale);
			this.frontRightLeg.render(scale);
			this.frontLeftLeg.render(scale);
			GlStateManager.popMatrix();
		} else {
			this.head.render(scale);
			this.torso.render(scale);
			this.backRightLeg.render(scale);
			this.backLeftLeg.render(scale);
			this.frontRightLeg.render(scale);
			this.frontLeftLeg.render(scale);
		}
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
		this.head.pitch = headPitch * (float) (Math.PI / 180.0);
		this.head.yaw = headYaw * (float) (Math.PI / 180.0);
		this.torso.pitch = (float) (Math.PI / 2);
		this.backRightLeg.pitch = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
		this.backLeftLeg.pitch = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * 1.4F * limbDistance;
		this.frontRightLeg.pitch = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * 1.4F * limbDistance;
		this.frontLeftLeg.pitch = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
	}
}
