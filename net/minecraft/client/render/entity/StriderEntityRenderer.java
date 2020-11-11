/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.SaddleFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.StriderEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class StriderEntityRenderer
extends MobEntityRenderer<StriderEntity, StriderEntityModel<StriderEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/strider/strider.png");
    private static final Identifier COLD_TEXTURE = new Identifier("textures/entity/strider/strider_cold.png");

    public StriderEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new StriderEntityModel(context.getPart(EntityModelLayers.STRIDER)), 0.5f);
        this.addFeature(new SaddleFeatureRenderer(this, new StriderEntityModel(context.getPart(EntityModelLayers.STRIDER_SADDLE)), new Identifier("textures/entity/strider/strider_saddle.png")));
    }

    @Override
    public Identifier getTexture(StriderEntity striderEntity) {
        return striderEntity.isCold() ? COLD_TEXTURE : TEXTURE;
    }

    @Override
    protected void scale(StriderEntity striderEntity, MatrixStack matrixStack, float f) {
        if (striderEntity.isBaby()) {
            matrixStack.scale(0.5f, 0.5f, 0.5f);
            this.shadowRadius = 0.25f;
        } else {
            this.shadowRadius = 0.5f;
        }
    }

    @Override
    protected boolean isShaking(StriderEntity striderEntity) {
        return striderEntity.isCold();
    }

    @Override
    protected /* synthetic */ boolean isShaking(LivingEntity entity) {
        return this.isShaking((StriderEntity)entity);
    }
}

