package net.minecraft.client.render.entity.feature;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public abstract class StickingOutThingsFeatureRenderer<T extends LivingEntity, M extends PlayerEntityModel<T>> extends FeatureRenderer<T, M> {
	public StickingOutThingsFeatureRenderer(LivingEntityRenderer<T, M> livingEntityRenderer) {
		super(livingEntityRenderer);
	}

	protected abstract int getThingCount(T livingEntity);

	protected abstract void renderThing(
		MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, Entity entity, float f, float g, float h, float i
	);

	public void method_22132(
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage,
		int i,
		T livingEntity,
		float f,
		float g,
		float h,
		float j,
		float k,
		float l,
		float m
	) {
		int n = this.getThingCount(livingEntity);
		Random random = new Random((long)livingEntity.getEntityId());
		if (n > 0) {
			for (int o = 0; o < n; o++) {
				matrixStack.push();
				ModelPart modelPart = this.getModel().getRandomPart(random);
				ModelPart.Cuboid cuboid = modelPart.getRandomCuboid(random);
				modelPart.rotate(matrixStack, 0.0625F);
				float p = random.nextFloat();
				float q = random.nextFloat();
				float r = random.nextFloat();
				float s = MathHelper.lerp(p, cuboid.minX, cuboid.maxX) / 16.0F;
				float t = MathHelper.lerp(q, cuboid.minY, cuboid.maxY) / 16.0F;
				float u = MathHelper.lerp(r, cuboid.minZ, cuboid.maxZ) / 16.0F;
				matrixStack.translate((double)s, (double)t, (double)u);
				p = -1.0F * (p * 2.0F - 1.0F);
				q = -1.0F * (q * 2.0F - 1.0F);
				r = -1.0F * (r * 2.0F - 1.0F);
				this.renderThing(matrixStack, layeredVertexConsumerStorage, livingEntity, p, q, r, h);
				matrixStack.pop();
			}
		}
	}
}
