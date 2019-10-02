package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MatrixStack;
import net.minecraft.util.math.Quaternion;

@Environment(EnvType.CLIENT)
public class ConduitBlockEntityRenderer extends BlockEntityRenderer<ConduitBlockEntity> {
	public static final Identifier BASE_TEX = new Identifier("entity/conduit/base");
	public static final Identifier CAGE_TEX = new Identifier("entity/conduit/cage");
	public static final Identifier WIND_TEX = new Identifier("entity/conduit/wind");
	public static final Identifier WIND_VERTICAL_TEX = new Identifier("entity/conduit/wind_vertical");
	public static final Identifier OPEN_EYE_TEX = new Identifier("entity/conduit/open_eye");
	public static final Identifier CLOSED_EYE_TEX = new Identifier("entity/conduit/closed_eye");
	private final ModelPart field_20823 = new ModelPart(8, 8, 0, 0);
	private final ModelPart field_20824;
	private final ModelPart field_20825;
	private final ModelPart field_20826;

	public ConduitBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		super(blockEntityRenderDispatcher);
		this.field_20823.addCuboid(-4.0F, -4.0F, 0.0F, 8.0F, 8.0F, 0.0F, 0.01F);
		this.field_20824 = new ModelPart(64, 32, 0, 0);
		this.field_20824.addCuboid(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F);
		this.field_20825 = new ModelPart(32, 16, 0, 0);
		this.field_20825.addCuboid(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F);
		this.field_20826 = new ModelPart(32, 16, 0, 0);
		this.field_20826.addCuboid(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F);
	}

	public void method_22750(
		ConduitBlockEntity conduitBlockEntity,
		double d,
		double e,
		double f,
		float g,
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage,
		int i
	) {
		float h = (float)conduitBlockEntity.ticks + g;
		VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.SOLID);
		if (!conduitBlockEntity.isActive()) {
			float j = conduitBlockEntity.getRotation(0.0F);
			matrixStack.push();
			matrixStack.translate(0.5, 0.5, 0.5);
			matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(j, true));
			this.field_20825.render(matrixStack, vertexConsumer, 0.0625F, i, this.getSprite(BASE_TEX));
			matrixStack.pop();
		} else {
			VertexConsumer vertexConsumer2 = layeredVertexConsumerStorage.getBuffer(RenderLayer.CUTOUT_MIPPED);
			float k = conduitBlockEntity.getRotation(g) * (180.0F / (float)Math.PI);
			float l = MathHelper.sin(h * 0.1F) / 2.0F + 0.5F;
			l = l * l + l;
			matrixStack.push();
			matrixStack.translate(0.5, (double)(0.3F + l * 0.2F), 0.5);
			Vector3f vector3f = new Vector3f(0.5F, 1.0F, 0.5F);
			vector3f.reciprocal();
			matrixStack.multiply(new Quaternion(vector3f, k, true));
			this.field_20826.render(matrixStack, vertexConsumer2, 0.0625F, i, this.getSprite(CAGE_TEX));
			matrixStack.pop();
			int m = conduitBlockEntity.ticks / 66 % 3;
			matrixStack.push();
			matrixStack.translate(0.5, 0.5, 0.5);
			if (m == 1) {
				matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(90.0F, true));
			} else if (m == 2) {
				matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(90.0F, true));
			}

			Sprite sprite = this.getSprite(m == 1 ? WIND_VERTICAL_TEX : WIND_TEX);
			this.field_20824.render(matrixStack, vertexConsumer2, 0.0625F, i, sprite);
			matrixStack.pop();
			matrixStack.push();
			matrixStack.translate(0.5, 0.5, 0.5);
			matrixStack.scale(0.875F, 0.875F, 0.875F);
			matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(180.0F, true));
			matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(180.0F, true));
			this.field_20824.render(matrixStack, vertexConsumer2, 0.0625F, i, sprite);
			matrixStack.pop();
			Camera camera = this.field_20989.cameraEntity;
			matrixStack.push();
			matrixStack.translate(0.5, (double)(0.3F + l * 0.2F), 0.5);
			matrixStack.scale(0.5F, 0.5F, 0.5F);
			matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(-camera.getYaw(), true));
			matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(camera.getPitch(), true));
			matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(180.0F, true));
			this.field_20823.render(matrixStack, vertexConsumer2, 0.083333336F, i, this.getSprite(conduitBlockEntity.isEyeOpen() ? OPEN_EYE_TEX : CLOSED_EYE_TEX));
			matrixStack.pop();
		}
	}
}
