/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5617;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class WanderingTraderEntityRenderer
extends MobEntityRenderer<WanderingTraderEntity, VillagerResemblingModel<WanderingTraderEntity>> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/wandering_trader.png");

    public WanderingTraderEntityRenderer(class_5617.class_5618 arg) {
        super(arg, new VillagerResemblingModel(arg.method_32167(EntityModelLayers.WANDERING_TRADER)), 0.5f);
        this.addFeature(new HeadFeatureRenderer<WanderingTraderEntity, VillagerResemblingModel<WanderingTraderEntity>>(this, arg.method_32170()));
        this.addFeature(new VillagerHeldItemFeatureRenderer<WanderingTraderEntity, VillagerResemblingModel<WanderingTraderEntity>>(this));
    }

    @Override
    public Identifier getTexture(WanderingTraderEntity wanderingTraderEntity) {
        return TEXTURE;
    }

    @Override
    protected void scale(WanderingTraderEntity wanderingTraderEntity, MatrixStack matrixStack, float f) {
        float g = 0.9375f;
        matrixStack.scale(0.9375f, 0.9375f, 0.9375f);
    }
}

