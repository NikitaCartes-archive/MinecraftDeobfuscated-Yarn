/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.CreeperChargeFeatureRenderer;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class CreeperEntityRenderer
extends MobEntityRenderer<CreeperEntity, CreeperEntityModel<CreeperEntity>> {
    private static final Identifier SKIN = new Identifier("textures/entity/creeper/creeper.png");

    public CreeperEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new CreeperEntityModel(), 0.5f);
        this.addFeature(new CreeperChargeFeatureRenderer(this));
    }

    protected void method_3900(CreeperEntity creeperEntity, float f) {
        float g = creeperEntity.getClientFuseTime(f);
        float h = 1.0f + MathHelper.sin(g * 100.0f) * g * 0.01f;
        g = MathHelper.clamp(g, 0.0f, 1.0f);
        g *= g;
        g *= g;
        float i = (1.0f + g * 0.4f) * h;
        float j = (1.0f + g * 0.1f) / h;
        GlStateManager.scalef(i, j, i);
    }

    protected int method_3898(CreeperEntity creeperEntity, float f, float g) {
        float h = creeperEntity.getClientFuseTime(g);
        if ((int)(h * 10.0f) % 2 == 0) {
            return 0;
        }
        int i = (int)(h * 0.2f * 255.0f);
        i = MathHelper.clamp(i, 0, 255);
        return i << 24 | 0x30FFFFFF;
    }

    protected Identifier method_3899(CreeperEntity creeperEntity) {
        return SKIN;
    }
}

