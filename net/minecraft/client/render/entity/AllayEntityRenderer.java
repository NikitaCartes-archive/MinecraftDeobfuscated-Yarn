/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.AllayEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public class AllayEntityRenderer
extends MobEntityRenderer<AllayEntity, AllayEntityModel> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/allay/allay.png");

    public AllayEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new AllayEntityModel(context.getPart(EntityModelLayers.ALLAY)), 0.4f);
        this.addFeature(new HeldItemFeatureRenderer<AllayEntity, AllayEntityModel>(this, context.getHeldItemRenderer()));
    }

    @Override
    public Identifier getTexture(AllayEntity allayEntity) {
        return TEXTURE;
    }

    @Override
    protected int getBlockLight(AllayEntity allayEntity, BlockPos blockPos) {
        return 15;
    }
}

