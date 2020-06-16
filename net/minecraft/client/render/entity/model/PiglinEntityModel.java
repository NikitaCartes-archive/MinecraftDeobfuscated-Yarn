/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.CrossbowPosing;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class PiglinEntityModel<T extends MobEntity>
extends PlayerEntityModel<T> {
    /**
     * Maybe the ears are swapped
     */
    public final ModelPart rightEar;
    public final ModelPart leftEar;
    private final ModelPart field_25634;
    private final ModelPart field_25635;
    private final ModelPart field_25632;
    private final ModelPart field_25633;

    public PiglinEntityModel(float scale, int textureWidth, int textureHeight) {
        super(scale, false);
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.torso = new ModelPart(this, 16, 16);
        this.torso.addCuboid(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, scale);
        this.head = new ModelPart(this);
        this.head.setTextureOffset(0, 0).addCuboid(-5.0f, -8.0f, -4.0f, 10.0f, 8.0f, 8.0f, scale);
        this.head.setTextureOffset(31, 1).addCuboid(-2.0f, -4.0f, -5.0f, 4.0f, 4.0f, 1.0f, scale);
        this.head.setTextureOffset(2, 4).addCuboid(2.0f, -2.0f, -5.0f, 1.0f, 2.0f, 1.0f, scale);
        this.head.setTextureOffset(2, 0).addCuboid(-3.0f, -2.0f, -5.0f, 1.0f, 2.0f, 1.0f, scale);
        this.rightEar = new ModelPart(this);
        this.rightEar.setPivot(4.5f, -6.0f, 0.0f);
        this.rightEar.setTextureOffset(51, 6).addCuboid(0.0f, 0.0f, -2.0f, 1.0f, 5.0f, 4.0f, scale);
        this.head.addChild(this.rightEar);
        this.leftEar = new ModelPart(this);
        this.leftEar.setPivot(-4.5f, -6.0f, 0.0f);
        this.leftEar.setTextureOffset(39, 6).addCuboid(-1.0f, 0.0f, -2.0f, 1.0f, 5.0f, 4.0f, scale);
        this.head.addChild(this.leftEar);
        this.helmet = new ModelPart(this);
        this.field_25634 = this.torso.method_29991();
        this.field_25635 = this.head.method_29991();
        this.field_25632 = this.leftArm.method_29991();
        this.field_25633 = this.leftArm.method_29991();
    }

    @Override
    public void setAngles(T mobEntity, float f, float g, float h, float i, float j) {
        this.torso.copyPositionAndRotation(this.field_25634);
        this.head.copyPositionAndRotation(this.field_25635);
        this.leftArm.copyPositionAndRotation(this.field_25632);
        this.rightArm.copyPositionAndRotation(this.field_25633);
        super.setAngles(mobEntity, f, g, h, i, j);
        float k = 0.5235988f;
        float l = h * 0.1f + f * 0.5f;
        float m = 0.08f + g * 0.4f;
        this.rightEar.roll = -0.5235988f - MathHelper.cos(l * 1.2f) * m;
        this.leftEar.roll = 0.5235988f + MathHelper.cos(l) * m;
        if (((Entity)mobEntity).getType() == EntityType.PIGLIN) {
            PiglinEntity piglinEntity = (PiglinEntity)mobEntity;
            PiglinEntity.Activity activity = piglinEntity.getActivity();
            if (activity == PiglinEntity.Activity.DANCING) {
                float n = h / 60.0f;
                this.leftEar.roll = 0.5235988f + (float)Math.PI / 180 * MathHelper.sin(n * 30.0f) * 10.0f;
                this.rightEar.roll = -0.5235988f - (float)Math.PI / 180 * MathHelper.cos(n * 30.0f) * 10.0f;
                this.head.pivotX = MathHelper.sin(n * 10.0f);
                this.head.pivotY = MathHelper.sin(n * 40.0f) + 0.4f;
                this.rightArm.roll = (float)Math.PI / 180 * (70.0f + MathHelper.cos(n * 40.0f) * 10.0f);
                this.leftArm.roll = this.rightArm.roll * -1.0f;
                this.rightArm.pivotY = MathHelper.sin(n * 40.0f) * 0.5f + 1.5f;
                this.leftArm.pivotY = MathHelper.sin(n * 40.0f) * 0.5f + 1.5f;
                this.torso.pivotY = MathHelper.sin(n * 40.0f) * 0.35f;
            } else if (activity == PiglinEntity.Activity.ATTACKING_WITH_MELEE_WEAPON && this.handSwingProgress == 0.0f) {
                this.method_29354(mobEntity);
            } else if (activity == PiglinEntity.Activity.CROSSBOW_HOLD) {
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
        } else if (((Entity)mobEntity).getType() == EntityType.ZOMBIFIED_PIGLIN) {
            CrossbowPosing.method_29352(this.leftArm, this.rightArm, ((MobEntity)mobEntity).isAttacking(), this.handSwingProgress, h);
        }
        this.leftPantLeg.copyPositionAndRotation(this.leftLeg);
        this.rightPantLeg.copyPositionAndRotation(this.rightLeg);
        this.leftSleeve.copyPositionAndRotation(this.leftArm);
        this.rightSleeve.copyPositionAndRotation(this.rightArm);
        this.jacket.copyPositionAndRotation(this.torso);
    }

    @Override
    protected void method_29353(T mobEntity, float f) {
        if (this.handSwingProgress > 0.0f && mobEntity instanceof PiglinEntity && ((PiglinEntity)mobEntity).getActivity() == PiglinEntity.Activity.ATTACKING_WITH_MELEE_WEAPON) {
            CrossbowPosing.method_29351(this.rightArm, this.leftArm, mobEntity, this.handSwingProgress, f);
            return;
        }
        super.method_29353(mobEntity, f);
    }

    private void method_29354(T mobEntity) {
        if (((MobEntity)mobEntity).isLeftHanded()) {
            this.leftArm.pitch = -1.8f;
        } else {
            this.rightArm.pitch = -1.8f;
        }
    }
}

