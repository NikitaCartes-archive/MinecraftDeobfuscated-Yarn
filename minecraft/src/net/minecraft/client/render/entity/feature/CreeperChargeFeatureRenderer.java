package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5599;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CreeperChargeFeatureRenderer extends EnergySwirlOverlayFeatureRenderer<CreeperEntity, CreeperEntityModel<CreeperEntity>> {
	private static final Identifier SKIN = new Identifier("textures/entity/creeper/creeper_armor.png");
	private final CreeperEntityModel<CreeperEntity> model;

	public CreeperChargeFeatureRenderer(FeatureRendererContext<CreeperEntity, CreeperEntityModel<CreeperEntity>> featureRendererContext, class_5599 arg) {
		super(featureRendererContext);
		this.model = new CreeperEntityModel<>(arg.method_32072(EntityModelLayers.CREEPER_ARMOR));
	}

	@Override
	protected float getEnergySwirlX(float partialAge) {
		return partialAge * 0.01F;
	}

	@Override
	protected Identifier getEnergySwirlTexture() {
		return SKIN;
	}

	@Override
	protected EntityModel<CreeperEntity> getEnergySwirlModel() {
		return this.model;
	}
}
