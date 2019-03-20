package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.AbsoluteHand;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class DrownedEntityModel<T extends ZombieEntity> extends ZombieEntityModel<T> {
	public DrownedEntityModel(float f, float g, int i, int j) {
		super(f, g, i, j);
		this.armRight = new Cuboid(this, 32, 48);
		this.armRight.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, f);
		this.armRight.setRotationPoint(-5.0F, 2.0F + g, 0.0F);
		this.legRight = new Cuboid(this, 16, 48);
		this.legRight.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
		this.legRight.setRotationPoint(-1.9F, 12.0F + g, 0.0F);
	}

	public DrownedEntityModel(float f, boolean bl) {
		super(f, 0.0F, 64, bl ? 32 : 64);
	}

	public void method_17077(T zombieEntity, float f, float g, float h) {
		this.armPoseRight = BipedEntityModel.ArmPose.field_3409;
		this.armPoseLeft = BipedEntityModel.ArmPose.field_3409;
		ItemStack itemStack = zombieEntity.getStackInHand(Hand.MAIN);
		if (itemStack.getItem() == Items.field_8547 && zombieEntity.method_6510()) {
			if (zombieEntity.getMainHand() == AbsoluteHand.field_6183) {
				this.armPoseRight = BipedEntityModel.ArmPose.field_3407;
			} else {
				this.armPoseLeft = BipedEntityModel.ArmPose.field_3407;
			}
		}

		super.method_17086(zombieEntity, f, g, h);
	}

	public void method_17134(T zombieEntity, float f, float g, float h, float i, float j, float k) {
		super.method_17791(zombieEntity, f, g, h, i, j, k);
		if (this.armPoseLeft == BipedEntityModel.ArmPose.field_3407) {
			this.armLeft.pitch = this.armLeft.pitch * 0.5F - (float) Math.PI;
			this.armLeft.yaw = 0.0F;
		}

		if (this.armPoseRight == BipedEntityModel.ArmPose.field_3407) {
			this.armRight.pitch = this.armRight.pitch * 0.5F - (float) Math.PI;
			this.armRight.yaw = 0.0F;
		}

		if (this.field_3396 > 0.0F) {
			this.armRight.pitch = this.method_2804(this.armRight.pitch, (float) (-Math.PI * 4.0 / 5.0), this.field_3396)
				+ this.field_3396 * 0.35F * MathHelper.sin(0.1F * h);
			this.armLeft.pitch = this.method_2804(this.armLeft.pitch, (float) (-Math.PI * 4.0 / 5.0), this.field_3396)
				- this.field_3396 * 0.35F * MathHelper.sin(0.1F * h);
			this.armRight.roll = this.method_2804(this.armRight.roll, -0.15F, this.field_3396);
			this.armLeft.roll = this.method_2804(this.armLeft.roll, 0.15F, this.field_3396);
			this.legLeft.pitch = this.legLeft.pitch - this.field_3396 * 0.55F * MathHelper.sin(0.1F * h);
			this.legRight.pitch = this.legRight.pitch + this.field_3396 * 0.55F * MathHelper.sin(0.1F * h);
			this.head.pitch = 0.0F;
		}
	}
}
