/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.IronGolemFlowerFeatureRenderer;
import net.minecraft.client.render.entity.model.IronGolemEntityModel;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class IronGolemEntityRenderer
extends MobEntityRenderer<IronGolemEntity, IronGolemEntityModel<IronGolemEntity>> {
    private static final Identifier SKIN = new Identifier("textures/entity/iron_golem.png");

    public IronGolemEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new IronGolemEntityModel(), 0.7f);
        this.addFeature(new IronGolemFlowerFeatureRenderer(this));
    }

    protected Identifier method_3987(IronGolemEntity ironGolemEntity) {
        return SKIN;
    }

    protected void method_3986(IronGolemEntity ironGolemEntity, float f, float g, float h) {
        super.setupTransforms(ironGolemEntity, f, g, h);
        if ((double)ironGolemEntity.limbDistance < 0.01) {
            return;
        }
        float i = 13.0f;
        float j = ironGolemEntity.limbAngle - ironGolemEntity.limbDistance * (1.0f - h) + 6.0f;
        float k = (Math.abs(j % 13.0f - 6.5f) - 3.25f) / 3.25f;
        GlStateManager.rotatef(6.5f * k, 0.0f, 0.0f, 1.0f);
    }
}

