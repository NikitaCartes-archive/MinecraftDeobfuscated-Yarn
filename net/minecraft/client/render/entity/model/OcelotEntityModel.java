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
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.util.math.Dilation;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class OcelotEntityModel<T extends Entity>
extends AnimalModel<T> {
    protected final ModelPart field_27454;
    protected final ModelPart field_27455;
    protected final ModelPart field_27456;
    protected final ModelPart field_27457;
    protected final ModelPart upperTail;
    protected final ModelPart lowerTail;
    protected final ModelPart head;
    protected final ModelPart torso;
    protected int animationState = 1;

    public OcelotEntityModel(ModelPart modelPart) {
        super(true, 10.0f, 4.0f);
        this.head = modelPart.getChild("head");
        this.torso = modelPart.getChild("body");
        this.upperTail = modelPart.getChild("tail1");
        this.lowerTail = modelPart.getChild("tail2");
        this.field_27454 = modelPart.getChild("left_hind_leg");
        this.field_27455 = modelPart.getChild("right_hind_leg");
        this.field_27456 = modelPart.getChild("left_front_leg");
        this.field_27457 = modelPart.getChild("right_front_leg");
    }

    public static ModelData getModelData(Dilation dilation) {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("head", ModelPartBuilder.create().cuboid("main", -2.5f, -2.0f, -3.0f, 5.0f, 4.0f, 5.0f, dilation).cuboid("nose", -1.5f, 0.0f, -4.0f, 3, 2, 2, dilation, 0, 24).cuboid("ear1", -2.0f, -3.0f, 0.0f, 1, 1, 2, dilation, 0, 10).cuboid("ear2", 1.0f, -3.0f, 0.0f, 1, 1, 2, dilation, 6, 10), ModelTransform.pivot(0.0f, 15.0f, -9.0f));
        modelPartData.addChild("body", ModelPartBuilder.create().uv(20, 0).cuboid(-2.0f, 3.0f, -8.0f, 4.0f, 16.0f, 6.0f, dilation), ModelTransform.of(0.0f, 12.0f, -10.0f, 1.5707964f, 0.0f, 0.0f));
        modelPartData.addChild("tail1", ModelPartBuilder.create().uv(0, 15).cuboid(-0.5f, 0.0f, 0.0f, 1.0f, 8.0f, 1.0f, dilation), ModelTransform.of(0.0f, 15.0f, 8.0f, 0.9f, 0.0f, 0.0f));
        modelPartData.addChild("tail2", ModelPartBuilder.create().uv(4, 15).cuboid(-0.5f, 0.0f, 0.0f, 1.0f, 8.0f, 1.0f, dilation), ModelTransform.pivot(0.0f, 20.0f, 14.0f));
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(8, 13).cuboid(-1.0f, 0.0f, 1.0f, 2.0f, 6.0f, 2.0f, dilation);
        modelPartData.addChild("left_hind_leg", modelPartBuilder, ModelTransform.pivot(1.1f, 18.0f, 5.0f));
        modelPartData.addChild("right_hind_leg", modelPartBuilder, ModelTransform.pivot(-1.1f, 18.0f, 5.0f));
        ModelPartBuilder modelPartBuilder2 = ModelPartBuilder.create().uv(40, 0).cuboid(-1.0f, 0.0f, 0.0f, 2.0f, 10.0f, 2.0f, dilation);
        modelPartData.addChild("left_front_leg", modelPartBuilder2, ModelTransform.pivot(1.2f, 14.1f, -5.0f));
        modelPartData.addChild("right_front_leg", modelPartBuilder2, ModelTransform.pivot(-1.2f, 14.1f, -5.0f));
        return modelData;
    }

    @Override
    protected Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of(this.head);
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of(this.torso, this.field_27454, this.field_27455, this.field_27456, this.field_27457, this.upperTail, this.lowerTail);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.head.pitch = headPitch * ((float)Math.PI / 180);
        this.head.yaw = headYaw * ((float)Math.PI / 180);
        if (this.animationState != 3) {
            this.torso.pitch = 1.5707964f;
            if (this.animationState == 2) {
                this.field_27454.pitch = MathHelper.cos(limbAngle * 0.6662f) * limbDistance;
                this.field_27455.pitch = MathHelper.cos(limbAngle * 0.6662f + 0.3f) * limbDistance;
                this.field_27456.pitch = MathHelper.cos(limbAngle * 0.6662f + (float)Math.PI + 0.3f) * limbDistance;
                this.field_27457.pitch = MathHelper.cos(limbAngle * 0.6662f + (float)Math.PI) * limbDistance;
                this.lowerTail.pitch = 1.7278761f + 0.31415927f * MathHelper.cos(limbAngle) * limbDistance;
            } else {
                this.field_27454.pitch = MathHelper.cos(limbAngle * 0.6662f) * limbDistance;
                this.field_27455.pitch = MathHelper.cos(limbAngle * 0.6662f + (float)Math.PI) * limbDistance;
                this.field_27456.pitch = MathHelper.cos(limbAngle * 0.6662f + (float)Math.PI) * limbDistance;
                this.field_27457.pitch = MathHelper.cos(limbAngle * 0.6662f) * limbDistance;
                this.lowerTail.pitch = this.animationState == 1 ? 1.7278761f + 0.7853982f * MathHelper.cos(limbAngle) * limbDistance : 1.7278761f + 0.47123894f * MathHelper.cos(limbAngle) * limbDistance;
            }
        }
    }

    @Override
    public void animateModel(T entity, float limbAngle, float limbDistance, float tickDelta) {
        this.torso.pivotY = 12.0f;
        this.torso.pivotZ = -10.0f;
        this.head.pivotY = 15.0f;
        this.head.pivotZ = -9.0f;
        this.upperTail.pivotY = 15.0f;
        this.upperTail.pivotZ = 8.0f;
        this.lowerTail.pivotY = 20.0f;
        this.lowerTail.pivotZ = 14.0f;
        this.field_27456.pivotY = 14.1f;
        this.field_27456.pivotZ = -5.0f;
        this.field_27457.pivotY = 14.1f;
        this.field_27457.pivotZ = -5.0f;
        this.field_27454.pivotY = 18.0f;
        this.field_27454.pivotZ = 5.0f;
        this.field_27455.pivotY = 18.0f;
        this.field_27455.pivotZ = 5.0f;
        this.upperTail.pitch = 0.9f;
        if (((Entity)entity).isInSneakingPose()) {
            this.torso.pivotY += 1.0f;
            this.head.pivotY += 2.0f;
            this.upperTail.pivotY += 1.0f;
            this.lowerTail.pivotY += -4.0f;
            this.lowerTail.pivotZ += 2.0f;
            this.upperTail.pitch = 1.5707964f;
            this.lowerTail.pitch = 1.5707964f;
            this.animationState = 0;
        } else if (((Entity)entity).isSprinting()) {
            this.lowerTail.pivotY = this.upperTail.pivotY;
            this.lowerTail.pivotZ += 2.0f;
            this.upperTail.pitch = 1.5707964f;
            this.lowerTail.pitch = 1.5707964f;
            this.animationState = 2;
        } else {
            this.animationState = 1;
        }
    }
}

