/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5603;
import net.minecraft.class_5605;
import net.minecraft.class_5606;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.CrossbowPosing;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PiglinActivity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class PiglinEntityModel<T extends MobEntity>
extends PlayerEntityModel<T> {
    public final ModelPart field_27464;
    private final ModelPart field_27465;
    private final class_5603 field_25634;
    private final class_5603 field_25635;
    private final class_5603 field_25632;
    private final class_5603 field_25633;

    public PiglinEntityModel(ModelPart modelPart) {
        super(modelPart, false);
        this.field_27464 = this.head.method_32086("right_ear");
        this.field_27465 = this.head.method_32086("left_ear");
        this.field_25634 = this.torso.method_32084();
        this.field_25635 = this.head.method_32084();
        this.field_25632 = this.field_27433.method_32084();
        this.field_25633 = this.rightArm.method_32084();
    }

    public static class_5609 method_32026(class_5605 arg) {
        class_5609 lv = PlayerEntityModel.method_32028(arg, false);
        class_5610 lv2 = lv.method_32111();
        lv2.method_32117("body", class_5606.method_32108().method_32101(16, 16).method_32098(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, arg), class_5603.field_27701);
        class_5610 lv3 = lv2.method_32117("head", class_5606.method_32108().method_32101(0, 0).method_32098(-5.0f, -8.0f, -4.0f, 10.0f, 8.0f, 8.0f, arg).method_32101(31, 1).method_32098(-2.0f, -4.0f, -5.0f, 4.0f, 4.0f, 1.0f, arg).method_32101(2, 4).method_32098(2.0f, -2.0f, -5.0f, 1.0f, 2.0f, 1.0f, arg).method_32101(2, 0).method_32098(-3.0f, -2.0f, -5.0f, 1.0f, 2.0f, 1.0f, arg), class_5603.field_27701);
        lv3.method_32117("left_ear", class_5606.method_32108().method_32101(51, 6).method_32098(0.0f, 0.0f, -2.0f, 1.0f, 5.0f, 4.0f, arg), class_5603.method_32091(4.5f, -6.0f, 0.0f, 0.0f, 0.0f, -0.5235988f));
        lv3.method_32117("right_ear", class_5606.method_32108().method_32101(39, 6).method_32098(-1.0f, 0.0f, -2.0f, 1.0f, 5.0f, 4.0f, arg), class_5603.method_32091(-4.5f, -6.0f, 0.0f, 0.0f, 0.0f, 0.5235988f));
        lv2.method_32117("hat", class_5606.method_32108(), class_5603.field_27701);
        return lv;
    }

    @Override
    public void setAngles(T mobEntity, float f, float g, float h, float i, float j) {
        this.torso.method_32085(this.field_25634);
        this.head.method_32085(this.field_25635);
        this.field_27433.method_32085(this.field_25632);
        this.rightArm.method_32085(this.field_25633);
        super.setAngles(mobEntity, f, g, h, i, j);
        float k = 0.5235988f;
        float l = h * 0.1f + f * 0.5f;
        float m = 0.08f + g * 0.4f;
        this.field_27465.roll = -0.5235988f - MathHelper.cos(l * 1.2f) * m;
        this.field_27464.roll = 0.5235988f + MathHelper.cos(l) * m;
        if (mobEntity instanceof AbstractPiglinEntity) {
            AbstractPiglinEntity abstractPiglinEntity = (AbstractPiglinEntity)mobEntity;
            PiglinActivity piglinActivity = abstractPiglinEntity.getActivity();
            if (piglinActivity == PiglinActivity.DANCING) {
                float n = h / 60.0f;
                this.field_27464.roll = 0.5235988f + (float)Math.PI / 180 * MathHelper.sin(n * 30.0f) * 10.0f;
                this.field_27465.roll = -0.5235988f - (float)Math.PI / 180 * MathHelper.cos(n * 30.0f) * 10.0f;
                this.head.pivotX = MathHelper.sin(n * 10.0f);
                this.head.pivotY = MathHelper.sin(n * 40.0f) + 0.4f;
                this.rightArm.roll = (float)Math.PI / 180 * (70.0f + MathHelper.cos(n * 40.0f) * 10.0f);
                this.field_27433.roll = this.rightArm.roll * -1.0f;
                this.rightArm.pivotY = MathHelper.sin(n * 40.0f) * 0.5f + 1.5f;
                this.field_27433.pivotY = MathHelper.sin(n * 40.0f) * 0.5f + 1.5f;
                this.torso.pivotY = MathHelper.sin(n * 40.0f) * 0.35f;
            } else if (piglinActivity == PiglinActivity.ATTACKING_WITH_MELEE_WEAPON && this.handSwingProgress == 0.0f) {
                this.method_29354(mobEntity);
            } else if (piglinActivity == PiglinActivity.CROSSBOW_HOLD) {
                CrossbowPosing.hold(this.rightArm, this.field_27433, this.head, !((MobEntity)mobEntity).isLeftHanded());
            } else if (piglinActivity == PiglinActivity.CROSSBOW_CHARGE) {
                CrossbowPosing.charge(this.rightArm, this.field_27433, mobEntity, !((MobEntity)mobEntity).isLeftHanded());
            } else if (piglinActivity == PiglinActivity.ADMIRING_ITEM) {
                this.head.pitch = 0.5f;
                this.head.yaw = 0.0f;
                if (((MobEntity)mobEntity).isLeftHanded()) {
                    this.rightArm.yaw = -0.5f;
                    this.rightArm.pitch = -0.9f;
                } else {
                    this.field_27433.yaw = 0.5f;
                    this.field_27433.pitch = -0.9f;
                }
            }
        } else if (((Entity)mobEntity).getType() == EntityType.ZOMBIFIED_PIGLIN) {
            CrossbowPosing.method_29352(this.field_27433, this.rightArm, ((MobEntity)mobEntity).isAttacking(), this.handSwingProgress, h);
        }
        this.leftPantLeg.copyPositionAndRotation(this.leftLeg);
        this.rightPantLeg.copyPositionAndRotation(this.rightLeg);
        this.leftSleeve.copyPositionAndRotation(this.field_27433);
        this.rightSleeve.copyPositionAndRotation(this.rightArm);
        this.jacket.copyPositionAndRotation(this.torso);
        this.helmet.copyPositionAndRotation(this.head);
    }

    @Override
    protected void method_29353(T mobEntity, float f) {
        if (this.handSwingProgress > 0.0f && mobEntity instanceof PiglinEntity && ((PiglinEntity)mobEntity).getActivity() == PiglinActivity.ATTACKING_WITH_MELEE_WEAPON) {
            CrossbowPosing.method_29351(this.rightArm, this.field_27433, mobEntity, this.handSwingProgress, f);
            return;
        }
        super.method_29353(mobEntity, f);
    }

    private void method_29354(T mobEntity) {
        if (((MobEntity)mobEntity).isLeftHanded()) {
            this.field_27433.pitch = -1.8f;
        } else {
            this.rightArm.pitch = -1.8f;
        }
    }
}

