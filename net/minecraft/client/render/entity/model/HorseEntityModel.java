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
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class HorseEntityModel<T extends HorseBaseEntity>
extends AnimalModel<T> {
    protected final ModelPart field_3305;
    protected final ModelPart field_3307;
    private final ModelPart field_3306;
    private final ModelPart field_3303;
    private final ModelPart field_3302;
    private final ModelPart field_3308;
    private final ModelPart field_20930;
    private final ModelPart field_20931;
    private final ModelPart field_20932;
    private final ModelPart field_20933;
    private final ModelPart field_3300;
    private final ModelPart[] field_3304;
    private final ModelPart[] field_3301;

    public HorseEntityModel(float f) {
        super(true, 16.2f, 1.36f, 2.7272f, 2.0f, 20.0f);
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.field_3305 = new ModelPart(this, 0, 32);
        this.field_3305.addCuboid(-5.0f, -8.0f, -17.0f, 10.0f, 10.0f, 22.0f, 0.05f);
        this.field_3305.setPivot(0.0f, 11.0f, 5.0f);
        this.field_3307 = new ModelPart(this, 0, 35);
        this.field_3307.addCuboid(-2.05f, -6.0f, -2.0f, 4.0f, 12.0f, 7.0f);
        this.field_3307.pitch = 0.5235988f;
        ModelPart modelPart = new ModelPart(this, 0, 13);
        modelPart.addCuboid(-3.0f, -11.0f, -2.0f, 6.0f, 5.0f, 7.0f, f);
        ModelPart modelPart2 = new ModelPart(this, 56, 36);
        modelPart2.addCuboid(-1.0f, -11.0f, 5.01f, 2.0f, 16.0f, 2.0f, f);
        ModelPart modelPart3 = new ModelPart(this, 0, 25);
        modelPart3.addCuboid(-2.0f, -11.0f, -7.0f, 4.0f, 5.0f, 5.0f, f);
        this.field_3307.addChild(modelPart);
        this.field_3307.addChild(modelPart2);
        this.field_3307.addChild(modelPart3);
        this.method_2789(this.field_3307);
        this.field_3306 = new ModelPart(this, 48, 21);
        this.field_3306.mirror = true;
        this.field_3306.addCuboid(-3.0f, -1.01f, -1.0f, 4.0f, 11.0f, 4.0f, f);
        this.field_3306.setPivot(4.0f, 14.0f, 7.0f);
        this.field_3303 = new ModelPart(this, 48, 21);
        this.field_3303.addCuboid(-1.0f, -1.01f, -1.0f, 4.0f, 11.0f, 4.0f, f);
        this.field_3303.setPivot(-4.0f, 14.0f, 7.0f);
        this.field_3302 = new ModelPart(this, 48, 21);
        this.field_3302.mirror = true;
        this.field_3302.addCuboid(-3.0f, -1.01f, -1.9f, 4.0f, 11.0f, 4.0f, f);
        this.field_3302.setPivot(4.0f, 6.0f, -12.0f);
        this.field_3308 = new ModelPart(this, 48, 21);
        this.field_3308.addCuboid(-1.0f, -1.01f, -1.9f, 4.0f, 11.0f, 4.0f, f);
        this.field_3308.setPivot(-4.0f, 6.0f, -12.0f);
        float g = 5.5f;
        this.field_20930 = new ModelPart(this, 48, 21);
        this.field_20930.mirror = true;
        this.field_20930.addCuboid(-3.0f, -1.01f, -1.0f, 4.0f, 11.0f, 4.0f, f, f + 5.5f, f);
        this.field_20930.setPivot(4.0f, 14.0f, 7.0f);
        this.field_20931 = new ModelPart(this, 48, 21);
        this.field_20931.addCuboid(-1.0f, -1.01f, -1.0f, 4.0f, 11.0f, 4.0f, f, f + 5.5f, f);
        this.field_20931.setPivot(-4.0f, 14.0f, 7.0f);
        this.field_20932 = new ModelPart(this, 48, 21);
        this.field_20932.mirror = true;
        this.field_20932.addCuboid(-3.0f, -1.01f, -1.9f, 4.0f, 11.0f, 4.0f, f, f + 5.5f, f);
        this.field_20932.setPivot(4.0f, 6.0f, -12.0f);
        this.field_20933 = new ModelPart(this, 48, 21);
        this.field_20933.addCuboid(-1.0f, -1.01f, -1.9f, 4.0f, 11.0f, 4.0f, f, f + 5.5f, f);
        this.field_20933.setPivot(-4.0f, 6.0f, -12.0f);
        this.field_3300 = new ModelPart(this, 42, 36);
        this.field_3300.addCuboid(-1.5f, 0.0f, 0.0f, 3.0f, 14.0f, 4.0f, f);
        this.field_3300.setPivot(0.0f, -5.0f, 2.0f);
        this.field_3300.pitch = 0.5235988f;
        this.field_3305.addChild(this.field_3300);
        ModelPart modelPart4 = new ModelPart(this, 26, 0);
        modelPart4.addCuboid(-5.0f, -8.0f, -9.0f, 10.0f, 9.0f, 9.0f, 0.5f);
        this.field_3305.addChild(modelPart4);
        ModelPart modelPart5 = new ModelPart(this, 29, 5);
        modelPart5.addCuboid(2.0f, -9.0f, -6.0f, 1.0f, 2.0f, 2.0f, f);
        this.field_3307.addChild(modelPart5);
        ModelPart modelPart6 = new ModelPart(this, 29, 5);
        modelPart6.addCuboid(-3.0f, -9.0f, -6.0f, 1.0f, 2.0f, 2.0f, f);
        this.field_3307.addChild(modelPart6);
        ModelPart modelPart7 = new ModelPart(this, 32, 2);
        modelPart7.addCuboid(3.1f, -6.0f, -8.0f, 0.0f, 3.0f, 16.0f, f);
        modelPart7.pitch = -0.5235988f;
        this.field_3307.addChild(modelPart7);
        ModelPart modelPart8 = new ModelPart(this, 32, 2);
        modelPart8.addCuboid(-3.1f, -6.0f, -8.0f, 0.0f, 3.0f, 16.0f, f);
        modelPart8.pitch = -0.5235988f;
        this.field_3307.addChild(modelPart8);
        ModelPart modelPart9 = new ModelPart(this, 1, 1);
        modelPart9.addCuboid(-3.0f, -11.0f, -1.9f, 6.0f, 5.0f, 6.0f, 0.2f);
        this.field_3307.addChild(modelPart9);
        ModelPart modelPart10 = new ModelPart(this, 19, 0);
        modelPart10.addCuboid(-2.0f, -11.0f, -4.0f, 4.0f, 5.0f, 2.0f, 0.2f);
        this.field_3307.addChild(modelPart10);
        this.field_3304 = new ModelPart[]{modelPart4, modelPart5, modelPart6, modelPart9, modelPart10};
        this.field_3301 = new ModelPart[]{modelPart7, modelPart8};
    }

    protected void method_2789(ModelPart modelPart) {
        ModelPart modelPart2 = new ModelPart(this, 19, 16);
        modelPart2.addCuboid(0.55f, -13.0f, 4.0f, 2.0f, 3.0f, 1.0f, -0.001f);
        ModelPart modelPart3 = new ModelPart(this, 19, 16);
        modelPart3.addCuboid(-2.55f, -13.0f, 4.0f, 2.0f, 3.0f, 1.0f, -0.001f);
        modelPart.addChild(modelPart2);
        modelPart.addChild(modelPart3);
    }

    public void method_17085(T horseBaseEntity, float f, float g, float h, float i, float j) {
        boolean bl = ((HorseBaseEntity)horseBaseEntity).isSaddled();
        boolean bl2 = ((Entity)horseBaseEntity).hasPassengers();
        for (ModelPart modelPart : this.field_3304) {
            modelPart.visible = bl;
        }
        for (ModelPart modelPart : this.field_3301) {
            modelPart.visible = bl2 && bl;
        }
        this.field_3305.pivotY = 11.0f;
    }

    @Override
    public Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of(this.field_3307);
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of(this.field_3305, this.field_3306, this.field_3303, this.field_3302, this.field_3308, this.field_20930, this.field_20931, this.field_20932, this.field_20933);
    }

    public void method_17084(T horseBaseEntity, float f, float g, float h) {
        super.animateModel(horseBaseEntity, f, g, h);
        float i = MathHelper.method_22859(((HorseBaseEntity)horseBaseEntity).prevBodyYaw, ((HorseBaseEntity)horseBaseEntity).bodyYaw, h);
        float j = MathHelper.method_22859(((HorseBaseEntity)horseBaseEntity).prevHeadYaw, ((HorseBaseEntity)horseBaseEntity).headYaw, h);
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
        this.field_3307.pivotY = 4.0f;
        this.field_3307.pivotZ = -12.0f;
        this.field_3305.pitch = 0.0f;
        this.field_3307.pitch = 0.5235988f + m;
        this.field_3307.yaw = l * ((float)Math.PI / 180);
        float s = ((Entity)horseBaseEntity).isInsideWater() ? 0.2f : 1.0f;
        float t = MathHelper.cos(s * f * 0.6662f + (float)Math.PI);
        float u = t * 0.8f * g;
        float v = (1.0f - Math.max(o, n)) * (0.5235988f + m + q * MathHelper.sin(r) * 0.05f);
        this.field_3307.pitch = o * (0.2617994f + m) + n * (2.1816616f + MathHelper.sin(r) * 0.05f) + v;
        this.field_3307.yaw = o * l * ((float)Math.PI / 180) + (1.0f - Math.max(o, n)) * this.field_3307.yaw;
        this.field_3307.pivotY = o * -4.0f + n * 11.0f + (1.0f - Math.max(o, n)) * this.field_3307.pivotY;
        this.field_3307.pivotZ = o * -4.0f + n * -12.0f + (1.0f - Math.max(o, n)) * this.field_3307.pivotZ;
        this.field_3305.pitch = o * -0.7853982f + p * this.field_3305.pitch;
        float w = 0.2617994f * o;
        float x = MathHelper.cos(r * 0.6f + (float)Math.PI);
        this.field_3302.pivotY = 2.0f * o + 14.0f * p;
        this.field_3302.pivotZ = -6.0f * o - 10.0f * p;
        this.field_3308.pivotY = this.field_3302.pivotY;
        this.field_3308.pivotZ = this.field_3302.pivotZ;
        float y = (-1.0471976f + x) * o + u * p;
        float z = (-1.0471976f - x) * o - u * p;
        this.field_3306.pitch = w - t * 0.5f * g * p;
        this.field_3303.pitch = w + t * 0.5f * g * p;
        this.field_3302.pitch = y;
        this.field_3308.pitch = z;
        this.field_3300.pitch = 0.5235988f + g * 0.75f;
        this.field_3300.pivotY = -5.0f + g;
        this.field_3300.pivotZ = 2.0f + g * 2.0f;
        this.field_3300.yaw = bl ? MathHelper.cos(r * 0.7f) : 0.0f;
        this.field_20930.pivotY = this.field_3306.pivotY;
        this.field_20930.pivotZ = this.field_3306.pivotZ;
        this.field_20930.pitch = this.field_3306.pitch;
        this.field_20931.pivotY = this.field_3303.pivotY;
        this.field_20931.pivotZ = this.field_3303.pivotZ;
        this.field_20931.pitch = this.field_3303.pitch;
        this.field_20932.pivotY = this.field_3302.pivotY;
        this.field_20932.pivotZ = this.field_3302.pivotZ;
        this.field_20932.pitch = this.field_3302.pitch;
        this.field_20933.pivotY = this.field_3308.pivotY;
        this.field_20933.pivotZ = this.field_3308.pivotZ;
        this.field_20933.pitch = this.field_3308.pitch;
        boolean bl2 = ((PassiveEntity)horseBaseEntity).isBaby();
        this.field_3306.visible = !bl2;
        this.field_3303.visible = !bl2;
        this.field_3302.visible = !bl2;
        this.field_3308.visible = !bl2;
        this.field_20930.visible = bl2;
        this.field_20931.visible = bl2;
        this.field_20932.visible = bl2;
        this.field_20933.visible = bl2;
        this.field_3305.pivotY = bl2 ? 10.8f : 0.0f;
    }
}

