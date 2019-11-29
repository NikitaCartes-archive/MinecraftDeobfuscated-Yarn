package net.minecraft.client.render.entity.feature;

import java.util.Random;
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

@Environment(EnvType.CLIENT)
public abstract class StickingOutThingsFeatureRenderer<T extends LivingEntity, M extends PlayerEntityModel<T>> extends FeatureRenderer<T, M> {
	public StickingOutThingsFeatureRenderer(LivingEntityRenderer<T, M> entityRenderer) {
		super(entityRenderer);
	}

	protected abstract int getThingCount(T entity);

	protected abstract void renderThing(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, Entity entity, float tickDelta, float f, float g, float h
	);

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l
	) {
		int m = this.getThingCount(livingEntity);
		Random random = new Random((long)livingEntity.getEntityId());
		if (m > 0) {
			for (int n = 0; n < m; n++) {
				matrixStack.push();
				ModelPart modelPart = this.getModel().getRandomPart(random);
				ModelPart.Cuboid cuboid = modelPart.getRandomCuboid(random);
				modelPart.rotate(matrixStack);
				float o = random.nextFloat();
				float p = random.nextFloat();
				float q = random.nextFloat();
				float r = MathHelper.lerp(o, cuboid.minX, cuboid.maxX) / 16.0F;
				float s = MathHelper.lerp(p, cuboid.minY, cuboid.maxY) / 16.0F;
				float t = MathHelper.lerp(q, cuboid.minZ, cuboid.maxZ) / 16.0F;
				matrixStack.translate((double)r, (double)s, (double)t);
				o = -1.0F * (o * 2.0F - 1.0F);
				p = -1.0F * (p * 2.0F - 1.0F);
				q = -1.0F * (q * 2.0F - 1.0F);
				this.renderThing(matrixStack, vertexConsumerProvider, i, livingEntity, o, p, q, h);
				matrixStack.pop();
			}
		}
	}
}
