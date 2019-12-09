/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.CompositeEntityModel;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BoatEntityModel
extends CompositeEntityModel<BoatEntity> {
    private final ModelPart[] paddles = new ModelPart[2];
    private final ModelPart bottom;
    private final ImmutableList<ModelPart> parts;

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
        this.parts = builder.build();
    }

    @Override
    public void setAngles(BoatEntity boatEntity, float f, float g, float h, float i, float j) {
        this.setPaddleAngle(boatEntity, 0, f);
        this.setPaddleAngle(boatEntity, 1, f);
    }

    public ImmutableList<ModelPart> getParts() {
        return this.parts;
    }

    public ModelPart getBottom() {
        return this.bottom;
    }

    protected ModelPart makePaddle(boolean isLeft) {
        ModelPart modelPart = new ModelPart(this, 62, isLeft ? 0 : 20).setTextureSize(128, 64);
        int i = 20;
        int j = 7;
        int k = 6;
        float f = -5.0f;
        modelPart.addCuboid(-1.0f, 0.0f, -5.0f, 2.0f, 2.0f, 18.0f);
        modelPart.addCuboid(isLeft ? -1.001f : 0.001f, -3.0f, 8.0f, 1.0f, 6.0f, 7.0f);
        return modelPart;
    }

    protected void setPaddleAngle(BoatEntity boat, int paddle, float angle) {
        float f = boat.interpolatePaddlePhase(paddle, angle);
        ModelPart modelPart = this.paddles[paddle];
        modelPart.pitch = (float)MathHelper.clampedLerp(-1.0471975803375244, -0.2617993950843811, (MathHelper.sin(-f) + 1.0f) / 2.0f);
        modelPart.yaw = (float)MathHelper.clampedLerp(-0.7853981852531433, 0.7853981852531433, (MathHelper.sin(-f + 1.0f) + 1.0f) / 2.0f);
        if (paddle == 1) {
            modelPart.yaw = (float)Math.PI - modelPart.yaw;
        }
    }

    @Override
    public /* synthetic */ Iterable getParts() {
        return this.getParts();
    }
}

