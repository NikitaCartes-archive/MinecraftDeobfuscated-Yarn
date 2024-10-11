package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.FoxHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.FoxEntityModel;
import net.minecraft.client.render.entity.state.FoxEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class FoxEntityRenderer extends AgeableMobEntityRenderer<FoxEntity, FoxEntityRenderState, FoxEntityModel> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/fox/fox.png");
	private static final Identifier SLEEPING_TEXTURE = Identifier.ofVanilla("textures/entity/fox/fox_sleep.png");
	private static final Identifier SNOW_TEXTURE = Identifier.ofVanilla("textures/entity/fox/snow_fox.png");
	private static final Identifier SLEEPING_SNOW_TEXTURE = Identifier.ofVanilla("textures/entity/fox/snow_fox_sleep.png");

	public FoxEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new FoxEntityModel(context.getPart(EntityModelLayers.FOX)), new FoxEntityModel(context.getPart(EntityModelLayers.FOX_BABY)), 0.4F);
		this.addFeature(new FoxHeldItemFeatureRenderer(this, context.getItemRenderer()));
	}

	protected void setupTransforms(FoxEntityRenderState foxEntityRenderState, MatrixStack matrixStack, float f, float g) {
		super.setupTransforms(foxEntityRenderState, matrixStack, f, g);
		if (foxEntityRenderState.chasing || foxEntityRenderState.walking) {
			matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-foxEntityRenderState.pitch));
		}
	}

	public Identifier getTexture(FoxEntityRenderState foxEntityRenderState) {
		if (foxEntityRenderState.type == FoxEntity.Type.RED) {
			return foxEntityRenderState.sleeping ? SLEEPING_TEXTURE : TEXTURE;
		} else {
			return foxEntityRenderState.sleeping ? SLEEPING_SNOW_TEXTURE : SNOW_TEXTURE;
		}
	}

	public FoxEntityRenderState createRenderState() {
		return new FoxEntityRenderState();
	}

	public void updateRenderState(FoxEntity foxEntity, FoxEntityRenderState foxEntityRenderState, float f) {
		super.updateRenderState(foxEntity, foxEntityRenderState, f);
		foxEntityRenderState.headRoll = foxEntity.getHeadRoll(f);
		foxEntityRenderState.inSneakingPose = foxEntity.isInSneakingPose();
		foxEntityRenderState.bodyRotationHeightOffset = foxEntity.getBodyRotationHeightOffset(f);
		foxEntityRenderState.sleeping = foxEntity.isSleeping();
		foxEntityRenderState.sitting = foxEntity.isSitting();
		foxEntityRenderState.walking = foxEntity.isWalking();
		foxEntityRenderState.chasing = foxEntity.isChasing();
		foxEntityRenderState.type = foxEntity.getVariant();
	}
}
