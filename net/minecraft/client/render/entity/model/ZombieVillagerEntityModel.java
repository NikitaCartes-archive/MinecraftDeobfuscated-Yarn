/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.CrossbowPosing;
import net.minecraft.client.render.entity.model.ModelWithHat;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;

@Environment(value=EnvType.CLIENT)
public class ZombieVillagerEntityModel<T extends ZombieEntity>
extends BipedEntityModel<T>
implements ModelWithHat {
    private ModelPart hatRim;

    public ZombieVillagerEntityModel(float scale, boolean bl) {
        super(scale, 0.0f, 64, bl ? 32 : 64);
        if (bl) {
            this.head = new ModelPart(this, 0, 0);
            this.head.addCuboid(-4.0f, -10.0f, -4.0f, 8.0f, 8.0f, 8.0f, scale);
            this.body = new ModelPart(this, 16, 16);
            this.body.addCuboid(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f, scale + 0.1f);
            this.rightLeg = new ModelPart(this, 0, 16);
            this.rightLeg.setPivot(-2.0f, 12.0f, 0.0f);
            this.rightLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, scale + 0.1f);
            this.leftLeg = new ModelPart(this, 0, 16);
            this.leftLeg.mirror = true;
            this.leftLeg.setPivot(2.0f, 12.0f, 0.0f);
            this.leftLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, scale + 0.1f);
        } else {
            this.head = new ModelPart(this, 0, 0);
            this.head.setTextureOffset(0, 0).addCuboid(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f, scale);
            this.head.setTextureOffset(24, 0).addCuboid(-1.0f, -3.0f, -6.0f, 2.0f, 4.0f, 2.0f, scale);
            this.hat = new ModelPart(this, 32, 0);
            this.hat.addCuboid(-4.0f, -10.0f, -4.0f, 8.0f, 10.0f, 8.0f, scale + 0.5f);
            this.hatRim = new ModelPart(this);
            this.hatRim.setTextureOffset(30, 47).addCuboid(-8.0f, -8.0f, -6.0f, 16.0f, 16.0f, 1.0f, scale);
            this.hatRim.pitch = -1.5707964f;
            this.hat.addChild(this.hatRim);
            this.body = new ModelPart(this, 16, 20);
            this.body.addCuboid(-4.0f, 0.0f, -3.0f, 8.0f, 12.0f, 6.0f, scale);
            this.body.setTextureOffset(0, 38).addCuboid(-4.0f, 0.0f, -3.0f, 8.0f, 18.0f, 6.0f, scale + 0.05f);
            this.rightArm = new ModelPart(this, 44, 22);
            this.rightArm.addCuboid(-3.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, scale);
            this.rightArm.setPivot(-5.0f, 2.0f, 0.0f);
            this.leftArm = new ModelPart(this, 44, 22);
            this.leftArm.mirror = true;
            this.leftArm.addCuboid(-1.0f, -2.0f, -2.0f, 4.0f, 12.0f, 4.0f, scale);
            this.leftArm.setPivot(5.0f, 2.0f, 0.0f);
            this.rightLeg = new ModelPart(this, 0, 22);
            this.rightLeg.setPivot(-2.0f, 12.0f, 0.0f);
            this.rightLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, scale);
            this.leftLeg = new ModelPart(this, 0, 22);
            this.leftLeg.mirror = true;
            this.leftLeg.setPivot(2.0f, 12.0f, 0.0f);
            this.leftLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, scale);
        }
    }

    @Override
    public void setAngles(T zombieEntity, float f, float g, float h, float i, float j) {
        super.setAngles(zombieEntity, f, g, h, i, j);
        CrossbowPosing.method_29352(this.leftArm, this.rightArm, ((MobEntity)zombieEntity).isAttacking(), this.handSwingProgress, h);
    }

    @Override
    public void setHatVisible(boolean visible) {
        this.head.visible = visible;
        this.hat.visible = visible;
        this.hatRim.visible = visible;
    }
}

