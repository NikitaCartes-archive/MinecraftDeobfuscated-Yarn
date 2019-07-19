/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class SalmonEntityModel<T extends Entity>
extends EntityModel<T> {
    private final ModelPart field_3546;
    private final ModelPart field_3548;
    private final ModelPart field_3547;
    private final ModelPart field_3545;
    private final ModelPart field_3543;
    private final ModelPart field_3549;
    private final ModelPart field_3542;
    private final ModelPart field_3544;

    public SalmonEntityModel() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        int i = 20;
        this.field_3546 = new ModelPart(this, 0, 0);
        this.field_3546.addCuboid(-1.5f, -2.5f, 0.0f, 3, 5, 8);
        this.field_3546.setPivot(0.0f, 20.0f, 0.0f);
        this.field_3548 = new ModelPart(this, 0, 13);
        this.field_3548.addCuboid(-1.5f, -2.5f, 0.0f, 3, 5, 8);
        this.field_3548.setPivot(0.0f, 20.0f, 8.0f);
        this.field_3547 = new ModelPart(this, 22, 0);
        this.field_3547.addCuboid(-1.0f, -2.0f, -3.0f, 2, 4, 3);
        this.field_3547.setPivot(0.0f, 20.0f, 0.0f);
        this.field_3549 = new ModelPart(this, 20, 10);
        this.field_3549.addCuboid(0.0f, -2.5f, 0.0f, 0, 5, 6);
        this.field_3549.setPivot(0.0f, 0.0f, 8.0f);
        this.field_3548.addChild(this.field_3549);
        this.field_3545 = new ModelPart(this, 2, 1);
        this.field_3545.addCuboid(0.0f, 0.0f, 0.0f, 0, 2, 3);
        this.field_3545.setPivot(0.0f, -4.5f, 5.0f);
        this.field_3546.addChild(this.field_3545);
        this.field_3543 = new ModelPart(this, 0, 2);
        this.field_3543.addCuboid(0.0f, 0.0f, 0.0f, 0, 2, 4);
        this.field_3543.setPivot(0.0f, -4.5f, -1.0f);
        this.field_3548.addChild(this.field_3543);
        this.field_3542 = new ModelPart(this, -4, 0);
        this.field_3542.addCuboid(-2.0f, 0.0f, 0.0f, 2, 0, 2);
        this.field_3542.setPivot(-1.5f, 21.5f, 0.0f);
        this.field_3542.roll = -0.7853982f;
        this.field_3544 = new ModelPart(this, 0, 0);
        this.field_3544.addCuboid(0.0f, 0.0f, 0.0f, 2, 0, 2);
        this.field_3544.setPivot(1.5f, 21.5f, 0.0f);
        this.field_3544.roll = 0.7853982f;
    }

    @Override
    public void render(T entity, float f, float g, float h, float i, float j, float k) {
        this.setAngles(entity, f, g, h, i, j, k);
        this.field_3546.render(k);
        this.field_3548.render(k);
        this.field_3547.render(k);
        this.field_3542.render(k);
        this.field_3544.render(k);
    }

    @Override
    public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
        float l = 1.0f;
        float m = 1.0f;
        if (!((Entity)entity).isTouchingWater()) {
            l = 1.3f;
            m = 1.7f;
        }
        this.field_3548.yaw = -l * 0.25f * MathHelper.sin(m * 0.6f * h);
    }
}

