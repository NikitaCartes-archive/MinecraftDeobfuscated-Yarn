package net.minecraft.client.gui.widget;

import com.google.common.base.Predicates;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.FontRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiEventListener;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexBuffer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class TextFieldWidget extends Drawable implements GuiEventListener {
	private final int id;
	private final FontRenderer fontRenderer;
	private int x;
	private int y;
	private final int width;
	private final int height;
	private String text = "";
	private int maxLength = 32;
	private int focusedTicks;
	private boolean field_2097 = true;
	private boolean field_2096 = true;
	private boolean focused;
	private boolean field_2094 = true;
	private boolean field_17037 = false;
	private int field_2103;
	private int field_2102;
	private int field_2101;
	private int field_2100 = 14737632;
	private int field_2098 = 7368816;
	private boolean visible = true;
	private String field_2106;
	private BiConsumer<Integer, String> field_2088;
	private Predicate<String> field_2104 = Predicates.alwaysTrue();
	private BiFunction<String, Integer, String> field_2099 = (string, integer) -> string;

	public TextFieldWidget(int i, FontRenderer fontRenderer, int j, int k, int l, int m) {
		this(i, fontRenderer, j, k, l, m, null);
	}

	public TextFieldWidget(int i, FontRenderer fontRenderer, int j, int k, int l, int m, @Nullable TextFieldWidget textFieldWidget) {
		this.id = i;
		this.fontRenderer = fontRenderer;
		this.x = j;
		this.y = k;
		this.width = l;
		this.height = m;
		if (textFieldWidget != null) {
			this.setText(textFieldWidget.getText());
		}
	}

	public void method_1863(BiConsumer<Integer, String> biConsumer) {
		this.field_2088 = biConsumer;
	}

	public void method_1854(BiFunction<String, Integer, String> biFunction) {
		this.field_2099 = biFunction;
	}

	public void tick() {
		this.focusedTicks++;
	}

	public void setText(String string) {
		if (this.field_2104.test(string)) {
			if (string.length() > this.maxLength) {
				this.text = string.substring(0, this.maxLength);
			} else {
				this.text = string;
			}

			this.method_1874(this.id, string);
			this.method_1872();
		}
	}

	public String getText() {
		return this.text;
	}

	public String getSelectedText() {
		int i = this.field_2102 < this.field_2101 ? this.field_2102 : this.field_2101;
		int j = this.field_2102 < this.field_2101 ? this.field_2101 : this.field_2102;
		return this.text.substring(i, j);
	}

	public void method_1890(Predicate<String> predicate) {
		this.field_2104 = predicate;
	}

	public void addText(String string) {
		String string2 = "";
		String string3 = SharedConstants.stripInvalidChars(string);
		int i = this.field_2102 < this.field_2101 ? this.field_2102 : this.field_2101;
		int j = this.field_2102 < this.field_2101 ? this.field_2101 : this.field_2102;
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

		if (this.field_2104.test(string2)) {
			this.text = string2;
			this.method_1875(i + l);
			this.method_1884(this.field_2102);
			this.method_1874(this.id, this.text);
		}
	}

	public void method_1874(int i, String string) {
		if (this.field_2088 != null) {
			this.field_2088.accept(i, string);
		}
	}

	private void method_16873(int i) {
		if (Gui.isControlPressed()) {
			this.method_1877(i);
		} else {
			this.method_1878(i);
		}
	}

	public void method_1877(int i) {
		if (!this.text.isEmpty()) {
			if (this.field_2101 != this.field_2102) {
				this.addText("");
			} else {
				this.method_1878(this.method_1853(i) - this.field_2102);
			}
		}
	}

	public void method_1878(int i) {
		if (!this.text.isEmpty()) {
			if (this.field_2101 != this.field_2102) {
				this.addText("");
			} else {
				boolean bl = i < 0;
				int j = bl ? this.field_2102 + i : this.field_2102;
				int k = bl ? this.field_2102 : this.field_2102 + i;
				String string = "";
				if (j >= 0) {
					string = this.text.substring(0, j);
				}

				if (k < this.text.length()) {
					string = string + this.text.substring(k);
				}

				if (this.field_2104.test(string)) {
					this.text = string;
					if (bl) {
						this.method_1855(i);
					}

					this.method_1874(this.id, this.text);
				}
			}
		}
	}

	public int method_1853(int i) {
		return this.method_1869(i, this.getId());
	}

	public int method_1869(int i, int j) {
		return this.method_1864(i, j, true);
	}

	public int method_1864(int i, int j, boolean bl) {
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

	public void method_1855(int i) {
		this.method_1883(this.field_2102 + i);
	}

	public void method_1883(int i) {
		this.method_1875(i);
		if (!this.field_17037) {
			this.method_1884(this.field_2102);
		}

		this.method_1874(this.id, this.text);
	}

	public void method_1875(int i) {
		this.field_2102 = MathHelper.clamp(i, 0, this.text.length());
	}

	public void method_1870() {
		this.method_1883(0);
	}

	public void method_1872() {
		this.method_1883(this.text.length());
	}

	@Override
	public boolean keyPressed(int i, int j, int k) {
		if (this.getVisible() && this.isFocused()) {
			this.field_17037 = Gui.isShiftPressed();
			if (Gui.isSelectAllShortcutPressed(i)) {
				this.method_1872();
				this.method_1884(0);
				return true;
			} else if (Gui.isCopyShortcutPressed(i)) {
				MinecraftClient.getInstance().keyboard.setClipbord(this.getSelectedText());
				return true;
			} else if (Gui.isPasteShortcutPressed(i)) {
				if (this.field_2094) {
					this.addText(MinecraftClient.getInstance().keyboard.getClipboard());
				}

				return true;
			} else if (Gui.isCutShortcutPressed(i)) {
				MinecraftClient.getInstance().keyboard.setClipbord(this.getSelectedText());
				if (this.field_2094) {
					this.addText("");
				}

				return true;
			} else {
				switch (i) {
					case 259:
						if (this.field_2094) {
							this.method_16873(-1);
						}

						return true;
					case 260:
					case 264:
					case 265:
					case 266:
					case 267:
					default:
						return i != 256;
					case 261:
						if (this.field_2094) {
							this.method_16873(1);
						}

						return true;
					case 262:
						if (Gui.isControlPressed()) {
							this.method_1883(this.method_1853(1));
						} else {
							this.method_1855(1);
						}

						return true;
					case 263:
						if (Gui.isControlPressed()) {
							this.method_1883(this.method_1853(-1));
						} else {
							this.method_1855(-1);
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
		} else {
			return false;
		}
	}

	@Override
	public boolean charTyped(char c, int i) {
		if (!this.getVisible() || !this.isFocused()) {
			return false;
		} else if (SharedConstants.isValidChar(c)) {
			if (this.field_2094) {
				this.addText(Character.toString(c));
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		if (!this.getVisible()) {
			return false;
		} else {
			boolean bl = d >= (double)this.x && d < (double)(this.x + this.width) && e >= (double)this.y && e < (double)(this.y + this.height);
			if (this.field_2096) {
				this.method_1876(bl);
			}

			if (this.focused && bl && i == 0) {
				int j = MathHelper.floor(d) - this.x;
				if (this.field_2097) {
					j -= 4;
				}

				String string = this.fontRenderer.method_1714(this.text.substring(this.field_2103), this.method_1859());
				this.method_1883(this.fontRenderer.method_1714(string, j).length() + this.field_2103);
				return true;
			} else {
				return false;
			}
		}
	}

	public void render(int i, int j, float f) {
		if (this.getVisible()) {
			if (this.method_1851()) {
				drawRect(this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, -6250336);
				drawRect(this.x, this.y, this.x + this.width, this.y + this.height, -16777216);
			}

			int k = this.field_2094 ? this.field_2100 : this.field_2098;
			int l = this.field_2102 - this.field_2103;
			int m = this.field_2101 - this.field_2103;
			String string = this.fontRenderer.method_1714(this.text.substring(this.field_2103), this.method_1859());
			boolean bl = l >= 0 && l <= string.length();
			boolean bl2 = this.focused && this.focusedTicks / 6 % 2 == 0 && bl;
			int n = this.field_2097 ? this.x + 4 : this.x;
			int o = this.field_2097 ? this.y + (this.height - 8) / 2 : this.y;
			int p = n;
			if (m > string.length()) {
				m = string.length();
			}

			if (!string.isEmpty()) {
				String string2 = bl ? string.substring(0, l) : string;
				p = this.fontRenderer.drawWithShadow((String)this.field_2099.apply(string2, this.field_2103), (float)n, (float)o, k);
			}

			boolean bl3 = this.field_2102 < this.text.length() || this.text.length() >= this.getMaxLength();
			int q = p;
			if (!bl) {
				q = l > 0 ? n + this.width : n;
			} else if (bl3) {
				q = p - 1;
				p--;
			}

			if (!string.isEmpty() && bl && l < string.length()) {
				this.fontRenderer.drawWithShadow((String)this.field_2099.apply(string.substring(l), this.field_2102), (float)p, (float)o, k);
			}

			if (!bl3 && this.field_2106 != null) {
				this.fontRenderer.drawWithShadow(this.field_2106, (float)(q - 1), (float)o, -8355712);
			}

			if (bl2) {
				if (bl3) {
					Drawable.drawRect(q, o - 1, q + 1, o + 1 + this.fontRenderer.FONT_HEIGHT, -3092272);
				} else {
					this.fontRenderer.drawWithShadow("_", (float)q, (float)o, k);
				}
			}

			if (m != l) {
				int r = n + this.fontRenderer.getStringWidth(string.substring(0, m));
				this.method_1886(q, o - 1, r - 1, o + 1 + this.fontRenderer.FONT_HEIGHT);
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
		VertexBuffer vertexBuffer = tessellator.getVertexBuffer();
		GlStateManager.color4f(0.0F, 0.0F, 255.0F, 255.0F);
		GlStateManager.disableTexture();
		GlStateManager.enableColorLogicOp();
		GlStateManager.logicOp(GlStateManager.LogicOp.OR_REVERSE);
		vertexBuffer.begin(7, VertexFormats.POSITION);
		vertexBuffer.vertex((double)i, (double)l, 0.0).next();
		vertexBuffer.vertex((double)k, (double)l, 0.0).next();
		vertexBuffer.vertex((double)k, (double)j, 0.0).next();
		vertexBuffer.vertex((double)i, (double)j, 0.0).next();
		tessellator.draw();
		GlStateManager.disableColorLogicOp();
		GlStateManager.enableTexture();
	}

	public void setMaxLength(int i) {
		this.maxLength = i;
		if (this.text.length() > i) {
			this.text = this.text.substring(0, i);
			this.method_1874(this.id, this.text);
		}
	}

	public int getMaxLength() {
		return this.maxLength;
	}

	public int getId() {
		return this.field_2102;
	}

	public boolean method_1851() {
		return this.field_2097;
	}

	public void method_1858(boolean bl) {
		this.field_2097 = bl;
	}

	public void method_1868(int i) {
		this.field_2100 = i;
	}

	public void method_1860(int i) {
		this.field_2098 = i;
	}

	@Override
	public void setHasFocus(boolean bl) {
		this.method_1876(bl);
	}

	@Override
	public boolean hasFocus() {
		return true;
	}

	public void method_1876(boolean bl) {
		if (bl && !this.focused) {
			this.focusedTicks = 0;
		}

		this.focused = bl;
	}

	public boolean isFocused() {
		return this.focused;
	}

	public void setIsEditable(boolean bl) {
		this.field_2094 = bl;
	}

	public int method_1859() {
		return this.method_1851() ? this.width - 8 : this.width;
	}

	public void method_1884(int i) {
		int j = this.text.length();
		this.field_2101 = MathHelper.clamp(i, 0, j);
		if (this.fontRenderer != null) {
			if (this.field_2103 > j) {
				this.field_2103 = j;
			}

			int k = this.method_1859();
			String string = this.fontRenderer.method_1714(this.text.substring(this.field_2103), k);
			int l = string.length() + this.field_2103;
			if (this.field_2101 == this.field_2103) {
				this.field_2103 = this.field_2103 - this.fontRenderer.method_1711(this.text, k, true).length();
			}

			if (this.field_2101 > l) {
				this.field_2103 = this.field_2103 + (this.field_2101 - l);
			} else if (this.field_2101 <= this.field_2103) {
				this.field_2103 = this.field_2103 - (this.field_2103 - this.field_2101);
			}

			this.field_2103 = MathHelper.clamp(this.field_2103, 0, j);
		}
	}

	public void method_1856(boolean bl) {
		this.field_2096 = bl;
	}

	public boolean getVisible() {
		return this.visible;
	}

	public void setVisible(boolean bl) {
		this.visible = bl;
	}

	public void method_1887(@Nullable String string) {
		this.field_2106 = string;
	}

	public int method_1889(int i) {
		return i > this.text.length() ? this.x : this.x + this.fontRenderer.getStringWidth(this.text.substring(0, i));
	}

	public void method_16872(int i) {
		this.x = i;
	}
}
