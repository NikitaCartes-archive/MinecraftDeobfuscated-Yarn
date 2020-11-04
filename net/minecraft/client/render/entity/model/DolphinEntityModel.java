/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5597;
import net.minecraft.class_5603;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class DolphinEntityModel<T extends Entity>
extends class_5597<T> {
    private final ModelPart field_27411;
    private final ModelPart body;
    private final ModelPart tail;
    private final ModelPart flukes;

    public DolphinEntityModel(ModelPart modelPart) {
        this.field_27411 = modelPart;
        this.body = modelPart.method_32086("body");
        this.tail = this.body.method_32086("tail");
        this.flukes = this.tail.method_32086("tail_fin");
    }

    public static class_5607 method_31992() {
        class_5609 lv = new class_5609();
        class_5610 lv2 = lv.method_32111();
        float f = 18.0f;
        float g = -8.0f;
        class_5610 lv3 = lv2.method_32117("body", class_5606.method_32108().method_32101(22, 0).method_32097(-4.0f, -7.0f, 0.0f, 8.0f, 7.0f, 13.0f), class_5603.method_32090(0.0f, 22.0f, -5.0f));
        lv3.method_32117("back_fin", class_5606.method_32108().method_32101(51, 0).method_32097(-0.5f, 0.0f, 8.0f, 1.0f, 4.0f, 5.0f), class_5603.method_32092(1.0471976f, 0.0f, 0.0f));
        lv3.method_32117("left_fin", class_5606.method_32108().method_32101(48, 20).method_32096().method_32097(-0.5f, -4.0f, 0.0f, 1.0f, 4.0f, 7.0f), class_5603.method_32091(2.0f, -2.0f, 4.0f, 1.0471976f, 0.0f, 2.0943952f));
        lv3.method_32117("right_fin", class_5606.method_32108().method_32101(48, 20).method_32097(-0.5f, -4.0f, 0.0f, 1.0f, 4.0f, 7.0f), class_5603.method_32091(-2.0f, -2.0f, 4.0f, 1.0471976f, 0.0f, -2.0943952f));
        class_5610 lv4 = lv3.method_32117("tail", class_5606.method_32108().method_32101(0, 19).method_32097(-2.0f, -2.5f, 0.0f, 4.0f, 5.0f, 11.0f), class_5603.method_32091(0.0f, -2.5f, 11.0f, -0.10471976f, 0.0f, 0.0f));
        lv4.method_32117("tail_fin", class_5606.method_32108().method_32101(19, 20).method_32097(-5.0f, -0.5f, 0.0f, 10.0f, 1.0f, 6.0f), class_5603.method_32090(0.0f, 0.0f, 9.0f));
        class_5610 lv5 = lv3.method_32117("head", class_5606.method_32108().method_32101(0, 0).method_32097(-4.0f, -3.0f, -3.0f, 8.0f, 7.0f, 6.0f), class_5603.method_32090(0.0f, -4.0f, -3.0f));
        lv5.method_32117("nose", class_5606.method_32108().method_32101(0, 13).method_32097(-1.0f, 2.0f, -7.0f, 2.0f, 2.0f, 4.0f), class_5603.field_27701);
        return class_5607.method_32110(lv, 64, 64);
    }

    @Override
    public ModelPart method_32008() {
        return this.field_27411;
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.body.pitch = headPitch * ((float)Math.PI / 180);
        this.body.yaw = headYaw * ((float)Math.PI / 180);
        if (Entity.squaredHorizontalLength(((Entity)entity).getVelocity()) > 1.0E-7) {
            this.body.pitch += -0.05f - 0.05f * MathHelper.cos(animationProgress * 0.3f);
            this.tail.pitch = -0.1f * MathHelper.cos(animationProgress * 0.3f);
            this.flukes.pitch = -0.2f * MathHelper.cos(animationProgress * 0.3f);
        }
    }
}

