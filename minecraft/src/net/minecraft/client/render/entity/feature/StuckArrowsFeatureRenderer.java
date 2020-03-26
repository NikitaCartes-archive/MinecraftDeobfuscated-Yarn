package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class StuckArrowsFeatureRenderer<T extends LivingEntity, M extends PlayerEntityModel<T>> extends StuckObjectsFeatureRenderer<T, M> {
	private final EntityRenderDispatcher dispatcher;
	private ArrowEntity arrow;

	public StuckArrowsFeatureRenderer(LivingEntityRenderer<T, M> livingEntityRenderer) {
		super(livingEntityRenderer);
		this.dispatcher = livingEntityRenderer.getRenderManager();
	}

	@Override
	protected int getObjectCount(T entity) {
		return entity.getStuckArrowCount();
	}

	@Override
	protected void renderObject(
		MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Entity entity, float directionX, float directionY, float directionZ, float tickDelta
	) {
		float f = MathHelper.sqrt(directionX * directionX + directionZ * directionZ);
		this.arrow = new ArrowEntity(entity.world, entity.getX(), entity.getY(), entity.getZ());
		this.arrow.yaw = (float)(Math.atan2((double)directionX, (double)directionZ) * 180.0F / (float)Math.PI);
		this.arrow.pitch = (float)(Math.atan2((double)directionY, (double)f) * 180.0F / (float)Math.PI);
		this.arrow.prevYaw = this.arrow.yaw;
		this.arrow.prevPitch = this.arrow.pitch;
		this.dispatcher.render(this.arrow, 0.0, 0.0, 0.0, 0.0F, tickDelta, matrices, vertexConsumers, light);
	}
}
