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
    private final ModelPart body;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart leftWing;
    private final ModelPart rightWing;
    private final ModelPart beak;
    private final ModelPart wattle;

    public ChickenEntityModel() {
        int i = 16;
        this.head = new ModelPart(this, 0, 0);
        this.head.addCuboid(-2.0f, -6.0f, -2.0f, 4, 6, 3, 0.0f);
        this.head.setRotationPoint(0.0f, 15.0f, -4.0f);
        this.beak = new ModelPart(this, 14, 0);
        this.beak.addCuboid(-2.0f, -4.0f, -4.0f, 4, 2, 2, 0.0f);
        this.beak.setRotationPoint(0.0f, 15.0f, -4.0f);
        this.wattle = new ModelPart(this, 14, 4);
        this.wattle.addCuboid(-1.0f, -2.0f, -3.0f, 2, 2, 2, 0.0f);
        this.wattle.setRotationPoint(0.0f, 15.0f, -4.0f);
        this.body = new ModelPart(this, 0, 9);
        this.body.addCuboid(-3.0f, -4.0f, -3.0f, 6, 8, 6, 0.0f);
        this.body.setRotationPoint(0.0f, 16.0f, 0.0f);
        this.leftLeg = new ModelPart(this, 26, 0);
        this.leftLeg.addCuboid(-1.0f, 0.0f, -3.0f, 3, 5, 3);
        this.leftLeg.setRotationPoint(-2.0f, 19.0f, 1.0f);
        this.rightLeg = new ModelPart(this, 26, 0);
        this.rightLeg.addCuboid(-1.0f, 0.0f, -3.0f, 3, 5, 3);
        this.rightLeg.setRotationPoint(1.0f, 19.0f, 1.0f);
        this.leftWing = new ModelPart(this, 24, 13);
        this.leftWing.addCuboid(0.0f, 0.0f, -3.0f, 1, 4, 6);
        this.leftWing.setRotationPoint(-4.0f, 13.0f, 0.0f);
        this.rightWing = new ModelPart(this, 24, 13);
        this.rightWing.addCuboid(-1.0f, 0.0f, -3.0f, 1, 4, 6);
        this.rightWing.setRotationPoint(4.0f, 13.0f, 0.0f);
    }

    @Override
    public void render(T entity, float f, float g, float h, float i, float j, float k) {
        this.setAngles(entity, f, g, h, i, j, k);
        if (this.isChild) {
            float l = 2.0f;
            RenderSystem.pushMatrix();
            RenderSystem.translatef(0.0f, 5.0f * k, 2.0f * k);
            this.head.render(k);
            this.beak.render(k);
            this.wattle.render(k);
            RenderSystem.popMatrix();
            RenderSystem.pushMatrix();
            RenderSystem.scalef(0.5f, 0.5f, 0.5f);
            RenderSystem.translatef(0.0f, 24.0f * k, 0.0f);
            this.body.render(k);
            this.leftLeg.render(k);
            this.rightLeg.render(k);
            this.leftWing.render(k);
            this.rightWing.render(k);
            RenderSystem.popMatrix();
        } else {
            this.head.render(k);
            this.beak.render(k);
            this.wattle.render(k);
            this.body.render(k);
            this.leftLeg.render(k);
            this.rightLeg.render(k);
            this.leftWing.render(k);
            this.rightWing.render(k);
        }
    }

    @Override
    public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
        this.head.pitch = j * ((float)Math.PI / 180);
        this.head.yaw = i * ((float)Math.PI / 180);
        this.beak.pitch = this.head.pitch;
        this.beak.yaw = this.head.yaw;
        this.wattle.pitch = this.head.pitch;
        this.wattle.yaw = this.head.yaw;
        this.body.pitch = 1.5707964f;
        this.leftLeg.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g;
        this.rightLeg.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
        this.leftWing.roll = h;
        this.rightWing.roll = -h;
    }
}

