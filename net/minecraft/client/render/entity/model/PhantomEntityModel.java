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
public class PhantomEntityModel<T extends Entity>
extends CompositeEntityModel<T> {
    private final ModelPart head;
    private final ModelPart field_3477;
    private final ModelPart field_3476;
    private final ModelPart field_3474;
    private final ModelPart field_3472;
    private final ModelPart field_3471;
    private final ModelPart field_3473;

    public PhantomEntityModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.head = new ModelPart(this, 0, 8);
        this.head.addCuboid(-3.0f, -2.0f, -8.0f, 5.0f, 3.0f, 9.0f);
        this.field_3471 = new ModelPart(this, 3, 20);
        this.field_3471.addCuboid(-2.0f, 0.0f, 0.0f, 3.0f, 2.0f, 6.0f);
        this.field_3471.setPivot(0.0f, -2.0f, 1.0f);
        this.head.addChild(this.field_3471);
        this.field_3473 = new ModelPart(this, 4, 29);
        this.field_3473.addCuboid(-1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 6.0f);
        this.field_3473.setPivot(0.0f, 0.5f, 6.0f);
        this.field_3471.addChild(this.field_3473);
        this.field_3477 = new ModelPart(this, 23, 12);
        this.field_3477.addCuboid(0.0f, 0.0f, 0.0f, 6.0f, 2.0f, 9.0f);
        this.field_3477.setPivot(2.0f, -2.0f, -8.0f);
        this.field_3476 = new ModelPart(this, 16, 24);
        this.field_3476.addCuboid(0.0f, 0.0f, 0.0f, 13.0f, 1.0f, 9.0f);
        this.field_3476.setPivot(6.0f, 0.0f, 0.0f);
        this.field_3477.addChild(this.field_3476);
        this.field_3474 = new ModelPart(this, 23, 12);
        this.field_3474.mirror = true;
        this.field_3474.addCuboid(-6.0f, 0.0f, 0.0f, 6.0f, 2.0f, 9.0f);
        this.field_3474.setPivot(-3.0f, -2.0f, -8.0f);
        this.field_3472 = new ModelPart(this, 16, 24);
        this.field_3472.mirror = true;
        this.field_3472.addCuboid(-13.0f, 0.0f, 0.0f, 13.0f, 1.0f, 9.0f);
        this.field_3472.setPivot(-6.0f, 0.0f, 0.0f);
        this.field_3474.addChild(this.field_3472);
        this.field_3477.roll = 0.1f;
        this.field_3476.roll = 0.1f;
        this.field_3474.roll = -0.1f;
        this.field_3472.roll = -0.1f;
        this.head.pitch = -0.1f;
        ModelPart modelPart = new ModelPart(this, 0, 0);
        modelPart.addCuboid(-4.0f, -2.0f, -5.0f, 7.0f, 3.0f, 5.0f);
        modelPart.setPivot(0.0f, 1.0f, -7.0f);
        modelPart.pitch = 0.2f;
        this.head.addChild(modelPart);
        this.head.addChild(this.field_3477);
        this.head.addChild(this.field_3474);
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of(this.head);
    }

    @Override
    public void setAngles(T entity, float f, float g, float h, float i, float j) {
        float k = ((float)(((Entity)entity).getEntityId() * 3) + h) * 0.13f;
        float l = 16.0f;
        this.field_3477.roll = MathHelper.cos(k) * 16.0f * ((float)Math.PI / 180);
        this.field_3476.roll = MathHelper.cos(k) * 16.0f * ((float)Math.PI / 180);
        this.field_3474.roll = -this.field_3477.roll;
        this.field_3472.roll = -this.field_3476.roll;
        this.field_3471.pitch = -(5.0f + MathHelper.cos(k * 2.0f) * 5.0f) * ((float)Math.PI / 180);
        this.field_3473.pitch = -(5.0f + MathHelper.cos(k * 2.0f) * 5.0f) * ((float)Math.PI / 180);
    }
}

