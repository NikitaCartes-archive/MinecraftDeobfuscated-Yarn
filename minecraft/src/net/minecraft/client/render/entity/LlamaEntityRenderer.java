package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.LlamaDecorFeatureRenderer;
import net.minecraft.client.render.entity.model.LlamaEntityModel;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class LlamaEntityRenderer extends MobEntityRenderer<LlamaEntity, LlamaEntityModel<LlamaEntity>> {
	private static final Identifier[] TEXTURES = new Identifier[]{
		new Identifier("textures/entity/llama/creamy.png"),
		new Identifier("textures/entity/llama/white.png"),
		new Identifier("textures/entity/llama/brown.png"),
		new Identifier("textures/entity/llama/gray.png")
	};

	public LlamaEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new LlamaEntityModel<>(0.0F), 0.7F);
		this.method_4046(new LlamaDecorFeatureRenderer(this));
	}

	protected Identifier method_4037(LlamaEntity llamaEntity) {
		return TEXTURES[llamaEntity.getVariant()];
	}
}
