package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.state.CreeperEntityRenderState;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CreeperChargeFeatureRenderer extends EnergySwirlOverlayFeatureRenderer<CreeperEntityRenderState, CreeperEntityModel> {
	private static final Identifier SKIN = Identifier.ofVanilla("textures/entity/creeper/creeper_armor.png");
	private final CreeperEntityModel model;

	public CreeperChargeFeatureRenderer(FeatureRendererContext<CreeperEntityRenderState, CreeperEntityModel> context, EntityModelLoader loader) {
		super(context);
		this.model = new CreeperEntityModel(loader.getModelPart(EntityModelLayers.CREEPER_ARMOR));
	}

	protected boolean shouldRender(CreeperEntityRenderState creeperEntityRenderState) {
		return creeperEntityRenderState.charged;
	}

	@Override
	protected float getEnergySwirlX(float partialAge) {
		return partialAge * 0.01F;
	}

	@Override
	protected Identifier getEnergySwirlTexture() {
		return SKIN;
	}

	protected CreeperEntityModel getEnergySwirlModel() {
		return this.model;
	}
}
