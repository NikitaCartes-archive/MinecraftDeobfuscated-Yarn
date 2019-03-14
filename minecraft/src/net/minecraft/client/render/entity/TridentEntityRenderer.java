package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class TridentEntityRenderer extends EntityRenderer<TridentEntity> {
	public static final Identifier field_4796 = new Identifier("textures/entity/trident.png");
	private final TridentEntityModel model = new TridentEntityModel();

	public TridentEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	public void method_4133(TridentEntity tridentEntity, double d, double e, double f, float g, float h) {
		this.bindEntityTexture(tridentEntity);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.pushMatrix();
		GlStateManager.disableLighting();
		GlStateManager.translatef((float)d, (float)e, (float)f);
		GlStateManager.rotatef(MathHelper.lerp(h, tridentEntity.prevYaw, tridentEntity.yaw) - 90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(MathHelper.lerp(h, tridentEntity.prevPitch, tridentEntity.pitch) + 90.0F, 0.0F, 0.0F, 1.0F);
		this.model.renderItem();
		GlStateManager.popMatrix();
		this.method_4131(tridentEntity, d, e, f, g, h);
		super.render(tridentEntity, d, e, f, g, h);
		GlStateManager.enableLighting();
	}

	protected Identifier method_4134(TridentEntity tridentEntity) {
		return field_4796;
	}

	protected void method_4131(TridentEntity tridentEntity, double d, double e, double f, float g, float h) {
		Entity entity = tridentEntity.getOwner();
		if (entity != null && tridentEntity.isNoClip()) {
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
			double i = (double)(MathHelper.lerp(h * 0.5F, entity.yaw, entity.prevYaw) * (float) (Math.PI / 180.0));
			double j = Math.cos(i);
			double k = Math.sin(i);
			double l = MathHelper.lerp((double)h, entity.prevX, entity.x);
			double m = MathHelper.lerp((double)h, entity.prevY + (double)entity.getStandingEyeHeight() * 0.8, entity.y + (double)entity.getStandingEyeHeight() * 0.8);
			double n = MathHelper.lerp((double)h, entity.prevZ, entity.z);
			double o = j - k;
			double p = k + j;
			double q = MathHelper.lerp((double)h, tridentEntity.prevX, tridentEntity.x);
			double r = MathHelper.lerp((double)h, tridentEntity.prevY, tridentEntity.y);
			double s = MathHelper.lerp((double)h, tridentEntity.prevZ, tridentEntity.z);
			double t = (double)((float)(l - q));
			double u = (double)((float)(m - r));
			double v = (double)((float)(n - s));
			double w = Math.sqrt(t * t + u * u + v * v);
			int x = tridentEntity.getEntityId() + tridentEntity.age;
			double y = (double)((float)x + h) * -0.1;
			double z = Math.min(0.5, w / 30.0);
			GlStateManager.disableTexture();
			GlStateManager.disableLighting();
			GlStateManager.disableCull();
			GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 255.0F, 255.0F);
			bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);
			int aa = 37;
			int ab = 7 - x % 7;
			double ac = 0.1;

			for (int ad = 0; ad <= 37; ad++) {
				double ae = (double)ad / 37.0;
				float af = 1.0F - (float)((ad + ab) % 7) / 7.0F;
				double ag = ae * 2.0 - 1.0;
				ag = (1.0 - ag * ag) * z;
				double ah = d + t * ae + Math.sin(ae * Math.PI * 8.0 + y) * o * ag;
				double ai = e + u * ae + Math.cos(ae * Math.PI * 8.0 + y) * 0.02 + (0.1 + ag) * 1.0;
				double aj = f + v * ae + Math.sin(ae * Math.PI * 8.0 + y) * p * ag;
				float ak = 0.87F * af + 0.3F * (1.0F - af);
				float al = 0.91F * af + 0.6F * (1.0F - af);
				float am = 0.85F * af + 0.5F * (1.0F - af);
				bufferBuilder.vertex(ah, ai, aj).color(ak, al, am, 1.0F).next();
				bufferBuilder.vertex(ah + 0.1 * ag, ai + 0.1 * ag, aj).color(ak, al, am, 1.0F).next();
				if (ad > tridentEntity.field_7649 * 2) {
					break;
				}
			}

			tessellator.draw();
			bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);

			for (int adx = 0; adx <= 37; adx++) {
				double ae = (double)adx / 37.0;
				float af = 1.0F - (float)((adx + ab) % 7) / 7.0F;
				double ag = ae * 2.0 - 1.0;
				ag = (1.0 - ag * ag) * z;
				double ah = d + t * ae + Math.sin(ae * Math.PI * 8.0 + y) * o * ag;
				double ai = e + u * ae + Math.cos(ae * Math.PI * 8.0 + y) * 0.01 + (0.1 + ag) * 1.0;
				double aj = f + v * ae + Math.sin(ae * Math.PI * 8.0 + y) * p * ag;
				float ak = 0.87F * af + 0.3F * (1.0F - af);
				float al = 0.91F * af + 0.6F * (1.0F - af);
				float am = 0.85F * af + 0.5F * (1.0F - af);
				bufferBuilder.vertex(ah, ai, aj).color(ak, al, am, 1.0F).next();
				bufferBuilder.vertex(ah + 0.1 * ag, ai, aj + 0.1 * ag).color(ak, al, am, 1.0F).next();
				if (adx > tridentEntity.field_7649 * 2) {
					break;
				}
			}

			tessellator.draw();
			GlStateManager.enableLighting();
			GlStateManager.enableTexture();
			GlStateManager.enableCull();
		}
	}
}
