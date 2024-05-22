package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SkeletonOverlayFeatureRenderer<T extends MobEntity & RangedAttackMob, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
	private final SkeletonEntityModel<T> model;
	private final Identifier texture;

	public SkeletonOverlayFeatureRenderer(FeatureRendererContext<T, M> context, EntityModelLoader loader, EntityModelLayer layer, Identifier texture) {
		super(context);
		this.texture = texture;
		this.model = new SkeletonEntityModel<>(loader.getModelPart(layer));
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T mobEntity, float f, float g, float h, float j, float k, float l
	) {
		render(this.getContextModel(), this.model, this.texture, matrixStack, vertexConsumerProvider, i, mobEntity, f, g, j, k, l, h, -1);
	}
}
