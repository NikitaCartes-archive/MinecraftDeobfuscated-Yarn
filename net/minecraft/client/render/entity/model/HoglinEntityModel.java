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
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Hoglin;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class HoglinEntityModel<T extends MobEntity>
extends AnimalModel<T> {
    private final ModelPart head;
    private final ModelPart rightEar;
    private final ModelPart leftEar;
    private final ModelPart torso;
    private final ModelPart field_27421;
    private final ModelPart field_27422;
    private final ModelPart field_27423;
    private final ModelPart field_27424;
    private final ModelPart field_25484;

    public HoglinEntityModel(ModelPart modelPart) {
        super(true, 8.0f, 6.0f, 1.9f, 2.0f, 24.0f);
        this.torso = modelPart.method_32086("body");
        this.field_25484 = this.torso.method_32086("mane");
        this.head = modelPart.method_32086("head");
        this.rightEar = this.head.method_32086("right_ear");
        this.leftEar = this.head.method_32086("left_ear");
        this.field_27421 = modelPart.method_32086("right_front_leg");
        this.field_27422 = modelPart.method_32086("left_front_leg");
        this.field_27423 = modelPart.method_32086("right_hind_leg");
        this.field_27424 = modelPart.method_32086("left_hind_leg");
    }

    public static class_5607 method_32009() {
        class_5609 lv = new class_5609();
        class_5610 lv2 = lv.method_32111();
        class_5610 lv3 = lv2.method_32117("body", class_5606.method_32108().method_32101(1, 1).method_32097(-8.0f, -7.0f, -13.0f, 16.0f, 14.0f, 26.0f), class_5603.method_32090(0.0f, 7.0f, 0.0f));
        lv3.method_32117("mane", class_5606.method_32108().method_32101(90, 33).method_32098(0.0f, 0.0f, -9.0f, 0.0f, 10.0f, 19.0f, new class_5605(0.001f)), class_5603.method_32090(0.0f, -14.0f, -5.0f));
        class_5610 lv4 = lv2.method_32117("head", class_5606.method_32108().method_32101(61, 1).method_32097(-7.0f, -3.0f, -19.0f, 14.0f, 6.0f, 19.0f), class_5603.method_32091(0.0f, 2.0f, -12.0f, 0.87266463f, 0.0f, 0.0f));
        lv4.method_32117("right_ear", class_5606.method_32108().method_32101(1, 1).method_32097(-6.0f, -1.0f, -2.0f, 6.0f, 1.0f, 4.0f), class_5603.method_32091(-6.0f, -2.0f, -3.0f, 0.0f, 0.0f, -0.6981317f));
        lv4.method_32117("left_ear", class_5606.method_32108().method_32101(1, 6).method_32097(0.0f, -1.0f, -2.0f, 6.0f, 1.0f, 4.0f), class_5603.method_32091(6.0f, -2.0f, -3.0f, 0.0f, 0.0f, 0.6981317f));
        lv4.method_32117("right_horn", class_5606.method_32108().method_32101(10, 13).method_32097(-1.0f, -11.0f, -1.0f, 2.0f, 11.0f, 2.0f), class_5603.method_32090(-7.0f, 2.0f, -12.0f));
        lv4.method_32117("left_horn", class_5606.method_32108().method_32101(1, 13).method_32097(-1.0f, -11.0f, -1.0f, 2.0f, 11.0f, 2.0f), class_5603.method_32090(7.0f, 2.0f, -12.0f));
        int i = 14;
        int j = 11;
        lv2.method_32117("right_front_leg", class_5606.method_32108().method_32101(66, 42).method_32097(-3.0f, 0.0f, -3.0f, 6.0f, 14.0f, 6.0f), class_5603.method_32090(-4.0f, 10.0f, -8.5f));
        lv2.method_32117("left_front_leg", class_5606.method_32108().method_32101(41, 42).method_32097(-3.0f, 0.0f, -3.0f, 6.0f, 14.0f, 6.0f), class_5603.method_32090(4.0f, 10.0f, -8.5f));
        lv2.method_32117("right_hind_leg", class_5606.method_32108().method_32101(21, 45).method_32097(-2.5f, 0.0f, -2.5f, 5.0f, 11.0f, 5.0f), class_5603.method_32090(-5.0f, 13.0f, 10.0f));
        lv2.method_32117("left_hind_leg", class_5606.method_32108().method_32101(0, 45).method_32097(-2.5f, 0.0f, -2.5f, 5.0f, 11.0f, 5.0f), class_5603.method_32090(5.0f, 13.0f, 10.0f));
        return class_5607.method_32110(lv, 128, 64);
    }

    @Override
    protected Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of(this.head);
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of(this.torso, this.field_27421, this.field_27422, this.field_27423, this.field_27424);
    }

    @Override
    public void setAngles(T mobEntity, float f, float g, float h, float i, float j) {
        this.rightEar.roll = -0.6981317f - g * MathHelper.sin(f);
        this.leftEar.roll = 0.6981317f + g * MathHelper.sin(f);
        this.head.yaw = i * ((float)Math.PI / 180);
        int k = ((Hoglin)mobEntity).getMovementCooldownTicks();
        float l = 1.0f - (float)MathHelper.abs(10 - 2 * k) / 10.0f;
        this.head.pitch = MathHelper.lerp(l, 0.87266463f, -0.34906584f);
        if (((LivingEntity)mobEntity).isBaby()) {
            this.head.pivotY = MathHelper.lerp(l, 2.0f, 5.0f);
            this.field_25484.pivotZ = -3.0f;
        } else {
            this.head.pivotY = 2.0f;
            this.field_25484.pivotZ = -7.0f;
        }
        float m = 1.2f;
        this.field_27421.pitch = MathHelper.cos(f) * 1.2f * g;
        this.field_27423.pitch = this.field_27422.pitch = MathHelper.cos(f + (float)Math.PI) * 1.2f * g;
        this.field_27424.pitch = this.field_27421.pitch;
    }
}

