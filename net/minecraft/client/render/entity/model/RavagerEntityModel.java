/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.CompositeEntityModel;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class RavagerEntityModel
extends CompositeEntityModel<RavagerEntity> {
    private final ModelPart field_3386;
    private final ModelPart jaw;
    private final ModelPart torso;
    private final ModelPart rightBackLeg;
    private final ModelPart leftBackLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart field_3384;

    public RavagerEntityModel() {
        this.textureWidth = 128;
        this.textureHeight = 128;
        int i = 16;
        float f = 0.0f;
        this.field_3384 = new ModelPart(this);
        this.field_3384.setPivot(0.0f, -7.0f, -1.5f);
        this.field_3384.setTextureOffset(68, 73).addCuboid(-5.0f, -1.0f, -18.0f, 10.0f, 10.0f, 18.0f, 0.0f);
        this.field_3386 = new ModelPart(this);
        this.field_3386.setPivot(0.0f, 16.0f, -17.0f);
        this.field_3386.setTextureOffset(0, 0).addCuboid(-8.0f, -20.0f, -14.0f, 16.0f, 20.0f, 16.0f, 0.0f);
        this.field_3386.setTextureOffset(0, 0).addCuboid(-2.0f, -6.0f, -18.0f, 4.0f, 8.0f, 4.0f, 0.0f);
        ModelPart modelPart = new ModelPart(this);
        modelPart.setPivot(-10.0f, -14.0f, -8.0f);
        modelPart.setTextureOffset(74, 55).addCuboid(0.0f, -14.0f, -2.0f, 2.0f, 14.0f, 4.0f, 0.0f);
        modelPart.pitch = 1.0995574f;
        this.field_3386.addChild(modelPart);
        ModelPart modelPart2 = new ModelPart(this);
        modelPart2.mirror = true;
        modelPart2.setPivot(8.0f, -14.0f, -8.0f);
        modelPart2.setTextureOffset(74, 55).addCuboid(0.0f, -14.0f, -2.0f, 2.0f, 14.0f, 4.0f, 0.0f);
        modelPart2.pitch = 1.0995574f;
        this.field_3386.addChild(modelPart2);
        this.jaw = new ModelPart(this);
        this.jaw.setPivot(0.0f, -2.0f, 2.0f);
        this.jaw.setTextureOffset(0, 36).addCuboid(-8.0f, 0.0f, -16.0f, 16.0f, 3.0f, 16.0f, 0.0f);
        this.field_3386.addChild(this.jaw);
        this.field_3384.addChild(this.field_3386);
        this.torso = new ModelPart(this);
        this.torso.setTextureOffset(0, 55).addCuboid(-7.0f, -10.0f, -7.0f, 14.0f, 16.0f, 20.0f, 0.0f);
        this.torso.setTextureOffset(0, 91).addCuboid(-6.0f, 6.0f, -7.0f, 12.0f, 13.0f, 18.0f, 0.0f);
        this.torso.setPivot(0.0f, 1.0f, 2.0f);
        this.rightBackLeg = new ModelPart(this, 96, 0);
        this.rightBackLeg.addCuboid(-4.0f, 0.0f, -4.0f, 8.0f, 37.0f, 8.0f, 0.0f);
        this.rightBackLeg.setPivot(-8.0f, -13.0f, 18.0f);
        this.leftBackLeg = new ModelPart(this, 96, 0);
        this.leftBackLeg.mirror = true;
        this.leftBackLeg.addCuboid(-4.0f, 0.0f, -4.0f, 8.0f, 37.0f, 8.0f, 0.0f);
        this.leftBackLeg.setPivot(8.0f, -13.0f, 18.0f);
        this.rightFrontLeg = new ModelPart(this, 64, 0);
        this.rightFrontLeg.addCuboid(-4.0f, 0.0f, -4.0f, 8.0f, 37.0f, 8.0f, 0.0f);
        this.rightFrontLeg.setPivot(-8.0f, -13.0f, -5.0f);
        this.leftFrontLeg = new ModelPart(this, 64, 0);
        this.leftFrontLeg.mirror = true;
        this.leftFrontLeg.addCuboid(-4.0f, 0.0f, -4.0f, 8.0f, 37.0f, 8.0f, 0.0f);
        this.leftFrontLeg.setPivot(8.0f, -13.0f, -5.0f);
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of(this.field_3384, this.torso, this.rightBackLeg, this.leftBackLeg, this.rightFrontLeg, this.leftFrontLeg);
    }

    @Override
    public void setAngles(RavagerEntity ravagerEntity, float f, float g, float h, float i, float j) {
        this.field_3386.pitch = j * ((float)Math.PI / 180);
        this.field_3386.yaw = i * ((float)Math.PI / 180);
        this.torso.pitch = 1.5707964f;
        float k = 0.4f * g;
        this.rightBackLeg.pitch = MathHelper.cos(f * 0.6662f) * k;
        this.leftBackLeg.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * k;
        this.rightFrontLeg.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * k;
        this.leftFrontLeg.pitch = MathHelper.cos(f * 0.6662f) * k;
    }

    @Override
    public void animateModel(RavagerEntity ravagerEntity, float f, float g, float h) {
        super.animateModel(ravagerEntity, f, g, h);
        int i = ravagerEntity.getStunTick();
        int j = ravagerEntity.getRoarTick();
        int k = 20;
        int l = ravagerEntity.getAttackTick();
        int m = 10;
        if (l > 0) {
            float n = this.method_2801((float)l - h, 10.0f);
            float o = (1.0f + n) * 0.5f;
            float p = o * o * o * 12.0f;
            float q = p * MathHelper.sin(this.field_3384.pitch);
            this.field_3384.pivotZ = -6.5f + p;
            this.field_3384.pivotY = -7.0f - q;
            float r = MathHelper.sin(((float)l - h) / 10.0f * (float)Math.PI * 0.25f);
            this.jaw.pitch = 1.5707964f * r;
            this.jaw.pitch = l > 5 ? MathHelper.sin(((float)(-4 + l) - h) / 4.0f) * (float)Math.PI * 0.4f : 0.15707964f * MathHelper.sin((float)Math.PI * ((float)l - h) / 10.0f);
        } else {
            float n = -1.0f;
            float o = -1.0f * MathHelper.sin(this.field_3384.pitch);
            this.field_3384.pivotX = 0.0f;
            this.field_3384.pivotY = -7.0f - o;
            this.field_3384.pivotZ = 5.5f;
            boolean bl = i > 0;
            this.field_3384.pitch = bl ? 0.21991149f : 0.0f;
            this.jaw.pitch = (float)Math.PI * (bl ? 0.05f : 0.01f);
            if (bl) {
                double d = (double)i / 40.0;
                this.field_3384.pivotX = (float)Math.sin(d * 10.0) * 3.0f;
            } else if (j > 0) {
                float q = MathHelper.sin(((float)(20 - j) - h) / 20.0f * (float)Math.PI * 0.25f);
                this.jaw.pitch = 1.5707964f * q;
            }
        }
    }

    private float method_2801(float f, float g) {
        return (Math.abs(f % g - g * 0.5f) - g * 0.25f) / (g * 0.25f);
    }
}

