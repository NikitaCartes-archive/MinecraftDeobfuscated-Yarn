package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.SnowmanPumpkinFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SnowGolemEntityModel;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SnowGolemEntityRenderer extends MobEntityRenderer<SnowGolemEntity, SnowGolemEntityModel<SnowGolemEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/snow_golem.png");

	public SnowGolemEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new SnowGolemEntityModel<>(context.getPart(EntityModelLayers.SNOW_GOLEM)), 0.5F);
		this.addFeature(new SnowmanPumpkinFeatureRenderer(this));
	}

	public Identifier getTexture(SnowGolemEntity snowGolemEntity) {
		return TEXTURE;
	}
}
