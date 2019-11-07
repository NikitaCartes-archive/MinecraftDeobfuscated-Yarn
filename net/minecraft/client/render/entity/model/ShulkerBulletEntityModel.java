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

@Environment(value=EnvType.CLIENT)
public class ShulkerBulletEntityModel<T extends Entity>
extends CompositeEntityModel<T> {
    private final ModelPart field_3556;

    public ShulkerBulletEntityModel() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.field_3556 = new ModelPart(this);
        this.field_3556.setTextureOffset(0, 0).addCuboid(-4.0f, -4.0f, -1.0f, 8.0f, 8.0f, 2.0f, 0.0f);
        this.field_3556.setTextureOffset(0, 10).addCuboid(-1.0f, -4.0f, -4.0f, 2.0f, 8.0f, 8.0f, 0.0f);
        this.field_3556.setTextureOffset(20, 0).addCuboid(-4.0f, -1.0f, -4.0f, 8.0f, 2.0f, 8.0f, 0.0f);
        this.field_3556.setPivot(0.0f, 0.0f, 0.0f);
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of(this.field_3556);
    }

    @Override
    public void setAngles(T entity, float f, float g, float h, float i, float j) {
        this.field_3556.yaw = i * ((float)Math.PI / 180);
        this.field_3556.pitch = j * ((float)Math.PI / 180);
    }
}

