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
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.TintableAnimalModel;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class WolfEntityModel<T extends WolfEntity>
extends TintableAnimalModel<T> {
    /**
     * The key of the real head model part, whose value is {@value}.
     */
    private static final String REAL_HEAD = "real_head";
    /**
     * The key of the upper body model part, whose value is {@value}.
     */
    private static final String UPPER_BODY = "upper_body";
    /**
     * The key of the real tail model part, whose value is {@value}.
     */
    private static final String REAL_TAIL = "real_tail";
    /**
     * The main bone used to animate the head. Contains {@link #realHead} as one of its children.
     */
    private final ModelPart head;
    private final ModelPart realHead;
    private final ModelPart torso;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    /**
     * The main bone used to animate the tail. Contains {@link #realTail} as one of its children.
     */
    private final ModelPart tail;
    private final ModelPart realTail;
    private final ModelPart neck;
    private static final int field_32580 = 8;

    public WolfEntityModel(ModelPart root) {
        this.head = root.getChild(EntityModelPartNames.HEAD);
        this.realHead = this.head.getChild(REAL_HEAD);
        this.torso = root.getChild(EntityModelPartNames.BODY);
        this.neck = root.getChild(UPPER_BODY);
        this.rightHindLeg = root.getChild(EntityModelPartNames.RIGHT_HIND_LEG);
        this.leftHindLeg = root.getChild(EntityModelPartNames.LEFT_HIND_LEG);
        this.rightFrontLeg = root.getChild(EntityModelPartNames.RIGHT_FRONT_LEG);
        this.leftFrontLeg = root.getChild(EntityModelPartNames.LEFT_FRONT_LEG);
        this.tail = root.getChild(EntityModelPartNames.TAIL);
        this.realTail = this.tail.getChild(REAL_TAIL);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        float f = 13.5f;
        ModelPartData modelPartData2 = modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create(), ModelTransform.pivot(-1.0f, 13.5f, -7.0f));
        modelPartData2.addChild(REAL_HEAD, ModelPartBuilder.create().uv(0, 0).cuboid(-2.0f, -3.0f, -2.0f, 6.0f, 6.0f, 4.0f).uv(16, 14).cuboid(-2.0f, -5.0f, 0.0f, 2.0f, 2.0f, 1.0f).uv(16, 14).cuboid(2.0f, -5.0f, 0.0f, 2.0f, 2.0f, 1.0f).uv(0, 10).cuboid(-0.5f, 0.0f, -5.0f, 3.0f, 3.0f, 4.0f), ModelTransform.NONE);
        modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(18, 14).cuboid(-3.0f, -2.0f, -3.0f, 6.0f, 9.0f, 6.0f), ModelTransform.of(0.0f, 14.0f, 2.0f, 1.5707964f, 0.0f, 0.0f));
        modelPartData.addChild(UPPER_BODY, ModelPartBuilder.create().uv(21, 0).cuboid(-3.0f, -3.0f, -3.0f, 8.0f, 6.0f, 7.0f), ModelTransform.of(-1.0f, 14.0f, -3.0f, 1.5707964f, 0.0f, 0.0f));
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(0, 18).cuboid(0.0f, 0.0f, -1.0f, 2.0f, 8.0f, 2.0f);
        modelPartData.addChild(EntityModelPartNames.RIGHT_HIND_LEG, modelPartBuilder, ModelTransform.pivot(-2.5f, 16.0f, 7.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_HIND_LEG, modelPartBuilder, ModelTransform.pivot(0.5f, 16.0f, 7.0f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_FRONT_LEG, modelPartBuilder, ModelTransform.pivot(-2.5f, 16.0f, -4.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_FRONT_LEG, modelPartBuilder, ModelTransform.pivot(0.5f, 16.0f, -4.0f));
        ModelPartData modelPartData3 = modelPartData.addChild(EntityModelPartNames.TAIL, ModelPartBuilder.create(), ModelTransform.of(-1.0f, 12.0f, 8.0f, 0.62831855f, 0.0f, 0.0f));
        modelPartData3.addChild(REAL_TAIL, ModelPartBuilder.create().uv(9, 18).cuboid(0.0f, 0.0f, -1.0f, 2.0f, 8.0f, 2.0f), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    protected Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of(this.head);
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of(this.torso, this.rightHindLeg, this.leftHindLeg, this.rightFrontLeg, this.leftFrontLeg, this.tail, this.neck);
    }

    @Override
    public void animateModel(T wolfEntity, float f, float g, float h) {
        this.tail.yaw = wolfEntity.hasAngerTime() ? 0.0f : MathHelper.cos(f * 0.6662f) * 1.4f * g;
        if (((TameableEntity)wolfEntity).isInSittingPose()) {
            this.neck.setPivot(-1.0f, 16.0f, -3.0f);
            this.neck.pitch = 1.2566371f;
            this.neck.yaw = 0.0f;
            this.torso.setPivot(0.0f, 18.0f, 0.0f);
            this.torso.pitch = 0.7853982f;
            this.tail.setPivot(-1.0f, 21.0f, 6.0f);
            this.rightHindLeg.setPivot(-2.5f, 22.7f, 2.0f);
            this.rightHindLeg.pitch = 4.712389f;
            this.leftHindLeg.setPivot(0.5f, 22.7f, 2.0f);
            this.leftHindLeg.pitch = 4.712389f;
            this.rightFrontLeg.pitch = 5.811947f;
            this.rightFrontLeg.setPivot(-2.49f, 17.0f, -4.0f);
            this.leftFrontLeg.pitch = 5.811947f;
            this.leftFrontLeg.setPivot(0.51f, 17.0f, -4.0f);
        } else {
            this.torso.setPivot(0.0f, 14.0f, 2.0f);
            this.torso.pitch = 1.5707964f;
            this.neck.setPivot(-1.0f, 14.0f, -3.0f);
            this.neck.pitch = this.torso.pitch;
            this.tail.setPivot(-1.0f, 12.0f, 8.0f);
            this.rightHindLeg.setPivot(-2.5f, 16.0f, 7.0f);
            this.leftHindLeg.setPivot(0.5f, 16.0f, 7.0f);
            this.rightFrontLeg.setPivot(-2.5f, 16.0f, -4.0f);
            this.leftFrontLeg.setPivot(0.5f, 16.0f, -4.0f);
            this.rightHindLeg.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g;
            this.leftHindLeg.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
            this.rightFrontLeg.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
            this.leftFrontLeg.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g;
        }
        this.realHead.roll = ((WolfEntity)wolfEntity).getBegAnimationProgress(h) + ((WolfEntity)wolfEntity).getShakeAnimationProgress(h, 0.0f);
        this.neck.roll = ((WolfEntity)wolfEntity).getShakeAnimationProgress(h, -0.08f);
        this.torso.roll = ((WolfEntity)wolfEntity).getShakeAnimationProgress(h, -0.16f);
        this.realTail.roll = ((WolfEntity)wolfEntity).getShakeAnimationProgress(h, -0.2f);
    }

    @Override
    public void setAngles(T wolfEntity, float f, float g, float h, float i, float j) {
        this.head.pitch = j * ((float)Math.PI / 180);
        this.head.yaw = i * ((float)Math.PI / 180);
        this.tail.pitch = h;
    }
}

