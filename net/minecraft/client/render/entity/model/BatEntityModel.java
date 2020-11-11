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
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BatEntityModel
extends SinglePartEntityModel<BatEntity> {
    private final ModelPart field_27393;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart rightWingTip;
    private final ModelPart leftWingTip;

    public BatEntityModel(ModelPart modelPart) {
        this.field_27393 = modelPart;
        this.head = modelPart.getChild("head");
        this.body = modelPart.getChild("body");
        this.rightWing = this.body.getChild("right_wing");
        this.rightWingTip = this.rightWing.getChild("right_wing_tip");
        this.leftWing = this.body.getChild("left_wing");
        this.leftWingTip = this.leftWing.getChild("left_wing_tip");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-3.0f, -3.0f, -3.0f, 6.0f, 6.0f, 6.0f), ModelTransform.NONE);
        modelPartData2.addChild("right_ear", ModelPartBuilder.create().uv(24, 0).cuboid(-4.0f, -6.0f, -2.0f, 3.0f, 4.0f, 1.0f), ModelTransform.NONE);
        modelPartData2.addChild("left_ear", ModelPartBuilder.create().uv(24, 0).mirrored().cuboid(1.0f, -6.0f, -2.0f, 3.0f, 4.0f, 1.0f), ModelTransform.NONE);
        ModelPartData modelPartData3 = modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 16).cuboid(-3.0f, 4.0f, -3.0f, 6.0f, 12.0f, 6.0f).uv(0, 34).cuboid(-5.0f, 16.0f, 0.0f, 10.0f, 6.0f, 1.0f), ModelTransform.NONE);
        ModelPartData modelPartData4 = modelPartData3.addChild("right_wing", ModelPartBuilder.create().uv(42, 0).cuboid(-12.0f, 1.0f, 1.5f, 10.0f, 16.0f, 1.0f), ModelTransform.NONE);
        modelPartData4.addChild("right_wing_tip", ModelPartBuilder.create().uv(24, 16).cuboid(-8.0f, 1.0f, 0.0f, 8.0f, 12.0f, 1.0f), ModelTransform.pivot(-12.0f, 1.0f, 1.5f));
        ModelPartData modelPartData5 = modelPartData3.addChild("left_wing", ModelPartBuilder.create().uv(42, 0).mirrored().cuboid(2.0f, 1.0f, 1.5f, 10.0f, 16.0f, 1.0f), ModelTransform.NONE);
        modelPartData5.addChild("left_wing_tip", ModelPartBuilder.create().uv(24, 16).mirrored().cuboid(0.0f, 1.0f, 0.0f, 8.0f, 12.0f, 1.0f), ModelTransform.pivot(12.0f, 1.0f, 1.5f));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public ModelPart getPart() {
        return this.field_27393;
    }

    @Override
    public void setAngles(BatEntity batEntity, float f, float g, float h, float i, float j) {
        if (batEntity.isRoosting()) {
            this.head.pitch = j * ((float)Math.PI / 180);
            this.head.yaw = (float)Math.PI - i * ((float)Math.PI / 180);
            this.head.roll = (float)Math.PI;
            this.head.setPivot(0.0f, -2.0f, 0.0f);
            this.rightWing.setPivot(-3.0f, 0.0f, 3.0f);
            this.leftWing.setPivot(3.0f, 0.0f, 3.0f);
            this.body.pitch = (float)Math.PI;
            this.rightWing.pitch = -0.15707964f;
            this.rightWing.yaw = -1.2566371f;
            this.rightWingTip.yaw = -1.7278761f;
            this.leftWing.pitch = this.rightWing.pitch;
            this.leftWing.yaw = -this.rightWing.yaw;
            this.leftWingTip.yaw = -this.rightWingTip.yaw;
        } else {
            this.head.pitch = j * ((float)Math.PI / 180);
            this.head.yaw = i * ((float)Math.PI / 180);
            this.head.roll = 0.0f;
            this.head.setPivot(0.0f, 0.0f, 0.0f);
            this.rightWing.setPivot(0.0f, 0.0f, 0.0f);
            this.leftWing.setPivot(0.0f, 0.0f, 0.0f);
            this.body.pitch = 0.7853982f + MathHelper.cos(h * 0.1f) * 0.15f;
            this.body.yaw = 0.0f;
            this.rightWing.yaw = MathHelper.cos(h * 1.3f) * (float)Math.PI * 0.25f;
            this.leftWing.yaw = -this.rightWing.yaw;
            this.rightWingTip.yaw = this.rightWing.yaw * 0.5f;
            this.leftWingTip.yaw = -this.rightWing.yaw * 0.5f;
        }
    }
}

