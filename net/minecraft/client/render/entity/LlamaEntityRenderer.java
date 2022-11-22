/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.LlamaDecorFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.LlamaEntityModel;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class LlamaEntityRenderer
extends MobEntityRenderer<LlamaEntity, LlamaEntityModel<LlamaEntity>> {
    private static final Identifier CREAMY_TEXTURE = new Identifier("textures/entity/llama/creamy.png");
    private static final Identifier WHITE_TEXTURE = new Identifier("textures/entity/llama/white.png");
    private static final Identifier BROWN_TEXTURE = new Identifier("textures/entity/llama/brown.png");
    private static final Identifier GRAY_TEXTURE = new Identifier("textures/entity/llama/gray.png");

    public LlamaEntityRenderer(EntityRendererFactory.Context ctx, EntityModelLayer layer) {
        super(ctx, new LlamaEntityModel(ctx.getPart(layer)), 0.7f);
        this.addFeature(new LlamaDecorFeatureRenderer(this, ctx.getModelLoader()));
    }

    @Override
    public Identifier getTexture(LlamaEntity llamaEntity) {
        return switch (llamaEntity.getVariant()) {
            default -> throw new IncompatibleClassChangeError();
            case LlamaEntity.Variant.CREAMY -> CREAMY_TEXTURE;
            case LlamaEntity.Variant.WHITE -> WHITE_TEXTURE;
            case LlamaEntity.Variant.BROWN -> BROWN_TEXTURE;
            case LlamaEntity.Variant.GRAY -> GRAY_TEXTURE;
        };
    }
}

