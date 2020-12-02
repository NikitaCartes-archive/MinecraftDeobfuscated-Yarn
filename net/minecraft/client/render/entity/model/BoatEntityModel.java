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
public class BoatEntityModel
extends CompositeEntityModel<BoatEntity> {
    private final ModelPart leftPaddle;
    private final ModelPart rightPaddle;
    private final ModelPart waterPatch;
    private final ImmutableList<ModelPart> parts;

    public BoatEntityModel(ModelPart root) {
        this.leftPaddle = root.getChild("left_paddle");
        this.rightPaddle = root.getChild("right_paddle");
        this.waterPatch = root.getChild("water_patch");
        this.parts = ImmutableList.of(root.getChild("bottom"), root.getChild("back"), root.getChild("front"), root.getChild("right"), root.getChild("left"), this.leftPaddle, this.rightPaddle);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        int i = 32;
        int j = 6;
        int k = 20;
        int l = 4;
        int m = 28;
        modelPartData.addChild("bottom", ModelPartBuilder.create().uv(0, 0).cuboid(-14.0f, -9.0f, -3.0f, 28.0f, 16.0f, 3.0f), ModelTransform.of(0.0f, 3.0f, 1.0f, 1.5707964f, 0.0f, 0.0f));
        modelPartData.addChild("back", ModelPartBuilder.create().uv(0, 19).cuboid(-13.0f, -7.0f, -1.0f, 18.0f, 6.0f, 2.0f), ModelTransform.of(-15.0f, 4.0f, 4.0f, 0.0f, 4.712389f, 0.0f));
        modelPartData.addChild("front", ModelPartBuilder.create().uv(0, 27).cuboid(-8.0f, -7.0f, -1.0f, 16.0f, 6.0f, 2.0f), ModelTransform.of(15.0f, 4.0f, 0.0f, 0.0f, 1.5707964f, 0.0f));
        modelPartData.addChild("right", ModelPartBuilder.create().uv(0, 35).cuboid(-14.0f, -7.0f, -1.0f, 28.0f, 6.0f, 2.0f), ModelTransform.of(0.0f, 4.0f, -9.0f, 0.0f, (float)Math.PI, 0.0f));
        modelPartData.addChild("left", ModelPartBuilder.create().uv(0, 43).cuboid(-14.0f, -7.0f, -1.0f, 28.0f, 6.0f, 2.0f), ModelTransform.pivot(0.0f, 4.0f, 9.0f));
        int n = 20;
        int o = 7;
        int p = 6;
        float f = -5.0f;
        modelPartData.addChild("left_paddle", ModelPartBuilder.create().uv(62, 0).cuboid(-1.0f, 0.0f, -5.0f, 2.0f, 2.0f, 18.0f).cuboid(-1.001f, -3.0f, 8.0f, 1.0f, 6.0f, 7.0f), ModelTransform.of(3.0f, -5.0f, 9.0f, 0.0f, 0.0f, 0.19634955f));
        modelPartData.addChild("right_paddle", ModelPartBuilder.create().uv(62, 20).cuboid(-1.0f, 0.0f, -5.0f, 2.0f, 2.0f, 18.0f).cuboid(0.001f, -3.0f, 8.0f, 1.0f, 6.0f, 7.0f), ModelTransform.of(3.0f, -5.0f, -9.0f, 0.0f, (float)Math.PI, 0.19634955f));
        modelPartData.addChild("water_patch", ModelPartBuilder.create().uv(0, 0).cuboid(-14.0f, -9.0f, -3.0f, 28.0f, 16.0f, 3.0f), ModelTransform.of(0.0f, -3.0f, 1.0f, 1.5707964f, 0.0f, 0.0f));
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
        part.pitch = (float)MathHelper.clampedLerp(-1.0471975803375244, -0.2617993950843811, (MathHelper.sin(-f) + 1.0f) / 2.0f);
        part.yaw = (float)MathHelper.clampedLerp(-0.7853981852531433, 0.7853981852531433, (MathHelper.sin(-f + 1.0f) + 1.0f) / 2.0f);
        if (sigma == 1) {
            part.yaw = (float)Math.PI - part.yaw;
        }
    }

    @Override
    public /* synthetic */ Iterable getParts() {
        return this.getParts();
    }
}

