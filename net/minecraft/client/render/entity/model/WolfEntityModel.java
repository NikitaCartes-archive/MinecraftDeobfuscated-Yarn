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
import net.minecraft.client.render.entity.model.TintableAnimalModel;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class WolfEntityModel<T extends WolfEntity>
extends TintableAnimalModel<T> {
    private final ModelPart head;
    private final ModelPart field_20788;
    private final ModelPart torso;
    private final ModelPart field_27538;
    private final ModelPart field_27539;
    private final ModelPart field_27540;
    private final ModelPart field_27541;
    private final ModelPart tail;
    private final ModelPart field_20789;
    private final ModelPart neck;

    public WolfEntityModel(ModelPart modelPart) {
        this.head = modelPart.getChild("head");
        this.field_20788 = this.head.getChild("real_head");
        this.torso = modelPart.getChild("body");
        this.neck = modelPart.getChild("upper_body");
        this.field_27538 = modelPart.getChild("right_hind_leg");
        this.field_27539 = modelPart.getChild("left_hind_leg");
        this.field_27540 = modelPart.getChild("right_front_leg");
        this.field_27541 = modelPart.getChild("left_front_leg");
        this.tail = modelPart.getChild("tail");
        this.field_20789 = this.tail.getChild("real_tail");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        float f = 13.5f;
        ModelPartData modelPartData2 = modelPartData.addChild("head", ModelPartBuilder.create(), ModelTransform.pivot(-1.0f, 13.5f, -7.0f));
        modelPartData2.addChild("real_head", ModelPartBuilder.create().uv(0, 0).cuboid(-2.0f, -3.0f, -2.0f, 6.0f, 6.0f, 4.0f).uv(16, 14).cuboid(-2.0f, -5.0f, 0.0f, 2.0f, 2.0f, 1.0f).uv(16, 14).cuboid(2.0f, -5.0f, 0.0f, 2.0f, 2.0f, 1.0f).uv(0, 10).cuboid(-0.5f, 0.0f, -5.0f, 3.0f, 3.0f, 4.0f), ModelTransform.NONE);
        modelPartData.addChild("body", ModelPartBuilder.create().uv(18, 14).cuboid(-3.0f, -2.0f, -3.0f, 6.0f, 9.0f, 6.0f), ModelTransform.of(0.0f, 14.0f, 2.0f, 1.5707964f, 0.0f, 0.0f));
        modelPartData.addChild("upper_body", ModelPartBuilder.create().uv(21, 0).cuboid(-3.0f, -3.0f, -3.0f, 8.0f, 6.0f, 7.0f), ModelTransform.of(-1.0f, 14.0f, -3.0f, 1.5707964f, 0.0f, 0.0f));
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(0, 18).cuboid(0.0f, 0.0f, -1.0f, 2.0f, 8.0f, 2.0f);
        modelPartData.addChild("right_hind_leg", modelPartBuilder, ModelTransform.pivot(-2.5f, 16.0f, 7.0f));
        modelPartData.addChild("left_hind_leg", modelPartBuilder, ModelTransform.pivot(0.5f, 16.0f, 7.0f));
        modelPartData.addChild("right_front_leg", modelPartBuilder, ModelTransform.pivot(-2.5f, 16.0f, -4.0f));
        modelPartData.addChild("left_front_leg", modelPartBuilder, ModelTransform.pivot(0.5f, 16.0f, -4.0f));
        ModelPartData modelPartData3 = modelPartData.addChild("tail", ModelPartBuilder.create(), ModelTransform.of(-1.0f, 12.0f, 8.0f, 0.62831855f, 0.0f, 0.0f));
        modelPartData3.addChild("real_tail", ModelPartBuilder.create().uv(9, 18).cuboid(0.0f, 0.0f, -1.0f, 2.0f, 8.0f, 2.0f), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    protected Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of(this.head);
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of(this.torso, this.field_27538, this.field_27539, this.field_27540, this.field_27541, this.tail, this.neck);
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
            this.field_27538.setPivot(-2.5f, 22.7f, 2.0f);
            this.field_27538.pitch = 4.712389f;
            this.field_27539.setPivot(0.5f, 22.7f, 2.0f);
            this.field_27539.pitch = 4.712389f;
            this.field_27540.pitch = 5.811947f;
            this.field_27540.setPivot(-2.49f, 17.0f, -4.0f);
            this.field_27541.pitch = 5.811947f;
            this.field_27541.setPivot(0.51f, 17.0f, -4.0f);
        } else {
            this.torso.setPivot(0.0f, 14.0f, 2.0f);
            this.torso.pitch = 1.5707964f;
            this.neck.setPivot(-1.0f, 14.0f, -3.0f);
            this.neck.pitch = this.torso.pitch;
            this.tail.setPivot(-1.0f, 12.0f, 8.0f);
            this.field_27538.setPivot(-2.5f, 16.0f, 7.0f);
            this.field_27539.setPivot(0.5f, 16.0f, 7.0f);
            this.field_27540.setPivot(-2.5f, 16.0f, -4.0f);
            this.field_27541.setPivot(0.5f, 16.0f, -4.0f);
            this.field_27538.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g;
            this.field_27539.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
            this.field_27540.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
            this.field_27541.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g;
        }
        this.field_20788.roll = ((WolfEntity)wolfEntity).getBegAnimationProgress(h) + ((WolfEntity)wolfEntity).getShakeAnimationProgress(h, 0.0f);
        this.neck.roll = ((WolfEntity)wolfEntity).getShakeAnimationProgress(h, -0.08f);
        this.torso.roll = ((WolfEntity)wolfEntity).getShakeAnimationProgress(h, -0.16f);
        this.field_20789.roll = ((WolfEntity)wolfEntity).getShakeAnimationProgress(h, -0.2f);
    }

    @Override
    public void setAngles(T wolfEntity, float f, float g, float h, float i, float j) {
        this.head.pitch = j * ((float)Math.PI / 180);
        this.head.yaw = i * ((float)Math.PI / 180);
        this.tail.pitch = h;
    }
}

