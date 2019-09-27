package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.client.render.entity.model.PolarBearEntityModel;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PolarBearEntityRenderer extends MobEntityRenderer<PolarBearEntity, PolarBearEntityModel<PolarBearEntity>> {
	private static final Identifier SKIN = new Identifier("textures/entity/bear/polarbear.png");

	public PolarBearEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new PolarBearEntityModel<>(), 0.9F);
	}

	public Identifier method_4097(PolarBearEntity polarBearEntity) {
		return SKIN;
	}

	protected void method_4099(PolarBearEntity polarBearEntity, class_4587 arg, float f) {
		arg.method_22905(1.2F, 1.2F, 1.2F);
		super.scale(polarBearEntity, arg, f);
	}
}
