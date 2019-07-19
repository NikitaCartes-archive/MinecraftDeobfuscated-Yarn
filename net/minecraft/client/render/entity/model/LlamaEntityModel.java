/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.QuadrupedEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.PassiveEntity;

@Environment(value=EnvType.CLIENT)
public class LlamaEntityModel<T extends AbstractDonkeyEntity>
extends QuadrupedEntityModel<T> {
    private final ModelPart field_3430;
    private final ModelPart field_3429;

    public LlamaEntityModel(float f) {
        super(15, f);
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.head = new ModelPart(this, 0, 0);
        this.head.addCuboid(-2.0f, -14.0f, -10.0f, 4, 4, 9, f);
        this.head.setPivot(0.0f, 7.0f, -6.0f);
        this.head.setTextureOffset(0, 14).addCuboid(-4.0f, -16.0f, -6.0f, 8, 18, 6, f);
        this.head.setTextureOffset(17, 0).addCuboid(-4.0f, -19.0f, -4.0f, 3, 3, 2, f);
        this.head.setTextureOffset(17, 0).addCuboid(1.0f, -19.0f, -4.0f, 3, 3, 2, f);
        this.torso = new ModelPart(this, 29, 0);
        this.torso.addCuboid(-6.0f, -10.0f, -7.0f, 12, 18, 10, f);
        this.torso.setPivot(0.0f, 5.0f, 2.0f);
        this.field_3430 = new ModelPart(this, 45, 28);
        this.field_3430.addCuboid(-3.0f, 0.0f, 0.0f, 8, 8, 3, f);
        this.field_3430.setPivot(-8.5f, 3.0f, 3.0f);
        this.field_3430.yaw = 1.5707964f;
        this.field_3429 = new ModelPart(this, 45, 41);
        this.field_3429.addCuboid(-3.0f, 0.0f, 0.0f, 8, 8, 3, f);
        this.field_3429.setPivot(5.5f, 3.0f, 3.0f);
        this.field_3429.yaw = 1.5707964f;
        int i = 4;
        int j = 14;
        this.backRightLeg = new ModelPart(this, 29, 29);
        this.backRightLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4, 14, 4, f);
        this.backRightLeg.setPivot(-2.5f, 10.0f, 6.0f);
        this.backLeftLeg = new ModelPart(this, 29, 29);
        this.backLeftLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4, 14, 4, f);
        this.backLeftLeg.setPivot(2.5f, 10.0f, 6.0f);
        this.frontRightLeg = new ModelPart(this, 29, 29);
        this.frontRightLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4, 14, 4, f);
        this.frontRightLeg.setPivot(-2.5f, 10.0f, -4.0f);
        this.frontLeftLeg = new ModelPart(this, 29, 29);
        this.frontLeftLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4, 14, 4, f);
        this.frontLeftLeg.setPivot(2.5f, 10.0f, -4.0f);
        this.backRightLeg.pivotX -= 1.0f;
        this.backLeftLeg.pivotX += 1.0f;
        this.backRightLeg.pivotZ += 0.0f;
        this.backLeftLeg.pivotZ += 0.0f;
        this.frontRightLeg.pivotX -= 1.0f;
        this.frontLeftLeg.pivotX += 1.0f;
        this.frontRightLeg.pivotZ -= 1.0f;
        this.frontLeftLeg.pivotZ -= 1.0f;
        this.field_3537 += 2.0f;
    }

    @Override
    public void render(T abstractDonkeyEntity, float f, float g, float h, float i, float j, float k) {
        boolean bl = !((PassiveEntity)abstractDonkeyEntity).isBaby() && ((AbstractDonkeyEntity)abstractDonkeyEntity).hasChest();
        this.setAngles(abstractDonkeyEntity, f, g, h, i, j, k);
        if (this.child) {
            float l = 2.0f;
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0.0f, this.field_3540 * k, this.field_3537 * k);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            float m = 0.7f;
            GlStateManager.scalef(0.71428573f, 0.64935064f, 0.7936508f);
            GlStateManager.translatef(0.0f, 21.0f * k, 0.22f);
            this.head.render(k);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            float n = 1.1f;
            GlStateManager.scalef(0.625f, 0.45454544f, 0.45454544f);
            GlStateManager.translatef(0.0f, 33.0f * k, 0.0f);
            this.torso.render(k);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.45454544f, 0.41322312f, 0.45454544f);
            GlStateManager.translatef(0.0f, 33.0f * k, 0.0f);
            this.backRightLeg.render(k);
            this.backLeftLeg.render(k);
            this.frontRightLeg.render(k);
            this.frontLeftLeg.render(k);
            GlStateManager.popMatrix();
        } else {
            this.head.render(k);
            this.torso.render(k);
            this.backRightLeg.render(k);
            this.backLeftLeg.render(k);
            this.frontRightLeg.render(k);
            this.frontLeftLeg.render(k);
        }
        if (bl) {
            this.field_3430.render(k);
            this.field_3429.render(k);
        }
    }

    @Override
    public /* synthetic */ void render(Entity entity, float f, float g, float h, float i, float j, float k) {
        this.render((T)((AbstractDonkeyEntity)entity), f, g, h, i, j, k);
    }
}

