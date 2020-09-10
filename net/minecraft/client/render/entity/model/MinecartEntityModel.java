/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.CompositeEntityModel;
import net.minecraft.entity.Entity;

@Environment(value=EnvType.CLIENT)
public class MinecartEntityModel<T extends Entity>
extends CompositeEntityModel<T> {
    private final ModelPart[] parts = new ModelPart[6];

    public MinecartEntityModel() {
        this.parts[0] = new ModelPart(this, 0, 10);
        this.parts[1] = new ModelPart(this, 0, 0);
        this.parts[2] = new ModelPart(this, 0, 0);
        this.parts[3] = new ModelPart(this, 0, 0);
        this.parts[4] = new ModelPart(this, 0, 0);
        this.parts[5] = new ModelPart(this, 44, 10);
        int i = 20;
        int j = 8;
        int k = 16;
        int l = 4;
        this.parts[0].addCuboid(-10.0f, -8.0f, -1.0f, 20.0f, 16.0f, 2.0f, 0.0f);
        this.parts[0].setPivot(0.0f, 4.0f, 0.0f);
        this.parts[5].addCuboid(-9.0f, -7.0f, -1.0f, 18.0f, 14.0f, 1.0f, 0.0f);
        this.parts[5].setPivot(0.0f, 4.0f, 0.0f);
        this.parts[1].addCuboid(-8.0f, -9.0f, -1.0f, 16.0f, 8.0f, 2.0f, 0.0f);
        this.parts[1].setPivot(-9.0f, 4.0f, 0.0f);
        this.parts[2].addCuboid(-8.0f, -9.0f, -1.0f, 16.0f, 8.0f, 2.0f, 0.0f);
        this.parts[2].setPivot(9.0f, 4.0f, 0.0f);
        this.parts[3].addCuboid(-8.0f, -9.0f, -1.0f, 16.0f, 8.0f, 2.0f, 0.0f);
        this.parts[3].setPivot(0.0f, 4.0f, -7.0f);
        this.parts[4].addCuboid(-8.0f, -9.0f, -1.0f, 16.0f, 8.0f, 2.0f, 0.0f);
        this.parts[4].setPivot(0.0f, 4.0f, 7.0f);
        this.parts[0].pitch = 1.5707964f;
        this.parts[1].yaw = 4.712389f;
        this.parts[2].yaw = 1.5707964f;
        this.parts[3].yaw = (float)Math.PI;
        this.parts[5].pitch = -1.5707964f;
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.parts[5].pivotY = 4.0f - animationProgress;
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return Arrays.asList(this.parts);
    }
}

