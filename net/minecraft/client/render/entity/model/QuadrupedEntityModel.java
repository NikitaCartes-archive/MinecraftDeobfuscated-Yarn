/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class QuadrupedEntityModel<T extends Entity>
extends AnimalModel<T> {
    protected ModelPart head = new ModelPart(this, 0, 0);
    protected ModelPart torso;
    protected ModelPart backRightLeg;
    protected ModelPart backLeftLeg;
    protected ModelPart frontRightLeg;
    protected ModelPart frontLeftLeg;

    public QuadrupedEntityModel(int i, float f, boolean bl, float g, float h, float j, float k, int l) {
        super(bl, g, h, j, k, l);
        this.head.addCuboid(-4.0f, -4.0f, -8.0f, 8.0f, 8.0f, 8.0f, f);
        this.head.setPivot(0.0f, 18 - i, -6.0f);
        this.torso = new ModelPart(this, 28, 8);
        this.torso.addCuboid(-5.0f, -10.0f, -7.0f, 10.0f, 16.0f, 8.0f, f);
        this.torso.setPivot(0.0f, 17 - i, 2.0f);
        this.backRightLeg = new ModelPart(this, 0, 16);
        this.backRightLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, (float)i, 4.0f, f);
        this.backRightLeg.setPivot(-3.0f, 24 - i, 7.0f);
        this.backLeftLeg = new ModelPart(this, 0, 16);
        this.backLeftLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, (float)i, 4.0f, f);
        this.backLeftLeg.setPivot(3.0f, 24 - i, 7.0f);
        this.frontRightLeg = new ModelPart(this, 0, 16);
        this.frontRightLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, (float)i, 4.0f, f);
        this.frontRightLeg.setPivot(-3.0f, 24 - i, -5.0f);
        this.frontLeftLeg = new ModelPart(this, 0, 16);
        this.frontLeftLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, (float)i, 4.0f, f);
        this.frontLeftLeg.setPivot(3.0f, 24 - i, -5.0f);
    }

    @Override
    protected Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of(this.head);
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of(this.torso, this.backRightLeg, this.backLeftLeg, this.frontRightLeg, this.frontLeftLeg);
    }

    @Override
    public void setAngles(T entity, float f, float g, float h, float i, float j) {
        this.head.pitch = j * ((float)Math.PI / 180);
        this.head.yaw = i * ((float)Math.PI / 180);
        this.torso.pitch = 1.5707964f;
        this.backRightLeg.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g;
        this.backLeftLeg.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
        this.frontRightLeg.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
        this.frontLeftLeg.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g;
    }
}

