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
    private final ModelPart helmet;
    private final ModelPart torso;
    private final ModelPart rightBackLeg;
    private final ModelPart leftBackLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;

    public CreeperEntityModel() {
        this(0.0f);
    }

    public CreeperEntityModel(float scale) {
        int i = 6;
        this.head = new ModelPart(this, 0, 0);
        this.head.addCuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, scale);
        this.head.setPivot(0.0f, 6.0f, 0.0f);
        this.helmet = new ModelPart(this, 32, 0);
        this.helmet.addCuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, scale + 0.5f);
        this.helmet.setPivot(0.0f, 6.0f, 0.0f);
        this.torso = new ModelPart(this, 16, 16);
        this.torso.addCuboid(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, scale);
        this.torso.setPivot(0.0f, 6.0f, 0.0f);
        this.rightBackLeg = new ModelPart(this, 0, 16);
        this.rightBackLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 6.0f, 4.0f, scale);
        this.rightBackLeg.setPivot(-2.0f, 18.0f, 4.0f);
        this.leftBackLeg = new ModelPart(this, 0, 16);
        this.leftBackLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 6.0f, 4.0f, scale);
        this.leftBackLeg.setPivot(2.0f, 18.0f, 4.0f);
        this.rightFrontLeg = new ModelPart(this, 0, 16);
        this.rightFrontLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 6.0f, 4.0f, scale);
        this.rightFrontLeg.setPivot(-2.0f, 18.0f, -4.0f);
        this.leftFrontLeg = new ModelPart(this, 0, 16);
        this.leftFrontLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 6.0f, 4.0f, scale);
        this.leftFrontLeg.setPivot(2.0f, 18.0f, -4.0f);
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of(this.head, this.torso, this.rightBackLeg, this.leftBackLeg, this.rightFrontLeg, this.leftFrontLeg);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.head.yaw = headYaw * ((float)Math.PI / 180);
        this.head.pitch = headPitch * ((float)Math.PI / 180);
        this.rightBackLeg.pitch = MathHelper.cos(limbAngle * 0.6662f) * 1.4f * limbDistance;
        this.leftBackLeg.pitch = MathHelper.cos(limbAngle * 0.6662f + (float)Math.PI) * 1.4f * limbDistance;
        this.rightFrontLeg.pitch = MathHelper.cos(limbAngle * 0.6662f + (float)Math.PI) * 1.4f * limbDistance;
        this.leftFrontLeg.pitch = MathHelper.cos(limbAngle * 0.6662f) * 1.4f * limbDistance;
    }
}

