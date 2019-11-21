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
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class DolphinEntityModel<T extends Entity>
extends CompositeEntityModel<T> {
    private final ModelPart body;
    private final ModelPart tail;
    private final ModelPart flukes;

    public DolphinEntityModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        float f = 18.0f;
        float g = -8.0f;
        this.body = new ModelPart(this, 22, 0);
        this.body.addCuboid(-4.0f, -7.0f, 0.0f, 8.0f, 7.0f, 13.0f);
        this.body.setPivot(0.0f, 22.0f, -5.0f);
        ModelPart modelPart = new ModelPart(this, 51, 0);
        modelPart.addCuboid(-0.5f, 0.0f, 8.0f, 1.0f, 4.0f, 5.0f);
        modelPart.pitch = 1.0471976f;
        this.body.addChild(modelPart);
        ModelPart modelPart2 = new ModelPart(this, 48, 20);
        modelPart2.mirror = true;
        modelPart2.addCuboid(-0.5f, -4.0f, 0.0f, 1.0f, 4.0f, 7.0f);
        modelPart2.setPivot(2.0f, -2.0f, 4.0f);
        modelPart2.pitch = 1.0471976f;
        modelPart2.roll = 2.0943952f;
        this.body.addChild(modelPart2);
        ModelPart modelPart3 = new ModelPart(this, 48, 20);
        modelPart3.addCuboid(-0.5f, -4.0f, 0.0f, 1.0f, 4.0f, 7.0f);
        modelPart3.setPivot(-2.0f, -2.0f, 4.0f);
        modelPart3.pitch = 1.0471976f;
        modelPart3.roll = -2.0943952f;
        this.body.addChild(modelPart3);
        this.tail = new ModelPart(this, 0, 19);
        this.tail.addCuboid(-2.0f, -2.5f, 0.0f, 4.0f, 5.0f, 11.0f);
        this.tail.setPivot(0.0f, -2.5f, 11.0f);
        this.tail.pitch = -0.10471976f;
        this.body.addChild(this.tail);
        this.flukes = new ModelPart(this, 19, 20);
        this.flukes.addCuboid(-5.0f, -0.5f, 0.0f, 10.0f, 1.0f, 6.0f);
        this.flukes.setPivot(0.0f, 0.0f, 9.0f);
        this.flukes.pitch = 0.0f;
        this.tail.addChild(this.flukes);
        ModelPart modelPart4 = new ModelPart(this, 0, 0);
        modelPart4.addCuboid(-4.0f, -3.0f, -3.0f, 8.0f, 7.0f, 6.0f);
        modelPart4.setPivot(0.0f, -4.0f, -3.0f);
        ModelPart modelPart5 = new ModelPart(this, 0, 13);
        modelPart5.addCuboid(-1.0f, 2.0f, -7.0f, 2.0f, 2.0f, 4.0f);
        modelPart4.addChild(modelPart5);
        this.body.addChild(modelPart4);
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of(this.body);
    }

    @Override
    public void setAngles(T entity, float f, float g, float h, float i, float j) {
        this.body.pitch = j * ((float)Math.PI / 180);
        this.body.yaw = i * ((float)Math.PI / 180);
        if (Entity.squaredHorizontalLength(((Entity)entity).getVelocity()) > 1.0E-7) {
            this.body.pitch += -0.05f + -0.05f * MathHelper.cos(h * 0.3f);
            this.tail.pitch = -0.1f * MathHelper.cos(h * 0.3f);
            this.flukes.pitch = -0.2f * MathHelper.cos(h * 0.3f);
        }
    }
}

