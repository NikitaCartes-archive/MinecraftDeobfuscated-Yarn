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
public class CodEntityModel<T extends Entity>
extends SinglePartEntityModel<T> {
    private final ModelPart field_27405;
    private final ModelPart tail;

    public CodEntityModel(ModelPart modelPart) {
        this.field_27405 = modelPart;
        this.tail = modelPart.getChild("tail_fin");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        int i = 22;
        modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0f, -2.0f, 0.0f, 2.0f, 4.0f, 7.0f), ModelTransform.pivot(0.0f, 22.0f, 0.0f));
        modelPartData.addChild("head", ModelPartBuilder.create().uv(11, 0).cuboid(-1.0f, -2.0f, -3.0f, 2.0f, 4.0f, 3.0f), ModelTransform.pivot(0.0f, 22.0f, 0.0f));
        modelPartData.addChild("nose", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0f, -2.0f, -1.0f, 2.0f, 3.0f, 1.0f), ModelTransform.pivot(0.0f, 22.0f, -3.0f));
        modelPartData.addChild("right_fin", ModelPartBuilder.create().uv(22, 1).cuboid(-2.0f, 0.0f, -1.0f, 2.0f, 0.0f, 2.0f), ModelTransform.of(-1.0f, 23.0f, 0.0f, 0.0f, 0.0f, -0.7853982f));
        modelPartData.addChild("left_fin", ModelPartBuilder.create().uv(22, 4).cuboid(0.0f, 0.0f, -1.0f, 2.0f, 0.0f, 2.0f), ModelTransform.of(1.0f, 23.0f, 0.0f, 0.0f, 0.0f, 0.7853982f));
        modelPartData.addChild("tail_fin", ModelPartBuilder.create().uv(22, 3).cuboid(0.0f, -2.0f, 0.0f, 0.0f, 4.0f, 4.0f), ModelTransform.pivot(0.0f, 22.0f, 7.0f));
        modelPartData.addChild("top_fin", ModelPartBuilder.create().uv(20, -6).cuboid(0.0f, -1.0f, -1.0f, 0.0f, 1.0f, 6.0f), ModelTransform.pivot(0.0f, 20.0f, 0.0f));
        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public ModelPart getPart() {
        return this.field_27405;
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

