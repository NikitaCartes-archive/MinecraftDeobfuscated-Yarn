/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.AbsoluteHand;

@Environment(value=EnvType.CLIENT)
public class PlayerEntityModel<T extends LivingEntity>
extends BipedEntityModel<T> {
    public final Cuboid leftArmOverlay;
    public final Cuboid rightArmOverlay;
    public final Cuboid leftLegOverlay;
    public final Cuboid rightLegOverlay;
    public final Cuboid bodyOverlay;
    private final Cuboid cape;
    private final Cuboid ears;
    private final boolean thinArms;

    public PlayerEntityModel(float f, boolean bl) {
        super(f, 0.0f, 64, 64);
        this.thinArms = bl;
        this.ears = new Cuboid(this, 24, 0);
        this.ears.addBox(-3.0f, -6.0f, -1.0f, 6, 6, 1, f);
        this.cape = new Cuboid(this, 0, 0);
        this.cape.setTextureSize(64, 32);
        this.cape.addBox(-5.0f, 0.0f, -1.0f, 10, 16, 1, f);
        if (bl) {
            this.leftArm = new Cuboid(this, 32, 48);
            this.leftArm.addBox(-1.0f, -2.0f, -2.0f, 3, 12, 4, f);
            this.leftArm.setRotationPoint(5.0f, 2.5f, 0.0f);
            this.rightArm = new Cuboid(this, 40, 16);
            this.rightArm.addBox(-2.0f, -2.0f, -2.0f, 3, 12, 4, f);
            this.rightArm.setRotationPoint(-5.0f, 2.5f, 0.0f);
            this.leftArmOverlay = new Cuboid(this, 48, 48);
            this.leftArmOverlay.addBox(-1.0f, -2.0f, -2.0f, 3, 12, 4, f + 0.25f);
            this.leftArmOverlay.setRotationPoint(5.0f, 2.5f, 0.0f);
            this.rightArmOverlay = new Cuboid(this, 40, 32);
            this.rightArmOverlay.addBox(-2.0f, -2.0f, -2.0f, 3, 12, 4, f + 0.25f);
            this.rightArmOverlay.setRotationPoint(-5.0f, 2.5f, 10.0f);
        } else {
            this.leftArm = new Cuboid(this, 32, 48);
            this.leftArm.addBox(-1.0f, -2.0f, -2.0f, 4, 12, 4, f);
            this.leftArm.setRotationPoint(5.0f, 2.0f, 0.0f);
            this.leftArmOverlay = new Cuboid(this, 48, 48);
            this.leftArmOverlay.addBox(-1.0f, -2.0f, -2.0f, 4, 12, 4, f + 0.25f);
            this.leftArmOverlay.setRotationPoint(5.0f, 2.0f, 0.0f);
            this.rightArmOverlay = new Cuboid(this, 40, 32);
            this.rightArmOverlay.addBox(-3.0f, -2.0f, -2.0f, 4, 12, 4, f + 0.25f);
            this.rightArmOverlay.setRotationPoint(-5.0f, 2.0f, 10.0f);
        }
        this.leftLeg = new Cuboid(this, 16, 48);
        this.leftLeg.addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, f);
        this.leftLeg.setRotationPoint(1.9f, 12.0f, 0.0f);
        this.leftLegOverlay = new Cuboid(this, 0, 48);
        this.leftLegOverlay.addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, f + 0.25f);
        this.leftLegOverlay.setRotationPoint(1.9f, 12.0f, 0.0f);
        this.rightLegOverlay = new Cuboid(this, 0, 32);
        this.rightLegOverlay.addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, f + 0.25f);
        this.rightLegOverlay.setRotationPoint(-1.9f, 12.0f, 0.0f);
        this.bodyOverlay = new Cuboid(this, 16, 32);
        this.bodyOverlay.addBox(-4.0f, 0.0f, -2.0f, 8, 12, 4, f + 0.25f);
        this.bodyOverlay.setRotationPoint(0.0f, 0.0f, 0.0f);
    }

    @Override
    public void method_17088(T livingEntity, float f, float g, float h, float i, float j, float k) {
        super.method_17088(livingEntity, f, g, h, i, j, k);
        GlStateManager.pushMatrix();
        if (this.isChild) {
            float l = 2.0f;
            GlStateManager.scalef(0.5f, 0.5f, 0.5f);
            GlStateManager.translatef(0.0f, 24.0f * k, 0.0f);
            this.leftLegOverlay.render(k);
            this.rightLegOverlay.render(k);
            this.leftArmOverlay.render(k);
            this.rightArmOverlay.render(k);
            this.bodyOverlay.render(k);
        } else {
            if (((Entity)livingEntity).isInSneakingPose()) {
                GlStateManager.translatef(0.0f, 0.2f, 0.0f);
            }
            this.leftLegOverlay.render(k);
            this.rightLegOverlay.render(k);
            this.leftArmOverlay.render(k);
            this.rightArmOverlay.render(k);
            this.bodyOverlay.render(k);
        }
        GlStateManager.popMatrix();
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
    public void setArmAngle(float f, AbsoluteHand absoluteHand) {
        Cuboid cuboid = this.getArm(absoluteHand);
        if (this.thinArms) {
            float g = 0.5f * (float)(absoluteHand == AbsoluteHand.RIGHT ? 1 : -1);
            cuboid.rotationPointX += g;
            cuboid.applyTransform(f);
            cuboid.rotationPointX -= g;
        } else {
            cuboid.applyTransform(f);
        }
    }
}

