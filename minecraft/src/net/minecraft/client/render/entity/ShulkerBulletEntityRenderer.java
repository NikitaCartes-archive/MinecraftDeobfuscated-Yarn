package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.ShulkerBulletEntityModel;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ShulkerBulletEntityRenderer extends EntityRenderer<ShulkerBulletEntity> {
	private static final Identifier SKIN = new Identifier("textures/entity/shulker/spark.png");
	private final ShulkerBulletEntityModel<ShulkerBulletEntity> field_4777 = new ShulkerBulletEntityModel<>();

	public ShulkerBulletEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	private float method_4104(float f, float g, float h) {
		float i = g - f;

		while (i < -180.0F) {
			i += 360.0F;
		}

		while (i >= 180.0F) {
			i -= 360.0F;
		}

		return f + h * i;
	}

	public void method_4103(ShulkerBulletEntity shulkerBulletEntity, double d, double e, double f, float g, float h) {
		GlStateManager.pushMatrix();
		float i = this.method_4104(shulkerBulletEntity.prevYaw, shulkerBulletEntity.yaw, h);
		float j = MathHelper.lerp(h, shulkerBulletEntity.prevPitch, shulkerBulletEntity.pitch);
		float k = (float)shulkerBulletEntity.age + h;
		GlStateManager.translatef((float)d, (float)e + 0.15F, (float)f);
		GlStateManager.rotatef(MathHelper.sin(k * 0.1F) * 180.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(MathHelper.cos(k * 0.1F) * 180.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotatef(MathHelper.sin(k * 0.15F) * 360.0F, 0.0F, 0.0F, 1.0F);
		float l = 0.03125F;
		GlStateManager.enableRescaleNormal();
		GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
		this.bindEntityTexture(shulkerBulletEntity);
		this.field_4777.render(shulkerBulletEntity, 0.0F, 0.0F, 0.0F, i, j, 0.03125F);
		GlStateManager.enableBlend();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.5F);
		GlStateManager.scalef(1.5F, 1.5F, 1.5F);
		this.field_4777.render(shulkerBulletEntity, 0.0F, 0.0F, 0.0F, i, j, 0.03125F);
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
		super.render(shulkerBulletEntity, d, e, f, g, h);
	}

	protected Identifier getTexture(ShulkerBulletEntity shulkerBulletEntity) {
		return SKIN;
	}
}
