package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5617;
import net.minecraft.client.render.entity.feature.LlamaDecorFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
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

	public LlamaEntityRenderer(class_5617.class_5618 arg, EntityModelLayer entityModelLayer) {
		super(arg, new LlamaEntityModel<>(arg.method_32167(entityModelLayer)), 0.7F);
		this.addFeature(new LlamaDecorFeatureRenderer(this, arg.method_32170()));
	}

	public Identifier getTexture(LlamaEntity llamaEntity) {
		return TEXTURES[llamaEntity.getVariant()];
	}
}
