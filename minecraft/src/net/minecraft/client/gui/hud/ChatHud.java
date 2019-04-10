package net.minecraft.client.gui.hud;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.ingame.ChatScreen;
import net.minecraft.client.options.ChatVisibility;
import net.minecraft.client.util.TextComponentUtil;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ChatHud extends DrawableHelper {
	private static final Logger LOGGER = LogManager.getLogger();
	private final MinecraftClient client;
	private final List<String> field_2063 = Lists.<String>newArrayList();
	private final List<ChatHudLine> messages = Lists.<ChatHudLine>newArrayList();
	private final List<ChatHudLine> visibleMessages = Lists.<ChatHudLine>newArrayList();
	private int field_2066;
	private boolean field_2067;

	public ChatHud(MinecraftClient minecraftClient) {
		this.client = minecraftClient;
	}

	public void draw(int i) {
		if (this.client.options.chatVisibility != ChatVisibility.HIDDEN) {
			int j = this.getVisibleLineCount();
			int k = this.visibleMessages.size();
			if (k > 0) {
				boolean bl = false;
				if (this.isChatFocused()) {
					bl = true;
				}

				double d = this.getScale();
				int l = MathHelper.ceil((double)this.getWidth() / d);
				GlStateManager.pushMatrix();
				GlStateManager.translatef(2.0F, 8.0F, 0.0F);
				GlStateManager.scaled(d, d, 1.0);
				double e = this.client.options.chatOpacity * 0.9F + 0.1F;
				double f = this.client.options.textBackgroundOpacity;
				int m = 0;

				for (int n = 0; n + this.field_2066 < this.visibleMessages.size() && n < j; n++) {
					ChatHudLine chatHudLine = (ChatHudLine)this.visibleMessages.get(n + this.field_2066);
					if (chatHudLine != null) {
						int o = i - chatHudLine.getTickCreated();
						if (o < 200 || bl) {
							double g = bl ? 1.0 : method_19348(o);
							int p = (int)(255.0 * g * e);
							int q = (int)(255.0 * g * f);
							m++;
							if (p > 3) {
								int r = 0;
								int s = -n * 9;
								fill(-2, s - 9, 0 + l + 4, s, q << 24);
								String string = chatHudLine.getContents().getFormattedText();
								GlStateManager.enableBlend();
								this.client.textRenderer.drawWithShadow(string, 0.0F, (float)(s - 8), 16777215 + (p << 24));
								GlStateManager.disableAlphaTest();
								GlStateManager.disableBlend();
							}
						}
					}
				}

				if (bl) {
					int nx = 9;
					GlStateManager.translatef(-3.0F, 0.0F, 0.0F);
					int t = k * nx + k;
					int o = m * nx + m;
					int u = this.field_2066 * o / k;
					int v = o * o / t;
					if (t != o) {
						int p = u > 0 ? 170 : 96;
						int q = this.field_2067 ? 13382451 : 3355562;
						fill(0, -u, 2, -u - v, q + (p << 24));
						fill(2, -u, 1, -u - v, 13421772 + (p << 24));
					}
				}

				GlStateManager.popMatrix();
			}
		}
	}

	private static double method_19348(int i) {
		double d = (double)i / 200.0;
		d = 1.0 - d;
		d *= 10.0;
		d = MathHelper.clamp(d, 0.0, 1.0);
		return d * d;
	}

	public void clear(boolean bl) {
		this.visibleMessages.clear();
		this.messages.clear();
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
		List<TextComponent> list = TextComponentUtil.wrapLines(textComponent, k, this.client.textRenderer, false, false);
		boolean bl2 = this.isChatFocused();

		for (TextComponent textComponent2 : list) {
			if (bl2 && this.field_2066 > 0) {
				this.field_2067 = true;
				this.method_1802(1.0);
			}

			this.visibleMessages.add(0, new ChatHudLine(j, textComponent2, i));
		}

		while (this.visibleMessages.size() > 100) {
			this.visibleMessages.remove(this.visibleMessages.size() - 1);
		}

		if (!bl) {
			this.messages.add(0, new ChatHudLine(j, textComponent, i));

			while (this.messages.size() > 100) {
				this.messages.remove(this.messages.size() - 1);
			}
		}
	}

	public void reset() {
		this.visibleMessages.clear();
		this.method_1820();

		for (int i = this.messages.size() - 1; i >= 0; i--) {
			ChatHudLine chatHudLine = (ChatHudLine)this.messages.get(i);
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
		int i = this.visibleMessages.size();
		if (this.field_2066 > i - this.getVisibleLineCount()) {
			this.field_2066 = i - this.getVisibleLineCount();
		}

		if (this.field_2066 <= 0) {
			this.field_2066 = 0;
			this.field_2067 = false;
		}
	}

	@Nullable
	public TextComponent getTextComponentAt(double d, double e) {
		if (!this.isChatFocused()) {
			return null;
		} else {
			double f = this.getScale();
			double g = d - 2.0;
			double h = (double)this.client.window.getScaledHeight() - e - 40.0;
			g = (double)MathHelper.floor(g / f);
			h = (double)MathHelper.floor(h / f);
			if (!(g < 0.0) && !(h < 0.0)) {
				int i = Math.min(this.getVisibleLineCount(), this.visibleMessages.size());
				if (g <= (double)MathHelper.floor((double)this.getWidth() / this.getScale()) && h < (double)(9 * i + i)) {
					int j = (int)(h / 9.0 + (double)this.field_2066);
					if (j >= 0 && j < this.visibleMessages.size()) {
						ChatHudLine chatHudLine = (ChatHudLine)this.visibleMessages.get(j);
						int k = 0;

						for (TextComponent textComponent : chatHudLine.getContents()) {
							if (textComponent instanceof StringTextComponent) {
								k += this.client.textRenderer.getStringWidth(TextComponentUtil.method_1849(((StringTextComponent)textComponent).getTextField(), false));
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
		return this.client.currentScreen instanceof ChatScreen;
	}

	public void removeMessage(int i) {
		Iterator<ChatHudLine> iterator = this.visibleMessages.iterator();

		while (iterator.hasNext()) {
			ChatHudLine chatHudLine = (ChatHudLine)iterator.next();
			if (chatHudLine.getId() == i) {
				iterator.remove();
			}
		}

		iterator = this.messages.iterator();

		while (iterator.hasNext()) {
			ChatHudLine chatHudLine = (ChatHudLine)iterator.next();
			if (chatHudLine.getId() == i) {
				iterator.remove();
				break;
			}
		}
	}

	public int getWidth() {
		return getWidth(this.client.options.chatWidth);
	}

	public int getHeight() {
		return getHeight(this.isChatFocused() ? this.client.options.chatHeightFocused : this.client.options.chatHeightUnfocused);
	}

	public double getScale() {
		return this.client.options.chatScale;
	}

	public static int getWidth(double d) {
		int i = 320;
		int j = 40;
		return MathHelper.floor(d * 280.0 + 40.0);
	}

	public static int getHeight(double d) {
		int i = 180;
		int j = 20;
		return MathHelper.floor(d * 160.0 + 20.0);
	}

	public int getVisibleLineCount() {
		return this.getHeight() / 9;
	}
}
