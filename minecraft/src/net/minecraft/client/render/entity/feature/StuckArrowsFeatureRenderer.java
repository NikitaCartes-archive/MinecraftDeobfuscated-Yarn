package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Box;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class StuckArrowsFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
	private final EntityRenderDispatcher field_17153;

	public StuckArrowsFeatureRenderer(LivingEntityRenderer<T, M> livingEntityRenderer) {
		super(livingEntityRenderer);
		this.field_17153 = livingEntityRenderer.getRenderManager();
	}

	public void method_17158(T livingEntity, float f, float g, float h, float i, float j, float k, float l) {
		int m = livingEntity.getStuckArrows();
		if (m > 0) {
			Entity entity = new ArrowEntity(livingEntity.field_6002, livingEntity.x, livingEntity.y, livingEntity.z);
			Random random = new Random((long)livingEntity.getEntityId());
			GuiLighting.disable();

			for (int n = 0; n < m; n++) {
				GlStateManager.pushMatrix();
				Cuboid cuboid = this.getModel().method_17101(random);
				Box box = (Box)cuboid.boxes.get(random.nextInt(cuboid.boxes.size()));
				cuboid.applyTransform(0.0625F);
				float o = random.nextFloat();
				float p = random.nextFloat();
				float q = random.nextFloat();
				float r = MathHelper.lerp(o, box.xMin, box.xMax) / 16.0F;
				float s = MathHelper.lerp(p, box.yMin, box.yMax) / 16.0F;
				float t = MathHelper.lerp(q, box.zMin, box.zMax) / 16.0F;
				GlStateManager.translatef(r, s, t);
				o = o * 2.0F - 1.0F;
				p = p * 2.0F - 1.0F;
				q = q * 2.0F - 1.0F;
				o *= -1.0F;
				p *= -1.0F;
				q *= -1.0F;
				float u = MathHelper.sqrt(o * o + q * q);
				entity.yaw = (float)(Math.atan2((double)o, (double)q) * 180.0F / (float)Math.PI);
				entity.pitch = (float)(Math.atan2((double)p, (double)u) * 180.0F / (float)Math.PI);
				entity.prevYaw = entity.yaw;
				entity.prevPitch = entity.pitch;
				double d = 0.0;
				double e = 0.0;
				double v = 0.0;
				this.field_17153.render(entity, 0.0, 0.0, 0.0, 0.0F, h, false);
				GlStateManager.popMatrix();
			}

			GuiLighting.enable();
		}
	}

	@Override
	public boolean method_4200() {
		return false;
	}
}
