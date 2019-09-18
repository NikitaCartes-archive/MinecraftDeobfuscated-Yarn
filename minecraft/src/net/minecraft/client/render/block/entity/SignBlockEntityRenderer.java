package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SignBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.model.SignBlockEntityModel;
import net.minecraft.client.util.TextComponentUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class SignBlockEntityRenderer extends BlockEntityRenderer<SignBlockEntity> {
	private static final Identifier OAK_TEX = new Identifier("textures/entity/signs/oak.png");
	private static final Identifier SPRUCE_TEX = new Identifier("textures/entity/signs/spruce.png");
	private static final Identifier BIRCH_TEX = new Identifier("textures/entity/signs/birch.png");
	private static final Identifier ACACIA_TEX = new Identifier("textures/entity/signs/acacia.png");
	private static final Identifier JUNGLE_TEX = new Identifier("textures/entity/signs/jungle.png");
	private static final Identifier DARK_OAK_TEX = new Identifier("textures/entity/signs/dark_oak.png");
	private final SignBlockEntityModel model = new SignBlockEntityModel();

	public void method_3582(SignBlockEntity signBlockEntity, double d, double e, double f, float g, int i, BlockRenderLayer blockRenderLayer) {
		BlockState blockState = signBlockEntity.getCachedState();
		RenderSystem.pushMatrix();
		float h = 0.6666667F;
		if (blockState.getBlock() instanceof SignBlock) {
			RenderSystem.translatef((float)d + 0.5F, (float)e + 0.5F, (float)f + 0.5F);
			RenderSystem.rotatef(-((float)((Integer)blockState.get(SignBlock.ROTATION) * 360) / 16.0F), 0.0F, 1.0F, 0.0F);
			this.model.getSignpostModel().visible = true;
		} else {
			RenderSystem.translatef((float)d + 0.5F, (float)e + 0.5F, (float)f + 0.5F);
			RenderSystem.rotatef(-((Direction)blockState.get(WallSignBlock.FACING)).asRotation(), 0.0F, 1.0F, 0.0F);
			RenderSystem.translatef(0.0F, -0.3125F, -0.4375F);
			this.model.getSignpostModel().visible = false;
		}

		if (i >= 0) {
			this.bindTexture((Identifier)DESTROY_STAGE_TEXTURES.get(i));
			RenderSystem.matrixMode(5890);
			RenderSystem.pushMatrix();
			RenderSystem.scalef(4.0F, 2.0F, 1.0F);
			RenderSystem.translatef(0.0625F, 0.0625F, 0.0625F);
			RenderSystem.matrixMode(5888);
		} else {
			this.bindTexture(this.getModelTexture(blockState.getBlock()));
		}

		RenderSystem.enableRescaleNormal();
		RenderSystem.pushMatrix();
		RenderSystem.scalef(0.6666667F, -0.6666667F, -0.6666667F);
		this.model.render();
		RenderSystem.popMatrix();
		TextRenderer textRenderer = this.getFontRenderer();
		float j = 0.010416667F;
		RenderSystem.translatef(0.0F, 0.33333334F, 0.046666667F);
		RenderSystem.scalef(0.010416667F, -0.010416667F, 0.010416667F);
		RenderSystem.normal3f(0.0F, 0.0F, -0.010416667F);
		RenderSystem.depthMask(false);
		int k = signBlockEntity.getTextColor().getSignColor();
		if (i < 0) {
			for (int l = 0; l < 4; l++) {
				String string = signBlockEntity.getTextBeingEditedOnRow(l, text -> {
					List<Text> list = TextComponentUtil.wrapLines(text, 90, textRenderer, false, true);
					return list.isEmpty() ? "" : ((Text)list.get(0)).asFormattedString();
				});
				if (string != null) {
					textRenderer.draw(string, (float)(-textRenderer.getStringWidth(string) / 2), (float)(l * 10 - signBlockEntity.text.length * 5), k);
					if (l == signBlockEntity.getCurrentRow() && signBlockEntity.getSelectionStart() >= 0) {
						int m = textRenderer.getStringWidth(string.substring(0, Math.max(Math.min(signBlockEntity.getSelectionStart(), string.length()), 0)));
						int n = textRenderer.isRightToLeft() ? -1 : 1;
						int o = (m - textRenderer.getStringWidth(string) / 2) * n;
						int p = l * 10 - signBlockEntity.text.length * 5;
						if (signBlockEntity.isCaretVisible()) {
							if (signBlockEntity.getSelectionStart() < string.length()) {
								DrawableHelper.fill(o, p - 1, o + 1, p + 9, 0xFF000000 | k);
							} else {
								textRenderer.draw("_", (float)o, (float)p, k);
							}
						}

						if (signBlockEntity.getSelectionEnd() != signBlockEntity.getSelectionStart()) {
							int q = Math.min(signBlockEntity.getSelectionStart(), signBlockEntity.getSelectionEnd());
							int r = Math.max(signBlockEntity.getSelectionStart(), signBlockEntity.getSelectionEnd());
							int s = (textRenderer.getStringWidth(string.substring(0, q)) - textRenderer.getStringWidth(string) / 2) * n;
							int t = (textRenderer.getStringWidth(string.substring(0, r)) - textRenderer.getStringWidth(string) / 2) * n;
							this.method_16210(Math.min(s, t), p, Math.max(s, t), p + 9);
						}
					}
				}
			}
		}

		RenderSystem.depthMask(true);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.popMatrix();
		if (i >= 0) {
			RenderSystem.matrixMode(5890);
			RenderSystem.popMatrix();
			RenderSystem.matrixMode(5888);
		}
	}

	private Identifier getModelTexture(Block block) {
		if (block == Blocks.OAK_SIGN || block == Blocks.OAK_WALL_SIGN) {
			return OAK_TEX;
		} else if (block == Blocks.SPRUCE_SIGN || block == Blocks.SPRUCE_WALL_SIGN) {
			return SPRUCE_TEX;
		} else if (block == Blocks.BIRCH_SIGN || block == Blocks.BIRCH_WALL_SIGN) {
			return BIRCH_TEX;
		} else if (block == Blocks.ACACIA_SIGN || block == Blocks.ACACIA_WALL_SIGN) {
			return ACACIA_TEX;
		} else if (block == Blocks.JUNGLE_SIGN || block == Blocks.JUNGLE_WALL_SIGN) {
			return JUNGLE_TEX;
		} else {
			return block != Blocks.DARK_OAK_SIGN && block != Blocks.DARK_OAK_WALL_SIGN ? OAK_TEX : DARK_OAK_TEX;
		}
	}

	private void method_16210(int i, int j, int k, int l) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		RenderSystem.color4f(0.0F, 0.0F, 255.0F, 255.0F);
		RenderSystem.disableTexture();
		RenderSystem.enableColorLogicOp();
		RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
		bufferBuilder.begin(7, VertexFormats.POSITION);
		bufferBuilder.vertex((double)i, (double)l, 0.0).next();
		bufferBuilder.vertex((double)k, (double)l, 0.0).next();
		bufferBuilder.vertex((double)k, (double)j, 0.0).next();
		bufferBuilder.vertex((double)i, (double)j, 0.0).next();
		tessellator.draw();
		RenderSystem.disableColorLogicOp();
		RenderSystem.enableTexture();
	}
}
