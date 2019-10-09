package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.SalmonEntityModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.passive.SalmonEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class SalmonEntityRenderer extends MobEntityRenderer<SalmonEntity, SalmonEntityModel<SalmonEntity>> {
	private static final Identifier SKIN = new Identifier("textures/entity/fish/salmon.png");

	public SalmonEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new SalmonEntityModel<>(), 0.4F);
	}

	public Identifier method_4101(SalmonEntity salmonEntity) {
		return SKIN;
	}

	protected void method_4100(SalmonEntity salmonEntity, MatrixStack matrixStack, float f, float g, float h) {
		super.setupTransforms(salmonEntity, matrixStack, f, g, h);
		float i = 1.0F;
		float j = 1.0F;
		if (!salmonEntity.isInsideWater()) {
			i = 1.3F;
			j = 1.7F;
		}

		float k = i * 4.3F * MathHelper.sin(j * 0.6F * f);
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(k));
		matrixStack.translate(0.0, 0.0, -0.4F);
		if (!salmonEntity.isInsideWater()) {
			matrixStack.translate(0.2F, 0.1F, 0.0);
			matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(90.0F));
		}
	}
}
