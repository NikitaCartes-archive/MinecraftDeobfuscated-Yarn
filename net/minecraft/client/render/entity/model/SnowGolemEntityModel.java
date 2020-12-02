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
import net.minecraft.client.util.math.Dilation;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class SnowGolemEntityModel<T extends Entity>
extends SinglePartEntityModel<T> {
    private final ModelPart root;
    private final ModelPart upperBody;
    private final ModelPart head;
    private final ModelPart leftArm;
    private final ModelPart rightArm;

    public SnowGolemEntityModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.leftArm = root.getChild("left_arm");
        this.rightArm = root.getChild("right_arm");
        this.upperBody = root.getChild("upper_body");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        float f = 4.0f;
        Dilation dilation = new Dilation(-0.5f);
        modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, dilation), ModelTransform.pivot(0.0f, 4.0f, 0.0f));
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(32, 0).cuboid(-1.0f, 0.0f, -1.0f, 12.0f, 2.0f, 2.0f, dilation);
        modelPartData.addChild("left_arm", modelPartBuilder, ModelTransform.of(5.0f, 6.0f, 1.0f, 0.0f, 0.0f, 1.0f));
        modelPartData.addChild("right_arm", modelPartBuilder, ModelTransform.of(-5.0f, 6.0f, -1.0f, 0.0f, (float)Math.PI, -1.0f));
        modelPartData.addChild("upper_body", ModelPartBuilder.create().uv(0, 16).cuboid(-5.0f, -10.0f, -5.0f, 10.0f, 10.0f, 10.0f, dilation), ModelTransform.pivot(0.0f, 13.0f, 0.0f));
        modelPartData.addChild("lower_body", ModelPartBuilder.create().uv(0, 36).cuboid(-6.0f, -12.0f, -6.0f, 12.0f, 12.0f, 12.0f, dilation), ModelTransform.pivot(0.0f, 24.0f, 0.0f));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.head.yaw = headYaw * ((float)Math.PI / 180);
        this.head.pitch = headPitch * ((float)Math.PI / 180);
        this.upperBody.yaw = headYaw * ((float)Math.PI / 180) * 0.25f;
        float f = MathHelper.sin(this.upperBody.yaw);
        float g = MathHelper.cos(this.upperBody.yaw);
        this.leftArm.yaw = this.upperBody.yaw;
        this.rightArm.yaw = this.upperBody.yaw + (float)Math.PI;
        this.leftArm.pivotX = g * 5.0f;
        this.leftArm.pivotZ = -f * 5.0f;
        this.rightArm.pivotX = -g * 5.0f;
        this.rightArm.pivotZ = f * 5.0f;
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }

    public ModelPart getTopSnowball() {
        return this.head;
    }
}

