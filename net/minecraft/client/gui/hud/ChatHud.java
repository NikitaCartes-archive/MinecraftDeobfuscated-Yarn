/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.hud;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.client.option.ChatVisibility;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

/**
 * Responsible for rendering various game messages such as chat messages or
 * join/leave messages.
 * 
 * @see net.minecraft.client.gui.screen.ChatScreen
 */
@Environment(value=EnvType.CLIENT)
public class ChatHud
extends DrawableHelper {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int MAX_MESSAGES = 100;
    private static final int MISSING_MESSAGE_INDEX = -1;
    private static final int field_39772 = 4;
    private static final int field_39773 = 4;
    private static final int field_40389 = 40;
    private static final int field_40390 = 60;
    private static final Text DELETED_MARKER_TEXT = Text.translatable("chat.deleted_marker").formatted(Formatting.GRAY, Formatting.ITALIC);
    private final MinecraftClient client;
    private final List<String> messageHistory = Lists.newArrayList();
    private final List<ChatHudLine> messages = Lists.newArrayList();
    private final List<ChatHudLine.Visible> visibleMessages = Lists.newArrayList();
    private int scrolledLines;
    private boolean hasUnreadNewMessages;
    private final List<RemovalQueuedMessage> removalQueue = new ArrayList<RemovalQueuedMessage>();

    public ChatHud(MinecraftClient client) {
        this.client = client;
    }

    public void tickRemovalQueueIfExists() {
        if (!this.removalQueue.isEmpty()) {
            this.tickRemovalQueue();
        }
    }

    public void render(MatrixStack matrices, int currentTick, int mouseX, int mouseY) {
        int x;
        int w;
        int v;
        int u;
        int t;
        if (this.isChatHidden()) {
            return;
        }
        int i = this.getVisibleLineCount();
        int j = this.visibleMessages.size();
        if (j <= 0) {
            return;
        }
        boolean bl = this.isChatFocused();
        float f = (float)this.getChatScale();
        int k = MathHelper.ceil((float)this.getWidth() / f);
        int l = this.client.getWindow().getScaledHeight();
        matrices.push();
        matrices.scale(f, f, 1.0f);
        matrices.translate(4.0f, 0.0f, 0.0f);
        int m = MathHelper.floor((float)(l - 40) / f);
        int n = this.getMessageIndex(this.toChatLineX(mouseX), this.toChatLineY(mouseY));
        double d = this.client.options.getChatOpacity().getValue() * (double)0.9f + (double)0.1f;
        double e = this.client.options.getTextBackgroundOpacity().getValue();
        double g = this.client.options.getChatLineSpacing().getValue();
        int o = this.getLineHeight();
        int p = (int)Math.round(-8.0 * (g + 1.0) + 4.0 * g);
        int q = 0;
        for (int r = 0; r + this.scrolledLines < this.visibleMessages.size() && r < i; ++r) {
            int s = r + this.scrolledLines;
            ChatHudLine.Visible visible = this.visibleMessages.get(s);
            if (visible == null || (t = currentTick - visible.addedTime()) >= 200 && !bl) continue;
            double h = bl ? 1.0 : ChatHud.getMessageOpacityMultiplier(t);
            u = (int)(255.0 * h * d);
            v = (int)(255.0 * h * e);
            ++q;
            if (u <= 3) continue;
            w = 0;
            x = m - r * o;
            int y = x + p;
            matrices.push();
            matrices.translate(0.0f, 0.0f, 50.0f);
            ChatHud.fill(matrices, -4, x - o, 0 + k + 4 + 4, x, v << 24);
            MessageIndicator messageIndicator = visible.indicator();
            if (messageIndicator != null) {
                int z = messageIndicator.indicatorColor() | u << 24;
                ChatHud.fill(matrices, -4, x - o, -2, x, z);
                if (s == n && messageIndicator.icon() != null) {
                    int aa = this.getIndicatorX(visible);
                    int ab = y + this.client.textRenderer.fontHeight;
                    this.drawIndicatorIcon(matrices, aa, ab, messageIndicator.icon());
                }
            }
            RenderSystem.enableBlend();
            matrices.translate(0.0f, 0.0f, 50.0f);
            this.client.textRenderer.drawWithShadow(matrices, visible.content(), 0.0f, (float)y, 0xFFFFFF + (u << 24));
            RenderSystem.disableBlend();
            matrices.pop();
        }
        long ac = this.client.getMessageHandler().getUnprocessedMessageCount();
        if (ac > 0L) {
            int ad = (int)(128.0 * d);
            t = (int)(255.0 * e);
            matrices.push();
            matrices.translate(0.0f, m, 50.0f);
            ChatHud.fill(matrices, -2, 0, k + 4, 9, t << 24);
            RenderSystem.enableBlend();
            matrices.translate(0.0f, 0.0f, 50.0f);
            this.client.textRenderer.drawWithShadow(matrices, Text.translatable("chat.queue", ac), 0.0f, 1.0f, 0xFFFFFF + (ad << 24));
            matrices.pop();
            RenderSystem.disableBlend();
        }
        if (bl) {
            int ad = this.getLineHeight();
            t = j * ad;
            int ae = q * ad;
            int af = this.scrolledLines * ae / j - m;
            u = ae * ae / t;
            if (t != ae) {
                v = af > 0 ? 170 : 96;
                w = this.hasUnreadNewMessages ? 0xCC3333 : 0x3333AA;
                x = k + 4;
                ChatHud.fill(matrices, x, -af, x + 2, -af - u, w + (v << 24));
                ChatHud.fill(matrices, x + 2, -af, x + 1, -af - u, 0xCCCCCC + (v << 24));
            }
        }
        matrices.pop();
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
        d *= d;
        return d;
    }

    public void clear(boolean clearHistory) {
        this.client.getMessageHandler().processAll();
        this.removalQueue.clear();
        this.visibleMessages.clear();
        this.messages.clear();
        if (clearHistory) {
            this.messageHistory.clear();
        }
    }

    public void addMessage(Text message) {
        this.addMessage(message, null, this.client.method_47392() ? MessageIndicator.method_47391() : MessageIndicator.system());
    }

    public void addMessage(Text message, @Nullable MessageSignatureData signature, @Nullable MessageIndicator indicator) {
        this.logChatMessage(message, indicator);
        this.addMessage(message, signature, this.client.inGameHud.getTicks(), indicator, false);
    }

    private void logChatMessage(Text message, @Nullable MessageIndicator indicator) {
        String string = message.getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n");
        String string2 = Util.map(indicator, MessageIndicator::loggedName);
        if (string2 != null) {
            LOGGER.info("[{}] [CHAT] {}", (Object)string2, (Object)string);
        } else {
            LOGGER.info("[CHAT] {}", (Object)string);
        }
    }

    private void addMessage(Text message, @Nullable MessageSignatureData signature, int ticks, @Nullable MessageIndicator indicator, boolean refresh) {
        int i = MathHelper.floor((double)this.getWidth() / this.getChatScale());
        if (indicator != null && indicator.icon() != null) {
            i -= indicator.icon().width + 4 + 2;
        }
        List<OrderedText> list = ChatMessages.breakRenderedChatMessageLines(message, i, this.client.textRenderer);
        boolean bl = this.isChatFocused();
        for (int j = 0; j < list.size(); ++j) {
            OrderedText orderedText = list.get(j);
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

    private void tickRemovalQueue() {
        int i = this.client.inGameHud.getTicks();
        this.removalQueue.removeIf(message -> {
            if (i >= message.deletableAfter()) {
                return this.queueForRemoval(message.signature()) == null;
            }
            return false;
        });
    }

    public void removeMessage(MessageSignatureData signature) {
        RemovalQueuedMessage removalQueuedMessage = this.queueForRemoval(signature);
        if (removalQueuedMessage != null) {
            this.removalQueue.add(removalQueuedMessage);
        }
    }

    @Nullable
    private RemovalQueuedMessage queueForRemoval(MessageSignatureData signature) {
        int i = this.client.inGameHud.getTicks();
        ListIterator<ChatHudLine> listIterator = this.messages.listIterator();
        while (listIterator.hasNext()) {
            ChatHudLine chatHudLine = listIterator.next();
            if (!signature.equals(chatHudLine.signature())) continue;
            int j = chatHudLine.creationTick() + 60;
            if (i >= j) {
                listIterator.set(this.createRemovalMarker(chatHudLine));
                this.refresh();
                return null;
            }
            return new RemovalQueuedMessage(signature, j);
        }
        return null;
    }

    private ChatHudLine createRemovalMarker(ChatHudLine original) {
        return new ChatHudLine(original.creationTick(), DELETED_MARKER_TEXT, null, MessageIndicator.system());
    }

    public void reset() {
        this.resetScroll();
        this.refresh();
    }

    private void refresh() {
        this.visibleMessages.clear();
        for (int i = this.messages.size() - 1; i >= 0; --i) {
            ChatHudLine chatHudLine = this.messages.get(i);
            this.addMessage(chatHudLine.content(), chatHudLine.signature(), chatHudLine.creationTick(), chatHudLine.indicator(), true);
        }
    }

    public List<String> getMessageHistory() {
        return this.messageHistory;
    }

    public void addToMessageHistory(String message) {
        if (this.messageHistory.isEmpty() || !this.messageHistory.get(this.messageHistory.size() - 1).equals(message)) {
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
        if (!this.isChatFocused() || this.client.options.hudHidden || this.isChatHidden()) {
            return false;
        }
        MessageHandler messageHandler = this.client.getMessageHandler();
        if (messageHandler.getUnprocessedMessageCount() == 0L) {
            return false;
        }
        double d = mouseX - 2.0;
        double e = (double)this.client.getWindow().getScaledHeight() - mouseY - 40.0;
        if (d <= (double)MathHelper.floor((double)this.getWidth() / this.getChatScale()) && e < 0.0 && e > (double)MathHelper.floor(-9.0 * this.getChatScale())) {
            messageHandler.process();
            return true;
        }
        return false;
    }

    @Nullable
    public Style getTextStyleAt(double x, double y) {
        double e;
        double d = this.toChatLineX(x);
        int i = this.getMessageLineIndex(d, e = this.toChatLineY(y));
        if (i >= 0 && i < this.visibleMessages.size()) {
            ChatHudLine.Visible visible = this.visibleMessages.get(i);
            return this.client.textRenderer.getTextHandler().getStyleAt(visible.content(), MathHelper.floor(d));
        }
        return null;
    }

    @Nullable
    public MessageIndicator getIndicatorAt(double mouseX, double mouseY) {
        ChatHudLine.Visible visible;
        MessageIndicator messageIndicator;
        double e;
        double d = this.toChatLineX(mouseX);
        int i = this.getMessageIndex(d, e = this.toChatLineY(mouseY));
        if (i >= 0 && i < this.visibleMessages.size() && (messageIndicator = (visible = this.visibleMessages.get(i)).indicator()) != null && this.isXInsideIndicatorIcon(d, visible, messageIndicator)) {
            return messageIndicator;
        }
        return null;
    }

    private boolean isXInsideIndicatorIcon(double x, ChatHudLine.Visible line, MessageIndicator indicator) {
        if (x < 0.0) {
            return true;
        }
        MessageIndicator.Icon icon = indicator.icon();
        if (icon != null) {
            int i = this.getIndicatorX(line);
            int j = i + icon.width;
            return x >= (double)i && x <= (double)j;
        }
        return false;
    }

    private double toChatLineX(double x) {
        return x / this.getChatScale() - 4.0;
    }

    private double toChatLineY(double y) {
        double d = (double)this.client.getWindow().getScaledHeight() - y - 40.0;
        return d / (this.getChatScale() * (double)this.getLineHeight());
    }

    private int getMessageIndex(double chatLineX, double chatLineY) {
        int i = this.getMessageLineIndex(chatLineX, chatLineY);
        if (i == -1) {
            return -1;
        }
        while (i >= 0) {
            if (this.visibleMessages.get(i).endOfEntry()) {
                return i;
            }
            --i;
        }
        return i;
    }

    private int getMessageLineIndex(double chatLineX, double chatLineY) {
        int j;
        if (!this.isChatFocused() || this.client.options.hudHidden || this.isChatHidden()) {
            return -1;
        }
        if (chatLineX < -4.0 || chatLineX > (double)MathHelper.floor((double)this.getWidth() / this.getChatScale())) {
            return -1;
        }
        int i = Math.min(this.getVisibleLineCount(), this.visibleMessages.size());
        if (chatLineY >= 0.0 && chatLineY < (double)i && (j = MathHelper.floor(chatLineY + (double)this.scrolledLines)) >= 0 && j < this.visibleMessages.size()) {
            return j;
        }
        return -1;
    }

    private boolean isChatFocused() {
        return this.client.currentScreen instanceof ChatScreen;
    }

    public int getWidth() {
        return ChatHud.getWidth(this.client.options.getChatWidth().getValue());
    }

    public int getHeight() {
        return ChatHud.getHeight(this.isChatFocused() ? this.client.options.getChatHeightFocused().getValue() : this.client.options.getChatHeightUnfocused().getValue());
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
        return 70.0 / (double)(ChatHud.getHeight(1.0) - 20);
    }

    public int getVisibleLineCount() {
        return this.getHeight() / this.getLineHeight();
    }

    private int getLineHeight() {
        return (int)((double)this.client.textRenderer.fontHeight * (this.client.options.getChatLineSpacing().getValue() + 1.0));
    }

    @Environment(value=EnvType.CLIENT)
    record RemovalQueuedMessage(MessageSignatureData signature, int deletableAfter) {
    }
}

