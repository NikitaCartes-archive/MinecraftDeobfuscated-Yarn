package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.PigSaddleFeatureRenderer;
import net.minecraft.client.render.entity.model.PigEntityModel;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PigEntityRenderer extends MobEntityRenderer<PigEntity, PigEntityModel<PigEntity>> {
	private static final Identifier field_4755 = new Identifier("textures/entity/pig/pig.png");

	public PigEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new PigEntityModel<>(), 0.7F);
		this.method_4046(new PigSaddleFeatureRenderer(this));
	}

	protected Identifier method_4087(PigEntity pigEntity) {
		return field_4755;
	}
}
