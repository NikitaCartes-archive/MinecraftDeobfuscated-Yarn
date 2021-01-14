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
    private final ModelPart body;
    private final ModelPart leftWingBase;
    private final ModelPart leftWingTip;
    private final ModelPart rightWingBase;
    private final ModelPart rightWingTip;
    private final ModelPart tailBase;
    private final ModelPart tailTip;

    public PhantomEntityModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.body = new ModelPart(this, 0, 8);
        this.body.addCuboid(-3.0f, -2.0f, -8.0f, 5.0f, 3.0f, 9.0f);
        this.tailBase = new ModelPart(this, 3, 20);
        this.tailBase.addCuboid(-2.0f, 0.0f, 0.0f, 3.0f, 2.0f, 6.0f);
        this.tailBase.setPivot(0.0f, -2.0f, 1.0f);
        this.body.addChild(this.tailBase);
        this.tailTip = new ModelPart(this, 4, 29);
        this.tailTip.addCuboid(-1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 6.0f);
        this.tailTip.setPivot(0.0f, 0.5f, 6.0f);
        this.tailBase.addChild(this.tailTip);
        this.leftWingBase = new ModelPart(this, 23, 12);
        this.leftWingBase.addCuboid(0.0f, 0.0f, 0.0f, 6.0f, 2.0f, 9.0f);
        this.leftWingBase.setPivot(2.0f, -2.0f, -8.0f);
        this.leftWingTip = new ModelPart(this, 16, 24);
        this.leftWingTip.addCuboid(0.0f, 0.0f, 0.0f, 13.0f, 1.0f, 9.0f);
        this.leftWingTip.setPivot(6.0f, 0.0f, 0.0f);
        this.leftWingBase.addChild(this.leftWingTip);
        this.rightWingBase = new ModelPart(this, 23, 12);
        this.rightWingBase.mirror = true;
        this.rightWingBase.addCuboid(-6.0f, 0.0f, 0.0f, 6.0f, 2.0f, 9.0f);
        this.rightWingBase.setPivot(-3.0f, -2.0f, -8.0f);
        this.rightWingTip = new ModelPart(this, 16, 24);
        this.rightWingTip.mirror = true;
        this.rightWingTip.addCuboid(-13.0f, 0.0f, 0.0f, 13.0f, 1.0f, 9.0f);
        this.rightWingTip.setPivot(-6.0f, 0.0f, 0.0f);
        this.rightWingBase.addChild(this.rightWingTip);
        this.leftWingBase.roll = 0.1f;
        this.leftWingTip.roll = 0.1f;
        this.rightWingBase.roll = -0.1f;
        this.rightWingTip.roll = -0.1f;
        this.body.pitch = -0.1f;
        ModelPart modelPart = new ModelPart(this, 0, 0);
        modelPart.addCuboid(-4.0f, -2.0f, -5.0f, 7.0f, 3.0f, 5.0f);
        modelPart.setPivot(0.0f, 1.0f, -7.0f);
        modelPart.pitch = 0.2f;
        this.body.addChild(modelPart);
        this.body.addChild(this.leftWingBase);
        this.body.addChild(this.rightWingBase);
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of(this.body);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        float f = ((float)(((Entity)entity).getEntityId() * 3) + animationProgress) * 0.13f;
        float g = 16.0f;
        this.leftWingBase.roll = MathHelper.cos(f) * 16.0f * ((float)Math.PI / 180);
        this.leftWingTip.roll = MathHelper.cos(f) * 16.0f * ((float)Math.PI / 180);
        this.rightWingBase.roll = -this.leftWingBase.roll;
        this.rightWingTip.roll = -this.leftWingTip.roll;
        this.tailBase.pitch = -(5.0f + MathHelper.cos(f * 2.0f) * 5.0f) * ((float)Math.PI / 180);
        this.tailTip.pitch = -(5.0f + MathHelper.cos(f * 2.0f) * 5.0f) * ((float)Math.PI / 180);
    }
}

