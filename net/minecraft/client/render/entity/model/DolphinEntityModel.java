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
public class DolphinEntityModel<T extends Entity>
extends SinglePartEntityModel<T> {
    private final ModelPart field_27411;
    private final ModelPart body;
    private final ModelPart tail;
    private final ModelPart flukes;

    public DolphinEntityModel(ModelPart modelPart) {
        this.field_27411 = modelPart;
        this.body = modelPart.getChild("body");
        this.tail = this.body.getChild("tail");
        this.flukes = this.tail.getChild("tail_fin");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        float f = 18.0f;
        float g = -8.0f;
        ModelPartData modelPartData2 = modelPartData.addChild("body", ModelPartBuilder.create().uv(22, 0).cuboid(-4.0f, -7.0f, 0.0f, 8.0f, 7.0f, 13.0f), ModelTransform.pivot(0.0f, 22.0f, -5.0f));
        modelPartData2.addChild("back_fin", ModelPartBuilder.create().uv(51, 0).cuboid(-0.5f, 0.0f, 8.0f, 1.0f, 4.0f, 5.0f), ModelTransform.rotation(1.0471976f, 0.0f, 0.0f));
        modelPartData2.addChild("left_fin", ModelPartBuilder.create().uv(48, 20).mirrored().cuboid(-0.5f, -4.0f, 0.0f, 1.0f, 4.0f, 7.0f), ModelTransform.of(2.0f, -2.0f, 4.0f, 1.0471976f, 0.0f, 2.0943952f));
        modelPartData2.addChild("right_fin", ModelPartBuilder.create().uv(48, 20).cuboid(-0.5f, -4.0f, 0.0f, 1.0f, 4.0f, 7.0f), ModelTransform.of(-2.0f, -2.0f, 4.0f, 1.0471976f, 0.0f, -2.0943952f));
        ModelPartData modelPartData3 = modelPartData2.addChild("tail", ModelPartBuilder.create().uv(0, 19).cuboid(-2.0f, -2.5f, 0.0f, 4.0f, 5.0f, 11.0f), ModelTransform.of(0.0f, -2.5f, 11.0f, -0.10471976f, 0.0f, 0.0f));
        modelPartData3.addChild("tail_fin", ModelPartBuilder.create().uv(19, 20).cuboid(-5.0f, -0.5f, 0.0f, 10.0f, 1.0f, 6.0f), ModelTransform.pivot(0.0f, 0.0f, 9.0f));
        ModelPartData modelPartData4 = modelPartData2.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -3.0f, -3.0f, 8.0f, 7.0f, 6.0f), ModelTransform.pivot(0.0f, -4.0f, -3.0f));
        modelPartData4.addChild("nose", ModelPartBuilder.create().uv(0, 13).cuboid(-1.0f, 2.0f, -7.0f, 2.0f, 2.0f, 4.0f), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public ModelPart getPart() {
        return this.field_27411;
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.body.pitch = headPitch * ((float)Math.PI / 180);
        this.body.yaw = headYaw * ((float)Math.PI / 180);
        if (Entity.squaredHorizontalLength(((Entity)entity).getVelocity()) > 1.0E-7) {
            this.body.pitch += -0.05f - 0.05f * MathHelper.cos(animationProgress * 0.3f);
            this.tail.pitch = -0.1f * MathHelper.cos(animationProgress * 0.3f);
            this.flukes.pitch = -0.2f * MathHelper.cos(animationProgress * 0.3f);
        }
    }
}

