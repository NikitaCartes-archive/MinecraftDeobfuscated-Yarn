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
public class SalmonEntityModel<T extends Entity>
extends SinglePartEntityModel<T> {
    private final ModelPart field_27494;
    private final ModelPart tail;

    public SalmonEntityModel(ModelPart modelPart) {
        this.field_27494 = modelPart;
        this.tail = modelPart.getChild("body_back");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        int i = 20;
        ModelPartData modelPartData2 = modelPartData.addChild("body_front", ModelPartBuilder.create().uv(0, 0).cuboid(-1.5f, -2.5f, 0.0f, 3.0f, 5.0f, 8.0f), ModelTransform.pivot(0.0f, 20.0f, 0.0f));
        ModelPartData modelPartData3 = modelPartData.addChild("body_back", ModelPartBuilder.create().uv(0, 13).cuboid(-1.5f, -2.5f, 0.0f, 3.0f, 5.0f, 8.0f), ModelTransform.pivot(0.0f, 20.0f, 8.0f));
        modelPartData.addChild("head", ModelPartBuilder.create().uv(22, 0).cuboid(-1.0f, -2.0f, -3.0f, 2.0f, 4.0f, 3.0f), ModelTransform.pivot(0.0f, 20.0f, 0.0f));
        modelPartData3.addChild("back_fin", ModelPartBuilder.create().uv(20, 10).cuboid(0.0f, -2.5f, 0.0f, 0.0f, 5.0f, 6.0f), ModelTransform.pivot(0.0f, 0.0f, 8.0f));
        modelPartData2.addChild("top_front_fin", ModelPartBuilder.create().uv(2, 1).cuboid(0.0f, 0.0f, 0.0f, 0.0f, 2.0f, 3.0f), ModelTransform.pivot(0.0f, -4.5f, 5.0f));
        modelPartData3.addChild("top_back_fin", ModelPartBuilder.create().uv(0, 2).cuboid(0.0f, 0.0f, 0.0f, 0.0f, 2.0f, 4.0f), ModelTransform.pivot(0.0f, -4.5f, -1.0f));
        modelPartData.addChild("right_fin", ModelPartBuilder.create().uv(-4, 0).cuboid(-2.0f, 0.0f, 0.0f, 2.0f, 0.0f, 2.0f), ModelTransform.of(-1.5f, 21.5f, 0.0f, 0.0f, 0.0f, -0.7853982f));
        modelPartData.addChild("left_fin", ModelPartBuilder.create().uv(0, 0).cuboid(0.0f, 0.0f, 0.0f, 2.0f, 0.0f, 2.0f), ModelTransform.of(1.5f, 21.5f, 0.0f, 0.0f, 0.0f, 0.7853982f));
        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public ModelPart getPart() {
        return this.field_27494;
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        float f = 1.0f;
        float g = 1.0f;
        if (!((Entity)entity).isTouchingWater()) {
            f = 1.3f;
            g = 1.7f;
        }
        this.tail.yaw = -f * 0.25f * MathHelper.sin(g * 0.6f * animationProgress);
    }
}

