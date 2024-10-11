package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.ArmadilloEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.ArmadilloEntityRenderState;
import net.minecraft.entity.passive.ArmadilloEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ArmadilloEntityRenderer extends AgeableMobEntityRenderer<ArmadilloEntity, ArmadilloEntityRenderState, ArmadilloEntityModel> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/armadillo.png");

	public ArmadilloEntityRenderer(EntityRendererFactory.Context context) {
		super(
			context,
			new ArmadilloEntityModel(context.getPart(EntityModelLayers.ARMADILLO)),
			new ArmadilloEntityModel(context.getPart(EntityModelLayers.ARMADILLO_BABY)),
			0.4F
		);
	}

	public Identifier getTexture(ArmadilloEntityRenderState armadilloEntityRenderState) {
		return TEXTURE;
	}

	public ArmadilloEntityRenderState createRenderState() {
		return new ArmadilloEntityRenderState();
	}

	public void updateRenderState(ArmadilloEntity armadilloEntity, ArmadilloEntityRenderState armadilloEntityRenderState, float f) {
		super.updateRenderState(armadilloEntity, armadilloEntityRenderState, f);
		armadilloEntityRenderState.rolledUp = armadilloEntity.isRolledUp();
		armadilloEntityRenderState.scaredAnimationState.copyFrom(armadilloEntity.scaredAnimationState);
		armadilloEntityRenderState.unrollingAnimationState.copyFrom(armadilloEntity.unrollingAnimationState);
		armadilloEntityRenderState.rollingAnimationState.copyFrom(armadilloEntity.rollingAnimationState);
	}
}
