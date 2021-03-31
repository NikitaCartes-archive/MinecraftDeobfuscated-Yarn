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
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.entity.Entity;

@Environment(value=EnvType.CLIENT)
public class SlimeEntityModel<T extends Entity>
extends SinglePartEntityModel<T> {
    private final ModelPart root;

    public SlimeEntityModel(ModelPart root) {
        this.root = root;
    }

    public static TexturedModelData getOuterTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(EntityModelPartNames.CUBE, ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, 16.0f, -4.0f, 8.0f, 8.0f, 8.0f), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 64, 32);
    }

    public static TexturedModelData getInnerTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(EntityModelPartNames.CUBE, ModelPartBuilder.create().uv(0, 16).cuboid(-3.0f, 17.0f, -3.0f, 6.0f, 6.0f, 6.0f), ModelTransform.NONE);
        modelPartData.addChild(EntityModelPartNames.RIGHT_EYE, ModelPartBuilder.create().uv(32, 0).cuboid(-3.25f, 18.0f, -3.5f, 2.0f, 2.0f, 2.0f), ModelTransform.NONE);
        modelPartData.addChild(EntityModelPartNames.LEFT_EYE, ModelPartBuilder.create().uv(32, 4).cuboid(1.25f, 18.0f, -3.5f, 2.0f, 2.0f, 2.0f), ModelTransform.NONE);
        modelPartData.addChild(EntityModelPartNames.MOUTH, ModelPartBuilder.create().uv(32, 8).cuboid(0.0f, 21.0f, -3.5f, 1.0f, 1.0f, 1.0f), ModelTransform.NONE);
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

