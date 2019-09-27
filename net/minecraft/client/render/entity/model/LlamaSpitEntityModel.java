/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4595;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;

@Environment(value=EnvType.CLIENT)
public class LlamaSpitEntityModel<T extends Entity>
extends class_4595<T> {
    private final ModelPart field_3433 = new ModelPart(this);

    public LlamaSpitEntityModel() {
        this(0.0f);
    }

    public LlamaSpitEntityModel(float f) {
        int i = 2;
        this.field_3433.setTextureOffset(0, 0).addCuboid(-4.0f, 0.0f, 0.0f, 2.0f, 2.0f, 2.0f, f);
        this.field_3433.setTextureOffset(0, 0).addCuboid(0.0f, -4.0f, 0.0f, 2.0f, 2.0f, 2.0f, f);
        this.field_3433.setTextureOffset(0, 0).addCuboid(0.0f, 0.0f, -4.0f, 2.0f, 2.0f, 2.0f, f);
        this.field_3433.setTextureOffset(0, 0).addCuboid(0.0f, 0.0f, 0.0f, 2.0f, 2.0f, 2.0f, f);
        this.field_3433.setTextureOffset(0, 0).addCuboid(2.0f, 0.0f, 0.0f, 2.0f, 2.0f, 2.0f, f);
        this.field_3433.setTextureOffset(0, 0).addCuboid(0.0f, 2.0f, 0.0f, 2.0f, 2.0f, 2.0f, f);
        this.field_3433.setTextureOffset(0, 0).addCuboid(0.0f, 0.0f, 2.0f, 2.0f, 2.0f, 2.0f, f);
        this.field_3433.setRotationPoint(0.0f, 0.0f, 0.0f);
    }

    @Override
    public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
    }

    @Override
    public Iterable<ModelPart> method_22960() {
        return ImmutableList.of(this.field_3433);
    }
}

