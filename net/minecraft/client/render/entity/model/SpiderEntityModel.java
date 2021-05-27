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
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class SpiderEntityModel<T extends Entity>
extends SinglePartEntityModel<T> {
    /**
     * The key of the first model part of the body, whose value is {@value}.
     */
    private static final String BODY0 = "body0";
    /**
     * The key of the second model part of the body, whose value is {@value}.
     */
    private static final String BODY1 = "body1";
    /**
     * The key of the right middle front leg model part, whose value is {@value}.
     */
    private static final String RIGHT_MIDDLE_FRONT_LEG = "right_middle_front_leg";
    /**
     * The key of the left middle front leg model part, whose value is {@value}.
     */
    private static final String LEFT_MIDDLE_FRONT_LEG = "left_middle_front_leg";
    /**
     * The key of the right middle hind leg model part, whose value is {@value}.
     */
    private static final String RIGHT_MIDDLE_HIND_LEG = "right_middle_hind_leg";
    /**
     * The key of the left middle hind leg model part, whose value is {@value}.
     */
    private static final String LEFT_MIDDLE_HIND_LEG = "left_middle_hind_leg";
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightMiddleLeg;
    private final ModelPart leftMiddleLeg;
    private final ModelPart rightMiddleFrontLeg;
    private final ModelPart leftMiddleFrontLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;

    public SpiderEntityModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild(EntityModelPartNames.HEAD);
        this.rightHindLeg = root.getChild(EntityModelPartNames.RIGHT_HIND_LEG);
        this.leftHindLeg = root.getChild(EntityModelPartNames.LEFT_HIND_LEG);
        this.rightMiddleLeg = root.getChild(RIGHT_MIDDLE_HIND_LEG);
        this.leftMiddleLeg = root.getChild(LEFT_MIDDLE_HIND_LEG);
        this.rightMiddleFrontLeg = root.getChild(RIGHT_MIDDLE_FRONT_LEG);
        this.leftMiddleFrontLeg = root.getChild(LEFT_MIDDLE_FRONT_LEG);
        this.rightFrontLeg = root.getChild(EntityModelPartNames.RIGHT_FRONT_LEG);
        this.leftFrontLeg = root.getChild(EntityModelPartNames.LEFT_FRONT_LEG);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        int i = 15;
        modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(32, 4).cuboid(-4.0f, -4.0f, -8.0f, 8.0f, 8.0f, 8.0f), ModelTransform.pivot(0.0f, 15.0f, -3.0f));
        modelPartData.addChild(BODY0, ModelPartBuilder.create().uv(0, 0).cuboid(-3.0f, -3.0f, -3.0f, 6.0f, 6.0f, 6.0f), ModelTransform.pivot(0.0f, 15.0f, 0.0f));
        modelPartData.addChild(BODY1, ModelPartBuilder.create().uv(0, 12).cuboid(-5.0f, -4.0f, -6.0f, 10.0f, 8.0f, 12.0f), ModelTransform.pivot(0.0f, 15.0f, 9.0f));
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(18, 0).cuboid(-15.0f, -1.0f, -1.0f, 16.0f, 2.0f, 2.0f);
        ModelPartBuilder modelPartBuilder2 = ModelPartBuilder.create().uv(18, 0).cuboid(-1.0f, -1.0f, -1.0f, 16.0f, 2.0f, 2.0f);
        modelPartData.addChild(EntityModelPartNames.RIGHT_HIND_LEG, modelPartBuilder, ModelTransform.pivot(-4.0f, 15.0f, 2.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_HIND_LEG, modelPartBuilder2, ModelTransform.pivot(4.0f, 15.0f, 2.0f));
        modelPartData.addChild(RIGHT_MIDDLE_HIND_LEG, modelPartBuilder, ModelTransform.pivot(-4.0f, 15.0f, 1.0f));
        modelPartData.addChild(LEFT_MIDDLE_HIND_LEG, modelPartBuilder2, ModelTransform.pivot(4.0f, 15.0f, 1.0f));
        modelPartData.addChild(RIGHT_MIDDLE_FRONT_LEG, modelPartBuilder, ModelTransform.pivot(-4.0f, 15.0f, 0.0f));
        modelPartData.addChild(LEFT_MIDDLE_FRONT_LEG, modelPartBuilder2, ModelTransform.pivot(4.0f, 15.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_FRONT_LEG, modelPartBuilder, ModelTransform.pivot(-4.0f, 15.0f, -1.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_FRONT_LEG, modelPartBuilder2, ModelTransform.pivot(4.0f, 15.0f, -1.0f));
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.head.yaw = headYaw * ((float)Math.PI / 180);
        this.head.pitch = headPitch * ((float)Math.PI / 180);
        float f = 0.7853982f;
        this.rightHindLeg.roll = -0.7853982f;
        this.leftHindLeg.roll = 0.7853982f;
        this.rightMiddleLeg.roll = -0.58119464f;
        this.leftMiddleLeg.roll = 0.58119464f;
        this.rightMiddleFrontLeg.roll = -0.58119464f;
        this.leftMiddleFrontLeg.roll = 0.58119464f;
        this.rightFrontLeg.roll = -0.7853982f;
        this.leftFrontLeg.roll = 0.7853982f;
        float g = -0.0f;
        float h = 0.3926991f;
        this.rightHindLeg.yaw = 0.7853982f;
        this.leftHindLeg.yaw = -0.7853982f;
        this.rightMiddleLeg.yaw = 0.3926991f;
        this.leftMiddleLeg.yaw = -0.3926991f;
        this.rightMiddleFrontLeg.yaw = -0.3926991f;
        this.leftMiddleFrontLeg.yaw = 0.3926991f;
        this.rightFrontLeg.yaw = -0.7853982f;
        this.leftFrontLeg.yaw = 0.7853982f;
        float i = -(MathHelper.cos(limbAngle * 0.6662f * 2.0f + 0.0f) * 0.4f) * limbDistance;
        float j = -(MathHelper.cos(limbAngle * 0.6662f * 2.0f + (float)Math.PI) * 0.4f) * limbDistance;
        float k = -(MathHelper.cos(limbAngle * 0.6662f * 2.0f + 1.5707964f) * 0.4f) * limbDistance;
        float l = -(MathHelper.cos(limbAngle * 0.6662f * 2.0f + 4.712389f) * 0.4f) * limbDistance;
        float m = Math.abs(MathHelper.sin(limbAngle * 0.6662f + 0.0f) * 0.4f) * limbDistance;
        float n = Math.abs(MathHelper.sin(limbAngle * 0.6662f + (float)Math.PI) * 0.4f) * limbDistance;
        float o = Math.abs(MathHelper.sin(limbAngle * 0.6662f + 1.5707964f) * 0.4f) * limbDistance;
        float p = Math.abs(MathHelper.sin(limbAngle * 0.6662f + 4.712389f) * 0.4f) * limbDistance;
        this.rightHindLeg.yaw += i;
        this.leftHindLeg.yaw += -i;
        this.rightMiddleLeg.yaw += j;
        this.leftMiddleLeg.yaw += -j;
        this.rightMiddleFrontLeg.yaw += k;
        this.leftMiddleFrontLeg.yaw += -k;
        this.rightFrontLeg.yaw += l;
        this.leftFrontLeg.yaw += -l;
        this.rightHindLeg.roll += m;
        this.leftHindLeg.roll += -m;
        this.rightMiddleLeg.roll += n;
        this.leftMiddleLeg.roll += -n;
        this.rightMiddleFrontLeg.roll += o;
        this.leftMiddleFrontLeg.roll += -o;
        this.rightFrontLeg.roll += p;
        this.leftFrontLeg.roll += -p;
    }
}

