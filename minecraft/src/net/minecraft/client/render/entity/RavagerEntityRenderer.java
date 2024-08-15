package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.RavagerEntityModel;
import net.minecraft.client.render.entity.state.RavagerEntityRenderState;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class RavagerEntityRenderer extends MobEntityRenderer<RavagerEntity, RavagerEntityRenderState, RavagerEntityModel> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/illager/ravager.png");

	public RavagerEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new RavagerEntityModel(context.getPart(EntityModelLayers.RAVAGER)), 1.1F);
	}

	public Identifier getTexture(RavagerEntityRenderState ravagerEntityRenderState) {
		return TEXTURE;
	}

	public RavagerEntityRenderState getRenderState() {
		return new RavagerEntityRenderState();
	}

	public void updateRenderState(RavagerEntity ravagerEntity, RavagerEntityRenderState ravagerEntityRenderState, float f) {
		super.updateRenderState(ravagerEntity, ravagerEntityRenderState, f);
		ravagerEntityRenderState.stunTick = (float)ravagerEntity.getStunTick() > 0.0F ? (float)ravagerEntity.getStunTick() - f : 0.0F;
		ravagerEntityRenderState.attackTick = (float)ravagerEntity.getAttackTick() > 0.0F ? (float)ravagerEntity.getAttackTick() - f : 0.0F;
		if (ravagerEntity.getRoarTick() > 0) {
			ravagerEntityRenderState.roarTick = ((float)(20 - ravagerEntity.getRoarTick()) + f) / 20.0F;
		} else {
			ravagerEntityRenderState.roarTick = 0.0F;
		}
	}
}
