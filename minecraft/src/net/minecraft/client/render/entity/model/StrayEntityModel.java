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
			this.field_3401 = new Cuboid(this, 40, 16);
			this.field_3401.addBox(-1.0F, -2.0F, -1.0F, 2, 12, 2, f);
			this.field_3401.setRotationPoint(-5.0F, 2.0F, 0.0F);
			this.field_3390 = new Cuboid(this, 40, 16);
			this.field_3390.mirror = true;
			this.field_3390.addBox(-1.0F, -2.0F, -1.0F, 2, 12, 2, f);
			this.field_3390.setRotationPoint(5.0F, 2.0F, 0.0F);
			this.field_3392 = new Cuboid(this, 0, 16);
			this.field_3392.addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2, f);
			this.field_3392.setRotationPoint(-2.0F, 12.0F, 0.0F);
			this.field_3397 = new Cuboid(this, 0, 16);
			this.field_3397.mirror = true;
			this.field_3397.addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2, f);
			this.field_3397.setRotationPoint(2.0F, 12.0F, 0.0F);
		}
	}

	@Override
	public void method_17086(T livingEntity, float f, float g, float h) {
		this.armPoseRight = BipedEntityModel.ArmPose.field_3409;
		this.armPoseLeft = BipedEntityModel.ArmPose.field_3409;
		ItemStack itemStack = livingEntity.method_5998(Hand.MAIN);
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
		ItemStack itemStack = livingEntity.method_6047();
		if (livingEntity.hasArmsRaised() && (itemStack.isEmpty() || itemStack.getItem() != Items.field_8102)) {
			float l = MathHelper.sin(this.swingProgress * (float) Math.PI);
			float m = MathHelper.sin((1.0F - (1.0F - this.swingProgress) * (1.0F - this.swingProgress)) * (float) Math.PI);
			this.field_3401.roll = 0.0F;
			this.field_3390.roll = 0.0F;
			this.field_3401.yaw = -(0.1F - l * 0.6F);
			this.field_3390.yaw = 0.1F - l * 0.6F;
			this.field_3401.pitch = (float) (-Math.PI / 2);
			this.field_3390.pitch = (float) (-Math.PI / 2);
			this.field_3401.pitch -= l * 1.2F - m * 0.4F;
			this.field_3390.pitch -= l * 1.2F - m * 0.4F;
			this.field_3401.roll = this.field_3401.roll + MathHelper.cos(h * 0.09F) * 0.05F + 0.05F;
			this.field_3390.roll = this.field_3390.roll - (MathHelper.cos(h * 0.09F) * 0.05F + 0.05F);
			this.field_3401.pitch = this.field_3401.pitch + MathHelper.sin(h * 0.067F) * 0.05F;
			this.field_3390.pitch = this.field_3390.pitch - MathHelper.sin(h * 0.067F) * 0.05F;
		}
	}

	@Override
	public void method_2803(float f, OptionMainHand optionMainHand) {
		float g = optionMainHand == OptionMainHand.field_6183 ? 1.0F : -1.0F;
		Cuboid cuboid = this.method_2808(optionMainHand);
		cuboid.rotationPointX += g;
		cuboid.applyTransform(f);
		cuboid.rotationPointX -= g;
	}
}
