package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EndermanEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class EndermanEyesFeatureRenderer<T extends LivingEntity> extends EntityEyesFeatureRenderer<T, EndermanEntityModel<T>> {
	private static final Identifier SKIN = new Identifier("textures/entity/enderman/enderman_eyes.png");

	public EndermanEyesFeatureRenderer(FeatureRendererContext<T, EndermanEntityModel<T>> featureRendererContext) {
		super(featureRendererContext);
	}

	@Override
	public Identifier getTexture() {
		return SKIN;
	}
}
