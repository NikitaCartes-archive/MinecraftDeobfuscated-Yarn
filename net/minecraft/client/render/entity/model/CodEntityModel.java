/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class CodEntityModel<T extends Entity>
extends EntityModel<T> {
    private final ModelPart body;
    private final ModelPart topFin;
    private final ModelPart head;
    private final ModelPart mouth;
    private final ModelPart leftFint;
    private final ModelPart rightFin;
    private final ModelPart tailFin;

    public CodEntityModel() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        int i = 22;
        this.body = new ModelPart(this, 0, 0);
        this.body.addCuboid(-1.0f, -2.0f, 0.0f, 2.0f, 4.0f, 7.0f);
        this.body.setRotationPoint(0.0f, 22.0f, 0.0f);
        this.head = new ModelPart(this, 11, 0);
        this.head.addCuboid(-1.0f, -2.0f, -3.0f, 2.0f, 4.0f, 3.0f);
        this.head.setRotationPoint(0.0f, 22.0f, 0.0f);
        this.mouth = new ModelPart(this, 0, 0);
        this.mouth.addCuboid(-1.0f, -2.0f, -1.0f, 2.0f, 3.0f, 1.0f);
        this.mouth.setRotationPoint(0.0f, 22.0f, -3.0f);
        this.leftFint = new ModelPart(this, 22, 1);
        this.leftFint.addCuboid(-2.0f, 0.0f, -1.0f, 2.0f, 0.0f, 2.0f);
        this.leftFint.setRotationPoint(-1.0f, 23.0f, 0.0f);
        this.leftFint.roll = -0.7853982f;
        this.rightFin = new ModelPart(this, 22, 4);
        this.rightFin.addCuboid(0.0f, 0.0f, -1.0f, 2.0f, 0.0f, 2.0f);
        this.rightFin.setRotationPoint(1.0f, 23.0f, 0.0f);
        this.rightFin.roll = 0.7853982f;
        this.tailFin = new ModelPart(this, 22, 3);
        this.tailFin.addCuboid(0.0f, -2.0f, 0.0f, 0.0f, 4.0f, 4.0f);
        this.tailFin.setRotationPoint(0.0f, 22.0f, 7.0f);
        this.topFin = new ModelPart(this, 20, -6);
        this.topFin.addCuboid(0.0f, -1.0f, -1.0f, 0.0f, 1.0f, 6.0f);
        this.topFin.setRotationPoint(0.0f, 20.0f, 0.0f);
    }

    @Override
    public void render(T entity, float f, float g, float h, float i, float j, float k) {
        this.setAngles(entity, f, g, h, i, j, k);
        this.body.render(k);
        this.head.render(k);
        this.mouth.render(k);
        this.leftFint.render(k);
        this.rightFin.render(k);
        this.tailFin.render(k);
        this.topFin.render(k);
    }

    @Override
    public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
        float l = 1.0f;
        if (!((Entity)entity).isInsideWater()) {
            l = 1.5f;
        }
        this.tailFin.yaw = -l * 0.45f * MathHelper.sin(0.6f * h);
    }
}

