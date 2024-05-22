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
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/pig/pig.png");

	public PigEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new PigEntityModel<>(context.getPart(EntityModelLayers.PIG)), 0.7F);
		this.addFeature(
			new SaddleFeatureRenderer<>(
				this, new PigEntityModel<>(context.getPart(EntityModelLayers.PIG_SADDLE)), Identifier.ofVanilla("textures/entity/pig/pig_saddle.png")
			)
		);
	}

	public Identifier getTexture(PigEntity pigEntity) {
		return TEXTURE;
	}
}
