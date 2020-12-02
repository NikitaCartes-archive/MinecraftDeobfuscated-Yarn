/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelUtil;
import net.minecraft.client.render.entity.model.OcelotEntityModel;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.TameableEntity;

@Environment(value=EnvType.CLIENT)
public class CatEntityModel<T extends CatEntity>
extends OcelotEntityModel<T> {
    private float sleepAnimation;
    private float tailCurlAnimation;
    private float headDownAnimation;

    public CatEntityModel(ModelPart modelPart) {
        super(modelPart);
    }

    @Override
    public void animateModel(T catEntity, float f, float g, float h) {
        this.sleepAnimation = ((CatEntity)catEntity).getSleepAnimation(h);
        this.tailCurlAnimation = ((CatEntity)catEntity).getTailCurlAnimation(h);
        this.headDownAnimation = ((CatEntity)catEntity).getHeadDownAnimation(h);
        if (this.sleepAnimation <= 0.0f) {
            this.head.pitch = 0.0f;
            this.head.roll = 0.0f;
            this.leftFrontLeg.pitch = 0.0f;
            this.leftFrontLeg.roll = 0.0f;
            this.rightFrontLeg.pitch = 0.0f;
            this.rightFrontLeg.roll = 0.0f;
            this.rightFrontLeg.pivotX = -1.2f;
            this.leftHindLeg.pitch = 0.0f;
            this.rightHindLeg.pitch = 0.0f;
            this.rightHindLeg.roll = 0.0f;
            this.rightHindLeg.pivotX = -1.1f;
            this.rightHindLeg.pivotY = 18.0f;
        }
        super.animateModel(catEntity, f, g, h);
        if (((TameableEntity)catEntity).isInSittingPose()) {
            this.body.pitch = 0.7853982f;
            this.body.pivotY += -4.0f;
            this.body.pivotZ += 5.0f;
            this.head.pivotY += -3.3f;
            this.head.pivotZ += 1.0f;
            this.upperTail.pivotY += 8.0f;
            this.upperTail.pivotZ += -2.0f;
            this.lowerTail.pivotY += 2.0f;
            this.lowerTail.pivotZ += -0.8f;
            this.upperTail.pitch = 1.7278761f;
            this.lowerTail.pitch = 2.670354f;
            this.leftFrontLeg.pitch = -0.15707964f;
            this.leftFrontLeg.pivotY = 16.1f;
            this.leftFrontLeg.pivotZ = -7.0f;
            this.rightFrontLeg.pitch = -0.15707964f;
            this.rightFrontLeg.pivotY = 16.1f;
            this.rightFrontLeg.pivotZ = -7.0f;
            this.leftHindLeg.pitch = -1.5707964f;
            this.leftHindLeg.pivotY = 21.0f;
            this.leftHindLeg.pivotZ = 1.0f;
            this.rightHindLeg.pitch = -1.5707964f;
            this.rightHindLeg.pivotY = 21.0f;
            this.rightHindLeg.pivotZ = 1.0f;
            this.animationState = 3;
        }
    }

    @Override
    public void setAngles(T catEntity, float f, float g, float h, float i, float j) {
        super.setAngles(catEntity, f, g, h, i, j);
        if (this.sleepAnimation > 0.0f) {
            this.head.roll = ModelUtil.interpolateAngle(this.head.roll, -1.2707963f, this.sleepAnimation);
            this.head.yaw = ModelUtil.interpolateAngle(this.head.yaw, 1.2707963f, this.sleepAnimation);
            this.leftFrontLeg.pitch = -1.2707963f;
            this.rightFrontLeg.pitch = -0.47079635f;
            this.rightFrontLeg.roll = -0.2f;
            this.rightFrontLeg.pivotX = -0.2f;
            this.leftHindLeg.pitch = -0.4f;
            this.rightHindLeg.pitch = 0.5f;
            this.rightHindLeg.roll = -0.5f;
            this.rightHindLeg.pivotX = -0.3f;
            this.rightHindLeg.pivotY = 20.0f;
            this.upperTail.pitch = ModelUtil.interpolateAngle(this.upperTail.pitch, 0.8f, this.tailCurlAnimation);
            this.lowerTail.pitch = ModelUtil.interpolateAngle(this.lowerTail.pitch, -0.4f, this.tailCurlAnimation);
        }
        if (this.headDownAnimation > 0.0f) {
            this.head.pitch = ModelUtil.interpolateAngle(this.head.pitch, -0.58177644f, this.headDownAnimation);
        }
    }
}

