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
public class StuckArrowsFeatureRenderer<T extends LivingEntity, M extends PlayerEntityModel<T>> extends StickingOutThingsFeatureRenderer<T, M> {
	private final EntityRenderDispatcher field_17153;
	private ArrowEntity field_20528;

	public StuckArrowsFeatureRenderer(LivingEntityRenderer<T, M> livingEntityRenderer) {
		super(livingEntityRenderer);
		this.field_17153 = livingEntityRenderer.getRenderManager();
	}

	@Override
	protected int getThingCount(T entity) {
		return entity.getStuckArrowCount();
	}

	@Override
	protected void renderThing(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, Entity entity, float z, float tickDelta, float f, float g) {
		float h = MathHelper.sqrt(z * z + f * f);
		this.field_20528 = new ArrowEntity(entity.world, entity.getX(), entity.getY(), entity.getZ());
		this.field_20528.yaw = (float)(Math.atan2((double)z, (double)f) * 180.0F / (float)Math.PI);
		this.field_20528.pitch = (float)(Math.atan2((double)tickDelta, (double)h) * 180.0F / (float)Math.PI);
		this.field_20528.prevYaw = this.field_20528.yaw;
		this.field_20528.prevPitch = this.field_20528.pitch;
		this.field_17153.render(this.field_20528, 0.0, 0.0, 0.0, 0.0F, g, matrixStack, vertexConsumerProvider);
	}
}
