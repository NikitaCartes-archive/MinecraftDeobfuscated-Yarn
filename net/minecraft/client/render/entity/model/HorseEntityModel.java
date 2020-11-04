/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5603;
import net.minecraft.class_5605;
import net.minecraft.class_5606;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class HorseEntityModel<T extends HorseBaseEntity>
extends AnimalModel<T> {
    protected final ModelPart torso;
    protected final ModelPart head;
    private final ModelPart field_27425;
    private final ModelPart field_27426;
    private final ModelPart field_27427;
    private final ModelPart field_27428;
    private final ModelPart field_27429;
    private final ModelPart field_27430;
    private final ModelPart field_27431;
    private final ModelPart field_27432;
    private final ModelPart tail;
    private final ModelPart[] field_3304;
    private final ModelPart[] field_3301;

    public HorseEntityModel(ModelPart modelPart) {
        super(true, 16.2f, 1.36f, 2.7272f, 2.0f, 20.0f);
        this.torso = modelPart.method_32086("body");
        this.head = modelPart.method_32086("head_parts");
        this.field_27425 = modelPart.method_32086("right_hind_leg");
        this.field_27426 = modelPart.method_32086("left_hind_leg");
        this.field_27427 = modelPart.method_32086("right_front_leg");
        this.field_27428 = modelPart.method_32086("left_front_leg");
        this.field_27429 = modelPart.method_32086("right_hind_baby_leg");
        this.field_27430 = modelPart.method_32086("left_hind_baby_leg");
        this.field_27431 = modelPart.method_32086("right_front_baby_leg");
        this.field_27432 = modelPart.method_32086("left_front_baby_leg");
        this.tail = this.torso.method_32086("tail");
        ModelPart modelPart2 = this.torso.method_32086("saddle");
        ModelPart modelPart3 = this.head.method_32086("left_saddle_mouth");
        ModelPart modelPart4 = this.head.method_32086("right_saddle_mouth");
        ModelPart modelPart5 = this.head.method_32086("left_saddle_line");
        ModelPart modelPart6 = this.head.method_32086("right_saddle_line");
        ModelPart modelPart7 = this.head.method_32086("head_saddle");
        ModelPart modelPart8 = this.head.method_32086("mouth_saddle_wrap");
        this.field_3304 = new ModelPart[]{modelPart2, modelPart3, modelPart4, modelPart7, modelPart8};
        this.field_3301 = new ModelPart[]{modelPart5, modelPart6};
    }

    public static class_5609 method_32010(class_5605 arg) {
        class_5609 lv = new class_5609();
        class_5610 lv2 = lv.method_32111();
        class_5610 lv3 = lv2.method_32117("body", class_5606.method_32108().method_32101(0, 32).method_32098(-5.0f, -8.0f, -17.0f, 10.0f, 10.0f, 22.0f, new class_5605(0.05f)), class_5603.method_32090(0.0f, 11.0f, 5.0f));
        class_5610 lv4 = lv2.method_32117("head_parts", class_5606.method_32108().method_32101(0, 35).method_32097(-2.05f, -6.0f, -2.0f, 4.0f, 12.0f, 7.0f), class_5603.method_32091(0.0f, 4.0f, -12.0f, 0.5235988f, 0.0f, 0.0f));
        class_5610 lv5 = lv4.method_32117("head", class_5606.method_32108().method_32101(0, 13).method_32098(-3.0f, -11.0f, -2.0f, 6.0f, 5.0f, 7.0f, arg), class_5603.field_27701);
        lv4.method_32117("mane", class_5606.method_32108().method_32101(56, 36).method_32098(-1.0f, -11.0f, 5.01f, 2.0f, 16.0f, 2.0f, arg), class_5603.field_27701);
        lv4.method_32117("upper_mouth", class_5606.method_32108().method_32101(0, 25).method_32098(-2.0f, -11.0f, -7.0f, 4.0f, 5.0f, 5.0f, arg), class_5603.field_27701);
        lv2.method_32117("left_hind_leg", class_5606.method_32108().method_32101(48, 21).method_32096().method_32098(-3.0f, -1.01f, -1.0f, 4.0f, 11.0f, 4.0f, arg), class_5603.method_32090(4.0f, 14.0f, 7.0f));
        lv2.method_32117("right_hind_leg", class_5606.method_32108().method_32101(48, 21).method_32098(-1.0f, -1.01f, -1.0f, 4.0f, 11.0f, 4.0f, arg), class_5603.method_32090(-4.0f, 14.0f, 7.0f));
        lv2.method_32117("left_front_leg", class_5606.method_32108().method_32101(48, 21).method_32096().method_32098(-3.0f, -1.01f, -1.9f, 4.0f, 11.0f, 4.0f, arg), class_5603.method_32090(4.0f, 14.0f, -12.0f));
        lv2.method_32117("right_front_leg", class_5606.method_32108().method_32101(48, 21).method_32098(-1.0f, -1.01f, -1.9f, 4.0f, 11.0f, 4.0f, arg), class_5603.method_32090(-4.0f, 14.0f, -12.0f));
        class_5605 lv6 = arg.method_32095(0.0f, 5.5f, 0.0f);
        lv2.method_32117("left_hind_baby_leg", class_5606.method_32108().method_32101(48, 21).method_32096().method_32098(-3.0f, -1.01f, -1.0f, 4.0f, 11.0f, 4.0f, lv6), class_5603.method_32090(4.0f, 14.0f, 7.0f));
        lv2.method_32117("right_hind_baby_leg", class_5606.method_32108().method_32101(48, 21).method_32098(-1.0f, -1.01f, -1.0f, 4.0f, 11.0f, 4.0f, lv6), class_5603.method_32090(-4.0f, 14.0f, 7.0f));
        lv2.method_32117("left_front_baby_leg", class_5606.method_32108().method_32101(48, 21).method_32096().method_32098(-3.0f, -1.01f, -1.9f, 4.0f, 11.0f, 4.0f, lv6), class_5603.method_32090(4.0f, 14.0f, -12.0f));
        lv2.method_32117("right_front_baby_leg", class_5606.method_32108().method_32101(48, 21).method_32098(-1.0f, -1.01f, -1.9f, 4.0f, 11.0f, 4.0f, lv6), class_5603.method_32090(-4.0f, 14.0f, -12.0f));
        lv3.method_32117("tail", class_5606.method_32108().method_32101(42, 36).method_32098(-1.5f, 0.0f, 0.0f, 3.0f, 14.0f, 4.0f, arg), class_5603.method_32091(0.0f, -5.0f, 2.0f, 0.5235988f, 0.0f, 0.0f));
        lv3.method_32117("saddle", class_5606.method_32108().method_32101(26, 0).method_32098(-5.0f, -8.0f, -9.0f, 10.0f, 9.0f, 9.0f, new class_5605(0.5f)), class_5603.field_27701);
        lv4.method_32117("left_saddle_mouth", class_5606.method_32108().method_32101(29, 5).method_32098(2.0f, -9.0f, -6.0f, 1.0f, 2.0f, 2.0f, arg), class_5603.field_27701);
        lv4.method_32117("right_saddle_mouth", class_5606.method_32108().method_32101(29, 5).method_32098(-3.0f, -9.0f, -6.0f, 1.0f, 2.0f, 2.0f, arg), class_5603.field_27701);
        lv4.method_32117("left_saddle_line", class_5606.method_32108().method_32101(32, 2).method_32098(3.1f, -6.0f, -8.0f, 0.0f, 3.0f, 16.0f, arg), class_5603.method_32092(-0.5235988f, 0.0f, 0.0f));
        lv4.method_32117("right_saddle_line", class_5606.method_32108().method_32101(32, 2).method_32098(-3.1f, -6.0f, -8.0f, 0.0f, 3.0f, 16.0f, arg), class_5603.method_32092(-0.5235988f, 0.0f, 0.0f));
        lv4.method_32117("head_saddle", class_5606.method_32108().method_32101(1, 1).method_32098(-3.0f, -11.0f, -1.9f, 6.0f, 5.0f, 6.0f, new class_5605(0.2f)), class_5603.field_27701);
        lv4.method_32117("mouth_saddle_wrap", class_5606.method_32108().method_32101(19, 0).method_32098(-2.0f, -11.0f, -4.0f, 4.0f, 5.0f, 2.0f, new class_5605(0.2f)), class_5603.field_27701);
        lv5.method_32117("left_ear", class_5606.method_32108().method_32101(19, 16).method_32098(0.55f, -13.0f, 4.0f, 2.0f, 3.0f, 1.0f, new class_5605(-0.001f)), class_5603.field_27701);
        lv5.method_32117("right_ear", class_5606.method_32108().method_32101(19, 16).method_32098(-2.55f, -13.0f, 4.0f, 2.0f, 3.0f, 1.0f, new class_5605(-0.001f)), class_5603.field_27701);
        return lv;
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
        this.torso.pivotY = 11.0f;
    }

    @Override
    public Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of(this.head);
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of(this.torso, this.field_27425, this.field_27426, this.field_27427, this.field_27428, this.field_27429, this.field_27430, this.field_27431, this.field_27432);
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
        this.torso.pitch = 0.0f;
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
        this.torso.pitch = o * -0.7853982f + p * this.torso.pitch;
        float w = 0.2617994f * o;
        float x = MathHelper.cos(r * 0.6f + (float)Math.PI);
        this.field_27428.pivotY = 2.0f * o + 14.0f * p;
        this.field_27428.pivotZ = -6.0f * o - 10.0f * p;
        this.field_27427.pivotY = this.field_27428.pivotY;
        this.field_27427.pivotZ = this.field_27428.pivotZ;
        float y = (-1.0471976f + x) * o + u * p;
        float z = (-1.0471976f - x) * o - u * p;
        this.field_27426.pitch = w - t * 0.5f * g * p;
        this.field_27425.pitch = w + t * 0.5f * g * p;
        this.field_27428.pitch = y;
        this.field_27427.pitch = z;
        this.tail.pitch = 0.5235988f + g * 0.75f;
        this.tail.pivotY = -5.0f + g;
        this.tail.pivotZ = 2.0f + g * 2.0f;
        this.tail.yaw = bl ? MathHelper.cos(r * 0.7f) : 0.0f;
        this.field_27429.pivotY = this.field_27425.pivotY;
        this.field_27429.pivotZ = this.field_27425.pivotZ;
        this.field_27429.pitch = this.field_27425.pitch;
        this.field_27430.pivotY = this.field_27426.pivotY;
        this.field_27430.pivotZ = this.field_27426.pivotZ;
        this.field_27430.pitch = this.field_27426.pitch;
        this.field_27431.pivotY = this.field_27427.pivotY;
        this.field_27431.pivotZ = this.field_27427.pivotZ;
        this.field_27431.pitch = this.field_27427.pitch;
        this.field_27432.pivotY = this.field_27428.pivotY;
        this.field_27432.pivotZ = this.field_27428.pivotZ;
        this.field_27432.pitch = this.field_27428.pitch;
        boolean bl2 = ((PassiveEntity)horseBaseEntity).isBaby();
        this.field_27425.visible = !bl2;
        this.field_27426.visible = !bl2;
        this.field_27427.visible = !bl2;
        this.field_27428.visible = !bl2;
        this.field_27429.visible = bl2;
        this.field_27430.visible = bl2;
        this.field_27431.visible = bl2;
        this.field_27432.visible = bl2;
        this.torso.pivotY = bl2 ? 10.8f : 0.0f;
    }
}

