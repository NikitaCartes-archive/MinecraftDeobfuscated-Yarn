package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.WitherEntityModel;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class WitherEntityRenderer extends EntityMobRenderer<EntityWither> {
	private static final Identifier INVINCIBLE_SKIN = new Identifier("textures/entity/wither/wither_invulnerable.png");
	private static final Identifier SKIN = new Identifier("textures/entity/wither/wither.png");

	public WitherEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new WitherEntityModel(0.0F), 1.0F);
		this.addLayer(new WitherArmorEntityRenderer(this));
	}

	protected Identifier getTexture(EntityWither entityWither) {
		int i = entityWither.getInvulTimer();
		return i > 0 && (i > 80 || i / 5 % 2 != 1) ? INVINCIBLE_SKIN : SKIN;
	}

	protected void method_4152(EntityWither entityWither, float f) {
		float g = 2.0F;
		int i = entityWither.getInvulTimer();
		if (i > 0) {
			g -= ((float)i - f) / 220.0F * 0.5F;
		}

		GlStateManager.scalef(g, g, g);
	}
}
