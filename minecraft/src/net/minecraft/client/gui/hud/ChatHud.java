package net.minecraft.client.gui.hud;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.client.option.ChatVisibility;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.message.MessageSignatureData;
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
	private int scrolledLines;
	private boolean hasUnreadNewMessages;

	public ChatHud(MinecraftClient client) {
		this.client = client;
	}

	public void render(MatrixStack matrices, int tickDelta) {
		if (!this.isChatHidden()) {
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
				int l = this.getLineHeight();
				double h = -8.0 * (g + 1.0) + 4.0 * g;
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
								int t = -n * l;
								int u = (int)((double)t + h);
								matrices.push();
								matrices.translate(0.0, 0.0, 50.0);
								fill(matrices, -4, t - l, 0 + k + 4 + 4, t, r << 24);
								MessageIndicator messageIndicator = visible.indicator();
								if (messageIndicator != null) {
									int v = messageIndicator.indicatorColor() | q << 24;
									fill(matrices, -4, t - l, -2, t, v);
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

				long y = this.client.getMessageHandler().getUnprocessedMessageCount();
				if (y > 0L) {
					int o = (int)(128.0 * d);
					int z = (int)(255.0 * e);
					matrices.push();
					matrices.translate(0.0, 0.0, 50.0);
					fill(matrices, -2, 0, k + 4, 9, z << 24);
					RenderSystem.enableBlend();
					matrices.translate(0.0, 0.0, 50.0);
					this.client.textRenderer.drawWithShadow(matrices, Text.translatable("chat.queue", y), 0.0F, 1.0F, 16777215 + (o << 24));
					matrices.pop();
					RenderSystem.disableBlend();
				}

				if (bl) {
					int o = this.getLineHeight();
					int z = j * o;
					int aa = m * o;
					int q = this.scrolledLines * aa / j;
					int r = aa * aa / z;
					if (z != aa) {
						int s = q > 0 ? 170 : 96;
						int t = this.hasUnreadNewMessages ? 13382451 : 3355562;
						int u = k + 4;
						fill(matrices, u, -q, u + 2, -q - r, t + (s << 24));
						fill(matrices, u + 2, -q, u + 1, -q - r, 13421772 + (s << 24));
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
		this.client.getMessageHandler().processAll();
		this.visibleMessages.clear();
		this.messages.clear();
		if (clearHistory) {
			this.messageHistory.clear();
		}
	}

	public void addMessage(Text message) {
		this.addMessage(message, null, MessageIndicator.system());
	}

	public void addMessage(Text message, @Nullable MessageSignatureData signature, @Nullable MessageIndicator indicator) {
		this.addMessage(message, signature, this.client.inGameHud.getTicks(), indicator, false);
	}

	private void method_45027(Text text, @Nullable MessageIndicator messageIndicator) {
		String string = text.getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n");
		String string2 = Util.map(messageIndicator, MessageIndicator::loggedName);
		if (string2 != null) {
			LOGGER.info("[{}] [CHAT] {}", string2, string);
		} else {
			LOGGER.info("[CHAT] {}", string);
		}
	}

	private void addMessage(Text message, @Nullable MessageSignatureData signature, int ticks, @Nullable MessageIndicator indicator, boolean refresh) {
		this.method_45027(message, indicator);
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
			this.visibleMessages.add(0, new ChatHudLine.Visible(ticks, orderedText, indicator, bl2));
		}

		while (this.visibleMessages.size() > 100) {
			this.visibleMessages.remove(this.visibleMessages.size() - 1);
		}

		if (!refresh) {
			this.messages.add(0, new ChatHudLine(ticks, message, signature, indicator));

			while (this.messages.size() > 100) {
				this.messages.remove(this.messages.size() - 1);
			}
		}
	}

	public void hideMessage(MessageSignatureData signature) {
		Iterator<ChatHudLine> iterator = this.messages.iterator();

		while (iterator.hasNext()) {
			MessageSignatureData messageSignatureData = ((ChatHudLine)iterator.next()).headerSignature();
			if (messageSignatureData != null && messageSignatureData.equals(signature)) {
				iterator.remove();
				break;
			}
		}

		this.refresh();
	}

	public void reset() {
		this.resetScroll();
		this.refresh();
	}

	private void refresh() {
		this.visibleMessages.clear();

		for (int i = this.messages.size() - 1; i >= 0; i--) {
			ChatHudLine chatHudLine = (ChatHudLine)this.messages.get(i);
			this.addMessage(chatHudLine.content(), chatHudLine.headerSignature(), chatHudLine.creationTick(), chatHudLine.indicator(), true);
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
		if (this.isChatFocused() && !this.client.options.hudHidden && !this.isChatHidden()) {
			MessageHandler messageHandler = this.client.getMessageHandler();
			if (messageHandler.getUnprocessedMessageCount() == 0L) {
				return false;
			} else {
				double d = mouseX - 2.0;
				double e = (double)this.client.getWindow().getScaledHeight() - mouseY - 40.0;
				if (d <= (double)MathHelper.floor((double)this.getWidth() / this.getChatScale()) && e < 0.0 && e > (double)MathHelper.floor(-9.0 * this.getChatScale())) {
					messageHandler.process();
					return true;
				} else {
					return false;
				}
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
		return getHeight(this.isChatFocused() ? this.client.options.getChatHeightFocused().getValue() : this.client.options.getChatHeightUnfocused().getValue());
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
		return this.getHeight() / this.getLineHeight();
	}

	private int getLineHeight() {
		return (int)(9.0 * (this.client.options.getChatLineSpacing().getValue() + 1.0));
	}
}
