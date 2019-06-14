package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VisibleRegion;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public abstract class MobEntityRenderer<T extends MobEntity, M extends EntityModel<T>> extends LivingEntityRenderer<T, M> {
	public MobEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, M entityModel, float f) {
		super(entityRenderDispatcher, entityModel, f);
	}

	protected boolean method_4071(T mobEntity) {
		return super.method_4055(mobEntity) && (mobEntity.shouldRenderName() || mobEntity.hasCustomName() && mobEntity == this.renderManager.targetedEntity);
	}

	public boolean method_4068(T mobEntity, VisibleRegion visibleRegion, double d, double e, double f) {
		if (super.isVisible(mobEntity, visibleRegion, d, e, f)) {
			return true;
		} else {
			Entity entity = mobEntity.getHoldingEntity();
			return entity != null ? visibleRegion.intersects(entity.method_5830()) : false;
		}
	}

	public void method_4072(T mobEntity, double d, double e, double f, float g, float h) {
		super.method_4054(mobEntity, d, e, f, g, h);
		if (!this.renderOutlines) {
			this.method_4073(mobEntity, d, e, f, g, h);
		}
	}

	protected void method_4073(T mobEntity, double d, double e, double f, float g, float h) {
		Entity entity = mobEntity.getHoldingEntity();
		if (entity != null) {
			e -= (1.6 - (double)mobEntity.getHeight()) * 0.5;
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
			double i = (double)(MathHelper.lerp(h * 0.5F, entity.yaw, entity.prevYaw) * (float) (Math.PI / 180.0));
			double j = (double)(MathHelper.lerp(h * 0.5F, entity.pitch, entity.prevPitch) * (float) (Math.PI / 180.0));
			double k = Math.cos(i);
			double l = Math.sin(i);
			double m = Math.sin(j);
			if (entity instanceof AbstractDecorationEntity) {
				k = 0.0;
				l = 0.0;
				m = -1.0;
			}

			double n = Math.cos(j);
			double o = MathHelper.lerp((double)h, entity.prevX, entity.x) - k * 0.7 - l * 0.5 * n;
			double p = MathHelper.lerp((double)h, entity.prevY + (double)entity.getStandingEyeHeight() * 0.7, entity.y + (double)entity.getStandingEyeHeight() * 0.7)
				- m * 0.5
				- 0.25;
			double q = MathHelper.lerp((double)h, entity.prevZ, entity.z) - l * 0.7 + k * 0.5 * n;
			double r = (double)(MathHelper.lerp(h, mobEntity.field_6283, mobEntity.field_6220) * (float) (Math.PI / 180.0)) + (Math.PI / 2);
			k = Math.cos(r) * (double)mobEntity.getWidth() * 0.4;
			l = Math.sin(r) * (double)mobEntity.getWidth() * 0.4;
			double s = MathHelper.lerp((double)h, mobEntity.prevX, mobEntity.x) + k;
			double t = MathHelper.lerp((double)h, mobEntity.prevY, mobEntity.y);
			double u = MathHelper.lerp((double)h, mobEntity.prevZ, mobEntity.z) + l;
			d += k;
			f += l;
			double v = (double)((float)(o - s));
			double w = (double)((float)(p - t));
			double x = (double)((float)(q - u));
			GlStateManager.disableTexture();
			GlStateManager.disableLighting();
			GlStateManager.disableCull();
			int y = 24;
			double z = 0.025;
			bufferBuilder.method_1328(5, VertexFormats.field_1576);

			for (int aa = 0; aa <= 24; aa++) {
				float ab = 0.5F;
				float ac = 0.4F;
				float ad = 0.3F;
				if (aa % 2 == 0) {
					ab *= 0.7F;
					ac *= 0.7F;
					ad *= 0.7F;
				}

				float ae = (float)aa / 24.0F;
				bufferBuilder.vertex(d + v * (double)ae + 0.0, e + w * (double)(ae * ae + ae) * 0.5 + (double)((24.0F - (float)aa) / 18.0F + 0.125F), f + x * (double)ae)
					.color(ab, ac, ad, 1.0F)
					.next();
				bufferBuilder.vertex(
						d + v * (double)ae + 0.025, e + w * (double)(ae * ae + ae) * 0.5 + (double)((24.0F - (float)aa) / 18.0F + 0.125F) + 0.025, f + x * (double)ae
					)
					.color(ab, ac, ad, 1.0F)
					.next();
			}

			tessellator.draw();
			bufferBuilder.method_1328(5, VertexFormats.field_1576);

			for (int aa = 0; aa <= 24; aa++) {
				float ab = 0.5F;
				float ac = 0.4F;
				float ad = 0.3F;
				if (aa % 2 == 0) {
					ab *= 0.7F;
					ac *= 0.7F;
					ad *= 0.7F;
				}

				float ae = (float)aa / 24.0F;
				bufferBuilder.vertex(
						d + v * (double)ae + 0.0, e + w * (double)(ae * ae + ae) * 0.5 + (double)((24.0F - (float)aa) / 18.0F + 0.125F) + 0.025, f + x * (double)ae
					)
					.color(ab, ac, ad, 1.0F)
					.next();
				bufferBuilder.vertex(
						d + v * (double)ae + 0.025, e + w * (double)(ae * ae + ae) * 0.5 + (double)((24.0F - (float)aa) / 18.0F + 0.125F), f + x * (double)ae + 0.025
					)
					.color(ab, ac, ad, 1.0F)
					.next();
			}

			tessellator.draw();
			GlStateManager.enableLighting();
			GlStateManager.enableTexture();
			GlStateManager.enableCull();
		}
	}
}
