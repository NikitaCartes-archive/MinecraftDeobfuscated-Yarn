package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.SaddleFeatureRenderer;
import net.minecraft.client.render.entity.model.PigEntityModel;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PigEntityRenderer extends MobEntityRenderer<PigEntity, PigEntityModel<PigEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/pig/pig.png");

	public PigEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new PigEntityModel<>(), 0.7F);
		this.addFeature(new SaddleFeatureRenderer<>(this, new PigEntityModel<>(0.5F), new Identifier("textures/entity/pig/pig_saddle.png")));
	}

	public Identifier method_4087(PigEntity pigEntity) {
		return TEXTURE;
	}
}
