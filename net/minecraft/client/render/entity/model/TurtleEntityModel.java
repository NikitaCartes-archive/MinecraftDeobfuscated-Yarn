/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.QuadrupedEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class TurtleEntityModel<T extends TurtleEntity>
extends QuadrupedEntityModel<T> {
    private final ModelPart field_3594;

    public TurtleEntityModel(float f) {
        super(12, f);
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.head = new ModelPart(this, 3, 0);
        this.head.addCuboid(-3.0f, -1.0f, -3.0f, 6.0f, 5.0f, 6.0f, 0.0f);
        this.head.setRotationPoint(0.0f, 19.0f, -10.0f);
        this.body = new ModelPart(this);
        this.body.setTextureOffset(7, 37).addCuboid(-9.5f, 3.0f, -10.0f, 19.0f, 20.0f, 6.0f, 0.0f);
        this.body.setTextureOffset(31, 1).addCuboid(-5.5f, 3.0f, -13.0f, 11.0f, 18.0f, 3.0f, 0.0f);
        this.body.setRotationPoint(0.0f, 11.0f, -10.0f);
        this.field_3594 = new ModelPart(this);
        this.field_3594.setTextureOffset(70, 33).addCuboid(-4.5f, 3.0f, -14.0f, 9.0f, 18.0f, 1.0f, 0.0f);
        this.field_3594.setRotationPoint(0.0f, 11.0f, -10.0f);
        boolean i = true;
        this.leg1 = new ModelPart(this, 1, 23);
        this.leg1.addCuboid(-2.0f, 0.0f, 0.0f, 4.0f, 1.0f, 10.0f, 0.0f);
        this.leg1.setRotationPoint(-3.5f, 22.0f, 11.0f);
        this.leg2 = new ModelPart(this, 1, 12);
        this.leg2.addCuboid(-2.0f, 0.0f, 0.0f, 4.0f, 1.0f, 10.0f, 0.0f);
        this.leg2.setRotationPoint(3.5f, 22.0f, 11.0f);
        this.leg3 = new ModelPart(this, 27, 30);
        this.leg3.addCuboid(-13.0f, 0.0f, -2.0f, 13.0f, 1.0f, 5.0f, 0.0f);
        this.leg3.setRotationPoint(-5.0f, 21.0f, -4.0f);
        this.leg4 = new ModelPart(this, 27, 24);
        this.leg4.addCuboid(0.0f, 0.0f, -2.0f, 13.0f, 1.0f, 5.0f, 0.0f);
        this.leg4.setRotationPoint(5.0f, 21.0f, -4.0f);
    }

    public void method_17124(T turtleEntity, float f, float g, float h, float i, float j, float k) {
        this.method_17125(turtleEntity, f, g, h, i, j, k);
        if (this.isChild) {
            float l = 6.0f;
            RenderSystem.pushMatrix();
            RenderSystem.scalef(0.16666667f, 0.16666667f, 0.16666667f);
            RenderSystem.translatef(0.0f, 120.0f * k, 0.0f);
            this.head.render(k);
            this.body.render(k);
            this.leg1.render(k);
            this.leg2.render(k);
            this.leg3.render(k);
            this.leg4.render(k);
            RenderSystem.popMatrix();
        } else {
            RenderSystem.pushMatrix();
            if (((TurtleEntity)turtleEntity).hasEgg()) {
                RenderSystem.translatef(0.0f, -0.08f, 0.0f);
            }
            this.head.render(k);
            this.body.render(k);
            RenderSystem.pushMatrix();
            this.leg1.render(k);
            this.leg2.render(k);
            RenderSystem.popMatrix();
            this.leg3.render(k);
            this.leg4.render(k);
            if (((TurtleEntity)turtleEntity).hasEgg()) {
                this.field_3594.render(k);
            }
            RenderSystem.popMatrix();
        }
    }

    public void method_17125(T turtleEntity, float f, float g, float h, float i, float j, float k) {
        super.setAngles(turtleEntity, f, g, h, i, j, k);
        this.leg1.pitch = MathHelper.cos(f * 0.6662f * 0.6f) * 0.5f * g;
        this.leg2.pitch = MathHelper.cos(f * 0.6662f * 0.6f + (float)Math.PI) * 0.5f * g;
        this.leg3.roll = MathHelper.cos(f * 0.6662f * 0.6f + (float)Math.PI) * 0.5f * g;
        this.leg4.roll = MathHelper.cos(f * 0.6662f * 0.6f) * 0.5f * g;
        this.leg3.pitch = 0.0f;
        this.leg4.pitch = 0.0f;
        this.leg3.yaw = 0.0f;
        this.leg4.yaw = 0.0f;
        this.leg1.yaw = 0.0f;
        this.leg2.yaw = 0.0f;
        this.field_3594.pitch = 1.5707964f;
        if (!((Entity)turtleEntity).isInsideWater() && ((TurtleEntity)turtleEntity).onGround) {
            float l = ((TurtleEntity)turtleEntity).isDiggingSand() ? 4.0f : 1.0f;
            float m = ((TurtleEntity)turtleEntity).isDiggingSand() ? 2.0f : 1.0f;
            float n = 5.0f;
            this.leg3.yaw = MathHelper.cos(l * f * 5.0f + (float)Math.PI) * 8.0f * g * m;
            this.leg3.roll = 0.0f;
            this.leg4.yaw = MathHelper.cos(l * f * 5.0f) * 8.0f * g * m;
            this.leg4.roll = 0.0f;
            this.leg1.yaw = MathHelper.cos(f * 5.0f + (float)Math.PI) * 3.0f * g;
            this.leg1.pitch = 0.0f;
            this.leg2.yaw = MathHelper.cos(f * 5.0f) * 3.0f * g;
            this.leg2.pitch = 0.0f;
        }
    }

    @Override
    public /* synthetic */ void setAngles(Entity entity, float f, float g, float h, float i, float j, float k) {
        this.method_17125((TurtleEntity)entity, f, g, h, i, j, k);
    }

    @Override
    public /* synthetic */ void render(Entity entity, float f, float g, float h, float i, float j, float k) {
        this.method_17124((TurtleEntity)entity, f, g, h, i, j, k);
    }
}

