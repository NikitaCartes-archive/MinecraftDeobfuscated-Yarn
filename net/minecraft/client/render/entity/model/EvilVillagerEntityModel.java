/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4595;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public class EvilVillagerEntityModel<T extends IllagerEntity>
extends class_4595<T>
implements ModelWithArms,
ModelWithHead {
    private final ModelPart field_3422;
    private final ModelPart field_3419;
    private final ModelPart field_3425;
    private final ModelPart field_3423;
    private final ModelPart field_3420;
    private final ModelPart field_3418;
    private final ModelPart field_3426;
    private final ModelPart field_3417;
    private float field_3424;

    public EvilVillagerEntityModel(float f, float g, int i, int j) {
        super(RenderLayer::getEntityCutoutNoCull);
        this.field_3422 = new ModelPart(this).setTextureSize(i, j);
        this.field_3422.setPivot(0.0f, 0.0f + g, 0.0f);
        this.field_3422.setTextureOffset(0, 0).addCuboid(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f, f);
        this.field_3419 = new ModelPart(this, 32, 0).setTextureSize(i, j);
        this.field_3419.addCuboid(-4.0f, -10.0f, -4.0f, 8.0f, 12.0f, 8.0f, f + 0.45f);
        this.field_3422.addChild(this.field_3419);
        this.field_3419.visible = false;
        ModelPart modelPart = new ModelPart(this).setTextureSize(i, j);
        modelPart.setPivot(0.0f, g - 2.0f, 0.0f);
        modelPart.setTextureOffset(24, 0).addCuboid(-1.0f, -1.0f, -6.0f, 2.0f, 4.0f, 2.0f, f);
        this.field_3422.addChild(modelPart);
        this.field_3425 = new ModelPart(this).setTextureSize(i, j);
        this.field_3425.setPivot(0.0f, 0.0f + g, 0.0f);
        this.field_3425.setTextureOffset(16, 20).addCuboid(-4.0f, 0.0f, -3.0f, 8.0f, 12.0f, 6.0f, f);
        this.field_3425.setTextureOffset(0, 38).addCuboid(-4.0f, 0.0f, -3.0f, 8.0f, 18.0f, 6.0f, f + 0.5f);
        this.field_3423 = new ModelPart(this).setTextureSize(i, j);
        this.field_3423.setPivot(0.0f, 0.0f + g + 2.0f, 0.0f);
        this.field_3423.setTextureOffset(44, 22).addCuboid(-8.0f, -2.0f, -2.0f, 4.0f, 8.0f, 4.0f, f);
        ModelPart modelPart2 = new ModelPart(this, 44, 22).setTextureSize(i, j);
        modelPart2.mirror = true;
        modelPart2.addCuboid(4.0f, -2.0f, -2.0f, 4.0f, 8.0f, 4.0f, f);
        this.field_3423.addChild(modelPart2);
        this.field_3423.setTextureOffset(40, 38).addCuboid(-4.0f, 2.0f, -2.0f, 8.0f, 4.0f, 4.0f, f);
        this.field_3420 = new ModelPart(this, 0, 22).setTextureSize(i, j);
        this.field_3420.setPivot(-2.0f, 12.0f + g, 0.0f);
        this.field_3420.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, f);
        this.field_3418 = new ModelPart(this, 0, 22).setTextureSize(i, j);
        this.field_3418.mirror = true;
        this.field_3418.setPivot(2.0f, 12.0f + g, 0.0f);
        this.field_3418.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, f);
        this.field_3426 = new ModelPart(this, 40, 46).setTextureSize(i, j);
        this.field_3426.addCuboid(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, f);
        this.field_3426.setPivot(-5.0f, 2.0f + g, 0.0f);
        this.field_3417 = new ModelPart(this, 40, 46).setTextureSize(i, j);
        this.field_3417.mirror = true;
        this.field_3417.addCuboid(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, f);
        this.field_3417.setPivot(5.0f, 2.0f + g, 0.0f);
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of(this.field_3422, this.field_3425, this.field_3420, this.field_3418, this.field_3423, this.field_3426, this.field_3417);
    }

    public void method_17094(T illagerEntity, float f, float g, float h, float i, float j, float k) {
        boolean bl;
        float l;
        this.field_3422.yaw = i * ((float)Math.PI / 180);
        this.field_3422.pitch = j * ((float)Math.PI / 180);
        this.field_3423.pivotY = 3.0f;
        this.field_3423.pivotZ = -1.0f;
        this.field_3423.pitch = -0.75f;
        if (this.isRiding) {
            this.field_3426.pitch = -0.62831855f;
            this.field_3426.yaw = 0.0f;
            this.field_3426.roll = 0.0f;
            this.field_3417.pitch = -0.62831855f;
            this.field_3417.yaw = 0.0f;
            this.field_3417.roll = 0.0f;
            this.field_3420.pitch = -1.4137167f;
            this.field_3420.yaw = 0.31415927f;
            this.field_3420.roll = 0.07853982f;
            this.field_3418.pitch = -1.4137167f;
            this.field_3418.yaw = -0.31415927f;
            this.field_3418.roll = -0.07853982f;
        } else {
            this.field_3426.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 2.0f * g * 0.5f;
            this.field_3426.yaw = 0.0f;
            this.field_3426.roll = 0.0f;
            this.field_3417.pitch = MathHelper.cos(f * 0.6662f) * 2.0f * g * 0.5f;
            this.field_3417.yaw = 0.0f;
            this.field_3417.roll = 0.0f;
            this.field_3420.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g * 0.5f;
            this.field_3420.yaw = 0.0f;
            this.field_3420.roll = 0.0f;
            this.field_3418.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g * 0.5f;
            this.field_3418.yaw = 0.0f;
            this.field_3418.roll = 0.0f;
        }
        IllagerEntity.State state = ((IllagerEntity)illagerEntity).getState();
        if (state == IllagerEntity.State.ATTACKING) {
            l = MathHelper.sin(this.handSwingProgress * (float)Math.PI);
            float m = MathHelper.sin((1.0f - (1.0f - this.handSwingProgress) * (1.0f - this.handSwingProgress)) * (float)Math.PI);
            this.field_3426.roll = 0.0f;
            this.field_3417.roll = 0.0f;
            this.field_3426.yaw = 0.15707964f;
            this.field_3417.yaw = -0.15707964f;
            if (((MobEntity)illagerEntity).getMainArm() == Arm.RIGHT) {
                this.field_3426.pitch = -1.8849558f + MathHelper.cos(h * 0.09f) * 0.15f;
                this.field_3417.pitch = -0.0f + MathHelper.cos(h * 0.19f) * 0.5f;
                this.field_3426.pitch += l * 2.2f - m * 0.4f;
                this.field_3417.pitch += l * 1.2f - m * 0.4f;
            } else {
                this.field_3426.pitch = -0.0f + MathHelper.cos(h * 0.19f) * 0.5f;
                this.field_3417.pitch = -1.8849558f + MathHelper.cos(h * 0.09f) * 0.15f;
                this.field_3426.pitch += l * 1.2f - m * 0.4f;
                this.field_3417.pitch += l * 2.2f - m * 0.4f;
            }
            this.field_3426.roll += MathHelper.cos(h * 0.09f) * 0.05f + 0.05f;
            this.field_3417.roll -= MathHelper.cos(h * 0.09f) * 0.05f + 0.05f;
            this.field_3426.pitch += MathHelper.sin(h * 0.067f) * 0.05f;
            this.field_3417.pitch -= MathHelper.sin(h * 0.067f) * 0.05f;
        } else if (state == IllagerEntity.State.SPELLCASTING) {
            this.field_3426.pivotZ = 0.0f;
            this.field_3426.pivotX = -5.0f;
            this.field_3417.pivotZ = 0.0f;
            this.field_3417.pivotX = 5.0f;
            this.field_3426.pitch = MathHelper.cos(h * 0.6662f) * 0.25f;
            this.field_3417.pitch = MathHelper.cos(h * 0.6662f) * 0.25f;
            this.field_3426.roll = 2.3561945f;
            this.field_3417.roll = -2.3561945f;
            this.field_3426.yaw = 0.0f;
            this.field_3417.yaw = 0.0f;
        } else if (state == IllagerEntity.State.BOW_AND_ARROW) {
            this.field_3426.yaw = -0.1f + this.field_3422.yaw;
            this.field_3426.pitch = -1.5707964f + this.field_3422.pitch;
            this.field_3417.pitch = -0.9424779f + this.field_3422.pitch;
            this.field_3417.yaw = this.field_3422.yaw - 0.4f;
            this.field_3417.roll = 1.5707964f;
        } else if (state == IllagerEntity.State.CROSSBOW_HOLD) {
            this.field_3426.yaw = -0.3f + this.field_3422.yaw;
            this.field_3417.yaw = 0.6f + this.field_3422.yaw;
            this.field_3426.pitch = -1.5707964f + this.field_3422.pitch + 0.1f;
            this.field_3417.pitch = -1.5f + this.field_3422.pitch;
        } else if (state == IllagerEntity.State.CROSSBOW_CHARGE) {
            this.field_3426.yaw = -0.8f;
            this.field_3426.pitch = -0.97079635f;
            this.field_3417.pitch = -0.97079635f;
            l = MathHelper.clamp(this.field_3424, 0.0f, 25.0f);
            this.field_3417.yaw = MathHelper.lerp(l / 25.0f, 0.4f, 0.85f);
            this.field_3417.pitch = MathHelper.lerp(l / 25.0f, this.field_3417.pitch, -1.5707964f);
        } else if (state == IllagerEntity.State.CELEBRATING) {
            this.field_3426.pivotZ = 0.0f;
            this.field_3426.pivotX = -5.0f;
            this.field_3426.pitch = MathHelper.cos(h * 0.6662f) * 0.05f;
            this.field_3426.roll = 2.670354f;
            this.field_3426.yaw = 0.0f;
            this.field_3417.pivotZ = 0.0f;
            this.field_3417.pivotX = 5.0f;
            this.field_3417.pitch = MathHelper.cos(h * 0.6662f) * 0.05f;
            this.field_3417.roll = -2.3561945f;
            this.field_3417.yaw = 0.0f;
        }
        this.field_3423.visible = bl = state == IllagerEntity.State.CROSSED;
        this.field_3417.visible = !bl;
        this.field_3426.visible = !bl;
    }

    public void method_17092(T illagerEntity, float f, float g, float h) {
        this.field_3424 = ((LivingEntity)illagerEntity).getItemUseTime();
        super.animateModel(illagerEntity, f, g, h);
    }

    private ModelPart method_2813(Arm arm) {
        if (arm == Arm.LEFT) {
            return this.field_3417;
        }
        return this.field_3426;
    }

    public ModelPart method_2812() {
        return this.field_3419;
    }

    @Override
    public ModelPart getHead() {
        return this.field_3422;
    }

    @Override
    public void setArmAngle(float f, Arm arm, MatrixStack matrixStack) {
        this.method_2813(arm).rotate(matrixStack, 0.0625f);
    }
}

