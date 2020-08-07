package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;

@Environment(EnvType.CLIENT)
public class EndCrystalEntityRenderer extends EntityRenderer<EndCrystalEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/end_crystal/end_crystal.png");
	private static final RenderLayer END_CRYSTAL = RenderLayer.getEntityCutoutNoCull(TEXTURE);
	private static final float SINE_45_DEGREES = (float)Math.sin(Math.PI / 4);
	private final ModelPart core;
	private final ModelPart frame;
	private final ModelPart bottom;

	public EndCrystalEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
		this.shadowRadius = 0.5F;
		this.frame = new ModelPart(64, 32, 0, 0);
		this.frame.addCuboid(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F);
		this.core = new ModelPart(64, 32, 32, 0);
		this.core.addCuboid(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F);
		this.bottom = new ModelPart(64, 32, 0, 16);
		this.bottom.addCuboid(-6.0F, 0.0F, -6.0F, 12.0F, 4.0F, 12.0F);
	}

	public void method_3908(EndCrystalEntity endCrystalEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		matrixStack.push();
		float h = getYOffset(endCrystalEntity, g);
		float j = ((float)endCrystalEntity.endCrystalAge + g) * 3.0F;
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(END_CRYSTAL);
		matrixStack.push();
		matrixStack.scale(2.0F, 2.0F, 2.0F);
		matrixStack.translate(0.0, -0.5, 0.0);
		int k = OverlayTexture.DEFAULT_UV;
		if (endCrystalEntity.getShowBottom()) {
			this.bottom.render(matrixStack, vertexConsumer, i, k);
		}

		matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(j));
		matrixStack.translate(0.0, (double)(1.5F + h / 2.0F), 0.0);
		matrixStack.multiply(new Quaternion(new Vector3f(SINE_45_DEGREES, 0.0F, SINE_45_DEGREES), 60.0F, true));
		this.frame.render(matrixStack, vertexConsumer, i, k);
		float l = 0.875F;
		matrixStack.scale(0.875F, 0.875F, 0.875F);
		matrixStack.multiply(new Quaternion(new Vector3f(SINE_45_DEGREES, 0.0F, SINE_45_DEGREES), 60.0F, true));
		matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(j));
		this.frame.render(matrixStack, vertexConsumer, i, k);
		matrixStack.scale(0.875F, 0.875F, 0.875F);
		matrixStack.multiply(new Quaternion(new Vector3f(SINE_45_DEGREES, 0.0F, SINE_45_DEGREES), 60.0F, true));
		matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(j));
		this.core.render(matrixStack, vertexConsumer, i, k);
		matrixStack.pop();
		matrixStack.pop();
		BlockPos blockPos = endCrystalEntity.getBeamTarget();
		if (blockPos != null) {
			float m = (float)blockPos.getX() + 0.5F;
			float n = (float)blockPos.getY() + 0.5F;
			float o = (float)blockPos.getZ() + 0.5F;
			float p = (float)((double)m - endCrystalEntity.getX());
			float q = (float)((double)n - endCrystalEntity.getY());
			float r = (float)((double)o - endCrystalEntity.getZ());
			matrixStack.translate((double)p, (double)q, (double)r);
			EnderDragonEntityRenderer.renderCrystalBeam(-p, -q + h, -r, g, endCrystalEntity.endCrystalAge, matrixStack, vertexConsumerProvider, i);
		}

		super.render(endCrystalEntity, f, g, matrixStack, vertexConsumerProvider, i);
	}

	public static float getYOffset(EndCrystalEntity crystal, float tickDelta) {
		float f = (float)crystal.endCrystalAge + tickDelta;
		float g = MathHelper.sin(f * 0.2F) / 2.0F + 0.5F;
		g = (g * g + g) * 0.4F;
		return g - 1.4F;
	}

	public Identifier method_3909(EndCrystalEntity endCrystalEntity) {
		return TEXTURE;
	}

	public boolean method_3907(EndCrystalEntity endCrystalEntity, Frustum frustum, double d, double e, double f) {
		return super.shouldRender(endCrystalEntity, frustum, d, e, f) || endCrystalEntity.getBeamTarget() != null;
	}
}
