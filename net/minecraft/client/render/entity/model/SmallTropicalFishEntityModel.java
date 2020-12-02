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
import net.minecraft.client.render.entity.model.TintableCompositeModel;
import net.minecraft.client.util.math.Dilation;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class SmallTropicalFishEntityModel<T extends Entity>
extends TintableCompositeModel<T> {
    private final ModelPart root;
    private final ModelPart tail;

    public SmallTropicalFishEntityModel(ModelPart root) {
        this.root = root;
        this.tail = root.getChild("tail");
    }

    public static TexturedModelData getTexturedModelData(Dilation dilation) {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        int i = 22;
        modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0f, -1.5f, -3.0f, 2.0f, 3.0f, 6.0f, dilation), ModelTransform.pivot(0.0f, 22.0f, 0.0f));
        modelPartData.addChild("tail", ModelPartBuilder.create().uv(22, -6).cuboid(0.0f, -1.5f, 0.0f, 0.0f, 3.0f, 6.0f, dilation), ModelTransform.pivot(0.0f, 22.0f, 3.0f));
        modelPartData.addChild("right_fin", ModelPartBuilder.create().uv(2, 16).cuboid(-2.0f, -1.0f, 0.0f, 2.0f, 2.0f, 0.0f, dilation), ModelTransform.of(-1.0f, 22.5f, 0.0f, 0.0f, 0.7853982f, 0.0f));
        modelPartData.addChild("left_fin", ModelPartBuilder.create().uv(2, 12).cuboid(0.0f, -1.0f, 0.0f, 2.0f, 2.0f, 0.0f, dilation), ModelTransform.of(1.0f, 22.5f, 0.0f, 0.0f, -0.7853982f, 0.0f));
        modelPartData.addChild("top_fin", ModelPartBuilder.create().uv(10, -5).cuboid(0.0f, -3.0f, 0.0f, 0.0f, 3.0f, 6.0f, dilation), ModelTransform.pivot(0.0f, 20.5f, -3.0f));
        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        float f = 1.0f;
        if (!((Entity)entity).isTouchingWater()) {
            f = 1.5f;
        }
        this.tail.yaw = -f * 0.45f * MathHelper.sin(0.6f * animationProgress);
    }
}

