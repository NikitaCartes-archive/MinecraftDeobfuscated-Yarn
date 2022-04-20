package net.minecraft.client.gui.hud;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.util.Deque;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.option.ChatVisibility;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.slf4j.Logger;

/**
 * Responsible for rendering various game messages such as chat messages or
 * join/leave messages.
 * 
 * @see net.minecraft.client.gui.screen.ChatScreen
 */
@Environment(EnvType.CLIENT)
public class ChatHud extends DrawableHelper {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final int MAX_MESSAGES = 100;
	private final MinecraftClient client;
	private final List<String> messageHistory = Lists.<String>newArrayList();
	private final List<ChatHudLine<Text>> messages = Lists.<ChatHudLine<Text>>newArrayList();
	private final List<ChatHudLine<OrderedText>> visibleMessages = Lists.<ChatHudLine<OrderedText>>newArrayList();
	private final Deque<Text> messageQueue = Queues.<Text>newArrayDeque();
	private int scrolledLines;
	private boolean hasUnreadNewMessages;
	private long lastMessageAddedTime;

	public ChatHud(MinecraftClient client) {
		this.client = client;
	}

	public void render(MatrixStack matrices, int tickDelta) {
		if (!this.isChatHidden()) {
			this.processMessageQueue();
			int i = this.getVisibleLineCount();
			int j = this.visibleMessages.size();
			if (j > 0) {
				boolean bl = this.isChatFocused();
				float f = (float)this.getChatScale();
				int k = MathHelper.ceil((float)this.getWidth() / f);
				matrices.push();
				matrices.translate(4.0, 8.0, 0.0);
				matrices.scale(f, f, 1.0F);
				double d = this.client.options.getChtOpacity().getValue() * 0.9F + 0.1F;
				double e = this.client.options.getTextBackgroundOpacity().getValue();
				double g = this.client.options.getChatLineSpacing().getValue();
				double h = 9.0 * (g + 1.0);
				double l = -8.0 * (g + 1.0) + 4.0 * g;
				int m = 0;

				for (int n = 0; n + this.scrolledLines < this.visibleMessages.size() && n < i; n++) {
					ChatHudLine<OrderedText> chatHudLine = (ChatHudLine<OrderedText>)this.visibleMessages.get(n + this.scrolledLines);
					if (chatHudLine != null) {
						int o = tickDelta - chatHudLine.getCreationTick();
						if (o < 200 || bl) {
							double p = bl ? 1.0 : getMessageOpacityMultiplier(o);
							int q = (int)(255.0 * p * d);
							int r = (int)(255.0 * p * e);
							m++;
							if (q > 3) {
								int s = 0;
								double t = (double)(-n) * h;
								matrices.push();
								matrices.translate(0.0, 0.0, 50.0);
								fill(matrices, -4, (int)(t - h), 0 + k + 4, (int)t, r << 24);
								RenderSystem.enableBlend();
								matrices.translate(0.0, 0.0, 50.0);
								this.client.textRenderer.drawWithShadow(matrices, chatHudLine.getText(), 0.0F, (float)((int)(t + l)), 16777215 + (q << 24));
								RenderSystem.disableBlend();
								matrices.pop();
							}
						}
					}
				}

				if (!this.messageQueue.isEmpty()) {
					int nx = (int)(128.0 * d);
					int u = (int)(255.0 * e);
					matrices.push();
					matrices.translate(0.0, 0.0, 50.0);
					fill(matrices, -2, 0, k + 4, 9, u << 24);
					RenderSystem.enableBlend();
					matrices.translate(0.0, 0.0, 50.0);
					this.client.textRenderer.drawWithShadow(matrices, Text.method_43469("chat.queue", this.messageQueue.size()), 0.0F, 1.0F, 16777215 + (nx << 24));
					matrices.pop();
					RenderSystem.disableBlend();
				}

				if (bl) {
					int nx = 9;
					int u = j * nx;
					int o = m * nx;
					int v = this.scrolledLines * o / j;
					int w = o * o / u;
					if (u != o) {
						int q = v > 0 ? 170 : 96;
						int r = this.hasUnreadNewMessages ? 13382451 : 3355562;
						matrices.translate(-4.0, 0.0, 0.0);
						fill(matrices, 0, -v, 2, -v - w, r + (q << 24));
						fill(matrices, 2, -v, 1, -v - w, 13421772 + (q << 24));
					}
				}

				matrices.pop();
			}
		}
	}

	private boolean isChatHidden() {
		return this.client.options.getChatVisibility().getValue() == ChatVisibility.HIDDEN;
	}

	private static double getMessageOpacityMultiplier(int age) {
		double d = (double)age / 200.0;
		d = 1.0 - d;
		d *= 10.0;
		d = MathHelper.clamp(d, 0.0, 1.0);
		return d * d;
	}

	public void clear(boolean clearHistory) {
		this.messageQueue.clear();
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

	private void addMessage(Text message, int messageId, int timestamp, boolean refresh) {
		if (messageId != 0) {
			this.removeMessage(messageId);
		}

		int i = MathHelper.floor((double)this.getWidth() / this.getChatScale());
		List<OrderedText> list = ChatMessages.breakRenderedChatMessageLines(message, i, this.client.textRenderer);
		boolean bl = this.isChatFocused();

		for (OrderedText orderedText : list) {
			if (bl && this.scrolledLines > 0) {
				this.hasUnreadNewMessages = true;
				this.scroll(1);
			}

			this.visibleMessages.add(0, new ChatHudLine<>(timestamp, orderedText, messageId));
		}

		while (this.visibleMessages.size() > 100) {
			this.visibleMessages.remove(this.visibleMessages.size() - 1);
		}

		if (!refresh) {
			this.messages.add(0, new ChatHudLine<>(timestamp, message, messageId));

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

	public void scroll(int i) {
		this.scrolledLines += i;
		int j = this.visibleMessages.size();
		if (this.scrolledLines > j - this.getVisibleLineCount()) {
			this.scrolledLines = j - this.getVisibleLineCount();
		}

		if (this.scrolledLines <= 0) {
			this.scrolledLines = 0;
			this.hasUnreadNewMessages = false;
		}
	}

	public boolean mouseClicked(double mouseX, double mouseY) {
		if (this.isChatFocused() && !this.client.options.hudHidden && !this.isChatHidden() && !this.messageQueue.isEmpty()) {
			double d = mouseX - 2.0;
			double e = (double)this.client.getWindow().getScaledHeight() - mouseY - 40.0;
			if (d <= (double)MathHelper.floor((double)this.getWidth() / this.getChatScale()) && e < 0.0 && e > (double)MathHelper.floor(-9.0 * this.getChatScale())) {
				this.addMessage((Text)this.messageQueue.remove());
				this.lastMessageAddedTime = System.currentTimeMillis();
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
			e = (double)MathHelper.floor(e / (this.getChatScale() * (this.client.options.getChatLineSpacing().getValue() + 1.0)));
			if (!(d < 0.0) && !(e < 0.0)) {
				int i = Math.min(this.getVisibleLineCount(), this.visibleMessages.size());
				if (d <= (double)MathHelper.floor((double)this.getWidth() / this.getChatScale()) && e < (double)(9 * i + i)) {
					int j = (int)(e / 9.0 + (double)this.scrolledLines);
					if (j >= 0 && j < this.visibleMessages.size()) {
						ChatHudLine<OrderedText> chatHudLine = (ChatHudLine<OrderedText>)this.visibleMessages.get(j);
						return this.client.textRenderer.getTextHandler().getStyleAt(chatHudLine.getText(), (int)d);
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
		this.visibleMessages.removeIf(message -> message.getId() == messageId);
		this.messages.removeIf(message -> message.getId() == messageId);
	}

	public int getWidth() {
		return getWidth(this.client.options.getChatWidth().getValue());
	}

	public int getHeight() {
		return getHeight(
			this.isChatFocused() ? this.client.options.getChatHeightFocused().getValue() : this.client.options.getChatHeightUnfocused().getValue()
				/ (this.client.options.getChatLineSpacing().getValue() + 1.0)
		);
	}

	public double getChatScale() {
		return this.client.options.getChatScale().getValue();
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

	public static double getDefaultUnfocusedHeight() {
		int i = 180;
		int j = 20;
		return 70.0 / (double)(getHeight(1.0) - 20);
	}

	public int getVisibleLineCount() {
		return this.getHeight() / 9;
	}

	private long getChatDelayMillis() {
		return (long)(this.client.options.getChatDelay().getValue() * 1000.0);
	}

	private void processMessageQueue() {
		if (!this.messageQueue.isEmpty()) {
			long l = System.currentTimeMillis();
			if (l - this.lastMessageAddedTime >= this.getChatDelayMillis()) {
				this.addMessage((Text)this.messageQueue.remove());
				this.lastMessageAddedTime = l;
			}
		}
	}

	public void queueMessage(Text message) {
		if (this.client.options.getChatDelay().getValue() <= 0.0) {
			this.addMessage(message);
		} else {
			long l = System.currentTimeMillis();
			if (l - this.lastMessageAddedTime >= this.getChatDelayMillis()) {
				this.addMessage(message);
				this.lastMessageAddedTime = l;
			} else {
				this.messageQueue.add(message);
			}
		}
	}
}
