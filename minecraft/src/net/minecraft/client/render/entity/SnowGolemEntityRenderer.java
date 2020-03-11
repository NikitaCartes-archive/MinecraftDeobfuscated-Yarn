package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.SnowmanPumpkinFeatureRenderer;
import net.minecraft.client.render.entity.model.SnowmanEntityModel;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SnowGolemEntityRenderer extends MobEntityRenderer<SnowGolemEntity, SnowmanEntityModel<SnowGolemEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/snow_golem.png");

	public SnowGolemEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new SnowmanEntityModel<>(), 0.5F);
		this.addFeature(new SnowmanPumpkinFeatureRenderer(this));
	}

	public Identifier getTexture(SnowGolemEntity snowGolemEntity) {
		return TEXTURE;
	}
}
