package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.ChickenEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.ChickenEntityRenderState;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ChickenEntityRenderer extends AgeableMobEntityRenderer<ChickenEntity, ChickenEntityRenderState, ChickenEntityModel> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/chicken.png");

	public ChickenEntityRenderer(EntityRendererFactory.Context context) {
		super(
			context, new ChickenEntityModel(context.getPart(EntityModelLayers.CHICKEN)), new ChickenEntityModel(context.getPart(EntityModelLayers.CHICKEN_BABY)), 0.3F
		);
	}

	public Identifier getTexture(ChickenEntityRenderState chickenEntityRenderState) {
		return TEXTURE;
	}

	public ChickenEntityRenderState getRenderState() {
		return new ChickenEntityRenderState();
	}

	public void updateRenderState(ChickenEntity chickenEntity, ChickenEntityRenderState chickenEntityRenderState, float f) {
		super.updateRenderState(chickenEntity, chickenEntityRenderState, f);
		chickenEntityRenderState.flapProgress = MathHelper.lerp(f, chickenEntity.prevFlapProgress, chickenEntity.flapProgress);
		chickenEntityRenderState.maxWingDeviation = MathHelper.lerp(f, chickenEntity.prevMaxWingDeviation, chickenEntity.maxWingDeviation);
	}
}
