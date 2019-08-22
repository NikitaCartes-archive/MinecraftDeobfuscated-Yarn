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
public class SmallPufferfishEntityModel<T extends Entity>
extends EntityModel<T> {
    private final ModelPart field_3505;
    private final ModelPart field_3507;
    private final ModelPart field_3506;
    private final ModelPart field_3504;
    private final ModelPart field_3503;
    private final ModelPart field_3508;

    public SmallPufferfishEntityModel() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        int i = 23;
        this.field_3505 = new ModelPart(this, 0, 27);
        this.field_3505.addCuboid(-1.5f, -2.0f, -1.5f, 3, 2, 3);
        this.field_3505.setRotationPoint(0.0f, 23.0f, 0.0f);
        this.field_3507 = new ModelPart(this, 24, 6);
        this.field_3507.addCuboid(-1.5f, 0.0f, -1.5f, 1, 1, 1);
        this.field_3507.setRotationPoint(0.0f, 20.0f, 0.0f);
        this.field_3506 = new ModelPart(this, 28, 6);
        this.field_3506.addCuboid(0.5f, 0.0f, -1.5f, 1, 1, 1);
        this.field_3506.setRotationPoint(0.0f, 20.0f, 0.0f);
        this.field_3508 = new ModelPart(this, -3, 0);
        this.field_3508.addCuboid(-1.5f, 0.0f, 0.0f, 3, 0, 3);
        this.field_3508.setRotationPoint(0.0f, 22.0f, 1.5f);
        this.field_3504 = new ModelPart(this, 25, 0);
        this.field_3504.addCuboid(-1.0f, 0.0f, 0.0f, 1, 0, 2);
        this.field_3504.setRotationPoint(-1.5f, 22.0f, -1.5f);
        this.field_3503 = new ModelPart(this, 25, 0);
        this.field_3503.addCuboid(0.0f, 0.0f, 0.0f, 1, 0, 2);
        this.field_3503.setRotationPoint(1.5f, 22.0f, -1.5f);
    }

    @Override
    public void render(T entity, float f, float g, float h, float i, float j, float k) {
        this.setAngles(entity, f, g, h, i, j, k);
        this.field_3505.render(k);
        this.field_3507.render(k);
        this.field_3506.render(k);
        this.field_3508.render(k);
        this.field_3504.render(k);
        this.field_3503.render(k);
    }

    @Override
    public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
        this.field_3504.roll = -0.2f + 0.4f * MathHelper.sin(h * 0.2f);
        this.field_3503.roll = 0.2f - 0.4f * MathHelper.sin(h * 0.2f);
    }
}

