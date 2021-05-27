/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.entity.LivingEntity;

/**
 * Represents the model of an enderman-like living entity.
 * 
 * <p>The model parts are the same as a {@link BipedEntityModel} but with different proportions.
 * 
 * <div class="fabric">
 * <table border=1>
 * <caption>Model parts of this model</caption>
 * <tr>
 *   <th>Part Name</th><th>Parent</th><th>Corresponding Field</th>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#HAT}</td><td>Root part</td><td>{@link #hat}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#HEAD}</td><td>Root part</td><td>{@link #head}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#BODY}</td><td>Root part</td><td>{@link #body}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#RIGHT_ARM}</td><td>Root part</td><td>{@link #rightArm}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#LEFT_ARM}</td><td>Root part</td><td>{@link #leftArm}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#RIGHT_LEG}</td><td>Root part</td><td>{@link #rightLeg}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#LEFT_LEG}</td><td>Root part</td><td>{@link #leftLeg}</td>
 * </tr>
 * </table>
 * </div>
 */
@Environment(value=EnvType.CLIENT)
public class EndermanEntityModel<T extends LivingEntity>
extends BipedEntityModel<T> {
    public boolean carryingBlock;
    public boolean angry;

    public EndermanEntityModel(ModelPart modelPart) {
        super(modelPart);
    }

    public static TexturedModelData getTexturedModelData() {
        float f = -14.0f;
        ModelData modelData = BipedEntityModel.getModelData(Dilation.NONE, -14.0f);
        ModelPartData modelPartData = modelData.getRoot();
        ModelTransform modelTransform = ModelTransform.pivot(0.0f, -13.0f, 0.0f);
        modelPartData.addChild(EntityModelPartNames.HAT, ModelPartBuilder.create().uv(0, 16).cuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, new Dilation(-0.5f)), modelTransform);
        modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f), modelTransform);
        modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(32, 16).cuboid(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f), ModelTransform.pivot(0.0f, -14.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_ARM, ModelPartBuilder.create().uv(56, 0).cuboid(-1.0f, -2.0f, -1.0f, 2.0f, 30.0f, 2.0f), ModelTransform.pivot(-5.0f, -12.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_ARM, ModelPartBuilder.create().uv(56, 0).mirrored().cuboid(-1.0f, -2.0f, -1.0f, 2.0f, 30.0f, 2.0f), ModelTransform.pivot(5.0f, -12.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_LEG, ModelPartBuilder.create().uv(56, 0).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 30.0f, 2.0f), ModelTransform.pivot(-2.0f, -5.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_LEG, ModelPartBuilder.create().uv(56, 0).mirrored().cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 30.0f, 2.0f), ModelTransform.pivot(2.0f, -5.0f, 0.0f));
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    public void setAngles(T livingEntity, float f, float g, float h, float i, float j) {
        super.setAngles(livingEntity, f, g, h, i, j);
        this.head.visible = true;
        int k = -14;
        this.body.pitch = 0.0f;
        this.body.pivotY = -14.0f;
        this.body.pivotZ = -0.0f;
        this.rightLeg.pitch -= 0.0f;
        this.leftLeg.pitch -= 0.0f;
        this.rightArm.pitch = (float)((double)this.rightArm.pitch * 0.5);
        this.leftArm.pitch = (float)((double)this.leftArm.pitch * 0.5);
        this.rightLeg.pitch = (float)((double)this.rightLeg.pitch * 0.5);
        this.leftLeg.pitch = (float)((double)this.leftLeg.pitch * 0.5);
        float l = 0.4f;
        if (this.rightArm.pitch > 0.4f) {
            this.rightArm.pitch = 0.4f;
        }
        if (this.leftArm.pitch > 0.4f) {
            this.leftArm.pitch = 0.4f;
        }
        if (this.rightArm.pitch < -0.4f) {
            this.rightArm.pitch = -0.4f;
        }
        if (this.leftArm.pitch < -0.4f) {
            this.leftArm.pitch = -0.4f;
        }
        if (this.rightLeg.pitch > 0.4f) {
            this.rightLeg.pitch = 0.4f;
        }
        if (this.leftLeg.pitch > 0.4f) {
            this.leftLeg.pitch = 0.4f;
        }
        if (this.rightLeg.pitch < -0.4f) {
            this.rightLeg.pitch = -0.4f;
        }
        if (this.leftLeg.pitch < -0.4f) {
            this.leftLeg.pitch = -0.4f;
        }
        if (this.carryingBlock) {
            this.rightArm.pitch = -0.5f;
            this.leftArm.pitch = -0.5f;
            this.rightArm.roll = 0.05f;
            this.leftArm.roll = -0.05f;
        }
        this.rightLeg.pivotZ = 0.0f;
        this.leftLeg.pivotZ = 0.0f;
        this.rightLeg.pivotY = -5.0f;
        this.leftLeg.pivotY = -5.0f;
        this.head.pivotZ = -0.0f;
        this.head.pivotY = -13.0f;
        this.hat.pivotX = this.head.pivotX;
        this.hat.pivotY = this.head.pivotY;
        this.hat.pivotZ = this.head.pivotZ;
        this.hat.pitch = this.head.pitch;
        this.hat.yaw = this.head.yaw;
        this.hat.roll = this.head.roll;
        if (this.angry) {
            float m = 1.0f;
            this.head.pivotY -= 5.0f;
        }
        int n = -14;
        this.rightArm.setPivot(-5.0f, -12.0f, 0.0f);
        this.leftArm.setPivot(5.0f, -12.0f, 0.0f);
    }
}

