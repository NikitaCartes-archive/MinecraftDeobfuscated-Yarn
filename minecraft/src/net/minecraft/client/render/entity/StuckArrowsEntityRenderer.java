package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_308;
import net.minecraft.client.model.Box;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class StuckArrowsEntityRenderer implements LayerEntityRenderer<LivingEntity> {
	private final LivingEntityRenderer<?> renderer;

	public StuckArrowsEntityRenderer(LivingEntityRenderer<?> livingEntityRenderer) {
		this.renderer = livingEntityRenderer;
	}

	@Override
	public void render(LivingEntity livingEntity, float f, float g, float h, float i, float j, float k, float l) {
		int m = livingEntity.getStuckArrows();
		if (m > 0) {
			Entity entity = new ArrowEntity(livingEntity.world, livingEntity.x, livingEntity.y, livingEntity.z);
			Random random = new Random((long)livingEntity.getEntityId());
			class_308.method_1450();

			for (int n = 0; n < m; n++) {
				GlStateManager.pushMatrix();
				Cuboid cuboid = this.renderer.method_4038().getRandomBox(random);
				Box box = (Box)cuboid.boxes.get(random.nextInt(cuboid.boxes.size()));
				cuboid.method_2847(0.0625F);
				float o = random.nextFloat();
				float p = random.nextFloat();
				float q = random.nextFloat();
				float r = MathHelper.lerp(o, box.field_3645, box.field_3648) / 16.0F;
				float s = MathHelper.lerp(p, box.field_3644, box.field_3647) / 16.0F;
				float t = MathHelper.lerp(q, box.field_3643, box.field_3646) / 16.0F;
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
				this.renderer.getRenderManager().method_3954(entity, 0.0, 0.0, 0.0, 0.0F, h, false);
				GlStateManager.popMatrix();
			}

			class_308.method_1452();
		}
	}

	@Override
	public boolean shouldMergeTextures() {
		return false;
	}
}
