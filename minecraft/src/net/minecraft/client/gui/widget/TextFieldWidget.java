package net.minecraft.client.gui.widget;

import com.google.common.base.Predicates;
import com.mojang.blaze3d.platform.GlStateManager;
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
	private boolean field_2096 = true;
	private boolean editable = true;
	private boolean field_17037;
	private int field_2103;
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

	public void setChangedListener(Consumer<String> changedListener) {
		this.changedListener = changedListener;
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

			this.method_1872();
			this.method_1884(this.selectionStart);
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
			this.method_1884(this.selectionStart);
			this.onChanged(this.text);
		}
	}

	private void onChanged(String newText) {
		if (this.changedListener != null) {
			this.changedListener.accept(newText);
		}

		this.nextNarration = Util.getMeasuringTimeMs() + 500L;
	}

	private void method_16873(int i) {
		if (Screen.hasControlDown()) {
			this.method_1877(i);
		} else {
			this.method_1878(i);
		}
	}

	public void method_1877(int i) {
		if (!this.text.isEmpty()) {
			if (this.selectionEnd != this.selectionStart) {
				this.write("");
			} else {
				this.method_1878(this.method_1853(i) - this.selectionStart);
			}
		}
	}

	public void method_1878(int i) {
		if (!this.text.isEmpty()) {
			if (this.selectionEnd != this.selectionStart) {
				this.write("");
			} else {
				boolean bl = i < 0;
				int j = bl ? this.selectionStart + i : this.selectionStart;
				int k = bl ? this.selectionStart : this.selectionStart + i;
				String string = "";
				if (j >= 0) {
					string = this.text.substring(0, j);
				}

				if (k < this.text.length()) {
					string = string + this.text.substring(k);
				}

				if (this.textPredicate.test(string)) {
					this.text = string;
					if (bl) {
						this.moveCursor(i);
					}

					this.onChanged(this.text);
				}
			}
		}
	}

	public int method_1853(int i) {
		return this.method_1869(i, this.getCursor());
	}

	private int method_1869(int i, int j) {
		return this.method_1864(i, j, true);
	}

	private int method_1864(int i, int j, boolean bl) {
		int k = j;
		boolean bl2 = i < 0;
		int l = Math.abs(i);

		for (int m = 0; m < l; m++) {
			if (!bl2) {
				int n = this.text.length();
				k = this.text.indexOf(32, k);
				if (k == -1) {
					k = n;
				} else {
					while (bl && k < n && this.text.charAt(k) == ' ') {
						k++;
					}
				}
			} else {
				while (bl && k > 0 && this.text.charAt(k - 1) == ' ') {
					k--;
				}

				while (k > 0 && this.text.charAt(k - 1) != ' ') {
					k--;
				}
			}
		}

		return k;
	}

	public void moveCursor(int offset) {
		this.method_1883(this.selectionStart + offset);
	}

	public void method_1883(int i) {
		this.setSelectionStart(i);
		if (!this.field_17037) {
			this.method_1884(this.selectionStart);
		}

		this.onChanged(this.text);
	}

	public void setSelectionStart(int cursor) {
		this.selectionStart = MathHelper.clamp(cursor, 0, this.text.length());
	}

	public void method_1870() {
		this.method_1883(0);
	}

	public void method_1872() {
		this.method_1883(this.text.length());
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (!this.method_20315()) {
			return false;
		} else {
			this.field_17037 = Screen.hasShiftDown();
			if (Screen.isSelectAll(keyCode)) {
				this.method_1872();
				this.method_1884(0);
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
							this.field_17037 = false;
							this.method_16873(-1);
							this.field_17037 = Screen.hasShiftDown();
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
							this.field_17037 = false;
							this.method_16873(1);
							this.field_17037 = Screen.hasShiftDown();
						}

						return true;
					case 262:
						if (Screen.hasControlDown()) {
							this.method_1883(this.method_1853(1));
						} else {
							this.moveCursor(1);
						}

						return true;
					case 263:
						if (Screen.hasControlDown()) {
							this.method_1883(this.method_1853(-1));
						} else {
							this.moveCursor(-1);
						}

						return true;
					case 268:
						this.method_1870();
						return true;
					case 269:
						this.method_1872();
						return true;
				}
			}
		}
	}

	public boolean method_20315() {
		return this.isVisible() && this.isFocused() && this.isEditable();
	}

	@Override
	public boolean charTyped(char chr, int keyCode) {
		if (!this.method_20315()) {
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
			if (this.field_2096) {
				this.method_1876(bl);
			}

			if (this.isFocused() && bl && button == 0) {
				int i = MathHelper.floor(mouseX) - this.x;
				if (this.focused) {
					i -= 4;
				}

				String string = this.textRenderer.trimToWidth(this.text.substring(this.field_2103), this.method_1859());
				this.method_1883(this.textRenderer.trimToWidth(string, i).length() + this.field_2103);
				return true;
			} else {
				return false;
			}
		}
	}

	public void method_1876(boolean bl) {
		super.setFocused(bl);
	}

	@Override
	public void renderButton(int mouseX, int mouseY, float delta) {
		if (this.isVisible()) {
			if (this.hasBorder()) {
				fill(this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, -6250336);
				fill(this.x, this.y, this.x + this.width, this.y + this.height, -16777216);
			}

			int i = this.editable ? this.editableColor : this.uneditableColor;
			int j = this.selectionStart - this.field_2103;
			int k = this.selectionEnd - this.field_2103;
			String string = this.textRenderer.trimToWidth(this.text.substring(this.field_2103), this.method_1859());
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
				n = this.textRenderer.drawWithShadow((String)this.renderTextProvider.apply(string2, this.field_2103), (float)l, (float)m, i);
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
				this.method_1886(o, m - 1, p - 1, m + 1 + 9);
			}
		}
	}

	private void method_1886(int i, int j, int k, int l) {
		if (i < k) {
			int m = i;
			i = k;
			k = m;
		}

		if (j < l) {
			int m = j;
			j = l;
			l = m;
		}

		if (k > this.x + this.width) {
			k = this.x + this.width;
		}

		if (i > this.x + this.width) {
			i = this.x + this.width;
		}

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		GlStateManager.color4f(0.0F, 0.0F, 255.0F, 255.0F);
		GlStateManager.disableTexture();
		GlStateManager.enableColorLogicOp();
		GlStateManager.logicOp(GlStateManager.LogicOp.OR_REVERSE);
		bufferBuilder.begin(7, VertexFormats.POSITION);
		bufferBuilder.vertex((double)i, (double)l, 0.0).next();
		bufferBuilder.vertex((double)k, (double)l, 0.0).next();
		bufferBuilder.vertex((double)k, (double)j, 0.0).next();
		bufferBuilder.vertex((double)i, (double)j, 0.0).next();
		tessellator.draw();
		GlStateManager.disableColorLogicOp();
		GlStateManager.enableTexture();
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

	public int method_1859() {
		return this.hasBorder() ? this.width - 8 : this.width;
	}

	public void method_1884(int i) {
		int j = this.text.length();
		this.selectionEnd = MathHelper.clamp(i, 0, j);
		if (this.textRenderer != null) {
			if (this.field_2103 > j) {
				this.field_2103 = j;
			}

			int k = this.method_1859();
			String string = this.textRenderer.trimToWidth(this.text.substring(this.field_2103), k);
			int l = string.length() + this.field_2103;
			if (this.selectionEnd == this.field_2103) {
				this.field_2103 = this.field_2103 - this.textRenderer.trimToWidth(this.text, k, true).length();
			}

			if (this.selectionEnd > l) {
				this.field_2103 = this.field_2103 + (this.selectionEnd - l);
			} else if (this.selectionEnd <= this.field_2103) {
				this.field_2103 = this.field_2103 - (this.field_2103 - this.selectionEnd);
			}

			this.field_2103 = MathHelper.clamp(this.field_2103, 0, j);
		}
	}

	public void method_1856(boolean bl) {
		this.field_2096 = bl;
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
