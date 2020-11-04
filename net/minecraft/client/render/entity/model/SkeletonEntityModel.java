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
import net.minecraft.client.render.entity.model.CrossbowPosing;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class SkeletonEntityModel<T extends MobEntity>
extends BipedEntityModel<T> {
    public SkeletonEntityModel(ModelPart modelPart) {
        super(modelPart);
    }

    public static class_5607 method_32047() {
        class_5609 lv = BipedEntityModel.method_32011(class_5605.field_27715, 0.0f);
        class_5610 lv2 = lv.method_32111();
        lv2.method_32117("right_arm", class_5606.method_32108().method_32101(40, 16).method_32097(-1.0f, -2.0f, -1.0f, 2.0f, 12.0f, 2.0f), class_5603.method_32090(-5.0f, 2.0f, 0.0f));
        lv2.method_32117("left_arm", class_5606.method_32108().method_32101(40, 16).method_32096().method_32097(-1.0f, -2.0f, -1.0f, 2.0f, 12.0f, 2.0f), class_5603.method_32090(5.0f, 2.0f, 0.0f));
        lv2.method_32117("right_leg", class_5606.method_32108().method_32101(0, 16).method_32097(-1.0f, 0.0f, -1.0f, 2.0f, 12.0f, 2.0f), class_5603.method_32090(-2.0f, 12.0f, 0.0f));
        lv2.method_32117("left_leg", class_5606.method_32108().method_32101(0, 16).method_32096().method_32097(-1.0f, 0.0f, -1.0f, 2.0f, 12.0f, 2.0f), class_5603.method_32090(2.0f, 12.0f, 0.0f));
        return class_5607.method_32110(lv, 64, 32);
    }

    @Override
    public void animateModel(T mobEntity, float f, float g, float h) {
        this.rightArmPose = BipedEntityModel.ArmPose.EMPTY;
        this.leftArmPose = BipedEntityModel.ArmPose.EMPTY;
        ItemStack itemStack = ((LivingEntity)mobEntity).getStackInHand(Hand.MAIN_HAND);
        if (itemStack.isOf(Items.BOW) && ((MobEntity)mobEntity).isAttacking()) {
            if (((MobEntity)mobEntity).getMainArm() == Arm.RIGHT) {
                this.rightArmPose = BipedEntityModel.ArmPose.BOW_AND_ARROW;
            } else {
                this.leftArmPose = BipedEntityModel.ArmPose.BOW_AND_ARROW;
            }
        }
        super.animateModel(mobEntity, f, g, h);
    }

    @Override
    public void setAngles(T mobEntity, float f, float g, float h, float i, float j) {
        super.setAngles(mobEntity, f, g, h, i, j);
        ItemStack itemStack = ((LivingEntity)mobEntity).getMainHandStack();
        if (((MobEntity)mobEntity).isAttacking() && (itemStack.isEmpty() || !itemStack.isOf(Items.BOW))) {
            float k = MathHelper.sin(this.handSwingProgress * (float)Math.PI);
            float l = MathHelper.sin((1.0f - (1.0f - this.handSwingProgress) * (1.0f - this.handSwingProgress)) * (float)Math.PI);
            this.rightArm.roll = 0.0f;
            this.field_27433.roll = 0.0f;
            this.rightArm.yaw = -(0.1f - k * 0.6f);
            this.field_27433.yaw = 0.1f - k * 0.6f;
            this.rightArm.pitch = -1.5707964f;
            this.field_27433.pitch = -1.5707964f;
            this.rightArm.pitch -= k * 1.2f - l * 0.4f;
            this.field_27433.pitch -= k * 1.2f - l * 0.4f;
            CrossbowPosing.method_29350(this.rightArm, this.field_27433, h);
        }
    }

    @Override
    public void setArmAngle(Arm arm, MatrixStack matrices) {
        float f = arm == Arm.RIGHT ? 1.0f : -1.0f;
        ModelPart modelPart = this.getArm(arm);
        modelPart.pivotX += f;
        modelPart.rotate(matrices);
        modelPart.pivotX -= f;
    }
}

