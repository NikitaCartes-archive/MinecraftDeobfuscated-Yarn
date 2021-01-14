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
public class LeashKnotEntityModel<T extends Entity>
extends CompositeEntityModel<T> {
    private final ModelPart knot;

    public LeashKnotEntityModel() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        this.knot = new ModelPart(this, 0, 0);
        this.knot.addCuboid(-3.0f, -6.0f, -3.0f, 6.0f, 8.0f, 6.0f, 0.0f);
        this.knot.setPivot(0.0f, 0.0f, 0.0f);
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of(this.knot);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.knot.yaw = headYaw * ((float)Math.PI / 180);
        this.knot.pitch = headPitch * ((float)Math.PI / 180);
    }
}

