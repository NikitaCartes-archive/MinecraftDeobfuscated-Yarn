/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4592;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class OcelotEntityModel<T extends Entity>
extends class_4592<T> {
    protected final ModelPart frontLegLeft;
    protected final ModelPart frontLegRight;
    protected final ModelPart backLegLeft;
    protected final ModelPart backLegRight;
    protected final ModelPart tail1;
    protected final ModelPart tail2;
    protected final ModelPart head = new ModelPart(this);
    protected final ModelPart body;
    protected int animationState = 1;

    public OcelotEntityModel(float f) {
        super(true, 10.0f, 4.0f);
        this.head.addCuboid("main", -2.5f, -2.0f, -3.0f, 5, 4, 5, f, 0, 0);
        this.head.addCuboid("nose", -1.5f, 0.0f, -4.0f, 3, 2, 2, f, 0, 24);
        this.head.addCuboid("ear1", -2.0f, -3.0f, 0.0f, 1, 1, 2, f, 0, 10);
        this.head.addCuboid("ear2", 1.0f, -3.0f, 0.0f, 1, 1, 2, f, 6, 10);
        this.head.setRotationPoint(0.0f, 15.0f, -9.0f);
        this.body = new ModelPart(this, 20, 0);
        this.body.addCuboid(-2.0f, 3.0f, -8.0f, 4.0f, 16.0f, 6.0f, f);
        this.body.setRotationPoint(0.0f, 12.0f, -10.0f);
        this.tail1 = new ModelPart(this, 0, 15);
        this.tail1.addCuboid(-0.5f, 0.0f, 0.0f, 1.0f, 8.0f, 1.0f, f);
        this.tail1.pitch = 0.9f;
        this.tail1.setRotationPoint(0.0f, 15.0f, 8.0f);
        this.tail2 = new ModelPart(this, 4, 15);
        this.tail2.addCuboid(-0.5f, 0.0f, 0.0f, 1.0f, 8.0f, 1.0f, f);
        this.tail2.setRotationPoint(0.0f, 20.0f, 14.0f);
        this.frontLegLeft = new ModelPart(this, 8, 13);
        this.frontLegLeft.addCuboid(-1.0f, 0.0f, 1.0f, 2.0f, 6.0f, 2.0f, f);
        this.frontLegLeft.setRotationPoint(1.1f, 18.0f, 5.0f);
        this.frontLegRight = new ModelPart(this, 8, 13);
        this.frontLegRight.addCuboid(-1.0f, 0.0f, 1.0f, 2.0f, 6.0f, 2.0f, f);
        this.frontLegRight.setRotationPoint(-1.1f, 18.0f, 5.0f);
        this.backLegLeft = new ModelPart(this, 40, 0);
        this.backLegLeft.addCuboid(-1.0f, 0.0f, 0.0f, 2.0f, 10.0f, 2.0f, f);
        this.backLegLeft.setRotationPoint(1.2f, 14.1f, -5.0f);
        this.backLegRight = new ModelPart(this, 40, 0);
        this.backLegRight.addCuboid(-1.0f, 0.0f, 0.0f, 2.0f, 10.0f, 2.0f, f);
        this.backLegRight.setRotationPoint(-1.2f, 14.1f, -5.0f);
    }

    @Override
    protected Iterable<ModelPart> method_22946() {
        return ImmutableList.of(this.head);
    }

    @Override
    protected Iterable<ModelPart> method_22948() {
        return ImmutableList.of(this.body, this.frontLegLeft, this.frontLegRight, this.backLegLeft, this.backLegRight, this.tail1, this.tail2);
    }

    @Override
    public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
        this.head.pitch = j * ((float)Math.PI / 180);
        this.head.yaw = i * ((float)Math.PI / 180);
        if (this.animationState != 3) {
            this.body.pitch = 1.5707964f;
            if (this.animationState == 2) {
                this.frontLegLeft.pitch = MathHelper.cos(f * 0.6662f) * g;
                this.frontLegRight.pitch = MathHelper.cos(f * 0.6662f + 0.3f) * g;
                this.backLegLeft.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI + 0.3f) * g;
                this.backLegRight.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * g;
                this.tail2.pitch = 1.7278761f + 0.31415927f * MathHelper.cos(f) * g;
            } else {
                this.frontLegLeft.pitch = MathHelper.cos(f * 0.6662f) * g;
                this.frontLegRight.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * g;
                this.backLegLeft.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * g;
                this.backLegRight.pitch = MathHelper.cos(f * 0.6662f) * g;
                this.tail2.pitch = this.animationState == 1 ? 1.7278761f + 0.7853982f * MathHelper.cos(f) * g : 1.7278761f + 0.47123894f * MathHelper.cos(f) * g;
            }
        }
    }

    @Override
    public void animateModel(T entity, float f, float g, float h) {
        this.body.rotationPointY = 12.0f;
        this.body.rotationPointZ = -10.0f;
        this.head.rotationPointY = 15.0f;
        this.head.rotationPointZ = -9.0f;
        this.tail1.rotationPointY = 15.0f;
        this.tail1.rotationPointZ = 8.0f;
        this.tail2.rotationPointY = 20.0f;
        this.tail2.rotationPointZ = 14.0f;
        this.backLegLeft.rotationPointY = 14.1f;
        this.backLegLeft.rotationPointZ = -5.0f;
        this.backLegRight.rotationPointY = 14.1f;
        this.backLegRight.rotationPointZ = -5.0f;
        this.frontLegLeft.rotationPointY = 18.0f;
        this.frontLegLeft.rotationPointZ = 5.0f;
        this.frontLegRight.rotationPointY = 18.0f;
        this.frontLegRight.rotationPointZ = 5.0f;
        this.tail1.pitch = 0.9f;
        if (((Entity)entity).isInSneakingPose()) {
            this.body.rotationPointY += 1.0f;
            this.head.rotationPointY += 2.0f;
            this.tail1.rotationPointY += 1.0f;
            this.tail2.rotationPointY += -4.0f;
            this.tail2.rotationPointZ += 2.0f;
            this.tail1.pitch = 1.5707964f;
            this.tail2.pitch = 1.5707964f;
            this.animationState = 0;
        } else if (((Entity)entity).isSprinting()) {
            this.tail2.rotationPointY = this.tail1.rotationPointY;
            this.tail2.rotationPointZ += 2.0f;
            this.tail1.pitch = 1.5707964f;
            this.tail2.pitch = 1.5707964f;
            this.animationState = 2;
        } else {
            this.animationState = 1;
        }
    }
}

