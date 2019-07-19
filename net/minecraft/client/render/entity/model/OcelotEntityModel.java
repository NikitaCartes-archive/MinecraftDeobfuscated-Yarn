/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class OcelotEntityModel<T extends Entity>
extends EntityModel<T> {
    protected final ModelPart leftBackLeg;
    protected final ModelPart rightBackLeg;
    protected final ModelPart leftFrontLeg;
    protected final ModelPart rightFrontLeg;
    protected final ModelPart upperTail;
    protected final ModelPart lowerTail;
    protected final ModelPart head = new ModelPart(this, "head");
    protected final ModelPart torso;
    protected int animationState = 1;

    public OcelotEntityModel(float f) {
        this.head.addCuboid("main", -2.5f, -2.0f, -3.0f, 5, 4, 5, f, 0, 0);
        this.head.addCuboid("nose", -1.5f, 0.0f, -4.0f, 3, 2, 2, f, 0, 24);
        this.head.addCuboid("ear1", -2.0f, -3.0f, 0.0f, 1, 1, 2, f, 0, 10);
        this.head.addCuboid("ear2", 1.0f, -3.0f, 0.0f, 1, 1, 2, f, 6, 10);
        this.head.setPivot(0.0f, 15.0f, -9.0f);
        this.torso = new ModelPart(this, 20, 0);
        this.torso.addCuboid(-2.0f, 3.0f, -8.0f, 4, 16, 6, f);
        this.torso.setPivot(0.0f, 12.0f, -10.0f);
        this.upperTail = new ModelPart(this, 0, 15);
        this.upperTail.addCuboid(-0.5f, 0.0f, 0.0f, 1, 8, 1, f);
        this.upperTail.pitch = 0.9f;
        this.upperTail.setPivot(0.0f, 15.0f, 8.0f);
        this.lowerTail = new ModelPart(this, 4, 15);
        this.lowerTail.addCuboid(-0.5f, 0.0f, 0.0f, 1, 8, 1, f);
        this.lowerTail.setPivot(0.0f, 20.0f, 14.0f);
        this.leftBackLeg = new ModelPart(this, 8, 13);
        this.leftBackLeg.addCuboid(-1.0f, 0.0f, 1.0f, 2, 6, 2, f);
        this.leftBackLeg.setPivot(1.1f, 18.0f, 5.0f);
        this.rightBackLeg = new ModelPart(this, 8, 13);
        this.rightBackLeg.addCuboid(-1.0f, 0.0f, 1.0f, 2, 6, 2, f);
        this.rightBackLeg.setPivot(-1.1f, 18.0f, 5.0f);
        this.leftFrontLeg = new ModelPart(this, 40, 0);
        this.leftFrontLeg.addCuboid(-1.0f, 0.0f, 0.0f, 2, 10, 2, f);
        this.leftFrontLeg.setPivot(1.2f, 14.1f, -5.0f);
        this.rightFrontLeg = new ModelPart(this, 40, 0);
        this.rightFrontLeg.addCuboid(-1.0f, 0.0f, 0.0f, 2, 10, 2, f);
        this.rightFrontLeg.setPivot(-1.2f, 14.1f, -5.0f);
    }

    @Override
    public void render(T entity, float f, float g, float h, float i, float j, float k) {
        this.setAngles(entity, f, g, h, i, j, k);
        if (this.child) {
            float l = 2.0f;
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.75f, 0.75f, 0.75f);
            GlStateManager.translatef(0.0f, 10.0f * k, 4.0f * k);
            this.head.render(k);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.5f, 0.5f, 0.5f);
            GlStateManager.translatef(0.0f, 24.0f * k, 0.0f);
            this.torso.render(k);
            this.leftBackLeg.render(k);
            this.rightBackLeg.render(k);
            this.leftFrontLeg.render(k);
            this.rightFrontLeg.render(k);
            this.upperTail.render(k);
            this.lowerTail.render(k);
            GlStateManager.popMatrix();
        } else {
            this.head.render(k);
            this.torso.render(k);
            this.upperTail.render(k);
            this.lowerTail.render(k);
            this.leftBackLeg.render(k);
            this.rightBackLeg.render(k);
            this.leftFrontLeg.render(k);
            this.rightFrontLeg.render(k);
        }
    }

    @Override
    public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
        this.head.pitch = j * ((float)Math.PI / 180);
        this.head.yaw = i * ((float)Math.PI / 180);
        if (this.animationState != 3) {
            this.torso.pitch = 1.5707964f;
            if (this.animationState == 2) {
                this.leftBackLeg.pitch = MathHelper.cos(f * 0.6662f) * g;
                this.rightBackLeg.pitch = MathHelper.cos(f * 0.6662f + 0.3f) * g;
                this.leftFrontLeg.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI + 0.3f) * g;
                this.rightFrontLeg.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * g;
                this.lowerTail.pitch = 1.7278761f + 0.31415927f * MathHelper.cos(f) * g;
            } else {
                this.leftBackLeg.pitch = MathHelper.cos(f * 0.6662f) * g;
                this.rightBackLeg.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * g;
                this.leftFrontLeg.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * g;
                this.rightFrontLeg.pitch = MathHelper.cos(f * 0.6662f) * g;
                this.lowerTail.pitch = this.animationState == 1 ? 1.7278761f + 0.7853982f * MathHelper.cos(f) * g : 1.7278761f + 0.47123894f * MathHelper.cos(f) * g;
            }
        }
    }

    @Override
    public void animateModel(T entity, float f, float g, float h) {
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
        if (((Entity)entity).isSneaking()) {
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

