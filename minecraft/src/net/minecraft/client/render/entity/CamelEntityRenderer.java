package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.CamelEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.CamelEntityRenderState;
import net.minecraft.entity.passive.CamelEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CamelEntityRenderer extends AgeableMobEntityRenderer<CamelEntity, CamelEntityRenderState, CamelEntityModel> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/camel/camel.png");

	public CamelEntityRenderer(EntityRendererFactory.Context ctx) {
		super(ctx, new CamelEntityModel(ctx.getPart(EntityModelLayers.CAMEL)), new CamelEntityModel(ctx.getPart(EntityModelLayers.CAMEL_BABY)), 0.7F);
	}

	public Identifier getTexture(CamelEntityRenderState camelEntityRenderState) {
		return TEXTURE;
	}

	public CamelEntityRenderState createRenderState() {
		return new CamelEntityRenderState();
	}

	public void updateRenderState(CamelEntity camelEntity, CamelEntityRenderState camelEntityRenderState, float f) {
		super.updateRenderState(camelEntity, camelEntityRenderState, f);
		camelEntityRenderState.saddled = camelEntity.isSaddled();
		camelEntityRenderState.hasPassengers = camelEntity.hasPassengers();
		camelEntityRenderState.jumpCooldown = Math.max((float)camelEntity.getJumpCooldown() - f, 0.0F);
		camelEntityRenderState.sittingTransitionAnimationState.copyFrom(camelEntity.sittingTransitionAnimationState);
		camelEntityRenderState.sittingAnimationState.copyFrom(camelEntity.sittingAnimationState);
		camelEntityRenderState.standingTransitionAnimationState.copyFrom(camelEntity.standingTransitionAnimationState);
		camelEntityRenderState.idlingAnimationState.copyFrom(camelEntity.idlingAnimationState);
		camelEntityRenderState.dashingAnimationState.copyFrom(camelEntity.dashingAnimationState);
	}
}
