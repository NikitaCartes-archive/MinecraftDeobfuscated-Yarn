package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public abstract class StickingOutThingsFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
	public StickingOutThingsFeatureRenderer(LivingEntityRenderer<T, M> livingEntityRenderer) {
		super(livingEntityRenderer);
	}

	protected abstract int getThingCount(T livingEntity);

	protected abstract void renderThing(Entity entity, float f, float g, float h, float i);

	protected void beforeRendering(T livingEntity) {
		GuiLighting.disable();
	}

	protected void afterRendering() {
		GuiLighting.enable();
	}

	public void method_22132(T livingEntity, float f, float g, float h, float i, float j, float k, float l) {
		int m = this.getThingCount(livingEntity);
		Random random = new Random((long)livingEntity.getEntityId());
		if (m > 0) {
			this.beforeRendering(livingEntity);

			for (int n = 0; n < m; n++) {
				RenderSystem.pushMatrix();
				ModelPart modelPart = this.getModel().getRandomCuboid(random);
				Cuboid cuboid = (Cuboid)modelPart.cuboids.get(random.nextInt(modelPart.cuboids.size()));
				modelPart.applyTransform(0.0625F);
				float o = random.nextFloat();
				float p = random.nextFloat();
				float q = random.nextFloat();
				float r = MathHelper.lerp(o, cuboid.xMin, cuboid.xMax) / 16.0F;
				float s = MathHelper.lerp(p, cuboid.yMin, cuboid.yMax) / 16.0F;
				float t = MathHelper.lerp(q, cuboid.zMin, cuboid.zMax) / 16.0F;
				RenderSystem.translatef(r, s, t);
				o = -1.0F * (o * 2.0F - 1.0F);
				p = -1.0F * (p * 2.0F - 1.0F);
				q = -1.0F * (q * 2.0F - 1.0F);
				this.renderThing(livingEntity, o, p, q, h);
				RenderSystem.popMatrix();
			}

			this.afterRendering();
		}
	}
}
