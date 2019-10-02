package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class ExperienceOrbEntityRenderer extends EntityRenderer<ExperienceOrbEntity> {
	private static final Identifier SKIN = new Identifier("textures/entity/experience_orb.png");

	public ExperienceOrbEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
		this.field_4673 = 0.15F;
		this.field_4672 = 0.75F;
	}

	public void method_3966(
		ExperienceOrbEntity experienceOrbEntity,
		double d,
		double e,
		double f,
		float g,
		float h,
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage
	) {
		matrixStack.push();
		int i = experienceOrbEntity.getOrbSize();
		float j = (float)(i % 4 * 16 + 0) / 64.0F;
		float k = (float)(i % 4 * 16 + 16) / 64.0F;
		float l = (float)(i / 4 * 16 + 0) / 64.0F;
		float m = (float)(i / 4 * 16 + 16) / 64.0F;
		float n = 1.0F;
		float o = 0.5F;
		float p = 0.25F;
		float q = 255.0F;
		float r = ((float)experienceOrbEntity.renderTicks + h) / 2.0F;
		int s = (int)((MathHelper.sin(r + 0.0F) + 1.0F) * 0.5F * 255.0F);
		int t = 255;
		int u = (int)((MathHelper.sin(r + (float) (Math.PI * 4.0 / 3.0)) + 1.0F) * 0.1F * 255.0F);
		matrixStack.translate(0.0, 0.1F, 0.0);
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(180.0F - this.renderManager.cameraYaw, true));
		matrixStack.multiply(
			Vector3f.POSITIVE_X.getRotationQuaternion((float)(this.renderManager.gameOptions.perspective == 2 ? -1 : 1) * -this.renderManager.cameraPitch, true)
		);
		float v = 0.3F;
		matrixStack.scale(0.3F, 0.3F, 0.3F);
		int w = experienceOrbEntity.getLightmapCoordinates();
		VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.method_23017(SKIN));
		OverlayTexture.clearDefaultOverlay(vertexConsumer);
		Matrix4f matrix4f = matrixStack.peek();
		method_23171(vertexConsumer, matrix4f, -0.5F, -0.25F, s, 255, u, j, m, w);
		method_23171(vertexConsumer, matrix4f, 0.5F, -0.25F, s, 255, u, k, m, w);
		method_23171(vertexConsumer, matrix4f, 0.5F, 0.75F, s, 255, u, k, l, w);
		method_23171(vertexConsumer, matrix4f, -0.5F, 0.75F, s, 255, u, j, l, w);
		vertexConsumer.clearDefaultOverlay();
		matrixStack.pop();
		super.render(experienceOrbEntity, d, e, f, g, h, matrixStack, layeredVertexConsumerStorage);
	}

	private static void method_23171(VertexConsumer vertexConsumer, Matrix4f matrix4f, float f, float g, int i, int j, int k, float h, float l, int m) {
		vertexConsumer.vertex(matrix4f, f, g, 0.0F).color(i, j, k, 128).texture(h, l).light(m).normal(0.0F, 1.0F, 0.0F).next();
	}

	public Identifier method_3967(ExperienceOrbEntity experienceOrbEntity) {
		return SKIN;
	}
}
