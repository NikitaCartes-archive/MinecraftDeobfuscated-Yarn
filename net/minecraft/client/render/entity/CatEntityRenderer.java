/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.CatCollarFeatureRenderer;
import net.minecraft.client.render.entity.model.CatEntityModel;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class CatEntityRenderer
extends MobEntityRenderer<CatEntity, CatEntityModel<CatEntity>> {
    public CatEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new CatEntityModel(0.0f), 0.4f);
        this.addFeature(new CatCollarFeatureRenderer(this));
    }

    @Override
    @Nullable
    protected Identifier getTexture(CatEntity catEntity) {
        return catEntity.getTexture();
    }

    @Override
    protected void scale(CatEntity catEntity, float f) {
        super.scale(catEntity, f);
        GlStateManager.scalef(0.8f, 0.8f, 0.8f);
    }

    @Override
    protected void setupTransforms(CatEntity catEntity, float f, float g, float h) {
        super.setupTransforms(catEntity, f, g, h);
        float i = catEntity.getSleepAnimation(h);
        if (i > 0.0f) {
            GlStateManager.translatef(0.4f * i, 0.15f * i, 0.1f * i);
            GlStateManager.rotatef(MathHelper.lerpAngleDegrees(i, 0.0f, 90.0f), 0.0f, 0.0f, 1.0f);
            BlockPos blockPos = new BlockPos(catEntity);
            List<PlayerEntity> list = catEntity.world.getNonSpectatingEntities(PlayerEntity.class, new Box(blockPos).expand(2.0, 2.0, 2.0));
            for (PlayerEntity playerEntity : list) {
                if (!playerEntity.isSleeping()) continue;
                GlStateManager.translatef(0.15f * i, 0.0f, 0.0f);
                break;
            }
        }
    }
}

