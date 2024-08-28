package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.ArrowEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.ProjectileEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public abstract class ProjectileEntityRenderer<T extends PersistentProjectileEntity, S extends ProjectileEntityRenderState> extends EntityRenderer<T, S> {
	private final ArrowEntityModel model;

	public ProjectileEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
		this.model = new ArrowEntityModel(context.getPart(EntityModelLayers.ARROW));
	}

	public void render(S projectileEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		matrixStack.push();
		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(projectileEntityRenderState.yaw - 90.0F));
		matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(projectileEntityRenderState.pitch));
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutout(this.getTexture(projectileEntityRenderState)));
		this.model.setAngles(projectileEntityRenderState);
		this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
		matrixStack.pop();
		super.render(projectileEntityRenderState, matrixStack, vertexConsumerProvider, i);
	}

	protected abstract Identifier getTexture(S state);

	public void updateRenderState(T persistentProjectileEntity, S projectileEntityRenderState, float f) {
		super.updateRenderState(persistentProjectileEntity, projectileEntityRenderState, f);
		projectileEntityRenderState.pitch = persistentProjectileEntity.getLerpedPitch(f);
		projectileEntityRenderState.yaw = persistentProjectileEntity.getLerpedYaw(f);
		projectileEntityRenderState.shake = (float)persistentProjectileEntity.shake - f;
	}
}
