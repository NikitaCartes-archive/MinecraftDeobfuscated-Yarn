package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.SaddleFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PigEntityModel;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PigEntityRenderer extends MobEntityRenderer<PigEntity, PigEntityModel<PigEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/pig/pig.png");
	private static final Identifier POTATO_TEXTURE = new Identifier("textures/entity/pig/pig_hwat.png");

	public PigEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new PigEntityModel<>(context.getPart(EntityModelLayers.PIG)), 0.7F);
		this.addFeature(
			new SaddleFeatureRenderer<>(this, new PigEntityModel<>(context.getPart(EntityModelLayers.PIG_SADDLE)), new Identifier("textures/entity/pig/pig_saddle.png"))
		);
	}

	public Identifier getTexture(PigEntity pigEntity) {
		return pigEntity.isPotato() && pigEntity.getPotato() ? POTATO_TEXTURE : TEXTURE;
	}
}
