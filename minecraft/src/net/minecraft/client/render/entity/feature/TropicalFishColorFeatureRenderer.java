package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.LargeTropicalFishEntityModel;
import net.minecraft.client.render.entity.model.SmallTropicalFishEntityModel;
import net.minecraft.client.render.entity.state.TropicalFishEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class TropicalFishColorFeatureRenderer extends FeatureRenderer<TropicalFishEntityRenderState, EntityModel<TropicalFishEntityRenderState>> {
	private static final Identifier KOB_TEXTURE = Identifier.ofVanilla("textures/entity/fish/tropical_a_pattern_1.png");
	private static final Identifier SUNSTREAK_TEXTURE = Identifier.ofVanilla("textures/entity/fish/tropical_a_pattern_2.png");
	private static final Identifier SNOOPER_TEXTURE = Identifier.ofVanilla("textures/entity/fish/tropical_a_pattern_3.png");
	private static final Identifier DASHER_TEXTURE = Identifier.ofVanilla("textures/entity/fish/tropical_a_pattern_4.png");
	private static final Identifier BRINELY_TEXTURE = Identifier.ofVanilla("textures/entity/fish/tropical_a_pattern_5.png");
	private static final Identifier SPOTTY_TEXTURE = Identifier.ofVanilla("textures/entity/fish/tropical_a_pattern_6.png");
	private static final Identifier FLOPPER_TEXTURE = Identifier.ofVanilla("textures/entity/fish/tropical_b_pattern_1.png");
	private static final Identifier STRIPEY_TEXTURE = Identifier.ofVanilla("textures/entity/fish/tropical_b_pattern_2.png");
	private static final Identifier GLITTER_TEXTURE = Identifier.ofVanilla("textures/entity/fish/tropical_b_pattern_3.png");
	private static final Identifier BLOCKFISH_TEXTURE = Identifier.ofVanilla("textures/entity/fish/tropical_b_pattern_4.png");
	private static final Identifier BETTY_TEXTURE = Identifier.ofVanilla("textures/entity/fish/tropical_b_pattern_5.png");
	private static final Identifier CLAYFISH_TEXTURE = Identifier.ofVanilla("textures/entity/fish/tropical_b_pattern_6.png");
	private final SmallTropicalFishEntityModel smallModel;
	private final LargeTropicalFishEntityModel largeModel;

	public TropicalFishColorFeatureRenderer(
		FeatureRendererContext<TropicalFishEntityRenderState, EntityModel<TropicalFishEntityRenderState>> context, EntityModelLoader loader
	) {
		super(context);
		this.smallModel = new SmallTropicalFishEntityModel(loader.getModelPart(EntityModelLayers.TROPICAL_FISH_SMALL_PATTERN));
		this.largeModel = new LargeTropicalFishEntityModel(loader.getModelPart(EntityModelLayers.TROPICAL_FISH_LARGE_PATTERN));
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, TropicalFishEntityRenderState tropicalFishEntityRenderState, float f, float g
	) {
		TropicalFishEntity.Variety variety = tropicalFishEntityRenderState.variety;

		EntityModel<TropicalFishEntityRenderState> entityModel = (EntityModel<TropicalFishEntityRenderState>)(switch (variety.getSize()) {
			case SMALL -> this.smallModel;
			case LARGE -> this.largeModel;
		});

		Identifier identifier = switch (variety) {
			case KOB -> KOB_TEXTURE;
			case SUNSTREAK -> SUNSTREAK_TEXTURE;
			case SNOOPER -> SNOOPER_TEXTURE;
			case DASHER -> DASHER_TEXTURE;
			case BRINELY -> BRINELY_TEXTURE;
			case SPOTTY -> SPOTTY_TEXTURE;
			case FLOPPER -> FLOPPER_TEXTURE;
			case STRIPEY -> STRIPEY_TEXTURE;
			case GLITTER -> GLITTER_TEXTURE;
			case BLOCKFISH -> BLOCKFISH_TEXTURE;
			case BETTY -> BETTY_TEXTURE;
			case CLAYFISH -> CLAYFISH_TEXTURE;
		};
		render(entityModel, identifier, matrixStack, vertexConsumerProvider, i, tropicalFishEntityRenderState, tropicalFishEntityRenderState.patternColor);
	}
}
