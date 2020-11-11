/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.ArmorStandArmorEntityModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.Dilation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ArmorStandEntityModel
extends ArmorStandArmorEntityModel {
    private final ModelPart field_27391;
    private final ModelPart field_27392;
    private final ModelPart hip;
    private final ModelPart plate;

    public ArmorStandEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.field_27391 = modelPart.getChild("right_body_stick");
        this.field_27392 = modelPart.getChild("left_body_stick");
        this.hip = modelPart.getChild("shoulder_stick");
        this.plate = modelPart.getChild("base_plate");
        this.helmet.visible = false;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = BipedEntityModel.getModelData(Dilation.NONE, 0.0f);
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0f, -7.0f, -1.0f, 2.0f, 7.0f, 2.0f), ModelTransform.pivot(0.0f, 1.0f, 0.0f));
        modelPartData.addChild("body", ModelPartBuilder.create().uv(0, 26).cuboid(-6.0f, 0.0f, -1.5f, 12.0f, 3.0f, 3.0f), ModelTransform.NONE);
        modelPartData.addChild("right_arm", ModelPartBuilder.create().uv(24, 0).cuboid(-2.0f, -2.0f, -1.0f, 2.0f, 12.0f, 2.0f), ModelTransform.pivot(-5.0f, 2.0f, 0.0f));
        modelPartData.addChild("left_arm", ModelPartBuilder.create().uv(32, 16).mirrored().cuboid(0.0f, -2.0f, -1.0f, 2.0f, 12.0f, 2.0f), ModelTransform.pivot(5.0f, 2.0f, 0.0f));
        modelPartData.addChild("right_leg", ModelPartBuilder.create().uv(8, 0).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 11.0f, 2.0f), ModelTransform.pivot(-1.9f, 12.0f, 0.0f));
        modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(40, 16).mirrored().cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 11.0f, 2.0f), ModelTransform.pivot(1.9f, 12.0f, 0.0f));
        modelPartData.addChild("right_body_stick", ModelPartBuilder.create().uv(16, 0).cuboid(-3.0f, 3.0f, -1.0f, 2.0f, 7.0f, 2.0f), ModelTransform.NONE);
        modelPartData.addChild("left_body_stick", ModelPartBuilder.create().uv(48, 16).cuboid(1.0f, 3.0f, -1.0f, 2.0f, 7.0f, 2.0f), ModelTransform.NONE);
        modelPartData.addChild("shoulder_stick", ModelPartBuilder.create().uv(0, 48).cuboid(-4.0f, 10.0f, -1.0f, 8.0f, 2.0f, 2.0f), ModelTransform.NONE);
        modelPartData.addChild("base_plate", ModelPartBuilder.create().uv(0, 32).cuboid(-6.0f, 11.0f, -6.0f, 12.0f, 1.0f, 12.0f), ModelTransform.pivot(0.0f, 12.0f, 0.0f));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void animateModel(ArmorStandEntity armorStandEntity, float f, float g, float h) {
        this.plate.pitch = 0.0f;
        this.plate.yaw = (float)Math.PI / 180 * -MathHelper.lerpAngleDegrees(h, armorStandEntity.prevYaw, armorStandEntity.yaw);
        this.plate.roll = 0.0f;
    }

    @Override
    public void setAngles(ArmorStandEntity armorStandEntity, float f, float g, float h, float i, float j) {
        super.setAngles(armorStandEntity, f, g, h, i, j);
        this.leftArm.visible = armorStandEntity.shouldShowArms();
        this.rightArm.visible = armorStandEntity.shouldShowArms();
        this.plate.visible = !armorStandEntity.shouldHideBasePlate();
        this.field_27391.pitch = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getPitch();
        this.field_27391.yaw = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getYaw();
        this.field_27391.roll = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getRoll();
        this.field_27392.pitch = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getPitch();
        this.field_27392.yaw = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getYaw();
        this.field_27392.roll = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getRoll();
        this.hip.pitch = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getPitch();
        this.hip.yaw = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getYaw();
        this.hip.roll = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getRoll();
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return Iterables.concat(super.getBodyParts(), ImmutableList.of(this.field_27391, this.field_27392, this.hip, this.plate));
    }

    @Override
    public void setArmAngle(Arm arm, MatrixStack matrices) {
        ModelPart modelPart = this.getArm(arm);
        boolean bl = modelPart.visible;
        modelPart.visible = true;
        super.setArmAngle(arm, matrices);
        modelPart.visible = bl;
    }
}

