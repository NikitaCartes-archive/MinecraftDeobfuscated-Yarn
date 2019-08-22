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
public class ShulkerBulletEntityModel<T extends Entity>
extends EntityModel<T> {
    private final ModelPart field_3556;

    public ShulkerBulletEntityModel() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.field_3556 = new ModelPart(this);
        this.field_3556.setTextureOffset(0, 0).addCuboid(-4.0f, -4.0f, -1.0f, 8, 8, 2, 0.0f);
        this.field_3556.setTextureOffset(0, 10).addCuboid(-1.0f, -4.0f, -4.0f, 2, 8, 8, 0.0f);
        this.field_3556.setTextureOffset(20, 0).addCuboid(-4.0f, -1.0f, -4.0f, 8, 2, 8, 0.0f);
        this.field_3556.setRotationPoint(0.0f, 0.0f, 0.0f);
    }

    @Override
    public void render(T entity, float f, float g, float h, float i, float j, float k) {
        this.setAngles(entity, f, g, h, i, j, k);
        this.field_3556.render(k);
    }

    @Override
    public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
        super.setAngles(entity, f, g, h, i, j, k);
        this.field_3556.yaw = i * ((float)Math.PI / 180);
        this.field_3556.pitch = j * ((float)Math.PI / 180);
    }
}

