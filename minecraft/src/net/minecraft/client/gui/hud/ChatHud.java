package net.minecraft.client.gui.hud;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.ingame.ChatGui;
import net.minecraft.client.util.TextComponentUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ChatHud extends Drawable {
	private static final Logger LOGGER = LogManager.getLogger();
	private final MinecraftClient client;
	private final List<String> field_2063 = Lists.<String>newArrayList();
	private final List<ChatHudLine> field_2061 = Lists.<ChatHudLine>newArrayList();
	private final List<ChatHudLine> field_2064 = Lists.<ChatHudLine>newArrayList();
	private int field_2066;
	private boolean field_2067;

	public ChatHud(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	public void method_1805(int i) {
		if (this.client.options.chatVisibility != PlayerEntity.ChatVisibility.HIDDEN) {
			int j = this.method_1813();
			int k = this.field_2064.size();
			double d = this.client.options.chatOpacity * 0.9F + 0.1F;
			if (k > 0) {
				boolean bl = false;
				if (this.isChatFocused()) {
					bl = true;
				}

				double e = this.getScale();
				int l = MathHelper.ceil((double)this.getWidth() / e);
				GlStateManager.pushMatrix();
				GlStateManager.translatef(2.0F, 8.0F, 0.0F);
				GlStateManager.scaled(e, e, 1.0);
				int m = 0;

				for (int n = 0; n + this.field_2066 < this.field_2064.size() && n < j; n++) {
					ChatHudLine chatHudLine = (ChatHudLine)this.field_2064.get(n + this.field_2066);
					if (chatHudLine != null) {
						int o = i - chatHudLine.getTickCreated();
						if (o < 200 || bl) {
							double f = (double)o / 200.0;
							f = 1.0 - f;
							f *= 10.0;
							f = MathHelper.clamp(f, 0.0, 1.0);
							f *= f;
							int p = (int)(255.0 * f);
							if (bl) {
								p = 255;
							}

							p = (int)((double)p * d);
							m++;
							if (p > 3) {
								int q = 0;
								int r = -n * 9;
								drawRect(-2, r - 9, 0 + l + 4, r, p / 2 << 24);
								String string = chatHudLine.getContents().getFormattedText();
								GlStateManager.enableBlend();
								this.client.fontRenderer.drawWithShadow(string, 0.0F, (float)(r - 8), 16777215 + (p << 24));
								GlStateManager.disableAlphaTest();
								GlStateManager.disableBlend();
							}
						}
					}
				}

				if (bl) {
					int nx = 9;
					GlStateManager.translatef(-3.0F, 0.0F, 0.0F);
					int s = k * nx + k;
					int o = m * nx + m;
					int t = this.field_2066 * o / k;
					int u = o * o / s;
					if (s != o) {
						int px = t > 0 ? 170 : 96;
						int q = this.field_2067 ? 13382451 : 3355562;
						drawRect(0, -t, 2, -t - u, q + (px << 24));
						drawRect(2, -t, 1, -t - u, 13421772 + (px << 24));
					}
				}

				GlStateManager.popMatrix();
			}
		}
	}

	public void clear(boolean bl) {
		this.field_2064.clear();
		this.field_2061.clear();
		if (bl) {
			this.field_2063.clear();
		}
	}

	public void addMessage(TextComponent textComponent) {
		this.addMessage(textComponent, 0);
	}

	public void addMessage(TextComponent textComponent, int i) {
		this.addMessage(textComponent, i, this.client.inGameHud.getTicks(), false);
		LOGGER.info("[CHAT] {}", textComponent.getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
	}

	private void addMessage(TextComponent textComponent, int i, int j, boolean bl) {
		if (i != 0) {
			this.removeMessage(i);
		}

		int k = MathHelper.floor((double)this.getWidth() / this.getScale());
		List<TextComponent> list = TextComponentUtil.wrapLines(textComponent, k, this.client.fontRenderer, false, false);
		boolean bl2 = this.isChatFocused();

		for (TextComponent textComponent2 : list) {
			if (bl2 && this.field_2066 > 0) {
				this.field_2067 = true;
				this.method_1802(1.0);
			}

			this.field_2064.add(0, new ChatHudLine(j, textComponent2, i));
		}

		while (this.field_2064.size() > 100) {
			this.field_2064.remove(this.field_2064.size() - 1);
		}

		if (!bl) {
			this.field_2061.add(0, new ChatHudLine(j, textComponent, i));

			while (this.field_2061.size() > 100) {
				this.field_2061.remove(this.field_2061.size() - 1);
			}
		}
	}

	public void reset() {
		this.field_2064.clear();
		this.method_1820();

		for (int i = this.field_2061.size() - 1; i >= 0; i--) {
			ChatHudLine chatHudLine = (ChatHudLine)this.field_2061.get(i);
			this.addMessage(chatHudLine.getContents(), chatHudLine.getId(), chatHudLine.getTickCreated(), true);
		}
	}

	public List<String> method_1809() {
		return this.field_2063;
	}

	public void method_1803(String string) {
		if (this.field_2063.isEmpty() || !((String)this.field_2063.get(this.field_2063.size() - 1)).equals(string)) {
			this.field_2063.add(string);
		}
	}

	public void method_1820() {
		this.field_2066 = 0;
		this.field_2067 = false;
	}

	public void method_1802(double d) {
		this.field_2066 = (int)((double)this.field_2066 + d);
		int i = this.field_2064.size();
		if (this.field_2066 > i - this.method_1813()) {
			this.field_2066 = i - this.method_1813();
		}

		if (this.field_2066 <= 0) {
			this.field_2066 = 0;
			this.field_2067 = false;
		}
	}

	@Nullable
	public TextComponent method_1816(double d, double e) {
		if (!this.isChatFocused()) {
			return null;
		} else {
			double f = this.getScale();
			double g = d - 2.0;
			double h = (double)this.client.window.getScaledHeight() - e - 40.0;
			g = (double)MathHelper.floor(g / f);
			h = (double)MathHelper.floor(h / f);
			if (!(g < 0.0) && !(h < 0.0)) {
				int i = Math.min(this.method_1813(), this.field_2064.size());
				if (g <= (double)MathHelper.floor((double)this.getWidth() / this.getScale()) && h < (double)(9 * i + i)) {
					int j = (int)(h / 9.0 + (double)this.field_2066);
					if (j >= 0 && j < this.field_2064.size()) {
						ChatHudLine chatHudLine = (ChatHudLine)this.field_2064.get(j);
						int k = 0;

						for (TextComponent textComponent : chatHudLine.getContents()) {
							if (textComponent instanceof StringTextComponent) {
								k += this.client.fontRenderer.getStringWidth(TextComponentUtil.method_1849(((StringTextComponent)textComponent).getTextField(), false));
								if ((double)k > g) {
									return textComponent;
								}
							}
						}
					}

					return null;
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
	}

	public boolean isChatFocused() {
		return this.client.currentGui instanceof ChatGui;
	}

	public void removeMessage(int i) {
		Iterator<ChatHudLine> iterator = this.field_2064.iterator();

		while (iterator.hasNext()) {
			ChatHudLine chatHudLine = (ChatHudLine)iterator.next();
			if (chatHudLine.getId() == i) {
				iterator.remove();
			}
		}

		iterator = this.field_2061.iterator();

		while (iterator.hasNext()) {
			ChatHudLine chatHudLine = (ChatHudLine)iterator.next();
			if (chatHudLine.getId() == i) {
				iterator.remove();
				break;
			}
		}
	}

	public int getWidth() {
		return method_1806(this.client.options.chatWidth);
	}

	public int getHeight() {
		return method_1818(this.isChatFocused() ? this.client.options.chatHeightFocused : this.client.options.chatHeightUnfocused);
	}

	public double getScale() {
		return this.client.options.chatScale;
	}

	public static int method_1806(double d) {
		int i = 320;
		int j = 40;
		return MathHelper.floor(d * 280.0 + 40.0);
	}

	public static int method_1818(double d) {
		int i = 180;
		int j = 20;
		return MathHelper.floor(d * 160.0 + 20.0);
	}

	public int method_1813() {
		return this.getHeight() / 9;
	}
}
