/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.model.SkullEntityModel;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class WitherSkullEntityRenderer
extends EntityRenderer<WitherSkullEntity> {
    private static final Identifier INVINCIBLE_SKIN = new Identifier("textures/entity/wither/wither_invulnerable.png");
    private static final Identifier SKIN = new Identifier("textures/entity/wither/wither.png");
    private final SkullEntityModel model = new SkullEntityModel();

    public WitherSkullEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    private float method_4158(float f, float g, float h) {
        float i;
        for (i = g - f; i < -180.0f; i += 360.0f) {
        }
        while (i >= 180.0f) {
            i -= 360.0f;
        }
        return f + h * i;
    }

    public void method_4159(WitherSkullEntity witherSkullEntity, double d, double e, double f, float g, float h) {
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        float i = this.method_4158(witherSkullEntity.prevYaw, witherSkullEntity.yaw, h);
        float j = MathHelper.lerp(h, witherSkullEntity.prevPitch, witherSkullEntity.pitch);
        GlStateManager.translatef((float)d, (float)e, (float)f);
        float k = 0.0625f;
        GlStateManager.enableRescaleNormal();
        GlStateManager.scalef(-1.0f, -1.0f, 1.0f);
        GlStateManager.enableAlphaTest();
        this.bindEntityTexture(witherSkullEntity);
        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getOutlineColor(witherSkullEntity));
        }
        this.model.setRotationAngles(0.0f, 0.0f, 0.0f, i, j, 0.0625f);
        if (this.renderOutlines) {
            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.popMatrix();
        super.render(witherSkullEntity, d, e, f, g, h);
    }

    protected Identifier method_4160(WitherSkullEntity witherSkullEntity) {
        return witherSkullEntity.isCharged() ? INVINCIBLE_SKIN : SKIN;
    }
}

