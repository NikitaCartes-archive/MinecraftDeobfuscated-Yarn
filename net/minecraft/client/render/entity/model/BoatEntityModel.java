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
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BoatEntityModel
extends EntityModel<BoatEntity> {
    private final Cuboid[] body = new Cuboid[5];
    private final Cuboid[] paddles = new Cuboid[2];
    private final Cuboid field_3326;

    public BoatEntityModel() {
        this.body[0] = new Cuboid(this, 0, 0).setTextureSize(128, 64);
        this.body[1] = new Cuboid(this, 0, 19).setTextureSize(128, 64);
        this.body[2] = new Cuboid(this, 0, 27).setTextureSize(128, 64);
        this.body[3] = new Cuboid(this, 0, 35).setTextureSize(128, 64);
        this.body[4] = new Cuboid(this, 0, 43).setTextureSize(128, 64);
        int i = 32;
        int j = 6;
        int k = 20;
        int l = 4;
        int m = 28;
        this.body[0].addBox(-14.0f, -9.0f, -3.0f, 28, 16, 3, 0.0f);
        this.body[0].setRotationPoint(0.0f, 3.0f, 1.0f);
        this.body[1].addBox(-13.0f, -7.0f, -1.0f, 18, 6, 2, 0.0f);
        this.body[1].setRotationPoint(-15.0f, 4.0f, 4.0f);
        this.body[2].addBox(-8.0f, -7.0f, -1.0f, 16, 6, 2, 0.0f);
        this.body[2].setRotationPoint(15.0f, 4.0f, 0.0f);
        this.body[3].addBox(-14.0f, -7.0f, -1.0f, 28, 6, 2, 0.0f);
        this.body[3].setRotationPoint(0.0f, 4.0f, -9.0f);
        this.body[4].addBox(-14.0f, -7.0f, -1.0f, 28, 6, 2, 0.0f);
        this.body[4].setRotationPoint(0.0f, 4.0f, 9.0f);
        this.body[0].pitch = 1.5707964f;
        this.body[1].yaw = 4.712389f;
        this.body[2].yaw = 1.5707964f;
        this.body[3].yaw = (float)Math.PI;
        this.paddles[0] = this.makePaddle(true);
        this.paddles[0].setRotationPoint(3.0f, -5.0f, 9.0f);
        this.paddles[1] = this.makePaddle(false);
        this.paddles[1].setRotationPoint(3.0f, -5.0f, -9.0f);
        this.paddles[1].yaw = (float)Math.PI;
        this.paddles[0].roll = 0.19634955f;
        this.paddles[1].roll = 0.19634955f;
        this.field_3326 = new Cuboid(this, 0, 0).setTextureSize(128, 64);
        this.field_3326.addBox(-14.0f, -9.0f, -3.0f, 28, 16, 3, 0.0f);
        this.field_3326.setRotationPoint(0.0f, -3.0f, 1.0f);
        this.field_3326.pitch = 1.5707964f;
    }

    public void method_17071(BoatEntity boatEntity, float f, float g, float h, float i, float j, float k) {
        GlStateManager.rotatef(90.0f, 0.0f, 1.0f, 0.0f);
        this.setAngles(boatEntity, f, g, h, i, j, k);
        for (int l = 0; l < 5; ++l) {
            this.body[l].render(k);
        }
        this.renderPaddle(boatEntity, 0, k, f);
        this.renderPaddle(boatEntity, 1, k, f);
    }

    public void renderPass(Entity entity, float f, float g, float h, float i, float j, float k) {
        GlStateManager.rotatef(90.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.colorMask(false, false, false, false);
        this.field_3326.render(k);
        GlStateManager.colorMask(true, true, true, true);
    }

    protected Cuboid makePaddle(boolean bl) {
        Cuboid cuboid = new Cuboid(this, 62, bl ? 0 : 20).setTextureSize(128, 64);
        int i = 20;
        int j = 7;
        int k = 6;
        float f = -5.0f;
        cuboid.addBox(-1.0f, 0.0f, -5.0f, 2, 2, 18);
        cuboid.addBox(bl ? -1.001f : 0.001f, -3.0f, 8.0f, 1, 6, 7);
        return cuboid;
    }

    protected void renderPaddle(BoatEntity boatEntity, int i, float f, float g) {
        float h = boatEntity.interpolatePaddlePhase(i, g);
        Cuboid cuboid = this.paddles[i];
        cuboid.pitch = (float)MathHelper.clampedLerp(-1.0471975803375244, -0.2617993950843811, (MathHelper.sin(-h) + 1.0f) / 2.0f);
        cuboid.yaw = (float)MathHelper.clampedLerp(-0.7853981852531433, 0.7853981852531433, (MathHelper.sin(-h + 1.0f) + 1.0f) / 2.0f);
        if (i == 1) {
            cuboid.yaw = (float)Math.PI - cuboid.yaw;
        }
        cuboid.render(f);
    }

    @Override
    public /* synthetic */ void render(Entity entity, float f, float g, float h, float i, float j, float k) {
        this.method_17071((BoatEntity)entity, f, g, h, i, j, k);
    }
}

