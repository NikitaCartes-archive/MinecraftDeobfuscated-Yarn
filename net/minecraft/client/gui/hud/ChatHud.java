/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.hud;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.util.Deque;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.ChatVisibility;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
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
    private final MinecraftClient client;
    private final List<String> messageHistory = Lists.newArrayList();
    private final List<ChatHudLine<Text>> messages = Lists.newArrayList();
    private final List<ChatHudLine<OrderedText>> visibleMessages = Lists.newArrayList();
    private final Deque<Text> messageQueue = Queues.newArrayDeque();
    private int scrolledLines;
    private boolean hasUnreadNewMessages;
    private long lastMessageAddedTime;

    public ChatHud(MinecraftClient client) {
        this.client = client;
    }

    public void render(MatrixStack matrices, int tickDelta) {
        int r;
        int q;
        int o;
        int n;
        if (this.isChatHidden()) {
            return;
        }
        this.processMessageQueue();
        int i = this.getVisibleLineCount();
        int j = this.visibleMessages.size();
        if (j <= 0) {
            return;
        }
        boolean bl = this.isChatFocused();
        float f = (float)this.getChatScale();
        int k = MathHelper.ceil((float)this.getWidth() / f);
        matrices.push();
        matrices.translate(4.0, 8.0, 0.0);
        matrices.scale(f, f, 1.0f);
        double d = this.client.options.getChtOpacity().getValue() * (double)0.9f + (double)0.1f;
        double e = this.client.options.getTextBackgroundOpacity().getValue();
        double g = this.client.options.getChatLineSpacing().getValue();
        double h = 9.0 * (g + 1.0);
        double l = -8.0 * (g + 1.0) + 4.0 * g;
        int m = 0;
        for (n = 0; n + this.scrolledLines < this.visibleMessages.size() && n < i; ++n) {
            ChatHudLine<OrderedText> chatHudLine = this.visibleMessages.get(n + this.scrolledLines);
            if (chatHudLine == null || (o = tickDelta - chatHudLine.getCreationTick()) >= 200 && !bl) continue;
            double p = bl ? 1.0 : ChatHud.getMessageOpacityMultiplier(o);
            q = (int)(255.0 * p * d);
            r = (int)(255.0 * p * e);
            ++m;
            if (q <= 3) continue;
            boolean s = false;
            double t = (double)(-n) * h;
            matrices.push();
            matrices.translate(0.0, 0.0, 50.0);
            ChatHud.fill(matrices, -4, (int)(t - h), 0 + k + 4, (int)t, r << 24);
            RenderSystem.enableBlend();
            matrices.translate(0.0, 0.0, 50.0);
            this.client.textRenderer.drawWithShadow(matrices, chatHudLine.getText(), 0.0f, (float)((int)(t + l)), 0xFFFFFF + (q << 24));
            RenderSystem.disableBlend();
            matrices.pop();
        }
        if (!this.messageQueue.isEmpty()) {
            n = (int)(128.0 * d);
            int u = (int)(255.0 * e);
            matrices.push();
            matrices.translate(0.0, 0.0, 50.0);
            ChatHud.fill(matrices, -2, 0, k + 4, 9, u << 24);
            RenderSystem.enableBlend();
            matrices.translate(0.0, 0.0, 50.0);
            this.client.textRenderer.drawWithShadow(matrices, Text.translatable("chat.queue", this.messageQueue.size()), 0.0f, 1.0f, 0xFFFFFF + (n << 24));
            matrices.pop();
            RenderSystem.disableBlend();
        }
        if (bl) {
            n = this.client.textRenderer.fontHeight;
            int u = j * n;
            o = m * n;
            int v = this.scrolledLines * o / j;
            int w = o * o / u;
            if (u != o) {
                q = v > 0 ? 170 : 96;
                r = this.hasUnreadNewMessages ? 0xCC3333 : 0x3333AA;
                matrices.translate(-4.0, 0.0, 0.0);
                ChatHud.fill(matrices, 0, -v, 2, -v - w, r + (q << 24));
                ChatHud.fill(matrices, 2, -v, 1, -v - w, 0xCCCCCC + (q << 24));
            }
        }
        matrices.pop();
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
        LOGGER.info("[CHAT] {}", (Object)message.getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
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
            this.visibleMessages.add(0, new ChatHudLine<OrderedText>(timestamp, orderedText, messageId));
        }
        while (this.visibleMessages.size() > 100) {
            this.visibleMessages.remove(this.visibleMessages.size() - 1);
        }
        if (!refresh) {
            this.messages.add(0, new ChatHudLine<Text>(timestamp, message, messageId));
            while (this.messages.size() > 100) {
                this.messages.remove(this.messages.size() - 1);
            }
        }
    }

    public void reset() {
        this.visibleMessages.clear();
        this.resetScroll();
        for (int i = this.messages.size() - 1; i >= 0; --i) {
            ChatHudLine<Text> chatHudLine = this.messages.get(i);
            this.addMessage(chatHudLine.getText(), chatHudLine.getId(), chatHudLine.getCreationTick(), true);
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
        if (!this.isChatFocused() || this.client.options.hudHidden || this.isChatHidden() || this.messageQueue.isEmpty()) {
            return false;
        }
        double d = mouseX - 2.0;
        double e = (double)this.client.getWindow().getScaledHeight() - mouseY - 40.0;
        if (d <= (double)MathHelper.floor((double)this.getWidth() / this.getChatScale()) && e < 0.0 && e > (double)MathHelper.floor(-9.0 * this.getChatScale())) {
            this.addMessage(this.messageQueue.remove());
            this.lastMessageAddedTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    @Nullable
    public Style getTextStyleAt(double x, double y) {
        int j;
        if (!this.isChatFocused() || this.client.options.hudHidden || this.isChatHidden()) {
            return null;
        }
        double d = x - 2.0;
        double e = (double)this.client.getWindow().getScaledHeight() - y - 40.0;
        d = MathHelper.floor(d / this.getChatScale());
        e = MathHelper.floor(e / (this.getChatScale() * (this.client.options.getChatLineSpacing().getValue() + 1.0)));
        if (d < 0.0 || e < 0.0) {
            return null;
        }
        int i = Math.min(this.getVisibleLineCount(), this.visibleMessages.size());
        if (d <= (double)MathHelper.floor((double)this.getWidth() / this.getChatScale()) && e < (double)(this.client.textRenderer.fontHeight * i + i) && (j = (int)(e / (double)this.client.textRenderer.fontHeight + (double)this.scrolledLines)) >= 0 && j < this.visibleMessages.size()) {
            ChatHudLine<OrderedText> chatHudLine = this.visibleMessages.get(j);
            return this.client.textRenderer.getTextHandler().getStyleAt(chatHudLine.getText(), (int)d);
        }
        return null;
    }

    @Nullable
    public ChatScreen getChatScreen() {
        Screen screen = this.client.currentScreen;
        if (screen instanceof ChatScreen) {
            ChatScreen chatScreen = (ChatScreen)screen;
            return chatScreen;
        }
        return null;
    }

    private boolean isChatFocused() {
        return this.getChatScreen() != null;
    }

    private void removeMessage(int messageId) {
        this.visibleMessages.removeIf(message -> message.getId() == messageId);
        this.messages.removeIf(message -> message.getId() == messageId);
    }

    public int getWidth() {
        return ChatHud.getWidth(this.client.options.getChatWidth().getValue());
    }

    public int getHeight() {
        return ChatHud.getHeight((this.isChatFocused() ? this.client.options.getChatHeightFocused().getValue() : this.client.options.getChatHeightUnfocused().getValue()) / (this.client.options.getChatLineSpacing().getValue() + 1.0));
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
        return this.getHeight() / 9;
    }

    private long getChatDelayMillis() {
        return (long)(this.client.options.getChatDelay().getValue() * 1000.0);
    }

    private void processMessageQueue() {
        if (this.messageQueue.isEmpty()) {
            return;
        }
        long l = System.currentTimeMillis();
        if (l - this.lastMessageAddedTime >= this.getChatDelayMillis()) {
            this.addMessage(this.messageQueue.remove());
            this.lastMessageAddedTime = l;
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

