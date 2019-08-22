package net.minecraft.client.render.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.ShulkerBulletEntityModel;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ShulkerBulletEntityRenderer extends EntityRenderer<ShulkerBulletEntity> {
	private static final Identifier SKIN = new Identifier("textures/entity/shulker/spark.png");
	private final ShulkerBulletEntityModel<ShulkerBulletEntity> model = new ShulkerBulletEntityModel<>();

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
		RenderSystem.pushMatrix();
		float i = this.method_4104(shulkerBulletEntity.prevYaw, shulkerBulletEntity.yaw, h);
		float j = MathHelper.lerp(h, shulkerBulletEntity.prevPitch, shulkerBulletEntity.pitch);
		float k = (float)shulkerBulletEntity.age + h;
		RenderSystem.translatef((float)d, (float)e + 0.15F, (float)f);
		RenderSystem.rotatef(MathHelper.sin(k * 0.1F) * 180.0F, 0.0F, 1.0F, 0.0F);
		RenderSystem.rotatef(MathHelper.cos(k * 0.1F) * 180.0F, 1.0F, 0.0F, 0.0F);
		RenderSystem.rotatef(MathHelper.sin(k * 0.15F) * 360.0F, 0.0F, 0.0F, 1.0F);
		float l = 0.03125F;
		RenderSystem.enableRescaleNormal();
		RenderSystem.scalef(-1.0F, -1.0F, 1.0F);
		this.bindEntityTexture(shulkerBulletEntity);
		this.model.render(shulkerBulletEntity, 0.0F, 0.0F, 0.0F, i, j, 0.03125F);
		RenderSystem.enableBlend();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 0.5F);
		RenderSystem.scalef(1.5F, 1.5F, 1.5F);
		this.model.render(shulkerBulletEntity, 0.0F, 0.0F, 0.0F, i, j, 0.03125F);
		RenderSystem.disableBlend();
		RenderSystem.popMatrix();
		super.render(shulkerBulletEntity, d, e, f, g, h);
	}

	protected Identifier method_4105(ShulkerBulletEntity shulkerBulletEntity) {
		return SKIN;
	}
}
