/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5597;
import net.minecraft.class_5603;
import net.minecraft.class_5605;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class IronGolemEntityModel<T extends IronGolemEntity>
extends class_5597<T> {
    private final ModelPart field_27436;
    private final ModelPart head;
    private final ModelPart field_27437;
    private final ModelPart field_27438;
    private final ModelPart field_27439;
    private final ModelPart field_27440;

    public IronGolemEntityModel(ModelPart modelPart) {
        this.field_27436 = modelPart;
        this.head = modelPart.method_32086("head");
        this.field_27437 = modelPart.method_32086("right_arm");
        this.field_27438 = modelPart.method_32086("left_arm");
        this.field_27439 = modelPart.method_32086("right_leg");
        this.field_27440 = modelPart.method_32086("left_leg");
    }

    public static class_5607 method_32013() {
        class_5609 lv = new class_5609();
        class_5610 lv2 = lv.method_32111();
        lv2.method_32117("head", class_5606.method_32108().method_32101(0, 0).method_32097(-4.0f, -12.0f, -5.5f, 8.0f, 10.0f, 8.0f).method_32101(24, 0).method_32097(-1.0f, -5.0f, -7.5f, 2.0f, 4.0f, 2.0f), class_5603.method_32090(0.0f, -7.0f, -2.0f));
        lv2.method_32117("body", class_5606.method_32108().method_32101(0, 40).method_32097(-9.0f, -2.0f, -6.0f, 18.0f, 12.0f, 11.0f).method_32101(0, 70).method_32098(-4.5f, 10.0f, -3.0f, 9.0f, 5.0f, 6.0f, new class_5605(0.5f)), class_5603.method_32090(0.0f, -7.0f, 0.0f));
        lv2.method_32117("right_arm", class_5606.method_32108().method_32101(60, 21).method_32097(-13.0f, -2.5f, -3.0f, 4.0f, 30.0f, 6.0f), class_5603.method_32090(0.0f, -7.0f, 0.0f));
        lv2.method_32117("left_arm", class_5606.method_32108().method_32101(60, 58).method_32097(9.0f, -2.5f, -3.0f, 4.0f, 30.0f, 6.0f), class_5603.method_32090(0.0f, -7.0f, 0.0f));
        lv2.method_32117("right_leg", class_5606.method_32108().method_32101(37, 0).method_32097(-3.5f, -3.0f, -3.0f, 6.0f, 16.0f, 5.0f), class_5603.method_32090(-4.0f, 11.0f, 0.0f));
        lv2.method_32117("left_leg", class_5606.method_32108().method_32101(60, 0).method_32096().method_32097(-3.5f, -3.0f, -3.0f, 6.0f, 16.0f, 5.0f), class_5603.method_32090(5.0f, 11.0f, 0.0f));
        return class_5607.method_32110(lv, 128, 128);
    }

    @Override
    public ModelPart method_32008() {
        return this.field_27436;
    }

    @Override
    public void setAngles(T ironGolemEntity, float f, float g, float h, float i, float j) {
        this.head.yaw = i * ((float)Math.PI / 180);
        this.head.pitch = j * ((float)Math.PI / 180);
        this.field_27439.pitch = -1.5f * MathHelper.method_24504(f, 13.0f) * g;
        this.field_27440.pitch = 1.5f * MathHelper.method_24504(f, 13.0f) * g;
        this.field_27439.yaw = 0.0f;
        this.field_27440.yaw = 0.0f;
    }

    @Override
    public void animateModel(T ironGolemEntity, float f, float g, float h) {
        int i = ((IronGolemEntity)ironGolemEntity).getAttackTicksLeft();
        if (i > 0) {
            this.field_27437.pitch = -2.0f + 1.5f * MathHelper.method_24504((float)i - h, 10.0f);
            this.field_27438.pitch = -2.0f + 1.5f * MathHelper.method_24504((float)i - h, 10.0f);
        } else {
            int j = ((IronGolemEntity)ironGolemEntity).getLookingAtVillagerTicks();
            if (j > 0) {
                this.field_27437.pitch = -0.8f + 0.025f * MathHelper.method_24504(j, 70.0f);
                this.field_27438.pitch = 0.0f;
            } else {
                this.field_27437.pitch = (-0.2f + 1.5f * MathHelper.method_24504(f, 13.0f)) * g;
                this.field_27438.pitch = (-0.2f - 1.5f * MathHelper.method_24504(f, 13.0f)) * g;
            }
        }
    }

    public ModelPart getRightArm() {
        return this.field_27437;
    }
}

