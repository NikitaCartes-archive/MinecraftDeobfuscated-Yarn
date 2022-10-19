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

@Environment(value=EnvType.CLIENT)
public class RaftEntityModel
extends CompositeEntityModel<BoatEntity> {
    private static final String LEFT_PADDLE = "left_paddle";
    private static final String RIGHT_PADDLE = "right_paddle";
    private static final String BOTTOM = "bottom";
    private final ModelPart leftPaddle;
    private final ModelPart rightPaddle;
    private final ImmutableList<ModelPart> parts;

    public RaftEntityModel(ModelPart root) {
        this.leftPaddle = root.getChild(LEFT_PADDLE);
        this.rightPaddle = root.getChild(RIGHT_PADDLE);
        this.parts = this.getParts(root).build();
    }

    protected ImmutableList.Builder<ModelPart> getParts(ModelPart root) {
        ImmutableList.Builder<ModelPart> builder = new ImmutableList.Builder<ModelPart>();
        builder.add(new ModelPart[]{root.getChild(BOTTOM), this.leftPaddle, this.rightPaddle});
        return builder;
    }

    public static void addParts(ModelPartData modelPartData) {
        modelPartData.addChild(BOTTOM, ModelPartBuilder.create().uv(0, 0).cuboid(-14.0f, -11.0f, -4.0f, 28.0f, 20.0f, 4.0f).uv(0, 0).cuboid(-14.0f, -9.0f, -8.0f, 28.0f, 16.0f, 4.0f), ModelTransform.of(0.0f, -3.0f, 1.0f, 1.5708f, 0.0f, 0.0f));
        int i = 20;
        int j = 7;
        int k = 6;
        float f = -5.0f;
        modelPartData.addChild(LEFT_PADDLE, ModelPartBuilder.create().uv(0, 24).cuboid(-1.0f, 0.0f, -5.0f, 2.0f, 2.0f, 18.0f).cuboid(-1.001f, -3.0f, 8.0f, 1.0f, 6.0f, 7.0f), ModelTransform.of(3.0f, -5.0f, 9.0f, 0.0f, 0.0f, 0.19634955f));
        modelPartData.addChild(RIGHT_PADDLE, ModelPartBuilder.create().uv(40, 24).cuboid(-1.0f, 0.0f, -5.0f, 2.0f, 2.0f, 18.0f).cuboid(0.001f, -3.0f, 8.0f, 1.0f, 6.0f, 7.0f), ModelTransform.of(3.0f, -5.0f, -9.0f, 0.0f, (float)Math.PI, 0.19634955f));
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        RaftEntityModel.addParts(modelPartData);
        return TexturedModelData.of(modelData, 128, 64);
    }

    @Override
    public void setAngles(BoatEntity boatEntity, float f, float g, float h, float i, float j) {
        RaftEntityModel.setPaddleAngle(boatEntity, 0, this.leftPaddle, f);
        RaftEntityModel.setPaddleAngle(boatEntity, 1, this.rightPaddle, f);
    }

    public ImmutableList<ModelPart> getParts() {
        return this.parts;
    }

    private static void setPaddleAngle(BoatEntity entity, int sigma, ModelPart part, float angle) {
        float f = entity.interpolatePaddlePhase(sigma, angle);
        part.pitch = MathHelper.clampedLerp(-1.0471976f, -0.2617994f, (MathHelper.sin(-f) + 1.0f) / 2.0f);
        part.yaw = MathHelper.clampedLerp(-0.7853982f, 0.7853982f, (MathHelper.sin(-f + 1.0f) + 1.0f) / 2.0f);
        if (sigma == 1) {
            part.yaw = (float)Math.PI - part.yaw;
        }
    }

    @Override
    public /* synthetic */ Iterable getParts() {
        return this.getParts();
    }
}

