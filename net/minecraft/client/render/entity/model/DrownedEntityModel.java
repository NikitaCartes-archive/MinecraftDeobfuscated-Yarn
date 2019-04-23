/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.AbsoluteHand;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class DrownedEntityModel<T extends ZombieEntity>
extends ZombieEntityModel<T> {
    public DrownedEntityModel(float f, float g, int i, int j) {
        super(f, g, i, j);
        this.rightArm = new Cuboid(this, 32, 48);
        this.rightArm.addBox(-3.0f, -2.0f, -2.0f, 4, 12, 4, f);
        this.rightArm.setRotationPoint(-5.0f, 2.0f + g, 0.0f);
        this.rightLeg = new Cuboid(this, 16, 48);
        this.rightLeg.addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, f);
        this.rightLeg.setRotationPoint(-1.9f, 12.0f + g, 0.0f);
    }

    public DrownedEntityModel(float f, boolean bl) {
        super(f, 0.0f, 64, bl ? 32 : 64);
    }

    public void method_17077(T zombieEntity, float f, float g, float h) {
        this.rightArmPose = BipedEntityModel.ArmPose.EMPTY;
        this.leftArmPose = BipedEntityModel.ArmPose.EMPTY;
        ItemStack itemStack = ((LivingEntity)zombieEntity).getStackInHand(Hand.MAIN_HAND);
        if (itemStack.getItem() == Items.TRIDENT && ((MobEntity)zombieEntity).isAttacking()) {
            if (((MobEntity)zombieEntity).getMainHand() == AbsoluteHand.RIGHT) {
                this.rightArmPose = BipedEntityModel.ArmPose.THROW_SPEAR;
            } else {
                this.leftArmPose = BipedEntityModel.ArmPose.THROW_SPEAR;
            }
        }
        super.method_17086(zombieEntity, f, g, h);
    }

    public void method_17134(T zombieEntity, float f, float g, float h, float i, float j, float k) {
        super.method_17791(zombieEntity, f, g, h, i, j, k);
        if (this.leftArmPose == BipedEntityModel.ArmPose.THROW_SPEAR) {
            this.leftArm.pitch = this.leftArm.pitch * 0.5f - (float)Math.PI;
            this.leftArm.yaw = 0.0f;
        }
        if (this.rightArmPose == BipedEntityModel.ArmPose.THROW_SPEAR) {
            this.rightArm.pitch = this.rightArm.pitch * 0.5f - (float)Math.PI;
            this.rightArm.yaw = 0.0f;
        }
        if (this.field_3396 > 0.0f) {
            this.rightArm.pitch = this.method_2804(this.rightArm.pitch, -2.5132742f, this.field_3396) + this.field_3396 * 0.35f * MathHelper.sin(0.1f * h);
            this.leftArm.pitch = this.method_2804(this.leftArm.pitch, -2.5132742f, this.field_3396) - this.field_3396 * 0.35f * MathHelper.sin(0.1f * h);
            this.rightArm.roll = this.method_2804(this.rightArm.roll, -0.15f, this.field_3396);
            this.leftArm.roll = this.method_2804(this.leftArm.roll, 0.15f, this.field_3396);
            this.leftLeg.pitch -= this.field_3396 * 0.55f * MathHelper.sin(0.1f * h);
            this.rightLeg.pitch += this.field_3396 * 0.55f * MathHelper.sin(0.1f * h);
            this.head.pitch = 0.0f;
        }
    }
}

