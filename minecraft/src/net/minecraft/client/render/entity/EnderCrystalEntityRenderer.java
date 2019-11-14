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
import net.minecraft.entity.decoration.EnderCrystalEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;

@Environment(EnvType.CLIENT)
public class EnderCrystalEntityRenderer extends EntityRenderer<EnderCrystalEntity> {
	private static final Identifier SKIN = new Identifier("textures/entity/end_crystal/end_crystal.png");
	public static final float field_21002 = (float)Math.sin(Math.PI / 4);
	private final ModelPart field_21003;
	private final ModelPart field_21004;
	private final ModelPart bottom;

	public EnderCrystalEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
		this.field_4673 = 0.5F;
		this.field_21004 = new ModelPart(64, 32, 0, 0);
		this.field_21004.addCuboid(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F);
		this.field_21003 = new ModelPart(64, 32, 32, 0);
		this.field_21003.addCuboid(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F);
		this.bottom = new ModelPart(64, 32, 0, 16);
		this.bottom.addCuboid(-6.0F, 0.0F, -6.0F, 12.0F, 4.0F, 12.0F);
	}

	public void method_3908(EnderCrystalEntity enderCrystalEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		matrixStack.push();
		float h = method_23155(enderCrystalEntity, g);
		float j = ((float)enderCrystalEntity.field_7034 + g) * 3.0F;
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutoutNoCull(this.method_3909(enderCrystalEntity)));
		matrixStack.push();
		matrixStack.scale(2.0F, 2.0F, 2.0F);
		matrixStack.translate(0.0, -0.5, 0.0);
		int k = OverlayTexture.DEFAULT_UV;
		if (enderCrystalEntity.getShowBottom()) {
			this.bottom.render(matrixStack, vertexConsumer, i, k, null);
		}

		matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(j));
		matrixStack.translate(0.0, (double)(1.5F + h / 2.0F), 0.0);
		matrixStack.multiply(new Quaternion(new Vector3f(field_21002, 0.0F, field_21002), 60.0F, true));
		this.field_21004.render(matrixStack, vertexConsumer, i, k, null);
		float l = 0.875F;
		matrixStack.scale(0.875F, 0.875F, 0.875F);
		matrixStack.multiply(new Quaternion(new Vector3f(field_21002, 0.0F, field_21002), 60.0F, true));
		matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(j));
		this.field_21004.render(matrixStack, vertexConsumer, i, k, null);
		matrixStack.scale(0.875F, 0.875F, 0.875F);
		matrixStack.multiply(new Quaternion(new Vector3f(field_21002, 0.0F, field_21002), 60.0F, true));
		matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(j));
		this.field_21003.render(matrixStack, vertexConsumer, i, k, null);
		matrixStack.pop();
		matrixStack.pop();
		BlockPos blockPos = enderCrystalEntity.getBeamTarget();
		if (blockPos != null) {
			float m = (float)blockPos.getX() + 0.5F;
			float n = (float)blockPos.getY() + 0.5F;
			float o = (float)blockPos.getZ() + 0.5F;
			float p = (float)((double)m - enderCrystalEntity.getX());
			float q = (float)((double)n - enderCrystalEntity.getY());
			float r = (float)((double)o - enderCrystalEntity.getZ());
			matrixStack.translate((double)p, (double)q, (double)r);
			EnderDragonEntityRenderer.renderCrystalBeam(-p, -q + h, -r, g, enderCrystalEntity.field_7034, matrixStack, vertexConsumerProvider, i);
		}

		super.render(enderCrystalEntity, f, g, matrixStack, vertexConsumerProvider, i);
	}

	public static float method_23155(EnderCrystalEntity enderCrystalEntity, float f) {
		float g = (float)enderCrystalEntity.field_7034 + f;
		float h = MathHelper.sin(g * 0.2F) / 2.0F + 0.5F;
		h = (h * h + h) * 0.4F;
		return h - 1.4F;
	}

	public Identifier method_3909(EnderCrystalEntity enderCrystalEntity) {
		return SKIN;
	}

	public boolean method_3907(EnderCrystalEntity enderCrystalEntity, Frustum frustum, double d, double e, double f) {
		return super.isVisible(enderCrystalEntity, frustum, d, e, f) || enderCrystalEntity.getBeamTarget() != null;
	}
}
