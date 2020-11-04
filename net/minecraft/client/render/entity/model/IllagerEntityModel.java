/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5597;
import net.minecraft.class_5603;
import net.minecraft.class_5605;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.CrossbowPosing;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class IllagerEntityModel<T extends IllagerEntity>
extends class_5597<T>
implements ModelWithArms,
ModelWithHead {
    private final ModelPart field_27435;
    private final ModelPart head;
    private final ModelPart hat;
    private final ModelPart arms;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart rightAttackingArm;
    private final ModelPart leftAttackingArm;

    public IllagerEntityModel(ModelPart modelPart) {
        this.field_27435 = modelPart;
        this.head = modelPart.method_32086("head");
        this.hat = this.head.method_32086("hat");
        this.hat.visible = false;
        this.arms = modelPart.method_32086("arms");
        this.rightLeg = modelPart.method_32086("left_leg");
        this.leftLeg = modelPart.method_32086("right_leg");
        this.leftAttackingArm = modelPart.method_32086("left_arm");
        this.rightAttackingArm = modelPart.method_32086("right_arm");
    }

    public static class_5607 method_32012() {
        class_5609 lv = new class_5609();
        class_5610 lv2 = lv.method_32111();
        class_5610 lv3 = lv2.method_32117("head", class_5606.method_32108().method_32101(0, 0).method_32097(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f), class_5603.method_32090(0.0f, 0.0f, 0.0f));
        lv3.method_32117("hat", class_5606.method_32108().method_32101(32, 0).method_32098(-4.0f, -10.0f, -4.0f, 8.0f, 12.0f, 8.0f, new class_5605(0.45f)), class_5603.field_27701);
        lv3.method_32117("nose", class_5606.method_32108().method_32101(24, 0).method_32097(-1.0f, -1.0f, -6.0f, 2.0f, 4.0f, 2.0f), class_5603.method_32090(0.0f, -2.0f, 0.0f));
        lv2.method_32117("body", class_5606.method_32108().method_32101(16, 20).method_32097(-4.0f, 0.0f, -3.0f, 8.0f, 12.0f, 6.0f).method_32101(0, 38).method_32098(-4.0f, 0.0f, -3.0f, 8.0f, 18.0f, 6.0f, new class_5605(0.5f)), class_5603.method_32090(0.0f, 0.0f, 0.0f));
        class_5610 lv4 = lv2.method_32117("arms", class_5606.method_32108().method_32101(44, 22).method_32097(-8.0f, -2.0f, -2.0f, 4.0f, 8.0f, 4.0f).method_32101(40, 38).method_32097(-4.0f, 2.0f, -2.0f, 8.0f, 4.0f, 4.0f), class_5603.method_32091(0.0f, 3.0f, -1.0f, -0.75f, 0.0f, 0.0f));
        lv4.method_32117("left_shoulder", class_5606.method_32108().method_32101(44, 22).method_32096().method_32097(4.0f, -2.0f, -2.0f, 4.0f, 8.0f, 4.0f), class_5603.field_27701);
        lv2.method_32117("right_leg", class_5606.method_32108().method_32101(0, 22).method_32097(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f), class_5603.method_32090(-2.0f, 12.0f, 0.0f));
        lv2.method_32117("left_leg", class_5606.method_32108().method_32101(0, 22).method_32096().method_32097(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f), class_5603.method_32090(2.0f, 12.0f, 0.0f));
        lv2.method_32117("right_arm", class_5606.method_32108().method_32101(40, 46).method_32097(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f), class_5603.method_32090(-5.0f, 2.0f, 0.0f));
        lv2.method_32117("left_arm", class_5606.method_32108().method_32101(40, 46).method_32096().method_32097(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f), class_5603.method_32090(5.0f, 2.0f, 0.0f));
        return class_5607.method_32110(lv, 64, 64);
    }

    @Override
    public ModelPart method_32008() {
        return this.field_27435;
    }

    @Override
    public void setAngles(T illagerEntity, float f, float g, float h, float i, float j) {
        boolean bl;
        this.head.yaw = i * ((float)Math.PI / 180);
        this.head.pitch = j * ((float)Math.PI / 180);
        if (this.riding) {
            this.rightAttackingArm.pitch = -0.62831855f;
            this.rightAttackingArm.yaw = 0.0f;
            this.rightAttackingArm.roll = 0.0f;
            this.leftAttackingArm.pitch = -0.62831855f;
            this.leftAttackingArm.yaw = 0.0f;
            this.leftAttackingArm.roll = 0.0f;
            this.leftLeg.pitch = -1.4137167f;
            this.leftLeg.yaw = 0.31415927f;
            this.leftLeg.roll = 0.07853982f;
            this.rightLeg.pitch = -1.4137167f;
            this.rightLeg.yaw = -0.31415927f;
            this.rightLeg.roll = -0.07853982f;
        } else {
            this.rightAttackingArm.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 2.0f * g * 0.5f;
            this.rightAttackingArm.yaw = 0.0f;
            this.rightAttackingArm.roll = 0.0f;
            this.leftAttackingArm.pitch = MathHelper.cos(f * 0.6662f) * 2.0f * g * 0.5f;
            this.leftAttackingArm.yaw = 0.0f;
            this.leftAttackingArm.roll = 0.0f;
            this.leftLeg.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g * 0.5f;
            this.leftLeg.yaw = 0.0f;
            this.leftLeg.roll = 0.0f;
            this.rightLeg.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g * 0.5f;
            this.rightLeg.yaw = 0.0f;
            this.rightLeg.roll = 0.0f;
        }
        IllagerEntity.State state = ((IllagerEntity)illagerEntity).getState();
        if (state == IllagerEntity.State.ATTACKING) {
            if (((LivingEntity)illagerEntity).getMainHandStack().isEmpty()) {
                CrossbowPosing.method_29352(this.leftAttackingArm, this.rightAttackingArm, true, this.handSwingProgress, h);
            } else {
                CrossbowPosing.method_29351(this.rightAttackingArm, this.leftAttackingArm, illagerEntity, this.handSwingProgress, h);
            }
        } else if (state == IllagerEntity.State.SPELLCASTING) {
            this.rightAttackingArm.pivotZ = 0.0f;
            this.rightAttackingArm.pivotX = -5.0f;
            this.leftAttackingArm.pivotZ = 0.0f;
            this.leftAttackingArm.pivotX = 5.0f;
            this.rightAttackingArm.pitch = MathHelper.cos(h * 0.6662f) * 0.25f;
            this.leftAttackingArm.pitch = MathHelper.cos(h * 0.6662f) * 0.25f;
            this.rightAttackingArm.roll = 2.3561945f;
            this.leftAttackingArm.roll = -2.3561945f;
            this.rightAttackingArm.yaw = 0.0f;
            this.leftAttackingArm.yaw = 0.0f;
        } else if (state == IllagerEntity.State.BOW_AND_ARROW) {
            this.rightAttackingArm.yaw = -0.1f + this.head.yaw;
            this.rightAttackingArm.pitch = -1.5707964f + this.head.pitch;
            this.leftAttackingArm.pitch = -0.9424779f + this.head.pitch;
            this.leftAttackingArm.yaw = this.head.yaw - 0.4f;
            this.leftAttackingArm.roll = 1.5707964f;
        } else if (state == IllagerEntity.State.CROSSBOW_HOLD) {
            CrossbowPosing.hold(this.rightAttackingArm, this.leftAttackingArm, this.head, true);
        } else if (state == IllagerEntity.State.CROSSBOW_CHARGE) {
            CrossbowPosing.charge(this.rightAttackingArm, this.leftAttackingArm, illagerEntity, true);
        } else if (state == IllagerEntity.State.CELEBRATING) {
            this.rightAttackingArm.pivotZ = 0.0f;
            this.rightAttackingArm.pivotX = -5.0f;
            this.rightAttackingArm.pitch = MathHelper.cos(h * 0.6662f) * 0.05f;
            this.rightAttackingArm.roll = 2.670354f;
            this.rightAttackingArm.yaw = 0.0f;
            this.leftAttackingArm.pivotZ = 0.0f;
            this.leftAttackingArm.pivotX = 5.0f;
            this.leftAttackingArm.pitch = MathHelper.cos(h * 0.6662f) * 0.05f;
            this.leftAttackingArm.roll = -2.3561945f;
            this.leftAttackingArm.yaw = 0.0f;
        }
        this.arms.visible = bl = state == IllagerEntity.State.CROSSED;
        this.leftAttackingArm.visible = !bl;
        this.rightAttackingArm.visible = !bl;
    }

    private ModelPart getAttackingArm(Arm arm) {
        if (arm == Arm.LEFT) {
            return this.leftAttackingArm;
        }
        return this.rightAttackingArm;
    }

    public ModelPart getHat() {
        return this.hat;
    }

    @Override
    public ModelPart getHead() {
        return this.head;
    }

    @Override
    public void setArmAngle(Arm arm, MatrixStack matrices) {
        this.getAttackingArm(arm).rotate(matrices);
    }
}

