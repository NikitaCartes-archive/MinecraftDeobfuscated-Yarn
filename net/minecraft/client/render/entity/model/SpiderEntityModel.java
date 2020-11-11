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

@Environment(value=EnvType.CLIENT)
public class SpiderEntityModel<T extends Entity>
extends SinglePartEntityModel<T> {
    private final ModelPart field_27504;
    private final ModelPart head;
    private final ModelPart field_27505;
    private final ModelPart field_27506;
    private final ModelPart field_27507;
    private final ModelPart field_27508;
    private final ModelPart field_27509;
    private final ModelPart field_27510;
    private final ModelPart field_27511;
    private final ModelPart field_27512;

    public SpiderEntityModel(ModelPart modelPart) {
        this.field_27504 = modelPart;
        this.head = modelPart.getChild("head");
        this.field_27505 = modelPart.getChild("right_hind_leg");
        this.field_27506 = modelPart.getChild("left_hind_leg");
        this.field_27507 = modelPart.getChild("right_middle_hind_leg");
        this.field_27508 = modelPart.getChild("left_middle_hind_leg");
        this.field_27509 = modelPart.getChild("right_middle_front_leg");
        this.field_27510 = modelPart.getChild("left_middle_front_leg");
        this.field_27511 = modelPart.getChild("right_front_leg");
        this.field_27512 = modelPart.getChild("left_front_leg");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        int i = 15;
        modelPartData.addChild("head", ModelPartBuilder.create().uv(32, 4).cuboid(-4.0f, -4.0f, -8.0f, 8.0f, 8.0f, 8.0f), ModelTransform.pivot(0.0f, 15.0f, -3.0f));
        modelPartData.addChild("body0", ModelPartBuilder.create().uv(0, 0).cuboid(-3.0f, -3.0f, -3.0f, 6.0f, 6.0f, 6.0f), ModelTransform.pivot(0.0f, 15.0f, 0.0f));
        modelPartData.addChild("body1", ModelPartBuilder.create().uv(0, 12).cuboid(-5.0f, -4.0f, -6.0f, 10.0f, 8.0f, 12.0f), ModelTransform.pivot(0.0f, 15.0f, 9.0f));
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(18, 0).cuboid(-15.0f, -1.0f, -1.0f, 16.0f, 2.0f, 2.0f);
        ModelPartBuilder modelPartBuilder2 = ModelPartBuilder.create().uv(18, 0).cuboid(-1.0f, -1.0f, -1.0f, 16.0f, 2.0f, 2.0f);
        modelPartData.addChild("right_hind_leg", modelPartBuilder, ModelTransform.pivot(-4.0f, 15.0f, 2.0f));
        modelPartData.addChild("left_hind_leg", modelPartBuilder2, ModelTransform.pivot(4.0f, 15.0f, 2.0f));
        modelPartData.addChild("right_middle_hind_leg", modelPartBuilder, ModelTransform.pivot(-4.0f, 15.0f, 1.0f));
        modelPartData.addChild("left_middle_hind_leg", modelPartBuilder2, ModelTransform.pivot(4.0f, 15.0f, 1.0f));
        modelPartData.addChild("right_middle_front_leg", modelPartBuilder, ModelTransform.pivot(-4.0f, 15.0f, 0.0f));
        modelPartData.addChild("left_middle_front_leg", modelPartBuilder2, ModelTransform.pivot(4.0f, 15.0f, 0.0f));
        modelPartData.addChild("right_front_leg", modelPartBuilder, ModelTransform.pivot(-4.0f, 15.0f, -1.0f));
        modelPartData.addChild("left_front_leg", modelPartBuilder2, ModelTransform.pivot(4.0f, 15.0f, -1.0f));
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    public ModelPart getPart() {
        return this.field_27504;
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.head.yaw = headYaw * ((float)Math.PI / 180);
        this.head.pitch = headPitch * ((float)Math.PI / 180);
        float f = 0.7853982f;
        this.field_27505.roll = -0.7853982f;
        this.field_27506.roll = 0.7853982f;
        this.field_27507.roll = -0.58119464f;
        this.field_27508.roll = 0.58119464f;
        this.field_27509.roll = -0.58119464f;
        this.field_27510.roll = 0.58119464f;
        this.field_27511.roll = -0.7853982f;
        this.field_27512.roll = 0.7853982f;
        float g = -0.0f;
        float h = 0.3926991f;
        this.field_27505.yaw = 0.7853982f;
        this.field_27506.yaw = -0.7853982f;
        this.field_27507.yaw = 0.3926991f;
        this.field_27508.yaw = -0.3926991f;
        this.field_27509.yaw = -0.3926991f;
        this.field_27510.yaw = 0.3926991f;
        this.field_27511.yaw = -0.7853982f;
        this.field_27512.yaw = 0.7853982f;
        float i = -(MathHelper.cos(limbAngle * 0.6662f * 2.0f + 0.0f) * 0.4f) * limbDistance;
        float j = -(MathHelper.cos(limbAngle * 0.6662f * 2.0f + (float)Math.PI) * 0.4f) * limbDistance;
        float k = -(MathHelper.cos(limbAngle * 0.6662f * 2.0f + 1.5707964f) * 0.4f) * limbDistance;
        float l = -(MathHelper.cos(limbAngle * 0.6662f * 2.0f + 4.712389f) * 0.4f) * limbDistance;
        float m = Math.abs(MathHelper.sin(limbAngle * 0.6662f + 0.0f) * 0.4f) * limbDistance;
        float n = Math.abs(MathHelper.sin(limbAngle * 0.6662f + (float)Math.PI) * 0.4f) * limbDistance;
        float o = Math.abs(MathHelper.sin(limbAngle * 0.6662f + 1.5707964f) * 0.4f) * limbDistance;
        float p = Math.abs(MathHelper.sin(limbAngle * 0.6662f + 4.712389f) * 0.4f) * limbDistance;
        this.field_27505.yaw += i;
        this.field_27506.yaw += -i;
        this.field_27507.yaw += j;
        this.field_27508.yaw += -j;
        this.field_27509.yaw += k;
        this.field_27510.yaw += -k;
        this.field_27511.yaw += l;
        this.field_27512.yaw += -l;
        this.field_27505.roll += m;
        this.field_27506.roll += -m;
        this.field_27507.roll += n;
        this.field_27508.roll += -n;
        this.field_27509.roll += o;
        this.field_27510.roll += -o;
        this.field_27511.roll += p;
        this.field_27512.roll += -p;
    }
}

