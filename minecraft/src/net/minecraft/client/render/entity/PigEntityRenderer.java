package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.PigSaddleFeatureRenderer;
import net.minecraft.client.render.entity.model.PigEntityModel;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PigEntityRenderer extends MobEntityRenderer<PigEntity, PigEntityModel<PigEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/pig/pig.png");

	public PigEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new PigEntityModel<>(), 0.7F);
		this.addFeature(new PigSaddleFeatureRenderer(this));
	}

	public Identifier getTexture(PigEntity pigEntity) {
		return TEXTURE;
	}
}
