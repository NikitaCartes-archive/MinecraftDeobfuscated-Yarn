/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.WolfCollarFeatureRenderer;
import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class WolfEntityRenderer
extends MobEntityRenderer<WolfEntity, WolfEntityModel<WolfEntity>> {
    private static final Identifier WILD_SKIN = new Identifier("textures/entity/wolf/wolf.png");
    private static final Identifier TAMED_SKIN = new Identifier("textures/entity/wolf/wolf_tame.png");
    private static final Identifier ANGRY_SKIN = new Identifier("textures/entity/wolf/wolf_angry.png");

    public WolfEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new WolfEntityModel(), 0.5f);
        this.addFeature(new WolfCollarFeatureRenderer(this));
    }

    @Override
    protected float getAnimationProgress(WolfEntity wolfEntity, float f) {
        return wolfEntity.method_6714();
    }

    @Override
    public void render(WolfEntity wolfEntity, double d, double e, double f, float g, float h) {
        if (wolfEntity.isFurWet()) {
            float i = wolfEntity.getBrightnessAtEyes() * wolfEntity.getFurWetBrightnessMultiplier(h);
            GlStateManager.color3f(i, i, i);
        }
        super.render(wolfEntity, d, e, f, g, h);
    }

    @Override
    protected Identifier getTexture(WolfEntity wolfEntity) {
        if (wolfEntity.isTamed()) {
            return TAMED_SKIN;
        }
        if (wolfEntity.isAngry()) {
            return ANGRY_SKIN;
        }
        return WILD_SKIN;
    }

    @Override
    protected /* synthetic */ float getAnimationProgress(LivingEntity livingEntity, float f) {
        return this.getAnimationProgress((WolfEntity)livingEntity, f);
    }
}

