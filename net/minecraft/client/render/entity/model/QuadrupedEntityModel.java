/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class QuadrupedEntityModel<T extends Entity>
extends AnimalModel<T> {
    protected ModelPart head = new ModelPart(this, 0, 0);
    protected ModelPart body;
    protected ModelPart backRightLeg;
    protected ModelPart backLeftLeg;
    protected ModelPart frontRightLeg;
    protected ModelPart frontLeftLeg;

    public QuadrupedEntityModel(int legHeight, float scale, boolean headScaled, float childHeadYOffset, float childHeadZOffset, float invertedChildHeadScale, float invertedChildBodyScale, int childBodyYOffset) {
        super(headScaled, childHeadYOffset, childHeadZOffset, invertedChildHeadScale, invertedChildBodyScale, childBodyYOffset);
        this.head.addCuboid(-4.0f, -4.0f, -8.0f, 8.0f, 8.0f, 8.0f, scale);
        this.head.setPivot(0.0f, 18 - legHeight, -6.0f);
        this.body = new ModelPart(this, 28, 8);
        this.body.addCuboid(-5.0f, -10.0f, -7.0f, 10.0f, 16.0f, 8.0f, scale);
        this.body.setPivot(0.0f, 17 - legHeight, 2.0f);
        this.backRightLeg = new ModelPart(this, 0, 16);
        this.backRightLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, (float)legHeight, 4.0f, scale);
        this.backRightLeg.setPivot(-3.0f, 24 - legHeight, 7.0f);
        this.backLeftLeg = new ModelPart(this, 0, 16);
        this.backLeftLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, (float)legHeight, 4.0f, scale);
        this.backLeftLeg.setPivot(3.0f, 24 - legHeight, 7.0f);
        this.frontRightLeg = new ModelPart(this, 0, 16);
        this.frontRightLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, (float)legHeight, 4.0f, scale);
        this.frontRightLeg.setPivot(-3.0f, 24 - legHeight, -5.0f);
        this.frontLeftLeg = new ModelPart(this, 0, 16);
        this.frontLeftLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, (float)legHeight, 4.0f, scale);
        this.frontLeftLeg.setPivot(3.0f, 24 - legHeight, -5.0f);
    }

    @Override
    protected Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of(this.head);
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of(this.body, this.backRightLeg, this.backLeftLeg, this.frontRightLeg, this.frontLeftLeg);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.head.pitch = headPitch * ((float)Math.PI / 180);
        this.head.yaw = headYaw * ((float)Math.PI / 180);
        this.body.pitch = 1.5707964f;
        this.backRightLeg.pitch = MathHelper.cos(limbAngle * 0.6662f) * 1.4f * limbDistance;
        this.backLeftLeg.pitch = MathHelper.cos(limbAngle * 0.6662f + (float)Math.PI) * 1.4f * limbDistance;
        this.frontRightLeg.pitch = MathHelper.cos(limbAngle * 0.6662f + (float)Math.PI) * 1.4f * limbDistance;
        this.frontLeftLeg.pitch = MathHelper.cos(limbAngle * 0.6662f) * 1.4f * limbDistance;
    }
}

