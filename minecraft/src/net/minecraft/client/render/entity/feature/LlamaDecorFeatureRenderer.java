package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.entity.model.LlamaEntityModel;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class LlamaDecorFeatureRenderer extends FeatureRenderer<LlamaEntity, LlamaEntityModel<LlamaEntity>> {
	private static final Identifier[] LLAMA_DECOR = new Identifier[]{
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
	private static final Identifier TRADER_LLAMA_DECOR = new Identifier("textures/entity/llama/decor/trader_llama.png");
	private final LlamaEntityModel<LlamaEntity> model = new LlamaEntityModel<>(0.5F);

	public LlamaDecorFeatureRenderer(FeatureRendererContext<LlamaEntity, LlamaEntityModel<LlamaEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_4191(
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage,
		int i,
		LlamaEntity llamaEntity,
		float f,
		float g,
		float h,
		float j,
		float k,
		float l,
		float m
	) {
		DyeColor dyeColor = llamaEntity.getCarpetColor();
		Identifier identifier;
		if (dyeColor != null) {
			identifier = LLAMA_DECOR[dyeColor.getId()];
		} else {
			if (!llamaEntity.isTrader()) {
				return;
			}

			identifier = TRADER_LLAMA_DECOR;
		}

		this.getModel().copyStateTo(this.model);
		this.model.method_22962(llamaEntity, f, g, j, k, l, m);
		method_23197(this.model, identifier, matrixStack, layeredVertexConsumerStorage, i, 1.0F, 1.0F, 1.0F);
	}
}
