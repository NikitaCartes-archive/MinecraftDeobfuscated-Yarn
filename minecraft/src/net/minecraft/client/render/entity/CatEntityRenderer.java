package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.CatCollarFeatureRenderer;
import net.minecraft.client.render.entity.model.CatEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.CatEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class CatEntityRenderer extends AgeableMobEntityRenderer<CatEntity, CatEntityRenderState, CatEntityModel> {
	public CatEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new CatEntityModel(context.getPart(EntityModelLayers.CAT)), new CatEntityModel(context.getPart(EntityModelLayers.CAT_BABY)), 0.4F);
		this.addFeature(new CatCollarFeatureRenderer(this, context.getModelLoader()));
	}

	public Identifier getTexture(CatEntityRenderState catEntityRenderState) {
		return catEntityRenderState.texture;
	}

	public CatEntityRenderState getRenderState() {
		return new CatEntityRenderState();
	}

	public void updateRenderState(CatEntity catEntity, CatEntityRenderState catEntityRenderState, float f) {
		super.updateRenderState(catEntity, catEntityRenderState, f);
		catEntityRenderState.texture = catEntity.getVariant().value().texture();
		catEntityRenderState.inSneakingPose = catEntity.isInSneakingPose();
		catEntityRenderState.sprinting = catEntity.isSprinting();
		catEntityRenderState.inSittingPose = catEntity.isInSittingPose();
		catEntityRenderState.sleepAnimationProgress = catEntity.getSleepAnimationProgress(f);
		catEntityRenderState.tailCurlAnimationProgress = catEntity.getTailCurlAnimationProgress(f);
		catEntityRenderState.headDownAnimationProgress = catEntity.getHeadDownAnimationProgress(f);
		catEntityRenderState.nearSleepingPlayer = catEntity.isNearSleepingPlayer();
		catEntityRenderState.collarColor = catEntity.isTamed() ? catEntity.getCollarColor() : null;
	}

	protected void setupTransforms(CatEntityRenderState catEntityRenderState, MatrixStack matrixStack, float f, float g) {
		super.setupTransforms(catEntityRenderState, matrixStack, f, g);
		float h = catEntityRenderState.sleepAnimationProgress;
		if (h > 0.0F) {
			matrixStack.translate(0.4F * h, 0.15F * h, 0.1F * h);
			matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.lerpAngleDegrees(h, 0.0F, 90.0F)));
			if (catEntityRenderState.nearSleepingPlayer) {
				matrixStack.translate(0.15F * h, 0.0F, 0.0F);
			}
		}
	}
}
