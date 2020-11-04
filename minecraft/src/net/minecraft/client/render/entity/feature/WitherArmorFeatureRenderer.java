package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5599;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.WitherEntityModel;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class WitherArmorFeatureRenderer extends EnergySwirlOverlayFeatureRenderer<WitherEntity, WitherEntityModel<WitherEntity>> {
	private static final Identifier SKIN = new Identifier("textures/entity/wither/wither_armor.png");
	private final WitherEntityModel<WitherEntity> model;

	public WitherArmorFeatureRenderer(FeatureRendererContext<WitherEntity, WitherEntityModel<WitherEntity>> featureRendererContext, class_5599 arg) {
		super(featureRendererContext);
		this.model = new WitherEntityModel<>(arg.method_32072(EntityModelLayers.WITHER_ARMOR));
	}

	@Override
	protected float getEnergySwirlX(float partialAge) {
		return MathHelper.cos(partialAge * 0.02F) * 3.0F;
	}

	@Override
	protected Identifier getEnergySwirlTexture() {
		return SKIN;
	}

	@Override
	protected EntityModel<WitherEntity> getEnergySwirlModel() {
		return this.model;
	}
}
