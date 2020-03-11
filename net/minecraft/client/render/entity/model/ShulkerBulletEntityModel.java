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
    private final ModelPart bullet;

    public ShulkerBulletEntityModel() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.bullet = new ModelPart(this);
        this.bullet.setTextureOffset(0, 0).addCuboid(-4.0f, -4.0f, -1.0f, 8.0f, 8.0f, 2.0f, 0.0f);
        this.bullet.setTextureOffset(0, 10).addCuboid(-1.0f, -4.0f, -4.0f, 2.0f, 8.0f, 8.0f, 0.0f);
        this.bullet.setTextureOffset(20, 0).addCuboid(-4.0f, -1.0f, -4.0f, 8.0f, 2.0f, 8.0f, 0.0f);
        this.bullet.setPivot(0.0f, 0.0f, 0.0f);
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of(this.bullet);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float customAngle, float headYaw, float headPitch) {
        this.bullet.yaw = headYaw * ((float)Math.PI / 180);
        this.bullet.pitch = headPitch * ((float)Math.PI / 180);
    }
}

