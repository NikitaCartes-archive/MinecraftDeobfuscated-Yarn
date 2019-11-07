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
public class MediumPufferfishEntityModel<T extends Entity>
extends CompositeEntityModel<T> {
    private final ModelPart field_3516;
    private final ModelPart field_3518;
    private final ModelPart field_3517;
    private final ModelPart field_3513;
    private final ModelPart field_3511;
    private final ModelPart field_3519;
    private final ModelPart field_3510;
    private final ModelPart field_3512;
    private final ModelPart field_3514;
    private final ModelPart field_3509;
    private final ModelPart field_3515;

    public MediumPufferfishEntityModel() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        int i = 22;
        this.field_3516 = new ModelPart(this, 12, 22);
        this.field_3516.addCuboid(-2.5f, -5.0f, -2.5f, 5.0f, 5.0f, 5.0f);
        this.field_3516.setPivot(0.0f, 22.0f, 0.0f);
        this.field_3518 = new ModelPart(this, 24, 0);
        this.field_3518.addCuboid(-2.0f, 0.0f, 0.0f, 2.0f, 0.0f, 2.0f);
        this.field_3518.setPivot(-2.5f, 17.0f, -1.5f);
        this.field_3517 = new ModelPart(this, 24, 3);
        this.field_3517.addCuboid(0.0f, 0.0f, 0.0f, 2.0f, 0.0f, 2.0f);
        this.field_3517.setPivot(2.5f, 17.0f, -1.5f);
        this.field_3513 = new ModelPart(this, 15, 16);
        this.field_3513.addCuboid(-2.5f, -1.0f, 0.0f, 5.0f, 1.0f, 1.0f);
        this.field_3513.setPivot(0.0f, 17.0f, -2.5f);
        this.field_3513.pitch = 0.7853982f;
        this.field_3511 = new ModelPart(this, 10, 16);
        this.field_3511.addCuboid(-2.5f, -1.0f, -1.0f, 5.0f, 1.0f, 1.0f);
        this.field_3511.setPivot(0.0f, 17.0f, 2.5f);
        this.field_3511.pitch = -0.7853982f;
        this.field_3519 = new ModelPart(this, 8, 16);
        this.field_3519.addCuboid(-1.0f, -5.0f, 0.0f, 1.0f, 5.0f, 1.0f);
        this.field_3519.setPivot(-2.5f, 22.0f, -2.5f);
        this.field_3519.yaw = -0.7853982f;
        this.field_3510 = new ModelPart(this, 8, 16);
        this.field_3510.addCuboid(-1.0f, -5.0f, 0.0f, 1.0f, 5.0f, 1.0f);
        this.field_3510.setPivot(-2.5f, 22.0f, 2.5f);
        this.field_3510.yaw = 0.7853982f;
        this.field_3512 = new ModelPart(this, 4, 16);
        this.field_3512.addCuboid(0.0f, -5.0f, 0.0f, 1.0f, 5.0f, 1.0f);
        this.field_3512.setPivot(2.5f, 22.0f, 2.5f);
        this.field_3512.yaw = -0.7853982f;
        this.field_3514 = new ModelPart(this, 0, 16);
        this.field_3514.addCuboid(0.0f, -5.0f, 0.0f, 1.0f, 5.0f, 1.0f);
        this.field_3514.setPivot(2.5f, 22.0f, -2.5f);
        this.field_3514.yaw = 0.7853982f;
        this.field_3509 = new ModelPart(this, 8, 22);
        this.field_3509.addCuboid(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        this.field_3509.setPivot(0.5f, 22.0f, 2.5f);
        this.field_3509.pitch = 0.7853982f;
        this.field_3515 = new ModelPart(this, 17, 21);
        this.field_3515.addCuboid(-2.5f, 0.0f, 0.0f, 5.0f, 1.0f, 1.0f);
        this.field_3515.setPivot(0.0f, 22.0f, -2.5f);
        this.field_3515.pitch = -0.7853982f;
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of(this.field_3516, this.field_3518, this.field_3517, this.field_3513, this.field_3511, this.field_3519, this.field_3510, this.field_3512, this.field_3514, this.field_3509, this.field_3515);
    }

    @Override
    public void setAngles(T entity, float f, float g, float h, float i, float j) {
        this.field_3518.roll = -0.2f + 0.4f * MathHelper.sin(h * 0.2f);
        this.field_3517.roll = 0.2f - 0.4f * MathHelper.sin(h * 0.2f);
    }
}

