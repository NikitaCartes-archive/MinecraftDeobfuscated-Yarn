package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.EditBox;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.joml.Matrix4f;

/**
 * A widget of {@link EditBox}, a multiline edit box with support for
 * basic keyboard shortcuts. This class implements the rendering and scrolling
 * for the edit box.
 */
@Environment(EnvType.CLIENT)
public class EditBoxWidget extends ScrollableWidget {
	private static final int CURSOR_PADDING = 1;
	private static final int CURSOR_COLOR = -3092272;
	private static final String UNDERSCORE = "_";
	private static final int FOCUSED_BOX_TEXT_COLOR = -2039584;
	private static final int UNFOCUSED_BOX_TEXT_COLOR = -857677600;
	private final TextRenderer textRenderer;
	/**
	 * The placeholder text that gets rendered when the edit box is empty. This does not
	 * get returned from {@link #getText}; an empty string will be returned in such cases.
	 */
	private final Text placeholder;
	private final EditBox editBox;
	private int tick;

	public EditBoxWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text placeholder, Text message) {
		super(x, y, width, height, message);
		this.textRenderer = textRenderer;
		this.placeholder = placeholder;
		this.editBox = new EditBox(textRenderer, width - this.getPaddingDoubled());
		this.editBox.setCursorChangeListener(this::onCursorChange);
	}

	/**
	 * Sets the maximum length of the edit box text in characters.
	 * 
	 * <p>If {@code maxLength} equals {@link EditBox#UNLIMITED_LENGTH}, the edit box does not
	 * have a length limit, and the widget does not show the current text length indicator.
	 * 
	 * @throws IllegalArgumentException if {@code maxLength} is negative
	 * @see EditBox#setMaxLength
	 */
	public void setMaxLength(int maxLength) {
		this.editBox.setMaxLength(maxLength);
	}

	/**
	 * Sets the change listener that is called every time the text changes.
	 * 
	 * @param changeListener the listener that takes the new text of the edit box
	 */
	public void setChangeListener(Consumer<String> changeListener) {
		this.editBox.setChangeListener(changeListener);
	}

	/**
	 * Sets the text of the edit box and moves the cursor to the end of the edit box.
	 */
	public void setText(String text) {
		this.editBox.setText(text);
	}

	/**
	 * {@return the current text of the edit box}
	 */
	public String getText() {
		return this.editBox.getText();
	}

	public void tick() {
		this.tick++;
	}

	@Override
	public void appendClickableNarrations(NarrationMessageBuilder builder) {
		builder.put(NarrationPart.TITLE, Text.translatable("gui.narrate.editBox", this.getMessage(), this.getText()));
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (super.mouseClicked(mouseX, mouseY, button)) {
			return true;
		} else if (this.isWithinBounds(mouseX, mouseY) && button == 0) {
			this.editBox.setSelecting(Screen.hasShiftDown());
			this.moveCursor(mouseX, mouseY);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
			return true;
		} else if (this.isWithinBounds(mouseX, mouseY) && button == 0) {
			this.editBox.setSelecting(true);
			this.moveCursor(mouseX, mouseY);
			this.editBox.setSelecting(Screen.hasShiftDown());
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return this.editBox.handleSpecialKey(keyCode);
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		if (this.visible && this.isFocused() && SharedConstants.isValidChar(chr)) {
			this.editBox.replaceSelection(Character.toString(chr));
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void renderContents(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		String string = this.editBox.getText();
		if (string.isEmpty() && !this.isFocused()) {
			this.textRenderer
				.drawTrimmed(this.placeholder, this.getX() + this.getPadding(), this.getY() + this.getPadding(), this.width - this.getPaddingDoubled(), -857677600);
		} else {
			int i = this.editBox.getCursor();
			boolean bl = this.isFocused() && this.tick / 6 % 2 == 0;
			boolean bl2 = i < string.length();
			int j = 0;
			int k = 0;
			int l = this.getY() + this.getPadding();

			for (EditBox.Substring substring : this.editBox.getLines()) {
				boolean bl3 = this.isVisible(l, l + 9);
				if (bl && bl2 && i >= substring.beginIndex() && i <= substring.endIndex()) {
					if (bl3) {
						j = this.textRenderer.drawWithShadow(matrices, string.substring(substring.beginIndex(), i), (float)(this.getX() + this.getPadding()), (float)l, -2039584)
							- 1;
						DrawableHelper.fill(matrices, j, l - 1, j + 1, l + 1 + 9, -3092272);
						this.textRenderer.drawWithShadow(matrices, string.substring(i, substring.endIndex()), (float)j, (float)l, -2039584);
					}
				} else {
					if (bl3) {
						j = this.textRenderer
								.drawWithShadow(matrices, string.substring(substring.beginIndex(), substring.endIndex()), (float)(this.getX() + this.getPadding()), (float)l, -2039584)
							- 1;
					}

					k = l;
				}

				l += 9;
			}

			if (bl && !bl2 && this.isVisible(k, k + 9)) {
				this.textRenderer.drawWithShadow(matrices, "_", (float)j, (float)k, -3092272);
			}

			if (this.editBox.hasSelection()) {
				EditBox.Substring substring2 = this.editBox.getSelection();
				int m = this.getX() + this.getPadding();
				l = this.getY() + this.getPadding();

				for (EditBox.Substring substring3 : this.editBox.getLines()) {
					if (substring2.beginIndex() > substring3.endIndex()) {
						l += 9;
					} else {
						if (substring3.beginIndex() > substring2.endIndex()) {
							break;
						}

						if (this.isVisible(l, l + 9)) {
							int n = this.textRenderer.getWidth(string.substring(substring3.beginIndex(), Math.max(substring2.beginIndex(), substring3.beginIndex())));
							int o;
							if (substring2.endIndex() > substring3.endIndex()) {
								o = this.width - this.getPadding();
							} else {
								o = this.textRenderer.getWidth(string.substring(substring3.beginIndex(), substring2.endIndex()));
							}

							this.drawSelection(matrices, m + n, l, m + o, l + 9);
						}

						l += 9;
					}
				}
			}
		}
	}

	@Override
	protected void renderOverlay(MatrixStack matrices) {
		super.renderOverlay(matrices);
		if (this.editBox.hasMaxLength()) {
			int i = this.editBox.getMaxLength();
			Text text = Text.translatable("gui.multiLineEditBox.character_limit", this.editBox.getText().length(), i);
			drawTextWithShadow(matrices, this.textRenderer, text, this.getX() + this.width - this.textRenderer.getWidth(text), this.getY() + this.height + 4, 10526880);
		}
	}

	@Override
	public int getContentsHeight() {
		return 9 * this.editBox.getLineCount();
	}

	@Override
	protected boolean overflows() {
		return (double)this.editBox.getLineCount() > this.getMaxLinesWithoutOverflow();
	}

	@Override
	protected double getDeltaYPerScroll() {
		return 9.0 / 2.0;
	}

	private void drawSelection(MatrixStack matrices, int left, int top, int right, int bottom) {
		Matrix4f matrix4f = matrices.peek().getPositionMatrix();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		RenderSystem.setShader(GameRenderer::getPositionProgram);
		RenderSystem.setShaderColor(0.0F, 0.0F, 1.0F, 1.0F);
		RenderSystem.disableTexture();
		RenderSystem.enableColorLogicOp();
		RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
		bufferBuilder.vertex(matrix4f, (float)left, (float)bottom, 0.0F).next();
		bufferBuilder.vertex(matrix4f, (float)right, (float)bottom, 0.0F).next();
		bufferBuilder.vertex(matrix4f, (float)right, (float)top, 0.0F).next();
		bufferBuilder.vertex(matrix4f, (float)left, (float)top, 0.0F).next();
		tessellator.draw();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.disableColorLogicOp();
		RenderSystem.enableTexture();
	}

	private void onCursorChange() {
		double d = this.getScrollY();
		EditBox.Substring substring = this.editBox.getLine((int)(d / 9.0));
		if (this.editBox.getCursor() <= substring.beginIndex()) {
			d = (double)(this.editBox.getCurrentLineIndex() * 9);
		} else {
			EditBox.Substring substring2 = this.editBox.getLine((int)((d + (double)this.height) / 9.0) - 1);
			if (this.editBox.getCursor() > substring2.endIndex()) {
				d = (double)(this.editBox.getCurrentLineIndex() * 9 - this.height + 9 + this.getPaddingDoubled());
			}
		}

		this.setScrollY(d);
	}

	/**
	 * {@return the maximum amount of lines the widget can hold without overflowing}
	 */
	private double getMaxLinesWithoutOverflow() {
		return (double)(this.height - this.getPaddingDoubled()) / 9.0;
	}

	private void moveCursor(double mouseX, double mouseY) {
		double d = mouseX - (double)this.getX() - (double)this.getPadding();
		double e = mouseY - (double)this.getY() - (double)this.getPadding() + this.getScrollY();
		this.editBox.moveCursor(d, e);
	}
}
