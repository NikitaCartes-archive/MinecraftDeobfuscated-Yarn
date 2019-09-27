package net.minecraft.client.render.entity.feature;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public abstract class StickingOutThingsFeatureRenderer<T extends LivingEntity, M extends PlayerEntityModel<T>> extends FeatureRenderer<T, M> {
	public StickingOutThingsFeatureRenderer(LivingEntityRenderer<T, M> livingEntityRenderer) {
		super(livingEntityRenderer);
	}

	protected abstract int getThingCount(T livingEntity);

	protected abstract void renderThing(class_4587 arg, class_4597 arg2, Entity entity, float f, float g, float h, float i);

	public void method_22132(class_4587 arg, class_4597 arg2, int i, T livingEntity, float f, float g, float h, float j, float k, float l, float m) {
		int n = this.getThingCount(livingEntity);
		Random random = new Random((long)livingEntity.getEntityId());
		if (n > 0) {
			for (int o = 0; o < n; o++) {
				arg.method_22903();
				ModelPart modelPart = this.getModel().method_22697(random);
				ModelPart.Cuboid cuboid = modelPart.method_22700(random);
				modelPart.method_22703(arg, 0.0625F);
				float p = random.nextFloat();
				float q = random.nextFloat();
				float r = random.nextFloat();
				float s = MathHelper.lerp(p, cuboid.xMin, cuboid.xMax) / 16.0F;
				float t = MathHelper.lerp(q, cuboid.yMin, cuboid.yMax) / 16.0F;
				float u = MathHelper.lerp(r, cuboid.zMin, cuboid.zMax) / 16.0F;
				arg.method_22904((double)s, (double)t, (double)u);
				p = -1.0F * (p * 2.0F - 1.0F);
				q = -1.0F * (q * 2.0F - 1.0F);
				r = -1.0F * (r * 2.0F - 1.0F);
				this.renderThing(arg, arg2, livingEntity, p, q, r, h);
				arg.method_22909();
			}
		}
	}
}
