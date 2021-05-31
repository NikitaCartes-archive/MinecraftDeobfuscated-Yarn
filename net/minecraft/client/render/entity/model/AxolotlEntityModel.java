/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import java.util.Map;
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
import net.minecraft.util.math.Vec3f;

/**
 * Represents the model of an {@linkplain AxolotlEntity}.
 * 
 * <div class="fabric">
 * <table border=1>
 * <caption>Model parts of this model</caption>
 * <tr>
 *   <th>Part Name</th><th>Parent</th><th>Corresponding Field</th>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#BODY}</td><td>Root part</td><td>{@link #body}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#HEAD}</td><td>{@value EntityModelPartNames#BODY}</td><td>{@link #head}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#TOP_GILLS}</td><td>{@value EntityModelPartNames#HEAD}</td><td>{@link #topGills}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#LEFT_GILLS}</td><td>{@value EntityModelPartNames#HEAD}</td><td>{@link #leftGills}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#RIGHT_GILLS}</td><td>{@value EntityModelPartNames#HEAD}</td><td>{@link #rightGills}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#RIGHT_HIND_LEG}</td><td>{@value EntityModelPartNames#BODY}</td><td>{@link #rightHindLeg}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#LEFT_HIND_LEG}</td><td>{@value EntityModelPartNames#BODY}</td><td>{@link #leftHindLeg}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#RIGHT_FRONT_LEG}</td><td>{@value EntityModelPartNames#BODY}</td><td>{@link #rightFrontLeg}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#LEFT_FRONT_LEG}</td><td>{@value EntityModelPartNames#BODY}</td><td>{@link #leftFrontLeg}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#TAIL}</td><td>{@value EntityModelPartNames#BODY}</td><td>{@link #tail}</td>
 * </tr>
 * </table>
 * </div>
 */
@Environment(value=EnvType.CLIENT)
public class AxolotlEntityModel<T extends AxolotlEntity>
extends AnimalModel<T> {
    /**
     * Represents the pitch value {@value} used for the legs of the axolotl when it is moving in water.
     */
    public static final float MOVING_IN_WATER_LEG_PITCH = 1.8849558f;
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
        this.resetAngles(axolotlEntity, i, j);
        if (((AxolotlEntity)axolotlEntity).isPlayingDead()) {
            this.setPlayingDeadAngles(i);
            this.updateAnglesCache(axolotlEntity);
            return;
        }
        boolean bl2 = bl = ((Entity)axolotlEntity).getVelocity().horizontalLengthSquared() > 1.0E-7 || ((Entity)axolotlEntity).getPitch() != ((AxolotlEntity)axolotlEntity).prevPitch || ((Entity)axolotlEntity).getYaw() != ((AxolotlEntity)axolotlEntity).prevYaw || ((AxolotlEntity)axolotlEntity).lastRenderX != ((Entity)axolotlEntity).getX() || ((AxolotlEntity)axolotlEntity).lastRenderZ != ((Entity)axolotlEntity).getZ();
        if (((Entity)axolotlEntity).isInsideWaterOrBubbleColumn()) {
            if (bl) {
                this.setMovingInWaterAngles(h, j);
            } else {
                this.setStandingInWaterAngles(h);
            }
            this.updateAnglesCache(axolotlEntity);
            return;
        }
        if (((Entity)axolotlEntity).isOnGround()) {
            if (bl) {
                this.setMovingOnGroundAngles(h, i);
            } else {
                this.setStandingOnGroundAngles(h, i);
            }
        }
        this.updateAnglesCache(axolotlEntity);
    }

    private void updateAnglesCache(T axolotl) {
        Map<String, Vec3f> map = ((AxolotlEntity)axolotl).getModelAngles();
        map.put("body", this.getAngles(this.body));
        map.put("head", this.getAngles(this.head));
        map.put("right_hind_leg", this.getAngles(this.rightHindLeg));
        map.put("left_hind_leg", this.getAngles(this.leftHindLeg));
        map.put("right_front_leg", this.getAngles(this.rightFrontLeg));
        map.put("left_front_leg", this.getAngles(this.leftFrontLeg));
        map.put("tail", this.getAngles(this.tail));
        map.put("top_gills", this.getAngles(this.topGills));
        map.put("left_gills", this.getAngles(this.leftGills));
        map.put("right_gills", this.getAngles(this.rightGills));
    }

    private Vec3f getAngles(ModelPart part) {
        return new Vec3f(part.pitch, part.yaw, part.roll);
    }

    private void setAngles(ModelPart part, Vec3f angles) {
        part.setAngles(angles.getX(), angles.getY(), angles.getZ());
    }

    /**
     * Resets the angles of the axolotl model.
     */
    private void resetAngles(T axolotl, float headYaw, float headPitch) {
        this.body.pivotX = 0.0f;
        this.head.pivotY = 0.0f;
        this.body.pivotY = 20.0f;
        Map<String, Vec3f> map = ((AxolotlEntity)axolotl).getModelAngles();
        if (map.isEmpty()) {
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
        } else {
            this.setAngles(this.body, map.get("body"));
            this.setAngles(this.head, map.get("head"));
            this.setAngles(this.leftHindLeg, map.get("left_hind_leg"));
            this.setAngles(this.rightHindLeg, map.get("right_hind_leg"));
            this.setAngles(this.leftFrontLeg, map.get("left_front_leg"));
            this.setAngles(this.rightFrontLeg, map.get("right_front_leg"));
            this.setAngles(this.leftGills, map.get("left_gills"));
            this.setAngles(this.rightGills, map.get("right_gills"));
            this.setAngles(this.topGills, map.get("top_gills"));
            this.setAngles(this.tail, map.get("tail"));
        }
    }

    private float lerpAngleDegrees(float start, float end) {
        return this.lerpAngleDegress(0.05f, start, end);
    }

    private float lerpAngleDegress(float delta, float start, float end) {
        return MathHelper.lerpAngleDegrees(delta, start, end);
    }

    private void setAngles(ModelPart part, float pitch, float yaw, float roll) {
        part.setAngles(this.lerpAngleDegrees(part.pitch, pitch), this.lerpAngleDegrees(part.yaw, yaw), this.lerpAngleDegrees(part.roll, roll));
    }

    private void setStandingOnGroundAngles(float animationProgress, float headYaw) {
        float f = animationProgress * 0.09f;
        float g = MathHelper.sin(f);
        float h = MathHelper.cos(f);
        float i = g * g - 2.0f * g;
        float j = h * h - 3.0f * g;
        this.head.pitch = this.lerpAngleDegrees(this.head.pitch, -0.09f * i);
        this.head.yaw = this.lerpAngleDegrees(this.head.yaw, 0.0f);
        this.head.roll = this.lerpAngleDegrees(this.head.roll, -0.2f);
        this.tail.yaw = this.lerpAngleDegrees(this.tail.yaw, -0.1f + 0.1f * i);
        this.topGills.pitch = this.lerpAngleDegrees(this.topGills.pitch, 0.6f + 0.05f * j);
        this.leftGills.yaw = this.lerpAngleDegrees(this.leftGills.yaw, -this.topGills.pitch);
        this.rightGills.yaw = this.lerpAngleDegrees(this.rightGills.yaw, -this.leftGills.yaw);
        this.setAngles(this.leftHindLeg, 1.1f, 1.0f, 0.0f);
        this.setAngles(this.leftFrontLeg, 0.8f, 2.3f, -0.5f);
        this.copyLegAngles();
        this.body.pitch = this.lerpAngleDegress(0.2f, this.body.pitch, 0.0f);
        this.body.yaw = this.lerpAngleDegrees(this.body.yaw, headYaw * ((float)Math.PI / 180));
        this.body.roll = this.lerpAngleDegrees(this.body.roll, 0.0f);
    }

    private void setMovingOnGroundAngles(float animationProgress, float headYaw) {
        float f = animationProgress * 0.11f;
        float g = MathHelper.cos(f);
        float h = (g * g - 2.0f * g) / 5.0f;
        float i = 0.7f * g;
        this.head.pitch = this.lerpAngleDegrees(this.head.pitch, 0.0f);
        this.head.yaw = this.lerpAngleDegrees(this.head.yaw, 0.09f * g);
        this.head.roll = this.lerpAngleDegrees(this.head.roll, 0.0f);
        this.tail.yaw = this.lerpAngleDegrees(this.tail.yaw, this.head.yaw);
        this.topGills.pitch = this.lerpAngleDegrees(this.topGills.pitch, 0.6f - 0.08f * (g * g + 2.0f * MathHelper.sin(f)));
        this.leftGills.yaw = this.lerpAngleDegrees(this.leftGills.yaw, -this.topGills.pitch);
        this.rightGills.yaw = this.lerpAngleDegrees(this.rightGills.yaw, -this.leftGills.yaw);
        this.setAngles(this.leftHindLeg, 0.9424779f, 1.5f - h, -0.1f);
        this.setAngles(this.leftFrontLeg, 1.0995574f, 1.5707964f - i, 0.0f);
        this.setAngles(this.rightHindLeg, this.leftHindLeg.pitch, -1.0f - h, 0.0f);
        this.setAngles(this.rightFrontLeg, this.leftFrontLeg.pitch, -1.5707964f - i, 0.0f);
        this.body.pitch = this.lerpAngleDegress(0.2f, this.body.pitch, 0.0f);
        this.body.yaw = this.lerpAngleDegrees(this.body.yaw, headYaw * ((float)Math.PI / 180));
        this.body.roll = this.lerpAngleDegrees(this.body.roll, 0.0f);
    }

    private void setStandingInWaterAngles(float animationProgress) {
        float f = animationProgress * 0.075f;
        float g = MathHelper.cos(f);
        float h = MathHelper.sin(f) * 0.15f;
        this.body.pitch = this.lerpAngleDegrees(this.body.pitch, -0.15f + 0.075f * g);
        this.body.pivotY -= h;
        this.head.pitch = this.lerpAngleDegrees(this.head.pitch, -this.body.pitch);
        this.topGills.pitch = this.lerpAngleDegrees(this.topGills.pitch, 0.2f * g);
        this.leftGills.yaw = this.lerpAngleDegrees(this.leftGills.yaw, -0.3f * g - 0.19f);
        this.rightGills.yaw = this.lerpAngleDegrees(this.rightGills.yaw, -this.leftGills.yaw);
        this.setAngles(this.leftHindLeg, 2.3561945f - g * 0.11f, 0.47123894f, 1.7278761f);
        this.setAngles(this.leftFrontLeg, 0.7853982f - g * 0.2f, 2.042035f, 0.0f);
        this.copyLegAngles();
        this.tail.yaw = this.lerpAngleDegrees(this.tail.yaw, 0.5f * g);
        this.head.yaw = this.lerpAngleDegrees(this.head.yaw, 0.0f);
        this.head.roll = this.lerpAngleDegrees(this.head.roll, 0.0f);
    }

    private void setMovingInWaterAngles(float animationProgress, float headPitch) {
        float f = animationProgress * 0.33f;
        float g = MathHelper.sin(f);
        float h = MathHelper.cos(f);
        float i = 0.13f * g;
        this.body.pitch = this.lerpAngleDegress(0.1f, this.body.pitch, headPitch * ((float)Math.PI / 180) + i);
        this.head.pitch = -i * 1.8f;
        this.body.pivotY -= 0.45f * h;
        this.topGills.pitch = this.lerpAngleDegrees(this.topGills.pitch, -0.5f * g - 0.8f);
        this.leftGills.yaw = this.lerpAngleDegrees(this.leftGills.yaw, 0.3f * g + 0.9f);
        this.rightGills.yaw = this.lerpAngleDegrees(this.rightGills.yaw, -this.leftGills.yaw);
        this.tail.yaw = this.lerpAngleDegrees(this.tail.yaw, 0.3f * MathHelper.cos(f * 0.9f));
        this.setAngles(this.leftHindLeg, 1.8849558f, -0.4f * g, 1.5707964f);
        this.setAngles(this.leftFrontLeg, 1.8849558f, -0.2f * h - 0.1f, 1.5707964f);
        this.copyLegAngles();
        this.head.yaw = this.lerpAngleDegrees(this.head.yaw, 0.0f);
        this.head.roll = this.lerpAngleDegrees(this.head.roll, 0.0f);
    }

    private void setPlayingDeadAngles(float headYaw) {
        this.setAngles(this.leftHindLeg, 1.4137167f, 1.0995574f, 0.7853982f);
        this.setAngles(this.leftFrontLeg, 0.7853982f, 2.042035f, 0.0f);
        this.body.pitch = this.lerpAngleDegrees(this.body.pitch, -0.15f);
        this.body.roll = this.lerpAngleDegrees(this.body.roll, 0.35f);
        this.copyLegAngles();
        this.body.yaw = this.lerpAngleDegrees(this.body.yaw, headYaw * ((float)Math.PI / 180));
        this.head.pitch = this.lerpAngleDegrees(this.head.pitch, 0.0f);
        this.head.yaw = this.lerpAngleDegrees(this.head.yaw, 0.0f);
        this.head.roll = this.lerpAngleDegrees(this.head.roll, 0.0f);
        this.tail.yaw = this.lerpAngleDegrees(this.tail.yaw, 0.0f);
        this.setAngles(this.topGills, 0.0f, 0.0f, 0.0f);
        this.setAngles(this.leftGills, 0.0f, 0.0f, 0.0f);
        this.setAngles(this.rightGills, 0.0f, 0.0f, 0.0f);
    }

    /**
     * Copies and mirrors the left leg angles to the right leg angles.
     */
    private void copyLegAngles() {
        this.setAngles(this.rightHindLeg, this.leftHindLeg.pitch, -this.leftHindLeg.yaw, -this.leftHindLeg.roll);
        this.setAngles(this.rightFrontLeg, this.leftFrontLeg.pitch, -this.leftFrontLeg.yaw, -this.leftFrontLeg.roll);
    }
}

