package net.minecraft.client.gui.hud;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Deque;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.options.ChatVisibility;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ChatHud extends DrawableHelper {
	private static final Logger LOGGER = LogManager.getLogger();
	private final MinecraftClient client;
	private final List<String> messageHistory = Lists.<String>newArrayList();
	private final List<ChatHudLine<Text>> messages = Lists.<ChatHudLine<Text>>newArrayList();
	private final List<ChatHudLine<OrderedText>> visibleMessages = Lists.<ChatHudLine<OrderedText>>newArrayList();
	private final Deque<Text> field_23934 = Queues.<Text>newArrayDeque();
	private int scrolledLines;
	private boolean hasUnreadNewMessages;
	private long field_23935 = 0L;

	public ChatHud(MinecraftClient client) {
		this.client = client;
	}

	public void render(MatrixStack matrices, int tickDelta) {
		if (!this.isChatHidden()) {
			this.method_27149();
			int i = this.getVisibleLineCount();
			int j = this.visibleMessages.size();
			if (j > 0) {
				boolean bl = false;
				if (this.isChatFocused()) {
					bl = true;
				}

				double d = this.getChatScale();
				int k = MathHelper.ceil((double)this.getWidth() / d);
				RenderSystem.pushMatrix();
				RenderSystem.translatef(2.0F, 8.0F, 0.0F);
				RenderSystem.scaled(d, d, 1.0);
				double e = this.client.options.chatOpacity * 0.9F + 0.1F;
				double f = this.client.options.textBackgroundOpacity;
				double g = 9.0 * (this.client.options.chatLineSpacing + 1.0);
				double h = -8.0 * (this.client.options.chatLineSpacing + 1.0) + 4.0 * this.client.options.chatLineSpacing;
				int l = 0;

				for (int m = 0; m + this.scrolledLines < this.visibleMessages.size() && m < i; m++) {
					ChatHudLine<OrderedText> chatHudLine = (ChatHudLine<OrderedText>)this.visibleMessages.get(m + this.scrolledLines);
					if (chatHudLine != null) {
						int n = tickDelta - chatHudLine.getCreationTick();
						if (n < 200 || bl) {
							double o = bl ? 1.0 : getMessageOpacityMultiplier(n);
							int p = (int)(255.0 * o * e);
							int q = (int)(255.0 * o * f);
							l++;
							if (p > 3) {
								int r = 0;
								double s = (double)(-m) * g;
								matrices.push();
								matrices.translate(0.0, 0.0, 50.0);
								fill(matrices, -2, (int)(s - g), 0 + k + 4, (int)s, q << 24);
								RenderSystem.enableBlend();
								matrices.translate(0.0, 0.0, 50.0);
								this.client.textRenderer.drawWithShadow(matrices, chatHudLine.getText(), 0.0F, (float)((int)(s + h)), 16777215 + (p << 24));
								RenderSystem.disableAlphaTest();
								RenderSystem.disableBlend();
								matrices.pop();
							}
						}
					}
				}

				if (!this.field_23934.isEmpty()) {
					int mx = (int)(128.0 * e);
					int t = (int)(255.0 * f);
					matrices.push();
					matrices.translate(0.0, 0.0, 50.0);
					fill(matrices, -2, 0, k + 4, 9, t << 24);
					RenderSystem.enableBlend();
					matrices.translate(0.0, 0.0, 50.0);
					this.client.textRenderer.drawWithShadow(matrices, new TranslatableText("chat.queue", this.field_23934.size()), 0.0F, 1.0F, 16777215 + (mx << 24));
					matrices.pop();
					RenderSystem.disableAlphaTest();
					RenderSystem.disableBlend();
				}

				if (bl) {
					int mx = 9;
					RenderSystem.translatef(-3.0F, 0.0F, 0.0F);
					int t = j * mx + j;
					int n = l * mx + l;
					int u = this.scrolledLines * n / j;
					int v = n * n / t;
					if (t != n) {
						int p = u > 0 ? 170 : 96;
						int q = this.hasUnreadNewMessages ? 13382451 : 3355562;
						fill(matrices, 0, -u, 2, -u - v, q + (p << 24));
						fill(matrices, 2, -u, 1, -u - v, 13421772 + (p << 24));
					}
				}

				RenderSystem.popMatrix();
			}
		}
	}

	private boolean isChatHidden() {
		return this.client.options.chatVisibility == ChatVisibility.HIDDEN;
	}

	private static double getMessageOpacityMultiplier(int age) {
		double d = (double)age / 200.0;
		d = 1.0 - d;
		d *= 10.0;
		d = MathHelper.clamp(d, 0.0, 1.0);
		return d * d;
	}

	public void clear(boolean clearHistory) {
		this.visibleMessages.clear();
		this.messages.clear();
		if (clearHistory) {
			this.messageHistory.clear();
		}
	}

	public void addMessage(Text message) {
		this.addMessage(message, 0);
	}

	private void addMessage(Text message, int messageId) {
		this.addMessage(message, messageId, this.client.inGameHud.getTicks(), false);
		LOGGER.info("[CHAT] {}", message.getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
	}

	private void addMessage(Text text, int messageId, int timestamp, boolean bl) {
		if (messageId != 0) {
			this.removeMessage(messageId);
		}

		int i = MathHelper.floor((double)this.getWidth() / this.getChatScale());
		List<OrderedText> list = ChatMessages.breakRenderedChatMessageLines(text, i, this.client.textRenderer);
		boolean bl2 = this.isChatFocused();

		for (OrderedText orderedText : list) {
			if (bl2 && this.scrolledLines > 0) {
				this.hasUnreadNewMessages = true;
				this.scroll(1.0);
			}

			this.visibleMessages.add(0, new ChatHudLine<>(timestamp, orderedText, messageId));
		}

		while (this.visibleMessages.size() > 100) {
			this.visibleMessages.remove(this.visibleMessages.size() - 1);
		}

		if (!bl) {
			this.messages.add(0, new ChatHudLine<>(timestamp, text, messageId));

			while (this.messages.size() > 100) {
				this.messages.remove(this.messages.size() - 1);
			}
		}
	}

	public void reset() {
		this.visibleMessages.clear();
		this.resetScroll();

		for (int i = this.messages.size() - 1; i >= 0; i--) {
			ChatHudLine<Text> chatHudLine = (ChatHudLine<Text>)this.messages.get(i);
			this.addMessage(chatHudLine.getText(), chatHudLine.getId(), chatHudLine.getCreationTick(), true);
		}
	}

	public List<String> getMessageHistory() {
		return this.messageHistory;
	}

	public void addToMessageHistory(String message) {
		if (this.messageHistory.isEmpty() || !((String)this.messageHistory.get(this.messageHistory.size() - 1)).equals(message)) {
			this.messageHistory.add(message);
		}
	}

	public void resetScroll() {
		this.scrolledLines = 0;
		this.hasUnreadNewMessages = false;
	}

	public void scroll(double amount) {
		this.scrolledLines = (int)((double)this.scrolledLines + amount);
		int i = this.visibleMessages.size();
		if (this.scrolledLines > i - this.getVisibleLineCount()) {
			this.scrolledLines = i - this.getVisibleLineCount();
		}

		if (this.scrolledLines <= 0) {
			this.scrolledLines = 0;
			this.hasUnreadNewMessages = false;
		}
	}

	public boolean method_27146(double d, double e) {
		if (this.isChatFocused() && !this.client.options.hudHidden && !this.isChatHidden() && !this.field_23934.isEmpty()) {
			double f = d - 2.0;
			double g = (double)this.client.getWindow().getScaledHeight() - e - 40.0;
			if (f <= (double)MathHelper.floor((double)this.getWidth() / this.getChatScale()) && g < 0.0 && g > (double)MathHelper.floor(-9.0 * this.getChatScale())) {
				this.addMessage((Text)this.field_23934.remove());
				this.field_23935 = System.currentTimeMillis();
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Nullable
	public Style getText(double x, double y) {
		if (this.isChatFocused() && !this.client.options.hudHidden && !this.isChatHidden()) {
			double d = x - 2.0;
			double e = (double)this.client.getWindow().getScaledHeight() - y - 40.0;
			d = (double)MathHelper.floor(d / this.getChatScale());
			e = (double)MathHelper.floor(e / (this.getChatScale() * (this.client.options.chatLineSpacing + 1.0)));
			if (!(d < 0.0) && !(e < 0.0)) {
				int i = Math.min(this.getVisibleLineCount(), this.visibleMessages.size());
				if (d <= (double)MathHelper.floor((double)this.getWidth() / this.getChatScale()) && e < (double)(9 * i + i)) {
					int j = (int)(e / 9.0 + (double)this.scrolledLines);
					if (j >= 0 && j < this.visibleMessages.size()) {
						ChatHudLine<OrderedText> chatHudLine = (ChatHudLine<OrderedText>)this.visibleMessages.get(j);
						return this.client.textRenderer.getTextHandler().method_30876(chatHudLine.getText(), (int)d);
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

	private boolean isChatFocused() {
		return this.client.currentScreen instanceof ChatScreen;
	}

	private void removeMessage(int messageId) {
		this.visibleMessages.removeIf(chatHudLine -> chatHudLine.getId() == messageId);
		this.messages.removeIf(chatHudLine -> chatHudLine.getId() == messageId);
	}

	public int getWidth() {
		return getWidth(this.client.options.chatWidth);
	}

	public int getHeight() {
		return getHeight(
			(this.isChatFocused() ? this.client.options.chatHeightFocused : this.client.options.chatHeightUnfocused) / (this.client.options.chatLineSpacing + 1.0)
		);
	}

	public double getChatScale() {
		return this.client.options.chatScale;
	}

	public static int getWidth(double widthOption) {
		int i = 320;
		int j = 40;
		return MathHelper.floor(widthOption * 280.0 + 40.0);
	}

	public static int getHeight(double heightOption) {
		int i = 180;
		int j = 20;
		return MathHelper.floor(heightOption * 160.0 + 20.0);
	}

	public int getVisibleLineCount() {
		return this.getHeight() / 9;
	}

	private long method_27148() {
		return (long)(this.client.options.chatDelay * 1000.0);
	}

	private void method_27149() {
		if (!this.field_23934.isEmpty()) {
			long l = System.currentTimeMillis();
			if (l - this.field_23935 >= this.method_27148()) {
				this.addMessage((Text)this.field_23934.remove());
				this.field_23935 = l;
			}
		}
	}

	public void method_27147(Text text) {
		if (this.client.options.chatDelay <= 0.0) {
			this.addMessage(text);
		} else {
			long l = System.currentTimeMillis();
			if (l - this.field_23935 >= this.method_27148()) {
				this.addMessage(text);
				this.field_23935 = l;
			} else {
				this.field_23934.add(text);
			}
		}
	}
}
