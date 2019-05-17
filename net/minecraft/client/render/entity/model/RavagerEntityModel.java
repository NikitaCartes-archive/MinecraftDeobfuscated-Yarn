/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class RavagerEntityModel
extends EntityModel<RavagerEntity> {
    private final Cuboid field_3386;
    private final Cuboid field_3388;
    private final Cuboid field_3387;
    private final Cuboid field_3385;
    private final Cuboid field_3383;
    private final Cuboid field_3389;
    private final Cuboid field_3382;
    private final Cuboid field_3384;

    public RavagerEntityModel() {
        this.textureWidth = 128;
        this.textureHeight = 128;
        int i = 16;
        float f = 0.0f;
        this.field_3384 = new Cuboid(this);
        this.field_3384.setRotationPoint(0.0f, -7.0f, -1.5f);
        this.field_3384.setTextureOffset(68, 73).addBox(-5.0f, -1.0f, -18.0f, 10, 10, 18, 0.0f);
        this.field_3386 = new Cuboid(this);
        this.field_3386.setRotationPoint(0.0f, 16.0f, -17.0f);
        this.field_3386.setTextureOffset(0, 0).addBox(-8.0f, -20.0f, -14.0f, 16, 20, 16, 0.0f);
        this.field_3386.setTextureOffset(0, 0).addBox(-2.0f, -6.0f, -18.0f, 4, 8, 4, 0.0f);
        Cuboid cuboid = new Cuboid(this);
        cuboid.setRotationPoint(-10.0f, -14.0f, -8.0f);
        cuboid.setTextureOffset(74, 55).addBox(0.0f, -14.0f, -2.0f, 2, 14, 4, 0.0f);
        cuboid.pitch = 1.0995574f;
        this.field_3386.addChild(cuboid);
        Cuboid cuboid2 = new Cuboid(this);
        cuboid2.mirror = true;
        cuboid2.setRotationPoint(8.0f, -14.0f, -8.0f);
        cuboid2.setTextureOffset(74, 55).addBox(0.0f, -14.0f, -2.0f, 2, 14, 4, 0.0f);
        cuboid2.pitch = 1.0995574f;
        this.field_3386.addChild(cuboid2);
        this.field_3388 = new Cuboid(this);
        this.field_3388.setRotationPoint(0.0f, -2.0f, 2.0f);
        this.field_3388.setTextureOffset(0, 36).addBox(-8.0f, 0.0f, -16.0f, 16, 3, 16, 0.0f);
        this.field_3386.addChild(this.field_3388);
        this.field_3384.addChild(this.field_3386);
        this.field_3387 = new Cuboid(this);
        this.field_3387.setTextureOffset(0, 55).addBox(-7.0f, -10.0f, -7.0f, 14, 16, 20, 0.0f);
        this.field_3387.setTextureOffset(0, 91).addBox(-6.0f, 6.0f, -7.0f, 12, 13, 18, 0.0f);
        this.field_3387.setRotationPoint(0.0f, 1.0f, 2.0f);
        this.field_3385 = new Cuboid(this, 96, 0);
        this.field_3385.addBox(-4.0f, 0.0f, -4.0f, 8, 37, 8, 0.0f);
        this.field_3385.setRotationPoint(-8.0f, -13.0f, 18.0f);
        this.field_3383 = new Cuboid(this, 96, 0);
        this.field_3383.mirror = true;
        this.field_3383.addBox(-4.0f, 0.0f, -4.0f, 8, 37, 8, 0.0f);
        this.field_3383.setRotationPoint(8.0f, -13.0f, 18.0f);
        this.field_3389 = new Cuboid(this, 64, 0);
        this.field_3389.addBox(-4.0f, 0.0f, -4.0f, 8, 37, 8, 0.0f);
        this.field_3389.setRotationPoint(-8.0f, -13.0f, -5.0f);
        this.field_3382 = new Cuboid(this, 64, 0);
        this.field_3382.mirror = true;
        this.field_3382.addBox(-4.0f, 0.0f, -4.0f, 8, 37, 8, 0.0f);
        this.field_3382.setRotationPoint(8.0f, -13.0f, -5.0f);
    }

    public void method_17090(RavagerEntity ravagerEntity, float f, float g, float h, float i, float j, float k) {
        this.method_17091(ravagerEntity, f, g, h, i, j, k);
        this.field_3384.render(k);
        this.field_3387.render(k);
        this.field_3385.render(k);
        this.field_3383.render(k);
        this.field_3389.render(k);
        this.field_3382.render(k);
    }

    public void method_17091(RavagerEntity ravagerEntity, float f, float g, float h, float i, float j, float k) {
        this.field_3386.pitch = j * ((float)Math.PI / 180);
        this.field_3386.yaw = i * ((float)Math.PI / 180);
        this.field_3387.pitch = 1.5707964f;
        float l = 0.4f * g;
        this.field_3385.pitch = MathHelper.cos(f * 0.6662f) * l;
        this.field_3383.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * l;
        this.field_3389.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * l;
        this.field_3382.pitch = MathHelper.cos(f * 0.6662f) * l;
    }

    public void method_17089(RavagerEntity ravagerEntity, float f, float g, float h) {
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
            this.field_3384.rotationPointZ = -6.5f + p;
            this.field_3384.rotationPointY = -7.0f - q;
            float r = MathHelper.sin(((float)l - h) / 10.0f * (float)Math.PI * 0.25f);
            this.field_3388.pitch = 1.5707964f * r;
            this.field_3388.pitch = l > 5 ? MathHelper.sin(((float)(-4 + l) - h) / 4.0f) * (float)Math.PI * 0.4f : 0.15707964f * MathHelper.sin((float)Math.PI * ((float)l - h) / 10.0f);
        } else {
            float n = -1.0f;
            float o = -1.0f * MathHelper.sin(this.field_3384.pitch);
            this.field_3384.rotationPointX = 0.0f;
            this.field_3384.rotationPointY = -7.0f - o;
            this.field_3384.rotationPointZ = 5.5f;
            boolean bl = i > 0;
            this.field_3384.pitch = bl ? 0.21991149f : 0.0f;
            this.field_3388.pitch = (float)Math.PI * (bl ? 0.05f : 0.01f);
            if (bl) {
                double d = (double)i / 40.0;
                this.field_3384.rotationPointX = (float)Math.sin(d * 10.0) * 3.0f;
            } else if (j > 0) {
                float q = MathHelper.sin(((float)(20 - j) - h) / 20.0f * (float)Math.PI * 0.25f);
                this.field_3388.pitch = 1.5707964f * q;
            }
        }
    }

    private float method_2801(float f, float g) {
        return (Math.abs(f % g - g * 0.5f) - g * 0.25f) / (g * 0.25f);
    }

    @Override
    public /* synthetic */ void setAngles(Entity entity, float f, float g, float h, float i, float j, float k) {
        this.method_17091((RavagerEntity)entity, f, g, h, i, j, k);
    }

    @Override
    public /* synthetic */ void render(Entity entity, float f, float g, float h, float i, float j, float k) {
        this.method_17090((RavagerEntity)entity, f, g, h, i, j, k);
    }
}

