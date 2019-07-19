package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.LlamaEntityModel;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

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

	public LlamaDecorFeatureRenderer(FeatureRendererContext<LlamaEntity, LlamaEntityModel<LlamaEntity>> context) {
		super(context);
	}

	public void render(LlamaEntity llamaEntity, float f, float g, float h, float i, float j, float k, float l) {
		DyeColor dyeColor = llamaEntity.getCarpetColor();
		if (dyeColor != null) {
			this.bindTexture(LLAMA_DECOR[dyeColor.getId()]);
		} else {
			if (!llamaEntity.isTrader()) {
				return;
			}

			this.bindTexture(TRADER_LLAMA_DECOR);
		}

		this.getContextModel().copyStateTo(this.model);
		this.model.render(llamaEntity, f, g, i, j, k, l);
	}

	@Override
	public boolean hasHurtOverlay() {
		return false;
	}
}
