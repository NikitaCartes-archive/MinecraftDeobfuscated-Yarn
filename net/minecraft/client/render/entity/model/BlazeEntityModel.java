/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import java.util.Arrays;
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
import net.minecraft.util.math.MathHelper;

/**
 * Represents the model of a blaze-like entity.
 * This model is not tied to a specific entity.
 * 
 * <div class="fabric">
 * <table border=1>
 * <caption>Model parts of this model</caption>
 * <tr>
 *   <th>Part Name</th><th>Parent</th><th>Corresponding Field</th>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#HEAD}</td><td>{@linkplain #root Root part}</td><td>{@link #head}</td>
 * </tr>
 * <tr>
 *   <td>{@code part0}</td><td>{@linkplain #root Root part}</td><td>{@link #rods rods[0]}</td>
 * </tr>
 * <tr>
 *   <td>{@code part1}</td><td>{@linkplain #root Root part}</td><td>{@link #rods rods[1]}</td>
 * </tr>
 * <tr>
 *   <td>{@code part2}</td><td>{@linkplain #root Root part}</td><td>{@link #rods rods[2]}</td>
 * </tr>
 * <tr>
 *   <td>{@code part3}</td><td>{@linkplain #root Root part}</td><td>{@link #rods rods[3]}</td>
 * </tr>
 * <tr>
 *   <td>{@code part4}</td><td>{@linkplain #root Root part}</td><td>{@link #rods rods[4]}</td>
 * </tr>
 * <tr>
 *   <td>{@code part5}</td><td>{@linkplain #root Root part}</td><td>{@link #rods rods[5]}</td>
 * </tr>
 * <tr>
 *   <td>{@code part6}</td><td>{@linkplain #root Root part}</td><td>{@link #rods rods[6]}</td>
 * </tr>
 * <tr>
 *   <td>{@code part7}</td><td>{@linkplain #root Root part}</td><td>{@link #rods rods[7]}</td>
 * </tr>
 * <tr>
 *   <td>{@code part8}</td><td>{@linkplain #root Root part}</td><td>{@link #rods rods[8]}</td>
 * </tr>
 * <tr>
 *   <td>{@code part9}</td><td>{@linkplain #root Root part}</td><td>{@link #rods rods[9]}</td>
 * </tr>
 * <tr>
 *   <td>{@code part10}</td><td>{@linkplain #root Root part}</td><td>{@link #rods rods[10]}</td>
 * </tr>
 * <tr>
 *   <td>{@code part11}</td><td>{@linkplain #root Root part}</td><td>{@link #rods rods[11]}</td>
 * </tr>
 * </table>
 * </div>
 */
@Environment(value=EnvType.CLIENT)
public class BlazeEntityModel<T extends Entity>
extends SinglePartEntityModel<T> {
    private final ModelPart root;
    private final ModelPart[] rods;
    private final ModelPart head;

    public BlazeEntityModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild(EntityModelPartNames.HEAD);
        this.rods = new ModelPart[12];
        Arrays.setAll(this.rods, index -> root.getChild(BlazeEntityModel.getRodName(index)));
    }

    private static String getRodName(int index) {
        return "part" + index;
    }

    public static TexturedModelData getTexturedModelData() {
        float j;
        float h;
        float g;
        int i;
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f), ModelTransform.NONE);
        float f = 0.0f;
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(0, 16).cuboid(0.0f, 0.0f, 0.0f, 2.0f, 8.0f, 2.0f);
        for (i = 0; i < 4; ++i) {
            g = MathHelper.cos(f) * 9.0f;
            h = -2.0f + MathHelper.cos((float)(i * 2) * 0.25f);
            j = MathHelper.sin(f) * 9.0f;
            modelPartData.addChild(BlazeEntityModel.getRodName(i), modelPartBuilder, ModelTransform.pivot(g, h, j));
            f += 1.5707964f;
        }
        f = 0.7853982f;
        for (i = 4; i < 8; ++i) {
            g = MathHelper.cos(f) * 7.0f;
            h = 2.0f + MathHelper.cos((float)(i * 2) * 0.25f);
            j = MathHelper.sin(f) * 7.0f;
            modelPartData.addChild(BlazeEntityModel.getRodName(i), modelPartBuilder, ModelTransform.pivot(g, h, j));
            f += 1.5707964f;
        }
        f = 0.47123894f;
        for (i = 8; i < 12; ++i) {
            g = MathHelper.cos(f) * 5.0f;
            h = 11.0f + MathHelper.cos((float)i * 1.5f * 0.5f);
            j = MathHelper.sin(f) * 5.0f;
            modelPartData.addChild(BlazeEntityModel.getRodName(i), modelPartBuilder, ModelTransform.pivot(g, h, j));
            f += 1.5707964f;
        }
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        int i;
        float f = animationProgress * (float)Math.PI * -0.1f;
        for (i = 0; i < 4; ++i) {
            this.rods[i].pivotY = -2.0f + MathHelper.cos(((float)(i * 2) + animationProgress) * 0.25f);
            this.rods[i].pivotX = MathHelper.cos(f) * 9.0f;
            this.rods[i].pivotZ = MathHelper.sin(f) * 9.0f;
            f += 1.5707964f;
        }
        f = 0.7853982f + animationProgress * (float)Math.PI * 0.03f;
        for (i = 4; i < 8; ++i) {
            this.rods[i].pivotY = 2.0f + MathHelper.cos(((float)(i * 2) + animationProgress) * 0.25f);
            this.rods[i].pivotX = MathHelper.cos(f) * 7.0f;
            this.rods[i].pivotZ = MathHelper.sin(f) * 7.0f;
            f += 1.5707964f;
        }
        f = 0.47123894f + animationProgress * (float)Math.PI * -0.05f;
        for (i = 8; i < 12; ++i) {
            this.rods[i].pivotY = 11.0f + MathHelper.cos(((float)i * 1.5f + animationProgress) * 0.5f);
            this.rods[i].pivotX = MathHelper.cos(f) * 5.0f;
            this.rods[i].pivotZ = MathHelper.sin(f) * 5.0f;
            f += 1.5707964f;
        }
        this.head.yaw = headYaw * ((float)Math.PI / 180);
        this.head.pitch = headPitch * ((float)Math.PI / 180);
    }
}

