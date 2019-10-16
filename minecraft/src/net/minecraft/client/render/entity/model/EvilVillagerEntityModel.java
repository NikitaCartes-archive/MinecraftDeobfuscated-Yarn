package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class EvilVillagerEntityModel<T extends IllagerEntity> extends CompositeEntityModel<T> implements ModelWithArms, ModelWithHead {
	private final ModelPart field_3422;
	private final ModelPart field_3419;
	private final ModelPart field_3425;
	private final ModelPart field_3423;
	private final ModelPart field_3420;
	private final ModelPart field_3418;
	private final ModelPart field_3426;
	private final ModelPart field_3417;
	private float field_3424;

	public EvilVillagerEntityModel(float f, float g, int i, int j) {
		this.field_3422 = new ModelPart(this).setTextureSize(i, j);
		this.field_3422.setPivot(0.0F, 0.0F + g, 0.0F);
		this.field_3422.setTextureOffset(0, 0).addCuboid(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, f);
		this.field_3419 = new ModelPart(this, 32, 0).setTextureSize(i, j);
		this.field_3419.addCuboid(-4.0F, -10.0F, -4.0F, 8.0F, 12.0F, 8.0F, f + 0.45F);
		this.field_3422.addChild(this.field_3419);
		this.field_3419.visible = false;
		ModelPart modelPart = new ModelPart(this).setTextureSize(i, j);
		modelPart.setPivot(0.0F, g - 2.0F, 0.0F);
		modelPart.setTextureOffset(24, 0).addCuboid(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, f);
		this.field_3422.addChild(modelPart);
		this.field_3425 = new ModelPart(this).setTextureSize(i, j);
		this.field_3425.setPivot(0.0F, 0.0F + g, 0.0F);
		this.field_3425.setTextureOffset(16, 20).addCuboid(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, f);
		this.field_3425.setTextureOffset(0, 38).addCuboid(-4.0F, 0.0F, -3.0F, 8.0F, 18.0F, 6.0F, f + 0.5F);
		this.field_3423 = new ModelPart(this).setTextureSize(i, j);
		this.field_3423.setPivot(0.0F, 0.0F + g + 2.0F, 0.0F);
		this.field_3423.setTextureOffset(44, 22).addCuboid(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, f);
		ModelPart modelPart2 = new ModelPart(this, 44, 22).setTextureSize(i, j);
		modelPart2.mirror = true;
		modelPart2.addCuboid(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, f);
		this.field_3423.addChild(modelPart2);
		this.field_3423.setTextureOffset(40, 38).addCuboid(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F, f);
		this.field_3420 = new ModelPart(this, 0, 22).setTextureSize(i, j);
		this.field_3420.setPivot(-2.0F, 12.0F + g, 0.0F);
		this.field_3420.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, f);
		this.field_3418 = new ModelPart(this, 0, 22).setTextureSize(i, j);
		this.field_3418.mirror = true;
		this.field_3418.setPivot(2.0F, 12.0F + g, 0.0F);
		this.field_3418.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, f);
		this.field_3426 = new ModelPart(this, 40, 46).setTextureSize(i, j);
		this.field_3426.addCuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, f);
		this.field_3426.setPivot(-5.0F, 2.0F + g, 0.0F);
		this.field_3417 = new ModelPart(this, 40, 46).setTextureSize(i, j);
		this.field_3417.mirror = true;
		this.field_3417.addCuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, f);
		this.field_3417.setPivot(5.0F, 2.0F + g, 0.0F);
	}

	@Override
	public Iterable<ModelPart> getParts() {
		return ImmutableList.<ModelPart>of(this.field_3422, this.field_3425, this.field_3420, this.field_3418, this.field_3423, this.field_3426, this.field_3417);
	}

	public void method_17094(T illagerEntity, float f, float g, float h, float i, float j, float k) {
		this.field_3422.yaw = i * (float) (Math.PI / 180.0);
		this.field_3422.pitch = j * (float) (Math.PI / 180.0);
		this.field_3423.pivotY = 3.0F;
		this.field_3423.pivotZ = -1.0F;
		this.field_3423.pitch = -0.75F;
		if (this.isRiding) {
			this.field_3426.pitch = (float) (-Math.PI / 5);
			this.field_3426.yaw = 0.0F;
			this.field_3426.roll = 0.0F;
			this.field_3417.pitch = (float) (-Math.PI / 5);
			this.field_3417.yaw = 0.0F;
			this.field_3417.roll = 0.0F;
			this.field_3420.pitch = -1.4137167F;
			this.field_3420.yaw = (float) (Math.PI / 10);
			this.field_3420.roll = 0.07853982F;
			this.field_3418.pitch = -1.4137167F;
			this.field_3418.yaw = (float) (-Math.PI / 10);
			this.field_3418.roll = -0.07853982F;
		} else {
			this.field_3426.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 2.0F * g * 0.5F;
			this.field_3426.yaw = 0.0F;
			this.field_3426.roll = 0.0F;
			this.field_3417.pitch = MathHelper.cos(f * 0.6662F) * 2.0F * g * 0.5F;
			this.field_3417.yaw = 0.0F;
			this.field_3417.roll = 0.0F;
			this.field_3420.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g * 0.5F;
			this.field_3420.yaw = 0.0F;
			this.field_3420.roll = 0.0F;
			this.field_3418.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g * 0.5F;
			this.field_3418.yaw = 0.0F;
			this.field_3418.roll = 0.0F;
		}

		IllagerEntity.State state = illagerEntity.getState();
		if (state == IllagerEntity.State.ATTACKING) {
			float l = MathHelper.sin(this.handSwingProgress * (float) Math.PI);
			float m = MathHelper.sin((1.0F - (1.0F - this.handSwingProgress) * (1.0F - this.handSwingProgress)) * (float) Math.PI);
			this.field_3426.roll = 0.0F;
			this.field_3417.roll = 0.0F;
			this.field_3426.yaw = (float) (Math.PI / 20);
			this.field_3417.yaw = (float) (-Math.PI / 20);
			if (illagerEntity.getMainArm() == Arm.RIGHT) {
				this.field_3426.pitch = -1.8849558F + MathHelper.cos(h * 0.09F) * 0.15F;
				this.field_3417.pitch = -0.0F + MathHelper.cos(h * 0.19F) * 0.5F;
				this.field_3426.pitch += l * 2.2F - m * 0.4F;
				this.field_3417.pitch += l * 1.2F - m * 0.4F;
			} else {
				this.field_3426.pitch = -0.0F + MathHelper.cos(h * 0.19F) * 0.5F;
				this.field_3417.pitch = -1.8849558F + MathHelper.cos(h * 0.09F) * 0.15F;
				this.field_3426.pitch += l * 1.2F - m * 0.4F;
				this.field_3417.pitch += l * 2.2F - m * 0.4F;
			}

			this.field_3426.roll = this.field_3426.roll + MathHelper.cos(h * 0.09F) * 0.05F + 0.05F;
			this.field_3417.roll = this.field_3417.roll - (MathHelper.cos(h * 0.09F) * 0.05F + 0.05F);
			this.field_3426.pitch = this.field_3426.pitch + MathHelper.sin(h * 0.067F) * 0.05F;
			this.field_3417.pitch = this.field_3417.pitch - MathHelper.sin(h * 0.067F) * 0.05F;
		} else if (state == IllagerEntity.State.SPELLCASTING) {
			this.field_3426.pivotZ = 0.0F;
			this.field_3426.pivotX = -5.0F;
			this.field_3417.pivotZ = 0.0F;
			this.field_3417.pivotX = 5.0F;
			this.field_3426.pitch = MathHelper.cos(h * 0.6662F) * 0.25F;
			this.field_3417.pitch = MathHelper.cos(h * 0.6662F) * 0.25F;
			this.field_3426.roll = (float) (Math.PI * 3.0 / 4.0);
			this.field_3417.roll = (float) (-Math.PI * 3.0 / 4.0);
			this.field_3426.yaw = 0.0F;
			this.field_3417.yaw = 0.0F;
		} else if (state == IllagerEntity.State.BOW_AND_ARROW) {
			this.field_3426.yaw = -0.1F + this.field_3422.yaw;
			this.field_3426.pitch = (float) (-Math.PI / 2) + this.field_3422.pitch;
			this.field_3417.pitch = -0.9424779F + this.field_3422.pitch;
			this.field_3417.yaw = this.field_3422.yaw - 0.4F;
			this.field_3417.roll = (float) (Math.PI / 2);
		} else if (state == IllagerEntity.State.CROSSBOW_HOLD) {
			this.field_3426.yaw = -0.3F + this.field_3422.yaw;
			this.field_3417.yaw = 0.6F + this.field_3422.yaw;
			this.field_3426.pitch = (float) (-Math.PI / 2) + this.field_3422.pitch + 0.1F;
			this.field_3417.pitch = -1.5F + this.field_3422.pitch;
		} else if (state == IllagerEntity.State.CROSSBOW_CHARGE) {
			this.field_3426.yaw = -0.8F;
			this.field_3426.pitch = -0.97079635F;
			this.field_3417.pitch = -0.97079635F;
			float l = MathHelper.clamp(this.field_3424, 0.0F, 25.0F);
			this.field_3417.yaw = MathHelper.lerp(l / 25.0F, 0.4F, 0.85F);
			this.field_3417.pitch = MathHelper.lerp(l / 25.0F, this.field_3417.pitch, (float) (-Math.PI / 2));
		} else if (state == IllagerEntity.State.CELEBRATING) {
			this.field_3426.pivotZ = 0.0F;
			this.field_3426.pivotX = -5.0F;
			this.field_3426.pitch = MathHelper.cos(h * 0.6662F) * 0.05F;
			this.field_3426.roll = 2.670354F;
			this.field_3426.yaw = 0.0F;
			this.field_3417.pivotZ = 0.0F;
			this.field_3417.pivotX = 5.0F;
			this.field_3417.pitch = MathHelper.cos(h * 0.6662F) * 0.05F;
			this.field_3417.roll = (float) (-Math.PI * 3.0 / 4.0);
			this.field_3417.yaw = 0.0F;
		}

		boolean bl = state == IllagerEntity.State.CROSSED;
		this.field_3423.visible = bl;
		this.field_3417.visible = !bl;
		this.field_3426.visible = !bl;
	}

	public void method_17092(T illagerEntity, float f, float g, float h) {
		this.field_3424 = (float)illagerEntity.getItemUseTime();
		super.animateModel(illagerEntity, f, g, h);
	}

	private ModelPart method_2813(Arm arm) {
		return arm == Arm.LEFT ? this.field_3417 : this.field_3426;
	}

	public ModelPart method_2812() {
		return this.field_3419;
	}

	@Override
	public ModelPart getHead() {
		return this.field_3422;
	}

	@Override
	public void setArmAngle(float f, Arm arm, MatrixStack matrixStack) {
		this.method_2813(arm).rotate(matrixStack, 0.0625F);
	}
}
