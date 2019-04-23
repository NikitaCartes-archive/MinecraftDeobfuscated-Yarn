/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.render.entity.model.QuadrupedEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.PassiveEntity;

@Environment(value=EnvType.CLIENT)
public class LlamaEntityModel<T extends AbstractDonkeyEntity>
extends QuadrupedEntityModel<T> {
    private final Cuboid field_3430;
    private final Cuboid field_3429;

    public LlamaEntityModel(float f) {
        super(15, f);
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.head = new Cuboid(this, 0, 0);
        this.head.addBox(-2.0f, -14.0f, -10.0f, 4, 4, 9, f);
        this.head.setRotationPoint(0.0f, 7.0f, -6.0f);
        this.head.setTextureOffset(0, 14).addBox(-4.0f, -16.0f, -6.0f, 8, 18, 6, f);
        this.head.setTextureOffset(17, 0).addBox(-4.0f, -19.0f, -4.0f, 3, 3, 2, f);
        this.head.setTextureOffset(17, 0).addBox(1.0f, -19.0f, -4.0f, 3, 3, 2, f);
        this.body = new Cuboid(this, 29, 0);
        this.body.addBox(-6.0f, -10.0f, -7.0f, 12, 18, 10, f);
        this.body.setRotationPoint(0.0f, 5.0f, 2.0f);
        this.field_3430 = new Cuboid(this, 45, 28);
        this.field_3430.addBox(-3.0f, 0.0f, 0.0f, 8, 8, 3, f);
        this.field_3430.setRotationPoint(-8.5f, 3.0f, 3.0f);
        this.field_3430.yaw = 1.5707964f;
        this.field_3429 = new Cuboid(this, 45, 41);
        this.field_3429.addBox(-3.0f, 0.0f, 0.0f, 8, 8, 3, f);
        this.field_3429.setRotationPoint(5.5f, 3.0f, 3.0f);
        this.field_3429.yaw = 1.5707964f;
        int i = 4;
        int j = 14;
        this.leg1 = new Cuboid(this, 29, 29);
        this.leg1.addBox(-2.0f, 0.0f, -2.0f, 4, 14, 4, f);
        this.leg1.setRotationPoint(-2.5f, 10.0f, 6.0f);
        this.leg2 = new Cuboid(this, 29, 29);
        this.leg2.addBox(-2.0f, 0.0f, -2.0f, 4, 14, 4, f);
        this.leg2.setRotationPoint(2.5f, 10.0f, 6.0f);
        this.leg3 = new Cuboid(this, 29, 29);
        this.leg3.addBox(-2.0f, 0.0f, -2.0f, 4, 14, 4, f);
        this.leg3.setRotationPoint(-2.5f, 10.0f, -4.0f);
        this.leg4 = new Cuboid(this, 29, 29);
        this.leg4.addBox(-2.0f, 0.0f, -2.0f, 4, 14, 4, f);
        this.leg4.setRotationPoint(2.5f, 10.0f, -4.0f);
        this.leg1.rotationPointX -= 1.0f;
        this.leg2.rotationPointX += 1.0f;
        this.leg1.rotationPointZ += 0.0f;
        this.leg2.rotationPointZ += 0.0f;
        this.leg3.rotationPointX -= 1.0f;
        this.leg4.rotationPointX += 1.0f;
        this.leg3.rotationPointZ -= 1.0f;
        this.leg4.rotationPointZ -= 1.0f;
        this.field_3537 += 2.0f;
    }

    public void method_17100(T abstractDonkeyEntity, float f, float g, float h, float i, float j, float k) {
        boolean bl = !((PassiveEntity)abstractDonkeyEntity).isBaby() && ((AbstractDonkeyEntity)abstractDonkeyEntity).hasChest();
        this.setAngles(abstractDonkeyEntity, f, g, h, i, j, k);
        if (this.isChild) {
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
            this.body.render(k);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.45454544f, 0.41322312f, 0.45454544f);
            GlStateManager.translatef(0.0f, 33.0f * k, 0.0f);
            this.leg1.render(k);
            this.leg2.render(k);
            this.leg3.render(k);
            this.leg4.render(k);
            GlStateManager.popMatrix();
        } else {
            this.head.render(k);
            this.body.render(k);
            this.leg1.render(k);
            this.leg2.render(k);
            this.leg3.render(k);
            this.leg4.render(k);
        }
        if (bl) {
            this.field_3430.render(k);
            this.field_3429.render(k);
        }
    }

    @Override
    public /* synthetic */ void render(Entity entity, float f, float g, float h, float i, float j, float k) {
        this.method_17100((AbstractDonkeyEntity)entity, f, g, h, i, j, k);
    }
}

