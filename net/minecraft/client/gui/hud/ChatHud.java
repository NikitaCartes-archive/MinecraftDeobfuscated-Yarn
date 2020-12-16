/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.hud;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Deque;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.option.ChatVisibility;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ChatHud
extends DrawableHelper {
    private static final Logger LOGGER = LogManager.getLogger();
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
        int q;
        int p;
        int n;
        int m;
        if (this.isChatHidden()) {
            return;
        }
        this.processMessageQueue();
        int i = this.getVisibleLineCount();
        int j = this.visibleMessages.size();
        if (j <= 0) {
            return;
        }
        boolean bl = false;
        if (this.isChatFocused()) {
            bl = true;
        }
        double d = this.getChatScale();
        int k = MathHelper.ceil((double)this.getWidth() / d);
        RenderSystem.pushMatrix();
        RenderSystem.translatef(2.0f, 8.0f, 0.0f);
        RenderSystem.scaled(d, d, 1.0);
        double e = this.client.options.chatOpacity * (double)0.9f + (double)0.1f;
        double f = this.client.options.textBackgroundOpacity;
        double g = 9.0 * (this.client.options.chatLineSpacing + 1.0);
        double h = -8.0 * (this.client.options.chatLineSpacing + 1.0) + 4.0 * this.client.options.chatLineSpacing;
        int l = 0;
        for (m = 0; m + this.scrolledLines < this.visibleMessages.size() && m < i; ++m) {
            ChatHudLine<OrderedText> chatHudLine = this.visibleMessages.get(m + this.scrolledLines);
            if (chatHudLine == null || (n = tickDelta - chatHudLine.getCreationTick()) >= 200 && !bl) continue;
            double o = bl ? 1.0 : ChatHud.getMessageOpacityMultiplier(n);
            p = (int)(255.0 * o * e);
            q = (int)(255.0 * o * f);
            ++l;
            if (p <= 3) continue;
            boolean r = false;
            double s = (double)(-m) * g;
            matrices.push();
            matrices.translate(0.0, 0.0, 50.0);
            ChatHud.fill(matrices, -2, (int)(s - g), 0 + k + 4, (int)s, q << 24);
            RenderSystem.enableBlend();
            matrices.translate(0.0, 0.0, 50.0);
            this.client.textRenderer.drawWithShadow(matrices, chatHudLine.getText(), 0.0f, (float)((int)(s + h)), 0xFFFFFF + (p << 24));
            RenderSystem.disableAlphaTest();
            RenderSystem.disableBlend();
            matrices.pop();
        }
        if (!this.messageQueue.isEmpty()) {
            m = (int)(128.0 * e);
            int t = (int)(255.0 * f);
            matrices.push();
            matrices.translate(0.0, 0.0, 50.0);
            ChatHud.fill(matrices, -2, 0, k + 4, 9, t << 24);
            RenderSystem.enableBlend();
            matrices.translate(0.0, 0.0, 50.0);
            this.client.textRenderer.drawWithShadow(matrices, new TranslatableText("chat.queue", this.messageQueue.size()), 0.0f, 1.0f, 0xFFFFFF + (m << 24));
            matrices.pop();
            RenderSystem.disableAlphaTest();
            RenderSystem.disableBlend();
        }
        if (bl) {
            m = this.client.textRenderer.fontHeight;
            RenderSystem.translatef(-3.0f, 0.0f, 0.0f);
            int t = j * m + j;
            n = l * m + l;
            int u = this.scrolledLines * n / j;
            int v = n * n / t;
            if (t != n) {
                p = u > 0 ? 170 : 96;
                q = this.hasUnreadNewMessages ? 0xCC3333 : 0x3333AA;
                ChatHud.fill(matrices, 0, -u, 2, -u - v, q + (p << 24));
                ChatHud.fill(matrices, 2, -u, 1, -u - v, 0xCCCCCC + (p << 24));
            }
        }
        RenderSystem.popMatrix();
    }

    private boolean isChatHidden() {
        return this.client.options.chatVisibility == ChatVisibility.HIDDEN;
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
                this.scroll(1.0);
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
    public Style getText(double x, double y) {
        int j;
        if (!this.isChatFocused() || this.client.options.hudHidden || this.isChatHidden()) {
            return null;
        }
        double d = x - 2.0;
        double e = (double)this.client.getWindow().getScaledHeight() - y - 40.0;
        d = MathHelper.floor(d / this.getChatScale());
        e = MathHelper.floor(e / (this.getChatScale() * (this.client.options.chatLineSpacing + 1.0)));
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

    private boolean isChatFocused() {
        return this.client.currentScreen instanceof ChatScreen;
    }

    private void removeMessage(int messageId) {
        this.visibleMessages.removeIf(message -> message.getId() == messageId);
        this.messages.removeIf(message -> message.getId() == messageId);
    }

    public int getWidth() {
        return ChatHud.getWidth(this.client.options.chatWidth);
    }

    public int getHeight() {
        return ChatHud.getHeight((this.isChatFocused() ? this.client.options.chatHeightFocused : this.client.options.chatHeightUnfocused) / (this.client.options.chatLineSpacing + 1.0));
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

    private long getChatDelayMillis() {
        return (long)(this.client.options.chatDelay * 1000.0);
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
        if (this.client.options.chatDelay <= 0.0) {
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

