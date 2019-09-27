package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4607;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CreeperChargeFeatureRenderer extends class_4607<CreeperEntity, CreeperEntityModel<CreeperEntity>> {
	private static final Identifier SKIN = new Identifier("textures/entity/creeper/creeper_armor.png");
	private final CreeperEntityModel<CreeperEntity> model = new CreeperEntityModel<>(2.0F);

	public CreeperChargeFeatureRenderer(FeatureRendererContext<CreeperEntity, CreeperEntityModel<CreeperEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	@Override
	protected float method_23202(float f) {
		return f * 0.01F;
	}

	@Override
	protected Identifier method_23201() {
		return SKIN;
	}

	@Override
	protected EntityModel<CreeperEntity> method_23203() {
		return this.model;
	}
}
