package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5617;
import net.minecraft.client.render.entity.model.CodEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.passive.CodEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class CodEntityRenderer extends MobEntityRenderer<CodEntity, CodEntityModel<CodEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/fish/cod.png");

	public CodEntityRenderer(class_5617.class_5618 arg) {
		super(arg, new CodEntityModel<>(arg.method_32167(EntityModelLayers.COD)), 0.3F);
	}

	public Identifier getTexture(CodEntity codEntity) {
		return TEXTURE;
	}

	protected void setupTransforms(CodEntity codEntity, MatrixStack matrixStack, float f, float g, float h) {
		super.setupTransforms(codEntity, matrixStack, f, g, h);
		float i = 4.3F * MathHelper.sin(0.6F * f);
		matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(i));
		if (!codEntity.isTouchingWater()) {
			matrixStack.translate(0.1F, 0.1F, -0.1F);
			matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(90.0F));
		}
	}
}
