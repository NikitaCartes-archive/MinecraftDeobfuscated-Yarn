/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5599;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.item.DyeableHorseArmorItem;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.ItemStack;

@Environment(value=EnvType.CLIENT)
public class HorseArmorFeatureRenderer
extends FeatureRenderer<HorseEntity, HorseEntityModel<HorseEntity>> {
    private final HorseEntityModel<HorseEntity> model;

    public HorseArmorFeatureRenderer(FeatureRendererContext<HorseEntity, HorseEntityModel<HorseEntity>> featureRendererContext, class_5599 arg) {
        super(featureRendererContext);
        this.model = new HorseEntityModel(arg.method_32072(EntityModelLayers.HORSE_ARMOR));
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, HorseEntity horseEntity, float f, float g, float h, float j, float k, float l) {
        float p;
        float o;
        float n;
        ItemStack itemStack = horseEntity.getArmorType();
        if (!(itemStack.getItem() instanceof HorseArmorItem)) {
            return;
        }
        HorseArmorItem horseArmorItem = (HorseArmorItem)itemStack.getItem();
        ((HorseEntityModel)this.getContextModel()).copyStateTo(this.model);
        this.model.animateModel(horseEntity, f, g, h);
        this.model.setAngles(horseEntity, f, g, j, k, l);
        if (horseArmorItem instanceof DyeableHorseArmorItem) {
            int m = ((DyeableHorseArmorItem)horseArmorItem).getColor(itemStack);
            n = (float)(m >> 16 & 0xFF) / 255.0f;
            o = (float)(m >> 8 & 0xFF) / 255.0f;
            p = (float)(m & 0xFF) / 255.0f;
        } else {
            n = 1.0f;
            o = 1.0f;
            p = 1.0f;
        }
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutoutNoCull(horseArmorItem.getEntityTexture()));
        this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, n, o, p, 1.0f);
    }
}

