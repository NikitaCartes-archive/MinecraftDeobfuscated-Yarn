/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.CrossbowPosing;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BipedEntityModel<T extends LivingEntity>
extends AnimalModel<T>
implements ModelWithArms,
ModelWithHead {
    public ModelPart head;
    public ModelPart hat;
    public ModelPart body;
    public ModelPart rightArm;
    public ModelPart leftArm;
    public ModelPart rightLeg;
    public ModelPart leftLeg;
    public ArmPose leftArmPose = ArmPose.EMPTY;
    public ArmPose rightArmPose = ArmPose.EMPTY;
    public boolean sneaking;
    public float leaningPitch;

    public BipedEntityModel(float scale) {
        this(RenderLayer::getEntityCutoutNoCull, scale, 0.0f, 64, 32);
    }

    protected BipedEntityModel(float scale, float pivotY, int textureWidth, int textureHeight) {
        this(RenderLayer::getEntityCutoutNoCull, scale, pivotY, textureWidth, textureHeight);
    }

    public BipedEntityModel(Function<Identifier, RenderLayer> texturedLayerFactory, float scale, float pivotY, int textureWidth, int textureHeight) {
        super(texturedLayerFactory, true, 16.0f, 0.0f, 2.0f, 2.0f, 24.0f);
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.head = new ModelPart(this, 0, 0);
        this.head.addCuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, scale);
        this.head.setPivot(0.0f, 0.0f + pivotY, 0.0f);
        this.hat = new ModelPart(this, 32, 0);
        this.hat.addCuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, scale + 0.5f);
        this.hat.setPivot(0.0f, 0.0f + pivotY, 0.0f);
        this.body = new ModelPart(this, 16, 16);
        this.body.addCuboid(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, scale);
        this.body.setPivot(0.0f, 0.0f + pivotY, 0.0f);
        this.rightArm = new ModelPart(this, 40, 16);
        this.rightArm.addCuboid(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, scale);
        this.rightArm.setPivot(-5.0f, 2.0f + pivotY, 0.0f);
        this.leftArm = new ModelPart(this, 40, 16);
        this.leftArm.mirror = true;
        this.leftArm.addCuboid(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, scale);
        this.leftArm.setPivot(5.0f, 2.0f + pivotY, 0.0f);
        this.rightLeg = new ModelPart(this, 0, 16);
        this.rightLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, scale);
        this.rightLeg.setPivot(-1.9f, 12.0f + pivotY, 0.0f);
        this.leftLeg = new ModelPart(this, 0, 16);
        this.leftLeg.mirror = true;
        this.leftLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, scale);
        this.leftLeg.setPivot(1.9f, 12.0f + pivotY, 0.0f);
    }

    @Override
    protected Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of(this.head);
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of(this.body, this.rightArm, this.leftArm, this.rightLeg, this.leftLeg, this.hat);
    }

    @Override
    public void animateModel(T livingEntity, float f, float g, float h) {
        this.leaningPitch = ((LivingEntity)livingEntity).getLeaningPitch(h);
        super.animateModel(livingEntity, f, g, h);
    }

    @Override
    public void setAngles(T livingEntity, float f, float g, float h, float i, float j) {
        boolean bl4;
        boolean bl = ((LivingEntity)livingEntity).getRoll() > 4;
        boolean bl2 = ((LivingEntity)livingEntity).isInSwimmingPose();
        this.head.yaw = i * ((float)Math.PI / 180);
        this.head.pitch = bl ? -0.7853982f : (this.leaningPitch > 0.0f ? (bl2 ? this.lerpAngle(this.leaningPitch, this.head.pitch, -0.7853982f) : this.lerpAngle(this.leaningPitch, this.head.pitch, j * ((float)Math.PI / 180))) : j * ((float)Math.PI / 180));
        this.body.yaw = 0.0f;
        this.rightArm.pivotZ = 0.0f;
        this.rightArm.pivotX = -5.0f;
        this.leftArm.pivotZ = 0.0f;
        this.leftArm.pivotX = 5.0f;
        float k = 1.0f;
        if (bl) {
            k = (float)((Entity)livingEntity).getVelocity().lengthSquared();
            k /= 0.2f;
            k *= k * k;
        }
        if (k < 1.0f) {
            k = 1.0f;
        }
        this.rightArm.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 2.0f * g * 0.5f / k;
        this.leftArm.pitch = MathHelper.cos(f * 0.6662f) * 2.0f * g * 0.5f / k;
        this.rightArm.roll = 0.0f;
        this.leftArm.roll = 0.0f;
        this.rightLeg.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g / k;
        this.leftLeg.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g / k;
        this.rightLeg.yaw = 0.0f;
        this.leftLeg.yaw = 0.0f;
        this.rightLeg.roll = 0.0f;
        this.leftLeg.roll = 0.0f;
        if (this.riding) {
            this.rightArm.pitch += -0.62831855f;
            this.leftArm.pitch += -0.62831855f;
            this.rightLeg.pitch = -1.4137167f;
            this.rightLeg.yaw = 0.31415927f;
            this.rightLeg.roll = 0.07853982f;
            this.leftLeg.pitch = -1.4137167f;
            this.leftLeg.yaw = -0.31415927f;
            this.leftLeg.roll = -0.07853982f;
        }
        this.rightArm.yaw = 0.0f;
        this.leftArm.yaw = 0.0f;
        boolean bl3 = ((LivingEntity)livingEntity).getMainArm() == Arm.RIGHT;
        boolean bl5 = bl4 = bl3 ? this.leftArmPose.method_30156() : this.rightArmPose.method_30156();
        if (bl3 != bl4) {
            this.method_30155(livingEntity);
            this.method_30154(livingEntity);
        } else {
            this.method_30154(livingEntity);
            this.method_30155(livingEntity);
        }
        this.method_29353(livingEntity, h);
        if (this.sneaking) {
            this.body.pitch = 0.5f;
            this.rightArm.pitch += 0.4f;
            this.leftArm.pitch += 0.4f;
            this.rightLeg.pivotZ = 4.0f;
            this.leftLeg.pivotZ = 4.0f;
            this.rightLeg.pivotY = 12.2f;
            this.leftLeg.pivotY = 12.2f;
            this.head.pivotY = 4.2f;
            this.body.pivotY = 3.2f;
            this.leftArm.pivotY = 5.2f;
            this.rightArm.pivotY = 5.2f;
        } else {
            this.body.pitch = 0.0f;
            this.rightLeg.pivotZ = 0.1f;
            this.leftLeg.pivotZ = 0.1f;
            this.rightLeg.pivotY = 12.0f;
            this.leftLeg.pivotY = 12.0f;
            this.head.pivotY = 0.0f;
            this.body.pivotY = 0.0f;
            this.leftArm.pivotY = 2.0f;
            this.rightArm.pivotY = 2.0f;
        }
        CrossbowPosing.method_29350(this.rightArm, this.leftArm, h);
        if (this.leaningPitch > 0.0f) {
            float o;
            float n;
            float l = f % 26.0f;
            Arm arm = this.getPreferredArm(livingEntity);
            float m = arm == Arm.RIGHT && this.handSwingProgress > 0.0f ? 0.0f : this.leaningPitch;
            float f2 = n = arm == Arm.LEFT && this.handSwingProgress > 0.0f ? 0.0f : this.leaningPitch;
            if (l < 14.0f) {
                this.leftArm.pitch = this.lerpAngle(n, this.leftArm.pitch, 0.0f);
                this.rightArm.pitch = MathHelper.lerp(m, this.rightArm.pitch, 0.0f);
                this.leftArm.yaw = this.lerpAngle(n, this.leftArm.yaw, (float)Math.PI);
                this.rightArm.yaw = MathHelper.lerp(m, this.rightArm.yaw, (float)Math.PI);
                this.leftArm.roll = this.lerpAngle(n, this.leftArm.roll, (float)Math.PI + 1.8707964f * this.method_2807(l) / this.method_2807(14.0f));
                this.rightArm.roll = MathHelper.lerp(m, this.rightArm.roll, (float)Math.PI - 1.8707964f * this.method_2807(l) / this.method_2807(14.0f));
            } else if (l >= 14.0f && l < 22.0f) {
                o = (l - 14.0f) / 8.0f;
                this.leftArm.pitch = this.lerpAngle(n, this.leftArm.pitch, 1.5707964f * o);
                this.rightArm.pitch = MathHelper.lerp(m, this.rightArm.pitch, 1.5707964f * o);
                this.leftArm.yaw = this.lerpAngle(n, this.leftArm.yaw, (float)Math.PI);
                this.rightArm.yaw = MathHelper.lerp(m, this.rightArm.yaw, (float)Math.PI);
                this.leftArm.roll = this.lerpAngle(n, this.leftArm.roll, 5.012389f - 1.8707964f * o);
                this.rightArm.roll = MathHelper.lerp(m, this.rightArm.roll, 1.2707963f + 1.8707964f * o);
            } else if (l >= 22.0f && l < 26.0f) {
                o = (l - 22.0f) / 4.0f;
                this.leftArm.pitch = this.lerpAngle(n, this.leftArm.pitch, 1.5707964f - 1.5707964f * o);
                this.rightArm.pitch = MathHelper.lerp(m, this.rightArm.pitch, 1.5707964f - 1.5707964f * o);
                this.leftArm.yaw = this.lerpAngle(n, this.leftArm.yaw, (float)Math.PI);
                this.rightArm.yaw = MathHelper.lerp(m, this.rightArm.yaw, (float)Math.PI);
                this.leftArm.roll = this.lerpAngle(n, this.leftArm.roll, (float)Math.PI);
                this.rightArm.roll = MathHelper.lerp(m, this.rightArm.roll, (float)Math.PI);
            }
            o = 0.3f;
            float p = 0.33333334f;
            this.leftLeg.pitch = MathHelper.lerp(this.leaningPitch, this.leftLeg.pitch, 0.3f * MathHelper.cos(f * 0.33333334f + (float)Math.PI));
            this.rightLeg.pitch = MathHelper.lerp(this.leaningPitch, this.rightLeg.pitch, 0.3f * MathHelper.cos(f * 0.33333334f));
        }
        this.hat.copyTransform(this.head);
    }

    private void method_30154(T livingEntity) {
        switch (this.rightArmPose) {
            case EMPTY: {
                this.rightArm.yaw = 0.0f;
                break;
            }
            case BLOCK: {
                this.rightArm.pitch = this.rightArm.pitch * 0.5f - 0.9424779f;
                this.rightArm.yaw = -0.5235988f;
                break;
            }
            case ITEM: {
                this.rightArm.pitch = this.rightArm.pitch * 0.5f - 0.31415927f;
                this.rightArm.yaw = 0.0f;
                break;
            }
            case THROW_SPEAR: {
                this.rightArm.pitch = this.rightArm.pitch * 0.5f - (float)Math.PI;
                this.rightArm.yaw = 0.0f;
                break;
            }
            case BOW_AND_ARROW: {
                this.rightArm.yaw = -0.1f + this.head.yaw;
                this.leftArm.yaw = 0.1f + this.head.yaw + 0.4f;
                this.rightArm.pitch = -1.5707964f + this.head.pitch;
                this.leftArm.pitch = -1.5707964f + this.head.pitch;
                break;
            }
            case CROSSBOW_CHARGE: {
                CrossbowPosing.charge(this.rightArm, this.leftArm, livingEntity, true);
                break;
            }
            case CROSSBOW_HOLD: {
                CrossbowPosing.hold(this.rightArm, this.leftArm, this.head, true);
            }
        }
    }

    private void method_30155(T livingEntity) {
        switch (this.leftArmPose) {
            case EMPTY: {
                this.leftArm.yaw = 0.0f;
                break;
            }
            case BLOCK: {
                this.leftArm.pitch = this.leftArm.pitch * 0.5f - 0.9424779f;
                this.leftArm.yaw = 0.5235988f;
                break;
            }
            case ITEM: {
                this.leftArm.pitch = this.leftArm.pitch * 0.5f - 0.31415927f;
                this.leftArm.yaw = 0.0f;
                break;
            }
            case THROW_SPEAR: {
                this.leftArm.pitch = this.leftArm.pitch * 0.5f - (float)Math.PI;
                this.leftArm.yaw = 0.0f;
                break;
            }
            case BOW_AND_ARROW: {
                this.rightArm.yaw = -0.1f + this.head.yaw - 0.4f;
                this.leftArm.yaw = 0.1f + this.head.yaw;
                this.rightArm.pitch = -1.5707964f + this.head.pitch;
                this.leftArm.pitch = -1.5707964f + this.head.pitch;
                break;
            }
            case CROSSBOW_CHARGE: {
                CrossbowPosing.charge(this.rightArm, this.leftArm, livingEntity, false);
                break;
            }
            case CROSSBOW_HOLD: {
                CrossbowPosing.hold(this.rightArm, this.leftArm, this.head, false);
            }
        }
    }

    protected void method_29353(T livingEntity, float f) {
        if (this.handSwingProgress <= 0.0f) {
            return;
        }
        Arm arm = this.getPreferredArm(livingEntity);
        ModelPart modelPart = this.getArm(arm);
        float g = this.handSwingProgress;
        this.body.yaw = MathHelper.sin(MathHelper.sqrt(g) * ((float)Math.PI * 2)) * 0.2f;
        if (arm == Arm.LEFT) {
            this.body.yaw *= -1.0f;
        }
        this.rightArm.pivotZ = MathHelper.sin(this.body.yaw) * 5.0f;
        this.rightArm.pivotX = -MathHelper.cos(this.body.yaw) * 5.0f;
        this.leftArm.pivotZ = -MathHelper.sin(this.body.yaw) * 5.0f;
        this.leftArm.pivotX = MathHelper.cos(this.body.yaw) * 5.0f;
        this.rightArm.yaw += this.body.yaw;
        this.leftArm.yaw += this.body.yaw;
        this.leftArm.pitch += this.body.yaw;
        g = 1.0f - this.handSwingProgress;
        g *= g;
        g *= g;
        g = 1.0f - g;
        float h = MathHelper.sin(g * (float)Math.PI);
        float i = MathHelper.sin(this.handSwingProgress * (float)Math.PI) * -(this.head.pitch - 0.7f) * 0.75f;
        modelPart.pitch = (float)((double)modelPart.pitch - ((double)h * 1.2 + (double)i));
        modelPart.yaw += this.body.yaw * 2.0f;
        modelPart.roll += MathHelper.sin(this.handSwingProgress * (float)Math.PI) * -0.4f;
    }

    protected float lerpAngle(float angleOne, float angleTwo, float magnitude) {
        float f = (magnitude - angleTwo) % ((float)Math.PI * 2);
        if (f < (float)(-Math.PI)) {
            f += (float)Math.PI * 2;
        }
        if (f >= (float)Math.PI) {
            f -= (float)Math.PI * 2;
        }
        return angleTwo + angleOne * f;
    }

    private float method_2807(float f) {
        return -65.0f * f + f * f;
    }

    public void setAttributes(BipedEntityModel<T> model) {
        super.copyStateTo(model);
        model.leftArmPose = this.leftArmPose;
        model.rightArmPose = this.rightArmPose;
        model.sneaking = this.sneaking;
        model.head.copyTransform(this.head);
        model.hat.copyTransform(this.hat);
        model.body.copyTransform(this.body);
        model.rightArm.copyTransform(this.rightArm);
        model.leftArm.copyTransform(this.leftArm);
        model.rightLeg.copyTransform(this.rightLeg);
        model.leftLeg.copyTransform(this.leftLeg);
    }

    public void setVisible(boolean visible) {
        this.head.visible = visible;
        this.hat.visible = visible;
        this.body.visible = visible;
        this.rightArm.visible = visible;
        this.leftArm.visible = visible;
        this.rightLeg.visible = visible;
        this.leftLeg.visible = visible;
    }

    @Override
    public void setArmAngle(Arm arm, MatrixStack matrices) {
        this.getArm(arm).rotate(matrices);
    }

    protected ModelPart getArm(Arm arm) {
        if (arm == Arm.LEFT) {
            return this.leftArm;
        }
        return this.rightArm;
    }

    @Override
    public ModelPart getHead() {
        return this.head;
    }

    protected Arm getPreferredArm(T entity) {
        Arm arm = ((LivingEntity)entity).getMainArm();
        return ((LivingEntity)entity).preferredHand == Hand.MAIN_HAND ? arm : arm.getOpposite();
    }

    @Environment(value=EnvType.CLIENT)
    public static enum ArmPose {
        EMPTY(false),
        ITEM(false),
        BLOCK(false),
        BOW_AND_ARROW(true),
        THROW_SPEAR(false),
        CROSSBOW_CHARGE(true),
        CROSSBOW_HOLD(true);

        private final boolean field_25722;

        private ArmPose(boolean bl) {
            this.field_25722 = bl;
        }

        public boolean method_30156() {
            return this.field_25722;
        }
    }
}

