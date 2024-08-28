package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.state.ExperienceOrbEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ExperienceOrbEntityRenderer extends EntityRenderer<ExperienceOrbEntity, ExperienceOrbEntityRenderState> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/experience_orb.png");
	private static final RenderLayer LAYER = RenderLayer.getItemEntityTranslucentCull(TEXTURE);

	public ExperienceOrbEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
		this.shadowRadius = 0.15F;
		this.shadowOpacity = 0.75F;
	}

	protected int getBlockLight(ExperienceOrbEntity experienceOrbEntity, BlockPos blockPos) {
		return MathHelper.clamp(super.getBlockLight(experienceOrbEntity, blockPos) + 7, 0, 15);
	}

	public void render(
		ExperienceOrbEntityRenderState experienceOrbEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i
	) {
		matrixStack.push();
		int j = experienceOrbEntityRenderState.size;
		float f = (float)(j % 4 * 16 + 0) / 64.0F;
		float g = (float)(j % 4 * 16 + 16) / 64.0F;
		float h = (float)(j / 4 * 16 + 0) / 64.0F;
		float k = (float)(j / 4 * 16 + 16) / 64.0F;
		float l = 1.0F;
		float m = 0.5F;
		float n = 0.25F;
		float o = 255.0F;
		float p = experienceOrbEntityRenderState.age / 2.0F;
		int q = (int)((MathHelper.sin(p + 0.0F) + 1.0F) * 0.5F * 255.0F);
		int r = 255;
		int s = (int)((MathHelper.sin(p + (float) (Math.PI * 4.0 / 3.0)) + 1.0F) * 0.1F * 255.0F);
		matrixStack.translate(0.0F, 0.1F, 0.0F);
		matrixStack.multiply(this.dispatcher.getRotation());
		float t = 0.3F;
		matrixStack.scale(0.3F, 0.3F, 0.3F);
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(LAYER);
		MatrixStack.Entry entry = matrixStack.peek();
		vertex(vertexConsumer, entry, -0.5F, -0.25F, q, 255, s, f, k, i);
		vertex(vertexConsumer, entry, 0.5F, -0.25F, q, 255, s, g, k, i);
		vertex(vertexConsumer, entry, 0.5F, 0.75F, q, 255, s, g, h, i);
		vertex(vertexConsumer, entry, -0.5F, 0.75F, q, 255, s, f, h, i);
		matrixStack.pop();
		super.render(experienceOrbEntityRenderState, matrixStack, vertexConsumerProvider, i);
	}

	private static void vertex(
		VertexConsumer vertexConsumer, MatrixStack.Entry matrix, float x, float y, int red, int green, int blue, float u, float v, int light
	) {
		vertexConsumer.vertex(matrix, x, y, 0.0F)
			.color(red, green, blue, 128)
			.texture(u, v)
			.overlay(OverlayTexture.DEFAULT_UV)
			.light(light)
			.normal(matrix, 0.0F, 1.0F, 0.0F);
	}

	public ExperienceOrbEntityRenderState getRenderState() {
		return new ExperienceOrbEntityRenderState();
	}

	public void updateRenderState(ExperienceOrbEntity experienceOrbEntity, ExperienceOrbEntityRenderState experienceOrbEntityRenderState, float f) {
		super.updateRenderState(experienceOrbEntity, experienceOrbEntityRenderState, f);
		experienceOrbEntityRenderState.size = experienceOrbEntity.getOrbSize();
	}
}
