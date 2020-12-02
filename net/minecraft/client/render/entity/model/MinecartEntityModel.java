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
public class MinecartEntityModel<T extends Entity>
extends SinglePartEntityModel<T> {
    private final ModelPart root;
    private final ModelPart contents;

    public MinecartEntityModel(ModelPart root) {
        this.root = root;
        this.contents = root.getChild("contents");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        int i = 20;
        int j = 8;
        int k = 16;
        int l = 4;
        modelPartData.addChild("bottom", ModelPartBuilder.create().uv(0, 10).cuboid(-10.0f, -8.0f, -1.0f, 20.0f, 16.0f, 2.0f), ModelTransform.of(0.0f, 4.0f, 0.0f, 1.5707964f, 0.0f, 0.0f));
        modelPartData.addChild("front", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0f, -9.0f, -1.0f, 16.0f, 8.0f, 2.0f), ModelTransform.of(-9.0f, 4.0f, 0.0f, 0.0f, 4.712389f, 0.0f));
        modelPartData.addChild("back", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0f, -9.0f, -1.0f, 16.0f, 8.0f, 2.0f), ModelTransform.of(9.0f, 4.0f, 0.0f, 0.0f, 1.5707964f, 0.0f));
        modelPartData.addChild("left", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0f, -9.0f, -1.0f, 16.0f, 8.0f, 2.0f), ModelTransform.of(0.0f, 4.0f, -7.0f, 0.0f, (float)Math.PI, 0.0f));
        modelPartData.addChild("right", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0f, -9.0f, -1.0f, 16.0f, 8.0f, 2.0f), ModelTransform.pivot(0.0f, 4.0f, 7.0f));
        modelPartData.addChild("contents", ModelPartBuilder.create().uv(44, 10).cuboid(-9.0f, -7.0f, -1.0f, 18.0f, 14.0f, 1.0f), ModelTransform.of(0.0f, 4.0f, 0.0f, -1.5707964f, 0.0f, 0.0f));
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.contents.pivotY = 4.0f - animationProgress;
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }
}

