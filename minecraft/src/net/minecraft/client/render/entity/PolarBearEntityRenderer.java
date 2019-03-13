package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.PolarBearEntityModel;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PolarBearEntityRenderer extends MobEntityRenderer<PolarBearEntity, PolarBearEntityModel<PolarBearEntity>> {
	private static final Identifier field_4766 = new Identifier("textures/entity/bear/polarbear.png");

	public PolarBearEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new PolarBearEntityModel<>(), 0.9F);
	}

	protected Identifier method_4097(PolarBearEntity polarBearEntity) {
		return field_4766;
	}

	protected void method_4099(PolarBearEntity polarBearEntity, float f) {
		GlStateManager.scalef(1.2F, 1.2F, 1.2F);
		super.method_4042(polarBearEntity, f);
	}
}
