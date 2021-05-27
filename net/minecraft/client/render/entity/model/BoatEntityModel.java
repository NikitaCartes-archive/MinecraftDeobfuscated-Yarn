/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.CompositeEntityModel;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.MathHelper;

/**
 * Represents the model of a {@linkplain BoatEntity}.
 * 
 * <div class="fabric">
 * <table border=1>
 * <caption>Model parts of this model</caption>
 * <tr>
 *   <th>Part Name</th><th>Parent</th><th>Corresponding Field</th>
 * </tr>
 * <tr>
 *   <td>{@value #BOTTOM}</td><td>Root part</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@value #BACK}</td><td>Root part</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@value #FRONT}</td><td>Root part</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@value #RIGHT}</td><td>Root part</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@value #LEFT}</td><td>Root part</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@value #LEFT_PADDLE}</td><td>Root part</td><td>{@link #leftPaddle}</td>
 * </tr>
 * <tr>
 *   <td>{@value #RIGHT_PADDLE}</td><td>Root part</td><td>{@link #rightPaddle}</td>
 * </tr>
 * <tr>
 *   <td>{@value #WATER_PATCH}</td><td>Root part</td><td>{@link #waterPatch}</td>
 * </tr>
 * </table>
 * </div>
 */
@Environment(value=EnvType.CLIENT)
public class BoatEntityModel
extends CompositeEntityModel<BoatEntity> {
    /**
     * The key of the left paddle model part, whose value is {@value}.
     */
    private static final String LEFT_PADDLE = "left_paddle";
    /**
     * The key of the right paddle model part, whose value is {@value}.
     */
    private static final String RIGHT_PADDLE = "right_paddle";
    /**
     * The key of the water patch model part, whose value is {@value}.
     */
    private static final String WATER_PATCH = "water_patch";
    /**
     * The key of the bottom model part, whose value is {@value}.
     */
    private static final String BOTTOM = "bottom";
    /**
     * The key of the back model part, whose value is {@value}.
     */
    private static final String BACK = "back";
    /**
     * The key of the front model part, whose value is {@value}.
     */
    private static final String FRONT = "front";
    /**
     * The key of the right model part, whose value is {@value}.
     */
    private static final String RIGHT = "right";
    /**
     * The key of the left model part, whose value is {@value}.
     */
    private static final String LEFT = "left";
    private final ModelPart leftPaddle;
    private final ModelPart rightPaddle;
    private final ModelPart waterPatch;
    private final ImmutableList<ModelPart> parts;

    public BoatEntityModel(ModelPart root) {
        this.leftPaddle = root.getChild(LEFT_PADDLE);
        this.rightPaddle = root.getChild(RIGHT_PADDLE);
        this.waterPatch = root.getChild(WATER_PATCH);
        this.parts = ImmutableList.of(root.getChild(BOTTOM), root.getChild(BACK), root.getChild(FRONT), root.getChild(RIGHT), root.getChild(LEFT), this.leftPaddle, this.rightPaddle);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        int i = 32;
        int j = 6;
        int k = 20;
        int l = 4;
        int m = 28;
        modelPartData.addChild(BOTTOM, ModelPartBuilder.create().uv(0, 0).cuboid(-14.0f, -9.0f, -3.0f, 28.0f, 16.0f, 3.0f), ModelTransform.of(0.0f, 3.0f, 1.0f, 1.5707964f, 0.0f, 0.0f));
        modelPartData.addChild(BACK, ModelPartBuilder.create().uv(0, 19).cuboid(-13.0f, -7.0f, -1.0f, 18.0f, 6.0f, 2.0f), ModelTransform.of(-15.0f, 4.0f, 4.0f, 0.0f, 4.712389f, 0.0f));
        modelPartData.addChild(FRONT, ModelPartBuilder.create().uv(0, 27).cuboid(-8.0f, -7.0f, -1.0f, 16.0f, 6.0f, 2.0f), ModelTransform.of(15.0f, 4.0f, 0.0f, 0.0f, 1.5707964f, 0.0f));
        modelPartData.addChild(RIGHT, ModelPartBuilder.create().uv(0, 35).cuboid(-14.0f, -7.0f, -1.0f, 28.0f, 6.0f, 2.0f), ModelTransform.of(0.0f, 4.0f, -9.0f, 0.0f, (float)Math.PI, 0.0f));
        modelPartData.addChild(LEFT, ModelPartBuilder.create().uv(0, 43).cuboid(-14.0f, -7.0f, -1.0f, 28.0f, 6.0f, 2.0f), ModelTransform.pivot(0.0f, 4.0f, 9.0f));
        int n = 20;
        int o = 7;
        int p = 6;
        float f = -5.0f;
        modelPartData.addChild(LEFT_PADDLE, ModelPartBuilder.create().uv(62, 0).cuboid(-1.0f, 0.0f, -5.0f, 2.0f, 2.0f, 18.0f).cuboid(-1.001f, -3.0f, 8.0f, 1.0f, 6.0f, 7.0f), ModelTransform.of(3.0f, -5.0f, 9.0f, 0.0f, 0.0f, 0.19634955f));
        modelPartData.addChild(RIGHT_PADDLE, ModelPartBuilder.create().uv(62, 20).cuboid(-1.0f, 0.0f, -5.0f, 2.0f, 2.0f, 18.0f).cuboid(0.001f, -3.0f, 8.0f, 1.0f, 6.0f, 7.0f), ModelTransform.of(3.0f, -5.0f, -9.0f, 0.0f, (float)Math.PI, 0.19634955f));
        modelPartData.addChild(WATER_PATCH, ModelPartBuilder.create().uv(0, 0).cuboid(-14.0f, -9.0f, -3.0f, 28.0f, 16.0f, 3.0f), ModelTransform.of(0.0f, -3.0f, 1.0f, 1.5707964f, 0.0f, 0.0f));
        return TexturedModelData.of(modelData, 128, 64);
    }

    @Override
    public void setAngles(BoatEntity boatEntity, float f, float g, float h, float i, float j) {
        BoatEntityModel.setPaddleAngle(boatEntity, 0, this.leftPaddle, f);
        BoatEntityModel.setPaddleAngle(boatEntity, 1, this.rightPaddle, f);
    }

    public ImmutableList<ModelPart> getParts() {
        return this.parts;
    }

    public ModelPart getBottom() {
        return this.waterPatch;
    }

    private static void setPaddleAngle(BoatEntity entity, int sigma, ModelPart part, float angle) {
        float f = entity.interpolatePaddlePhase(sigma, angle);
        part.pitch = MathHelper.method_37166(-1.0471976f, -0.2617994f, (MathHelper.sin(-f) + 1.0f) / 2.0f);
        part.yaw = MathHelper.method_37166(-0.7853982f, 0.7853982f, (MathHelper.sin(-f + 1.0f) + 1.0f) / 2.0f);
        if (sigma == 1) {
            part.yaw = (float)Math.PI - part.yaw;
        }
    }

    @Override
    public /* synthetic */ Iterable getParts() {
        return this.getParts();
    }
}

