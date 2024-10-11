package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.LlamaDecorFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.LlamaEntityModel;
import net.minecraft.client.render.entity.state.LlamaEntityRenderState;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class LlamaEntityRenderer extends AgeableMobEntityRenderer<LlamaEntity, LlamaEntityRenderState, LlamaEntityModel> {
	private static final Identifier CREAMY_TEXTURE = Identifier.ofVanilla("textures/entity/llama/creamy.png");
	private static final Identifier WHITE_TEXTURE = Identifier.ofVanilla("textures/entity/llama/white.png");
	private static final Identifier BROWN_TEXTURE = Identifier.ofVanilla("textures/entity/llama/brown.png");
	private static final Identifier GRAY_TEXTURE = Identifier.ofVanilla("textures/entity/llama/gray.png");

	public LlamaEntityRenderer(EntityRendererFactory.Context context, EntityModelLayer layer, EntityModelLayer babyLayer) {
		super(context, new LlamaEntityModel(context.getPart(layer)), new LlamaEntityModel(context.getPart(babyLayer)), 0.7F);
		this.addFeature(new LlamaDecorFeatureRenderer(this, context.getModelLoader(), context.getEquipmentRenderer()));
	}

	public Identifier getTexture(LlamaEntityRenderState llamaEntityRenderState) {
		return switch (llamaEntityRenderState.variant) {
			case CREAMY -> CREAMY_TEXTURE;
			case WHITE -> WHITE_TEXTURE;
			case BROWN -> BROWN_TEXTURE;
			case GRAY -> GRAY_TEXTURE;
		};
	}

	public LlamaEntityRenderState createRenderState() {
		return new LlamaEntityRenderState();
	}

	public void updateRenderState(LlamaEntity llamaEntity, LlamaEntityRenderState llamaEntityRenderState, float f) {
		super.updateRenderState(llamaEntity, llamaEntityRenderState, f);
		llamaEntityRenderState.variant = llamaEntity.getVariant();
		llamaEntityRenderState.hasChest = !llamaEntity.isBaby() && llamaEntity.hasChest();
		llamaEntityRenderState.bodyArmor = llamaEntity.getBodyArmor();
		llamaEntityRenderState.trader = llamaEntity.isTrader();
	}
}
