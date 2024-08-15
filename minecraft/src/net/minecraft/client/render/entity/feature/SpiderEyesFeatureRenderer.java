package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.SpiderEntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SpiderEyesFeatureRenderer<M extends SpiderEntityModel> extends EyesFeatureRenderer<LivingEntityRenderState, M> {
	private static final RenderLayer SKIN = RenderLayer.getEyes(Identifier.ofVanilla("textures/entity/spider_eyes.png"));

	public SpiderEyesFeatureRenderer(FeatureRendererContext<LivingEntityRenderState, M> featureRendererContext) {
		super(featureRendererContext);
	}

	@Override
	public RenderLayer getEyesTexture() {
		return SKIN;
	}
}
