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
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class RabbitEntityModel<T extends RabbitEntity>
extends EntityModel<T> {
    private final ModelPart field_27480;
    private final ModelPart field_27481;
    private final ModelPart field_27482;
    private final ModelPart field_27483;
    private final ModelPart torso;
    private final ModelPart field_27484;
    private final ModelPart field_27485;
    private final ModelPart field_27486;
    private final ModelPart field_27487;
    private final ModelPart field_27488;
    private final ModelPart tail;
    private final ModelPart nose;
    private float field_3531;

    public RabbitEntityModel(ModelPart modelPart) {
        this.field_27480 = modelPart.method_32086("left_hind_foot");
        this.field_27481 = modelPart.method_32086("right_hind_foot");
        this.field_27482 = modelPart.method_32086("left_haunch");
        this.field_27483 = modelPart.method_32086("right_haunch");
        this.torso = modelPart.method_32086("body");
        this.field_27484 = modelPart.method_32086("left_front_leg");
        this.field_27485 = modelPart.method_32086("right_front_leg");
        this.field_27486 = modelPart.method_32086("head");
        this.field_27487 = modelPart.method_32086("right_ear");
        this.field_27488 = modelPart.method_32086("left_ear");
        this.tail = modelPart.method_32086("tail");
        this.nose = modelPart.method_32086("nose");
    }

    public static class_5607 method_32034() {
        class_5609 lv = new class_5609();
        class_5610 lv2 = lv.method_32111();
        lv2.method_32117("left_hind_foot", class_5606.method_32108().method_32101(26, 24).method_32097(-1.0f, 5.5f, -3.7f, 2.0f, 1.0f, 7.0f), class_5603.method_32090(3.0f, 17.5f, 3.7f));
        lv2.method_32117("right_hind_foot", class_5606.method_32108().method_32101(8, 24).method_32097(-1.0f, 5.5f, -3.7f, 2.0f, 1.0f, 7.0f), class_5603.method_32090(-3.0f, 17.5f, 3.7f));
        lv2.method_32117("left_haunch", class_5606.method_32108().method_32101(30, 15).method_32097(-1.0f, 0.0f, 0.0f, 2.0f, 4.0f, 5.0f), class_5603.method_32091(3.0f, 17.5f, 3.7f, -0.34906584f, 0.0f, 0.0f));
        lv2.method_32117("right_haunch", class_5606.method_32108().method_32101(16, 15).method_32097(-1.0f, 0.0f, 0.0f, 2.0f, 4.0f, 5.0f), class_5603.method_32091(-3.0f, 17.5f, 3.7f, -0.34906584f, 0.0f, 0.0f));
        lv2.method_32117("body", class_5606.method_32108().method_32101(0, 0).method_32097(-3.0f, -2.0f, -10.0f, 6.0f, 5.0f, 10.0f), class_5603.method_32091(0.0f, 19.0f, 8.0f, -0.34906584f, 0.0f, 0.0f));
        lv2.method_32117("left_front_leg", class_5606.method_32108().method_32101(8, 15).method_32097(-1.0f, 0.0f, -1.0f, 2.0f, 7.0f, 2.0f), class_5603.method_32091(3.0f, 17.0f, -1.0f, -0.17453292f, 0.0f, 0.0f));
        lv2.method_32117("right_front_leg", class_5606.method_32108().method_32101(0, 15).method_32097(-1.0f, 0.0f, -1.0f, 2.0f, 7.0f, 2.0f), class_5603.method_32091(-3.0f, 17.0f, -1.0f, -0.17453292f, 0.0f, 0.0f));
        lv2.method_32117("head", class_5606.method_32108().method_32101(32, 0).method_32097(-2.5f, -4.0f, -5.0f, 5.0f, 4.0f, 5.0f), class_5603.method_32090(0.0f, 16.0f, -1.0f));
        lv2.method_32117("right_ear", class_5606.method_32108().method_32101(52, 0).method_32097(-2.5f, -9.0f, -1.0f, 2.0f, 5.0f, 1.0f), class_5603.method_32091(0.0f, 16.0f, -1.0f, 0.0f, -0.2617994f, 0.0f));
        lv2.method_32117("left_ear", class_5606.method_32108().method_32101(58, 0).method_32097(0.5f, -9.0f, -1.0f, 2.0f, 5.0f, 1.0f), class_5603.method_32091(0.0f, 16.0f, -1.0f, 0.0f, 0.2617994f, 0.0f));
        lv2.method_32117("tail", class_5606.method_32108().method_32101(52, 6).method_32097(-1.5f, -1.5f, 0.0f, 3.0f, 3.0f, 2.0f), class_5603.method_32091(0.0f, 20.0f, 7.0f, -0.3490659f, 0.0f, 0.0f));
        lv2.method_32117("nose", class_5606.method_32108().method_32101(32, 9).method_32097(-0.5f, -2.5f, -5.5f, 1.0f, 1.0f, 1.0f), class_5603.method_32090(0.0f, 16.0f, -1.0f));
        return class_5607.method_32110(lv, 64, 32);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        if (this.child) {
            float f = 1.5f;
            matrices.push();
            matrices.scale(0.56666666f, 0.56666666f, 0.56666666f);
            matrices.translate(0.0, 1.375, 0.125);
            ImmutableList.of(this.field_27486, this.field_27488, this.field_27487, this.nose).forEach(modelPart -> modelPart.render(matrices, vertices, light, overlay, red, green, blue, alpha));
            matrices.pop();
            matrices.push();
            matrices.scale(0.4f, 0.4f, 0.4f);
            matrices.translate(0.0, 2.25, 0.0);
            ImmutableList.of(this.field_27480, this.field_27481, this.field_27482, this.field_27483, this.torso, this.field_27484, this.field_27485, this.tail).forEach(modelPart -> modelPart.render(matrices, vertices, light, overlay, red, green, blue, alpha));
            matrices.pop();
        } else {
            matrices.push();
            matrices.scale(0.6f, 0.6f, 0.6f);
            matrices.translate(0.0, 1.0, 0.0);
            ImmutableList.of(this.field_27480, this.field_27481, this.field_27482, this.field_27483, this.torso, this.field_27484, this.field_27485, this.field_27486, this.field_27487, this.field_27488, this.tail, this.nose, new ModelPart[0]).forEach(modelPart -> modelPart.render(matrices, vertices, light, overlay, red, green, blue, alpha));
            matrices.pop();
        }
    }

    @Override
    public void setAngles(T rabbitEntity, float f, float g, float h, float i, float j) {
        float k = h - (float)((RabbitEntity)rabbitEntity).age;
        this.nose.pitch = j * ((float)Math.PI / 180);
        this.field_27486.pitch = j * ((float)Math.PI / 180);
        this.field_27487.pitch = j * ((float)Math.PI / 180);
        this.field_27488.pitch = j * ((float)Math.PI / 180);
        this.nose.yaw = i * ((float)Math.PI / 180);
        this.field_27486.yaw = i * ((float)Math.PI / 180);
        this.field_27487.yaw = this.nose.yaw - 0.2617994f;
        this.field_27488.yaw = this.nose.yaw + 0.2617994f;
        this.field_3531 = MathHelper.sin(((RabbitEntity)rabbitEntity).getJumpProgress(k) * (float)Math.PI);
        this.field_27482.pitch = (this.field_3531 * 50.0f - 21.0f) * ((float)Math.PI / 180);
        this.field_27483.pitch = (this.field_3531 * 50.0f - 21.0f) * ((float)Math.PI / 180);
        this.field_27480.pitch = this.field_3531 * 50.0f * ((float)Math.PI / 180);
        this.field_27481.pitch = this.field_3531 * 50.0f * ((float)Math.PI / 180);
        this.field_27484.pitch = (this.field_3531 * -40.0f - 11.0f) * ((float)Math.PI / 180);
        this.field_27485.pitch = (this.field_3531 * -40.0f - 11.0f) * ((float)Math.PI / 180);
    }

    @Override
    public void animateModel(T rabbitEntity, float f, float g, float h) {
        super.animateModel(rabbitEntity, f, g, h);
        this.field_3531 = MathHelper.sin(((RabbitEntity)rabbitEntity).getJumpProgress(h) * (float)Math.PI);
    }
}

