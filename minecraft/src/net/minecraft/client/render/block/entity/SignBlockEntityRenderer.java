package net.minecraft.client.render.block.entity;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SignBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.util.SignType;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class SignBlockEntityRenderer implements BlockEntityRenderer<SignBlockEntity> {
	private final Map<SignType, SignBlockEntityRenderer.SignModel> typeToModel;
	private final TextRenderer textRenderer;

	public SignBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
		this.typeToModel = (Map<SignType, SignBlockEntityRenderer.SignModel>)SignType.stream()
			.collect(
				ImmutableMap.toImmutableMap(
					signType -> signType, signType -> new SignBlockEntityRenderer.SignModel(ctx.getLayerModelPart(EntityModelLayers.createSign(signType)))
				)
			);
		this.textRenderer = ctx.getTextRenderer();
	}

	public void render(SignBlockEntity signBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		BlockState blockState = signBlockEntity.getCachedState();
		matrixStack.push();
		float g = 0.6666667F;
		SignType signType = getSignType(blockState.getBlock());
		SignBlockEntityRenderer.SignModel signModel = (SignBlockEntityRenderer.SignModel)this.typeToModel.get(signType);
		if (blockState.getBlock() instanceof SignBlock) {
			matrixStack.translate(0.5, 0.5, 0.5);
			float h = -((float)((Integer)blockState.get(SignBlock.ROTATION) * 360) / 16.0F);
			matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(h));
			signModel.stick.visible = true;
		} else {
			matrixStack.translate(0.5, 0.5, 0.5);
			float h = -((Direction)blockState.get(WallSignBlock.FACING)).asRotation();
			matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(h));
			matrixStack.translate(0.0, -0.3125, -0.4375);
			signModel.stick.visible = false;
		}

		matrixStack.push();
		matrixStack.scale(0.6666667F, -0.6666667F, -0.6666667F);
		SpriteIdentifier spriteIdentifier = TexturedRenderLayers.getSignTextureId(signType);
		VertexConsumer vertexConsumer = spriteIdentifier.getVertexConsumer(vertexConsumerProvider, signModel::getLayer);
		signModel.root.render(matrixStack, vertexConsumer, i, j);
		matrixStack.pop();
		float k = 0.010416667F;
		matrixStack.translate(0.0, 0.33333334F, 0.046666667F);
		matrixStack.scale(0.010416667F, -0.010416667F, 0.010416667F);
		int l = signBlockEntity.getTextColor().getSignColor();
		double d = 0.4;
		int m = (int)((double)NativeImage.getRed(l) * 0.4);
		int n = (int)((double)NativeImage.getGreen(l) * 0.4);
		int o = (int)((double)NativeImage.getBlue(l) * 0.4);
		int p = NativeImage.getAbgrColor(0, o, n, m);
		int q = 20;
		OrderedText[] orderedTexts = signBlockEntity.updateSign(MinecraftClient.getInstance().shouldFilterText(), text -> {
			List<OrderedText> list = this.textRenderer.wrapLines(text, 90);
			return list.isEmpty() ? OrderedText.EMPTY : (OrderedText)list.get(0);
		});

		for (int r = 0; r < 4; r++) {
			OrderedText orderedText = orderedTexts[r];
			float s = (float)(-this.textRenderer.getWidth(orderedText) / 2);
			int t = signBlockEntity.isGlowingText() ? 15728880 : i;
			this.textRenderer.draw(orderedText, s, (float)(r * 10 - 20), p, false, matrixStack.peek().getModel(), vertexConsumerProvider, false, 0, t);
		}

		matrixStack.pop();
	}

	public static SignType getSignType(Block block) {
		SignType signType;
		if (block instanceof AbstractSignBlock) {
			signType = ((AbstractSignBlock)block).getSignType();
		} else {
			signType = SignType.OAK;
		}

		return signType;
	}

	public static SignBlockEntityRenderer.SignModel createSignModel(EntityModelLoader entityModelLoader, SignType type) {
		return new SignBlockEntityRenderer.SignModel(entityModelLoader.getModelPart(EntityModelLayers.createSign(type)));
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild("sign", ModelPartBuilder.create().uv(0, 0).cuboid(-12.0F, -14.0F, -1.0F, 24.0F, 12.0F, 2.0F), ModelTransform.NONE);
		modelPartData.addChild("stick", ModelPartBuilder.create().uv(0, 14).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 14.0F, 2.0F), ModelTransform.NONE);
		return TexturedModelData.of(modelData, 64, 32);
	}

	@Environment(EnvType.CLIENT)
	public static final class SignModel extends Model {
		public final ModelPart root;
		public final ModelPart stick;

		public SignModel(ModelPart root) {
			super(RenderLayer::getEntityCutoutNoCull);
			this.root = root;
			this.stick = root.getChild("stick");
		}

		@Override
		public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
			this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
		}
	}
}
