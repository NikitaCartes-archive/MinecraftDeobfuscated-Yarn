package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.MobEntity;

@Deprecated
@Environment(EnvType.CLIENT)
public abstract class AgeableMobEntityRenderer<T extends MobEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>>
	extends MobEntityRenderer<T, S, M> {
	private final M adultModel;
	private final M babyModel;

	public AgeableMobEntityRenderer(EntityRendererFactory.Context context, M model, M babyModel, float scale) {
		super(context, model, scale);
		this.adultModel = model;
		this.babyModel = babyModel;
	}

	@Override
	public void render(S livingEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		this.model = livingEntityRenderState.baby ? this.babyModel : this.adultModel;
		super.render(livingEntityRenderState, matrixStack, vertexConsumerProvider, i);
	}
}
