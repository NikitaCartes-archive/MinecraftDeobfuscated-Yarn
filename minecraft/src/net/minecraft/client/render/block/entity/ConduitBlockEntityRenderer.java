package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.Dilation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;

@Environment(EnvType.CLIENT)
public class ConduitBlockEntityRenderer implements BlockEntityRenderer<ConduitBlockEntity> {
	public static final SpriteIdentifier BASE_TEXTURE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("entity/conduit/base"));
	public static final SpriteIdentifier CAGE_TEXTURE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("entity/conduit/cage"));
	public static final SpriteIdentifier WIND_TEXTURE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("entity/conduit/wind"));
	public static final SpriteIdentifier WIND_VERTICAL_TEXTURE = new SpriteIdentifier(
		SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("entity/conduit/wind_vertical")
	);
	public static final SpriteIdentifier OPEN_EYE_TEXTURE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("entity/conduit/open_eye"));
	public static final SpriteIdentifier CLOSED_EYE_TEXTURE = new SpriteIdentifier(
		SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("entity/conduit/closed_eye")
	);
	private final ModelPart field_20823;
	private final ModelPart field_20824;
	private final ModelPart field_20825;
	private final ModelPart field_20826;
	private final BlockEntityRenderDispatcher field_27753;

	public ConduitBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
		this.field_27753 = context.getRenderDispatcher();
		this.field_20823 = context.getLayerModelPart(EntityModelLayers.CONDUIT_EYE);
		this.field_20824 = context.getLayerModelPart(EntityModelLayers.CONDUIT_WIND);
		this.field_20825 = context.getLayerModelPart(EntityModelLayers.CONDUIT_SHELL);
		this.field_20826 = context.getLayerModelPart(EntityModelLayers.CONDUIT);
	}

	public static TexturedModelData getEyeTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild("eye", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -4.0F, 0.0F, 8.0F, 8.0F, 0.0F, new Dilation(0.01F)), ModelTransform.NONE);
		return TexturedModelData.of(modelData, 16, 16);
	}

	public static TexturedModelData getWindTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild("wind", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F), ModelTransform.NONE);
		return TexturedModelData.of(modelData, 64, 32);
	}

	public static TexturedModelData getShellTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild("shell", ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F), ModelTransform.NONE);
		return TexturedModelData.of(modelData, 32, 16);
	}

	public static TexturedModelData getPlainTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild("shell", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), ModelTransform.NONE);
		return TexturedModelData.of(modelData, 32, 16);
	}

	public void render(ConduitBlockEntity conduitBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		float g = (float)conduitBlockEntity.ticks + f;
		if (!conduitBlockEntity.isActive()) {
			float h = conduitBlockEntity.getRotation(0.0F);
			VertexConsumer vertexConsumer = BASE_TEXTURE.getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntitySolid);
			matrixStack.push();
			matrixStack.translate(0.5, 0.5, 0.5);
			matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(h));
			this.field_20825.render(matrixStack, vertexConsumer, i, j);
			matrixStack.pop();
		} else {
			float h = conduitBlockEntity.getRotation(f) * (180.0F / (float)Math.PI);
			float k = MathHelper.sin(g * 0.1F) / 2.0F + 0.5F;
			k = k * k + k;
			matrixStack.push();
			matrixStack.translate(0.5, (double)(0.3F + k * 0.2F), 0.5);
			Vector3f vector3f = new Vector3f(0.5F, 1.0F, 0.5F);
			vector3f.normalize();
			matrixStack.multiply(new Quaternion(vector3f, h, true));
			this.field_20826.render(matrixStack, CAGE_TEXTURE.getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntityCutoutNoCull), i, j);
			matrixStack.pop();
			int l = conduitBlockEntity.ticks / 66 % 3;
			matrixStack.push();
			matrixStack.translate(0.5, 0.5, 0.5);
			if (l == 1) {
				matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90.0F));
			} else if (l == 2) {
				matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(90.0F));
			}

			VertexConsumer vertexConsumer2 = (l == 1 ? WIND_VERTICAL_TEXTURE : WIND_TEXTURE)
				.getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntityCutoutNoCull);
			this.field_20824.render(matrixStack, vertexConsumer2, i, j);
			matrixStack.pop();
			matrixStack.push();
			matrixStack.translate(0.5, 0.5, 0.5);
			matrixStack.scale(0.875F, 0.875F, 0.875F);
			matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(180.0F));
			matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0F));
			this.field_20824.render(matrixStack, vertexConsumer2, i, j);
			matrixStack.pop();
			Camera camera = this.field_27753.camera;
			matrixStack.push();
			matrixStack.translate(0.5, (double)(0.3F + k * 0.2F), 0.5);
			matrixStack.scale(0.5F, 0.5F, 0.5F);
			float m = -camera.getYaw();
			matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(m));
			matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(camera.getPitch()));
			matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0F));
			float n = 1.3333334F;
			matrixStack.scale(1.3333334F, 1.3333334F, 1.3333334F);
			this.field_20823
				.render(
					matrixStack,
					(conduitBlockEntity.isEyeOpen() ? OPEN_EYE_TEXTURE : CLOSED_EYE_TEXTURE).getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntityCutoutNoCull),
					i,
					j
				);
			matrixStack.pop();
		}
	}
}
