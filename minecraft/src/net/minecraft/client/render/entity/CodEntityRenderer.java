package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.CodEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.passive.CodEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class CodEntityRenderer extends MobEntityRenderer<CodEntity, CodEntityModel<CodEntity>> {
	private static final Identifier SKIN = new Identifier("textures/entity/fish/cod.png");

	public CodEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new CodEntityModel<>(), 0.3F);
	}

	public Identifier method_3897(CodEntity codEntity) {
		return SKIN;
	}

	protected void method_3896(CodEntity codEntity, MatrixStack matrixStack, float f, float g, float h) {
		super.setupTransforms(codEntity, matrixStack, f, g, h);
		float i = 4.3F * MathHelper.sin(0.6F * f);
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(i));
		if (!codEntity.isInsideWater()) {
			matrixStack.translate(0.1F, 0.1F, -0.1F);
			matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(90.0F));
		}
	}
}
