/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class AxolotlEntityModel<T extends AxolotlEntity>
extends AnimalModel<T> {
    private final ModelPart tail;
    private final ModelPart leftHindLeg;
    private final ModelPart rightHindLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart topGills;
    private final ModelPart leftGills;
    private final ModelPart rightGills;
    public static final float MOVING_IN_WATER_LEG_PITCH = 1.8849558f;

    public AxolotlEntityModel(ModelPart root) {
        super(true, 8.0f, 3.35f);
        this.body = root.getChild(EntityModelPartNames.BODY);
        this.head = this.body.getChild(EntityModelPartNames.HEAD);
        this.rightHindLeg = this.body.getChild(EntityModelPartNames.RIGHT_HIND_LEG);
        this.leftHindLeg = this.body.getChild(EntityModelPartNames.LEFT_HIND_LEG);
        this.rightFrontLeg = this.body.getChild(EntityModelPartNames.RIGHT_FRONT_LEG);
        this.leftFrontLeg = this.body.getChild(EntityModelPartNames.LEFT_FRONT_LEG);
        this.tail = this.body.getChild(EntityModelPartNames.TAIL);
        this.topGills = this.head.getChild(EntityModelPartNames.TOP_GILLS);
        this.leftGills = this.head.getChild(EntityModelPartNames.LEFT_GILLS);
        this.rightGills = this.head.getChild(EntityModelPartNames.RIGHT_GILLS);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(0, 11).cuboid(-4.0f, -2.0f, -9.0f, 8.0f, 4.0f, 10.0f).uv(2, 17).cuboid(0.0f, -3.0f, -8.0f, 0.0f, 5.0f, 9.0f), ModelTransform.pivot(0.0f, 20.0f, 5.0f));
        Dilation dilation = new Dilation(0.001f);
        ModelPartData modelPartData3 = modelPartData2.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 1).cuboid(-4.0f, -3.0f, -5.0f, 8.0f, 5.0f, 5.0f, dilation), ModelTransform.pivot(0.0f, 0.0f, -9.0f));
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(3, 37).cuboid(-4.0f, -3.0f, 0.0f, 8.0f, 3.0f, 0.0f, dilation);
        ModelPartBuilder modelPartBuilder2 = ModelPartBuilder.create().uv(0, 40).cuboid(-3.0f, -5.0f, 0.0f, 3.0f, 7.0f, 0.0f, dilation);
        ModelPartBuilder modelPartBuilder3 = ModelPartBuilder.create().uv(11, 40).cuboid(0.0f, -5.0f, 0.0f, 3.0f, 7.0f, 0.0f, dilation);
        modelPartData3.addChild(EntityModelPartNames.TOP_GILLS, modelPartBuilder, ModelTransform.pivot(0.0f, -3.0f, -1.0f));
        modelPartData3.addChild(EntityModelPartNames.LEFT_GILLS, modelPartBuilder2, ModelTransform.pivot(-4.0f, 0.0f, -1.0f));
        modelPartData3.addChild(EntityModelPartNames.RIGHT_GILLS, modelPartBuilder3, ModelTransform.pivot(4.0f, 0.0f, -1.0f));
        ModelPartBuilder modelPartBuilder4 = ModelPartBuilder.create().uv(2, 13).cuboid(-1.0f, 0.0f, 0.0f, 3.0f, 5.0f, 0.0f, dilation);
        ModelPartBuilder modelPartBuilder5 = ModelPartBuilder.create().uv(2, 13).cuboid(-2.0f, 0.0f, 0.0f, 3.0f, 5.0f, 0.0f, dilation);
        modelPartData2.addChild(EntityModelPartNames.RIGHT_HIND_LEG, modelPartBuilder5, ModelTransform.pivot(-3.5f, 1.0f, -1.0f));
        modelPartData2.addChild(EntityModelPartNames.LEFT_HIND_LEG, modelPartBuilder4, ModelTransform.pivot(3.5f, 1.0f, -1.0f));
        modelPartData2.addChild(EntityModelPartNames.RIGHT_FRONT_LEG, modelPartBuilder5, ModelTransform.pivot(-3.5f, 1.0f, -8.0f));
        modelPartData2.addChild(EntityModelPartNames.LEFT_FRONT_LEG, modelPartBuilder4, ModelTransform.pivot(3.5f, 1.0f, -8.0f));
        modelPartData2.addChild(EntityModelPartNames.TAIL, ModelPartBuilder.create().uv(2, 19).cuboid(0.0f, -3.0f, 0.0f, 0.0f, 5.0f, 12.0f), ModelTransform.pivot(0.0f, 0.0f, 1.0f));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    protected Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of();
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of(this.body);
    }

    @Override
    public void setAngles(T axolotlEntity, float f, float g, float h, float i, float j) {
        boolean bl;
        this.resetAngles(i, j);
        if (((AxolotlEntity)axolotlEntity).isPlayingDead()) {
            this.setPlayingDeadAngles();
            return;
        }
        boolean bl2 = bl = Entity.squaredHorizontalLength(((Entity)axolotlEntity).getVelocity()) > 1.0E-7;
        if (((Entity)axolotlEntity).isInsideWaterOrBubbleColumn()) {
            if (bl) {
                this.setMovingInWaterAngles(h, j);
            } else {
                this.setStandingInWaterAngles(h);
            }
            return;
        }
        if (((Entity)axolotlEntity).isOnGround()) {
            if (bl) {
                this.setMovingOnGroundAngles(h);
            } else {
                this.setStandingOnGroundAngles(h);
            }
        }
    }

    /**
     * Resets the angles of the axolotl model.
     * 
     * @param headYaw the axolotl head yaw
     * @param headPitch the axolotl head pitch
     */
    private void resetAngles(float headYaw, float headPitch) {
        this.body.pivotX = 0.0f;
        this.head.pivotY = 0.0f;
        this.body.pivotY = 20.0f;
        this.body.setAngles(headPitch * ((float)Math.PI / 180), headYaw * ((float)Math.PI / 180), 0.0f);
        this.head.setAngles(0.0f, 0.0f, 0.0f);
        this.leftHindLeg.setAngles(0.0f, 0.0f, 0.0f);
        this.rightHindLeg.setAngles(0.0f, 0.0f, 0.0f);
        this.leftFrontLeg.setAngles(0.0f, 0.0f, 0.0f);
        this.rightFrontLeg.setAngles(0.0f, 0.0f, 0.0f);
        this.leftGills.setAngles(0.0f, 0.0f, 0.0f);
        this.rightGills.setAngles(0.0f, 0.0f, 0.0f);
        this.topGills.setAngles(0.0f, 0.0f, 0.0f);
        this.tail.setAngles(0.0f, 0.0f, 0.0f);
    }

    private void setStandingOnGroundAngles(float animationProgress) {
        float f = animationProgress * 0.09f;
        float g = MathHelper.sin(f);
        float h = MathHelper.cos(f);
        float i = g * g - 2.0f * g;
        float j = h * h - 3.0f * g;
        this.head.pitch = -0.09f * i;
        this.head.roll = -0.2f;
        this.tail.yaw = -0.1f + 0.1f * i;
        this.topGills.pitch = 0.6f + 0.05f * j;
        this.leftGills.yaw = -this.topGills.pitch;
        this.rightGills.yaw = -this.leftGills.yaw;
        this.leftHindLeg.setAngles(1.1f, 1.0f, 0.0f);
        this.leftFrontLeg.setAngles(0.8f, 2.3f, -0.5f);
        this.copyLegAngles();
    }

    private void setMovingOnGroundAngles(float animationProgress) {
        float f = animationProgress * 0.11f;
        float g = MathHelper.cos(f);
        float h = (g * g - 2.0f * g) / 5.0f;
        float i = 0.7f * g;
        this.tail.yaw = this.head.yaw = 0.09f * g;
        this.topGills.pitch = 0.6f - 0.08f * (g * g + 2.0f * MathHelper.sin(f));
        this.leftGills.yaw = -this.topGills.pitch;
        this.rightGills.yaw = -this.leftGills.yaw;
        this.leftHindLeg.setAngles(0.9424779f, 1.5f - h, -0.1f);
        this.leftFrontLeg.setAngles(1.0995574f, 1.5707964f - i, 0.0f);
        this.rightHindLeg.setAngles(this.leftHindLeg.pitch, -1.0f - h, 0.0f);
        this.rightFrontLeg.setAngles(this.leftFrontLeg.pitch, -1.5707964f - i, 0.0f);
    }

    private void setStandingInWaterAngles(float animationProgress) {
        float f = animationProgress * 0.075f;
        float g = MathHelper.cos(f);
        float h = MathHelper.sin(f) * 0.15f;
        this.body.pitch = -0.15f + 0.075f * g;
        this.body.pivotY -= h;
        this.head.pitch = -this.body.pitch;
        this.topGills.pitch = 0.2f * g;
        this.leftGills.yaw = -0.3f * g - 0.19f;
        this.rightGills.yaw = -this.leftGills.yaw;
        this.leftHindLeg.setAngles(2.3561945f - g * 0.11f, 0.47123894f, 1.7278761f);
        this.leftFrontLeg.setAngles(0.7853982f - g * 0.2f, 2.042035f, 0.0f);
        this.copyLegAngles();
        this.tail.yaw = 0.5f * g;
    }

    private void setMovingInWaterAngles(float animationProgress, float headPitch) {
        float f = animationProgress * 0.33f;
        float g = MathHelper.sin(f);
        float h = MathHelper.cos(f);
        float i = 0.13f * g;
        this.body.pitch = headPitch * ((float)Math.PI / 180) + i;
        this.head.pitch = -i * 1.8f;
        this.body.pivotY -= 0.45f * h;
        this.topGills.pitch = -0.5f * g - 0.8f;
        this.leftGills.yaw = 0.3f * g + 0.9f;
        this.rightGills.yaw = -this.leftGills.yaw;
        this.tail.yaw = 0.3f * MathHelper.cos(f * 0.9f);
        this.leftHindLeg.setAngles(1.8849558f, -0.4f * g, 1.5707964f);
        this.leftFrontLeg.setAngles(1.8849558f, -0.2f * h - 0.1f, 1.5707964f);
        this.copyLegAngles();
    }

    private void setPlayingDeadAngles() {
        this.leftHindLeg.setAngles(1.4137167f, 1.0995574f, 0.7853982f);
        this.leftFrontLeg.setAngles(0.7853982f, 2.042035f, 0.0f);
        this.body.pitch = -0.15f;
        this.body.roll = 0.35f;
        this.copyLegAngles();
    }

    /**
     * Copies and mirrors the left leg angles to the right leg angles.
     */
    private void copyLegAngles() {
        this.rightHindLeg.setAngles(this.leftHindLeg.pitch, -this.leftHindLeg.yaw, -this.leftHindLeg.roll);
        this.rightFrontLeg.setAngles(this.leftFrontLeg.pitch, -this.leftFrontLeg.yaw, -this.leftFrontLeg.roll);
    }
}

