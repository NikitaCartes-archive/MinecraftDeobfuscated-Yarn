/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.CodEntityModel;
import net.minecraft.entity.passive.CodEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class CodEntityRenderer
extends MobEntityRenderer<CodEntity, CodEntityModel<CodEntity>> {
    private static final Identifier SKIN = new Identifier("textures/entity/fish/cod.png");

    public CodEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new CodEntityModel(), 0.3f);
    }

    @Override
    @Nullable
    protected Identifier getTexture(CodEntity codEntity) {
        return SKIN;
    }

    @Override
    protected void setupTransforms(CodEntity codEntity, float f, float g, float h) {
        super.setupTransforms(codEntity, f, g, h);
        float i = 4.3f * MathHelper.sin(0.6f * f);
        GlStateManager.rotatef(i, 0.0f, 1.0f, 0.0f);
        if (!codEntity.isTouchingWater()) {
            GlStateManager.translatef(0.1f, 0.1f, -0.1f);
            GlStateManager.rotatef(90.0f, 0.0f, 0.0f, 1.0f);
        }
    }
}

