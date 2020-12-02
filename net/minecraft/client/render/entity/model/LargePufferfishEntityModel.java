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
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class LargePufferfishEntityModel<T extends Entity>
extends SinglePartEntityModel<T> {
    private final ModelPart root;
    private final ModelPart leftBlueFin;
    private final ModelPart rightBlueFin;

    public LargePufferfishEntityModel(ModelPart root) {
        this.root = root;
        this.leftBlueFin = root.getChild("left_blue_fin");
        this.rightBlueFin = root.getChild("right_blue_fin");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        int i = 22;
        modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f), ModelTransform.pivot(0.0f, 22.0f, 0.0f));
        modelPartData.addChild("right_blue_fin", ModelPartBuilder.create().uv(24, 0).cuboid(-2.0f, 0.0f, -1.0f, 2.0f, 1.0f, 2.0f), ModelTransform.pivot(-4.0f, 15.0f, -2.0f));
        modelPartData.addChild("left_blue_fin", ModelPartBuilder.create().uv(24, 3).cuboid(0.0f, 0.0f, -1.0f, 2.0f, 1.0f, 2.0f), ModelTransform.pivot(4.0f, 15.0f, -2.0f));
        modelPartData.addChild("top_front_fin", ModelPartBuilder.create().uv(15, 17).cuboid(-4.0f, -1.0f, 0.0f, 8.0f, 1.0f, 0.0f), ModelTransform.of(0.0f, 14.0f, -4.0f, 0.7853982f, 0.0f, 0.0f));
        modelPartData.addChild("top_middle_fin", ModelPartBuilder.create().uv(14, 16).cuboid(-4.0f, -1.0f, 0.0f, 8.0f, 1.0f, 1.0f), ModelTransform.pivot(0.0f, 14.0f, 0.0f));
        modelPartData.addChild("top_back_fin", ModelPartBuilder.create().uv(23, 18).cuboid(-4.0f, -1.0f, 0.0f, 8.0f, 1.0f, 0.0f), ModelTransform.of(0.0f, 14.0f, 4.0f, -0.7853982f, 0.0f, 0.0f));
        modelPartData.addChild("right_front_fin", ModelPartBuilder.create().uv(5, 17).cuboid(-1.0f, -8.0f, 0.0f, 1.0f, 8.0f, 0.0f), ModelTransform.of(-4.0f, 22.0f, -4.0f, 0.0f, -0.7853982f, 0.0f));
        modelPartData.addChild("left_front_fin", ModelPartBuilder.create().uv(1, 17).cuboid(0.0f, -8.0f, 0.0f, 1.0f, 8.0f, 0.0f), ModelTransform.of(4.0f, 22.0f, -4.0f, 0.0f, 0.7853982f, 0.0f));
        modelPartData.addChild("bottom_front_fin", ModelPartBuilder.create().uv(15, 20).cuboid(-4.0f, 0.0f, 0.0f, 8.0f, 1.0f, 0.0f), ModelTransform.of(0.0f, 22.0f, -4.0f, -0.7853982f, 0.0f, 0.0f));
        modelPartData.addChild("bottom_middle_fin", ModelPartBuilder.create().uv(15, 20).cuboid(-4.0f, 0.0f, 0.0f, 8.0f, 1.0f, 0.0f), ModelTransform.pivot(0.0f, 22.0f, 0.0f));
        modelPartData.addChild("bottom_back_fin", ModelPartBuilder.create().uv(15, 20).cuboid(-4.0f, 0.0f, 0.0f, 8.0f, 1.0f, 0.0f), ModelTransform.of(0.0f, 22.0f, 4.0f, 0.7853982f, 0.0f, 0.0f));
        modelPartData.addChild("right_back_fin", ModelPartBuilder.create().uv(9, 17).cuboid(-1.0f, -8.0f, 0.0f, 1.0f, 8.0f, 0.0f), ModelTransform.of(-4.0f, 22.0f, 4.0f, 0.0f, 0.7853982f, 0.0f));
        modelPartData.addChild("left_back_fin", ModelPartBuilder.create().uv(9, 17).cuboid(0.0f, -8.0f, 0.0f, 1.0f, 8.0f, 0.0f), ModelTransform.of(4.0f, 22.0f, 4.0f, 0.0f, -0.7853982f, 0.0f));
        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.rightBlueFin.roll = -0.2f + 0.4f * MathHelper.sin(animationProgress * 0.2f);
        this.leftBlueFin.roll = 0.2f - 0.4f * MathHelper.sin(animationProgress * 0.2f);
    }
}

