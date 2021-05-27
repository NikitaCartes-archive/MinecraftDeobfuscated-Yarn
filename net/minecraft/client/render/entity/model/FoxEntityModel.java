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
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.util.math.MathHelper;

/**
 * Represents the model of a {@linkplain FoxEntity}.
 * 
 * <div class="fabric">
 * <table border=1>
 * <caption>Model parts of this model</caption>
 * <tr>
 *   <th>Part Name</th><th>Parent</th><th>Corresponding Field</th>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#HEAD}</td><td>Root part</td><td>{@link #head}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#RIGHT_EAR}</td><td>{@value EntityModelPartNames#HEAD}</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#LEFT_EAR}</td><td>{@value EntityModelPartNames#HEAD}</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#NOSE}</td><td>{@value EntityModelPartNames#HEAD}</td><td></td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#BODY}</td><td>Root part</td><td>{@link #body}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#TAIL}</td><td>{@value EntityModelPartNames#BODY}</td><td>{@link #tail}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#RIGHT_HIND_LEG}</td><td>Root part</td><td>{@link #rightHindLeg}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#LEFT_HIND_LEG}</td><td>Root part</td><td>{@link #leftHindLeg}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#RIGHT_FRONT_LEG}</td><td>Root part</td><td>{@link #rightFrontLeg}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#LEFT_FRONT_LEG}</td><td>Root part</td><td>{@link #leftFrontLeg}</td>
 * </tr>
 * </table>
 * </div>
 */
@Environment(value=EnvType.CLIENT)
public class FoxEntityModel<T extends FoxEntity>
extends AnimalModel<T> {
    public final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart tail;
    private static final int field_32477 = 6;
    private static final float HEAD_Y_PIVOT = 16.5f;
    private static final float LEG_Y_PIVOT = 17.5f;
    private float legPitchModifier;

    public FoxEntityModel(ModelPart root) {
        super(true, 8.0f, 3.35f);
        this.head = root.getChild(EntityModelPartNames.HEAD);
        this.body = root.getChild(EntityModelPartNames.BODY);
        this.rightHindLeg = root.getChild(EntityModelPartNames.RIGHT_HIND_LEG);
        this.leftHindLeg = root.getChild(EntityModelPartNames.LEFT_HIND_LEG);
        this.rightFrontLeg = root.getChild(EntityModelPartNames.RIGHT_FRONT_LEG);
        this.leftFrontLeg = root.getChild(EntityModelPartNames.LEFT_FRONT_LEG);
        this.tail = this.body.getChild(EntityModelPartNames.TAIL);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(1, 5).cuboid(-3.0f, -2.0f, -5.0f, 8.0f, 6.0f, 6.0f), ModelTransform.pivot(-1.0f, 16.5f, -3.0f));
        modelPartData2.addChild(EntityModelPartNames.RIGHT_EAR, ModelPartBuilder.create().uv(8, 1).cuboid(-3.0f, -4.0f, -4.0f, 2.0f, 2.0f, 1.0f), ModelTransform.NONE);
        modelPartData2.addChild(EntityModelPartNames.LEFT_EAR, ModelPartBuilder.create().uv(15, 1).cuboid(3.0f, -4.0f, -4.0f, 2.0f, 2.0f, 1.0f), ModelTransform.NONE);
        modelPartData2.addChild(EntityModelPartNames.NOSE, ModelPartBuilder.create().uv(6, 18).cuboid(-1.0f, 2.01f, -8.0f, 4.0f, 2.0f, 3.0f), ModelTransform.NONE);
        ModelPartData modelPartData3 = modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(24, 15).cuboid(-3.0f, 3.999f, -3.5f, 6.0f, 11.0f, 6.0f), ModelTransform.of(0.0f, 16.0f, -6.0f, 1.5707964f, 0.0f, 0.0f));
        Dilation dilation = new Dilation(0.001f);
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(4, 24).cuboid(2.0f, 0.5f, -1.0f, 2.0f, 6.0f, 2.0f, dilation);
        ModelPartBuilder modelPartBuilder2 = ModelPartBuilder.create().uv(13, 24).cuboid(2.0f, 0.5f, -1.0f, 2.0f, 6.0f, 2.0f, dilation);
        modelPartData.addChild(EntityModelPartNames.RIGHT_HIND_LEG, modelPartBuilder2, ModelTransform.pivot(-5.0f, 17.5f, 7.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_HIND_LEG, modelPartBuilder, ModelTransform.pivot(-1.0f, 17.5f, 7.0f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_FRONT_LEG, modelPartBuilder2, ModelTransform.pivot(-5.0f, 17.5f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_FRONT_LEG, modelPartBuilder, ModelTransform.pivot(-1.0f, 17.5f, 0.0f));
        modelPartData3.addChild(EntityModelPartNames.TAIL, ModelPartBuilder.create().uv(30, 0).cuboid(2.0f, 0.0f, -1.0f, 4.0f, 9.0f, 5.0f), ModelTransform.of(-4.0f, 15.0f, -1.0f, -0.05235988f, 0.0f, 0.0f));
        return TexturedModelData.of(modelData, 48, 32);
    }

    @Override
    public void animateModel(T foxEntity, float f, float g, float h) {
        this.body.pitch = 1.5707964f;
        this.tail.pitch = -0.05235988f;
        this.rightHindLeg.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g;
        this.leftHindLeg.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
        this.rightFrontLeg.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
        this.leftFrontLeg.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g;
        this.head.setPivot(-1.0f, 16.5f, -3.0f);
        this.head.yaw = 0.0f;
        this.head.roll = ((FoxEntity)foxEntity).getHeadRoll(h);
        this.rightHindLeg.visible = true;
        this.leftHindLeg.visible = true;
        this.rightFrontLeg.visible = true;
        this.leftFrontLeg.visible = true;
        this.body.setPivot(0.0f, 16.0f, -6.0f);
        this.body.roll = 0.0f;
        this.rightHindLeg.setPivot(-5.0f, 17.5f, 7.0f);
        this.leftHindLeg.setPivot(-1.0f, 17.5f, 7.0f);
        if (((FoxEntity)foxEntity).isInSneakingPose()) {
            this.body.pitch = 1.6755161f;
            float i = ((FoxEntity)foxEntity).getBodyRotationHeightOffset(h);
            this.body.setPivot(0.0f, 16.0f + ((FoxEntity)foxEntity).getBodyRotationHeightOffset(h), -6.0f);
            this.head.setPivot(-1.0f, 16.5f + i, -3.0f);
            this.head.yaw = 0.0f;
        } else if (((FoxEntity)foxEntity).isSleeping()) {
            this.body.roll = -1.5707964f;
            this.body.setPivot(0.0f, 21.0f, -6.0f);
            this.tail.pitch = -2.6179938f;
            if (this.child) {
                this.tail.pitch = -2.1816616f;
                this.body.setPivot(0.0f, 21.0f, -2.0f);
            }
            this.head.setPivot(1.0f, 19.49f, -3.0f);
            this.head.pitch = 0.0f;
            this.head.yaw = -2.0943952f;
            this.head.roll = 0.0f;
            this.rightHindLeg.visible = false;
            this.leftHindLeg.visible = false;
            this.rightFrontLeg.visible = false;
            this.leftFrontLeg.visible = false;
        } else if (((FoxEntity)foxEntity).isSitting()) {
            this.body.pitch = 0.5235988f;
            this.body.setPivot(0.0f, 9.0f, -3.0f);
            this.tail.pitch = 0.7853982f;
            this.tail.setPivot(-4.0f, 15.0f, -2.0f);
            this.head.setPivot(-1.0f, 10.0f, -0.25f);
            this.head.pitch = 0.0f;
            this.head.yaw = 0.0f;
            if (this.child) {
                this.head.setPivot(-1.0f, 13.0f, -3.75f);
            }
            this.rightHindLeg.pitch = -1.3089969f;
            this.rightHindLeg.setPivot(-5.0f, 21.5f, 6.75f);
            this.leftHindLeg.pitch = -1.3089969f;
            this.leftHindLeg.setPivot(-1.0f, 21.5f, 6.75f);
            this.rightFrontLeg.pitch = -0.2617994f;
            this.leftFrontLeg.pitch = -0.2617994f;
        }
    }

    @Override
    protected Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of(this.head);
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of(this.body, this.rightHindLeg, this.leftHindLeg, this.rightFrontLeg, this.leftFrontLeg);
    }

    @Override
    public void setAngles(T foxEntity, float f, float g, float h, float i, float j) {
        float k;
        if (!(((FoxEntity)foxEntity).isSleeping() || ((FoxEntity)foxEntity).isWalking() || ((FoxEntity)foxEntity).isInSneakingPose())) {
            this.head.pitch = j * ((float)Math.PI / 180);
            this.head.yaw = i * ((float)Math.PI / 180);
        }
        if (((FoxEntity)foxEntity).isSleeping()) {
            this.head.pitch = 0.0f;
            this.head.yaw = -2.0943952f;
            this.head.roll = MathHelper.cos(h * 0.027f) / 22.0f;
        }
        if (((FoxEntity)foxEntity).isInSneakingPose()) {
            this.body.yaw = k = MathHelper.cos(h) * 0.01f;
            this.rightHindLeg.roll = k;
            this.leftHindLeg.roll = k;
            this.rightFrontLeg.roll = k / 2.0f;
            this.leftFrontLeg.roll = k / 2.0f;
        }
        if (((FoxEntity)foxEntity).isWalking()) {
            k = 0.1f;
            this.legPitchModifier += 0.67f;
            this.rightHindLeg.pitch = MathHelper.cos(this.legPitchModifier * 0.4662f) * 0.1f;
            this.leftHindLeg.pitch = MathHelper.cos(this.legPitchModifier * 0.4662f + (float)Math.PI) * 0.1f;
            this.rightFrontLeg.pitch = MathHelper.cos(this.legPitchModifier * 0.4662f + (float)Math.PI) * 0.1f;
            this.leftFrontLeg.pitch = MathHelper.cos(this.legPitchModifier * 0.4662f) * 0.1f;
        }
    }
}

