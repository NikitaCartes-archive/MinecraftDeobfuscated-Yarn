/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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

    public CatEntityModel(float f) {
        super(f);
    }

    public void method_17074(T catEntity, float f, float g, float h) {
        this.sleepAnimation = ((CatEntity)catEntity).getSleepAnimation(h);
        this.tailCurlAnimation = ((CatEntity)catEntity).getTailCurlAnimation(h);
        this.headDownAnimation = ((CatEntity)catEntity).getHeadDownAnimation(h);
        if (this.sleepAnimation <= 0.0f) {
            this.head.pitch = 0.0f;
            this.head.roll = 0.0f;
            this.backLegLeft.pitch = 0.0f;
            this.backLegLeft.roll = 0.0f;
            this.backLegRight.pitch = 0.0f;
            this.backLegRight.roll = 0.0f;
            this.backLegRight.pivotX = -1.2f;
            this.frontLegLeft.pitch = 0.0f;
            this.frontLegRight.pitch = 0.0f;
            this.frontLegRight.roll = 0.0f;
            this.frontLegRight.pivotX = -1.1f;
            this.frontLegRight.pivotY = 18.0f;
        }
        super.animateModel(catEntity, f, g, h);
        if (((TameableEntity)catEntity).isSitting()) {
            this.body.pitch = 0.7853982f;
            this.body.pivotY += -4.0f;
            this.body.pivotZ += 5.0f;
            this.head.pivotY += -3.3f;
            this.head.pivotZ += 1.0f;
            this.tail1.pivotY += 8.0f;
            this.tail1.pivotZ += -2.0f;
            this.tail2.pivotY += 2.0f;
            this.tail2.pivotZ += -0.8f;
            this.tail1.pitch = 1.7278761f;
            this.tail2.pitch = 2.670354f;
            this.backLegLeft.pitch = -0.15707964f;
            this.backLegLeft.pivotY = 16.1f;
            this.backLegLeft.pivotZ = -7.0f;
            this.backLegRight.pitch = -0.15707964f;
            this.backLegRight.pivotY = 16.1f;
            this.backLegRight.pivotZ = -7.0f;
            this.frontLegLeft.pitch = -1.5707964f;
            this.frontLegLeft.pivotY = 21.0f;
            this.frontLegLeft.pivotZ = 1.0f;
            this.frontLegRight.pitch = -1.5707964f;
            this.frontLegRight.pivotY = 21.0f;
            this.frontLegRight.pivotZ = 1.0f;
            this.animationState = 3;
        }
    }

    public void method_17075(T catEntity, float f, float g, float h, float i, float j, float k) {
        super.setAngles(catEntity, f, g, h, i, j, k);
        if (this.sleepAnimation > 0.0f) {
            this.head.roll = ModelUtil.interpolateAngle(this.head.roll, -1.2707963f, this.sleepAnimation);
            this.head.yaw = ModelUtil.interpolateAngle(this.head.yaw, 1.2707963f, this.sleepAnimation);
            this.backLegLeft.pitch = -1.2707963f;
            this.backLegRight.pitch = -0.47079635f;
            this.backLegRight.roll = -0.2f;
            this.backLegRight.pivotX = -0.2f;
            this.frontLegLeft.pitch = -0.4f;
            this.frontLegRight.pitch = 0.5f;
            this.frontLegRight.roll = -0.5f;
            this.frontLegRight.pivotX = -0.3f;
            this.frontLegRight.pivotY = 20.0f;
            this.tail1.pitch = ModelUtil.interpolateAngle(this.tail1.pitch, 0.8f, this.tailCurlAnimation);
            this.tail2.pitch = ModelUtil.interpolateAngle(this.tail2.pitch, -0.4f, this.tailCurlAnimation);
        }
        if (this.headDownAnimation > 0.0f) {
            this.head.pitch = ModelUtil.interpolateAngle(this.head.pitch, -0.58177644f, this.headDownAnimation);
        }
    }
}

