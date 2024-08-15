package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.client.render.entity.state.SkeletonEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SkeletonOverlayFeatureRenderer<S extends SkeletonEntityRenderState, M extends EntityModel<S>> extends FeatureRenderer<S, M> {
	private final SkeletonEntityModel<S> model;
	private final Identifier texture;

	public SkeletonOverlayFeatureRenderer(FeatureRendererContext<S, M> context, EntityModelLoader loader, EntityModelLayer layer, Identifier texture) {
		super(context);
		this.texture = texture;
		this.model = new SkeletonEntityModel<>(loader.getModelPart(layer));
	}

	public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, S skeletonEntityRenderState, float f, float g) {
		render(this.model, this.texture, matrixStack, vertexConsumerProvider, i, skeletonEntityRenderState, -1);
	}
}
