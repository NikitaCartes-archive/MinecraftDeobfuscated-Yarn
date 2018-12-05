package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.LlamaEntityModel;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class LlamaDecorEntityRenderer implements LayerEntityRenderer<LlamaEntity> {
	private static final Identifier[] field_4880 = new Identifier[]{
		new Identifier("textures/entity/llama/decor/white.png"),
		new Identifier("textures/entity/llama/decor/orange.png"),
		new Identifier("textures/entity/llama/decor/magenta.png"),
		new Identifier("textures/entity/llama/decor/light_blue.png"),
		new Identifier("textures/entity/llama/decor/yellow.png"),
		new Identifier("textures/entity/llama/decor/lime.png"),
		new Identifier("textures/entity/llama/decor/pink.png"),
		new Identifier("textures/entity/llama/decor/gray.png"),
		new Identifier("textures/entity/llama/decor/light_gray.png"),
		new Identifier("textures/entity/llama/decor/cyan.png"),
		new Identifier("textures/entity/llama/decor/purple.png"),
		new Identifier("textures/entity/llama/decor/blue.png"),
		new Identifier("textures/entity/llama/decor/brown.png"),
		new Identifier("textures/entity/llama/decor/green.png"),
		new Identifier("textures/entity/llama/decor/red.png"),
		new Identifier("textures/entity/llama/decor/black.png")
	};
	private final LlamaEntityRenderer field_4879;
	private final LlamaEntityModel field_4881 = new LlamaEntityModel(0.5F);

	public LlamaDecorEntityRenderer(LlamaEntityRenderer llamaEntityRenderer) {
		this.field_4879 = llamaEntityRenderer;
	}

	public void render(LlamaEntity llamaEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (llamaEntity.method_6807()) {
			this.field_4879.bindTexture(field_4880[llamaEntity.method_6800().getId()]);
			this.field_4881.setAttributes(this.field_4879.method_4038());
			this.field_4881.render(llamaEntity, f, g, i, j, k, l);
		}
	}

	@Override
	public boolean shouldMergeTextures() {
		return false;
	}
}
