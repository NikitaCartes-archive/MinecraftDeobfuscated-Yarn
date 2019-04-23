/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class WolfEntityModel<T extends WolfEntity>
extends EntityModel<T> {
    private final Cuboid head;
    private final Cuboid field_3623;
    private final Cuboid field_3622;
    private final Cuboid field_3620;
    private final Cuboid field_3618;
    private final Cuboid field_3624;
    private final Cuboid field_3617;
    private final Cuboid field_3619;

    public WolfEntityModel() {
        float f = 0.0f;
        float g = 13.5f;
        this.head = new Cuboid(this, 0, 0);
        this.head.addBox(-2.0f, -3.0f, -2.0f, 6, 6, 4, 0.0f);
        this.head.setRotationPoint(-1.0f, 13.5f, -7.0f);
        this.field_3623 = new Cuboid(this, 18, 14);
        this.field_3623.addBox(-3.0f, -2.0f, -3.0f, 6, 9, 6, 0.0f);
        this.field_3623.setRotationPoint(0.0f, 14.0f, 2.0f);
        this.field_3619 = new Cuboid(this, 21, 0);
        this.field_3619.addBox(-3.0f, -3.0f, -3.0f, 8, 6, 7, 0.0f);
        this.field_3619.setRotationPoint(-1.0f, 14.0f, 2.0f);
        this.field_3622 = new Cuboid(this, 0, 18);
        this.field_3622.addBox(0.0f, 0.0f, -1.0f, 2, 8, 2, 0.0f);
        this.field_3622.setRotationPoint(-2.5f, 16.0f, 7.0f);
        this.field_3620 = new Cuboid(this, 0, 18);
        this.field_3620.addBox(0.0f, 0.0f, -1.0f, 2, 8, 2, 0.0f);
        this.field_3620.setRotationPoint(0.5f, 16.0f, 7.0f);
        this.field_3618 = new Cuboid(this, 0, 18);
        this.field_3618.addBox(0.0f, 0.0f, -1.0f, 2, 8, 2, 0.0f);
        this.field_3618.setRotationPoint(-2.5f, 16.0f, -4.0f);
        this.field_3624 = new Cuboid(this, 0, 18);
        this.field_3624.addBox(0.0f, 0.0f, -1.0f, 2, 8, 2, 0.0f);
        this.field_3624.setRotationPoint(0.5f, 16.0f, -4.0f);
        this.field_3617 = new Cuboid(this, 9, 18);
        this.field_3617.addBox(0.0f, 0.0f, -1.0f, 2, 8, 2, 0.0f);
        this.field_3617.setRotationPoint(-1.0f, 12.0f, 8.0f);
        this.head.setTextureOffset(16, 14).addBox(-2.0f, -5.0f, 0.0f, 2, 2, 1, 0.0f);
        this.head.setTextureOffset(16, 14).addBox(2.0f, -5.0f, 0.0f, 2, 2, 1, 0.0f);
        this.head.setTextureOffset(0, 10).addBox(-0.5f, 0.0f, -5.0f, 3, 3, 4, 0.0f);
    }

    public void method_17132(T wolfEntity, float f, float g, float h, float i, float j, float k) {
        super.render(wolfEntity, f, g, h, i, j, k);
        this.method_17133(wolfEntity, f, g, h, i, j, k);
        if (this.isChild) {
            float l = 2.0f;
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0.0f, 5.0f * k, 2.0f * k);
            this.head.method_2852(k);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.5f, 0.5f, 0.5f);
            GlStateManager.translatef(0.0f, 24.0f * k, 0.0f);
            this.field_3623.render(k);
            this.field_3622.render(k);
            this.field_3620.render(k);
            this.field_3618.render(k);
            this.field_3624.render(k);
            this.field_3617.method_2852(k);
            this.field_3619.render(k);
            GlStateManager.popMatrix();
        } else {
            this.head.method_2852(k);
            this.field_3623.render(k);
            this.field_3622.render(k);
            this.field_3620.render(k);
            this.field_3618.render(k);
            this.field_3624.render(k);
            this.field_3617.method_2852(k);
            this.field_3619.render(k);
        }
    }

    public void method_17131(T wolfEntity, float f, float g, float h) {
        this.field_3617.yaw = ((WolfEntity)wolfEntity).isAngry() ? 0.0f : MathHelper.cos(f * 0.6662f) * 1.4f * g;
        if (((TameableEntity)wolfEntity).isSitting()) {
            this.field_3619.setRotationPoint(-1.0f, 16.0f, -3.0f);
            this.field_3619.pitch = 1.2566371f;
            this.field_3619.yaw = 0.0f;
            this.field_3623.setRotationPoint(0.0f, 18.0f, 0.0f);
            this.field_3623.pitch = 0.7853982f;
            this.field_3617.setRotationPoint(-1.0f, 21.0f, 6.0f);
            this.field_3622.setRotationPoint(-2.5f, 22.0f, 2.0f);
            this.field_3622.pitch = 4.712389f;
            this.field_3620.setRotationPoint(0.5f, 22.0f, 2.0f);
            this.field_3620.pitch = 4.712389f;
            this.field_3618.pitch = 5.811947f;
            this.field_3618.setRotationPoint(-2.49f, 17.0f, -4.0f);
            this.field_3624.pitch = 5.811947f;
            this.field_3624.setRotationPoint(0.51f, 17.0f, -4.0f);
        } else {
            this.field_3623.setRotationPoint(0.0f, 14.0f, 2.0f);
            this.field_3623.pitch = 1.5707964f;
            this.field_3619.setRotationPoint(-1.0f, 14.0f, -3.0f);
            this.field_3619.pitch = this.field_3623.pitch;
            this.field_3617.setRotationPoint(-1.0f, 12.0f, 8.0f);
            this.field_3622.setRotationPoint(-2.5f, 16.0f, 7.0f);
            this.field_3620.setRotationPoint(0.5f, 16.0f, 7.0f);
            this.field_3618.setRotationPoint(-2.5f, 16.0f, -4.0f);
            this.field_3624.setRotationPoint(0.5f, 16.0f, -4.0f);
            this.field_3622.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g;
            this.field_3620.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
            this.field_3618.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
            this.field_3624.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g;
        }
        this.head.roll = ((WolfEntity)wolfEntity).getBegAnimationProgress(h) + ((WolfEntity)wolfEntity).getShakeAnimationProgress(h, 0.0f);
        this.field_3619.roll = ((WolfEntity)wolfEntity).getShakeAnimationProgress(h, -0.08f);
        this.field_3623.roll = ((WolfEntity)wolfEntity).getShakeAnimationProgress(h, -0.16f);
        this.field_3617.roll = ((WolfEntity)wolfEntity).getShakeAnimationProgress(h, -0.2f);
    }

    public void method_17133(T wolfEntity, float f, float g, float h, float i, float j, float k) {
        super.setAngles(wolfEntity, f, g, h, i, j, k);
        this.head.pitch = j * ((float)Math.PI / 180);
        this.head.yaw = i * ((float)Math.PI / 180);
        this.field_3617.pitch = h;
    }

    @Override
    public /* synthetic */ void setAngles(Entity entity, float f, float g, float h, float i, float j, float k) {
        this.method_17133((WolfEntity)entity, f, g, h, i, j, k);
    }

    @Override
    public /* synthetic */ void render(Entity entity, float f, float g, float h, float i, float j, float k) {
        this.method_17132((WolfEntity)entity, f, g, h, i, j, k);
    }
}

