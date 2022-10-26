package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SalmonEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.SalmonEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class SalmonEntityRenderer extends MobEntityRenderer<SalmonEntity, SalmonEntityModel<SalmonEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/fish/salmon.png");

	public SalmonEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new SalmonEntityModel<>(context.getPart(EntityModelLayers.SALMON)), 0.4F);
	}

	public Identifier getTexture(SalmonEntity salmonEntity) {
		return TEXTURE;
	}

	protected void setupTransforms(SalmonEntity salmonEntity, MatrixStack matrixStack, float f, float g, float h) {
		super.setupTransforms(salmonEntity, matrixStack, f, g, h);
		float i = 1.0F;
		float j = 1.0F;
		if (!salmonEntity.isTouchingWater()) {
			i = 1.3F;
			j = 1.7F;
		}

		float k = i * 4.3F * MathHelper.sin(j * 0.6F * f);
		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(k));
		matrixStack.translate(0.0F, 0.0F, -0.4F);
		if (!salmonEntity.isTouchingWater()) {
			matrixStack.translate(0.2F, 0.1F, 0.0F);
			matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0F));
		}
	}
}
