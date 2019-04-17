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
import net.minecraft.client.gui.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.resource.language.I18n;
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
	private int cursorMax;
	private int cursorMin;
	private int field_2100 = 14737632;
	private int field_2098 = 7368816;
	private String suggestion;
	private Consumer<String> changedListener;
	private Predicate<String> textPredicate = Predicates.alwaysTrue();
	private BiFunction<String, Integer, String> renderTextProvider = (stringx, integer) -> stringx;

	public TextFieldWidget(TextRenderer textRenderer, int i, int j, int k, int l, String string) {
		this(textRenderer, i, j, k, l, null, string);
	}

	public TextFieldWidget(TextRenderer textRenderer, int i, int j, int k, int l, @Nullable TextFieldWidget textFieldWidget, String string) {
		super(i, j, k, l, string);
		this.textRenderer = textRenderer;
		if (textFieldWidget != null) {
			this.setText(textFieldWidget.getText());
		}
	}

	public void setChangedListener(Consumer<String> consumer) {
		this.changedListener = consumer;
	}

	public void setRenderTextProvider(BiFunction<String, Integer, String> biFunction) {
		this.renderTextProvider = biFunction;
	}

	public void tick() {
		this.focusedTicks++;
	}

	@Override
	protected String getNarrationMessage() {
		String string = this.getMessage();
		return string.isEmpty() ? "" : I18n.translate("gui.narrate.editBox", string);
	}

	public void setText(String string) {
		if (this.textPredicate.test(string)) {
			if (string.length() > this.maxLength) {
				this.text = string.substring(0, this.maxLength);
			} else {
				this.text = string;
			}

			this.method_1872();
			this.method_1884(this.cursorMax);
			this.onChanged(string);
		}
	}

	public String getText() {
		return this.text;
	}

	public String getSelectedText() {
		int i = this.cursorMax < this.cursorMin ? this.cursorMax : this.cursorMin;
		int j = this.cursorMax < this.cursorMin ? this.cursorMin : this.cursorMax;
		return this.text.substring(i, j);
	}

	public void method_1890(Predicate<String> predicate) {
		this.textPredicate = predicate;
	}

	public void addText(String string) {
		String string2 = "";
		String string3 = SharedConstants.stripInvalidChars(string);
		int i = this.cursorMax < this.cursorMin ? this.cursorMax : this.cursorMin;
		int j = this.cursorMax < this.cursorMin ? this.cursorMin : this.cursorMax;
		int k = this.maxLength - this.text.length() - (i - j);
		if (!this.text.isEmpty()) {
			string2 = string2 + this.text.substring(0, i);
		}

		int l;
		if (k < string3.length()) {
			string2 = string2 + string3.substring(0, k);
			l = k;
		} else {
			string2 = string2 + string3;
			l = string3.length();
		}

		if (!this.text.isEmpty() && j < this.text.length()) {
			string2 = string2 + this.text.substring(j);
		}

		if (this.textPredicate.test(string2)) {
			this.text = string2;
			this.setCursor(i + l);
			this.method_1884(this.cursorMax);
			this.onChanged(this.text);
		}
	}

	private void onChanged(String string) {
		if (this.changedListener != null) {
			this.changedListener.accept(string);
		}
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
			if (this.cursorMin != this.cursorMax) {
				this.addText("");
			} else {
				this.method_1878(this.method_1853(i) - this.cursorMax);
			}
		}
	}

	public void method_1878(int i) {
		if (!this.text.isEmpty()) {
			if (this.cursorMin != this.cursorMax) {
				this.addText("");
			} else {
				boolean bl = i < 0;
				int j = bl ? this.cursorMax + i : this.cursorMax;
				int k = bl ? this.cursorMax : this.cursorMax + i;
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

	public void moveCursor(int i) {
		this.method_1883(this.cursorMax + i);
	}

	public void method_1883(int i) {
		this.setCursor(i);
		if (!this.field_17037) {
			this.method_1884(this.cursorMax);
		}

		this.onChanged(this.text);
	}

	public void setCursor(int i) {
		this.cursorMax = MathHelper.clamp(i, 0, this.text.length());
	}

	public void method_1870() {
		this.method_1883(0);
	}

	public void method_1872() {
		this.method_1883(this.text.length());
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (!this.method_20315()) {
			return false;
		} else {
			this.field_17037 = Screen.hasShiftDown();
			if (Screen.isSelectAll(i)) {
				this.method_1872();
				this.method_1884(0);
				return true;
			} else if (Screen.isCopy(i)) {
				MinecraftClient.getInstance().keyboard.setClipboard(this.getSelectedText());
				return true;
			} else if (Screen.isPaste(i)) {
				if (this.editable) {
					this.addText(MinecraftClient.getInstance().keyboard.getClipboard());
				}

				return true;
			} else if (Screen.isCut(i)) {
				MinecraftClient.getInstance().keyboard.setClipboard(this.getSelectedText());
				if (this.editable) {
					this.addText("");
				}

				return true;
			} else {
				switch (i) {
					case 259:
						if (this.editable) {
							this.method_16873(-1);
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
							this.method_16873(1);
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
		return this.isVisible() && this.isFocused() && this.method_20316();
	}

	@Override
	public boolean charTyped(char c, int i) {
		if (!this.method_20315()) {
			return false;
		} else if (SharedConstants.isValidChar(c)) {
			if (this.editable) {
				this.addText(Character.toString(c));
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (!this.isVisible()) {
			return false;
		} else {
			boolean bl = d >= (double)this.x && d < (double)(this.x + this.width) && e >= (double)this.y && e < (double)(this.y + this.height);
			if (this.field_2096) {
				this.method_1876(bl);
			}

			if (this.isFocused() && bl && i == 0) {
				int j = MathHelper.floor(d) - this.x;
				if (this.focused) {
					j -= 4;
				}

				String string = this.textRenderer.trimToWidth(this.text.substring(this.field_2103), this.method_1859());
				this.method_1883(this.textRenderer.trimToWidth(string, j).length() + this.field_2103);
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
	public void renderButton(int i, int j, float f) {
		if (this.isVisible()) {
			if (this.hasBorder()) {
				fill(this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, -6250336);
				fill(this.x, this.y, this.x + this.width, this.y + this.height, -16777216);
			}

			int k = this.editable ? this.field_2100 : this.field_2098;
			int l = this.cursorMax - this.field_2103;
			int m = this.cursorMin - this.field_2103;
			String string = this.textRenderer.trimToWidth(this.text.substring(this.field_2103), this.method_1859());
			boolean bl = l >= 0 && l <= string.length();
			boolean bl2 = this.isFocused() && this.focusedTicks / 6 % 2 == 0 && bl;
			int n = this.focused ? this.x + 4 : this.x;
			int o = this.focused ? this.y + (this.height - 8) / 2 : this.y;
			int p = n;
			if (m > string.length()) {
				m = string.length();
			}

			if (!string.isEmpty()) {
				String string2 = bl ? string.substring(0, l) : string;
				p = this.textRenderer.drawWithShadow((String)this.renderTextProvider.apply(string2, this.field_2103), (float)n, (float)o, k);
			}

			boolean bl3 = this.cursorMax < this.text.length() || this.text.length() >= this.getMaxLength();
			int q = p;
			if (!bl) {
				q = l > 0 ? n + this.width : n;
			} else if (bl3) {
				q = p - 1;
				p--;
			}

			if (!string.isEmpty() && bl && l < string.length()) {
				this.textRenderer.drawWithShadow((String)this.renderTextProvider.apply(string.substring(l), this.cursorMax), (float)p, (float)o, k);
			}

			if (!bl3 && this.suggestion != null) {
				this.textRenderer.drawWithShadow(this.suggestion, (float)(q - 1), (float)o, -8355712);
			}

			if (bl2) {
				if (bl3) {
					DrawableHelper.fill(q, o - 1, q + 1, o + 1 + 9, -3092272);
				} else {
					this.textRenderer.drawWithShadow("_", (float)q, (float)o, k);
				}
			}

			if (m != l) {
				int r = n + this.textRenderer.getStringWidth(string.substring(0, m));
				this.method_1886(q, o - 1, r - 1, o + 1 + 9);
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
		BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
		GlStateManager.color4f(0.0F, 0.0F, 255.0F, 255.0F);
		GlStateManager.disableTexture();
		GlStateManager.enableColorLogicOp();
		GlStateManager.logicOp(GlStateManager.LogicOp.field_5110);
		bufferBuilder.begin(7, VertexFormats.POSITION);
		bufferBuilder.vertex((double)i, (double)l, 0.0).next();
		bufferBuilder.vertex((double)k, (double)l, 0.0).next();
		bufferBuilder.vertex((double)k, (double)j, 0.0).next();
		bufferBuilder.vertex((double)i, (double)j, 0.0).next();
		tessellator.draw();
		GlStateManager.disableColorLogicOp();
		GlStateManager.enableTexture();
	}

	public void setMaxLength(int i) {
		this.maxLength = i;
		if (this.text.length() > i) {
			this.text = this.text.substring(0, i);
			this.onChanged(this.text);
		}
	}

	private int getMaxLength() {
		return this.maxLength;
	}

	public int getCursor() {
		return this.cursorMax;
	}

	private boolean hasBorder() {
		return this.focused;
	}

	public void setHasBorder(boolean bl) {
		this.focused = bl;
	}

	public void method_1868(int i) {
		this.field_2100 = i;
	}

	public void method_1860(int i) {
		this.field_2098 = i;
	}

	@Override
	public boolean changeFocus(boolean bl) {
		return this.visible && this.editable ? super.changeFocus(bl) : false;
	}

	@Override
	public boolean isMouseOver(double d, double e) {
		return this.visible && d >= (double)this.x && d < (double)(this.x + this.width) && e >= (double)this.y && e < (double)(this.y + this.height);
	}

	@Override
	protected void onFocusedChanged(boolean bl) {
		if (bl) {
			this.focusedTicks = 0;
		}
	}

	private boolean method_20316() {
		return this.editable;
	}

	public void setIsEditable(boolean bl) {
		this.editable = bl;
	}

	public int method_1859() {
		return this.hasBorder() ? this.width - 8 : this.width;
	}

	public void method_1884(int i) {
		int j = this.text.length();
		this.cursorMin = MathHelper.clamp(i, 0, j);
		if (this.textRenderer != null) {
			if (this.field_2103 > j) {
				this.field_2103 = j;
			}

			int k = this.method_1859();
			String string = this.textRenderer.trimToWidth(this.text.substring(this.field_2103), k);
			int l = string.length() + this.field_2103;
			if (this.cursorMin == this.field_2103) {
				this.field_2103 = this.field_2103 - this.textRenderer.trimToWidth(this.text, k, true).length();
			}

			if (this.cursorMin > l) {
				this.field_2103 = this.field_2103 + (this.cursorMin - l);
			} else if (this.cursorMin <= this.field_2103) {
				this.field_2103 = this.field_2103 - (this.field_2103 - this.cursorMin);
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

	public void setVisible(boolean bl) {
		this.visible = bl;
	}

	public void setSuggestion(@Nullable String string) {
		this.suggestion = string;
	}

	public int method_1889(int i) {
		return i > this.text.length() ? this.x : this.x + this.textRenderer.getStringWidth(this.text.substring(0, i));
	}

	public void setX(int i) {
		this.x = i;
	}
}
