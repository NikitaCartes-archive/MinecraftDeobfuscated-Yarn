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
public class SmallPufferfishEntityModel<T extends Entity>
extends CompositeEntityModel<T> {
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
        this.field_3505.addCuboid(-1.5f, -2.0f, -1.5f, 3.0f, 2.0f, 3.0f);
        this.field_3505.setPivot(0.0f, 23.0f, 0.0f);
        this.field_3507 = new ModelPart(this, 24, 6);
        this.field_3507.addCuboid(-1.5f, 0.0f, -1.5f, 1.0f, 1.0f, 1.0f);
        this.field_3507.setPivot(0.0f, 20.0f, 0.0f);
        this.field_3506 = new ModelPart(this, 28, 6);
        this.field_3506.addCuboid(0.5f, 0.0f, -1.5f, 1.0f, 1.0f, 1.0f);
        this.field_3506.setPivot(0.0f, 20.0f, 0.0f);
        this.field_3508 = new ModelPart(this, -3, 0);
        this.field_3508.addCuboid(-1.5f, 0.0f, 0.0f, 3.0f, 0.0f, 3.0f);
        this.field_3508.setPivot(0.0f, 22.0f, 1.5f);
        this.field_3504 = new ModelPart(this, 25, 0);
        this.field_3504.addCuboid(-1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 2.0f);
        this.field_3504.setPivot(-1.5f, 22.0f, -1.5f);
        this.field_3503 = new ModelPart(this, 25, 0);
        this.field_3503.addCuboid(0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 2.0f);
        this.field_3503.setPivot(1.5f, 22.0f, -1.5f);
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of(this.field_3505, this.field_3507, this.field_3506, this.field_3508, this.field_3504, this.field_3503);
    }

    @Override
    public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
        this.field_3504.roll = -0.2f + 0.4f * MathHelper.sin(h * 0.2f);
        this.field_3503.roll = 0.2f - 0.4f * MathHelper.sin(h * 0.2f);
    }
}

