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
public class LlamaSpitEntityModel<T extends Entity>
extends CompositeEntityModel<T> {
    private final ModelPart spit = new ModelPart(this);

    public LlamaSpitEntityModel() {
        this(0.0f);
    }

    public LlamaSpitEntityModel(float scale) {
        int i = 2;
        this.spit.setTextureOffset(0, 0).addCuboid(-4.0f, 0.0f, 0.0f, 2.0f, 2.0f, 2.0f, scale);
        this.spit.setTextureOffset(0, 0).addCuboid(0.0f, -4.0f, 0.0f, 2.0f, 2.0f, 2.0f, scale);
        this.spit.setTextureOffset(0, 0).addCuboid(0.0f, 0.0f, -4.0f, 2.0f, 2.0f, 2.0f, scale);
        this.spit.setTextureOffset(0, 0).addCuboid(0.0f, 0.0f, 0.0f, 2.0f, 2.0f, 2.0f, scale);
        this.spit.setTextureOffset(0, 0).addCuboid(2.0f, 0.0f, 0.0f, 2.0f, 2.0f, 2.0f, scale);
        this.spit.setTextureOffset(0, 0).addCuboid(0.0f, 2.0f, 0.0f, 2.0f, 2.0f, 2.0f, scale);
        this.spit.setTextureOffset(0, 0).addCuboid(0.0f, 0.0f, 2.0f, 2.0f, 2.0f, 2.0f, scale);
        this.spit.setPivot(0.0f, 0.0f, 0.0f);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float customAngle, float headYaw, float headPitch) {
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of(this.spit);
    }
}

