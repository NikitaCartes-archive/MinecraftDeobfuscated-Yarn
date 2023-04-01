package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.BeretFeatureRenderer;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.feature.MustacheFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public abstract class BipedEntityRenderer<T extends MobEntity, M extends BipedEntityModel<T>> extends MobEntityRenderer<T, M> {
	public BipedEntityRenderer(EntityRendererFactory.Context ctx, M model, float shadowRadius) {
		this(ctx, model, shadowRadius, 1.0F, 1.0F, 1.0F);
	}

	public BipedEntityRenderer(EntityRendererFactory.Context ctx, M model, float shadowRadius, float scaleX, float scaleY, float scaleZ) {
		super(ctx, model, shadowRadius);
		this.addFeature(new HeadFeatureRenderer<>(this, ctx.getModelLoader(), scaleX, scaleY, scaleZ, ctx.getHeldItemRenderer()));
		this.addFeature(new ElytraFeatureRenderer<>(this, ctx.getModelLoader()));
		this.addFeature(new HeldItemFeatureRenderer<>(this, ctx.getHeldItemRenderer()));
		this.addFeature(new BeretFeatureRenderer<>(this, ctx.getModelLoader()));
		this.addFeature(new MustacheFeatureRenderer<>(this, ctx.getModelLoader()));
	}

	@Override
	public void render(T mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		this.setupPoseAndVisibility(mobEntity);
		super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
	}

	private void setupPoseAndVisibility(T entity) {
		this.model.setupPoseAndVisibility(entity);
	}

	public Vec3d getPositionOffset(T mobEntity, float f) {
		return mobEntity.isInSneakingPose() ? new Vec3d(0.0, -0.125, 0.0) : super.getPositionOffset(mobEntity, f);
	}
}
