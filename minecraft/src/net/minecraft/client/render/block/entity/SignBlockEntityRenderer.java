package net.minecraft.client.render.block.entity;

import java.util.List;
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
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.Texts;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.text.Text;
import net.minecraft.util.SignType;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class SignBlockEntityRenderer extends BlockEntityRenderer<SignBlockEntity> {
	private final SignBlockEntityRenderer.SignModel model = new SignBlockEntityRenderer.SignModel();

	public SignBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		super(blockEntityRenderDispatcher);
	}

	public void render(SignBlockEntity signBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		BlockState blockState = signBlockEntity.getCachedState();
		matrixStack.push();
		float g = 0.6666667F;
		if (blockState.getBlock() instanceof SignBlock) {
			matrixStack.translate(0.5, 0.5, 0.5);
			float h = -((float)((Integer)blockState.get(SignBlock.ROTATION) * 360) / 16.0F);
			matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(h));
			this.model.foot.visible = true;
		} else {
			matrixStack.translate(0.5, 0.5, 0.5);
			float h = -((Direction)blockState.get(WallSignBlock.FACING)).asRotation();
			matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(h));
			matrixStack.translate(0.0, -0.3125, -0.4375);
			this.model.foot.visible = false;
		}

		matrixStack.push();
		matrixStack.scale(0.6666667F, -0.6666667F, -0.6666667F);
		SpriteIdentifier spriteIdentifier = getModelTexture(blockState.getBlock());
		VertexConsumer vertexConsumer = spriteIdentifier.getVertexConsumer(vertexConsumerProvider, this.model::getLayer);
		this.model.field.render(matrixStack, vertexConsumer, i, j);
		this.model.foot.render(matrixStack, vertexConsumer, i, j);
		matrixStack.pop();
		TextRenderer textRenderer = this.dispatcher.getTextRenderer();
		float k = 0.010416667F;
		matrixStack.translate(0.0, 0.33333334F, 0.046666667F);
		matrixStack.scale(0.010416667F, -0.010416667F, 0.010416667F);
		int l = signBlockEntity.getTextColor().getSignColor();
		double d = 0.4;
		int m = (int)((double)NativeImage.method_24033(l) * 0.4);
		int n = (int)((double)NativeImage.method_24034(l) * 0.4);
		int o = (int)((double)NativeImage.method_24035(l) * 0.4);
		int p = NativeImage.method_24031(0, o, n, m);

		for (int q = 0; q < 4; q++) {
			String string = signBlockEntity.getTextBeingEditedOnRow(q, text -> {
				List<Text> list = Texts.wrapLines(text, 90, textRenderer, false, true);
				return list.isEmpty() ? "" : ((Text)list.get(0)).asFormattedString();
			});
			if (string != null) {
				float r = (float)(-textRenderer.getStringWidth(string) / 2);
				textRenderer.draw(
					string, r, (float)(q * 10 - signBlockEntity.text.length * 5), p, false, matrixStack.peek().getModel(), vertexConsumerProvider, false, 0, i
				);
			}
		}

		matrixStack.pop();
	}

	public static SpriteIdentifier getModelTexture(Block block) {
		SignType signType;
		if (block instanceof AbstractSignBlock) {
			signType = ((AbstractSignBlock)block).getSignType();
		} else {
			signType = SignType.OAK;
		}

		return TexturedRenderLayers.getSignTextureId(signType);
	}

	@Environment(EnvType.CLIENT)
	public static final class SignModel extends Model {
		public final ModelPart field = new ModelPart(64, 32, 0, 0);
		public final ModelPart foot;

		public SignModel() {
			super(RenderLayer::getEntityCutoutNoCull);
			this.field.addCuboid(-12.0F, -14.0F, -1.0F, 24.0F, 12.0F, 2.0F, 0.0F);
			this.foot = new ModelPart(64, 32, 0, 14);
			this.foot.addCuboid(-1.0F, -2.0F, -1.0F, 2.0F, 14.0F, 2.0F, 0.0F);
		}

		@Override
		public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
			this.field.render(matrices, vertices, light, overlay, red, green, blue, alpha);
			this.foot.render(matrices, vertices, light, overlay, red, green, blue, alpha);
		}
	}
}
