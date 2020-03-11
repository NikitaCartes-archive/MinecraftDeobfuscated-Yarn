/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.QuadrupedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class TurtleEntityModel<T extends TurtleEntity>
extends QuadrupedEntityModel<T> {
    private final ModelPart tail;

    public TurtleEntityModel(float scale) {
        super(12, scale, true, 120.0f, 0.0f, 9.0f, 6.0f, 120);
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.head = new ModelPart(this, 3, 0);
        this.head.addCuboid(-3.0f, -1.0f, -3.0f, 6.0f, 5.0f, 6.0f, 0.0f);
        this.head.setPivot(0.0f, 19.0f, -10.0f);
        this.torso = new ModelPart(this);
        this.torso.setTextureOffset(7, 37).addCuboid(-9.5f, 3.0f, -10.0f, 19.0f, 20.0f, 6.0f, 0.0f);
        this.torso.setTextureOffset(31, 1).addCuboid(-5.5f, 3.0f, -13.0f, 11.0f, 18.0f, 3.0f, 0.0f);
        this.torso.setPivot(0.0f, 11.0f, -10.0f);
        this.tail = new ModelPart(this);
        this.tail.setTextureOffset(70, 33).addCuboid(-4.5f, 3.0f, -14.0f, 9.0f, 18.0f, 1.0f, 0.0f);
        this.tail.setPivot(0.0f, 11.0f, -10.0f);
        boolean i = true;
        this.backRightLeg = new ModelPart(this, 1, 23);
        this.backRightLeg.addCuboid(-2.0f, 0.0f, 0.0f, 4.0f, 1.0f, 10.0f, 0.0f);
        this.backRightLeg.setPivot(-3.5f, 22.0f, 11.0f);
        this.backLeftLeg = new ModelPart(this, 1, 12);
        this.backLeftLeg.addCuboid(-2.0f, 0.0f, 0.0f, 4.0f, 1.0f, 10.0f, 0.0f);
        this.backLeftLeg.setPivot(3.5f, 22.0f, 11.0f);
        this.frontRightLeg = new ModelPart(this, 27, 30);
        this.frontRightLeg.addCuboid(-13.0f, 0.0f, -2.0f, 13.0f, 1.0f, 5.0f, 0.0f);
        this.frontRightLeg.setPivot(-5.0f, 21.0f, -4.0f);
        this.frontLeftLeg = new ModelPart(this, 27, 24);
        this.frontLeftLeg.addCuboid(0.0f, 0.0f, -2.0f, 13.0f, 1.0f, 5.0f, 0.0f);
        this.frontLeftLeg.setPivot(5.0f, 21.0f, -4.0f);
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return Iterables.concat(super.getBodyParts(), ImmutableList.of(this.tail));
    }

    @Override
    public void setAngles(T turtleEntity, float f, float g, float h, float i, float j) {
        super.setAngles(turtleEntity, f, g, h, i, j);
        this.backRightLeg.pitch = MathHelper.cos(f * 0.6662f * 0.6f) * 0.5f * g;
        this.backLeftLeg.pitch = MathHelper.cos(f * 0.6662f * 0.6f + (float)Math.PI) * 0.5f * g;
        this.frontRightLeg.roll = MathHelper.cos(f * 0.6662f * 0.6f + (float)Math.PI) * 0.5f * g;
        this.frontLeftLeg.roll = MathHelper.cos(f * 0.6662f * 0.6f) * 0.5f * g;
        this.frontRightLeg.pitch = 0.0f;
        this.frontLeftLeg.pitch = 0.0f;
        this.frontRightLeg.yaw = 0.0f;
        this.frontLeftLeg.yaw = 0.0f;
        this.backRightLeg.yaw = 0.0f;
        this.backLeftLeg.yaw = 0.0f;
        this.tail.pitch = 1.5707964f;
        if (!((Entity)turtleEntity).isTouchingWater() && ((Entity)turtleEntity).isOnGround()) {
            float k = ((TurtleEntity)turtleEntity).isDiggingSand() ? 4.0f : 1.0f;
            float l = ((TurtleEntity)turtleEntity).isDiggingSand() ? 2.0f : 1.0f;
            float m = 5.0f;
            this.frontRightLeg.yaw = MathHelper.cos(k * f * 5.0f + (float)Math.PI) * 8.0f * g * l;
            this.frontRightLeg.roll = 0.0f;
            this.frontLeftLeg.yaw = MathHelper.cos(k * f * 5.0f) * 8.0f * g * l;
            this.frontLeftLeg.roll = 0.0f;
            this.backRightLeg.yaw = MathHelper.cos(f * 5.0f + (float)Math.PI) * 3.0f * g;
            this.backRightLeg.pitch = 0.0f;
            this.backLeftLeg.yaw = MathHelper.cos(f * 5.0f) * 3.0f * g;
            this.backLeftLeg.pitch = 0.0f;
        }
        this.tail.visible = !this.child && ((TurtleEntity)turtleEntity).hasEgg();
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        boolean bl = this.tail.visible;
        if (bl) {
            matrices.push();
            matrices.translate(0.0, -0.08f, 0.0);
        }
        super.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        if (bl) {
            matrices.pop();
        }
    }
}

