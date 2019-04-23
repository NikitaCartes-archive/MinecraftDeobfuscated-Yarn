/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class HorseEntityModel<T extends HorseBaseEntity>
extends EntityModel<T> {
    protected final Cuboid field_3305;
    protected final Cuboid field_3307;
    private final Cuboid field_3306;
    private final Cuboid field_3303;
    private final Cuboid field_3302;
    private final Cuboid field_3308;
    private final Cuboid field_3300;
    private final Cuboid[] field_3304;
    private final Cuboid[] field_3301;

    public HorseEntityModel(float f) {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.field_3305 = new Cuboid(this, 0, 32);
        this.field_3305.addBox(-5.0f, -8.0f, -17.0f, 10, 10, 22, 0.05f);
        this.field_3305.setRotationPoint(0.0f, 11.0f, 5.0f);
        this.field_3307 = new Cuboid(this, 0, 35);
        this.field_3307.addBox(-2.05f, -6.0f, -2.0f, 4, 12, 7);
        this.field_3307.pitch = 0.5235988f;
        Cuboid cuboid = new Cuboid(this, 0, 13);
        cuboid.addBox(-3.0f, -11.0f, -2.0f, 6, 5, 7, f);
        Cuboid cuboid2 = new Cuboid(this, 56, 36);
        cuboid2.addBox(-1.0f, -11.0f, 5.01f, 2, 16, 2, f);
        Cuboid cuboid3 = new Cuboid(this, 0, 25);
        cuboid3.addBox(-2.0f, -11.0f, -7.0f, 4, 5, 5, f);
        this.field_3307.addChild(cuboid);
        this.field_3307.addChild(cuboid2);
        this.field_3307.addChild(cuboid3);
        this.method_2789(this.field_3307);
        this.field_3306 = new Cuboid(this, 48, 21);
        this.field_3306.mirror = true;
        this.field_3306.addBox(-3.0f, -1.01f, -1.0f, 4, 11, 4, f);
        this.field_3306.setRotationPoint(4.0f, 14.0f, 7.0f);
        this.field_3303 = new Cuboid(this, 48, 21);
        this.field_3303.addBox(-1.0f, -1.01f, -1.0f, 4, 11, 4, f);
        this.field_3303.setRotationPoint(-4.0f, 14.0f, 7.0f);
        this.field_3302 = new Cuboid(this, 48, 21);
        this.field_3302.mirror = true;
        this.field_3302.addBox(-3.0f, -1.01f, -1.9f, 4, 11, 4, f);
        this.field_3302.setRotationPoint(4.0f, 6.0f, -12.0f);
        this.field_3308 = new Cuboid(this, 48, 21);
        this.field_3308.addBox(-1.0f, -1.01f, -1.9f, 4, 11, 4, f);
        this.field_3308.setRotationPoint(-4.0f, 6.0f, -12.0f);
        this.field_3300 = new Cuboid(this, 42, 36);
        this.field_3300.addBox(-1.5f, 0.0f, 0.0f, 3, 14, 4, f);
        this.field_3300.setRotationPoint(0.0f, -5.0f, 2.0f);
        this.field_3300.pitch = 0.5235988f;
        this.field_3305.addChild(this.field_3300);
        Cuboid cuboid4 = new Cuboid(this, 26, 0);
        cuboid4.addBox(-5.0f, -8.0f, -9.0f, 10, 9, 9, 0.5f);
        this.field_3305.addChild(cuboid4);
        Cuboid cuboid5 = new Cuboid(this, 29, 5);
        cuboid5.addBox(2.0f, -9.0f, -6.0f, 1, 2, 2, f);
        this.field_3307.addChild(cuboid5);
        Cuboid cuboid6 = new Cuboid(this, 29, 5);
        cuboid6.addBox(-3.0f, -9.0f, -6.0f, 1, 2, 2, f);
        this.field_3307.addChild(cuboid6);
        Cuboid cuboid7 = new Cuboid(this, 32, 2);
        cuboid7.addBox(3.1f, -6.0f, -8.0f, 0, 3, 16, f);
        cuboid7.pitch = -0.5235988f;
        this.field_3307.addChild(cuboid7);
        Cuboid cuboid8 = new Cuboid(this, 32, 2);
        cuboid8.addBox(-3.1f, -6.0f, -8.0f, 0, 3, 16, f);
        cuboid8.pitch = -0.5235988f;
        this.field_3307.addChild(cuboid8);
        Cuboid cuboid9 = new Cuboid(this, 1, 1);
        cuboid9.addBox(-3.0f, -11.0f, -1.9f, 6, 5, 6, 0.2f);
        this.field_3307.addChild(cuboid9);
        Cuboid cuboid10 = new Cuboid(this, 19, 0);
        cuboid10.addBox(-2.0f, -11.0f, -4.0f, 4, 5, 2, 0.2f);
        this.field_3307.addChild(cuboid10);
        this.field_3304 = new Cuboid[]{cuboid4, cuboid5, cuboid6, cuboid9, cuboid10};
        this.field_3301 = new Cuboid[]{cuboid7, cuboid8};
    }

    protected void method_2789(Cuboid cuboid) {
        Cuboid cuboid2 = new Cuboid(this, 19, 16);
        cuboid2.addBox(0.55f, -13.0f, 4.0f, 2, 3, 1, -0.001f);
        Cuboid cuboid3 = new Cuboid(this, 19, 16);
        cuboid3.addBox(-2.55f, -13.0f, 4.0f, 2, 3, 1, -0.001f);
        cuboid.addChild(cuboid2);
        cuboid.addChild(cuboid3);
    }

    public void method_17085(T horseBaseEntity, float f, float g, float h, float i, float j, float k) {
        boolean bl = ((PassiveEntity)horseBaseEntity).isBaby();
        float l = ((LivingEntity)horseBaseEntity).getScaleFactor();
        boolean bl2 = ((HorseBaseEntity)horseBaseEntity).isSaddled();
        boolean bl3 = ((Entity)horseBaseEntity).hasPassengers();
        for (Cuboid cuboid : this.field_3304) {
            cuboid.visible = bl2;
        }
        for (Cuboid cuboid : this.field_3301) {
            cuboid.visible = bl3 && bl2;
        }
        if (bl) {
            GlStateManager.pushMatrix();
            GlStateManager.scalef(l, 0.5f + l * 0.5f, l);
            GlStateManager.translatef(0.0f, 0.95f * (1.0f - l), 0.0f);
        }
        this.field_3306.render(k);
        this.field_3303.render(k);
        this.field_3302.render(k);
        this.field_3308.render(k);
        if (bl) {
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.scalef(l, l, l);
            GlStateManager.translatef(0.0f, 2.3f * (1.0f - l), 0.0f);
        }
        this.field_3305.render(k);
        if (bl) {
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            float m = l + 0.1f * l;
            GlStateManager.scalef(m, m, m);
            GlStateManager.translatef(0.0f, 2.25f * (1.0f - m), 0.1f * (1.4f - m));
        }
        this.field_3307.render(k);
        if (bl) {
            GlStateManager.popMatrix();
        }
    }

    public void method_17084(T horseBaseEntity, float f, float g, float h) {
        super.animateModel(horseBaseEntity, f, g, h);
        float i = this.method_2790(((HorseBaseEntity)horseBaseEntity).field_6220, ((HorseBaseEntity)horseBaseEntity).field_6283, h);
        float j = this.method_2790(((HorseBaseEntity)horseBaseEntity).prevHeadYaw, ((HorseBaseEntity)horseBaseEntity).headYaw, h);
        float k = MathHelper.lerp(h, ((HorseBaseEntity)horseBaseEntity).prevPitch, ((HorseBaseEntity)horseBaseEntity).pitch);
        float l = j - i;
        float m = k * ((float)Math.PI / 180);
        if (l > 20.0f) {
            l = 20.0f;
        }
        if (l < -20.0f) {
            l = -20.0f;
        }
        if (g > 0.2f) {
            m += MathHelper.cos(f * 0.4f) * 0.15f * g;
        }
        float n = ((HorseBaseEntity)horseBaseEntity).getEatingGrassAnimationProgress(h);
        float o = ((HorseBaseEntity)horseBaseEntity).getAngryAnimationProgress(h);
        float p = 1.0f - o;
        float q = ((HorseBaseEntity)horseBaseEntity).getEatingAnimationProgress(h);
        boolean bl = ((HorseBaseEntity)horseBaseEntity).field_6957 != 0;
        float r = (float)((HorseBaseEntity)horseBaseEntity).age + h;
        this.field_3307.rotationPointY = 4.0f;
        this.field_3307.rotationPointZ = -12.0f;
        this.field_3305.pitch = 0.0f;
        this.field_3307.pitch = 0.5235988f + m;
        this.field_3307.yaw = l * ((float)Math.PI / 180);
        float s = ((Entity)horseBaseEntity).isInsideWater() ? 0.2f : 1.0f;
        float t = MathHelper.cos(s * f * 0.6662f + (float)Math.PI);
        float u = t * 0.8f * g;
        float v = (1.0f - Math.max(o, n)) * (0.5235988f + m + q * MathHelper.sin(r) * 0.05f);
        this.field_3307.pitch = o * (0.2617994f + m) + n * (2.1816616f + MathHelper.sin(r) * 0.05f) + v;
        this.field_3307.yaw = o * l * ((float)Math.PI / 180) + (1.0f - Math.max(o, n)) * this.field_3307.yaw;
        this.field_3307.rotationPointY = o * -4.0f + n * 11.0f + (1.0f - Math.max(o, n)) * this.field_3307.rotationPointY;
        this.field_3307.rotationPointZ = o * -4.0f + n * -12.0f + (1.0f - Math.max(o, n)) * this.field_3307.rotationPointZ;
        this.field_3305.pitch = o * -0.7853982f + p * this.field_3305.pitch;
        float w = 0.2617994f * o;
        float x = MathHelper.cos(r * 0.6f + (float)Math.PI);
        this.field_3302.rotationPointY = 2.0f * o + 14.0f * p;
        this.field_3302.rotationPointZ = -6.0f * o - 10.0f * p;
        this.field_3308.rotationPointY = this.field_3302.rotationPointY;
        this.field_3308.rotationPointZ = this.field_3302.rotationPointZ;
        float y = (-1.0471976f + x) * o + u * p;
        float z = (-1.0471976f - x) * o - u * p;
        this.field_3306.pitch = w - t * 0.5f * g * p;
        this.field_3303.pitch = w + t * 0.5f * g * p;
        this.field_3302.pitch = y;
        this.field_3308.pitch = z;
        this.field_3300.pitch = 0.5235988f + g * 0.75f;
        this.field_3300.rotationPointY = -5.0f + g;
        this.field_3300.rotationPointZ = 2.0f + g * 2.0f;
        this.field_3300.yaw = bl ? MathHelper.cos(r * 0.7f) : 0.0f;
    }

    private float method_2790(float f, float g, float h) {
        float i;
        for (i = g - f; i < -180.0f; i += 360.0f) {
        }
        while (i >= 180.0f) {
            i -= 360.0f;
        }
        return f + h * i;
    }

    @Override
    public /* synthetic */ void render(Entity entity, float f, float g, float h, float i, float j, float k) {
        this.method_17085((HorseBaseEntity)entity, f, g, h, i, j, k);
    }
}

