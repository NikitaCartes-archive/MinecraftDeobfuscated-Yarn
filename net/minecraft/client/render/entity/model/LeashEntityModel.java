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
public class LeashEntityModel<T extends Entity>
extends EntityModel<T> {
    private final ModelPart field_3431;

    public LeashEntityModel() {
        this(0, 0, 32, 32);
    }

    public LeashEntityModel(int i, int j, int k, int l) {
        this.textureWidth = k;
        this.textureHeight = l;
        this.field_3431 = new ModelPart(this, i, j);
        this.field_3431.addCuboid(-3.0f, -6.0f, -3.0f, 6, 8, 6, 0.0f);
        this.field_3431.setPivot(0.0f, 0.0f, 0.0f);
    }

    @Override
    public void render(T entity, float f, float g, float h, float i, float j, float k) {
        this.setAngles(entity, f, g, h, i, j, k);
        this.field_3431.render(k);
    }

    @Override
    public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
        super.setAngles(entity, f, g, h, i, j, k);
        this.field_3431.yaw = i * ((float)Math.PI / 180);
        this.field_3431.pitch = j * ((float)Math.PI / 180);
    }
}

