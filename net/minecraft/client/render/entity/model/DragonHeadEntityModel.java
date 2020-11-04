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
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public class DragonHeadEntityModel
extends SkullBlockEntityModel {
    private final ModelPart head;
    private final ModelPart jaw;

    public DragonHeadEntityModel(ModelPart modelPart) {
        this.head = modelPart.method_32086("head");
        this.jaw = this.head.method_32086("jaw");
    }

    public static class_5607 method_32071() {
        class_5609 lv = new class_5609();
        class_5610 lv2 = lv.method_32111();
        float f = -16.0f;
        class_5610 lv3 = lv2.method_32117("head", class_5606.method_32108().method_32104("upper_lip", -6.0f, -1.0f, -24.0f, 12, 5, 16, 176, 44).method_32104("upper_head", -8.0f, -8.0f, -10.0f, 16, 16, 16, 112, 30).method_32106(true).method_32104("scale", -5.0f, -12.0f, -4.0f, 2, 4, 6, 0, 0).method_32104("nostril", -5.0f, -3.0f, -22.0f, 2, 2, 4, 112, 0).method_32106(false).method_32104("scale", 3.0f, -12.0f, -4.0f, 2, 4, 6, 0, 0).method_32104("nostril", 3.0f, -3.0f, -22.0f, 2, 2, 4, 112, 0), class_5603.field_27701);
        lv3.method_32117("jaw", class_5606.method_32108().method_32101(176, 65).method_32102("jaw", -6.0f, 0.0f, -16.0f, 12.0f, 4.0f, 16.0f), class_5603.method_32090(0.0f, 4.0f, -8.0f));
        return class_5607.method_32110(lv, 256, 256);
    }

    @Override
    public void method_2821(float f, float g, float h) {
        this.jaw.pitch = (float)(Math.sin(f * (float)Math.PI * 0.2f) + 1.0) * 0.2f;
        this.head.yaw = g * ((float)Math.PI / 180);
        this.head.pitch = h * ((float)Math.PI / 180);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        matrices.push();
        matrices.translate(0.0, -0.374375f, 0.0);
        matrices.scale(0.75f, 0.75f, 0.75f);
        this.head.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        matrices.pop();
    }
}

