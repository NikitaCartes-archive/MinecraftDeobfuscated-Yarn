package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttacker;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sortme.OptionMainHand;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class StrayEntityModel<T extends LivingEntity & RangedAttacker> extends BipedEntityModel<T> {
	public StrayEntityModel() {
		this(0.0F, false);
	}

	public StrayEntityModel(float f, boolean bl) {
		super(f, 0.0F, 64, 32);
		if (!bl) {
			this.armRight = new Cuboid(this, 40, 16);
			this.armRight.addBox(-1.0F, -2.0F, -1.0F, 2, 12, 2, f);
			this.armRight.setRotationPoint(-5.0F, 2.0F, 0.0F);
			this.armLeft = new Cuboid(this, 40, 16);
			this.armLeft.mirror = true;
			this.armLeft.addBox(-1.0F, -2.0F, -1.0F, 2, 12, 2, f);
			this.armLeft.setRotationPoint(5.0F, 2.0F, 0.0F);
			this.legRight = new Cuboid(this, 0, 16);
			this.legRight.addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2, f);
			this.legRight.setRotationPoint(-2.0F, 12.0F, 0.0F);
			this.legLeft = new Cuboid(this, 0, 16);
			this.legLeft.mirror = true;
			this.legLeft.addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2, f);
			this.legLeft.setRotationPoint(2.0F, 12.0F, 0.0F);
		}
	}

	@Override
	public void method_17086(T livingEntity, float f, float g, float h) {
		this.armPoseRight = BipedEntityModel.ArmPose.field_3409;
		this.armPoseLeft = BipedEntityModel.ArmPose.field_3409;
		ItemStack itemStack = livingEntity.getStackInHand(Hand.MAIN);
		if (itemStack.getItem() == Items.field_8102 && livingEntity.hasArmsRaised()) {
			if (livingEntity.getMainHand() == OptionMainHand.field_6183) {
				this.armPoseRight = BipedEntityModel.ArmPose.field_3403;
			} else {
				this.armPoseLeft = BipedEntityModel.ArmPose.field_3403;
			}
		}

		super.method_17086(livingEntity, f, g, h);
	}

	@Override
	public void method_17087(T livingEntity, float f, float g, float h, float i, float j, float k) {
		super.method_17087(livingEntity, f, g, h, i, j, k);
		ItemStack itemStack = livingEntity.getMainHandStack();
		if (livingEntity.hasArmsRaised() && (itemStack.isEmpty() || itemStack.getItem() != Items.field_8102)) {
			float l = MathHelper.sin(this.swingProgress * (float) Math.PI);
			float m = MathHelper.sin((1.0F - (1.0F - this.swingProgress) * (1.0F - this.swingProgress)) * (float) Math.PI);
			this.armRight.roll = 0.0F;
			this.armLeft.roll = 0.0F;
			this.armRight.yaw = -(0.1F - l * 0.6F);
			this.armLeft.yaw = 0.1F - l * 0.6F;
			this.armRight.pitch = (float) (-Math.PI / 2);
			this.armLeft.pitch = (float) (-Math.PI / 2);
			this.armRight.pitch -= l * 1.2F - m * 0.4F;
			this.armLeft.pitch -= l * 1.2F - m * 0.4F;
			this.armRight.roll = this.armRight.roll + MathHelper.cos(h * 0.09F) * 0.05F + 0.05F;
			this.armLeft.roll = this.armLeft.roll - (MathHelper.cos(h * 0.09F) * 0.05F + 0.05F);
			this.armRight.pitch = this.armRight.pitch + MathHelper.sin(h * 0.067F) * 0.05F;
			this.armLeft.pitch = this.armLeft.pitch - MathHelper.sin(h * 0.067F) * 0.05F;
		}
	}

	@Override
	public void method_2803(float f, OptionMainHand optionMainHand) {
		float g = optionMainHand == OptionMainHand.field_6183 ? 1.0F : -1.0F;
		Cuboid cuboid = this.getArm(optionMainHand);
		cuboid.rotationPointX += g;
		cuboid.applyTransform(f);
		cuboid.rotationPointX -= g;
	}
}
