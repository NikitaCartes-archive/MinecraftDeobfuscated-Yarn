/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;

@Environment(value=EnvType.CLIENT)
public class LlamaSpitEntityModel<T extends Entity>
extends EntityModel<T> {
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
    public void render(T entity, float f, float g, float h, float i, float j, float k) {
        this.setAngles(entity, f, g, h, i, j, k);
        this.field_3433.render(k);
    }
}

