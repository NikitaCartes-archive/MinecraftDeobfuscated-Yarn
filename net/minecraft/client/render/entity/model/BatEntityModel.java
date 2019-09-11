/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BatEntityModel
extends EntityModel<BatEntity> {
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart rightWingTip;
    private final ModelPart leftWingTip;

    public BatEntityModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.head = new ModelPart(this, 0, 0);
        this.head.addCuboid(-3.0f, -3.0f, -3.0f, 6, 6, 6);
        ModelPart modelPart = new ModelPart(this, 24, 0);
        modelPart.addCuboid(-4.0f, -6.0f, -2.0f, 3, 4, 1);
        this.head.addChild(modelPart);
        ModelPart modelPart2 = new ModelPart(this, 24, 0);
        modelPart2.mirror = true;
        modelPart2.addCuboid(1.0f, -6.0f, -2.0f, 3, 4, 1);
        this.head.addChild(modelPart2);
        this.body = new ModelPart(this, 0, 16);
        this.body.addCuboid(-3.0f, 4.0f, -3.0f, 6, 12, 6);
        this.body.setTextureOffset(0, 34).addCuboid(-5.0f, 16.0f, 0.0f, 10, 6, 1);
        this.rightWing = new ModelPart(this, 42, 0);
        this.rightWing.addCuboid(-12.0f, 1.0f, 1.5f, 10, 16, 1);
        this.rightWingTip = new ModelPart(this, 24, 16);
        this.rightWingTip.setRotationPoint(-12.0f, 1.0f, 1.5f);
        this.rightWingTip.addCuboid(-8.0f, 1.0f, 0.0f, 8, 12, 1);
        this.leftWing = new ModelPart(this, 42, 0);
        this.leftWing.mirror = true;
        this.leftWing.addCuboid(2.0f, 1.0f, 1.5f, 10, 16, 1);
        this.leftWingTip = new ModelPart(this, 24, 16);
        this.leftWingTip.mirror = true;
        this.leftWingTip.setRotationPoint(12.0f, 1.0f, 1.5f);
        this.leftWingTip.addCuboid(0.0f, 1.0f, 0.0f, 8, 12, 1);
        this.body.addChild(this.rightWing);
        this.body.addChild(this.leftWing);
        this.rightWing.addChild(this.rightWingTip);
        this.leftWing.addChild(this.leftWingTip);
    }

    public void method_17068(BatEntity batEntity, float f, float g, float h, float i, float j, float k) {
        this.method_17069(batEntity, f, g, h, i, j, k);
        this.head.render(k);
        this.body.render(k);
    }

    public void method_17069(BatEntity batEntity, float f, float g, float h, float i, float j, float k) {
        if (batEntity.isRoosting()) {
            this.head.pitch = j * ((float)Math.PI / 180);
            this.head.yaw = (float)Math.PI - i * ((float)Math.PI / 180);
            this.head.roll = (float)Math.PI;
            this.head.setRotationPoint(0.0f, -2.0f, 0.0f);
            this.rightWing.setRotationPoint(-3.0f, 0.0f, 3.0f);
            this.leftWing.setRotationPoint(3.0f, 0.0f, 3.0f);
            this.body.pitch = (float)Math.PI;
            this.rightWing.pitch = -0.15707964f;
            this.rightWing.yaw = -1.2566371f;
            this.rightWingTip.yaw = -1.7278761f;
            this.leftWing.pitch = this.rightWing.pitch;
            this.leftWing.yaw = -this.rightWing.yaw;
            this.leftWingTip.yaw = -this.rightWingTip.yaw;
        } else {
            this.head.pitch = j * ((float)Math.PI / 180);
            this.head.yaw = i * ((float)Math.PI / 180);
            this.head.roll = 0.0f;
            this.head.setRotationPoint(0.0f, 0.0f, 0.0f);
            this.rightWing.setRotationPoint(0.0f, 0.0f, 0.0f);
            this.leftWing.setRotationPoint(0.0f, 0.0f, 0.0f);
            this.body.pitch = 0.7853982f + MathHelper.cos(h * 0.1f) * 0.15f;
            this.body.yaw = 0.0f;
            this.rightWing.yaw = MathHelper.cos(h * 1.3f) * (float)Math.PI * 0.25f;
            this.leftWing.yaw = -this.rightWing.yaw;
            this.rightWingTip.yaw = this.rightWing.yaw * 0.5f;
            this.leftWingTip.yaw = -this.rightWing.yaw * 0.5f;
        }
    }

    @Override
    public /* synthetic */ void setAngles(Entity entity, float f, float g, float h, float i, float j, float k) {
        this.method_17069((BatEntity)entity, f, g, h, i, j, k);
    }

    @Override
    public /* synthetic */ void render(Entity entity, float f, float g, float h, float i, float j, float k) {
        this.method_17068((BatEntity)entity, f, g, h, i, j, k);
    }
}

