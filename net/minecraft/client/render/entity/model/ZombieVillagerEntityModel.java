/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.ModelWithHat;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ZombieVillagerEntityModel<T extends ZombieEntity>
extends BipedEntityModel<T>
implements ModelWithHat {
    private ModelPart hat;

    public ZombieVillagerEntityModel(float f, boolean bl) {
        super(f, 0.0f, 64, bl ? 32 : 64);
        if (bl) {
            this.head = new ModelPart(this, 0, 0);
            this.head.addCuboid(-4.0f, -10.0f, -4.0f, 8.0f, 8.0f, 8.0f, f);
            this.torso = new ModelPart(this, 16, 16);
            this.torso.addCuboid(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, f + 0.1f);
            this.rightLeg = new ModelPart(this, 0, 16);
            this.rightLeg.setPivot(-2.0f, 12.0f, 0.0f);
            this.rightLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, f + 0.1f);
            this.leftLeg = new ModelPart(this, 0, 16);
            this.leftLeg.mirror = true;
            this.leftLeg.setPivot(2.0f, 12.0f, 0.0f);
            this.leftLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, f + 0.1f);
        } else {
            this.head = new ModelPart(this, 0, 0);
            this.head.setTextureOffset(0, 0).addCuboid(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f, f);
            this.head.setTextureOffset(24, 0).addCuboid(-1.0f, -3.0f, -6.0f, 2.0f, 4.0f, 2.0f, f);
            this.helmet = new ModelPart(this, 32, 0);
            this.helmet.addCuboid(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f, f + 0.5f);
            this.hat = new ModelPart(this);
            this.hat.setTextureOffset(30, 47).addCuboid(-8.0f, -8.0f, -6.0f, 16.0f, 16.0f, 1.0f, f);
            this.hat.pitch = -1.5707964f;
            this.helmet.addChild(this.hat);
            this.torso = new ModelPart(this, 16, 20);
            this.torso.addCuboid(-4.0f, 0.0f, -3.0f, 8.0f, 12.0f, 6.0f, f);
            this.torso.setTextureOffset(0, 38).addCuboid(-4.0f, 0.0f, -3.0f, 8.0f, 18.0f, 6.0f, f + 0.05f);
            this.rightArm = new ModelPart(this, 44, 22);
            this.rightArm.addCuboid(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, f);
            this.rightArm.setPivot(-5.0f, 2.0f, 0.0f);
            this.leftArm = new ModelPart(this, 44, 22);
            this.leftArm.mirror = true;
            this.leftArm.addCuboid(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, f);
            this.leftArm.setPivot(5.0f, 2.0f, 0.0f);
            this.rightLeg = new ModelPart(this, 0, 22);
            this.rightLeg.setPivot(-2.0f, 12.0f, 0.0f);
            this.rightLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, f);
            this.leftLeg = new ModelPart(this, 0, 22);
            this.leftLeg.mirror = true;
            this.leftLeg.setPivot(2.0f, 12.0f, 0.0f);
            this.leftLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, f);
        }
    }

    public void method_17135(T zombieEntity, float f, float g, float h, float i, float j) {
        float m;
        super.method_17087(zombieEntity, f, g, h, i, j);
        float k = MathHelper.sin(this.handSwingProgress * (float)Math.PI);
        float l = MathHelper.sin((1.0f - (1.0f - this.handSwingProgress) * (1.0f - this.handSwingProgress)) * (float)Math.PI);
        this.rightArm.roll = 0.0f;
        this.leftArm.roll = 0.0f;
        this.rightArm.yaw = -(0.1f - k * 0.6f);
        this.leftArm.yaw = 0.1f - k * 0.6f;
        this.rightArm.pitch = m = (float)(-Math.PI) / (((MobEntity)zombieEntity).isAttacking() ? 1.5f : 2.25f);
        this.leftArm.pitch = m;
        this.rightArm.pitch += k * 1.2f - l * 0.4f;
        this.leftArm.pitch += k * 1.2f - l * 0.4f;
        this.rightArm.roll += MathHelper.cos(h * 0.09f) * 0.05f + 0.05f;
        this.leftArm.roll -= MathHelper.cos(h * 0.09f) * 0.05f + 0.05f;
        this.rightArm.pitch += MathHelper.sin(h * 0.067f) * 0.05f;
        this.leftArm.pitch -= MathHelper.sin(h * 0.067f) * 0.05f;
    }

    @Override
    public void setHatVisible(boolean bl) {
        this.head.visible = bl;
        this.helmet.visible = bl;
        this.hat.visible = bl;
    }
}

