/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class EvilVillagerEntityModel<T extends IllagerEntity>
extends EntityModel<T>
implements ModelWithArms,
ModelWithHead {
    protected final ModelPart field_3422;
    private final ModelPart field_3419;
    protected final ModelPart field_3425;
    protected final ModelPart field_3423;
    protected final ModelPart field_3420;
    protected final ModelPart field_3418;
    private final ModelPart field_3421;
    protected final ModelPart field_3426;
    protected final ModelPart field_3417;
    private float field_3424;

    public EvilVillagerEntityModel(float f, float g, int i, int j) {
        this.field_3422 = new ModelPart(this).setTextureSize(i, j);
        this.field_3422.setRotationPoint(0.0f, 0.0f + g, 0.0f);
        this.field_3422.setTextureOffset(0, 0).addCuboid(-4.0f, -10.0f, -4.0f, 8, 10, 8, f);
        this.field_3419 = new ModelPart(this, 32, 0).setTextureSize(i, j);
        this.field_3419.addCuboid(-4.0f, -10.0f, -4.0f, 8, 12, 8, f + 0.45f);
        this.field_3422.addChild(this.field_3419);
        this.field_3419.visible = false;
        this.field_3421 = new ModelPart(this).setTextureSize(i, j);
        this.field_3421.setRotationPoint(0.0f, g - 2.0f, 0.0f);
        this.field_3421.setTextureOffset(24, 0).addCuboid(-1.0f, -1.0f, -6.0f, 2, 4, 2, f);
        this.field_3422.addChild(this.field_3421);
        this.field_3425 = new ModelPart(this).setTextureSize(i, j);
        this.field_3425.setRotationPoint(0.0f, 0.0f + g, 0.0f);
        this.field_3425.setTextureOffset(16, 20).addCuboid(-4.0f, 0.0f, -3.0f, 8, 12, 6, f);
        this.field_3425.setTextureOffset(0, 38).addCuboid(-4.0f, 0.0f, -3.0f, 8, 18, 6, f + 0.5f);
        this.field_3423 = new ModelPart(this).setTextureSize(i, j);
        this.field_3423.setRotationPoint(0.0f, 0.0f + g + 2.0f, 0.0f);
        this.field_3423.setTextureOffset(44, 22).addCuboid(-8.0f, -2.0f, -2.0f, 4, 8, 4, f);
        ModelPart modelPart = new ModelPart(this, 44, 22).setTextureSize(i, j);
        modelPart.mirror = true;
        modelPart.addCuboid(4.0f, -2.0f, -2.0f, 4, 8, 4, f);
        this.field_3423.addChild(modelPart);
        this.field_3423.setTextureOffset(40, 38).addCuboid(-4.0f, 2.0f, -2.0f, 8, 4, 4, f);
        this.field_3420 = new ModelPart(this, 0, 22).setTextureSize(i, j);
        this.field_3420.setRotationPoint(-2.0f, 12.0f + g, 0.0f);
        this.field_3420.addCuboid(-2.0f, 0.0f, -2.0f, 4, 12, 4, f);
        this.field_3418 = new ModelPart(this, 0, 22).setTextureSize(i, j);
        this.field_3418.mirror = true;
        this.field_3418.setRotationPoint(2.0f, 12.0f + g, 0.0f);
        this.field_3418.addCuboid(-2.0f, 0.0f, -2.0f, 4, 12, 4, f);
        this.field_3426 = new ModelPart(this, 40, 46).setTextureSize(i, j);
        this.field_3426.addCuboid(-3.0f, -2.0f, -2.0f, 4, 12, 4, f);
        this.field_3426.setRotationPoint(-5.0f, 2.0f + g, 0.0f);
        this.field_3417 = new ModelPart(this, 40, 46).setTextureSize(i, j);
        this.field_3417.mirror = true;
        this.field_3417.addCuboid(-1.0f, -2.0f, -2.0f, 4, 12, 4, f);
        this.field_3417.setRotationPoint(5.0f, 2.0f + g, 0.0f);
    }

    public void method_17093(T illagerEntity, float f, float g, float h, float i, float j, float k) {
        this.method_17094(illagerEntity, f, g, h, i, j, k);
        this.field_3422.render(k);
        this.field_3425.render(k);
        this.field_3420.render(k);
        this.field_3418.render(k);
        if (((IllagerEntity)illagerEntity).getState() == IllagerEntity.State.CROSSED) {
            this.field_3423.render(k);
        } else {
            this.field_3426.render(k);
            this.field_3417.render(k);
        }
    }

    public void method_17094(T illagerEntity, float f, float g, float h, float i, float j, float k) {
        this.field_3422.yaw = i * ((float)Math.PI / 180);
        this.field_3422.pitch = j * ((float)Math.PI / 180);
        this.field_3423.rotationPointY = 3.0f;
        this.field_3423.rotationPointZ = -1.0f;
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
            float l = MathHelper.sin(this.handSwingProgress * (float)Math.PI);
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
            this.field_3426.rotationPointZ = 0.0f;
            this.field_3426.rotationPointX = -5.0f;
            this.field_3417.rotationPointZ = 0.0f;
            this.field_3417.rotationPointX = 5.0f;
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
            float l = MathHelper.clamp(this.field_3424, 0.0f, 25.0f);
            this.field_3417.yaw = MathHelper.lerp(l / 25.0f, 0.4f, 0.85f);
            this.field_3417.pitch = MathHelper.lerp(l / 25.0f, this.field_3417.pitch, -1.5707964f);
        } else if (state == IllagerEntity.State.CELEBRATING) {
            this.field_3426.rotationPointZ = 0.0f;
            this.field_3426.rotationPointX = -5.0f;
            this.field_3426.pitch = MathHelper.cos(h * 0.6662f) * 0.05f;
            this.field_3426.roll = 2.670354f;
            this.field_3426.yaw = 0.0f;
            this.field_3417.rotationPointZ = 0.0f;
            this.field_3417.rotationPointX = 5.0f;
            this.field_3417.pitch = MathHelper.cos(h * 0.6662f) * 0.05f;
            this.field_3417.roll = -2.3561945f;
            this.field_3417.yaw = 0.0f;
        }
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
    public void setArmAngle(float f, Arm arm) {
        this.method_2813(arm).applyTransform(0.0625f);
    }

    @Override
    public /* synthetic */ void setAngles(Entity entity, float f, float g, float h, float i, float j, float k) {
        this.method_17094((IllagerEntity)entity, f, g, h, i, j, k);
    }

    @Override
    public /* synthetic */ void render(Entity entity, float f, float g, float h, float i, float j, float k) {
        this.method_17093((IllagerEntity)entity, f, g, h, i, j, k);
    }
}

