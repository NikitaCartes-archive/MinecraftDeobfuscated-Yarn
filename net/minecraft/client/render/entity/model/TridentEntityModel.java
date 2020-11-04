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
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class TridentEntityModel
extends Model {
    public static final Identifier TEXTURE = new Identifier("textures/entity/trident.png");
    private final ModelPart field_27521;

    public TridentEntityModel(ModelPart modelPart) {
        super(RenderLayer::getEntitySolid);
        this.field_27521 = modelPart;
    }

    public static class_5607 method_32059() {
        class_5609 lv = new class_5609();
        class_5610 lv2 = lv.method_32111();
        class_5610 lv3 = lv2.method_32117("pole", class_5606.method_32108().method_32101(0, 6).method_32097(-0.5f, 2.0f, -0.5f, 1.0f, 25.0f, 1.0f), class_5603.field_27701);
        lv3.method_32117("base", class_5606.method_32108().method_32101(4, 0).method_32097(-1.5f, 0.0f, -0.5f, 3.0f, 2.0f, 1.0f), class_5603.field_27701);
        lv3.method_32117("left_spike", class_5606.method_32108().method_32101(4, 3).method_32097(-2.5f, -3.0f, -0.5f, 1.0f, 4.0f, 1.0f), class_5603.field_27701);
        lv3.method_32117("middle_spike", class_5606.method_32108().method_32101(0, 0).method_32097(-0.5f, -4.0f, -0.5f, 1.0f, 4.0f, 1.0f), class_5603.field_27701);
        lv3.method_32117("right_spike", class_5606.method_32108().method_32101(4, 3).method_32096().method_32097(1.5f, -3.0f, -0.5f, 1.0f, 4.0f, 1.0f), class_5603.field_27701);
        return class_5607.method_32110(lv, 32, 32);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.field_27521.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}

