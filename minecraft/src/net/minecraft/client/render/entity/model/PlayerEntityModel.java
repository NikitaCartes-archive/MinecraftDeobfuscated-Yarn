package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;

@Environment(EnvType.CLIENT)
public class PlayerEntityModel<T extends LivingEntity> extends BipedEntityModel<T> {
	public final ModelPart leftSleeve;
	public final ModelPart rightSleeve;
	public final ModelPart leftPantLeg;
	public final ModelPart rightPantLeg;
	public final ModelPart jacket;
	private final ModelPart cape;
	private final ModelPart ears;
	private final boolean thinArms;

	public PlayerEntityModel(float scale, boolean thinArms) {
		super(scale, 0.0F, 64, 64);
		this.thinArms = thinArms;
		this.ears = new ModelPart(this, 24, 0);
		this.ears.addCuboid(-3.0F, -6.0F, -1.0F, 6, 6, 1, scale);
		this.cape = new ModelPart(this, 0, 0);
		this.cape.setTextureSize(64, 32);
		this.cape.addCuboid(-5.0F, 0.0F, -1.0F, 10, 16, 1, scale);
		if (thinArms) {
			this.leftArm = new ModelPart(this, 32, 48);
			this.leftArm.addCuboid(-1.0F, -2.0F, -2.0F, 3, 12, 4, scale);
			this.leftArm.setPivot(5.0F, 2.5F, 0.0F);
			this.rightArm = new ModelPart(this, 40, 16);
			this.rightArm.addCuboid(-2.0F, -2.0F, -2.0F, 3, 12, 4, scale);
			this.rightArm.setPivot(-5.0F, 2.5F, 0.0F);
			this.leftSleeve = new ModelPart(this, 48, 48);
			this.leftSleeve.addCuboid(-1.0F, -2.0F, -2.0F, 3, 12, 4, scale + 0.25F);
			this.leftSleeve.setPivot(5.0F, 2.5F, 0.0F);
			this.rightSleeve = new ModelPart(this, 40, 32);
			this.rightSleeve.addCuboid(-2.0F, -2.0F, -2.0F, 3, 12, 4, scale + 0.25F);
			this.rightSleeve.setPivot(-5.0F, 2.5F, 10.0F);
		} else {
			this.leftArm = new ModelPart(this, 32, 48);
			this.leftArm.addCuboid(-1.0F, -2.0F, -2.0F, 4, 12, 4, scale);
			this.leftArm.setPivot(5.0F, 2.0F, 0.0F);
			this.leftSleeve = new ModelPart(this, 48, 48);
			this.leftSleeve.addCuboid(-1.0F, -2.0F, -2.0F, 4, 12, 4, scale + 0.25F);
			this.leftSleeve.setPivot(5.0F, 2.0F, 0.0F);
			this.rightSleeve = new ModelPart(this, 40, 32);
			this.rightSleeve.addCuboid(-3.0F, -2.0F, -2.0F, 4, 12, 4, scale + 0.25F);
			this.rightSleeve.setPivot(-5.0F, 2.0F, 10.0F);
		}

		this.leftLeg = new ModelPart(this, 16, 48);
		this.leftLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale);
		this.leftLeg.setPivot(1.9F, 12.0F, 0.0F);
		this.leftPantLeg = new ModelPart(this, 0, 48);
		this.leftPantLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale + 0.25F);
		this.leftPantLeg.setPivot(1.9F, 12.0F, 0.0F);
		this.rightPantLeg = new ModelPart(this, 0, 32);
		this.rightPantLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale + 0.25F);
		this.rightPantLeg.setPivot(-1.9F, 12.0F, 0.0F);
		this.jacket = new ModelPart(this, 16, 32);
		this.jacket.addCuboid(-4.0F, 0.0F, -2.0F, 8, 12, 4, scale + 0.25F);
		this.jacket.setPivot(0.0F, 0.0F, 0.0F);
	}

	@Override
	public void render(T livingEntity, float f, float g, float h, float i, float j, float k) {
		super.render(livingEntity, f, g, h, i, j, k);
		GlStateManager.pushMatrix();
		if (this.child) {
			float l = 2.0F;
			GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			GlStateManager.translatef(0.0F, 24.0F * k, 0.0F);
			this.leftPantLeg.render(k);
			this.rightPantLeg.render(k);
			this.leftSleeve.render(k);
			this.rightSleeve.render(k);
			this.jacket.render(k);
		} else {
			if (livingEntity.isInSneakingPose()) {
				GlStateManager.translatef(0.0F, 0.2F, 0.0F);
			}

			this.leftPantLeg.render(k);
			this.rightPantLeg.render(k);
			this.leftSleeve.render(k);
			this.rightSleeve.render(k);
			this.jacket.render(k);
		}

		GlStateManager.popMatrix();
	}

	public void renderEars(float scale) {
		this.ears.copyPositionAndRotation(this.head);
		this.ears.pivotX = 0.0F;
		this.ears.pivotY = 0.0F;
		this.ears.render(scale);
	}

	public void renderCape(float scale) {
		this.cape.render(scale);
	}

	@Override
	public void setAngles(T livingEntity, float f, float g, float h, float i, float j, float k) {
		super.setAngles(livingEntity, f, g, h, i, j, k);
		this.leftPantLeg.copyPositionAndRotation(this.leftLeg);
		this.rightPantLeg.copyPositionAndRotation(this.rightLeg);
		this.leftSleeve.copyPositionAndRotation(this.leftArm);
		this.rightSleeve.copyPositionAndRotation(this.rightArm);
		this.jacket.copyPositionAndRotation(this.torso);
		if (livingEntity.isInSneakingPose()) {
			this.cape.pivotY = 2.0F;
		} else {
			this.cape.pivotY = 0.0F;
		}
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		this.leftSleeve.visible = visible;
		this.rightSleeve.visible = visible;
		this.leftPantLeg.visible = visible;
		this.rightPantLeg.visible = visible;
		this.jacket.visible = visible;
		this.cape.visible = visible;
		this.ears.visible = visible;
	}

	@Override
	public void setArmAngle(float f, Arm arm) {
		ModelPart modelPart = this.getArm(arm);
		if (this.thinArms) {
			float g = 0.5F * (float)(arm == Arm.RIGHT ? 1 : -1);
			modelPart.pivotX += g;
			modelPart.applyTransform(f);
			modelPart.pivotX -= g;
		} else {
			modelPart.applyTransform(f);
		}
	}
}
