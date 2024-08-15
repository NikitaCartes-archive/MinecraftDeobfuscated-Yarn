package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingHorseEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.AbstractHorseEntity;

@Environment(EnvType.CLIENT)
public abstract class AbstractHorseEntityRenderer<T extends AbstractHorseEntity, S extends LivingHorseEntityRenderState, M extends EntityModel<? super S>>
	extends AgeableMobEntityRenderer<T, S, M> {
	private final float scale;

	public AbstractHorseEntityRenderer(EntityRendererFactory.Context ctx, M entityModel, M entityModel2, float f) {
		super(ctx, entityModel, entityModel2, 0.75F);
		this.scale = f;
	}

	protected void scale(S livingHorseEntityRenderState, MatrixStack matrixStack) {
		matrixStack.scale(this.scale, this.scale, this.scale);
		super.scale(livingHorseEntityRenderState, matrixStack);
	}

	public void updateRenderState(T abstractHorseEntity, S livingHorseEntityRenderState, float f) {
		super.updateRenderState(abstractHorseEntity, livingHorseEntityRenderState, f);
		livingHorseEntityRenderState.saddled = abstractHorseEntity.isSaddled();
		livingHorseEntityRenderState.hasPassengers = abstractHorseEntity.hasPassengers();
		livingHorseEntityRenderState.eatingGrassAnimationProgress = abstractHorseEntity.getEatingGrassAnimationProgress(f);
		livingHorseEntityRenderState.angryAnimationProgress = abstractHorseEntity.getAngryAnimationProgress(f);
		livingHorseEntityRenderState.eatingAnimationProgress = abstractHorseEntity.getEatingAnimationProgress(f);
		livingHorseEntityRenderState.waggingTail = abstractHorseEntity.tailWagTicks > 0;
	}
}
