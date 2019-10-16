package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.SpiderEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SpiderEyesFeatureRenderer<T extends Entity, M extends SpiderEntityModel<T>> extends EntityEyesFeatureRenderer<T, M> {
	private static final Identifier SKIN = new Identifier("textures/entity/spider_eyes.png");

	public SpiderEyesFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
		super(featureRendererContext);
	}

	@Override
	public Identifier getTexture() {
		return SKIN;
	}
}
