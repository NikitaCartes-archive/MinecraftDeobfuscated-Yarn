/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.DolphinHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.DolphinEntityModel;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class DolphinEntityRenderer
extends MobEntityRenderer<DolphinEntity, DolphinEntityModel<DolphinEntity>> {
    private static final Identifier SKIN = new Identifier("textures/entity/dolphin.png");

    public DolphinEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new DolphinEntityModel(), 0.7f);
        this.addFeature(new DolphinHeldItemFeatureRenderer(this));
    }

    protected Identifier method_3903(DolphinEntity dolphinEntity) {
        return SKIN;
    }

    protected void method_3901(DolphinEntity dolphinEntity, float f) {
        float g = 1.0f;
        GlStateManager.scalef(1.0f, 1.0f, 1.0f);
    }

    protected void method_3902(DolphinEntity dolphinEntity, float f, float g, float h) {
        super.setupTransforms(dolphinEntity, f, g, h);
    }
}

