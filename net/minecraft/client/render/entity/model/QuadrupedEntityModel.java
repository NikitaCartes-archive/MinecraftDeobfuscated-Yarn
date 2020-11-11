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
public class QuadrupedEntityModel<T extends Entity>
extends AnimalModel<T> {
    protected final ModelPart head;
    protected final ModelPart torso;
    protected final ModelPart field_27476;
    protected final ModelPart field_27477;
    protected final ModelPart field_27478;
    protected final ModelPart field_27479;

    protected QuadrupedEntityModel(ModelPart modelPart, boolean bl, float f, float g, float h, float i, int j) {
        super(bl, f, g, h, i, j);
        this.head = modelPart.getChild("head");
        this.torso = modelPart.getChild("body");
        this.field_27476 = modelPart.getChild("right_hind_leg");
        this.field_27477 = modelPart.getChild("left_hind_leg");
        this.field_27478 = modelPart.getChild("right_front_leg");
        this.field_27479 = modelPart.getChild("left_front_leg");
    }

    public static ModelData method_32033(int i, Dilation dilation) {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -4.0f, -8.0f, 8.0f, 8.0f, 8.0f, dilation), ModelTransform.pivot(0.0f, 18 - i, -6.0f));
        modelPartData.addChild("body", ModelPartBuilder.create().uv(28, 8).cuboid(-5.0f, -10.0f, -7.0f, 10.0f, 16.0f, 8.0f, dilation), ModelTransform.of(0.0f, 17 - i, 2.0f, 1.5707964f, 0.0f, 0.0f));
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(0, 16).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, (float)i, 4.0f, dilation);
        modelPartData.addChild("right_hind_leg", modelPartBuilder, ModelTransform.pivot(-3.0f, 24 - i, 7.0f));
        modelPartData.addChild("left_hind_leg", modelPartBuilder, ModelTransform.pivot(3.0f, 24 - i, 7.0f));
        modelPartData.addChild("right_front_leg", modelPartBuilder, ModelTransform.pivot(-3.0f, 24 - i, -5.0f));
        modelPartData.addChild("left_front_leg", modelPartBuilder, ModelTransform.pivot(3.0f, 24 - i, -5.0f));
        return modelData;
    }

    @Override
    protected Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of(this.head);
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of(this.torso, this.field_27476, this.field_27477, this.field_27478, this.field_27479);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.head.pitch = headPitch * ((float)Math.PI / 180);
        this.head.yaw = headYaw * ((float)Math.PI / 180);
        this.torso.pitch = 1.5707964f;
        this.field_27476.pitch = MathHelper.cos(limbAngle * 0.6662f) * 1.4f * limbDistance;
        this.field_27477.pitch = MathHelper.cos(limbAngle * 0.6662f + (float)Math.PI) * 1.4f * limbDistance;
        this.field_27478.pitch = MathHelper.cos(limbAngle * 0.6662f + (float)Math.PI) * 1.4f * limbDistance;
        this.field_27479.pitch = MathHelper.cos(limbAngle * 0.6662f) * 1.4f * limbDistance;
    }
}

