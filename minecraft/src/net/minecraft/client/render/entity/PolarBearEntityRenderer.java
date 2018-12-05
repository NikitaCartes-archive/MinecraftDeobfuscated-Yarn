package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.PolarBearEntityModel;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PolarBearEntityRenderer extends EntityMobRenderer<PolarBearEntity> {
	private static final Identifier SKIN = new Identifier("textures/entity/bear/polarbear.png");

	public PolarBearEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new PolarBearEntityModel(), 0.7F);
	}

	protected Identifier getTexture(PolarBearEntity polarBearEntity) {
		return SKIN;
	}

	public void method_4098(PolarBearEntity polarBearEntity, double d, double e, double f, float g, float h) {
		super.method_4072(polarBearEntity, d, e, f, g, h);
	}

	protected void method_4099(PolarBearEntity polarBearEntity, float f) {
		GlStateManager.scalef(1.2F, 1.2F, 1.2F);
		super.method_4042(polarBearEntity, f);
	}
}
