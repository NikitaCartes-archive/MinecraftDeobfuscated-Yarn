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
public class CreeperEntityModel<T extends Entity>
extends CompositeEntityModel<T> {
    private final ModelPart head;
    private final ModelPart headHat;
    private final ModelPart body;
    private final ModelPart leg1;
    private final ModelPart leg2;
    private final ModelPart leg3;
    private final ModelPart leg4;

    public CreeperEntityModel() {
        this(0.0f);
    }

    public CreeperEntityModel(float f) {
        int i = 6;
        this.head = new ModelPart(this, 0, 0);
        this.head.addCuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, f);
        this.head.setPivot(0.0f, 6.0f, 0.0f);
        this.headHat = new ModelPart(this, 32, 0);
        this.headHat.addCuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, f + 0.5f);
        this.headHat.setPivot(0.0f, 6.0f, 0.0f);
        this.body = new ModelPart(this, 16, 16);
        this.body.addCuboid(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, f);
        this.body.setPivot(0.0f, 6.0f, 0.0f);
        this.leg1 = new ModelPart(this, 0, 16);
        this.leg1.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 6.0f, 4.0f, f);
        this.leg1.setPivot(-2.0f, 18.0f, 4.0f);
        this.leg2 = new ModelPart(this, 0, 16);
        this.leg2.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 6.0f, 4.0f, f);
        this.leg2.setPivot(2.0f, 18.0f, 4.0f);
        this.leg3 = new ModelPart(this, 0, 16);
        this.leg3.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 6.0f, 4.0f, f);
        this.leg3.setPivot(-2.0f, 18.0f, -4.0f);
        this.leg4 = new ModelPart(this, 0, 16);
        this.leg4.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 6.0f, 4.0f, f);
        this.leg4.setPivot(2.0f, 18.0f, -4.0f);
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of(this.head, this.body, this.leg1, this.leg2, this.leg3, this.leg4);
    }

    @Override
    public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
        this.head.yaw = i * ((float)Math.PI / 180);
        this.head.pitch = j * ((float)Math.PI / 180);
        this.leg1.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g;
        this.leg2.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
        this.leg3.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
        this.leg4.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g;
    }
}

