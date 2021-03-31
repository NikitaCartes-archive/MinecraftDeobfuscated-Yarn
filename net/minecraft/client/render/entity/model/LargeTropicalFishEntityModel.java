/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.TintableCompositeModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class LargeTropicalFishEntityModel<T extends Entity>
extends TintableCompositeModel<T> {
    private final ModelPart root;
    private final ModelPart tail;

    public LargeTropicalFishEntityModel(ModelPart root) {
        this.root = root;
        this.tail = root.getChild(EntityModelPartNames.TAIL);
    }

    public static TexturedModelData getTexturedModelData(Dilation dilation) {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        int i = 19;
        modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(0, 20).cuboid(-1.0f, -3.0f, -3.0f, 2.0f, 6.0f, 6.0f, dilation), ModelTransform.pivot(0.0f, 19.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.TAIL, ModelPartBuilder.create().uv(21, 16).cuboid(0.0f, -3.0f, 0.0f, 0.0f, 6.0f, 5.0f, dilation), ModelTransform.pivot(0.0f, 19.0f, 3.0f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_FIN, ModelPartBuilder.create().uv(2, 16).cuboid(-2.0f, 0.0f, 0.0f, 2.0f, 2.0f, 0.0f, dilation), ModelTransform.of(-1.0f, 20.0f, 0.0f, 0.0f, 0.7853982f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_FIN, ModelPartBuilder.create().uv(2, 12).cuboid(0.0f, 0.0f, 0.0f, 2.0f, 2.0f, 0.0f, dilation), ModelTransform.of(1.0f, 20.0f, 0.0f, 0.0f, -0.7853982f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.TOP_FIN, ModelPartBuilder.create().uv(20, 11).cuboid(0.0f, -4.0f, 0.0f, 0.0f, 4.0f, 6.0f, dilation), ModelTransform.pivot(0.0f, 16.0f, -3.0f));
        modelPartData.addChild(EntityModelPartNames.BOTTOM_FIN, ModelPartBuilder.create().uv(20, 21).cuboid(0.0f, 0.0f, 0.0f, 0.0f, 4.0f, 6.0f, dilation), ModelTransform.pivot(0.0f, 22.0f, -3.0f));
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

