/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.PhantomEyesFeatureRenderer;
import net.minecraft.client.render.entity.model.PhantomEntityModel;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class PhantomEntityRenderer
extends MobEntityRenderer<PhantomEntity, PhantomEntityModel<PhantomEntity>> {
    private static final Identifier SKIN = new Identifier("textures/entity/phantom.png");

    public PhantomEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new PhantomEntityModel(), 0.75f);
        this.addFeature(new PhantomEyesFeatureRenderer<PhantomEntity>(this));
    }

    protected Identifier method_4090(PhantomEntity phantomEntity) {
        return SKIN;
    }

    protected void method_4088(PhantomEntity phantomEntity, float f) {
        int i = phantomEntity.getPhantomSize();
        float g = 1.0f + 0.15f * (float)i;
        RenderSystem.scalef(g, g, g);
        RenderSystem.translatef(0.0f, 1.3125f, 0.1875f);
    }

    protected void method_4089(PhantomEntity phantomEntity, float f, float g, float h) {
        super.setupTransforms(phantomEntity, f, g, h);
        RenderSystem.rotatef(phantomEntity.pitch, 1.0f, 0.0f, 0.0f);
    }
}

