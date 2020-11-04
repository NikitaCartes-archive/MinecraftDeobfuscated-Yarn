package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5617;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PolarBearEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PolarBearEntityRenderer extends MobEntityRenderer<PolarBearEntity, PolarBearEntityModel<PolarBearEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/bear/polarbear.png");

	public PolarBearEntityRenderer(class_5617.class_5618 arg) {
		super(arg, new PolarBearEntityModel<>(arg.method_32167(EntityModelLayers.POLAR_BEAR)), 0.9F);
	}

	public Identifier getTexture(PolarBearEntity polarBearEntity) {
		return TEXTURE;
	}

	protected void scale(PolarBearEntity polarBearEntity, MatrixStack matrixStack, float f) {
		matrixStack.scale(1.2F, 1.2F, 1.2F);
		super.scale(polarBearEntity, matrixStack, f);
	}
}
