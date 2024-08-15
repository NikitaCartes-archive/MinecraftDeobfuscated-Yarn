package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.SaddleFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PigEntityModel;
import net.minecraft.client.render.entity.state.PigEntityRenderState;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PigEntityRenderer extends AgeableMobEntityRenderer<PigEntity, PigEntityRenderState, PigEntityModel> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/pig/pig.png");

	public PigEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new PigEntityModel(context.getPart(EntityModelLayers.PIG)), new PigEntityModel(context.getPart(EntityModelLayers.PIG_BABY)), 0.7F);
		this.addFeature(
			new SaddleFeatureRenderer<>(
				this,
				new PigEntityModel(context.getPart(EntityModelLayers.PIG_SADDLE)),
				new PigEntityModel(context.getPart(EntityModelLayers.PIG_BABY_SADDLE)),
				Identifier.ofVanilla("textures/entity/pig/pig_saddle.png")
			)
		);
	}

	public Identifier getTexture(PigEntityRenderState pigEntityRenderState) {
		return TEXTURE;
	}

	public PigEntityRenderState getRenderState() {
		return new PigEntityRenderState();
	}

	public void updateRenderState(PigEntity pigEntity, PigEntityRenderState pigEntityRenderState, float f) {
		super.updateRenderState(pigEntity, pigEntityRenderState, f);
		pigEntityRenderState.saddled = pigEntity.isSaddled();
	}
}
