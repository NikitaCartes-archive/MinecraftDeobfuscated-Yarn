/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.CrossbowPosing;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
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

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = BipedEntityModel.getModelData(Dilation.NONE, 0.0f);
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(EntityModelPartNames.RIGHT_ARM, ModelPartBuilder.create().uv(40, 16).cuboid(-1.0f, -2.0f, -1.0f, 2.0f, 12.0f, 2.0f), ModelTransform.pivot(-5.0f, 2.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_ARM, ModelPartBuilder.create().uv(40, 16).mirrored().cuboid(-1.0f, -2.0f, -1.0f, 2.0f, 12.0f, 2.0f), ModelTransform.pivot(5.0f, 2.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_LEG, ModelPartBuilder.create().uv(0, 16).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 12.0f, 2.0f), ModelTransform.pivot(-2.0f, 12.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_LEG, ModelPartBuilder.create().uv(0, 16).mirrored().cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 12.0f, 2.0f), ModelTransform.pivot(2.0f, 12.0f, 0.0f));
        return TexturedModelData.of(modelData, 64, 32);
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
            this.leftArm.roll = 0.0f;
            this.rightArm.yaw = -(0.1f - k * 0.6f);
            this.leftArm.yaw = 0.1f - k * 0.6f;
            this.rightArm.pitch = -1.5707964f;
            this.leftArm.pitch = -1.5707964f;
            this.rightArm.pitch -= k * 1.2f - l * 0.4f;
            this.leftArm.pitch -= k * 1.2f - l * 0.4f;
            CrossbowPosing.swingArms(this.rightArm, this.leftArm, h);
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

