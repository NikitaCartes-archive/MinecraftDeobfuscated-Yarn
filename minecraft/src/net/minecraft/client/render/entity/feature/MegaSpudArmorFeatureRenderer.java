package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.MegaSpudEntityModel;
import net.minecraft.entity.mob.MegaSpudEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class MegaSpudArmorFeatureRenderer extends EnergySwirlOverlayFeatureRenderer<MegaSpudEntity, MegaSpudEntityModel<MegaSpudEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/wither/wither_armor.png");
	private final MegaSpudEntityModel<MegaSpudEntity> model;

	public MegaSpudArmorFeatureRenderer(FeatureRendererContext<MegaSpudEntity, MegaSpudEntityModel<MegaSpudEntity>> context, EntityModelLoader loader) {
		super(context);
		this.model = new MegaSpudEntityModel<>(loader.getModelPart(EntityModelLayers.MEGA_SPUD_OUTER));
	}

	@Override
	protected float getEnergySwirlX(float partialAge) {
		return MathHelper.cos(partialAge * 0.02F) * 3.0F;
	}

	@Override
	protected Identifier getEnergySwirlTexture() {
		return TEXTURE;
	}

	@Override
	protected EntityModel<MegaSpudEntity> getEnergySwirlModel() {
		return this.model;
	}
}
