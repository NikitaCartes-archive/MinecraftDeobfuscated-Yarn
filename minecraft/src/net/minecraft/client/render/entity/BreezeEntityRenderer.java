package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.BreezeEyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.BreezeWindFeatureRenderer;
import net.minecraft.client.render.entity.model.BreezeEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.mob.BreezeEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BreezeEntityRenderer extends MobEntityRenderer<BreezeEntity, BreezeEntityModel<BreezeEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/breeze/breeze.png");
	private static final Identifier WIND_TEXTURE = new Identifier("textures/entity/breeze/breeze_wind.png");

	public BreezeEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new BreezeEntityModel<>(context.getPart(EntityModelLayers.BREEZE)), 0.8F);
		this.addFeature(new BreezeWindFeatureRenderer(this, context.getModelLoader(), WIND_TEXTURE));
		this.addFeature(new BreezeEyesFeatureRenderer(this, context.getModelLoader(), TEXTURE));
	}

	public Identifier getTexture(BreezeEntity breezeEntity) {
		return TEXTURE;
	}
}
