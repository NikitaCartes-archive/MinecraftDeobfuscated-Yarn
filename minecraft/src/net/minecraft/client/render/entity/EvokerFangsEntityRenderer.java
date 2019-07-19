package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EvokerFangsEntityModel;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class EvokerFangsEntityRenderer extends EntityRenderer<EvokerFangsEntity> {
	private static final Identifier SKIN = new Identifier("textures/entity/illager/evoker_fangs.png");
	private final EvokerFangsEntityModel<EvokerFangsEntity> model = new EvokerFangsEntityModel<>();

	public EvokerFangsEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	public void render(EvokerFangsEntity model, double x, double y, double d, float f, float g) {
		float h = model.getAnimationProgress(g);
		if (h != 0.0F) {
			float i = 2.0F;
			if (h > 0.9F) {
				i = (float)((double)i * ((1.0 - (double)h) / 0.1F));
			}

			GlStateManager.pushMatrix();
			GlStateManager.disableCull();
			GlStateManager.enableAlphaTest();
			this.bindEntityTexture(model);
			GlStateManager.translatef((float)x, (float)y, (float)d);
			GlStateManager.rotatef(90.0F - model.yaw, 0.0F, 1.0F, 0.0F);
			GlStateManager.scalef(-i, -i, i);
			float j = 0.03125F;
			GlStateManager.translatef(0.0F, -0.626F, 0.0F);
			this.model.render(model, h, 0.0F, 0.0F, model.yaw, model.pitch, 0.03125F);
			GlStateManager.popMatrix();
			GlStateManager.enableCull();
			super.render(model, x, y, d, f, g);
		}
	}

	protected Identifier getTexture(EvokerFangsEntity evokerFangsEntity) {
		return SKIN;
	}
}
