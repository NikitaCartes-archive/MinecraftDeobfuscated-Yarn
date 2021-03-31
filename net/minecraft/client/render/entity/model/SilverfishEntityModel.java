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
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class SilverfishEntityModel<T extends Entity>
extends SinglePartEntityModel<T> {
    private static final int BODY_PARTS_COUNT = 7;
    private final ModelPart root;
    private final ModelPart[] body = new ModelPart[7];
    private final ModelPart[] scales = new ModelPart[3];
    private static final int[][] segmentLocations = new int[][]{{3, 2, 2}, {4, 3, 2}, {6, 4, 3}, {3, 3, 3}, {2, 2, 3}, {2, 1, 2}, {1, 1, 2}};
    private static final int[][] segmentSizes = new int[][]{{0, 0}, {0, 4}, {0, 9}, {0, 16}, {0, 22}, {11, 0}, {13, 4}};

    public SilverfishEntityModel(ModelPart root) {
        this.root = root;
        Arrays.setAll(this.body, index -> root.getChild(SilverfishEntityModel.getSegmentName(index)));
        Arrays.setAll(this.scales, index -> root.getChild(SilverfishEntityModel.getLayerName(index)));
    }

    private static String getLayerName(int index) {
        return "layer" + index;
    }

    private static String getSegmentName(int index) {
        return "segment" + index;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        float[] fs = new float[7];
        float f = -3.5f;
        for (int i = 0; i < 7; ++i) {
            modelPartData.addChild(SilverfishEntityModel.getSegmentName(i), ModelPartBuilder.create().uv(segmentSizes[i][0], segmentSizes[i][1]).cuboid((float)segmentLocations[i][0] * -0.5f, 0.0f, (float)segmentLocations[i][2] * -0.5f, segmentLocations[i][0], segmentLocations[i][1], segmentLocations[i][2]), ModelTransform.pivot(0.0f, 24 - segmentLocations[i][1], f));
            fs[i] = f;
            if (i >= 6) continue;
            f += (float)(segmentLocations[i][2] + segmentLocations[i + 1][2]) * 0.5f;
        }
        modelPartData.addChild(SilverfishEntityModel.getLayerName(0), ModelPartBuilder.create().uv(20, 0).cuboid(-5.0f, 0.0f, (float)segmentLocations[2][2] * -0.5f, 10.0f, 8.0f, segmentLocations[2][2]), ModelTransform.pivot(0.0f, 16.0f, fs[2]));
        modelPartData.addChild(SilverfishEntityModel.getLayerName(1), ModelPartBuilder.create().uv(20, 11).cuboid(-3.0f, 0.0f, (float)segmentLocations[4][2] * -0.5f, 6.0f, 4.0f, segmentLocations[4][2]), ModelTransform.pivot(0.0f, 20.0f, fs[4]));
        modelPartData.addChild(SilverfishEntityModel.getLayerName(2), ModelPartBuilder.create().uv(20, 18).cuboid(-3.0f, 0.0f, (float)segmentLocations[4][2] * -0.5f, 6.0f, 5.0f, segmentLocations[1][2]), ModelTransform.pivot(0.0f, 19.0f, fs[1]));
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        for (int i = 0; i < this.body.length; ++i) {
            this.body[i].yaw = MathHelper.cos(animationProgress * 0.9f + (float)i * 0.15f * (float)Math.PI) * (float)Math.PI * 0.05f * (float)(1 + Math.abs(i - 2));
            this.body[i].pivotX = MathHelper.sin(animationProgress * 0.9f + (float)i * 0.15f * (float)Math.PI) * (float)Math.PI * 0.2f * (float)Math.abs(i - 2);
        }
        this.scales[0].yaw = this.body[2].yaw;
        this.scales[1].yaw = this.body[4].yaw;
        this.scales[1].pivotX = this.body[4].pivotX;
        this.scales[2].yaw = this.body[1].yaw;
        this.scales[2].pivotX = this.body[1].pivotX;
    }
}

