/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;

@Environment(value=EnvType.CLIENT)
public class PlayerEntityModel<T extends LivingEntity>
extends BipedEntityModel<T> {
    public final ModelPart leftArmOverlay;
    public final ModelPart rightArmOverlay;
    public final ModelPart leftLegOverlay;
    public final ModelPart rightLegOverlay;
    public final ModelPart bodyOverlay;
    private final ModelPart cape;
    private final ModelPart ears;
    private final boolean thinArms;

    public PlayerEntityModel(float f, boolean bl) {
        super(f, 0.0f, 64, 64);
        this.thinArms = bl;
        this.ears = new ModelPart(this, 24, 0);
        this.ears.addCuboid(-3.0f, -6.0f, -1.0f, 6, 6, 1, f);
        this.cape = new ModelPart(this, 0, 0);
        this.cape.setTextureSize(64, 32);
        this.cape.addCuboid(-5.0f, 0.0f, -1.0f, 10, 16, 1, f);
        if (bl) {
            this.leftArm = new ModelPart(this, 32, 48);
            this.leftArm.addCuboid(-1.0f, -2.0f, -2.0f, 3, 12, 4, f);
            this.leftArm.setRotationPoint(5.0f, 2.5f, 0.0f);
            this.rightArm = new ModelPart(this, 40, 16);
            this.rightArm.addCuboid(-2.0f, -2.0f, -2.0f, 3, 12, 4, f);
            this.rightArm.setRotationPoint(-5.0f, 2.5f, 0.0f);
            this.leftArmOverlay = new ModelPart(this, 48, 48);
            this.leftArmOverlay.addCuboid(-1.0f, -2.0f, -2.0f, 3, 12, 4, f + 0.25f);
            this.leftArmOverlay.setRotationPoint(5.0f, 2.5f, 0.0f);
            this.rightArmOverlay = new ModelPart(this, 40, 32);
            this.rightArmOverlay.addCuboid(-2.0f, -2.0f, -2.0f, 3, 12, 4, f + 0.25f);
            this.rightArmOverlay.setRotationPoint(-5.0f, 2.5f, 10.0f);
        } else {
            this.leftArm = new ModelPart(this, 32, 48);
            this.leftArm.addCuboid(-1.0f, -2.0f, -2.0f, 4, 12, 4, f);
            this.leftArm.setRotationPoint(5.0f, 2.0f, 0.0f);
            this.leftArmOverlay = new ModelPart(this, 48, 48);
            this.leftArmOverlay.addCuboid(-1.0f, -2.0f, -2.0f, 4, 12, 4, f + 0.25f);
            this.leftArmOverlay.setRotationPoint(5.0f, 2.0f, 0.0f);
            this.rightArmOverlay = new ModelPart(this, 40, 32);
            this.rightArmOverlay.addCuboid(-3.0f, -2.0f, -2.0f, 4, 12, 4, f + 0.25f);
            this.rightArmOverlay.setRotationPoint(-5.0f, 2.0f, 10.0f);
        }
        this.leftLeg = new ModelPart(this, 16, 48);
        this.leftLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4, 12, 4, f);
        this.leftLeg.setRotationPoint(1.9f, 12.0f, 0.0f);
        this.leftLegOverlay = new ModelPart(this, 0, 48);
        this.leftLegOverlay.addCuboid(-2.0f, 0.0f, -2.0f, 4, 12, 4, f + 0.25f);
        this.leftLegOverlay.setRotationPoint(1.9f, 12.0f, 0.0f);
        this.rightLegOverlay = new ModelPart(this, 0, 32);
        this.rightLegOverlay.addCuboid(-2.0f, 0.0f, -2.0f, 4, 12, 4, f + 0.25f);
        this.rightLegOverlay.setRotationPoint(-1.9f, 12.0f, 0.0f);
        this.bodyOverlay = new ModelPart(this, 16, 32);
        this.bodyOverlay.addCuboid(-4.0f, 0.0f, -2.0f, 8, 12, 4, f + 0.25f);
        this.bodyOverlay.setRotationPoint(0.0f, 0.0f, 0.0f);
    }

    @Override
    public void method_17088(T livingEntity, float f, float g, float h, float i, float j, float k) {
        super.method_17088(livingEntity, f, g, h, i, j, k);
        RenderSystem.pushMatrix();
        if (this.isChild) {
            float l = 2.0f;
            RenderSystem.scalef(0.5f, 0.5f, 0.5f);
            RenderSystem.translatef(0.0f, 24.0f * k, 0.0f);
            this.leftLegOverlay.render(k);
            this.rightLegOverlay.render(k);
            this.leftArmOverlay.render(k);
            this.rightArmOverlay.render(k);
            this.bodyOverlay.render(k);
        } else {
            if (((Entity)livingEntity).isInSneakingPose()) {
                RenderSystem.translatef(0.0f, 0.2f, 0.0f);
            }
            this.leftLegOverlay.render(k);
            this.rightLegOverlay.render(k);
            this.leftArmOverlay.render(k);
            this.rightArmOverlay.render(k);
            this.bodyOverlay.render(k);
        }
        RenderSystem.popMatrix();
    }

    public void renderEars(float f) {
        this.ears.copyRotation(this.head);
        this.ears.rotationPointX = 0.0f;
        this.ears.rotationPointY = 0.0f;
        this.ears.render(f);
    }

    public void renderCape(float f) {
        this.cape.render(f);
    }

    @Override
    public void method_17087(T livingEntity, float f, float g, float h, float i, float j, float k) {
        super.method_17087(livingEntity, f, g, h, i, j, k);
        this.leftLegOverlay.copyRotation(this.leftLeg);
        this.rightLegOverlay.copyRotation(this.rightLeg);
        this.leftArmOverlay.copyRotation(this.leftArm);
        this.rightArmOverlay.copyRotation(this.rightArm);
        this.bodyOverlay.copyRotation(this.body);
        this.cape.rotationPointY = ((Entity)livingEntity).isInSneakingPose() ? 2.0f : 0.0f;
    }

    @Override
    public void setVisible(boolean bl) {
        super.setVisible(bl);
        this.leftArmOverlay.visible = bl;
        this.rightArmOverlay.visible = bl;
        this.leftLegOverlay.visible = bl;
        this.rightLegOverlay.visible = bl;
        this.bodyOverlay.visible = bl;
        this.cape.visible = bl;
        this.ears.visible = bl;
    }

    @Override
    public void setArmAngle(float f, Arm arm) {
        ModelPart modelPart = this.getArm(arm);
        if (this.thinArms) {
            float g = 0.5f * (float)(arm == Arm.RIGHT ? 1 : -1);
            modelPart.rotationPointX += g;
            modelPart.applyTransform(f);
            modelPart.rotationPointX -= g;
        } else {
            modelPart.applyTransform(f);
        }
    }
}

