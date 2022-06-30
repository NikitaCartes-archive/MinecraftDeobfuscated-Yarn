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
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.ChatVisibility;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
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
	private static final int MISSING_MESSAGE_INDEX = -1;
	private static final int field_39772 = 4;
	private static final int field_39773 = 4;
	private final MinecraftClient client;
	private final List<String> messageHistory = Lists.<String>newArrayList();
	private final List<ChatHudLine> messages = Lists.<ChatHudLine>newArrayList();
	private final List<ChatHudLine.Visible> visibleMessages = Lists.<ChatHudLine.Visible>newArrayList();
	private final Deque<ChatHud.MessageWithIndicator> messageQueue = Queues.<ChatHud.MessageWithIndicator>newArrayDeque();
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
				double d = this.client.options.getChatOpacity().getValue() * 0.9F + 0.1F;
				double e = this.client.options.getTextBackgroundOpacity().getValue();
				double g = this.client.options.getChatLineSpacing().getValue();
				double h = 9.0 * (g + 1.0);
				double l = -8.0 * (g + 1.0) + 4.0 * g;
				int m = 0;

				for (int n = 0; n + this.scrolledLines < this.visibleMessages.size() && n < i; n++) {
					ChatHudLine.Visible visible = (ChatHudLine.Visible)this.visibleMessages.get(n + this.scrolledLines);
					if (visible != null) {
						int o = tickDelta - visible.addedTime();
						if (o < 200 || bl) {
							double p = bl ? 1.0 : getMessageOpacityMultiplier(o);
							int q = (int)(255.0 * p * d);
							int r = (int)(255.0 * p * e);
							m++;
							if (q > 3) {
								int s = 0;
								double t = (double)(-n) * h;
								int u = (int)(t + l);
								matrices.push();
								matrices.translate(0.0, 0.0, 50.0);
								fill(matrices, -4, (int)(t - h), 0 + k + 4, (int)t, r << 24);
								MessageIndicator messageIndicator = visible.indicator();
								if (messageIndicator != null) {
									int v = messageIndicator.indicatorColor() | q << 24;
									fill(matrices, -4, (int)(t - h), -2, (int)t, v);
									if (bl && visible.endOfEntry() && messageIndicator.icon() != null) {
										int w = this.getIndicatorX(visible);
										int x = u + 9;
										this.drawIndicatorIcon(matrices, w, x, messageIndicator.icon());
									}
								}

								RenderSystem.enableBlend();
								matrices.translate(0.0, 0.0, 50.0);
								this.client.textRenderer.drawWithShadow(matrices, visible.content(), 0.0F, (float)u, 16777215 + (q << 24));
								RenderSystem.disableBlend();
								matrices.pop();
							}
						}
					}
				}

				if (!this.messageQueue.isEmpty()) {
					int nx = (int)(128.0 * d);
					int y = (int)(255.0 * e);
					matrices.push();
					matrices.translate(0.0, 0.0, 50.0);
					fill(matrices, -2, 0, k + 4, 9, y << 24);
					RenderSystem.enableBlend();
					matrices.translate(0.0, 0.0, 50.0);
					this.client.textRenderer.drawWithShadow(matrices, Text.translatable("chat.queue", this.messageQueue.size()), 0.0F, 1.0F, 16777215 + (nx << 24));
					matrices.pop();
					RenderSystem.disableBlend();
				}

				if (bl) {
					int nx = 9;
					int y = j * nx;
					int o = m * nx;
					int z = this.scrolledLines * o / j;
					int aa = o * o / y;
					if (y != o) {
						int q = z > 0 ? 170 : 96;
						int r = this.hasUnreadNewMessages ? 13382451 : 3355562;
						matrices.translate(-4.0, 0.0, 0.0);
						fill(matrices, 0, -z, 2, -z - aa, r + (q << 24));
						fill(matrices, 2, -z, 1, -z - aa, 13421772 + (q << 24));
					}
				}

				matrices.pop();
			}
		}
	}

	private void drawIndicatorIcon(MatrixStack matrices, int x, int y, MessageIndicator.Icon icon) {
		int i = y - icon.height - 1;
		icon.draw(matrices, x, i);
	}

	private int getIndicatorX(ChatHudLine.Visible line) {
		return this.client.textRenderer.getWidth(line.content()) + 4;
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
		this.addMessage(message, null);
	}

	public void addMessage(Text message, @Nullable MessageIndicator indicator) {
		this.addMessage(message, this.client.inGameHud.getTicks(), indicator, false);
		String string = message.getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n");
		String string2 = Util.map(indicator, MessageIndicator::loggedName);
		if (string2 != null) {
			LOGGER.info("[{}] [CHAT] {}", string2, string);
		} else {
			LOGGER.info("[CHAT] {}", string);
		}
	}

	private void addMessage(Text message, int messageId, @Nullable MessageIndicator indicator, boolean refresh) {
		int i = MathHelper.floor((double)this.getWidth() / this.getChatScale());
		if (indicator != null && indicator.icon() != null) {
			i -= indicator.icon().width + 4 + 2;
		}

		List<OrderedText> list = ChatMessages.breakRenderedChatMessageLines(message, i, this.client.textRenderer);
		boolean bl = this.isChatFocused();

		for (int j = 0; j < list.size(); j++) {
			OrderedText orderedText = (OrderedText)list.get(j);
			if (bl && this.scrolledLines > 0) {
				this.hasUnreadNewMessages = true;
				this.scroll(1);
			}

			boolean bl2 = j == list.size() - 1;
			this.visibleMessages.add(0, new ChatHudLine.Visible(messageId, orderedText, indicator, bl2));
		}

		while (this.visibleMessages.size() > 100) {
			this.visibleMessages.remove(this.visibleMessages.size() - 1);
		}

		if (!refresh) {
			this.messages.add(0, new ChatHudLine(messageId, message, indicator));

			while (this.messages.size() > 100) {
				this.messages.remove(this.messages.size() - 1);
			}
		}
	}

	public void reset() {
		this.visibleMessages.clear();
		this.resetScroll();

		for (int i = this.messages.size() - 1; i >= 0; i--) {
			ChatHudLine chatHudLine = (ChatHudLine)this.messages.get(i);
			this.addMessage(chatHudLine.content(), chatHudLine.creationTick(), chatHudLine.indicator(), true);
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

	public void scroll(int scroll) {
		this.scrolledLines += scroll;
		int i = this.visibleMessages.size();
		if (this.scrolledLines > i - this.getVisibleLineCount()) {
			this.scrolledLines = i - this.getVisibleLineCount();
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
				ChatHud.MessageWithIndicator messageWithIndicator = (ChatHud.MessageWithIndicator)this.messageQueue.remove();
				this.addMessage(messageWithIndicator.message(), messageWithIndicator.indicator());
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
	public Style getTextStyleAt(double x, double y) {
		double d = this.toChatLineX(x);
		if (!(d < 0.0) && !(d > (double)MathHelper.floor((double)this.getWidth() / this.getChatScale()))) {
			double e = this.toChatLineY(y);
			int i = this.getMessageIndex(e);
			if (i >= 0 && i < this.visibleMessages.size()) {
				ChatHudLine.Visible visible = (ChatHudLine.Visible)this.visibleMessages.get(i);
				return this.client.textRenderer.getTextHandler().getStyleAt(visible.content(), MathHelper.floor(d));
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	@Nullable
	public MessageIndicator getIndicatorAt(double mouseX, double mouseY) {
		double d = this.toChatLineX(mouseX);
		double e = this.toChatLineY(mouseY);
		int i = this.getMessageIndex(e);
		if (i >= 0 && i < this.visibleMessages.size()) {
			ChatHudLine.Visible visible = (ChatHudLine.Visible)this.visibleMessages.get(i);
			MessageIndicator messageIndicator = visible.indicator();
			if (messageIndicator != null && this.isXInsideIndicatorIcon(d, visible, messageIndicator)) {
				return messageIndicator;
			}
		}

		return null;
	}

	private boolean isXInsideIndicatorIcon(double x, ChatHudLine.Visible line, MessageIndicator indicator) {
		if (x < 0.0) {
			return true;
		} else {
			MessageIndicator.Icon icon = indicator.icon();
			if (icon == null) {
				return false;
			} else {
				int i = this.getIndicatorX(line);
				int j = i + icon.width;
				return x >= (double)i && x <= (double)j;
			}
		}
	}

	private double toChatLineX(double x) {
		return (x - 4.0) / this.getChatScale();
	}

	private double toChatLineY(double y) {
		double d = (double)this.client.getWindow().getScaledHeight() - y - 40.0;
		return d / (this.getChatScale() * (this.client.options.getChatLineSpacing().getValue() + 1.0));
	}

	private int getMessageIndex(double y) {
		if (this.isChatFocused() && !this.client.options.hudHidden && !this.isChatHidden()) {
			int i = Math.min(this.getVisibleLineCount(), this.visibleMessages.size());
			if (y >= 0.0 && y < (double)(9 * i + i)) {
				int j = MathHelper.floor(y / 9.0 + (double)this.scrolledLines);
				if (j >= 0 && j < this.visibleMessages.size()) {
					return j;
				}
			}

			return -1;
		} else {
			return -1;
		}
	}

	@Nullable
	public ChatScreen getChatScreen() {
		Screen var2 = this.client.currentScreen;
		return var2 instanceof ChatScreen ? (ChatScreen)var2 : null;
	}

	private boolean isChatFocused() {
		return this.getChatScreen() != null;
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
				ChatHud.MessageWithIndicator messageWithIndicator = (ChatHud.MessageWithIndicator)this.messageQueue.remove();
				this.addMessage(messageWithIndicator.message(), messageWithIndicator.indicator());
				this.lastMessageAddedTime = l;
			}
		}
	}

	public void queueMessage(Text message) {
		this.queueMessage(message, null);
	}

	public void queueMessage(Text message, @Nullable MessageIndicator indicator) {
		if (this.client.options.getChatDelay().getValue() <= 0.0) {
			this.addMessage(message, indicator);
		} else {
			long l = System.currentTimeMillis();
			if (l - this.lastMessageAddedTime >= this.getChatDelayMillis()) {
				this.addMessage(message);
				this.lastMessageAddedTime = l;
			} else {
				this.messageQueue.add(new ChatHud.MessageWithIndicator(message, indicator));
			}
		}
	}

	@Environment(EnvType.CLIENT)
	static record MessageWithIndicator(Text message, @Nullable MessageIndicator indicator) {
	}
}
