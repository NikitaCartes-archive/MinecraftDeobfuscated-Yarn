/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5603;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelUtil;
import net.minecraft.client.render.entity.model.QuadrupedEntityModel;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class PandaEntityModel<T extends PandaEntity>
extends QuadrupedEntityModel<T> {
    private float scaredAnimationProgress;
    private float lieOnBackAnimationProgress;
    private float playAnimationProgress;

    public PandaEntityModel(ModelPart modelPart) {
        super(modelPart, true, 23.0f, 4.8f, 2.7f, 3.0f, 49);
    }

    public static class_5607 method_32022() {
        class_5609 lv = new class_5609();
        class_5610 lv2 = lv.method_32111();
        lv2.method_32117("head", class_5606.method_32108().method_32101(0, 6).method_32097(-6.5f, -5.0f, -4.0f, 13.0f, 10.0f, 9.0f).method_32101(45, 16).method_32102("nose", -3.5f, 0.0f, -6.0f, 7.0f, 5.0f, 2.0f).method_32101(52, 25).method_32102("left_ear", 3.5f, -8.0f, -1.0f, 5.0f, 4.0f, 1.0f).method_32101(52, 25).method_32102("right_ear", -8.5f, -8.0f, -1.0f, 5.0f, 4.0f, 1.0f), class_5603.method_32090(0.0f, 11.5f, -17.0f));
        lv2.method_32117("body", class_5606.method_32108().method_32101(0, 25).method_32097(-9.5f, -13.0f, -6.5f, 19.0f, 26.0f, 13.0f), class_5603.method_32091(0.0f, 10.0f, 0.0f, 1.5707964f, 0.0f, 0.0f));
        int i = 9;
        int j = 6;
        class_5606 lv3 = class_5606.method_32108().method_32101(40, 0).method_32097(-3.0f, 0.0f, -3.0f, 6.0f, 9.0f, 6.0f);
        lv2.method_32117("right_hind_leg", lv3, class_5603.method_32090(-5.5f, 15.0f, 9.0f));
        lv2.method_32117("left_hind_leg", lv3, class_5603.method_32090(5.5f, 15.0f, 9.0f));
        lv2.method_32117("right_front_leg", lv3, class_5603.method_32090(-5.5f, 15.0f, -9.0f));
        lv2.method_32117("left_front_leg", lv3, class_5603.method_32090(5.5f, 15.0f, -9.0f));
        return class_5607.method_32110(lv, 64, 64);
    }

    @Override
    public void animateModel(T pandaEntity, float f, float g, float h) {
        super.animateModel(pandaEntity, f, g, h);
        this.scaredAnimationProgress = ((PandaEntity)pandaEntity).getScaredAnimationProgress(h);
        this.lieOnBackAnimationProgress = ((PandaEntity)pandaEntity).getLieOnBackAnimationProgress(h);
        this.playAnimationProgress = ((PassiveEntity)pandaEntity).isBaby() ? 0.0f : ((PandaEntity)pandaEntity).getRollOverAnimationProgress(h);
    }

    @Override
    public void setAngles(T pandaEntity, float f, float g, float h, float i, float j) {
        super.setAngles(pandaEntity, f, g, h, i, j);
        boolean bl = ((PandaEntity)pandaEntity).getAskForBambooTicks() > 0;
        boolean bl2 = ((PandaEntity)pandaEntity).isSneezing();
        int k = ((PandaEntity)pandaEntity).getSneezeProgress();
        boolean bl3 = ((PandaEntity)pandaEntity).isEating();
        boolean bl4 = ((PandaEntity)pandaEntity).isScaredByThunderstorm();
        if (bl) {
            this.head.yaw = 0.35f * MathHelper.sin(0.6f * h);
            this.head.roll = 0.35f * MathHelper.sin(0.6f * h);
            this.field_27478.pitch = -0.75f * MathHelper.sin(0.3f * h);
            this.field_27479.pitch = 0.75f * MathHelper.sin(0.3f * h);
        } else {
            this.head.roll = 0.0f;
        }
        if (bl2) {
            if (k < 15) {
                this.head.pitch = -0.7853982f * (float)k / 14.0f;
            } else if (k < 20) {
                float l = (k - 15) / 5;
                this.head.pitch = -0.7853982f + 0.7853982f * l;
            }
        }
        if (this.scaredAnimationProgress > 0.0f) {
            this.torso.pitch = ModelUtil.interpolateAngle(this.torso.pitch, 1.7407963f, this.scaredAnimationProgress);
            this.head.pitch = ModelUtil.interpolateAngle(this.head.pitch, 1.5707964f, this.scaredAnimationProgress);
            this.field_27478.roll = -0.27079642f;
            this.field_27479.roll = 0.27079642f;
            this.field_27476.roll = 0.5707964f;
            this.field_27477.roll = -0.5707964f;
            if (bl3) {
                this.head.pitch = 1.5707964f + 0.2f * MathHelper.sin(h * 0.6f);
                this.field_27478.pitch = -0.4f - 0.2f * MathHelper.sin(h * 0.6f);
                this.field_27479.pitch = -0.4f - 0.2f * MathHelper.sin(h * 0.6f);
            }
            if (bl4) {
                this.head.pitch = 2.1707964f;
                this.field_27478.pitch = -0.9f;
                this.field_27479.pitch = -0.9f;
            }
        } else {
            this.field_27476.roll = 0.0f;
            this.field_27477.roll = 0.0f;
            this.field_27478.roll = 0.0f;
            this.field_27479.roll = 0.0f;
        }
        if (this.lieOnBackAnimationProgress > 0.0f) {
            this.field_27476.pitch = -0.6f * MathHelper.sin(h * 0.15f);
            this.field_27477.pitch = 0.6f * MathHelper.sin(h * 0.15f);
            this.field_27478.pitch = 0.3f * MathHelper.sin(h * 0.25f);
            this.field_27479.pitch = -0.3f * MathHelper.sin(h * 0.25f);
            this.head.pitch = ModelUtil.interpolateAngle(this.head.pitch, 1.5707964f, this.lieOnBackAnimationProgress);
        }
        if (this.playAnimationProgress > 0.0f) {
            this.head.pitch = ModelUtil.interpolateAngle(this.head.pitch, 2.0561945f, this.playAnimationProgress);
            this.field_27476.pitch = -0.5f * MathHelper.sin(h * 0.5f);
            this.field_27477.pitch = 0.5f * MathHelper.sin(h * 0.5f);
            this.field_27478.pitch = 0.5f * MathHelper.sin(h * 0.5f);
            this.field_27479.pitch = -0.5f * MathHelper.sin(h * 0.5f);
        }
    }
}

