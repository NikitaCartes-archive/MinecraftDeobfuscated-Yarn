package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EndermanEntityModel;
import net.minecraft.client.render.entity.state.EndermanEntityRenderState;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class EndermanEyesFeatureRenderer extends EyesFeatureRenderer<EndermanEntityRenderState, EndermanEntityModel<EndermanEntityRenderState>> {
	private static final RenderLayer SKIN = RenderLayer.getEyes(Identifier.ofVanilla("textures/entity/enderman/enderman_eyes.png"));

	public EndermanEyesFeatureRenderer(FeatureRendererContext<EndermanEntityRenderState, EndermanEntityModel<EndermanEntityRenderState>> featureRendererContext) {
		super(featureRendererContext);
	}

	@Override
	public RenderLayer getEyesTexture() {
		return SKIN;
	}
}
