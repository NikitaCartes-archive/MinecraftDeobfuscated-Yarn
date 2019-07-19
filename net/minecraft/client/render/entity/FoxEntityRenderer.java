/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.FoxHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.FoxEntityModel;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class FoxEntityRenderer
extends MobEntityRenderer<FoxEntity, FoxEntityModel<FoxEntity>> {
    private static final Identifier SKIN = new Identifier("textures/entity/fox/fox.png");
    private static final Identifier SLEEPING_SKIN = new Identifier("textures/entity/fox/fox_sleep.png");
    private static final Identifier SNOW_SKIN = new Identifier("textures/entity/fox/snow_fox.png");
    private static final Identifier SLEEPING_SNOW_SKIN = new Identifier("textures/entity/fox/snow_fox_sleep.png");

    public FoxEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new FoxEntityModel(), 0.4f);
        this.addFeature(new FoxHeldItemFeatureRenderer(this));
    }

    @Override
    protected void setupTransforms(FoxEntity foxEntity, float f, float g, float h) {
        super.setupTransforms(foxEntity, f, g, h);
        if (foxEntity.isChasing() || foxEntity.isWalking()) {
            GlStateManager.rotatef(-MathHelper.lerp(h, foxEntity.prevPitch, foxEntity.pitch), 1.0f, 0.0f, 0.0f);
        }
    }

    @Override
    @Nullable
    protected Identifier getTexture(FoxEntity foxEntity) {
        if (foxEntity.getFoxType() == FoxEntity.Type.RED) {
            return foxEntity.isSleeping() ? SLEEPING_SKIN : SKIN;
        }
        return foxEntity.isSleeping() ? SLEEPING_SNOW_SKIN : SNOW_SKIN;
    }
}

