package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.BeeEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.BeeEntityRenderState;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BeeEntityRenderer extends AgeableMobEntityRenderer<BeeEntity, BeeEntityRenderState, BeeEntityModel> {
	private static final Identifier ANGRY_TEXTURE = Identifier.ofVanilla("textures/entity/bee/bee_angry.png");
	private static final Identifier ANGRY_NECTAR_TEXTURE = Identifier.ofVanilla("textures/entity/bee/bee_angry_nectar.png");
	private static final Identifier PASSIVE_TEXTURE = Identifier.ofVanilla("textures/entity/bee/bee.png");
	private static final Identifier NECTAR_TEXTURE = Identifier.ofVanilla("textures/entity/bee/bee_nectar.png");

	public BeeEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new BeeEntityModel(context.getPart(EntityModelLayers.BEE)), new BeeEntityModel(context.getPart(EntityModelLayers.BEE_BABY)), 0.4F);
	}

	public Identifier getTexture(BeeEntityRenderState beeEntityRenderState) {
		if (beeEntityRenderState.angry) {
			return beeEntityRenderState.hasNectar ? ANGRY_NECTAR_TEXTURE : ANGRY_TEXTURE;
		} else {
			return beeEntityRenderState.hasNectar ? NECTAR_TEXTURE : PASSIVE_TEXTURE;
		}
	}

	public BeeEntityRenderState getRenderState() {
		return new BeeEntityRenderState();
	}

	public void updateRenderState(BeeEntity beeEntity, BeeEntityRenderState beeEntityRenderState, float f) {
		super.updateRenderState(beeEntity, beeEntityRenderState, f);
		beeEntityRenderState.bodyPitch = beeEntity.getBodyPitch(f);
		beeEntityRenderState.hasStinger = !beeEntity.hasStung();
		beeEntityRenderState.stoppedOnGround = beeEntity.isOnGround() && beeEntity.getVelocity().lengthSquared() < 1.0E-7;
		beeEntityRenderState.angry = beeEntity.hasAngerTime();
		beeEntityRenderState.hasNectar = beeEntity.hasNectar();
	}
}
