/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.CompositeEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ParrotEntityModel
extends CompositeEntityModel<ParrotEntity> {
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
        this.field_3458.addCuboid(-1.5f, 0.0f, -1.5f, 3.0f, 6.0f, 3.0f);
        this.field_3458.setPivot(0.0f, 16.5f, -3.0f);
        this.field_3460 = new ModelPart(this, 22, 1);
        this.field_3460.addCuboid(-1.5f, -1.0f, -1.0f, 3.0f, 4.0f, 1.0f);
        this.field_3460.setPivot(0.0f, 21.07f, 1.16f);
        this.field_3459 = new ModelPart(this, 19, 8);
        this.field_3459.addCuboid(-0.5f, 0.0f, -1.5f, 1.0f, 5.0f, 3.0f);
        this.field_3459.setPivot(1.5f, 16.94f, -2.76f);
        this.field_3455 = new ModelPart(this, 19, 8);
        this.field_3455.addCuboid(-0.5f, 0.0f, -1.5f, 1.0f, 5.0f, 3.0f);
        this.field_3455.setPivot(-1.5f, 16.94f, -2.76f);
        this.field_3452 = new ModelPart(this, 2, 2);
        this.field_3452.addCuboid(-1.0f, -1.5f, -1.0f, 2.0f, 3.0f, 2.0f);
        this.field_3452.setPivot(0.0f, 15.69f, -2.76f);
        this.field_3461 = new ModelPart(this, 10, 0);
        this.field_3461.addCuboid(-1.0f, -0.5f, -2.0f, 2.0f, 1.0f, 4.0f);
        this.field_3461.setPivot(0.0f, -2.0f, -1.0f);
        this.field_3452.addChild(this.field_3461);
        this.field_3451 = new ModelPart(this, 11, 7);
        this.field_3451.addCuboid(-0.5f, -1.0f, -0.5f, 1.0f, 2.0f, 1.0f);
        this.field_3451.setPivot(0.0f, -0.5f, -1.5f);
        this.field_3452.addChild(this.field_3451);
        this.field_3453 = new ModelPart(this, 16, 7);
        this.field_3453.addCuboid(-0.5f, 0.0f, -0.5f, 1.0f, 2.0f, 1.0f);
        this.field_3453.setPivot(0.0f, -1.75f, -2.45f);
        this.field_3452.addChild(this.field_3453);
        this.field_3456 = new ModelPart(this, 2, 18);
        this.field_3456.addCuboid(0.0f, -4.0f, -2.0f, 0.0f, 5.0f, 4.0f);
        this.field_3456.setPivot(0.0f, -2.15f, 0.15f);
        this.field_3452.addChild(this.field_3456);
        this.field_3450 = new ModelPart(this, 14, 18);
        this.field_3450.addCuboid(-0.5f, 0.0f, -0.5f, 1.0f, 2.0f, 1.0f);
        this.field_3450.setPivot(1.0f, 22.0f, -1.05f);
        this.field_3457 = new ModelPart(this, 14, 18);
        this.field_3457.addCuboid(-0.5f, 0.0f, -0.5f, 1.0f, 2.0f, 1.0f);
        this.field_3457.setPivot(-1.0f, 22.0f, -1.05f);
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of(this.field_3458, this.field_3459, this.field_3455, this.field_3460, this.field_3452, this.field_3450, this.field_3457);
    }

    public void method_17112(ParrotEntity parrotEntity, float f, float g, float h, float i, float j) {
        this.method_17111(ParrotEntityModel.getPose(parrotEntity), parrotEntity.age, f, g, h, i, j);
    }

    public void method_17108(ParrotEntity parrotEntity, float f, float g, float h) {
        this.method_17110(ParrotEntityModel.getPose(parrotEntity));
    }

    public void method_17106(MatrixStack matrixStack, VertexConsumer vertexConsumer, int i, int j, float f, float g, float h, float k, int l) {
        this.method_17110(Pose.ON_SHOULDER);
        this.method_17111(Pose.ON_SHOULDER, l, f, g, 0.0f, h, k);
        this.getParts().forEach(modelPart -> modelPart.render(matrixStack, vertexConsumer, i, j, null));
    }

    private void method_17111(Pose pose, int i, float f, float g, float h, float j, float k) {
        this.field_3452.pitch = k * ((float)Math.PI / 180);
        this.field_3452.yaw = j * ((float)Math.PI / 180);
        this.field_3452.roll = 0.0f;
        this.field_3452.pivotX = 0.0f;
        this.field_3458.pivotX = 0.0f;
        this.field_3460.pivotX = 0.0f;
        this.field_3455.pivotX = -1.5f;
        this.field_3459.pivotX = 1.5f;
        switch (pose) {
            case SITTING: {
                break;
            }
            case PARTY: {
                float l = MathHelper.cos(i);
                float m = MathHelper.sin(i);
                this.field_3452.pivotX = l;
                this.field_3452.pivotY = 15.69f + m;
                this.field_3452.pitch = 0.0f;
                this.field_3452.yaw = 0.0f;
                this.field_3452.roll = MathHelper.sin(i) * 0.4f;
                this.field_3458.pivotX = l;
                this.field_3458.pivotY = 16.5f + m;
                this.field_3459.roll = -0.0873f - h;
                this.field_3459.pivotX = 1.5f + l;
                this.field_3459.pivotY = 16.94f + m;
                this.field_3455.roll = 0.0873f + h;
                this.field_3455.pivotX = -1.5f + l;
                this.field_3455.pivotY = 16.94f + m;
                this.field_3460.pivotX = l;
                this.field_3460.pivotY = 21.07f + m;
                break;
            }
            case STANDING: {
                this.field_3450.pitch += MathHelper.cos(f * 0.6662f) * 1.4f * g;
                this.field_3457.pitch += MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
            }
            default: {
                float n = h * 0.3f;
                this.field_3452.pivotY = 15.69f + n;
                this.field_3460.pitch = 1.015f + MathHelper.cos(f * 0.6662f) * 0.3f * g;
                this.field_3460.pivotY = 21.07f + n;
                this.field_3458.pivotY = 16.5f + n;
                this.field_3459.roll = -0.0873f - h;
                this.field_3459.pivotY = 16.94f + n;
                this.field_3455.roll = 0.0873f + h;
                this.field_3455.pivotY = 16.94f + n;
                this.field_3450.pivotY = 22.0f + n;
                this.field_3457.pivotY = 22.0f + n;
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
        this.field_3450.pivotY = 22.0f;
        this.field_3457.pivotY = 22.0f;
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
                this.field_3452.pivotY = 17.59f;
                this.field_3460.pitch = 1.5388988f;
                this.field_3460.pivotY = 22.97f;
                this.field_3458.pivotY = 18.4f;
                this.field_3459.roll = -0.0873f;
                this.field_3459.pivotY = 18.84f;
                this.field_3455.roll = 0.0873f;
                this.field_3455.pivotY = 18.84f;
                this.field_3450.pivotY += 1.9f;
                this.field_3457.pivotY += 1.9f;
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

    private static Pose getPose(ParrotEntity parrotEntity) {
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

    @Environment(value=EnvType.CLIENT)
    public static enum Pose {
        FLYING,
        STANDING,
        SITTING,
        PARTY,
        ON_SHOULDER;

    }
}

