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
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class LlamaEntityModel<T extends AbstractDonkeyEntity>
extends EntityModel<T> {
    private final ModelPart field_27443;
    private final ModelPart field_27444;
    private final ModelPart field_27445;
    private final ModelPart field_27446;
    private final ModelPart field_27447;
    private final ModelPart field_27448;
    private final ModelPart field_27449;
    private final ModelPart field_27450;

    public LlamaEntityModel(ModelPart modelPart) {
        this.field_27443 = modelPart.method_32086("head");
        this.field_27444 = modelPart.method_32086("body");
        this.field_27449 = modelPart.method_32086("right_chest");
        this.field_27450 = modelPart.method_32086("left_chest");
        this.field_27445 = modelPart.method_32086("right_hind_leg");
        this.field_27446 = modelPart.method_32086("left_hind_leg");
        this.field_27447 = modelPart.method_32086("right_front_leg");
        this.field_27448 = modelPart.method_32086("left_front_leg");
    }

    public static class_5607 method_32018(class_5605 arg) {
        class_5609 lv = new class_5609();
        class_5610 lv2 = lv.method_32111();
        lv2.method_32117("head", class_5606.method_32108().method_32101(0, 0).method_32098(-2.0f, -14.0f, -10.0f, 4.0f, 4.0f, 9.0f, arg).method_32101(0, 14).method_32103("neck", -4.0f, -16.0f, -6.0f, 8.0f, 18.0f, 6.0f, arg).method_32101(17, 0).method_32103("ear", -4.0f, -19.0f, -4.0f, 3.0f, 3.0f, 2.0f, arg).method_32101(17, 0).method_32103("ear", 1.0f, -19.0f, -4.0f, 3.0f, 3.0f, 2.0f, arg), class_5603.method_32090(0.0f, 7.0f, -6.0f));
        lv2.method_32117("body", class_5606.method_32108().method_32101(29, 0).method_32098(-6.0f, -10.0f, -7.0f, 12.0f, 18.0f, 10.0f, arg), class_5603.method_32091(0.0f, 5.0f, 2.0f, 1.5707964f, 0.0f, 0.0f));
        lv2.method_32117("right_chest", class_5606.method_32108().method_32101(45, 28).method_32098(-3.0f, 0.0f, 0.0f, 8.0f, 8.0f, 3.0f, arg), class_5603.method_32091(-8.5f, 3.0f, 3.0f, 0.0f, 1.5707964f, 0.0f));
        lv2.method_32117("left_chest", class_5606.method_32108().method_32101(45, 41).method_32098(-3.0f, 0.0f, 0.0f, 8.0f, 8.0f, 3.0f, arg), class_5603.method_32091(5.5f, 3.0f, 3.0f, 0.0f, 1.5707964f, 0.0f));
        int i = 4;
        int j = 14;
        class_5606 lv3 = class_5606.method_32108().method_32101(29, 29).method_32098(-2.0f, 0.0f, -2.0f, 4.0f, 14.0f, 4.0f, arg);
        lv2.method_32117("right_hind_leg", lv3, class_5603.method_32090(-3.5f, 10.0f, 6.0f));
        lv2.method_32117("left_hind_leg", lv3, class_5603.method_32090(3.5f, 10.0f, 6.0f));
        lv2.method_32117("right_front_leg", lv3, class_5603.method_32090(-3.5f, 10.0f, -5.0f));
        lv2.method_32117("left_front_leg", lv3, class_5603.method_32090(3.5f, 10.0f, -5.0f));
        return class_5607.method_32110(lv, 128, 64);
    }

    @Override
    public void setAngles(T abstractDonkeyEntity, float f, float g, float h, float i, float j) {
        boolean bl;
        this.field_27443.pitch = j * ((float)Math.PI / 180);
        this.field_27443.yaw = i * ((float)Math.PI / 180);
        this.field_27445.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g;
        this.field_27446.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
        this.field_27447.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
        this.field_27448.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g;
        this.field_27449.visible = bl = !((PassiveEntity)abstractDonkeyEntity).isBaby() && ((AbstractDonkeyEntity)abstractDonkeyEntity).hasChest();
        this.field_27450.visible = bl;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        if (this.child) {
            float f = 2.0f;
            matrices.push();
            float g = 0.7f;
            matrices.scale(0.71428573f, 0.64935064f, 0.7936508f);
            matrices.translate(0.0, 1.3125, 0.22f);
            this.field_27443.render(matrices, vertices, light, overlay, red, green, blue, alpha);
            matrices.pop();
            matrices.push();
            float h = 1.1f;
            matrices.scale(0.625f, 0.45454544f, 0.45454544f);
            matrices.translate(0.0, 2.0625, 0.0);
            this.field_27444.render(matrices, vertices, light, overlay, red, green, blue, alpha);
            matrices.pop();
            matrices.push();
            matrices.scale(0.45454544f, 0.41322312f, 0.45454544f);
            matrices.translate(0.0, 2.0625, 0.0);
            ImmutableList.of(this.field_27445, this.field_27446, this.field_27447, this.field_27448, this.field_27449, this.field_27450).forEach(modelPart -> modelPart.render(matrices, vertices, light, overlay, red, green, blue, alpha));
            matrices.pop();
        } else {
            ImmutableList.of(this.field_27443, this.field_27444, this.field_27445, this.field_27446, this.field_27447, this.field_27448, this.field_27449, this.field_27450).forEach(modelPart -> modelPart.render(matrices, vertices, light, overlay, red, green, blue, alpha));
        }
    }
}

