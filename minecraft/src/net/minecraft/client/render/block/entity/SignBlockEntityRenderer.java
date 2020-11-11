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
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.text.OrderedText;
import net.minecraft.util.SignType;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class SignBlockEntityRenderer implements BlockEntityRenderer<SignBlockEntity> {
	private final Map<SignType, SignBlockEntityRenderer.SignModel> field_27754;
	private final TextRenderer field_27755;

	public SignBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
		this.field_27754 = (Map<SignType, SignBlockEntityRenderer.SignModel>)SignType.stream()
			.collect(
				ImmutableMap.toImmutableMap(
					signType -> signType, signType -> new SignBlockEntityRenderer.SignModel(context.getLayerModelPart(EntityModelLayers.createSign(signType)))
				)
			);
		this.field_27755 = context.getTextRenderer();
	}

	public void render(SignBlockEntity signBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		BlockState blockState = signBlockEntity.getCachedState();
		matrixStack.push();
		float g = 0.6666667F;
		SignType signType = method_32155(blockState.getBlock());
		SignBlockEntityRenderer.SignModel signModel = (SignBlockEntityRenderer.SignModel)this.field_27754.get(signType);
		if (blockState.getBlock() instanceof SignBlock) {
			matrixStack.translate(0.5, 0.5, 0.5);
			float h = -((float)((Integer)blockState.get(SignBlock.ROTATION) * 360) / 16.0F);
			matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(h));
			signModel.foot.visible = true;
		} else {
			matrixStack.translate(0.5, 0.5, 0.5);
			float h = -((Direction)blockState.get(WallSignBlock.FACING)).asRotation();
			matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(h));
			matrixStack.translate(0.0, -0.3125, -0.4375);
			signModel.foot.visible = false;
		}

		matrixStack.push();
		matrixStack.scale(0.6666667F, -0.6666667F, -0.6666667F);
		SpriteIdentifier spriteIdentifier = TexturedRenderLayers.getSignTextureId(signType);
		VertexConsumer vertexConsumer = spriteIdentifier.getVertexConsumer(vertexConsumerProvider, signModel::getLayer);
		signModel.field_27756.render(matrixStack, vertexConsumer, i, j);
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

		for (int r = 0; r < 4; r++) {
			OrderedText orderedText = signBlockEntity.getTextBeingEditedOnRow(r, text -> {
				List<OrderedText> list = this.field_27755.wrapLines(text, 90);
				return list.isEmpty() ? OrderedText.EMPTY : (OrderedText)list.get(0);
			});
			if (orderedText != null) {
				float s = (float)(-this.field_27755.getWidth(orderedText) / 2);
				this.field_27755.draw(orderedText, s, (float)(r * 10 - 20), p, false, matrixStack.peek().getModel(), vertexConsumerProvider, false, 0, i);
			}
		}

		matrixStack.pop();
	}

	public static SignType method_32155(Block block) {
		SignType signType;
		if (block instanceof AbstractSignBlock) {
			signType = ((AbstractSignBlock)block).getSignType();
		} else {
			signType = SignType.OAK;
		}

		return signType;
	}

	public static SignBlockEntityRenderer.SignModel method_32157(EntityModelLoader entityModelLoader, SignType signType) {
		return new SignBlockEntityRenderer.SignModel(entityModelLoader.getModelPart(EntityModelLayers.createSign(signType)));
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
		public final ModelPart field_27756;
		public final ModelPart foot;

		public SignModel(ModelPart modelPart) {
			super(RenderLayer::getEntityCutoutNoCull);
			this.field_27756 = modelPart;
			this.foot = modelPart.getChild("stick");
		}

		@Override
		public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
			this.field_27756.render(matrices, vertices, light, overlay, red, green, blue, alpha);
		}
	}
}
