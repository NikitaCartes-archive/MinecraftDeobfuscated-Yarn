/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class OcelotEntityModel<T extends Entity>
extends EntityModel<T> {
    protected final Cuboid frontLegLeft;
    protected final Cuboid frontLegRight;
    protected final Cuboid backLegLeft;
    protected final Cuboid backLegRight;
    protected final Cuboid tail1;
    protected final Cuboid tail2;
    protected final Cuboid head = new Cuboid(this, "head");
    protected final Cuboid body;
    protected int animationState = 1;

    public OcelotEntityModel(float f) {
        this.head.addBox("main", -2.5f, -2.0f, -3.0f, 5, 4, 5, f, 0, 0);
        this.head.addBox("nose", -1.5f, 0.0f, -4.0f, 3, 2, 2, f, 0, 24);
        this.head.addBox("ear1", -2.0f, -3.0f, 0.0f, 1, 1, 2, f, 0, 10);
        this.head.addBox("ear2", 1.0f, -3.0f, 0.0f, 1, 1, 2, f, 6, 10);
        this.head.setRotationPoint(0.0f, 15.0f, -9.0f);
        this.body = new Cuboid(this, 20, 0);
        this.body.addBox(-2.0f, 3.0f, -8.0f, 4, 16, 6, f);
        this.body.setRotationPoint(0.0f, 12.0f, -10.0f);
        this.tail1 = new Cuboid(this, 0, 15);
        this.tail1.addBox(-0.5f, 0.0f, 0.0f, 1, 8, 1, f);
        this.tail1.pitch = 0.9f;
        this.tail1.setRotationPoint(0.0f, 15.0f, 8.0f);
        this.tail2 = new Cuboid(this, 4, 15);
        this.tail2.addBox(-0.5f, 0.0f, 0.0f, 1, 8, 1, f);
        this.tail2.setRotationPoint(0.0f, 20.0f, 14.0f);
        this.frontLegLeft = new Cuboid(this, 8, 13);
        this.frontLegLeft.addBox(-1.0f, 0.0f, 1.0f, 2, 6, 2, f);
        this.frontLegLeft.setRotationPoint(1.1f, 18.0f, 5.0f);
        this.frontLegRight = new Cuboid(this, 8, 13);
        this.frontLegRight.addBox(-1.0f, 0.0f, 1.0f, 2, 6, 2, f);
        this.frontLegRight.setRotationPoint(-1.1f, 18.0f, 5.0f);
        this.backLegLeft = new Cuboid(this, 40, 0);
        this.backLegLeft.addBox(-1.0f, 0.0f, 0.0f, 2, 10, 2, f);
        this.backLegLeft.setRotationPoint(1.2f, 13.8f, -5.0f);
        this.backLegRight = new Cuboid(this, 40, 0);
        this.backLegRight.addBox(-1.0f, 0.0f, 0.0f, 2, 10, 2, f);
        this.backLegRight.setRotationPoint(-1.2f, 13.8f, -5.0f);
    }

    @Override
    public void render(T entity, float f, float g, float h, float i, float j, float k) {
        this.setAngles(entity, f, g, h, i, j, k);
        if (this.isChild) {
            float l = 2.0f;
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.75f, 0.75f, 0.75f);
            GlStateManager.translatef(0.0f, 10.0f * k, 4.0f * k);
            this.head.render(k);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.5f, 0.5f, 0.5f);
            GlStateManager.translatef(0.0f, 24.0f * k, 0.0f);
            this.body.render(k);
            this.frontLegLeft.render(k);
            this.frontLegRight.render(k);
            this.backLegLeft.render(k);
            this.backLegRight.render(k);
            this.tail1.render(k);
            this.tail2.render(k);
            GlStateManager.popMatrix();
        } else {
            this.head.render(k);
            this.body.render(k);
            this.tail1.render(k);
            this.tail2.render(k);
            this.frontLegLeft.render(k);
            this.frontLegRight.render(k);
            this.backLegLeft.render(k);
            this.backLegRight.render(k);
        }
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
        this.backLegLeft.rotationPointY = 13.8f;
        this.backLegLeft.rotationPointZ = -5.0f;
        this.backLegRight.rotationPointY = 13.8f;
        this.backLegRight.rotationPointZ = -5.0f;
        this.frontLegLeft.rotationPointY = 18.0f;
        this.frontLegLeft.rotationPointZ = 5.0f;
        this.frontLegRight.rotationPointY = 18.0f;
        this.frontLegRight.rotationPointZ = 5.0f;
        this.tail1.pitch = 0.9f;
        if (((Entity)entity).isSneaking()) {
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

