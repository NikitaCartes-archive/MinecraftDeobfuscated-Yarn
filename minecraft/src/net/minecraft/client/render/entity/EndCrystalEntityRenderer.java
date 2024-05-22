package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;

@Environment(EnvType.CLIENT)
public class EndCrystalEntityRenderer extends EntityRenderer<EndCrystalEntity> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/end_crystal/end_crystal.png");
	private static final RenderLayer END_CRYSTAL = RenderLayer.getEntityCutoutNoCull(TEXTURE);
	private static final float SINE_45_DEGREES = (float)Math.sin(Math.PI / 4);
	private static final String GLASS = "glass";
	private static final String BASE = "base";
	private final ModelPart core;
	private final ModelPart frame;
	private final ModelPart bottom;

	public EndCrystalEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
		this.shadowRadius = 0.5F;
		ModelPart modelPart = context.getPart(EntityModelLayers.END_CRYSTAL);
		this.frame = modelPart.getChild("glass");
		this.core = modelPart.getChild(EntityModelPartNames.CUBE);
		this.bottom = modelPart.getChild("base");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild("glass", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), ModelTransform.NONE);
		modelPartData.addChild(EntityModelPartNames.CUBE, ModelPartBuilder.create().uv(32, 0).cuboid(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), ModelTransform.NONE);
		modelPartData.addChild("base", ModelPartBuilder.create().uv(0, 16).cuboid(-6.0F, 0.0F, -6.0F, 12.0F, 4.0F, 12.0F), ModelTransform.NONE);
		return TexturedModelData.of(modelData, 64, 32);
	}

	public void render(EndCrystalEntity endCrystalEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		matrixStack.push();
		float h = getYOffset(endCrystalEntity, g);
		float j = ((float)endCrystalEntity.endCrystalAge + g) * 3.0F;
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(END_CRYSTAL);
		matrixStack.push();
		matrixStack.scale(2.0F, 2.0F, 2.0F);
		matrixStack.translate(0.0F, -0.5F, 0.0F);
		int k = OverlayTexture.DEFAULT_UV;
		if (endCrystalEntity.shouldShowBottom()) {
			this.bottom.render(matrixStack, vertexConsumer, i, k);
		}

		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j));
		matrixStack.translate(0.0F, 1.5F + h / 2.0F, 0.0F);
		matrixStack.multiply(new Quaternionf().setAngleAxis((float) (Math.PI / 3), SINE_45_DEGREES, 0.0F, SINE_45_DEGREES));
		this.frame.render(matrixStack, vertexConsumer, i, k);
		float l = 0.875F;
		matrixStack.scale(0.875F, 0.875F, 0.875F);
		matrixStack.multiply(new Quaternionf().setAngleAxis((float) (Math.PI / 3), SINE_45_DEGREES, 0.0F, SINE_45_DEGREES));
		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j));
		this.frame.render(matrixStack, vertexConsumer, i, k);
		matrixStack.scale(0.875F, 0.875F, 0.875F);
		matrixStack.multiply(new Quaternionf().setAngleAxis((float) (Math.PI / 3), SINE_45_DEGREES, 0.0F, SINE_45_DEGREES));
		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j));
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
			matrixStack.translate(p, q, r);
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
