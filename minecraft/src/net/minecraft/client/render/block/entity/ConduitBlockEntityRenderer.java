package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.client.model.Dilation;
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
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class ConduitBlockEntityRenderer implements BlockEntityRenderer<ConduitBlockEntity> {
	public static final SpriteIdentifier BASE_TEXTURE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.ofVanilla("entity/conduit/base"));
	public static final SpriteIdentifier CAGE_TEXTURE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.ofVanilla("entity/conduit/cage"));
	public static final SpriteIdentifier WIND_TEXTURE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.ofVanilla("entity/conduit/wind"));
	public static final SpriteIdentifier WIND_VERTICAL_TEXTURE = new SpriteIdentifier(
		SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.ofVanilla("entity/conduit/wind_vertical")
	);
	public static final SpriteIdentifier OPEN_EYE_TEXTURE = new SpriteIdentifier(
		SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.ofVanilla("entity/conduit/open_eye")
	);
	public static final SpriteIdentifier CLOSED_EYE_TEXTURE = new SpriteIdentifier(
		SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.ofVanilla("entity/conduit/closed_eye")
	);
	private final ModelPart conduitEye;
	private final ModelPart conduitWind;
	private final ModelPart conduitShell;
	private final ModelPart conduit;
	private final BlockEntityRenderDispatcher dispatcher;

	public ConduitBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
		this.dispatcher = ctx.getRenderDispatcher();
		this.conduitEye = ctx.getLayerModelPart(EntityModelLayers.CONDUIT_EYE);
		this.conduitWind = ctx.getLayerModelPart(EntityModelLayers.CONDUIT_WIND);
		this.conduitShell = ctx.getLayerModelPart(EntityModelLayers.CONDUIT_SHELL);
		this.conduit = ctx.getLayerModelPart(EntityModelLayers.CONDUIT);
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
			matrixStack.translate(0.5F, 0.5F, 0.5F);
			matrixStack.multiply(new Quaternionf().rotationY(h * (float) (Math.PI / 180.0)));
			this.conduitShell.render(matrixStack, vertexConsumer, i, j);
			matrixStack.pop();
		} else {
			float h = conduitBlockEntity.getRotation(f) * (180.0F / (float)Math.PI);
			float k = MathHelper.sin(g * 0.1F) / 2.0F + 0.5F;
			k = k * k + k;
			matrixStack.push();
			matrixStack.translate(0.5F, 0.3F + k * 0.2F, 0.5F);
			Vector3f vector3f = new Vector3f(0.5F, 1.0F, 0.5F).normalize();
			matrixStack.multiply(new Quaternionf().rotationAxis(h * (float) (Math.PI / 180.0), vector3f));
			this.conduit.render(matrixStack, CAGE_TEXTURE.getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntityCutoutNoCull), i, j);
			matrixStack.pop();
			int l = conduitBlockEntity.ticks / 66 % 3;
			matrixStack.push();
			matrixStack.translate(0.5F, 0.5F, 0.5F);
			if (l == 1) {
				matrixStack.multiply(new Quaternionf().rotationX((float) (Math.PI / 2)));
			} else if (l == 2) {
				matrixStack.multiply(new Quaternionf().rotationZ((float) (Math.PI / 2)));
			}

			VertexConsumer vertexConsumer2 = (l == 1 ? WIND_VERTICAL_TEXTURE : WIND_TEXTURE)
				.getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntityCutoutNoCull);
			this.conduitWind.render(matrixStack, vertexConsumer2, i, j);
			matrixStack.pop();
			matrixStack.push();
			matrixStack.translate(0.5F, 0.5F, 0.5F);
			matrixStack.scale(0.875F, 0.875F, 0.875F);
			matrixStack.multiply(new Quaternionf().rotationXYZ((float) Math.PI, 0.0F, (float) Math.PI));
			this.conduitWind.render(matrixStack, vertexConsumer2, i, j);
			matrixStack.pop();
			Camera camera = this.dispatcher.camera;
			matrixStack.push();
			matrixStack.translate(0.5F, 0.3F + k * 0.2F, 0.5F);
			matrixStack.scale(0.5F, 0.5F, 0.5F);
			float m = -camera.getYaw();
			matrixStack.multiply(new Quaternionf().rotationYXZ(m * (float) (Math.PI / 180.0), camera.getPitch() * (float) (Math.PI / 180.0), (float) Math.PI));
			float n = 1.3333334F;
			matrixStack.scale(1.3333334F, 1.3333334F, 1.3333334F);
			this.conduitEye
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
