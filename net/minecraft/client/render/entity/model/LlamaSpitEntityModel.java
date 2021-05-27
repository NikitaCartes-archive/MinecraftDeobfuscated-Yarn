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

/**
 * Represents the model of llama-spit-like entity.
 * 
 * <div class="fabric">
 * <table border=1>
 * <caption>Model parts of this model</caption>
 * <tr>
 *   <th>Part Name</th><th>Parent</th><th>Corresponding Field</th>
 * </tr>
 * <tr>
 *   <td>{@value #MAIN}</td><td>{@linkplain #root Root part}</td><td></td>
 * </tr>
 * </table>
 * </div>
 */
@Environment(value=EnvType.CLIENT)
public class LlamaSpitEntityModel<T extends Entity>
extends SinglePartEntityModel<T> {
    /**
     * The key of the main model part, whose value is {@value}.
     */
    private static final String MAIN = "main";
    private final ModelPart root;

    public LlamaSpitEntityModel(ModelPart root) {
        this.root = root;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        int i = 2;
        modelPartData.addChild(MAIN, ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, 0.0f, 0.0f, 2.0f, 2.0f, 2.0f).cuboid(0.0f, -4.0f, 0.0f, 2.0f, 2.0f, 2.0f).cuboid(0.0f, 0.0f, -4.0f, 2.0f, 2.0f, 2.0f).cuboid(0.0f, 0.0f, 0.0f, 2.0f, 2.0f, 2.0f).cuboid(2.0f, 0.0f, 0.0f, 2.0f, 2.0f, 2.0f).cuboid(0.0f, 2.0f, 0.0f, 2.0f, 2.0f, 2.0f).cuboid(0.0f, 0.0f, 2.0f, 2.0f, 2.0f, 2.0f), ModelTransform.NONE);
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

