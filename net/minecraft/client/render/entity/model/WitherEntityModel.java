/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.Dilation;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class WitherEntityModel<T extends WitherEntity>
extends SinglePartEntityModel<T> {
    private final ModelPart field_27532;
    private final ModelPart field_27533;
    private final ModelPart field_27534;
    private final ModelPart field_27535;
    private final ModelPart field_27536;
    private final ModelPart field_27537;

    public WitherEntityModel(ModelPart modelPart) {
        this.field_27532 = modelPart;
        this.field_27536 = modelPart.getChild("ribcage");
        this.field_27537 = modelPart.getChild("tail");
        this.field_27533 = modelPart.getChild("center_head");
        this.field_27534 = modelPart.getChild("right_head");
        this.field_27535 = modelPart.getChild("left_head");
    }

    public static TexturedModelData getTexturedModelData(Dilation dilation) {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("shoulders", ModelPartBuilder.create().uv(0, 16).cuboid(-10.0f, 3.9f, -0.5f, 20.0f, 3.0f, 3.0f, dilation), ModelTransform.NONE);
        float f = 0.20420352f;
        modelPartData.addChild("ribcage", ModelPartBuilder.create().uv(0, 22).cuboid(0.0f, 0.0f, 0.0f, 3.0f, 10.0f, 3.0f, dilation).uv(24, 22).cuboid(-4.0f, 1.5f, 0.5f, 11.0f, 2.0f, 2.0f, dilation).uv(24, 22).cuboid(-4.0f, 4.0f, 0.5f, 11.0f, 2.0f, 2.0f, dilation).uv(24, 22).cuboid(-4.0f, 6.5f, 0.5f, 11.0f, 2.0f, 2.0f, dilation), ModelTransform.of(-2.0f, 6.9f, -0.5f, 0.20420352f, 0.0f, 0.0f));
        modelPartData.addChild("tail", ModelPartBuilder.create().uv(12, 22).cuboid(0.0f, 0.0f, 0.0f, 3.0f, 6.0f, 3.0f, dilation), ModelTransform.of(-2.0f, 6.9f + MathHelper.cos(0.20420352f) * 10.0f, -0.5f + MathHelper.sin(0.20420352f) * 10.0f, 0.83252203f, 0.0f, 0.0f));
        modelPartData.addChild("center_head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f, dilation), ModelTransform.NONE);
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(32, 0).cuboid(-4.0f, -4.0f, -4.0f, 6.0f, 6.0f, 6.0f, dilation);
        modelPartData.addChild("right_head", modelPartBuilder, ModelTransform.pivot(-8.0f, 4.0f, 0.0f));
        modelPartData.addChild("left_head", modelPartBuilder, ModelTransform.pivot(10.0f, 4.0f, 0.0f));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public ModelPart getPart() {
        return this.field_27532;
    }

    @Override
    public void setAngles(T witherEntity, float f, float g, float h, float i, float j) {
        float k = MathHelper.cos(h * 0.1f);
        this.field_27536.pitch = (0.065f + 0.05f * k) * (float)Math.PI;
        this.field_27537.setPivot(-2.0f, 6.9f + MathHelper.cos(this.field_27536.pitch) * 10.0f, -0.5f + MathHelper.sin(this.field_27536.pitch) * 10.0f);
        this.field_27537.pitch = (0.265f + 0.1f * k) * (float)Math.PI;
        this.field_27533.yaw = i * ((float)Math.PI / 180);
        this.field_27533.pitch = j * ((float)Math.PI / 180);
    }

    @Override
    public void animateModel(T witherEntity, float f, float g, float h) {
        WitherEntityModel.method_32066(witherEntity, this.field_27534, 0);
        WitherEntityModel.method_32066(witherEntity, this.field_27535, 1);
    }

    private static <T extends WitherEntity> void method_32066(T witherEntity, ModelPart modelPart, int i) {
        modelPart.yaw = (witherEntity.getHeadYaw(i) - witherEntity.bodyYaw) * ((float)Math.PI / 180);
        modelPart.pitch = witherEntity.getHeadPitch(i) * ((float)Math.PI / 180);
    }
}

