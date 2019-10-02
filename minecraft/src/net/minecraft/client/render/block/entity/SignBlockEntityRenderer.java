package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SignBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.Texts;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class SignBlockEntityRenderer extends BlockEntityRenderer<SignBlockEntity> {
	private final ModelPart field_20990 = new ModelPart(64, 32, 0, 0);
	private final ModelPart field_20991;

	public SignBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		super(blockEntityRenderDispatcher);
		this.field_20990.addCuboid(-12.0F, -14.0F, -1.0F, 24.0F, 12.0F, 2.0F, 0.0F);
		this.field_20991 = new ModelPart(64, 32, 0, 14);
		this.field_20991.addCuboid(-1.0F, -2.0F, -1.0F, 2.0F, 14.0F, 2.0F, 0.0F);
	}

	public void method_23083(
		SignBlockEntity signBlockEntity,
		double d,
		double e,
		double f,
		float g,
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage,
		int i
	) {
		BlockState blockState = signBlockEntity.getCachedState();
		matrixStack.push();
		float h = 0.6666667F;
		if (blockState.getBlock() instanceof SignBlock) {
			matrixStack.translate(0.5, 0.5, 0.5);
			matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(-((float)((Integer)blockState.get(SignBlock.ROTATION) * 360) / 16.0F), true));
			this.field_20991.visible = true;
		} else {
			matrixStack.translate(0.5, 0.5, 0.5);
			matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(-((Direction)blockState.get(WallSignBlock.FACING)).asRotation(), true));
			matrixStack.translate(0.0, -0.3125, -0.4375);
			this.field_20991.visible = false;
		}

		Sprite sprite = this.getSprite(this.getModelTexture(blockState.getBlock()));
		matrixStack.push();
		matrixStack.scale(0.6666667F, -0.6666667F, -0.6666667F);
		VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.SOLID);
		this.field_20990.render(matrixStack, vertexConsumer, 0.0625F, i, sprite);
		this.field_20991.render(matrixStack, vertexConsumer, 0.0625F, i, sprite);
		matrixStack.pop();
		TextRenderer textRenderer = this.field_20989.getFontRenderer();
		float j = 0.010416667F;
		matrixStack.translate(0.0, 0.33333334F, 0.046666667F);
		matrixStack.scale(0.010416667F, -0.010416667F, 0.010416667F);
		int k = signBlockEntity.getTextColor().getSignColor();

		for (int l = 0; l < 4; l++) {
			String string = signBlockEntity.getTextBeingEditedOnRow(l, text -> {
				List<Text> list = Texts.wrapLines(text, 90, textRenderer, false, true);
				return list.isEmpty() ? "" : ((Text)list.get(0)).asFormattedString();
			});
			if (string != null) {
				float m = (float)(-textRenderer.getStringWidth(string) / 2);
				textRenderer.method_22942(
					string, m, (float)(l * 10 - signBlockEntity.text.length * 5), k, false, matrixStack.peek(), layeredVertexConsumerStorage, false, 0, i
				);
				if (l == signBlockEntity.getCurrentRow() && signBlockEntity.getSelectionStart() >= 0) {
					int n = textRenderer.getStringWidth(string.substring(0, Math.max(Math.min(signBlockEntity.getSelectionStart(), string.length()), 0)));
					int o = textRenderer.isRightToLeft() ? -1 : 1;
					int p = (n - textRenderer.getStringWidth(string) / 2) * o;
					int q = l * 10 - signBlockEntity.text.length * 5;
					if (signBlockEntity.isCaretVisible()) {
						if (signBlockEntity.getSelectionStart() < string.length()) {
							DrawableHelper.fill(p, q - 1, p + 1, q + 9, 0xFF000000 | k);
						} else {
							textRenderer.method_22942("_", (float)p, (float)q, k, false, matrixStack.peek(), layeredVertexConsumerStorage, false, 0, i);
						}
					}

					if (signBlockEntity.getSelectionEnd() != signBlockEntity.getSelectionStart()) {
						int r = Math.min(signBlockEntity.getSelectionStart(), signBlockEntity.getSelectionEnd());
						int s = Math.max(signBlockEntity.getSelectionStart(), signBlockEntity.getSelectionEnd());
						int t = (textRenderer.getStringWidth(string.substring(0, r)) - textRenderer.getStringWidth(string) / 2) * o;
						int u = (textRenderer.getStringWidth(string.substring(0, s)) - textRenderer.getStringWidth(string) / 2) * o;
						RenderSystem.pushMatrix();
						RenderSystem.multMatrix(matrixStack.peek());
						this.method_16210(Math.min(t, u), q, Math.max(t, u), q + 9);
						RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
						RenderSystem.popMatrix();
					}
				}
			}
		}

		matrixStack.pop();
	}

	private Identifier getModelTexture(Block block) {
		if (block == Blocks.OAK_SIGN || block == Blocks.OAK_WALL_SIGN) {
			return ModelLoader.field_21014;
		} else if (block == Blocks.SPRUCE_SIGN || block == Blocks.SPRUCE_WALL_SIGN) {
			return ModelLoader.field_21015;
		} else if (block == Blocks.BIRCH_SIGN || block == Blocks.BIRCH_WALL_SIGN) {
			return ModelLoader.field_21016;
		} else if (block == Blocks.ACACIA_SIGN || block == Blocks.ACACIA_WALL_SIGN) {
			return ModelLoader.field_21017;
		} else if (block == Blocks.JUNGLE_SIGN || block == Blocks.JUNGLE_WALL_SIGN) {
			return ModelLoader.field_21018;
		} else {
			return block != Blocks.DARK_OAK_SIGN && block != Blocks.DARK_OAK_WALL_SIGN ? ModelLoader.field_21014 : ModelLoader.field_21019;
		}
	}

	private void method_16210(int i, int j, int k, int l) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		RenderSystem.color4f(0.0F, 0.0F, 1.0F, 1.0F);
		RenderSystem.disableTexture();
		RenderSystem.enableColorLogicOp();
		RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
		bufferBuilder.begin(7, VertexFormats.POSITION);
		bufferBuilder.vertex((double)i, (double)l, 0.0).next();
		bufferBuilder.vertex((double)k, (double)l, 0.0).next();
		bufferBuilder.vertex((double)k, (double)j, 0.0).next();
		bufferBuilder.vertex((double)i, (double)j, 0.0).next();
		bufferBuilder.end();
		BufferRenderer.draw(bufferBuilder);
		RenderSystem.disableColorLogicOp();
		RenderSystem.enableTexture();
	}
}
