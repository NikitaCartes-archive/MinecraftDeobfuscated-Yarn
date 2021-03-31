/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.ArmorStandArmorEntityModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ArmorStandEntityModel
extends ArmorStandArmorEntityModel {
    private static final String field_32445 = "right_body_stick";
    private static final String field_32446 = "left_body_stick";
    private static final String field_32447 = "shoulder_stick";
    private static final String field_32448 = "base_plate";
    private final ModelPart rightBodyStick;
    private final ModelPart leftBodyStick;
    private final ModelPart shoulderStick;
    private final ModelPart basePlate;

    public ArmorStandEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.rightBodyStick = modelPart.getChild(field_32445);
        this.leftBodyStick = modelPart.getChild(field_32446);
        this.shoulderStick = modelPart.getChild(field_32447);
        this.basePlate = modelPart.getChild(field_32448);
        this.hat.visible = false;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = BipedEntityModel.getModelData(Dilation.NONE, 0.0f);
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 0).cuboid(-1.0f, -7.0f, -1.0f, 2.0f, 7.0f, 2.0f), ModelTransform.pivot(0.0f, 1.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(0, 26).cuboid(-6.0f, 0.0f, -1.5f, 12.0f, 3.0f, 3.0f), ModelTransform.NONE);
        modelPartData.addChild(EntityModelPartNames.RIGHT_ARM, ModelPartBuilder.create().uv(24, 0).cuboid(-2.0f, -2.0f, -1.0f, 2.0f, 12.0f, 2.0f), ModelTransform.pivot(-5.0f, 2.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_ARM, ModelPartBuilder.create().uv(32, 16).mirrored().cuboid(0.0f, -2.0f, -1.0f, 2.0f, 12.0f, 2.0f), ModelTransform.pivot(5.0f, 2.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_LEG, ModelPartBuilder.create().uv(8, 0).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 11.0f, 2.0f), ModelTransform.pivot(-1.9f, 12.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_LEG, ModelPartBuilder.create().uv(40, 16).mirrored().cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 11.0f, 2.0f), ModelTransform.pivot(1.9f, 12.0f, 0.0f));
        modelPartData.addChild(field_32445, ModelPartBuilder.create().uv(16, 0).cuboid(-3.0f, 3.0f, -1.0f, 2.0f, 7.0f, 2.0f), ModelTransform.NONE);
        modelPartData.addChild(field_32446, ModelPartBuilder.create().uv(48, 16).cuboid(1.0f, 3.0f, -1.0f, 2.0f, 7.0f, 2.0f), ModelTransform.NONE);
        modelPartData.addChild(field_32447, ModelPartBuilder.create().uv(0, 48).cuboid(-4.0f, 10.0f, -1.0f, 8.0f, 2.0f, 2.0f), ModelTransform.NONE);
        modelPartData.addChild(field_32448, ModelPartBuilder.create().uv(0, 32).cuboid(-6.0f, 11.0f, -6.0f, 12.0f, 1.0f, 12.0f), ModelTransform.pivot(0.0f, 12.0f, 0.0f));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void animateModel(ArmorStandEntity armorStandEntity, float f, float g, float h) {
        this.basePlate.pitch = 0.0f;
        this.basePlate.yaw = (float)Math.PI / 180 * -MathHelper.lerpAngleDegrees(h, armorStandEntity.prevYaw, armorStandEntity.yaw);
        this.basePlate.roll = 0.0f;
    }

    @Override
    public void setAngles(ArmorStandEntity armorStandEntity, float f, float g, float h, float i, float j) {
        super.setAngles(armorStandEntity, f, g, h, i, j);
        this.leftArm.visible = armorStandEntity.shouldShowArms();
        this.rightArm.visible = armorStandEntity.shouldShowArms();
        this.basePlate.visible = !armorStandEntity.shouldHideBasePlate();
        this.rightBodyStick.pitch = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getPitch();
        this.rightBodyStick.yaw = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getYaw();
        this.rightBodyStick.roll = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getRoll();
        this.leftBodyStick.pitch = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getPitch();
        this.leftBodyStick.yaw = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getYaw();
        this.leftBodyStick.roll = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getRoll();
        this.shoulderStick.pitch = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getPitch();
        this.shoulderStick.yaw = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getYaw();
        this.shoulderStick.roll = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getRoll();
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return Iterables.concat(super.getBodyParts(), ImmutableList.of(this.rightBodyStick, this.leftBodyStick, this.shoulderStick, this.basePlate));
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

