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
    protected final ModelPart body;
    protected final ModelPart head;
    private final ModelPart leftBackLeg;
    private final ModelPart rightBackLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart field_20930;
    private final ModelPart field_20931;
    private final ModelPart field_20932;
    private final ModelPart field_20933;
    private final ModelPart tail;
    private final ModelPart[] field_3304;
    private final ModelPart[] field_3301;

    public HorseEntityModel(float scale) {
        super(true, 16.2f, 1.36f, 2.7272f, 2.0f, 20.0f);
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.body = new ModelPart(this, 0, 32);
        this.body.addCuboid(-5.0f, -8.0f, -17.0f, 10.0f, 10.0f, 22.0f, 0.05f);
        this.body.setPivot(0.0f, 11.0f, 5.0f);
        this.head = new ModelPart(this, 0, 35);
        this.head.addCuboid(-2.05f, -6.0f, -2.0f, 4.0f, 12.0f, 7.0f);
        this.head.pitch = 0.5235988f;
        ModelPart modelPart = new ModelPart(this, 0, 13);
        modelPart.addCuboid(-3.0f, -11.0f, -2.0f, 6.0f, 5.0f, 7.0f, scale);
        ModelPart modelPart2 = new ModelPart(this, 56, 36);
        modelPart2.addCuboid(-1.0f, -11.0f, 5.01f, 2.0f, 16.0f, 2.0f, scale);
        ModelPart modelPart3 = new ModelPart(this, 0, 25);
        modelPart3.addCuboid(-2.0f, -11.0f, -7.0f, 4.0f, 5.0f, 5.0f, scale);
        this.head.addChild(modelPart);
        this.head.addChild(modelPart2);
        this.head.addChild(modelPart3);
        this.method_2789(this.head);
        this.leftBackLeg = new ModelPart(this, 48, 21);
        this.leftBackLeg.mirror = true;
        this.leftBackLeg.addCuboid(-3.0f, -1.01f, -1.0f, 4.0f, 11.0f, 4.0f, scale);
        this.leftBackLeg.setPivot(4.0f, 14.0f, 7.0f);
        this.rightBackLeg = new ModelPart(this, 48, 21);
        this.rightBackLeg.addCuboid(-1.0f, -1.01f, -1.0f, 4.0f, 11.0f, 4.0f, scale);
        this.rightBackLeg.setPivot(-4.0f, 14.0f, 7.0f);
        this.leftFrontLeg = new ModelPart(this, 48, 21);
        this.leftFrontLeg.mirror = true;
        this.leftFrontLeg.addCuboid(-3.0f, -1.01f, -1.9f, 4.0f, 11.0f, 4.0f, scale);
        this.leftFrontLeg.setPivot(4.0f, 6.0f, -12.0f);
        this.rightFrontLeg = new ModelPart(this, 48, 21);
        this.rightFrontLeg.addCuboid(-1.0f, -1.01f, -1.9f, 4.0f, 11.0f, 4.0f, scale);
        this.rightFrontLeg.setPivot(-4.0f, 6.0f, -12.0f);
        float f = 5.5f;
        this.field_20930 = new ModelPart(this, 48, 21);
        this.field_20930.mirror = true;
        this.field_20930.addCuboid(-3.0f, -1.01f, -1.0f, 4.0f, 11.0f, 4.0f, scale, scale + 5.5f, scale);
        this.field_20930.setPivot(4.0f, 14.0f, 7.0f);
        this.field_20931 = new ModelPart(this, 48, 21);
        this.field_20931.addCuboid(-1.0f, -1.01f, -1.0f, 4.0f, 11.0f, 4.0f, scale, scale + 5.5f, scale);
        this.field_20931.setPivot(-4.0f, 14.0f, 7.0f);
        this.field_20932 = new ModelPart(this, 48, 21);
        this.field_20932.mirror = true;
        this.field_20932.addCuboid(-3.0f, -1.01f, -1.9f, 4.0f, 11.0f, 4.0f, scale, scale + 5.5f, scale);
        this.field_20932.setPivot(4.0f, 6.0f, -12.0f);
        this.field_20933 = new ModelPart(this, 48, 21);
        this.field_20933.addCuboid(-1.0f, -1.01f, -1.9f, 4.0f, 11.0f, 4.0f, scale, scale + 5.5f, scale);
        this.field_20933.setPivot(-4.0f, 6.0f, -12.0f);
        this.tail = new ModelPart(this, 42, 36);
        this.tail.addCuboid(-1.5f, 0.0f, 0.0f, 3.0f, 14.0f, 4.0f, scale);
        this.tail.setPivot(0.0f, -5.0f, 2.0f);
        this.tail.pitch = 0.5235988f;
        this.body.addChild(this.tail);
        ModelPart modelPart4 = new ModelPart(this, 26, 0);
        modelPart4.addCuboid(-5.0f, -8.0f, -9.0f, 10.0f, 9.0f, 9.0f, 0.5f);
        this.body.addChild(modelPart4);
        ModelPart modelPart5 = new ModelPart(this, 29, 5);
        modelPart5.addCuboid(2.0f, -9.0f, -6.0f, 1.0f, 2.0f, 2.0f, scale);
        this.head.addChild(modelPart5);
        ModelPart modelPart6 = new ModelPart(this, 29, 5);
        modelPart6.addCuboid(-3.0f, -9.0f, -6.0f, 1.0f, 2.0f, 2.0f, scale);
        this.head.addChild(modelPart6);
        ModelPart modelPart7 = new ModelPart(this, 32, 2);
        modelPart7.addCuboid(3.1f, -6.0f, -8.0f, 0.0f, 3.0f, 16.0f, scale);
        modelPart7.pitch = -0.5235988f;
        this.head.addChild(modelPart7);
        ModelPart modelPart8 = new ModelPart(this, 32, 2);
        modelPart8.addCuboid(-3.1f, -6.0f, -8.0f, 0.0f, 3.0f, 16.0f, scale);
        modelPart8.pitch = -0.5235988f;
        this.head.addChild(modelPart8);
        ModelPart modelPart9 = new ModelPart(this, 1, 1);
        modelPart9.addCuboid(-3.0f, -11.0f, -1.9f, 6.0f, 5.0f, 6.0f, 0.2f);
        this.head.addChild(modelPart9);
        ModelPart modelPart10 = new ModelPart(this, 19, 0);
        modelPart10.addCuboid(-2.0f, -11.0f, -4.0f, 4.0f, 5.0f, 2.0f, 0.2f);
        this.head.addChild(modelPart10);
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

    @Override
    public void setAngles(T horseBaseEntity, float f, float g, float h, float i, float j) {
        boolean bl = ((HorseBaseEntity)horseBaseEntity).isSaddled();
        boolean bl2 = ((Entity)horseBaseEntity).hasPassengers();
        for (ModelPart modelPart : this.field_3304) {
            modelPart.visible = bl;
        }
        for (ModelPart modelPart : this.field_3301) {
            modelPart.visible = bl2 && bl;
        }
        this.body.pivotY = 11.0f;
    }

    @Override
    public Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of(this.head);
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of(this.body, this.leftBackLeg, this.rightBackLeg, this.leftFrontLeg, this.rightFrontLeg, this.field_20930, this.field_20931, this.field_20932, this.field_20933);
    }

    @Override
    public void animateModel(T horseBaseEntity, float f, float g, float h) {
        super.animateModel(horseBaseEntity, f, g, h);
        float i = MathHelper.lerpAngle(((HorseBaseEntity)horseBaseEntity).prevBodyYaw, ((HorseBaseEntity)horseBaseEntity).bodyYaw, h);
        float j = MathHelper.lerpAngle(((HorseBaseEntity)horseBaseEntity).prevHeadYaw, ((HorseBaseEntity)horseBaseEntity).headYaw, h);
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
        boolean bl = ((HorseBaseEntity)horseBaseEntity).tailWagTicks != 0;
        float r = (float)((HorseBaseEntity)horseBaseEntity).age + h;
        this.head.pivotY = 4.0f;
        this.head.pivotZ = -12.0f;
        this.body.pitch = 0.0f;
        this.head.pitch = 0.5235988f + m;
        this.head.yaw = l * ((float)Math.PI / 180);
        float s = ((Entity)horseBaseEntity).isTouchingWater() ? 0.2f : 1.0f;
        float t = MathHelper.cos(s * f * 0.6662f + (float)Math.PI);
        float u = t * 0.8f * g;
        float v = (1.0f - Math.max(o, n)) * (0.5235988f + m + q * MathHelper.sin(r) * 0.05f);
        this.head.pitch = o * (0.2617994f + m) + n * (2.1816616f + MathHelper.sin(r) * 0.05f) + v;
        this.head.yaw = o * l * ((float)Math.PI / 180) + (1.0f - Math.max(o, n)) * this.head.yaw;
        this.head.pivotY = o * -4.0f + n * 11.0f + (1.0f - Math.max(o, n)) * this.head.pivotY;
        this.head.pivotZ = o * -4.0f + n * -12.0f + (1.0f - Math.max(o, n)) * this.head.pivotZ;
        this.body.pitch = o * -0.7853982f + p * this.body.pitch;
        float w = 0.2617994f * o;
        float x = MathHelper.cos(r * 0.6f + (float)Math.PI);
        this.leftFrontLeg.pivotY = 2.0f * o + 14.0f * p;
        this.leftFrontLeg.pivotZ = -6.0f * o - 10.0f * p;
        this.rightFrontLeg.pivotY = this.leftFrontLeg.pivotY;
        this.rightFrontLeg.pivotZ = this.leftFrontLeg.pivotZ;
        float y = (-1.0471976f + x) * o + u * p;
        float z = (-1.0471976f - x) * o - u * p;
        this.leftBackLeg.pitch = w - t * 0.5f * g * p;
        this.rightBackLeg.pitch = w + t * 0.5f * g * p;
        this.leftFrontLeg.pitch = y;
        this.rightFrontLeg.pitch = z;
        this.tail.pitch = 0.5235988f + g * 0.75f;
        this.tail.pivotY = -5.0f + g;
        this.tail.pivotZ = 2.0f + g * 2.0f;
        this.tail.yaw = bl ? MathHelper.cos(r * 0.7f) : 0.0f;
        this.field_20930.pivotY = this.leftBackLeg.pivotY;
        this.field_20930.pivotZ = this.leftBackLeg.pivotZ;
        this.field_20930.pitch = this.leftBackLeg.pitch;
        this.field_20931.pivotY = this.rightBackLeg.pivotY;
        this.field_20931.pivotZ = this.rightBackLeg.pivotZ;
        this.field_20931.pitch = this.rightBackLeg.pitch;
        this.field_20932.pivotY = this.leftFrontLeg.pivotY;
        this.field_20932.pivotZ = this.leftFrontLeg.pivotZ;
        this.field_20932.pitch = this.leftFrontLeg.pitch;
        this.field_20933.pivotY = this.rightFrontLeg.pivotY;
        this.field_20933.pivotZ = this.rightFrontLeg.pivotZ;
        this.field_20933.pitch = this.rightFrontLeg.pitch;
        boolean bl2 = ((PassiveEntity)horseBaseEntity).isBaby();
        this.leftBackLeg.visible = !bl2;
        this.rightBackLeg.visible = !bl2;
        this.leftFrontLeg.visible = !bl2;
        this.rightFrontLeg.visible = !bl2;
        this.field_20930.visible = bl2;
        this.field_20931.visible = bl2;
        this.field_20932.visible = bl2;
        this.field_20933.visible = bl2;
        this.body.pivotY = bl2 ? 10.8f : 0.0f;
    }
}

