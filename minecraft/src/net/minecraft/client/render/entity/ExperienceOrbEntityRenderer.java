package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.Matrix3f;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ExperienceOrbEntityRenderer extends EntityRenderer<ExperienceOrbEntity> {
	private static final Identifier SKIN = new Identifier("textures/entity/experience_orb.png");

	public ExperienceOrbEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
		this.field_4673 = 0.15F;
		this.field_4672 = 0.75F;
	}

	public void method_3966(
		ExperienceOrbEntity experienceOrbEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i
	) {
		matrixStack.push();
		int j = experienceOrbEntity.getOrbSize();
		float h = (float)(j % 4 * 16 + 0) / 64.0F;
		float k = (float)(j % 4 * 16 + 16) / 64.0F;
		float l = (float)(j / 4 * 16 + 0) / 64.0F;
		float m = (float)(j / 4 * 16 + 16) / 64.0F;
		float n = 1.0F;
		float o = 0.5F;
		float p = 0.25F;
		float q = 255.0F;
		float r = ((float)experienceOrbEntity.renderTicks + g) / 2.0F;
		int s = (int)((MathHelper.sin(r + 0.0F) + 1.0F) * 0.5F * 255.0F);
		int t = 255;
		int u = (int)((MathHelper.sin(r + (float) (Math.PI * 4.0 / 3.0)) + 1.0F) * 0.1F * 255.0F);
		matrixStack.translate(0.0, 0.1F, 0.0);
		matrixStack.multiply(this.renderManager.camera.method_23767());
		matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
		float v = 0.3F;
		matrixStack.scale(0.3F, 0.3F, 0.3F);
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(SKIN));
		MatrixStack.Entry entry = matrixStack.peek();
		Matrix4f matrix4f = entry.getModel();
		Matrix3f matrix3f = entry.getNormal();
		method_23171(vertexConsumer, matrix4f, matrix3f, -0.5F, -0.25F, s, 255, u, h, m, i);
		method_23171(vertexConsumer, matrix4f, matrix3f, 0.5F, -0.25F, s, 255, u, k, m, i);
		method_23171(vertexConsumer, matrix4f, matrix3f, 0.5F, 0.75F, s, 255, u, k, l, i);
		method_23171(vertexConsumer, matrix4f, matrix3f, -0.5F, 0.75F, s, 255, u, h, l, i);
		matrixStack.pop();
		super.render(experienceOrbEntity, f, g, matrixStack, vertexConsumerProvider, i);
	}

	private static void method_23171(
		VertexConsumer vertexConsumer, Matrix4f matrix4f, Matrix3f matrix3f, float f, float g, int i, int j, int k, float h, float l, int m
	) {
		vertexConsumer.vertex(matrix4f, f, g, 0.0F)
			.color(i, j, k, 128)
			.texture(h, l)
			.overlay(OverlayTexture.DEFAULT_UV)
			.light(m)
			.method_23763(matrix3f, 0.0F, 1.0F, 0.0F)
			.next();
	}

	public Identifier method_3967(ExperienceOrbEntity experienceOrbEntity) {
		return SKIN;
	}
}
