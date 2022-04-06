package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.AbstractRandom;

@Environment(EnvType.CLIENT)
public abstract class StuckObjectsFeatureRenderer<T extends LivingEntity, M extends PlayerEntityModel<T>> extends FeatureRenderer<T, M> {
	public StuckObjectsFeatureRenderer(LivingEntityRenderer<T, M> entityRenderer) {
		super(entityRenderer);
	}

	protected abstract int getObjectCount(T entity);

	protected abstract void renderObject(
		MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Entity entity, float directionX, float directionY, float directionZ, float tickDelta
	);

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l
	) {
		int m = this.getObjectCount(livingEntity);
		AbstractRandom abstractRandom = AbstractRandom.createAtomic((long)livingEntity.getId());
		if (m > 0) {
			for (int n = 0; n < m; n++) {
				matrixStack.push();
				ModelPart modelPart = this.getContextModel().getRandomPart(abstractRandom);
				ModelPart.Cuboid cuboid = modelPart.getRandomCuboid(abstractRandom);
				modelPart.rotate(matrixStack);
				float o = abstractRandom.nextFloat();
				float p = abstractRandom.nextFloat();
				float q = abstractRandom.nextFloat();
				float r = MathHelper.lerp(o, cuboid.minX, cuboid.maxX) / 16.0F;
				float s = MathHelper.lerp(p, cuboid.minY, cuboid.maxY) / 16.0F;
				float t = MathHelper.lerp(q, cuboid.minZ, cuboid.maxZ) / 16.0F;
				matrixStack.translate((double)r, (double)s, (double)t);
				o = -1.0F * (o * 2.0F - 1.0F);
				p = -1.0F * (p * 2.0F - 1.0F);
				q = -1.0F * (q * 2.0F - 1.0F);
				this.renderObject(matrixStack, vertexConsumerProvider, i, livingEntity, o, p, q, h);
				matrixStack.pop();
			}
		}
	}
}
