/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
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
    public final ModelPart leftSleeve;
    public final ModelPart rightSleeve;
    public final ModelPart leftPantLeg;
    public final ModelPart rightPantLeg;
    public final ModelPart jacket;
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
            this.leftArm.setPivot(5.0f, 2.5f, 0.0f);
            this.rightArm = new ModelPart(this, 40, 16);
            this.rightArm.addCuboid(-2.0f, -2.0f, -2.0f, 3, 12, 4, f);
            this.rightArm.setPivot(-5.0f, 2.5f, 0.0f);
            this.leftSleeve = new ModelPart(this, 48, 48);
            this.leftSleeve.addCuboid(-1.0f, -2.0f, -2.0f, 3, 12, 4, f + 0.25f);
            this.leftSleeve.setPivot(5.0f, 2.5f, 0.0f);
            this.rightSleeve = new ModelPart(this, 40, 32);
            this.rightSleeve.addCuboid(-2.0f, -2.0f, -2.0f, 3, 12, 4, f + 0.25f);
            this.rightSleeve.setPivot(-5.0f, 2.5f, 10.0f);
        } else {
            this.leftArm = new ModelPart(this, 32, 48);
            this.leftArm.addCuboid(-1.0f, -2.0f, -2.0f, 4, 12, 4, f);
            this.leftArm.setPivot(5.0f, 2.0f, 0.0f);
            this.leftSleeve = new ModelPart(this, 48, 48);
            this.leftSleeve.addCuboid(-1.0f, -2.0f, -2.0f, 4, 12, 4, f + 0.25f);
            this.leftSleeve.setPivot(5.0f, 2.0f, 0.0f);
            this.rightSleeve = new ModelPart(this, 40, 32);
            this.rightSleeve.addCuboid(-3.0f, -2.0f, -2.0f, 4, 12, 4, f + 0.25f);
            this.rightSleeve.setPivot(-5.0f, 2.0f, 10.0f);
        }
        this.leftLeg = new ModelPart(this, 16, 48);
        this.leftLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4, 12, 4, f);
        this.leftLeg.setPivot(1.9f, 12.0f, 0.0f);
        this.leftPantLeg = new ModelPart(this, 0, 48);
        this.leftPantLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4, 12, 4, f + 0.25f);
        this.leftPantLeg.setPivot(1.9f, 12.0f, 0.0f);
        this.rightPantLeg = new ModelPart(this, 0, 32);
        this.rightPantLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4, 12, 4, f + 0.25f);
        this.rightPantLeg.setPivot(-1.9f, 12.0f, 0.0f);
        this.jacket = new ModelPart(this, 16, 32);
        this.jacket.addCuboid(-4.0f, 0.0f, -2.0f, 8, 12, 4, f + 0.25f);
        this.jacket.setPivot(0.0f, 0.0f, 0.0f);
    }

    @Override
    public void render(T livingEntity, float f, float g, float h, float i, float j, float k) {
        super.render(livingEntity, f, g, h, i, j, k);
        GlStateManager.pushMatrix();
        if (this.child) {
            float l = 2.0f;
            GlStateManager.scalef(0.5f, 0.5f, 0.5f);
            GlStateManager.translatef(0.0f, 24.0f * k, 0.0f);
            this.leftPantLeg.render(k);
            this.rightPantLeg.render(k);
            this.leftSleeve.render(k);
            this.rightSleeve.render(k);
            this.jacket.render(k);
        } else {
            if (((Entity)livingEntity).isInSneakingPose()) {
                GlStateManager.translatef(0.0f, 0.2f, 0.0f);
            }
            this.leftPantLeg.render(k);
            this.rightPantLeg.render(k);
            this.leftSleeve.render(k);
            this.rightSleeve.render(k);
            this.jacket.render(k);
        }
        GlStateManager.popMatrix();
    }

    public void renderEars(float f) {
        this.ears.copyPositionAndRotation(this.head);
        this.ears.pivotX = 0.0f;
        this.ears.pivotY = 0.0f;
        this.ears.render(f);
    }

    public void renderCape(float f) {
        this.cape.render(f);
    }

    @Override
    public void setAngles(T livingEntity, float f, float g, float h, float i, float j, float k) {
        super.setAngles(livingEntity, f, g, h, i, j, k);
        this.leftPantLeg.copyPositionAndRotation(this.leftLeg);
        this.rightPantLeg.copyPositionAndRotation(this.rightLeg);
        this.leftSleeve.copyPositionAndRotation(this.leftArm);
        this.rightSleeve.copyPositionAndRotation(this.rightArm);
        this.jacket.copyPositionAndRotation(this.torso);
        this.cape.pivotY = ((Entity)livingEntity).isInSneakingPose() ? 2.0f : 0.0f;
    }

    @Override
    public void setVisible(boolean bl) {
        super.setVisible(bl);
        this.leftSleeve.visible = bl;
        this.rightSleeve.visible = bl;
        this.leftPantLeg.visible = bl;
        this.rightPantLeg.visible = bl;
        this.jacket.visible = bl;
        this.cape.visible = bl;
        this.ears.visible = bl;
    }

    @Override
    public void setArmAngle(float f, Arm arm) {
        ModelPart modelPart = this.getArm(arm);
        if (this.thinArms) {
            float g = 0.5f * (float)(arm == Arm.RIGHT ? 1 : -1);
            modelPart.pivotX += g;
            modelPart.applyTransform(f);
            modelPart.pivotX -= g;
        } else {
            modelPart.applyTransform(f);
        }
    }
}

