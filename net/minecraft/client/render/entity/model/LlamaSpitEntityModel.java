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

@Environment(value=EnvType.CLIENT)
public class LlamaSpitEntityModel<T extends Entity>
extends SinglePartEntityModel<T> {
    private final ModelPart root;

    public LlamaSpitEntityModel(ModelPart root) {
        this.root = root;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        int i = 2;
        modelPartData.addChild("main", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, 0.0f, 0.0f, 2.0f, 2.0f, 2.0f).cuboid(0.0f, -4.0f, 0.0f, 2.0f, 2.0f, 2.0f).cuboid(0.0f, 0.0f, -4.0f, 2.0f, 2.0f, 2.0f).cuboid(0.0f, 0.0f, 0.0f, 2.0f, 2.0f, 2.0f).cuboid(2.0f, 0.0f, 0.0f, 2.0f, 2.0f, 2.0f).cuboid(0.0f, 2.0f, 0.0f, 2.0f, 2.0f, 2.0f).cuboid(0.0f, 0.0f, 2.0f, 2.0f, 2.0f, 2.0f), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }
}

