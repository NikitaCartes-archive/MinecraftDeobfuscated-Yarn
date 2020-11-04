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
import net.minecraft.client.render.entity.model.TintableAnimalModel;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class WolfEntityModel<T extends WolfEntity>
extends TintableAnimalModel<T> {
    private final ModelPart head;
    private final ModelPart field_20788;
    private final ModelPart torso;
    private final ModelPart field_27538;
    private final ModelPart field_27539;
    private final ModelPart field_27540;
    private final ModelPart field_27541;
    private final ModelPart tail;
    private final ModelPart field_20789;
    private final ModelPart neck;

    public WolfEntityModel(ModelPart modelPart) {
        this.head = modelPart.method_32086("head");
        this.field_20788 = this.head.method_32086("real_head");
        this.torso = modelPart.method_32086("body");
        this.neck = modelPart.method_32086("upper_body");
        this.field_27538 = modelPart.method_32086("right_hind_leg");
        this.field_27539 = modelPart.method_32086("left_hind_leg");
        this.field_27540 = modelPart.method_32086("right_front_leg");
        this.field_27541 = modelPart.method_32086("left_front_leg");
        this.tail = modelPart.method_32086("tail");
        this.field_20789 = this.tail.method_32086("real_tail");
    }

    public static class_5607 method_32068() {
        class_5609 lv = new class_5609();
        class_5610 lv2 = lv.method_32111();
        float f = 13.5f;
        class_5610 lv3 = lv2.method_32117("head", class_5606.method_32108(), class_5603.method_32090(-1.0f, 13.5f, -7.0f));
        lv3.method_32117("real_head", class_5606.method_32108().method_32101(0, 0).method_32097(-2.0f, -3.0f, -2.0f, 6.0f, 6.0f, 4.0f).method_32101(16, 14).method_32097(-2.0f, -5.0f, 0.0f, 2.0f, 2.0f, 1.0f).method_32101(16, 14).method_32097(2.0f, -5.0f, 0.0f, 2.0f, 2.0f, 1.0f).method_32101(0, 10).method_32097(-0.5f, 0.0f, -5.0f, 3.0f, 3.0f, 4.0f), class_5603.field_27701);
        lv2.method_32117("body", class_5606.method_32108().method_32101(18, 14).method_32097(-3.0f, -2.0f, -3.0f, 6.0f, 9.0f, 6.0f), class_5603.method_32091(0.0f, 14.0f, 2.0f, 1.5707964f, 0.0f, 0.0f));
        lv2.method_32117("upper_body", class_5606.method_32108().method_32101(21, 0).method_32097(-3.0f, -3.0f, -3.0f, 8.0f, 6.0f, 7.0f), class_5603.method_32091(-1.0f, 14.0f, -3.0f, 1.5707964f, 0.0f, 0.0f));
        class_5606 lv4 = class_5606.method_32108().method_32101(0, 18).method_32097(0.0f, 0.0f, -1.0f, 2.0f, 8.0f, 2.0f);
        lv2.method_32117("right_hind_leg", lv4, class_5603.method_32090(-2.5f, 16.0f, 7.0f));
        lv2.method_32117("left_hind_leg", lv4, class_5603.method_32090(0.5f, 16.0f, 7.0f));
        lv2.method_32117("right_front_leg", lv4, class_5603.method_32090(-2.5f, 16.0f, -4.0f));
        lv2.method_32117("left_front_leg", lv4, class_5603.method_32090(0.5f, 16.0f, -4.0f));
        class_5610 lv5 = lv2.method_32117("tail", class_5606.method_32108(), class_5603.method_32091(-1.0f, 12.0f, 8.0f, 0.62831855f, 0.0f, 0.0f));
        lv5.method_32117("real_tail", class_5606.method_32108().method_32101(9, 18).method_32097(0.0f, 0.0f, -1.0f, 2.0f, 8.0f, 2.0f), class_5603.field_27701);
        return class_5607.method_32110(lv, 64, 32);
    }

    @Override
    protected Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of(this.head);
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of(this.torso, this.field_27538, this.field_27539, this.field_27540, this.field_27541, this.tail, this.neck);
    }

    @Override
    public void animateModel(T wolfEntity, float f, float g, float h) {
        this.tail.yaw = wolfEntity.hasAngerTime() ? 0.0f : MathHelper.cos(f * 0.6662f) * 1.4f * g;
        if (((TameableEntity)wolfEntity).isInSittingPose()) {
            this.neck.setPivot(-1.0f, 16.0f, -3.0f);
            this.neck.pitch = 1.2566371f;
            this.neck.yaw = 0.0f;
            this.torso.setPivot(0.0f, 18.0f, 0.0f);
            this.torso.pitch = 0.7853982f;
            this.tail.setPivot(-1.0f, 21.0f, 6.0f);
            this.field_27538.setPivot(-2.5f, 22.7f, 2.0f);
            this.field_27538.pitch = 4.712389f;
            this.field_27539.setPivot(0.5f, 22.7f, 2.0f);
            this.field_27539.pitch = 4.712389f;
            this.field_27540.pitch = 5.811947f;
            this.field_27540.setPivot(-2.49f, 17.0f, -4.0f);
            this.field_27541.pitch = 5.811947f;
            this.field_27541.setPivot(0.51f, 17.0f, -4.0f);
        } else {
            this.torso.setPivot(0.0f, 14.0f, 2.0f);
            this.torso.pitch = 1.5707964f;
            this.neck.setPivot(-1.0f, 14.0f, -3.0f);
            this.neck.pitch = this.torso.pitch;
            this.tail.setPivot(-1.0f, 12.0f, 8.0f);
            this.field_27538.setPivot(-2.5f, 16.0f, 7.0f);
            this.field_27539.setPivot(0.5f, 16.0f, 7.0f);
            this.field_27540.setPivot(-2.5f, 16.0f, -4.0f);
            this.field_27541.setPivot(0.5f, 16.0f, -4.0f);
            this.field_27538.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g;
            this.field_27539.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
            this.field_27540.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
            this.field_27541.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g;
        }
        this.field_20788.roll = ((WolfEntity)wolfEntity).getBegAnimationProgress(h) + ((WolfEntity)wolfEntity).getShakeAnimationProgress(h, 0.0f);
        this.neck.roll = ((WolfEntity)wolfEntity).getShakeAnimationProgress(h, -0.08f);
        this.torso.roll = ((WolfEntity)wolfEntity).getShakeAnimationProgress(h, -0.16f);
        this.field_20789.roll = ((WolfEntity)wolfEntity).getShakeAnimationProgress(h, -0.2f);
    }

    @Override
    public void setAngles(T wolfEntity, float f, float g, float h, float i, float j) {
        this.head.pitch = j * ((float)Math.PI / 180);
        this.head.yaw = i * ((float)Math.PI / 180);
        this.tail.pitch = h;
    }
}

