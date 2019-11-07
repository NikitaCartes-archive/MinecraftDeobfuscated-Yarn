package net.minecraft.client.gui.screen.ingame;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.SignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SelectionManager;
import net.minecraft.client.util.Texts;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.server.network.packet.UpdateSignC2SPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class SignEditScreen extends Screen {
	private final SignBlockEntityRenderer.class_4702 field_21525 = new SignBlockEntityRenderer.class_4702();
	private final SignBlockEntity sign;
	private int ticksSinceOpened;
	private int currentRow;
	private SelectionManager selectionManager;

	public SignEditScreen(SignBlockEntity signBlockEntity) {
		super(new TranslatableText("sign.edit"));
		this.sign = signBlockEntity;
	}

	@Override
	protected void init() {
		this.minecraft.keyboard.enableRepeatEvents(true);
		this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120, 200, 20, I18n.translate("gui.done"), buttonWidget -> this.finishEditing()));
		this.sign.setEditable(false);
		this.selectionManager = new SelectionManager(
			this.minecraft, () -> this.sign.getTextOnRow(this.currentRow).getString(), string -> this.sign.setTextOnRow(this.currentRow, new LiteralText(string)), 90
		);
	}

	@Override
	public void removed() {
		this.minecraft.keyboard.enableRepeatEvents(false);
		ClientPlayNetworkHandler clientPlayNetworkHandler = this.minecraft.getNetworkHandler();
		if (clientPlayNetworkHandler != null) {
			clientPlayNetworkHandler.sendPacket(
				new UpdateSignC2SPacket(this.sign.getPos(), this.sign.getTextOnRow(0), this.sign.getTextOnRow(1), this.sign.getTextOnRow(2), this.sign.getTextOnRow(3))
			);
		}

		this.sign.setEditable(true);
	}

	@Override
	public void tick() {
		this.ticksSinceOpened++;
		if (!this.sign.getType().supports(this.sign.getCachedState().getBlock())) {
			this.finishEditing();
		}
	}

	private void finishEditing() {
		this.sign.markDirty();
		this.minecraft.openScreen(null);
	}

	@Override
	public boolean charTyped(char chr, int keyCode) {
		this.selectionManager.insert(chr);
		return true;
	}

	@Override
	public void onClose() {
		this.finishEditing();
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 265) {
			this.currentRow = this.currentRow - 1 & 3;
			this.selectionManager.moveCaretToEnd();
			return true;
		} else if (keyCode == 264 || keyCode == 257 || keyCode == 335) {
			this.currentRow = this.currentRow + 1 & 3;
			this.selectionManager.moveCaretToEnd();
			return true;
		} else {
			return this.selectionManager.handleSpecialKey(keyCode) ? true : super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();
		this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 40, 16777215);
		MatrixStack matrixStack = new MatrixStack();
		matrixStack.push();
		matrixStack.translate((double)(this.width / 2), 0.0, 50.0);
		float f = 93.75F;
		matrixStack.scale(-93.75F, -93.75F, -93.75F);
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(180.0F));
		matrixStack.translate(0.0, -1.3125, 0.0);
		BlockState blockState = this.sign.getCachedState();
		boolean bl = blockState.getBlock() instanceof SignBlock;
		if (!bl) {
			matrixStack.translate(0.0, -0.3125, 0.0);
		}

		boolean bl2 = this.ticksSinceOpened / 6 % 2 == 0;
		float g = 0.6666667F;
		matrixStack.push();
		matrixStack.scale(0.6666667F, -0.6666667F, -0.6666667F);
		VertexConsumerProvider.Immediate immediate = this.minecraft.getBufferBuilders().getEntityVertexConsumers();
		Sprite sprite = this.minecraft.getSpriteAtlas().getSprite(SignBlockEntityRenderer.getModelTexture(blockState.getBlock()));
		VertexConsumer vertexConsumer = immediate.getBuffer(RenderLayer.getCutout());
		this.field_21525.field_21530.render(matrixStack, vertexConsumer, 15728880, OverlayTexture.DEFAULT_UV, sprite);
		if (bl) {
			this.field_21525.field_21531.render(matrixStack, vertexConsumer, 15728880, OverlayTexture.DEFAULT_UV, sprite);
		}

		matrixStack.pop();
		float h = 0.010416667F;
		matrixStack.translate(0.0, 0.33333334F, 0.046666667F);
		matrixStack.scale(0.010416667F, -0.010416667F, 0.010416667F);
		int i = this.sign.getTextColor().getSignColor();
		String[] strings = new String[4];

		for (int j = 0; j < strings.length; j++) {
			strings[j] = this.sign.getTextBeingEditedOnRow(j, text -> {
				List<Text> list = Texts.wrapLines(text, 90, this.minecraft.textRenderer, false, true);
				return list.isEmpty() ? "" : ((Text)list.get(0)).asFormattedString();
			});
		}

		Matrix4f matrix4f = matrixStack.method_23760().method_23761();
		int k = this.selectionManager.getSelectionStart();
		int l = this.selectionManager.getSelectionEnd();
		int m = this.minecraft.textRenderer.isRightToLeft() ? -1 : 1;
		int n = this.currentRow * 10 - this.sign.text.length * 5;

		for (int o = 0; o < strings.length; o++) {
			String string = strings[o];
			if (string != null) {
				float p = (float)(-this.minecraft.textRenderer.getStringWidth(string) / 2);
				this.minecraft.textRenderer.draw(string, p, (float)(o * 10 - this.sign.text.length * 5), i, false, matrix4f, immediate, false, 0, 15728880);
				if (o == this.currentRow && k >= 0 && bl2) {
					int q = this.minecraft.textRenderer.getStringWidth(string.substring(0, Math.max(Math.min(k, string.length()), 0)));
					int r = (q - this.minecraft.textRenderer.getStringWidth(string) / 2) * m;
					if (k >= string.length()) {
						this.minecraft.textRenderer.draw("_", (float)r, (float)n, i, false, matrix4f, immediate, false, 0, 15728880);
					}
				}
			}
		}

		immediate.draw();

		for (int ox = 0; ox < strings.length; ox++) {
			String string = strings[ox];
			if (string != null && ox == this.currentRow && k >= 0) {
				int s = this.minecraft.textRenderer.getStringWidth(string.substring(0, Math.max(Math.min(k, string.length()), 0)));
				int q = (s - this.minecraft.textRenderer.getStringWidth(string) / 2) * m;
				if (bl2 && k < string.length()) {
					fill(matrix4f, q, n - 1, q + 1, n + 9, 0xFF000000 | i);
				}

				if (l != k) {
					int r = Math.min(k, l);
					int t = Math.max(k, l);
					int u = (this.minecraft.textRenderer.getStringWidth(string.substring(0, r)) - this.minecraft.textRenderer.getStringWidth(string) / 2) * m;
					int v = (this.minecraft.textRenderer.getStringWidth(string.substring(0, t)) - this.minecraft.textRenderer.getStringWidth(string) / 2) * m;
					int w = Math.min(u, v);
					int x = Math.max(u, v);
					Tessellator tessellator = Tessellator.getInstance();
					BufferBuilder bufferBuilder = tessellator.getBuffer();
					RenderSystem.disableTexture();
					RenderSystem.enableColorLogicOp();
					RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
					bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
					bufferBuilder.vertex(matrix4f, (float)w, (float)(n + 9), 0.0F).color(0, 0, 255, 255).next();
					bufferBuilder.vertex(matrix4f, (float)x, (float)(n + 9), 0.0F).color(0, 0, 255, 255).next();
					bufferBuilder.vertex(matrix4f, (float)x, (float)n, 0.0F).color(0, 0, 255, 255).next();
					bufferBuilder.vertex(matrix4f, (float)w, (float)n, 0.0F).color(0, 0, 255, 255).next();
					bufferBuilder.end();
					BufferRenderer.draw(bufferBuilder);
					RenderSystem.disableColorLogicOp();
					RenderSystem.enableTexture();
				}
			}
		}

		matrixStack.pop();
		super.render(mouseX, mouseY, delta);
	}
}
