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
import net.minecraft.client.render.entity.model.LlamaEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class LlamaDecorFeatureRenderer
extends FeatureRenderer<LlamaEntity, LlamaEntityModel<LlamaEntity>> {
    private static final Identifier[] LLAMA_DECOR = new Identifier[]{new Identifier("textures/entity/llama/decor/white.png"), new Identifier("textures/entity/llama/decor/orange.png"), new Identifier("textures/entity/llama/decor/magenta.png"), new Identifier("textures/entity/llama/decor/light_blue.png"), new Identifier("textures/entity/llama/decor/yellow.png"), new Identifier("textures/entity/llama/decor/lime.png"), new Identifier("textures/entity/llama/decor/pink.png"), new Identifier("textures/entity/llama/decor/gray.png"), new Identifier("textures/entity/llama/decor/light_gray.png"), new Identifier("textures/entity/llama/decor/cyan.png"), new Identifier("textures/entity/llama/decor/purple.png"), new Identifier("textures/entity/llama/decor/blue.png"), new Identifier("textures/entity/llama/decor/brown.png"), new Identifier("textures/entity/llama/decor/green.png"), new Identifier("textures/entity/llama/decor/red.png"), new Identifier("textures/entity/llama/decor/black.png")};
    private static final Identifier TRADER_LLAMA_DECOR = new Identifier("textures/entity/llama/decor/trader_llama.png");
    private final LlamaEntityModel<LlamaEntity> model;

    public LlamaDecorFeatureRenderer(FeatureRendererContext<LlamaEntity, LlamaEntityModel<LlamaEntity>> featureRendererContext, class_5599 arg) {
        super(featureRendererContext);
        this.model = new LlamaEntityModel(arg.method_32072(EntityModelLayers.LLAMA_DECOR));
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, LlamaEntity llamaEntity, float f, float g, float h, float j, float k, float l) {
        Identifier identifier;
        DyeColor dyeColor = llamaEntity.getCarpetColor();
        if (dyeColor != null) {
            identifier = LLAMA_DECOR[dyeColor.getId()];
        } else if (llamaEntity.isTrader()) {
            identifier = TRADER_LLAMA_DECOR;
        } else {
            return;
        }
        ((LlamaEntityModel)this.getContextModel()).copyStateTo(this.model);
        this.model.setAngles(llamaEntity, f, g, j, k, l);
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutoutNoCull(identifier));
        this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
    }
}

