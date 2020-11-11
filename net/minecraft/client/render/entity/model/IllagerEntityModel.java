/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.CrossbowPosing;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.Dilation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class IllagerEntityModel<T extends IllagerEntity>
extends SinglePartEntityModel<T>
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
        this.head = modelPart.getChild("head");
        this.hat = this.head.getChild("hat");
        this.hat.visible = false;
        this.arms = modelPart.getChild("arms");
        this.rightLeg = modelPart.getChild("left_leg");
        this.leftLeg = modelPart.getChild("right_leg");
        this.leftAttackingArm = modelPart.getChild("left_arm");
        this.rightAttackingArm = modelPart.getChild("right_arm");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f), ModelTransform.pivot(0.0f, 0.0f, 0.0f));
        modelPartData2.addChild("hat", ModelPartBuilder.create().uv(32, 0).cuboid(-4.0f, -10.0f, -4.0f, 8.0f, 12.0f, 8.0f, new Dilation(0.45f)), ModelTransform.NONE);
        modelPartData2.addChild("nose", ModelPartBuilder.create().uv(24, 0).cuboid(-1.0f, -1.0f, -6.0f, 2.0f, 4.0f, 2.0f), ModelTransform.pivot(0.0f, -2.0f, 0.0f));
        modelPartData.addChild("body", ModelPartBuilder.create().uv(16, 20).cuboid(-4.0f, 0.0f, -3.0f, 8.0f, 12.0f, 6.0f).uv(0, 38).cuboid(-4.0f, 0.0f, -3.0f, 8.0f, 18.0f, 6.0f, new Dilation(0.5f)), ModelTransform.pivot(0.0f, 0.0f, 0.0f));
        ModelPartData modelPartData3 = modelPartData.addChild("arms", ModelPartBuilder.create().uv(44, 22).cuboid(-8.0f, -2.0f, -2.0f, 4.0f, 8.0f, 4.0f).uv(40, 38).cuboid(-4.0f, 2.0f, -2.0f, 8.0f, 4.0f, 4.0f), ModelTransform.of(0.0f, 3.0f, -1.0f, -0.75f, 0.0f, 0.0f));
        modelPartData3.addChild("left_shoulder", ModelPartBuilder.create().uv(44, 22).mirrored().cuboid(4.0f, -2.0f, -2.0f, 4.0f, 8.0f, 4.0f), ModelTransform.NONE);
        modelPartData.addChild("right_leg", ModelPartBuilder.create().uv(0, 22).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f), ModelTransform.pivot(-2.0f, 12.0f, 0.0f));
        modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(0, 22).mirrored().cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f), ModelTransform.pivot(2.0f, 12.0f, 0.0f));
        modelPartData.addChild("right_arm", ModelPartBuilder.create().uv(40, 46).cuboid(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f), ModelTransform.pivot(-5.0f, 2.0f, 0.0f));
        modelPartData.addChild("left_arm", ModelPartBuilder.create().uv(40, 46).mirrored().cuboid(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f), ModelTransform.pivot(5.0f, 2.0f, 0.0f));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public ModelPart getPart() {
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

