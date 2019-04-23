/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.model.ShulkerBulletEntityModel;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ShulkerBulletEntityRenderer
extends EntityRenderer<ShulkerBulletEntity> {
    private static final Identifier SKIN = new Identifier("textures/entity/shulker/spark.png");
    private final ShulkerBulletEntityModel<ShulkerBulletEntity> model = new ShulkerBulletEntityModel();

    public ShulkerBulletEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    private float method_4104(float f, float g, float h) {
        float i;
        for (i = g - f; i < -180.0f; i += 360.0f) {
        }
        while (i >= 180.0f) {
            i -= 360.0f;
        }
        return f + h * i;
    }

    public void method_4103(ShulkerBulletEntity shulkerBulletEntity, double d, double e, double f, float g, float h) {
        GlStateManager.pushMatrix();
        float i = this.method_4104(shulkerBulletEntity.prevYaw, shulkerBulletEntity.yaw, h);
        float j = MathHelper.lerp(h, shulkerBulletEntity.prevPitch, shulkerBulletEntity.pitch);
        float k = (float)shulkerBulletEntity.age + h;
        GlStateManager.translatef((float)d, (float)e + 0.15f, (float)f);
        GlStateManager.rotatef(MathHelper.sin(k * 0.1f) * 180.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(MathHelper.cos(k * 0.1f) * 180.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotatef(MathHelper.sin(k * 0.15f) * 360.0f, 0.0f, 0.0f, 1.0f);
        float l = 0.03125f;
        GlStateManager.enableRescaleNormal();
        GlStateManager.scalef(-1.0f, -1.0f, 1.0f);
        this.bindEntityTexture(shulkerBulletEntity);
        this.model.render(shulkerBulletEntity, 0.0f, 0.0f, 0.0f, i, j, 0.03125f);
        GlStateManager.enableBlend();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 0.5f);
        GlStateManager.scalef(1.5f, 1.5f, 1.5f);
        this.model.render(shulkerBulletEntity, 0.0f, 0.0f, 0.0f, i, j, 0.03125f);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        super.render(shulkerBulletEntity, d, e, f, g, h);
    }

    protected Identifier method_4105(ShulkerBulletEntity shulkerBulletEntity) {
        return SKIN;
    }
}

