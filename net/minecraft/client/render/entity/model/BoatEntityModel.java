/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4595;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BoatEntityModel
extends class_4595<BoatEntity> {
    private final ModelPart[] paddles = new ModelPart[2];
    private final ModelPart bottom;
    private final ImmutableList<ModelPart> field_20922;

    public BoatEntityModel() {
        ModelPart[] modelParts = new ModelPart[]{new ModelPart(this, 0, 0).setTextureSize(128, 64), new ModelPart(this, 0, 19).setTextureSize(128, 64), new ModelPart(this, 0, 27).setTextureSize(128, 64), new ModelPart(this, 0, 35).setTextureSize(128, 64), new ModelPart(this, 0, 43).setTextureSize(128, 64)};
        int i = 32;
        int j = 6;
        int k = 20;
        int l = 4;
        int m = 28;
        modelParts[0].addCuboid(-14.0f, -9.0f, -3.0f, 28.0f, 16.0f, 3.0f, 0.0f);
        modelParts[0].setPivot(0.0f, 3.0f, 1.0f);
        modelParts[1].addCuboid(-13.0f, -7.0f, -1.0f, 18.0f, 6.0f, 2.0f, 0.0f);
        modelParts[1].setPivot(-15.0f, 4.0f, 4.0f);
        modelParts[2].addCuboid(-8.0f, -7.0f, -1.0f, 16.0f, 6.0f, 2.0f, 0.0f);
        modelParts[2].setPivot(15.0f, 4.0f, 0.0f);
        modelParts[3].addCuboid(-14.0f, -7.0f, -1.0f, 28.0f, 6.0f, 2.0f, 0.0f);
        modelParts[3].setPivot(0.0f, 4.0f, -9.0f);
        modelParts[4].addCuboid(-14.0f, -7.0f, -1.0f, 28.0f, 6.0f, 2.0f, 0.0f);
        modelParts[4].setPivot(0.0f, 4.0f, 9.0f);
        modelParts[0].pitch = 1.5707964f;
        modelParts[1].yaw = 4.712389f;
        modelParts[2].yaw = 1.5707964f;
        modelParts[3].yaw = (float)Math.PI;
        this.paddles[0] = this.makePaddle(true);
        this.paddles[0].setPivot(3.0f, -5.0f, 9.0f);
        this.paddles[1] = this.makePaddle(false);
        this.paddles[1].setPivot(3.0f, -5.0f, -9.0f);
        this.paddles[1].yaw = (float)Math.PI;
        this.paddles[0].roll = 0.19634955f;
        this.paddles[1].roll = 0.19634955f;
        this.bottom = new ModelPart(this, 0, 0).setTextureSize(128, 64);
        this.bottom.addCuboid(-14.0f, -9.0f, -3.0f, 28.0f, 16.0f, 3.0f, 0.0f);
        this.bottom.setPivot(0.0f, -3.0f, 1.0f);
        this.bottom.pitch = 1.5707964f;
        ImmutableList.Builder builder = ImmutableList.builder();
        builder.addAll(Arrays.asList(modelParts));
        builder.addAll(Arrays.asList(this.paddles));
        this.field_20922 = builder.build();
    }

    public void method_22952(BoatEntity boatEntity, float f, float g, float h, float i, float j, float k) {
        this.renderPaddle(boatEntity, 0, k, f);
        this.renderPaddle(boatEntity, 1, k, f);
    }

    public ImmutableList<ModelPart> method_22953() {
        return this.field_20922;
    }

    public ModelPart method_22954() {
        return this.bottom;
    }

    protected ModelPart makePaddle(boolean bl) {
        ModelPart modelPart = new ModelPart(this, 62, bl ? 0 : 20).setTextureSize(128, 64);
        int i = 20;
        int j = 7;
        int k = 6;
        float f = -5.0f;
        modelPart.addCuboid(-1.0f, 0.0f, -5.0f, 2.0f, 2.0f, 18.0f);
        modelPart.addCuboid(bl ? -1.001f : 0.001f, -3.0f, 8.0f, 1.0f, 6.0f, 7.0f);
        return modelPart;
    }

    protected void renderPaddle(BoatEntity boatEntity, int i, float f, float g) {
        float h = boatEntity.interpolatePaddlePhase(i, g);
        ModelPart modelPart = this.paddles[i];
        modelPart.pitch = (float)MathHelper.clampedLerp(-1.0471975803375244, -0.2617993950843811, (MathHelper.sin(-h) + 1.0f) / 2.0f);
        modelPart.yaw = (float)MathHelper.clampedLerp(-0.7853981852531433, 0.7853981852531433, (MathHelper.sin(-h + 1.0f) + 1.0f) / 2.0f);
        if (i == 1) {
            modelPart.yaw = (float)Math.PI - modelPart.yaw;
        }
    }

    @Override
    public /* synthetic */ Iterable getParts() {
        return this.method_22953();
    }
}

