/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class SpiderEntityModel<T extends Entity>
extends EntityModel<T> {
    private final Cuboid field_3583;
    private final Cuboid field_3585;
    private final Cuboid field_3584;
    private final Cuboid field_3580;
    private final Cuboid field_3578;
    private final Cuboid field_3586;
    private final Cuboid field_3577;
    private final Cuboid field_3579;
    private final Cuboid field_3581;
    private final Cuboid field_3576;
    private final Cuboid field_3582;

    public SpiderEntityModel() {
        float f = 0.0f;
        int i = 15;
        this.field_3583 = new Cuboid(this, 32, 4);
        this.field_3583.addBox(-4.0f, -4.0f, -8.0f, 8, 8, 8, 0.0f);
        this.field_3583.setRotationPoint(0.0f, 15.0f, -3.0f);
        this.field_3585 = new Cuboid(this, 0, 0);
        this.field_3585.addBox(-3.0f, -3.0f, -3.0f, 6, 6, 6, 0.0f);
        this.field_3585.setRotationPoint(0.0f, 15.0f, 0.0f);
        this.field_3584 = new Cuboid(this, 0, 12);
        this.field_3584.addBox(-5.0f, -4.0f, -6.0f, 10, 8, 12, 0.0f);
        this.field_3584.setRotationPoint(0.0f, 15.0f, 9.0f);
        this.field_3580 = new Cuboid(this, 18, 0);
        this.field_3580.addBox(-15.0f, -1.0f, -1.0f, 16, 2, 2, 0.0f);
        this.field_3580.setRotationPoint(-4.0f, 15.0f, 2.0f);
        this.field_3578 = new Cuboid(this, 18, 0);
        this.field_3578.addBox(-1.0f, -1.0f, -1.0f, 16, 2, 2, 0.0f);
        this.field_3578.setRotationPoint(4.0f, 15.0f, 2.0f);
        this.field_3586 = new Cuboid(this, 18, 0);
        this.field_3586.addBox(-15.0f, -1.0f, -1.0f, 16, 2, 2, 0.0f);
        this.field_3586.setRotationPoint(-4.0f, 15.0f, 1.0f);
        this.field_3577 = new Cuboid(this, 18, 0);
        this.field_3577.addBox(-1.0f, -1.0f, -1.0f, 16, 2, 2, 0.0f);
        this.field_3577.setRotationPoint(4.0f, 15.0f, 1.0f);
        this.field_3579 = new Cuboid(this, 18, 0);
        this.field_3579.addBox(-15.0f, -1.0f, -1.0f, 16, 2, 2, 0.0f);
        this.field_3579.setRotationPoint(-4.0f, 15.0f, 0.0f);
        this.field_3581 = new Cuboid(this, 18, 0);
        this.field_3581.addBox(-1.0f, -1.0f, -1.0f, 16, 2, 2, 0.0f);
        this.field_3581.setRotationPoint(4.0f, 15.0f, 0.0f);
        this.field_3576 = new Cuboid(this, 18, 0);
        this.field_3576.addBox(-15.0f, -1.0f, -1.0f, 16, 2, 2, 0.0f);
        this.field_3576.setRotationPoint(-4.0f, 15.0f, -1.0f);
        this.field_3582 = new Cuboid(this, 18, 0);
        this.field_3582.addBox(-1.0f, -1.0f, -1.0f, 16, 2, 2, 0.0f);
        this.field_3582.setRotationPoint(4.0f, 15.0f, -1.0f);
    }

    @Override
    public void render(T entity, float f, float g, float h, float i, float j, float k) {
        this.setAngles(entity, f, g, h, i, j, k);
        this.field_3583.render(k);
        this.field_3585.render(k);
        this.field_3584.render(k);
        this.field_3580.render(k);
        this.field_3578.render(k);
        this.field_3586.render(k);
        this.field_3577.render(k);
        this.field_3579.render(k);
        this.field_3581.render(k);
        this.field_3576.render(k);
        this.field_3582.render(k);
    }

    @Override
    public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
        this.field_3583.yaw = i * ((float)Math.PI / 180);
        this.field_3583.pitch = j * ((float)Math.PI / 180);
        float l = 0.7853982f;
        this.field_3580.roll = -0.7853982f;
        this.field_3578.roll = 0.7853982f;
        this.field_3586.roll = -0.58119464f;
        this.field_3577.roll = 0.58119464f;
        this.field_3579.roll = -0.58119464f;
        this.field_3581.roll = 0.58119464f;
        this.field_3576.roll = -0.7853982f;
        this.field_3582.roll = 0.7853982f;
        float m = -0.0f;
        float n = 0.3926991f;
        this.field_3580.yaw = 0.7853982f;
        this.field_3578.yaw = -0.7853982f;
        this.field_3586.yaw = 0.3926991f;
        this.field_3577.yaw = -0.3926991f;
        this.field_3579.yaw = -0.3926991f;
        this.field_3581.yaw = 0.3926991f;
        this.field_3576.yaw = -0.7853982f;
        this.field_3582.yaw = 0.7853982f;
        float o = -(MathHelper.cos(f * 0.6662f * 2.0f + 0.0f) * 0.4f) * g;
        float p = -(MathHelper.cos(f * 0.6662f * 2.0f + (float)Math.PI) * 0.4f) * g;
        float q = -(MathHelper.cos(f * 0.6662f * 2.0f + 1.5707964f) * 0.4f) * g;
        float r = -(MathHelper.cos(f * 0.6662f * 2.0f + 4.712389f) * 0.4f) * g;
        float s = Math.abs(MathHelper.sin(f * 0.6662f + 0.0f) * 0.4f) * g;
        float t = Math.abs(MathHelper.sin(f * 0.6662f + (float)Math.PI) * 0.4f) * g;
        float u = Math.abs(MathHelper.sin(f * 0.6662f + 1.5707964f) * 0.4f) * g;
        float v = Math.abs(MathHelper.sin(f * 0.6662f + 4.712389f) * 0.4f) * g;
        this.field_3580.yaw += o;
        this.field_3578.yaw += -o;
        this.field_3586.yaw += p;
        this.field_3577.yaw += -p;
        this.field_3579.yaw += q;
        this.field_3581.yaw += -q;
        this.field_3576.yaw += r;
        this.field_3582.yaw += -r;
        this.field_3580.roll += s;
        this.field_3578.roll += -s;
        this.field_3586.roll += t;
        this.field_3577.roll += -t;
        this.field_3579.roll += u;
        this.field_3581.roll += -u;
        this.field_3576.roll += v;
        this.field_3582.roll += -v;
    }
}

