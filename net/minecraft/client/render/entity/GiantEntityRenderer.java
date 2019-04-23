/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorBipedFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.GiantEntityModel;
import net.minecraft.entity.mob.GiantEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class GiantEntityRenderer
extends MobEntityRenderer<GiantEntity, BipedEntityModel<GiantEntity>> {
    private static final Identifier SKIN = new Identifier("textures/entity/zombie/zombie.png");
    private final float scale;

    public GiantEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, float f) {
        super(entityRenderDispatcher, new GiantEntityModel(), 0.5f * f);
        this.scale = f;
        this.addFeature(new HeldItemFeatureRenderer<GiantEntity, BipedEntityModel<GiantEntity>>(this));
        this.addFeature(new ArmorBipedFeatureRenderer<GiantEntity, BipedEntityModel<GiantEntity>, GiantEntityModel>(this, new GiantEntityModel(0.5f, true), new GiantEntityModel(1.0f, true)));
    }

    protected void method_3980(GiantEntity giantEntity, float f) {
        GlStateManager.scalef(this.scale, this.scale, this.scale);
    }

    protected Identifier method_3981(GiantEntity giantEntity) {
        return SKIN;
    }
}

