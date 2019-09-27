/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4595;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;

@Environment(value=EnvType.CLIENT)
public class MinecartEntityModel<T extends Entity>
extends class_4595<T> {
    private final ModelPart[] field_3432 = new ModelPart[6];

    public MinecartEntityModel() {
        this.field_3432[0] = new ModelPart(this, 0, 10);
        this.field_3432[1] = new ModelPart(this, 0, 0);
        this.field_3432[2] = new ModelPart(this, 0, 0);
        this.field_3432[3] = new ModelPart(this, 0, 0);
        this.field_3432[4] = new ModelPart(this, 0, 0);
        this.field_3432[5] = new ModelPart(this, 44, 10);
        int i = 20;
        int j = 8;
        int k = 16;
        int l = 4;
        this.field_3432[0].addCuboid(-10.0f, -8.0f, -1.0f, 20.0f, 16.0f, 2.0f, 0.0f);
        this.field_3432[0].setRotationPoint(0.0f, 4.0f, 0.0f);
        this.field_3432[5].addCuboid(-9.0f, -7.0f, -1.0f, 18.0f, 14.0f, 1.0f, 0.0f);
        this.field_3432[5].setRotationPoint(0.0f, 4.0f, 0.0f);
        this.field_3432[1].addCuboid(-8.0f, -9.0f, -1.0f, 16.0f, 8.0f, 2.0f, 0.0f);
        this.field_3432[1].setRotationPoint(-9.0f, 4.0f, 0.0f);
        this.field_3432[2].addCuboid(-8.0f, -9.0f, -1.0f, 16.0f, 8.0f, 2.0f, 0.0f);
        this.field_3432[2].setRotationPoint(9.0f, 4.0f, 0.0f);
        this.field_3432[3].addCuboid(-8.0f, -9.0f, -1.0f, 16.0f, 8.0f, 2.0f, 0.0f);
        this.field_3432[3].setRotationPoint(0.0f, 4.0f, -7.0f);
        this.field_3432[4].addCuboid(-8.0f, -9.0f, -1.0f, 16.0f, 8.0f, 2.0f, 0.0f);
        this.field_3432[4].setRotationPoint(0.0f, 4.0f, 7.0f);
        this.field_3432[0].pitch = 1.5707964f;
        this.field_3432[1].yaw = 4.712389f;
        this.field_3432[2].yaw = 1.5707964f;
        this.field_3432[3].yaw = (float)Math.PI;
        this.field_3432[5].pitch = -1.5707964f;
    }

    @Override
    public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
        this.field_3432[5].rotationPointY = 4.0f - h;
    }

    @Override
    public Iterable<ModelPart> method_22960() {
        return Arrays.asList(this.field_3432);
    }
}

