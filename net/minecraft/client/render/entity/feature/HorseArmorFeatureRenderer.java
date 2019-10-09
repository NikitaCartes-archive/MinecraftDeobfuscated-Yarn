/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.item.DyeableHorseArmorItem;
import net.minecraft.item.HorseArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public class HorseArmorFeatureRenderer
extends FeatureRenderer<HorseEntity, HorseEntityModel<HorseEntity>> {
    private final HorseEntityModel<HorseEntity> model = new HorseEntityModel(RenderLayer::getEntitySolid, 0.1f);

    public HorseArmorFeatureRenderer(FeatureRendererContext<HorseEntity, HorseEntityModel<HorseEntity>> featureRendererContext) {
        super(featureRendererContext);
    }

    public void method_18658(MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, int i, HorseEntity horseEntity, float f, float g, float h, float j, float k, float l, float m) {
        float q;
        float p;
        float o;
        ItemStack itemStack = horseEntity.getArmorType();
        if (!(itemStack.getItem() instanceof HorseArmorItem)) {
            return;
        }
        HorseArmorItem horseArmorItem = (HorseArmorItem)itemStack.getItem();
        ((HorseEntityModel)this.getModel()).copyStateTo(this.model);
        this.model.method_17084(horseEntity, f, g, h);
        this.model.method_17085(horseEntity, f, g, j, k, l, m);
        if (horseArmorItem instanceof DyeableHorseArmorItem) {
            int n = ((DyeableHorseArmorItem)horseArmorItem).getColor(itemStack);
            o = (float)(n >> 16 & 0xFF) / 255.0f;
            p = (float)(n >> 8 & 0xFF) / 255.0f;
            q = (float)(n & 0xFF) / 255.0f;
        } else {
            o = 1.0f;
            p = 1.0f;
            q = 1.0f;
        }
        VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.getEntityCutoutNoCull(horseArmorItem.getEntityTexture()));
        this.model.renderItem(matrixStack, vertexConsumer, i, OverlayTexture.field_21444, o, p, q);
    }
}

