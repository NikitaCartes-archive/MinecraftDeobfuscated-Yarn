/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.CompositeEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class SalmonEntityModel<T extends Entity>
extends CompositeEntityModel<T> {
    private final ModelPart field_3546;
    private final ModelPart field_3548;
    private final ModelPart field_3547;
    private final ModelPart field_3542;
    private final ModelPart field_3544;

    public SalmonEntityModel() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        int i = 20;
        this.field_3546 = new ModelPart(this, 0, 0);
        this.field_3546.addCuboid(-1.5f, -2.5f, 0.0f, 3.0f, 5.0f, 8.0f);
        this.field_3546.setPivot(0.0f, 20.0f, 0.0f);
        this.field_3548 = new ModelPart(this, 0, 13);
        this.field_3548.addCuboid(-1.5f, -2.5f, 0.0f, 3.0f, 5.0f, 8.0f);
        this.field_3548.setPivot(0.0f, 20.0f, 8.0f);
        this.field_3547 = new ModelPart(this, 22, 0);
        this.field_3547.addCuboid(-1.0f, -2.0f, -3.0f, 2.0f, 4.0f, 3.0f);
        this.field_3547.setPivot(0.0f, 20.0f, 0.0f);
        ModelPart modelPart = new ModelPart(this, 20, 10);
        modelPart.addCuboid(0.0f, -2.5f, 0.0f, 0.0f, 5.0f, 6.0f);
        modelPart.setPivot(0.0f, 0.0f, 8.0f);
        this.field_3548.addChild(modelPart);
        ModelPart modelPart2 = new ModelPart(this, 2, 1);
        modelPart2.addCuboid(0.0f, 0.0f, 0.0f, 0.0f, 2.0f, 3.0f);
        modelPart2.setPivot(0.0f, -4.5f, 5.0f);
        this.field_3546.addChild(modelPart2);
        ModelPart modelPart3 = new ModelPart(this, 0, 2);
        modelPart3.addCuboid(0.0f, 0.0f, 0.0f, 0.0f, 2.0f, 4.0f);
        modelPart3.setPivot(0.0f, -4.5f, -1.0f);
        this.field_3548.addChild(modelPart3);
        this.field_3542 = new ModelPart(this, -4, 0);
        this.field_3542.addCuboid(-2.0f, 0.0f, 0.0f, 2.0f, 0.0f, 2.0f);
        this.field_3542.setPivot(-1.5f, 21.5f, 0.0f);
        this.field_3542.roll = -0.7853982f;
        this.field_3544 = new ModelPart(this, 0, 0);
        this.field_3544.addCuboid(0.0f, 0.0f, 0.0f, 2.0f, 0.0f, 2.0f);
        this.field_3544.setPivot(1.5f, 21.5f, 0.0f);
        this.field_3544.roll = 0.7853982f;
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of(this.field_3546, this.field_3548, this.field_3547, this.field_3542, this.field_3544);
    }

    @Override
    public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
        float l = 1.0f;
        float m = 1.0f;
        if (!((Entity)entity).isInsideWater()) {
            l = 1.3f;
            m = 1.7f;
        }
        this.field_3548.yaw = -l * 0.25f * MathHelper.sin(m * 0.6f * h);
    }
}

