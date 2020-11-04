/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5603;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ChickenEntityModel<T extends Entity>
extends AnimalModel<T> {
    private final ModelPart head;
    private final ModelPart torso;
    private final ModelPart field_27401;
    private final ModelPart field_27402;
    private final ModelPart field_27403;
    private final ModelPart field_27404;
    private final ModelPart beak;
    private final ModelPart wattle;

    public ChickenEntityModel(ModelPart modelPart) {
        this.head = modelPart.method_32086("head");
        this.beak = modelPart.method_32086("beak");
        this.wattle = modelPart.method_32086("red_thing");
        this.torso = modelPart.method_32086("body");
        this.field_27401 = modelPart.method_32086("right_leg");
        this.field_27402 = modelPart.method_32086("left_leg");
        this.field_27403 = modelPart.method_32086("right_wing");
        this.field_27404 = modelPart.method_32086("left_wing");
    }

    public static class_5607 method_31988() {
        class_5609 lv = new class_5609();
        class_5610 lv2 = lv.method_32111();
        int i = 16;
        lv2.method_32117("head", class_5606.method_32108().method_32101(0, 0).method_32097(-2.0f, -6.0f, -2.0f, 4.0f, 6.0f, 3.0f), class_5603.method_32090(0.0f, 15.0f, -4.0f));
        lv2.method_32117("beak", class_5606.method_32108().method_32101(14, 0).method_32097(-2.0f, -4.0f, -4.0f, 4.0f, 2.0f, 2.0f), class_5603.method_32090(0.0f, 15.0f, -4.0f));
        lv2.method_32117("red_thing", class_5606.method_32108().method_32101(14, 4).method_32097(-1.0f, -2.0f, -3.0f, 2.0f, 2.0f, 2.0f), class_5603.method_32090(0.0f, 15.0f, -4.0f));
        lv2.method_32117("body", class_5606.method_32108().method_32101(0, 9).method_32097(-3.0f, -4.0f, -3.0f, 6.0f, 8.0f, 6.0f), class_5603.method_32091(0.0f, 16.0f, 0.0f, 1.5707964f, 0.0f, 0.0f));
        class_5606 lv3 = class_5606.method_32108().method_32101(26, 0).method_32097(-1.0f, 0.0f, -3.0f, 3.0f, 5.0f, 3.0f);
        lv2.method_32117("right_leg", lv3, class_5603.method_32090(-2.0f, 19.0f, 1.0f));
        lv2.method_32117("left_leg", lv3, class_5603.method_32090(1.0f, 19.0f, 1.0f));
        lv2.method_32117("right_wing", class_5606.method_32108().method_32101(24, 13).method_32097(0.0f, 0.0f, -3.0f, 1.0f, 4.0f, 6.0f), class_5603.method_32090(-4.0f, 13.0f, 0.0f));
        lv2.method_32117("left_wing", class_5606.method_32108().method_32101(24, 13).method_32097(-1.0f, 0.0f, -3.0f, 1.0f, 4.0f, 6.0f), class_5603.method_32090(4.0f, 13.0f, 0.0f));
        return class_5607.method_32110(lv, 64, 32);
    }

    @Override
    protected Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of(this.head, this.beak, this.wattle);
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of(this.torso, this.field_27401, this.field_27402, this.field_27403, this.field_27404);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.head.pitch = headPitch * ((float)Math.PI / 180);
        this.head.yaw = headYaw * ((float)Math.PI / 180);
        this.beak.pitch = this.head.pitch;
        this.beak.yaw = this.head.yaw;
        this.wattle.pitch = this.head.pitch;
        this.wattle.yaw = this.head.yaw;
        this.field_27401.pitch = MathHelper.cos(limbAngle * 0.6662f) * 1.4f * limbDistance;
        this.field_27402.pitch = MathHelper.cos(limbAngle * 0.6662f + (float)Math.PI) * 1.4f * limbDistance;
        this.field_27403.roll = animationProgress;
        this.field_27404.roll = -animationProgress;
    }
}

