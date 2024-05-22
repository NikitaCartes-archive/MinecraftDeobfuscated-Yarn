package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.LlamaDecorFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.LlamaEntityModel;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class LlamaEntityRenderer extends MobEntityRenderer<LlamaEntity, LlamaEntityModel<LlamaEntity>> {
	private static final Identifier CREAMY_TEXTURE = Identifier.ofVanilla("textures/entity/llama/creamy.png");
	private static final Identifier WHITE_TEXTURE = Identifier.ofVanilla("textures/entity/llama/white.png");
	private static final Identifier BROWN_TEXTURE = Identifier.ofVanilla("textures/entity/llama/brown.png");
	private static final Identifier GRAY_TEXTURE = Identifier.ofVanilla("textures/entity/llama/gray.png");

	public LlamaEntityRenderer(EntityRendererFactory.Context ctx, EntityModelLayer layer) {
		super(ctx, new LlamaEntityModel<>(ctx.getPart(layer)), 0.7F);
		this.addFeature(new LlamaDecorFeatureRenderer(this, ctx.getModelLoader()));
	}

	public Identifier getTexture(LlamaEntity llamaEntity) {
		return switch (llamaEntity.getVariant()) {
			case CREAMY -> CREAMY_TEXTURE;
			case WHITE -> WHITE_TEXTURE;
			case BROWN -> BROWN_TEXTURE;
			case GRAY -> GRAY_TEXTURE;
		};
	}
}
