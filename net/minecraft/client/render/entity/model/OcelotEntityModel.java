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
public class OcelotEntityModel<T extends Entity>
extends AnimalModel<T> {
    protected final ModelPart leftBackLeg;
    protected final ModelPart rightBackLeg;
    protected final ModelPart leftFrontLeg;
    protected final ModelPart rightFrontLeg;
    protected final ModelPart upperTail;
    protected final ModelPart lowerTail;
    protected final ModelPart head = new ModelPart(this);
    protected final ModelPart torso;
    protected int animationState = 1;

    public OcelotEntityModel(float scale) {
        super(true, 10.0f, 4.0f);
        this.head.addCuboid("main", -2.5f, -2.0f, -3.0f, 5, 4, 5, scale, 0, 0);
        this.head.addCuboid("nose", -1.5f, 0.0f, -4.0f, 3, 2, 2, scale, 0, 24);
        this.head.addCuboid("ear1", -2.0f, -3.0f, 0.0f, 1, 1, 2, scale, 0, 10);
        this.head.addCuboid("ear2", 1.0f, -3.0f, 0.0f, 1, 1, 2, scale, 6, 10);
        this.head.setPivot(0.0f, 15.0f, -9.0f);
        this.torso = new ModelPart(this, 20, 0);
        this.torso.addCuboid(-2.0f, 3.0f, -8.0f, 4.0f, 16.0f, 6.0f, scale);
        this.torso.setPivot(0.0f, 12.0f, -10.0f);
        this.upperTail = new ModelPart(this, 0, 15);
        this.upperTail.addCuboid(-0.5f, 0.0f, 0.0f, 1.0f, 8.0f, 1.0f, scale);
        this.upperTail.pitch = 0.9f;
        this.upperTail.setPivot(0.0f, 15.0f, 8.0f);
        this.lowerTail = new ModelPart(this, 4, 15);
        this.lowerTail.addCuboid(-0.5f, 0.0f, 0.0f, 1.0f, 8.0f, 1.0f, scale);
        this.lowerTail.setPivot(0.0f, 20.0f, 14.0f);
        this.leftBackLeg = new ModelPart(this, 8, 13);
        this.leftBackLeg.addCuboid(-1.0f, 0.0f, 1.0f, 2.0f, 6.0f, 2.0f, scale);
        this.leftBackLeg.setPivot(1.1f, 18.0f, 5.0f);
        this.rightBackLeg = new ModelPart(this, 8, 13);
        this.rightBackLeg.addCuboid(-1.0f, 0.0f, 1.0f, 2.0f, 6.0f, 2.0f, scale);
        this.rightBackLeg.setPivot(-1.1f, 18.0f, 5.0f);
        this.leftFrontLeg = new ModelPart(this, 40, 0);
        this.leftFrontLeg.addCuboid(-1.0f, 0.0f, 0.0f, 2.0f, 10.0f, 2.0f, scale);
        this.leftFrontLeg.setPivot(1.2f, 14.1f, -5.0f);
        this.rightFrontLeg = new ModelPart(this, 40, 0);
        this.rightFrontLeg.addCuboid(-1.0f, 0.0f, 0.0f, 2.0f, 10.0f, 2.0f, scale);
        this.rightFrontLeg.setPivot(-1.2f, 14.1f, -5.0f);
    }

    @Override
    protected Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of(this.head);
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of(this.torso, this.leftBackLeg, this.rightBackLeg, this.leftFrontLeg, this.rightFrontLeg, this.upperTail, this.lowerTail);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float customAngle, float headYaw, float headPitch) {
        this.head.pitch = headPitch * ((float)Math.PI / 180);
        this.head.yaw = headYaw * ((float)Math.PI / 180);
        if (this.animationState != 3) {
            this.torso.pitch = 1.5707964f;
            if (this.animationState == 2) {
                this.leftBackLeg.pitch = MathHelper.cos(limbAngle * 0.6662f) * limbDistance;
                this.rightBackLeg.pitch = MathHelper.cos(limbAngle * 0.6662f + 0.3f) * limbDistance;
                this.leftFrontLeg.pitch = MathHelper.cos(limbAngle * 0.6662f + (float)Math.PI + 0.3f) * limbDistance;
                this.rightFrontLeg.pitch = MathHelper.cos(limbAngle * 0.6662f + (float)Math.PI) * limbDistance;
                this.lowerTail.pitch = 1.7278761f + 0.31415927f * MathHelper.cos(limbAngle) * limbDistance;
            } else {
                this.leftBackLeg.pitch = MathHelper.cos(limbAngle * 0.6662f) * limbDistance;
                this.rightBackLeg.pitch = MathHelper.cos(limbAngle * 0.6662f + (float)Math.PI) * limbDistance;
                this.leftFrontLeg.pitch = MathHelper.cos(limbAngle * 0.6662f + (float)Math.PI) * limbDistance;
                this.rightFrontLeg.pitch = MathHelper.cos(limbAngle * 0.6662f) * limbDistance;
                this.lowerTail.pitch = this.animationState == 1 ? 1.7278761f + 0.7853982f * MathHelper.cos(limbAngle) * limbDistance : 1.7278761f + 0.47123894f * MathHelper.cos(limbAngle) * limbDistance;
            }
        }
    }

    @Override
    public void animateModel(T entity, float limbAngle, float limbDistance, float tickDelta) {
        this.torso.pivotY = 12.0f;
        this.torso.pivotZ = -10.0f;
        this.head.pivotY = 15.0f;
        this.head.pivotZ = -9.0f;
        this.upperTail.pivotY = 15.0f;
        this.upperTail.pivotZ = 8.0f;
        this.lowerTail.pivotY = 20.0f;
        this.lowerTail.pivotZ = 14.0f;
        this.leftFrontLeg.pivotY = 14.1f;
        this.leftFrontLeg.pivotZ = -5.0f;
        this.rightFrontLeg.pivotY = 14.1f;
        this.rightFrontLeg.pivotZ = -5.0f;
        this.leftBackLeg.pivotY = 18.0f;
        this.leftBackLeg.pivotZ = 5.0f;
        this.rightBackLeg.pivotY = 18.0f;
        this.rightBackLeg.pivotZ = 5.0f;
        this.upperTail.pitch = 0.9f;
        if (((Entity)entity).isInSneakingPose()) {
            this.torso.pivotY += 1.0f;
            this.head.pivotY += 2.0f;
            this.upperTail.pivotY += 1.0f;
            this.lowerTail.pivotY += -4.0f;
            this.lowerTail.pivotZ += 2.0f;
            this.upperTail.pitch = 1.5707964f;
            this.lowerTail.pitch = 1.5707964f;
            this.animationState = 0;
        } else if (((Entity)entity).isSprinting()) {
            this.lowerTail.pivotY = this.upperTail.pivotY;
            this.lowerTail.pivotZ += 2.0f;
            this.upperTail.pitch = 1.5707964f;
            this.lowerTail.pitch = 1.5707964f;
            this.animationState = 2;
        } else {
            this.animationState = 1;
        }
    }
}

