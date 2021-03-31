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
import net.minecraft.client.render.entity.model.QuadrupedEntityModel;
import net.minecraft.entity.Entity;

@Environment(value=EnvType.CLIENT)
public class CowEntityModel<T extends Entity>
extends QuadrupedEntityModel<T> {
    public CowEntityModel(ModelPart root) {
        super(root, false, 10.0f, 4.0f, 2.0f, 2.0f, 24);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        int i = 12;
        modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -4.0f, -6.0f, 8.0f, 8.0f, 6.0f).uv(22, 0).cuboid(EntityModelPartNames.RIGHT_HORN, -5.0f, -5.0f, -4.0f, 1.0f, 3.0f, 1.0f).uv(22, 0).cuboid(EntityModelPartNames.LEFT_HORN, 4.0f, -5.0f, -4.0f, 1.0f, 3.0f, 1.0f), ModelTransform.pivot(0.0f, 4.0f, -8.0f));
        modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(18, 4).cuboid(-6.0f, -10.0f, -7.0f, 12.0f, 18.0f, 10.0f).uv(52, 0).cuboid(-2.0f, 2.0f, -8.0f, 4.0f, 6.0f, 1.0f), ModelTransform.of(0.0f, 5.0f, 2.0f, 1.5707964f, 0.0f, 0.0f));
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(0, 16).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f);
        modelPartData.addChild(EntityModelPartNames.RIGHT_HIND_LEG, modelPartBuilder, ModelTransform.pivot(-4.0f, 12.0f, 7.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_HIND_LEG, modelPartBuilder, ModelTransform.pivot(4.0f, 12.0f, 7.0f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_FRONT_LEG, modelPartBuilder, ModelTransform.pivot(-4.0f, 12.0f, -6.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_FRONT_LEG, modelPartBuilder, ModelTransform.pivot(4.0f, 12.0f, -6.0f));
        return TexturedModelData.of(modelData, 64, 32);
    }

    public ModelPart getHead() {
        return this.head;
    }
}

