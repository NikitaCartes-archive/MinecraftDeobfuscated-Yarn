package net.minecraft.client.gui.widget;

import com.google.common.base.Predicates;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class TextFieldWidget extends AbstractButtonWidget implements Drawable, Element {
	private final TextRenderer textRenderer;
	private String text = "";
	private int maxLength = 32;
	private int focusedTicks;
	private boolean focused = true;
	private boolean focusUnlocked = true;
	private boolean editable = true;
	private boolean selecting;
	private int firstCharacter;
	private int selectionStart;
	private int selectionEnd;
	private int editableColor = 14737632;
	private int uneditableColor = 7368816;
	private String suggestion;
	private Consumer<String> changedListener;
	private Predicate<String> textPredicate = Predicates.alwaysTrue();
	private BiFunction<String, Integer, String> renderTextProvider = (string, integer) -> string;

	public TextFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, String message) {
		this(textRenderer, x, y, width, height, null, message);
	}

	public TextFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, @Nullable TextFieldWidget copyFrom, String message) {
		super(x, y, width, height, message);
		this.textRenderer = textRenderer;
		if (copyFrom != null) {
			this.setText(copyFrom.getText());
		}
	}

	public void setChangedListener(Consumer<String> consumer) {
		this.changedListener = consumer;
	}

	public void setRenderTextProvider(BiFunction<String, Integer, String> renderTextProvider) {
		this.renderTextProvider = renderTextProvider;
	}

	public void tick() {
		this.focusedTicks++;
	}

	@Override
	protected String getNarrationMessage() {
		String string = this.getMessage();
		return string.isEmpty() ? "" : I18n.translate("gui.narrate.editBox", string, this.text);
	}

	public void setText(String text) {
		if (this.textPredicate.test(text)) {
			if (text.length() > this.maxLength) {
				this.text = text.substring(0, this.maxLength);
			} else {
				this.text = text;
			}

			this.setCursorToEnd();
			this.setSelectionEnd(this.selectionStart);
			this.onChanged(text);
		}
	}

	public String getText() {
		return this.text;
	}

	public String getSelectedText() {
		int i = this.selectionStart < this.selectionEnd ? this.selectionStart : this.selectionEnd;
		int j = this.selectionStart < this.selectionEnd ? this.selectionEnd : this.selectionStart;
		return this.text.substring(i, j);
	}

	public void setTextPredicate(Predicate<String> textPredicate) {
		this.textPredicate = textPredicate;
	}

	public void write(String text) {
		String string = "";
		String string2 = SharedConstants.stripInvalidChars(text);
		int i = this.selectionStart < this.selectionEnd ? this.selectionStart : this.selectionEnd;
		int j = this.selectionStart < this.selectionEnd ? this.selectionEnd : this.selectionStart;
		int k = this.maxLength - this.text.length() - (i - j);
		if (!this.text.isEmpty()) {
			string = string + this.text.substring(0, i);
		}

		int l;
		if (k < string2.length()) {
			string = string + string2.substring(0, k);
			l = k;
		} else {
			string = string + string2;
			l = string2.length();
		}

		if (!this.text.isEmpty() && j < this.text.length()) {
			string = string + this.text.substring(j);
		}

		if (this.textPredicate.test(string)) {
			this.text = string;
			this.setSelectionStart(i + l);
			this.setSelectionEnd(this.selectionStart);
			this.onChanged(this.text);
		}
	}

	private void onChanged(String newText) {
		if (this.changedListener != null) {
			this.changedListener.accept(newText);
		}

		this.nextNarration = Util.getMeasuringTimeMs() + 500L;
	}

	private void erase(int offset) {
		if (Screen.hasControlDown()) {
			this.eraseWords(offset);
		} else {
			this.eraseCharacters(offset);
		}
	}

	public void eraseWords(int wordOffset) {
		if (!this.text.isEmpty()) {
			if (this.selectionEnd != this.selectionStart) {
				this.write("");
			} else {
				this.eraseCharacters(this.getWordSkipPosition(wordOffset) - this.selectionStart);
			}
		}
	}

	public void eraseCharacters(int characterOffset) {
		if (!this.text.isEmpty()) {
			if (this.selectionEnd != this.selectionStart) {
				this.write("");
			} else {
				boolean bl = characterOffset < 0;
				int i = bl ? this.selectionStart + characterOffset : this.selectionStart;
				int j = bl ? this.selectionStart : this.selectionStart + characterOffset;
				String string = "";
				if (i >= 0) {
					string = this.text.substring(0, i);
				}

				if (j < this.text.length()) {
					string = string + this.text.substring(j);
				}

				if (this.textPredicate.test(string)) {
					this.text = string;
					if (bl) {
						this.moveCursor(characterOffset);
					}

					this.onChanged(this.text);
				}
			}
		}
	}

	public int getWordSkipPosition(int wordOffset) {
		return this.getWordSkipPosition(wordOffset, this.getCursor());
	}

	private int getWordSkipPosition(int wordOffset, int cursorPosition) {
		return this.getWordSkipPosition(wordOffset, cursorPosition, true);
	}

	private int getWordSkipPosition(int wordOffset, int cursorPosition, boolean skipOverSpaces) {
		int i = cursorPosition;
		boolean bl = wordOffset < 0;
		int j = Math.abs(wordOffset);

		for (int k = 0; k < j; k++) {
			if (!bl) {
				int l = this.text.length();
				i = this.text.indexOf(32, i);
				if (i == -1) {
					i = l;
				} else {
					while (skipOverSpaces && i < l && this.text.charAt(i) == ' ') {
						i++;
					}
				}
			} else {
				while (skipOverSpaces && i > 0 && this.text.charAt(i - 1) == ' ') {
					i--;
				}

				while (i > 0 && this.text.charAt(i - 1) != ' ') {
					i--;
				}
			}
		}

		return i;
	}

	public void moveCursor(int offset) {
		this.setCursor(this.selectionStart + offset);
	}

	public void setCursor(int cursor) {
		this.setSelectionStart(cursor);
		if (!this.selecting) {
			this.setSelectionEnd(this.selectionStart);
		}

		this.onChanged(this.text);
	}

	public void setSelectionStart(int cursor) {
		this.selectionStart = MathHelper.clamp(cursor, 0, this.text.length());
	}

	public void setCursorToStart() {
		this.setCursor(0);
	}

	public void setCursorToEnd() {
		this.setCursor(this.text.length());
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (!this.isActive()) {
			return false;
		} else {
			this.selecting = Screen.hasShiftDown();
			if (Screen.isSelectAll(keyCode)) {
				this.setCursorToEnd();
				this.setSelectionEnd(0);
				return true;
			} else if (Screen.isCopy(keyCode)) {
				MinecraftClient.getInstance().keyboard.setClipboard(this.getSelectedText());
				return true;
			} else if (Screen.isPaste(keyCode)) {
				if (this.editable) {
					this.write(MinecraftClient.getInstance().keyboard.getClipboard());
				}

				return true;
			} else if (Screen.isCut(keyCode)) {
				MinecraftClient.getInstance().keyboard.setClipboard(this.getSelectedText());
				if (this.editable) {
					this.write("");
				}

				return true;
			} else {
				switch (keyCode) {
					case 259:
						if (this.editable) {
							this.selecting = false;
							this.erase(-1);
							this.selecting = Screen.hasShiftDown();
						}

						return true;
					case 260:
					case 264:
					case 265:
					case 266:
					case 267:
					default:
						return false;
					case 261:
						if (this.editable) {
							this.selecting = false;
							this.erase(1);
							this.selecting = Screen.hasShiftDown();
						}

						return true;
					case 262:
						if (Screen.hasControlDown()) {
							this.setCursor(this.getWordSkipPosition(1));
						} else {
							this.moveCursor(1);
						}

						return true;
					case 263:
						if (Screen.hasControlDown()) {
							this.setCursor(this.getWordSkipPosition(-1));
						} else {
							this.moveCursor(-1);
						}

						return true;
					case 268:
						this.setCursorToStart();
						return true;
					case 269:
						this.setCursorToEnd();
						return true;
				}
			}
		}
	}

	public boolean isActive() {
		return this.isVisible() && this.isFocused() && this.isEditable();
	}

	@Override
	public boolean charTyped(char chr, int keyCode) {
		if (!this.isActive()) {
			return false;
		} else if (SharedConstants.isValidChar(chr)) {
			if (this.editable) {
				this.write(Character.toString(chr));
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (!this.isVisible()) {
			return false;
		} else {
			boolean bl = mouseX >= (double)this.x && mouseX < (double)(this.x + this.width) && mouseY >= (double)this.y && mouseY < (double)(this.y + this.height);
			if (this.focusUnlocked) {
				this.setSelected(bl);
			}

			if (this.isFocused() && bl && button == 0) {
				int i = MathHelper.floor(mouseX) - this.x;
				if (this.focused) {
					i -= 4;
				}

				String string = this.textRenderer.trimToWidth(this.text.substring(this.firstCharacter), this.getInnerWidth());
				this.setCursor(this.textRenderer.trimToWidth(string, i).length() + this.firstCharacter);
				return true;
			} else {
				return false;
			}
		}
	}

	public void setSelected(boolean selected) {
		super.setFocused(selected);
	}

	@Override
	public void renderButton(int mouseX, int mouseY, float delta) {
		if (this.isVisible()) {
			if (this.hasBorder()) {
				fill(this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, -6250336);
				fill(this.x, this.y, this.x + this.width, this.y + this.height, -16777216);
			}

			int i = this.editable ? this.editableColor : this.uneditableColor;
			int j = this.selectionStart - this.firstCharacter;
			int k = this.selectionEnd - this.firstCharacter;
			String string = this.textRenderer.trimToWidth(this.text.substring(this.firstCharacter), this.getInnerWidth());
			boolean bl = j >= 0 && j <= string.length();
			boolean bl2 = this.isFocused() && this.focusedTicks / 6 % 2 == 0 && bl;
			int l = this.focused ? this.x + 4 : this.x;
			int m = this.focused ? this.y + (this.height - 8) / 2 : this.y;
			int n = l;
			if (k > string.length()) {
				k = string.length();
			}

			if (!string.isEmpty()) {
				String string2 = bl ? string.substring(0, j) : string;
				n = this.textRenderer.drawWithShadow((String)this.renderTextProvider.apply(string2, this.firstCharacter), (float)l, (float)m, i);
			}

			boolean bl3 = this.selectionStart < this.text.length() || this.text.length() >= this.getMaxLength();
			int o = n;
			if (!bl) {
				o = j > 0 ? l + this.width : l;
			} else if (bl3) {
				o = n - 1;
				n--;
			}

			if (!string.isEmpty() && bl && j < string.length()) {
				this.textRenderer.drawWithShadow((String)this.renderTextProvider.apply(string.substring(j), this.selectionStart), (float)n, (float)m, i);
			}

			if (!bl3 && this.suggestion != null) {
				this.textRenderer.drawWithShadow(this.suggestion, (float)(o - 1), (float)m, -8355712);
			}

			if (bl2) {
				if (bl3) {
					DrawableHelper.fill(o, m - 1, o + 1, m + 1 + 9, -3092272);
				} else {
					this.textRenderer.drawWithShadow("_", (float)o, (float)m, i);
				}
			}

			if (k != j) {
				int p = l + this.textRenderer.getStringWidth(string.substring(0, k));
				this.drawSelectionHighlight(o, m - 1, p - 1, m + 1 + 9);
			}
		}
	}

	private void drawSelectionHighlight(int x1, int y1, int x2, int y2) {
		if (x1 < x2) {
			int i = x1;
			x1 = x2;
			x2 = i;
		}

		if (y1 < y2) {
			int i = y1;
			y1 = y2;
			y2 = i;
		}

		if (x2 > this.x + this.width) {
			x2 = this.x + this.width;
		}

		if (x1 > this.x + this.width) {
			x1 = this.x + this.width;
		}

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		RenderSystem.color4f(0.0F, 0.0F, 255.0F, 255.0F);
		RenderSystem.disableTexture();
		RenderSystem.enableColorLogicOp();
		RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
		bufferBuilder.begin(7, VertexFormats.POSITION);
		bufferBuilder.vertex((double)x1, (double)y2, 0.0).next();
		bufferBuilder.vertex((double)x2, (double)y2, 0.0).next();
		bufferBuilder.vertex((double)x2, (double)y1, 0.0).next();
		bufferBuilder.vertex((double)x1, (double)y1, 0.0).next();
		tessellator.draw();
		RenderSystem.disableColorLogicOp();
		RenderSystem.enableTexture();
	}

	public void setMaxLength(int maximumLength) {
		this.maxLength = maximumLength;
		if (this.text.length() > maximumLength) {
			this.text = this.text.substring(0, maximumLength);
			this.onChanged(this.text);
		}
	}

	private int getMaxLength() {
		return this.maxLength;
	}

	public int getCursor() {
		return this.selectionStart;
	}

	private boolean hasBorder() {
		return this.focused;
	}

	public void setHasBorder(boolean hasBorder) {
		this.focused = hasBorder;
	}

	public void setEditableColor(int color) {
		this.editableColor = color;
	}

	public void setUneditableColor(int color) {
		this.uneditableColor = color;
	}

	@Override
	public boolean changeFocus(boolean bl) {
		return this.visible && this.editable ? super.changeFocus(bl) : false;
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return this.visible
			&& mouseX >= (double)this.x
			&& mouseX < (double)(this.x + this.width)
			&& mouseY >= (double)this.y
			&& mouseY < (double)(this.y + this.height);
	}

	@Override
	protected void onFocusedChanged(boolean bl) {
		if (bl) {
			this.focusedTicks = 0;
		}
	}

	private boolean isEditable() {
		return this.editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public int getInnerWidth() {
		return this.hasBorder() ? this.width - 8 : this.width;
	}

	public void setSelectionEnd(int i) {
		int j = this.text.length();
		this.selectionEnd = MathHelper.clamp(i, 0, j);
		if (this.textRenderer != null) {
			if (this.firstCharacter > j) {
				this.firstCharacter = j;
			}

			int k = this.getInnerWidth();
			String string = this.textRenderer.trimToWidth(this.text.substring(this.firstCharacter), k);
			int l = string.length() + this.firstCharacter;
			if (this.selectionEnd == this.firstCharacter) {
				this.firstCharacter = this.firstCharacter - this.textRenderer.trimToWidth(this.text, k, true).length();
			}

			if (this.selectionEnd > l) {
				this.firstCharacter = this.firstCharacter + (this.selectionEnd - l);
			} else if (this.selectionEnd <= this.firstCharacter) {
				this.firstCharacter = this.firstCharacter - (this.firstCharacter - this.selectionEnd);
			}

			this.firstCharacter = MathHelper.clamp(this.firstCharacter, 0, j);
		}
	}

	public void setFocusUnlocked(boolean focusUnlocked) {
		this.focusUnlocked = focusUnlocked;
	}

	public boolean isVisible() {
		return this.visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void setSuggestion(@Nullable String suggestion) {
		this.suggestion = suggestion;
	}

	public int getCharacterX(int index) {
		return index > this.text.length() ? this.x : this.x + this.textRenderer.getStringWidth(this.text.substring(0, index));
	}

	public void setX(int x) {
		this.x = x;
	}
}
