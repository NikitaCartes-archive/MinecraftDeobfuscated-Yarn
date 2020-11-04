package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5603;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.class_5617;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
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

	public EndCrystalEntityRenderer(class_5617.class_5618 arg) {
		super(arg);
		this.shadowRadius = 0.5F;
		ModelPart modelPart = arg.method_32167(EntityModelLayers.END_CRYSTAL);
		this.frame = modelPart.method_32086("glass");
		this.core = modelPart.method_32086("cube");
		this.bottom = modelPart.method_32086("base");
	}

	public static class_5607 method_32164() {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		lv2.method_32117("glass", class_5606.method_32108().method_32101(0, 0).method_32097(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), class_5603.field_27701);
		lv2.method_32117("cube", class_5606.method_32108().method_32101(32, 0).method_32097(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), class_5603.field_27701);
		lv2.method_32117("base", class_5606.method_32108().method_32101(0, 16).method_32097(-6.0F, 0.0F, -6.0F, 12.0F, 4.0F, 12.0F), class_5603.field_27701);
		return class_5607.method_32110(lv, 64, 32);
	}

	public void render(EndCrystalEntity endCrystalEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
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

	public Identifier getTexture(EndCrystalEntity endCrystalEntity) {
		return TEXTURE;
	}

	public boolean shouldRender(EndCrystalEntity endCrystalEntity, Frustum frustum, double d, double e, double f) {
		return super.shouldRender(endCrystalEntity, frustum, d, e, f) || endCrystalEntity.getBeamTarget() != null;
	}
}
