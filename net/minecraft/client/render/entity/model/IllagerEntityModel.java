/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.CompositeEntityModel;
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
extends CompositeEntityModel<T>
implements ModelWithArms,
ModelWithHead {
    private final ModelPart head;
    private final ModelPart hat;
    private final ModelPart torso;
    private final ModelPart arms;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart rightArm;
    private final ModelPart leftArm;

    public IllagerEntityModel(float scale, float pivotY, int textureWidth, int textureHeight) {
        this.head = new ModelPart(this).setTextureSize(textureWidth, textureHeight);
        this.head.setPivot(0.0f, 0.0f + pivotY, 0.0f);
        this.head.setTextureOffset(0, 0).addCuboid(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f, scale);
        this.hat = new ModelPart(this, 32, 0).setTextureSize(textureWidth, textureHeight);
        this.hat.addCuboid(-4.0f, -10.0f, -4.0f, 8.0f, 12.0f, 8.0f, scale + 0.45f);
        this.head.addChild(this.hat);
        this.hat.visible = false;
        ModelPart modelPart = new ModelPart(this).setTextureSize(textureWidth, textureHeight);
        modelPart.setPivot(0.0f, pivotY - 2.0f, 0.0f);
        modelPart.setTextureOffset(24, 0).addCuboid(-1.0f, -1.0f, -6.0f, 2.0f, 4.0f, 2.0f, scale);
        this.head.addChild(modelPart);
        this.torso = new ModelPart(this).setTextureSize(textureWidth, textureHeight);
        this.torso.setPivot(0.0f, 0.0f + pivotY, 0.0f);
        this.torso.setTextureOffset(16, 20).addCuboid(-4.0f, 0.0f, -3.0f, 8.0f, 12.0f, 6.0f, scale);
        this.torso.setTextureOffset(0, 38).addCuboid(-4.0f, 0.0f, -3.0f, 8.0f, 18.0f, 6.0f, scale + 0.5f);
        this.arms = new ModelPart(this).setTextureSize(textureWidth, textureHeight);
        this.arms.setPivot(0.0f, 0.0f + pivotY + 2.0f, 0.0f);
        this.arms.setTextureOffset(44, 22).addCuboid(-8.0f, -2.0f, -2.0f, 4.0f, 8.0f, 4.0f, scale);
        ModelPart modelPart2 = new ModelPart(this, 44, 22).setTextureSize(textureWidth, textureHeight);
        modelPart2.mirror = true;
        modelPart2.addCuboid(4.0f, -2.0f, -2.0f, 4.0f, 8.0f, 4.0f, scale);
        this.arms.addChild(modelPart2);
        this.arms.setTextureOffset(40, 38).addCuboid(-4.0f, 2.0f, -2.0f, 8.0f, 4.0f, 4.0f, scale);
        this.leftLeg = new ModelPart(this, 0, 22).setTextureSize(textureWidth, textureHeight);
        this.leftLeg.setPivot(-2.0f, 12.0f + pivotY, 0.0f);
        this.leftLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, scale);
        this.rightLeg = new ModelPart(this, 0, 22).setTextureSize(textureWidth, textureHeight);
        this.rightLeg.mirror = true;
        this.rightLeg.setPivot(2.0f, 12.0f + pivotY, 0.0f);
        this.rightLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, scale);
        this.rightArm = new ModelPart(this, 40, 46).setTextureSize(textureWidth, textureHeight);
        this.rightArm.addCuboid(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, scale);
        this.rightArm.setPivot(-5.0f, 2.0f + pivotY, 0.0f);
        this.leftArm = new ModelPart(this, 40, 46).setTextureSize(textureWidth, textureHeight);
        this.leftArm.mirror = true;
        this.leftArm.addCuboid(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, scale);
        this.leftArm.setPivot(5.0f, 2.0f + pivotY, 0.0f);
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of(this.head, this.torso, this.leftLeg, this.rightLeg, this.arms, this.rightArm, this.leftArm);
    }

    @Override
    public void setAngles(T illagerEntity, float f, float g, float h, float i, float j) {
        boolean bl;
        this.head.yaw = i * ((float)Math.PI / 180);
        this.head.pitch = j * ((float)Math.PI / 180);
        this.arms.pivotY = 3.0f;
        this.arms.pivotZ = -1.0f;
        this.arms.pitch = -0.75f;
        if (this.riding) {
            this.rightArm.pitch = -0.62831855f;
            this.rightArm.yaw = 0.0f;
            this.rightArm.roll = 0.0f;
            this.leftArm.pitch = -0.62831855f;
            this.leftArm.yaw = 0.0f;
            this.leftArm.roll = 0.0f;
            this.leftLeg.pitch = -1.4137167f;
            this.leftLeg.yaw = 0.31415927f;
            this.leftLeg.roll = 0.07853982f;
            this.rightLeg.pitch = -1.4137167f;
            this.rightLeg.yaw = -0.31415927f;
            this.rightLeg.roll = -0.07853982f;
        } else {
            this.rightArm.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 2.0f * g * 0.5f;
            this.rightArm.yaw = 0.0f;
            this.rightArm.roll = 0.0f;
            this.leftArm.pitch = MathHelper.cos(f * 0.6662f) * 2.0f * g * 0.5f;
            this.leftArm.yaw = 0.0f;
            this.leftArm.roll = 0.0f;
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
                CrossbowPosing.method_29352(this.leftArm, this.rightArm, true, this.handSwingProgress, h);
            } else {
                CrossbowPosing.method_29351(this.rightArm, this.leftArm, illagerEntity, this.handSwingProgress, h);
            }
        } else if (state == IllagerEntity.State.SPELLCASTING) {
            this.rightArm.pivotZ = 0.0f;
            this.rightArm.pivotX = -5.0f;
            this.leftArm.pivotZ = 0.0f;
            this.leftArm.pivotX = 5.0f;
            this.rightArm.pitch = MathHelper.cos(h * 0.6662f) * 0.25f;
            this.leftArm.pitch = MathHelper.cos(h * 0.6662f) * 0.25f;
            this.rightArm.roll = 2.3561945f;
            this.leftArm.roll = -2.3561945f;
            this.rightArm.yaw = 0.0f;
            this.leftArm.yaw = 0.0f;
        } else if (state == IllagerEntity.State.BOW_AND_ARROW) {
            this.rightArm.yaw = -0.1f + this.head.yaw;
            this.rightArm.pitch = -1.5707964f + this.head.pitch;
            this.leftArm.pitch = -0.9424779f + this.head.pitch;
            this.leftArm.yaw = this.head.yaw - 0.4f;
            this.leftArm.roll = 1.5707964f;
        } else if (state == IllagerEntity.State.CROSSBOW_HOLD) {
            CrossbowPosing.hold(this.rightArm, this.leftArm, this.head, true);
        } else if (state == IllagerEntity.State.CROSSBOW_CHARGE) {
            CrossbowPosing.charge(this.rightArm, this.leftArm, illagerEntity, true);
        } else if (state == IllagerEntity.State.CELEBRATING) {
            this.rightArm.pivotZ = 0.0f;
            this.rightArm.pivotX = -5.0f;
            this.rightArm.pitch = MathHelper.cos(h * 0.6662f) * 0.05f;
            this.rightArm.roll = 2.670354f;
            this.rightArm.yaw = 0.0f;
            this.leftArm.pivotZ = 0.0f;
            this.leftArm.pivotX = 5.0f;
            this.leftArm.pitch = MathHelper.cos(h * 0.6662f) * 0.05f;
            this.leftArm.roll = -2.3561945f;
            this.leftArm.yaw = 0.0f;
        }
        this.arms.visible = bl = state == IllagerEntity.State.CROSSED;
        this.leftArm.visible = !bl;
        this.rightArm.visible = !bl;
    }

    private ModelPart getAttackingArm(Arm arm) {
        if (arm == Arm.LEFT) {
            return this.leftArm;
        }
        return this.rightArm;
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

