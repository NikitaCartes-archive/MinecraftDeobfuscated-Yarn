/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5603;
import net.minecraft.class_5605;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class DrownedEntityModel<T extends ZombieEntity>
extends ZombieEntityModel<T> {
    public DrownedEntityModel(ModelPart modelPart) {
        super(modelPart);
    }

    public static class_5607 method_31993(class_5605 arg) {
        class_5609 lv = BipedEntityModel.method_32011(arg, 0.0f);
        class_5610 lv2 = lv.method_32111();
        lv2.method_32117("left_arm", class_5606.method_32108().method_32101(32, 48).method_32098(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, arg), class_5603.method_32090(5.0f, 2.0f, 0.0f));
        lv2.method_32117("left_leg", class_5606.method_32108().method_32101(16, 48).method_32098(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, arg), class_5603.method_32090(1.9f, 12.0f, 0.0f));
        return class_5607.method_32110(lv, 64, 64);
    }

    @Override
    public void animateModel(T zombieEntity, float f, float g, float h) {
        this.rightArmPose = BipedEntityModel.ArmPose.EMPTY;
        this.leftArmPose = BipedEntityModel.ArmPose.EMPTY;
        ItemStack itemStack = ((LivingEntity)zombieEntity).getStackInHand(Hand.MAIN_HAND);
        if (itemStack.isOf(Items.TRIDENT) && ((MobEntity)zombieEntity).isAttacking()) {
            if (((MobEntity)zombieEntity).getMainArm() == Arm.RIGHT) {
                this.rightArmPose = BipedEntityModel.ArmPose.THROW_SPEAR;
            } else {
                this.leftArmPose = BipedEntityModel.ArmPose.THROW_SPEAR;
            }
        }
        super.animateModel(zombieEntity, f, g, h);
    }

    @Override
    public void setAngles(T zombieEntity, float f, float g, float h, float i, float j) {
        super.setAngles(zombieEntity, f, g, h, i, j);
        if (this.leftArmPose == BipedEntityModel.ArmPose.THROW_SPEAR) {
            this.field_27433.pitch = this.field_27433.pitch * 0.5f - (float)Math.PI;
            this.field_27433.yaw = 0.0f;
        }
        if (this.rightArmPose == BipedEntityModel.ArmPose.THROW_SPEAR) {
            this.rightArm.pitch = this.rightArm.pitch * 0.5f - (float)Math.PI;
            this.rightArm.yaw = 0.0f;
        }
        if (this.leaningPitch > 0.0f) {
            this.rightArm.pitch = this.lerpAngle(this.leaningPitch, this.rightArm.pitch, -2.5132742f) + this.leaningPitch * 0.35f * MathHelper.sin(0.1f * h);
            this.field_27433.pitch = this.lerpAngle(this.leaningPitch, this.field_27433.pitch, -2.5132742f) - this.leaningPitch * 0.35f * MathHelper.sin(0.1f * h);
            this.rightArm.roll = this.lerpAngle(this.leaningPitch, this.rightArm.roll, -0.15f);
            this.field_27433.roll = this.lerpAngle(this.leaningPitch, this.field_27433.roll, 0.15f);
            this.leftLeg.pitch -= this.leaningPitch * 0.55f * MathHelper.sin(0.1f * h);
            this.rightLeg.pitch += this.leaningPitch * 0.55f * MathHelper.sin(0.1f * h);
            this.head.pitch = 0.0f;
        }
    }
}

