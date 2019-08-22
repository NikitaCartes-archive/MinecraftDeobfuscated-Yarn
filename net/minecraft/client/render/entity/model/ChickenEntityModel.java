/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ChickenEntityModel<T extends Entity>
extends EntityModel<T> {
    private final ModelPart head;
    private final ModelPart field_3346;
    private final ModelPart field_3345;
    private final ModelPart field_3343;
    private final ModelPart field_3341;
    private final ModelPart field_3347;
    private final ModelPart field_3340;
    private final ModelPart field_3342;

    public ChickenEntityModel() {
        int i = 16;
        this.head = new ModelPart(this, 0, 0);
        this.head.addCuboid(-2.0f, -6.0f, -2.0f, 4, 6, 3, 0.0f);
        this.head.setRotationPoint(0.0f, 15.0f, -4.0f);
        this.field_3340 = new ModelPart(this, 14, 0);
        this.field_3340.addCuboid(-2.0f, -4.0f, -4.0f, 4, 2, 2, 0.0f);
        this.field_3340.setRotationPoint(0.0f, 15.0f, -4.0f);
        this.field_3342 = new ModelPart(this, 14, 4);
        this.field_3342.addCuboid(-1.0f, -2.0f, -3.0f, 2, 2, 2, 0.0f);
        this.field_3342.setRotationPoint(0.0f, 15.0f, -4.0f);
        this.field_3346 = new ModelPart(this, 0, 9);
        this.field_3346.addCuboid(-3.0f, -4.0f, -3.0f, 6, 8, 6, 0.0f);
        this.field_3346.setRotationPoint(0.0f, 16.0f, 0.0f);
        this.field_3345 = new ModelPart(this, 26, 0);
        this.field_3345.addCuboid(-1.0f, 0.0f, -3.0f, 3, 5, 3);
        this.field_3345.setRotationPoint(-2.0f, 19.0f, 1.0f);
        this.field_3343 = new ModelPart(this, 26, 0);
        this.field_3343.addCuboid(-1.0f, 0.0f, -3.0f, 3, 5, 3);
        this.field_3343.setRotationPoint(1.0f, 19.0f, 1.0f);
        this.field_3341 = new ModelPart(this, 24, 13);
        this.field_3341.addCuboid(0.0f, 0.0f, -3.0f, 1, 4, 6);
        this.field_3341.setRotationPoint(-4.0f, 13.0f, 0.0f);
        this.field_3347 = new ModelPart(this, 24, 13);
        this.field_3347.addCuboid(-1.0f, 0.0f, -3.0f, 1, 4, 6);
        this.field_3347.setRotationPoint(4.0f, 13.0f, 0.0f);
    }

    @Override
    public void render(T entity, float f, float g, float h, float i, float j, float k) {
        this.setAngles(entity, f, g, h, i, j, k);
        if (this.isChild) {
            float l = 2.0f;
            RenderSystem.pushMatrix();
            RenderSystem.translatef(0.0f, 5.0f * k, 2.0f * k);
            this.head.render(k);
            this.field_3340.render(k);
            this.field_3342.render(k);
            RenderSystem.popMatrix();
            RenderSystem.pushMatrix();
            RenderSystem.scalef(0.5f, 0.5f, 0.5f);
            RenderSystem.translatef(0.0f, 24.0f * k, 0.0f);
            this.field_3346.render(k);
            this.field_3345.render(k);
            this.field_3343.render(k);
            this.field_3341.render(k);
            this.field_3347.render(k);
            RenderSystem.popMatrix();
        } else {
            this.head.render(k);
            this.field_3340.render(k);
            this.field_3342.render(k);
            this.field_3346.render(k);
            this.field_3345.render(k);
            this.field_3343.render(k);
            this.field_3341.render(k);
            this.field_3347.render(k);
        }
    }

    @Override
    public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
        this.head.pitch = j * ((float)Math.PI / 180);
        this.head.yaw = i * ((float)Math.PI / 180);
        this.field_3340.pitch = this.head.pitch;
        this.field_3340.yaw = this.head.yaw;
        this.field_3342.pitch = this.head.pitch;
        this.field_3342.yaw = this.head.yaw;
        this.field_3346.pitch = 1.5707964f;
        this.field_3345.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g;
        this.field_3343.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
        this.field_3341.roll = h;
        this.field_3347.roll = -h;
    }
}

