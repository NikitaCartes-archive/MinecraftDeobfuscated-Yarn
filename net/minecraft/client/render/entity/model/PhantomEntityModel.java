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
public class PhantomEntityModel<T extends Entity>
extends SinglePartEntityModel<T> {
    private final ModelPart root;
    private final ModelPart leftWingBase;
    private final ModelPart leftWingTip;
    private final ModelPart rightWingBase;
    private final ModelPart rightWingTip;
    private final ModelPart tailBase;
    private final ModelPart tailTip;

    public PhantomEntityModel(ModelPart root) {
        this.root = root;
        ModelPart modelPart = root.getChild("body");
        this.tailBase = modelPart.getChild("tail_base");
        this.tailTip = this.tailBase.getChild("tail_tip");
        this.leftWingBase = modelPart.getChild("left_wing_base");
        this.leftWingTip = this.leftWingBase.getChild("left_wing_tip");
        this.rightWingBase = modelPart.getChild("right_wing_base");
        this.rightWingTip = this.rightWingBase.getChild("right_wing_tip");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 8).cuboid(-3.0f, -2.0f, -8.0f, 5.0f, 3.0f, 9.0f), ModelTransform.rotation(-0.1f, 0.0f, 0.0f));
        ModelPartData modelPartData3 = modelPartData2.addChild("tail_base", ModelPartBuilder.create().uv(3, 20).cuboid(-2.0f, 0.0f, 0.0f, 3.0f, 2.0f, 6.0f), ModelTransform.pivot(0.0f, -2.0f, 1.0f));
        modelPartData3.addChild("tail_tip", ModelPartBuilder.create().uv(4, 29).cuboid(-1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 6.0f), ModelTransform.pivot(0.0f, 0.5f, 6.0f));
        ModelPartData modelPartData4 = modelPartData2.addChild("left_wing_base", ModelPartBuilder.create().uv(23, 12).cuboid(0.0f, 0.0f, 0.0f, 6.0f, 2.0f, 9.0f), ModelTransform.of(2.0f, -2.0f, -8.0f, 0.0f, 0.0f, 0.1f));
        modelPartData4.addChild("left_wing_tip", ModelPartBuilder.create().uv(16, 24).cuboid(0.0f, 0.0f, 0.0f, 13.0f, 1.0f, 9.0f), ModelTransform.of(6.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.1f));
        ModelPartData modelPartData5 = modelPartData2.addChild("right_wing_base", ModelPartBuilder.create().uv(23, 12).mirrored().cuboid(-6.0f, 0.0f, 0.0f, 6.0f, 2.0f, 9.0f), ModelTransform.of(-3.0f, -2.0f, -8.0f, 0.0f, 0.0f, -0.1f));
        modelPartData5.addChild("right_wing_tip", ModelPartBuilder.create().uv(16, 24).mirrored().cuboid(-13.0f, 0.0f, 0.0f, 13.0f, 1.0f, 9.0f), ModelTransform.of(-6.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.1f));
        modelPartData2.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -2.0f, -5.0f, 7.0f, 3.0f, 5.0f), ModelTransform.of(0.0f, 1.0f, -7.0f, 0.2f, 0.0f, 0.0f));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        float f = ((float)(((Entity)entity).getId() * 3) + animationProgress) * 0.13f;
        float g = 16.0f;
        this.leftWingBase.roll = MathHelper.cos(f) * 16.0f * ((float)Math.PI / 180);
        this.leftWingTip.roll = MathHelper.cos(f) * 16.0f * ((float)Math.PI / 180);
        this.rightWingBase.roll = -this.leftWingBase.roll;
        this.rightWingTip.roll = -this.leftWingTip.roll;
        this.tailBase.pitch = -(5.0f + MathHelper.cos(f * 2.0f) * 5.0f) * ((float)Math.PI / 180);
        this.tailTip.pitch = -(5.0f + MathHelper.cos(f * 2.0f) * 5.0f) * ((float)Math.PI / 180);
    }
}

