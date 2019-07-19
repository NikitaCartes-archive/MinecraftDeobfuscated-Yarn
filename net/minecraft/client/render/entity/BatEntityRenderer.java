/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.BatEntityModel;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BatEntityRenderer
extends MobEntityRenderer<BatEntity, BatEntityModel> {
    private static final Identifier SKIN = new Identifier("textures/entity/bat.png");

    public BatEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new BatEntityModel(), 0.25f);
    }

    @Override
    protected Identifier getTexture(BatEntity batEntity) {
        return SKIN;
    }

    @Override
    protected void scale(BatEntity batEntity, float f) {
        GlStateManager.scalef(0.35f, 0.35f, 0.35f);
    }

    @Override
    protected void setupTransforms(BatEntity batEntity, float f, float g, float h) {
        if (batEntity.isRoosting()) {
            GlStateManager.translatef(0.0f, -0.1f, 0.0f);
        } else {
            GlStateManager.translatef(0.0f, MathHelper.cos(f * 0.3f) * 0.1f, 0.0f);
        }
        super.setupTransforms(batEntity, f, g, h);
    }
}

