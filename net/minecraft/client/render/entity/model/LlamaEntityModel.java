/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.Dilation;
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
        this.field_27443 = modelPart.getChild("head");
        this.field_27444 = modelPart.getChild("body");
        this.field_27449 = modelPart.getChild("right_chest");
        this.field_27450 = modelPart.getChild("left_chest");
        this.field_27445 = modelPart.getChild("right_hind_leg");
        this.field_27446 = modelPart.getChild("left_hind_leg");
        this.field_27447 = modelPart.getChild("right_front_leg");
        this.field_27448 = modelPart.getChild("left_front_leg");
    }

    public static TexturedModelData getTexturedModelData(Dilation dilation) {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-2.0f, -14.0f, -10.0f, 4.0f, 4.0f, 9.0f, dilation).uv(0, 14).cuboid("neck", -4.0f, -16.0f, -6.0f, 8.0f, 18.0f, 6.0f, dilation).uv(17, 0).cuboid("ear", -4.0f, -19.0f, -4.0f, 3.0f, 3.0f, 2.0f, dilation).uv(17, 0).cuboid("ear", 1.0f, -19.0f, -4.0f, 3.0f, 3.0f, 2.0f, dilation), ModelTransform.pivot(0.0f, 7.0f, -6.0f));
        modelPartData.addChild("body", ModelPartBuilder.create().uv(29, 0).cuboid(-6.0f, -10.0f, -7.0f, 12.0f, 18.0f, 10.0f, dilation), ModelTransform.of(0.0f, 5.0f, 2.0f, 1.5707964f, 0.0f, 0.0f));
        modelPartData.addChild("right_chest", ModelPartBuilder.create().uv(45, 28).cuboid(-3.0f, 0.0f, 0.0f, 8.0f, 8.0f, 3.0f, dilation), ModelTransform.of(-8.5f, 3.0f, 3.0f, 0.0f, 1.5707964f, 0.0f));
        modelPartData.addChild("left_chest", ModelPartBuilder.create().uv(45, 41).cuboid(-3.0f, 0.0f, 0.0f, 8.0f, 8.0f, 3.0f, dilation), ModelTransform.of(5.5f, 3.0f, 3.0f, 0.0f, 1.5707964f, 0.0f));
        int i = 4;
        int j = 14;
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(29, 29).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 14.0f, 4.0f, dilation);
        modelPartData.addChild("right_hind_leg", modelPartBuilder, ModelTransform.pivot(-3.5f, 10.0f, 6.0f));
        modelPartData.addChild("left_hind_leg", modelPartBuilder, ModelTransform.pivot(3.5f, 10.0f, 6.0f));
        modelPartData.addChild("right_front_leg", modelPartBuilder, ModelTransform.pivot(-3.5f, 10.0f, -5.0f));
        modelPartData.addChild("left_front_leg", modelPartBuilder, ModelTransform.pivot(3.5f, 10.0f, -5.0f));
        return TexturedModelData.of(modelData, 128, 64);
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

