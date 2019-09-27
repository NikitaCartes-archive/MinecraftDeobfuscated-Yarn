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
public class LeashEntityModel<T extends Entity>
extends class_4595<T> {
    private final ModelPart field_3431;

    public LeashEntityModel() {
        this(0, 0, 32, 32);
    }

    public LeashEntityModel(int i, int j, int k, int l) {
        this.textureWidth = k;
        this.textureHeight = l;
        this.field_3431 = new ModelPart(this, i, j);
        this.field_3431.addCuboid(-3.0f, -6.0f, -3.0f, 6.0f, 8.0f, 6.0f, 0.0f);
        this.field_3431.setRotationPoint(0.0f, 0.0f, 0.0f);
    }

    @Override
    public Iterable<ModelPart> method_22960() {
        return ImmutableList.of(this.field_3431);
    }

    @Override
    public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
        this.field_3431.yaw = i * ((float)Math.PI / 180);
        this.field_3431.pitch = j * ((float)Math.PI / 180);
    }
}

