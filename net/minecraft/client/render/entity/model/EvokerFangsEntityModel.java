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

/**
 * Represents the model of an evoker-fangs-like entity.
 * 
 * <div class="fabric">
 * <table border=1>
 * <caption>Model parts of this model</caption>
 * <tr>
 *   <th>Part Name</th><th>Parent</th><th>Corresponding Field</th>
 * </tr>
 * <tr>
 *   <td>{@value #BASE}</td><td>{@linkplain #root Root part}</td><td>{@link #base}</td>
 * </tr>
 * <tr>
 *   <td>{@value #UPPER_JAW}</td><td>{@linkplain #root Root part}</td><td>{@link #upperJaw}</td>
 * </tr>
 * <tr>
 *   <td>{@value #LOWER_JAW}</td><td>{@linkplain #root Root part}</td><td>{@link #lowerJaw}</td>
 * </tr>
 * </table>
 * </div>
 */
@Environment(value=EnvType.CLIENT)
public class EvokerFangsEntityModel<T extends Entity>
extends SinglePartEntityModel<T> {
    /**
     * The key of the base model part, whose value is {@value}.
     */
    private static final String BASE = "base";
    /**
     * The key of the upper jaw model part, whose value is {@value}.
     */
    private static final String UPPER_JAW = "upper_jaw";
    /**
     * The key of the lower jaw model part, whose value is {@value}.
     */
    private static final String LOWER_JAW = "lower_jaw";
    private final ModelPart root;
    private final ModelPart base;
    private final ModelPart upperJaw;
    private final ModelPart lowerJaw;

    public EvokerFangsEntityModel(ModelPart root) {
        this.root = root;
        this.base = root.getChild(BASE);
        this.upperJaw = root.getChild(UPPER_JAW);
        this.lowerJaw = root.getChild(LOWER_JAW);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(BASE, ModelPartBuilder.create().uv(0, 0).cuboid(0.0f, 0.0f, 0.0f, 10.0f, 12.0f, 10.0f), ModelTransform.pivot(-5.0f, 24.0f, -5.0f));
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(40, 0).cuboid(0.0f, 0.0f, 0.0f, 4.0f, 14.0f, 8.0f);
        modelPartData.addChild(UPPER_JAW, modelPartBuilder, ModelTransform.pivot(1.5f, 24.0f, -4.0f));
        modelPartData.addChild(LOWER_JAW, modelPartBuilder, ModelTransform.of(-1.5f, 24.0f, 4.0f, 0.0f, (float)Math.PI, 0.0f));
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        float f = limbAngle * 2.0f;
        if (f > 1.0f) {
            f = 1.0f;
        }
        f = 1.0f - f * f * f;
        this.upperJaw.roll = (float)Math.PI - f * 0.35f * (float)Math.PI;
        this.lowerJaw.roll = (float)Math.PI + f * 0.35f * (float)Math.PI;
        float g = (limbAngle + MathHelper.sin(limbAngle * 2.7f)) * 0.6f * 12.0f;
        this.lowerJaw.pivotY = this.upperJaw.pivotY = 24.0f - g;
        this.base.pivotY = this.upperJaw.pivotY;
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }
}

