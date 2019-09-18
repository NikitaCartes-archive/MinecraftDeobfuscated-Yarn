package net.minecraft.client.render.entity.model;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;

@Environment(EnvType.CLIENT)
public class PlayerEntityModel<T extends LivingEntity> extends BipedEntityModel<T> {
	private List<ModelPart> field_20787 = Lists.<ModelPart>newArrayList();
	public final ModelPart leftArmOverlay;
	public final ModelPart rightArmOverlay;
	public final ModelPart leftLegOverlay;
	public final ModelPart rightLegOverlay;
	public final ModelPart bodyOverlay;
	private final ModelPart cape;
	private final ModelPart ears;
	private final boolean thinArms;

	public PlayerEntityModel(float f, boolean bl) {
		super(f, 0.0F, 64, 64);
		this.thinArms = bl;
		this.ears = new ModelPart(this, 24, 0);
		this.ears.addCuboid(-3.0F, -6.0F, -1.0F, 6.0F, 6.0F, 1.0F, f);
		this.cape = new ModelPart(this, 0, 0);
		this.cape.setTextureSize(64, 32);
		this.cape.addCuboid(-5.0F, 0.0F, -1.0F, 10.0F, 16.0F, 1.0F, f);
		if (bl) {
			this.leftArm = new ModelPart(this, 32, 48);
			this.leftArm.addCuboid(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, f);
			this.leftArm.setRotationPoint(5.0F, 2.5F, 0.0F);
			this.rightArm = new ModelPart(this, 40, 16);
			this.rightArm.addCuboid(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, f);
			this.rightArm.setRotationPoint(-5.0F, 2.5F, 0.0F);
			this.leftArmOverlay = new ModelPart(this, 48, 48);
			this.leftArmOverlay.addCuboid(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, f + 0.25F);
			this.leftArmOverlay.setRotationPoint(5.0F, 2.5F, 0.0F);
			this.rightArmOverlay = new ModelPart(this, 40, 32);
			this.rightArmOverlay.addCuboid(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, f + 0.25F);
			this.rightArmOverlay.setRotationPoint(-5.0F, 2.5F, 10.0F);
		} else {
			this.leftArm = new ModelPart(this, 32, 48);
			this.leftArm.addCuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, f);
			this.leftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
			this.leftArmOverlay = new ModelPart(this, 48, 48);
			this.leftArmOverlay.addCuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, f + 0.25F);
			this.leftArmOverlay.setRotationPoint(5.0F, 2.0F, 0.0F);
			this.rightArmOverlay = new ModelPart(this, 40, 32);
			this.rightArmOverlay.addCuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, f + 0.25F);
			this.rightArmOverlay.setRotationPoint(-5.0F, 2.0F, 10.0F);
		}

		this.leftLeg = new ModelPart(this, 16, 48);
		this.leftLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, f);
		this.leftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
		this.leftLegOverlay = new ModelPart(this, 0, 48);
		this.leftLegOverlay.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, f + 0.25F);
		this.leftLegOverlay.setRotationPoint(1.9F, 12.0F, 0.0F);
		this.rightLegOverlay = new ModelPart(this, 0, 32);
		this.rightLegOverlay.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, f + 0.25F);
		this.rightLegOverlay.setRotationPoint(-1.9F, 12.0F, 0.0F);
		this.bodyOverlay = new ModelPart(this, 16, 32);
		this.bodyOverlay.addCuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, f + 0.25F);
		this.bodyOverlay.setRotationPoint(0.0F, 0.0F, 0.0F);
	}

	@Override
	public void method_17088(T livingEntity, float f, float g, float h, float i, float j, float k) {
		super.method_17088(livingEntity, f, g, h, i, j, k);
		RenderSystem.pushMatrix();
		if (this.isChild) {
			float l = 2.0F;
			RenderSystem.scalef(0.5F, 0.5F, 0.5F);
			RenderSystem.translatef(0.0F, 24.0F * k, 0.0F);
			this.leftLegOverlay.render(k);
			this.rightLegOverlay.render(k);
			this.leftArmOverlay.render(k);
			this.rightArmOverlay.render(k);
			this.bodyOverlay.render(k);
		} else {
			if (livingEntity.isInSneakingPose()) {
				RenderSystem.translatef(0.0F, 0.2F, 0.0F);
			}

			this.leftLegOverlay.render(k);
			this.rightLegOverlay.render(k);
			this.leftArmOverlay.render(k);
			this.rightArmOverlay.render(k);
			this.bodyOverlay.render(k);
		}

		RenderSystem.popMatrix();
	}

	public void renderEars(float f) {
		this.ears.copyRotation(this.head);
		this.ears.rotationPointX = 0.0F;
		this.ears.rotationPointY = 0.0F;
		this.ears.render(f);
	}

	public void renderCape(float f) {
		this.cape.render(f);
	}

	@Override
	public void method_17087(T livingEntity, float f, float g, float h, float i, float j, float k) {
		super.method_17087(livingEntity, f, g, h, i, j, k);
		this.leftLegOverlay.copyRotation(this.leftLeg);
		this.rightLegOverlay.copyRotation(this.rightLeg);
		this.leftArmOverlay.copyRotation(this.leftArm);
		this.rightArmOverlay.copyRotation(this.rightArm);
		this.bodyOverlay.copyRotation(this.body);
		if (livingEntity.isInSneakingPose()) {
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
		this.ears.visible = bl;
	}

	@Override
	public void setArmAngle(float f, Arm arm) {
		ModelPart modelPart = this.getArm(arm);
		if (this.thinArms) {
			float g = 0.5F * (float)(arm == Arm.RIGHT ? 1 : -1);
			modelPart.rotationPointX += g;
			modelPart.applyTransform(f);
			modelPart.rotationPointX -= g;
		} else {
			modelPart.applyTransform(f);
		}
	}

	public ModelPart method_22697(Random random) {
		return (ModelPart)this.field_20787.get(random.nextInt(this.field_20787.size()));
	}

	@Override
	public void method_22696(ModelPart modelPart) {
		if (this.field_20787 == null) {
			this.field_20787 = Lists.<ModelPart>newArrayList();
		}

		this.field_20787.add(modelPart);
	}
}
