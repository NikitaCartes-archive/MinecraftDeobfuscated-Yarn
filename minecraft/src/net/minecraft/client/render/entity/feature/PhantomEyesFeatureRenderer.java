package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.PhantomEntityModel;
import net.minecraft.client.render.entity.state.PhantomEntityRenderState;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PhantomEyesFeatureRenderer extends EyesFeatureRenderer<PhantomEntityRenderState, PhantomEntityModel> {
	private static final RenderLayer SKIN = RenderLayer.getEyes(Identifier.ofVanilla("textures/entity/phantom_eyes.png"));

	public PhantomEyesFeatureRenderer(FeatureRendererContext<PhantomEntityRenderState, PhantomEntityModel> featureRendererContext) {
		super(featureRendererContext);
	}

	@Override
	public RenderLayer getEyesTexture() {
		return SKIN;
	}
}
