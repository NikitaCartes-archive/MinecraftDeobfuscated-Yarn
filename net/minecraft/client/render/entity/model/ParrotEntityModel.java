/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ParrotEntityModel
extends EntityModel<ParrotEntity> {
    private final ModelPart field_3458;
    private final ModelPart field_3460;
    private final ModelPart field_3459;
    private final ModelPart field_3455;
    private final ModelPart field_3452;
    private final ModelPart field_3461;
    private final ModelPart field_3451;
    private final ModelPart field_3453;
    private final ModelPart field_3456;
    private final ModelPart field_3450;
    private final ModelPart field_3457;

    public ParrotEntityModel() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        this.field_3458 = new ModelPart(this, 2, 8);
        this.field_3458.addCuboid(-1.5f, 0.0f, -1.5f, 3, 6, 3);
        this.field_3458.setRotationPoint(0.0f, 16.5f, -3.0f);
        this.field_3460 = new ModelPart(this, 22, 1);
        this.field_3460.addCuboid(-1.5f, -1.0f, -1.0f, 3, 4, 1);
        this.field_3460.setRotationPoint(0.0f, 21.07f, 1.16f);
        this.field_3459 = new ModelPart(this, 19, 8);
        this.field_3459.addCuboid(-0.5f, 0.0f, -1.5f, 1, 5, 3);
        this.field_3459.setRotationPoint(1.5f, 16.94f, -2.76f);
        this.field_3455 = new ModelPart(this, 19, 8);
        this.field_3455.addCuboid(-0.5f, 0.0f, -1.5f, 1, 5, 3);
        this.field_3455.setRotationPoint(-1.5f, 16.94f, -2.76f);
        this.field_3452 = new ModelPart(this, 2, 2);
        this.field_3452.addCuboid(-1.0f, -1.5f, -1.0f, 2, 3, 2);
        this.field_3452.setRotationPoint(0.0f, 15.69f, -2.76f);
        this.field_3461 = new ModelPart(this, 10, 0);
        this.field_3461.addCuboid(-1.0f, -0.5f, -2.0f, 2, 1, 4);
        this.field_3461.setRotationPoint(0.0f, -2.0f, -1.0f);
        this.field_3452.addChild(this.field_3461);
        this.field_3451 = new ModelPart(this, 11, 7);
        this.field_3451.addCuboid(-0.5f, -1.0f, -0.5f, 1, 2, 1);
        this.field_3451.setRotationPoint(0.0f, -0.5f, -1.5f);
        this.field_3452.addChild(this.field_3451);
        this.field_3453 = new ModelPart(this, 16, 7);
        this.field_3453.addCuboid(-0.5f, 0.0f, -0.5f, 1, 2, 1);
        this.field_3453.setRotationPoint(0.0f, -1.75f, -2.45f);
        this.field_3452.addChild(this.field_3453);
        this.field_3456 = new ModelPart(this, 2, 18);
        this.field_3456.addCuboid(0.0f, -4.0f, -2.0f, 0, 5, 4);
        this.field_3456.setRotationPoint(0.0f, -2.15f, 0.15f);
        this.field_3452.addChild(this.field_3456);
        this.field_3450 = new ModelPart(this, 14, 18);
        this.field_3450.addCuboid(-0.5f, 0.0f, -0.5f, 1, 2, 1);
        this.field_3450.setRotationPoint(1.0f, 22.0f, -1.05f);
        this.field_3457 = new ModelPart(this, 14, 18);
        this.field_3457.addCuboid(-0.5f, 0.0f, -0.5f, 1, 2, 1);
        this.field_3457.setRotationPoint(-1.0f, 22.0f, -1.05f);
    }

    public void method_17109(ParrotEntity parrotEntity, float f, float g, float h, float i, float j, float k) {
        this.method_17105(k);
    }

    public void method_17112(ParrotEntity parrotEntity, float f, float g, float h, float i, float j, float k) {
        this.method_17111(ParrotEntityModel.method_17107(parrotEntity), parrotEntity.age, f, g, h, i, j);
    }

    public void method_17108(ParrotEntity parrotEntity, float f, float g, float h) {
        this.method_17110(ParrotEntityModel.method_17107(parrotEntity));
    }

    public void method_17106(float f, float g, float h, float i, float j, int k) {
        this.method_17110(Pose.ON_SHOULDER);
        this.method_17111(Pose.ON_SHOULDER, k, f, g, 0.0f, h, i);
        this.method_17105(j);
    }

    private void method_17105(float f) {
        this.field_3458.render(f);
        this.field_3459.render(f);
        this.field_3455.render(f);
        this.field_3460.render(f);
        this.field_3452.render(f);
        this.field_3450.render(f);
        this.field_3457.render(f);
    }

    private void method_17111(Pose pose, int i, float f, float g, float h, float j, float k) {
        this.field_3452.pitch = k * ((float)Math.PI / 180);
        this.field_3452.yaw = j * ((float)Math.PI / 180);
        this.field_3452.roll = 0.0f;
        this.field_3452.rotationPointX = 0.0f;
        this.field_3458.rotationPointX = 0.0f;
        this.field_3460.rotationPointX = 0.0f;
        this.field_3455.rotationPointX = -1.5f;
        this.field_3459.rotationPointX = 1.5f;
        switch (pose) {
            case SITTING: {
                break;
            }
            case PARTY: {
                float l = MathHelper.cos(i);
                float m = MathHelper.sin(i);
                this.field_3452.rotationPointX = l;
                this.field_3452.rotationPointY = 15.69f + m;
                this.field_3452.pitch = 0.0f;
                this.field_3452.yaw = 0.0f;
                this.field_3452.roll = MathHelper.sin(i) * 0.4f;
                this.field_3458.rotationPointX = l;
                this.field_3458.rotationPointY = 16.5f + m;
                this.field_3459.roll = -0.0873f - h;
                this.field_3459.rotationPointX = 1.5f + l;
                this.field_3459.rotationPointY = 16.94f + m;
                this.field_3455.roll = 0.0873f + h;
                this.field_3455.rotationPointX = -1.5f + l;
                this.field_3455.rotationPointY = 16.94f + m;
                this.field_3460.rotationPointX = l;
                this.field_3460.rotationPointY = 21.07f + m;
                break;
            }
            case STANDING: {
                this.field_3450.pitch += MathHelper.cos(f * 0.6662f) * 1.4f * g;
                this.field_3457.pitch += MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
            }
            default: {
                float n = h * 0.3f;
                this.field_3452.rotationPointY = 15.69f + n;
                this.field_3460.pitch = 1.015f + MathHelper.cos(f * 0.6662f) * 0.3f * g;
                this.field_3460.rotationPointY = 21.07f + n;
                this.field_3458.rotationPointY = 16.5f + n;
                this.field_3459.roll = -0.0873f - h;
                this.field_3459.rotationPointY = 16.94f + n;
                this.field_3455.roll = 0.0873f + h;
                this.field_3455.rotationPointY = 16.94f + n;
                this.field_3450.rotationPointY = 22.0f + n;
                this.field_3457.rotationPointY = 22.0f + n;
            }
        }
    }

    private void method_17110(Pose pose) {
        this.field_3456.pitch = -0.2214f;
        this.field_3458.pitch = 0.4937f;
        this.field_3459.pitch = -0.6981f;
        this.field_3459.yaw = (float)(-Math.PI);
        this.field_3455.pitch = -0.6981f;
        this.field_3455.yaw = (float)(-Math.PI);
        this.field_3450.pitch = -0.0299f;
        this.field_3457.pitch = -0.0299f;
        this.field_3450.rotationPointY = 22.0f;
        this.field_3457.rotationPointY = 22.0f;
        this.field_3450.roll = 0.0f;
        this.field_3457.roll = 0.0f;
        switch (pose) {
            case FLYING: {
                this.field_3450.pitch += 0.6981317f;
                this.field_3457.pitch += 0.6981317f;
                break;
            }
            case SITTING: {
                float f = 1.9f;
                this.field_3452.rotationPointY = 17.59f;
                this.field_3460.pitch = 1.5388988f;
                this.field_3460.rotationPointY = 22.97f;
                this.field_3458.rotationPointY = 18.4f;
                this.field_3459.roll = -0.0873f;
                this.field_3459.rotationPointY = 18.84f;
                this.field_3455.roll = 0.0873f;
                this.field_3455.rotationPointY = 18.84f;
                this.field_3450.rotationPointY += 1.9f;
                this.field_3457.rotationPointY += 1.9f;
                this.field_3450.pitch += 1.5707964f;
                this.field_3457.pitch += 1.5707964f;
                break;
            }
            case PARTY: {
                this.field_3450.roll = -0.34906584f;
                this.field_3457.roll = 0.34906584f;
                break;
            }
        }
    }

    private static Pose method_17107(ParrotEntity parrotEntity) {
        if (parrotEntity.getSongPlaying()) {
            return Pose.PARTY;
        }
        if (parrotEntity.isSitting()) {
            return Pose.SITTING;
        }
        if (parrotEntity.isInAir()) {
            return Pose.FLYING;
        }
        return Pose.STANDING;
    }

    @Override
    public /* synthetic */ void setAngles(Entity entity, float f, float g, float h, float i, float j, float k) {
        this.method_17112((ParrotEntity)entity, f, g, h, i, j, k);
    }

    @Override
    public /* synthetic */ void render(Entity entity, float f, float g, float h, float i, float j, float k) {
        this.method_17109((ParrotEntity)entity, f, g, h, i, j, k);
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Pose {
        FLYING,
        STANDING,
        SITTING,
        PARTY,
        ON_SHOULDER;

    }
}

