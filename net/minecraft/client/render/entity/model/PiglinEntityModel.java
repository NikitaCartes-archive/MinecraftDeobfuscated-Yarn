/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.CrossbowPosing;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class PiglinEntityModel<T extends MobEntity>
extends BipedEntityModel<T> {
    /**
     * Maybe the ears are swapped
     */
    public final ModelPart rightEar;
    public final ModelPart leftEar;

    public PiglinEntityModel(float scale, int textureWidth, int textureHeight) {
        super(scale, 0.0f, textureWidth, textureHeight);
        this.torso = new ModelPart(this, 16, 16);
        this.torso.addCuboid(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, scale);
        this.head = new ModelPart(this);
        this.head.setTextureOffset(0, 0).addCuboid(-5.0f, -8.0f, -4.0f, 10.0f, 8.0f, 8.0f, scale);
        this.head.setTextureOffset(31, 1).addCuboid(-2.0f, -4.0f, -5.0f, 4.0f, 4.0f, 1.0f, scale);
        this.head.setTextureOffset(2, 4).addCuboid(2.0f, -2.0f, -5.0f, 1.0f, 2.0f, 1.0f, scale);
        this.head.setTextureOffset(2, 0).addCuboid(-3.0f, -2.0f, -5.0f, 1.0f, 2.0f, 1.0f, scale);
        this.rightEar = new ModelPart(this);
        this.rightEar.setPivot(4.5f, -6.0f, 0.0f);
        this.rightEar.setTextureOffset(57, 38).addCuboid(0.0f, 0.0f, -2.0f, 1.0f, 5.0f, 4.0f, scale);
        this.head.addChild(this.rightEar);
        this.leftEar = new ModelPart(this);
        this.leftEar.setPivot(-4.5f, -6.0f, 0.0f);
        this.head.addChild(this.leftEar);
        this.leftEar.setTextureOffset(57, 22).addCuboid(-1.0f, 0.0f, -2.0f, 1.0f, 5.0f, 4.0f, scale);
        this.helmet = new ModelPart(this);
        this.rightArm = new ModelPart(this);
        this.rightArm.setPivot(-5.0f, 2.0f, 0.0f);
        this.rightArm.setTextureOffset(40, 16).addCuboid(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, scale);
        this.leftArm = new ModelPart(this);
        this.leftArm.setPivot(5.0f, 2.0f, 0.0f);
        this.leftArm.setTextureOffset(40, 16).addCuboid(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, scale);
        this.rightLeg = new ModelPart(this);
        this.rightLeg.setPivot(-1.9f, 12.0f, 0.0f);
        this.rightLeg.setTextureOffset(0, 16).addCuboid(-2.1f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, scale);
        this.leftLeg = new ModelPart(this);
        this.leftLeg.setPivot(1.9f, 12.0f, 0.0f);
        this.leftLeg.setTextureOffset(0, 16).addCuboid(-1.9f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, scale);
    }

    @Override
    public void setAngles(T mobEntity, float f, float g, float h, float i, float j) {
        super.setAngles(mobEntity, f, g, h, i, j);
        float k = 0.5235988f;
        float l = h * 0.1f + f * 0.5f;
        float m = 0.08f + g * 0.4f;
        this.rightEar.roll = -0.5235988f - MathHelper.cos(l * 1.2f) * m;
        this.leftEar.roll = 0.5235988f + MathHelper.cos(l) * m;
        if (mobEntity instanceof PiglinEntity) {
            PiglinEntity piglinEntity = (PiglinEntity)mobEntity;
            PiglinEntity.Activity activity = piglinEntity.getActivity();
            if (activity == PiglinEntity.Activity.CROSSBOW_HOLD) {
                CrossbowPosing.hold(this.rightArm, this.leftArm, this.head, !((MobEntity)mobEntity).isLeftHanded());
            } else if (activity == PiglinEntity.Activity.CROSSBOW_CHARGE) {
                CrossbowPosing.charge(this.rightArm, this.leftArm, mobEntity, !((MobEntity)mobEntity).isLeftHanded());
            } else if (activity == PiglinEntity.Activity.ADMIRING_ITEM) {
                this.head.pitch = 0.5f;
                this.head.yaw = 0.0f;
                if (((MobEntity)mobEntity).isLeftHanded()) {
                    this.rightArm.yaw = -0.5f;
                    this.rightArm.pitch = -0.9f;
                } else {
                    this.leftArm.yaw = 0.5f;
                    this.leftArm.pitch = -0.9f;
                }
            }
        }
    }
}

