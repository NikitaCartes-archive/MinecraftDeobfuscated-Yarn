/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4592;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class QuadrupedEntityModel<T extends Entity>
extends class_4592<T> {
    protected ModelPart head = new ModelPart(this, 0, 0);
    protected ModelPart body;
    protected ModelPart leg1;
    protected ModelPart leg2;
    protected ModelPart leg3;
    protected ModelPart leg4;

    public QuadrupedEntityModel(int i, float f, boolean bl, float g, float h, float j, float k, int l) {
        super(bl, g, h, j, k, l);
        this.head.addCuboid(-4.0f, -4.0f, -8.0f, 8.0f, 8.0f, 8.0f, f);
        this.head.setPivot(0.0f, 18 - i, -6.0f);
        this.body = new ModelPart(this, 28, 8);
        this.body.addCuboid(-5.0f, -10.0f, -7.0f, 10.0f, 16.0f, 8.0f, f);
        this.body.setPivot(0.0f, 17 - i, 2.0f);
        this.leg1 = new ModelPart(this, 0, 16);
        this.leg1.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, (float)i, 4.0f, f);
        this.leg1.setPivot(-3.0f, 24 - i, 7.0f);
        this.leg2 = new ModelPart(this, 0, 16);
        this.leg2.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, (float)i, 4.0f, f);
        this.leg2.setPivot(3.0f, 24 - i, 7.0f);
        this.leg3 = new ModelPart(this, 0, 16);
        this.leg3.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, (float)i, 4.0f, f);
        this.leg3.setPivot(-3.0f, 24 - i, -5.0f);
        this.leg4 = new ModelPart(this, 0, 16);
        this.leg4.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, (float)i, 4.0f, f);
        this.leg4.setPivot(3.0f, 24 - i, -5.0f);
    }

    @Override
    protected Iterable<ModelPart> method_22946() {
        return ImmutableList.of(this.head);
    }

    @Override
    protected Iterable<ModelPart> method_22948() {
        return ImmutableList.of(this.body, this.leg1, this.leg2, this.leg3, this.leg4);
    }

    @Override
    public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
        this.head.pitch = j * ((float)Math.PI / 180);
        this.head.yaw = i * ((float)Math.PI / 180);
        this.body.pitch = 1.5707964f;
        this.leg1.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g;
        this.leg2.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
        this.leg3.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
        this.leg4.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g;
    }
}

