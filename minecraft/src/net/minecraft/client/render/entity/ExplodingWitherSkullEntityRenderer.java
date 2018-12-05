package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.SkullEntityModel;
import net.minecraft.entity.projectile.ExplodingWitherSkullEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ExplodingWitherSkullEntityRenderer extends EntityRenderer<ExplodingWitherSkullEntity> {
	private static final Identifier INVINCIBLE_SKIN = new Identifier("textures/entity/wither/wither_invulnerable.png");
	private static final Identifier SKIN = new Identifier("textures/entity/wither/wither.png");
	private final SkullEntityModel field_4816 = new SkullEntityModel();

	public ExplodingWitherSkullEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	private float method_4158(float f, float g, float h) {
		float i = g - f;

		while (i < -180.0F) {
			i += 360.0F;
		}

		while (i >= 180.0F) {
			i -= 360.0F;
		}

		return f + h * i;
	}

	public void method_4159(ExplodingWitherSkullEntity explodingWitherSkullEntity, double d, double e, double f, float g, float h) {
		GlStateManager.pushMatrix();
		GlStateManager.disableCull();
		float i = this.method_4158(explodingWitherSkullEntity.prevYaw, explodingWitherSkullEntity.yaw, h);
		float j = MathHelper.lerp(h, explodingWitherSkullEntity.prevPitch, explodingWitherSkullEntity.pitch);
		GlStateManager.translatef((float)d, (float)e, (float)f);
		float k = 0.0625F;
		GlStateManager.enableRescaleNormal();
		GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
		GlStateManager.enableAlphaTest();
		this.method_3925(explodingWitherSkullEntity);
		if (this.field_4674) {
			GlStateManager.enableColorMaterial();
			GlStateManager.setupSolidRenderingTextureCombine(this.method_3929(explodingWitherSkullEntity));
		}

		this.field_4816.render(explodingWitherSkullEntity, 0.0F, 0.0F, 0.0F, i, j, 0.0625F);
		if (this.field_4674) {
			GlStateManager.tearDownSolidRenderingTextureCombine();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.popMatrix();
		super.method_3936(explodingWitherSkullEntity, d, e, f, g, h);
	}

	protected Identifier getTexture(ExplodingWitherSkullEntity explodingWitherSkullEntity) {
		return explodingWitherSkullEntity.isCharged() ? INVINCIBLE_SKIN : SKIN;
	}
}
