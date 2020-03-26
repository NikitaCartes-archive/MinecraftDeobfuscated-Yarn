/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.hud;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.options.ChatVisibility;
import net.minecraft.client.util.Texts;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ChatHud
extends DrawableHelper {
    private static final Logger LOGGER = LogManager.getLogger();
    private final MinecraftClient client;
    private final List<String> messageHistory = Lists.newArrayList();
    private final List<ChatHudLine> messages = Lists.newArrayList();
    private final List<ChatHudLine> visibleMessages = Lists.newArrayList();
    private int scrolledLines;
    private boolean hasUnreadNewMessages;

    public ChatHud(MinecraftClient client) {
        this.client = client;
    }

    public void render(int ticks) {
        int p;
        int o;
        int n;
        int m;
        if (!this.method_23677()) {
            return;
        }
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
        int l = 0;
        Matrix4f matrix4f = Matrix4f.translate(0.0f, 0.0f, -100.0f);
        for (m = 0; m + this.scrolledLines < this.visibleMessages.size() && m < i; ++m) {
            ChatHudLine chatHudLine = this.visibleMessages.get(m + this.scrolledLines);
            if (chatHudLine == null || (n = ticks - chatHudLine.getCreationTick()) >= 200 && !bl) continue;
            double g = bl ? 1.0 : ChatHud.getMessageOpacityMultiplier(n);
            o = (int)(255.0 * g * e);
            p = (int)(255.0 * g * f);
            ++l;
            if (o <= 3) continue;
            boolean q = false;
            int r = -m * 9;
            ChatHud.fill(matrix4f, -2, r - 9, 0 + k + 4, r, p << 24);
            String string = chatHudLine.getText().asFormattedString();
            RenderSystem.enableBlend();
            this.client.textRenderer.drawWithShadow(string, 0.0f, r - 8, 0xFFFFFF + (o << 24));
            RenderSystem.disableAlphaTest();
            RenderSystem.disableBlend();
        }
        if (bl) {
            m = this.client.textRenderer.fontHeight;
            RenderSystem.translatef(-3.0f, 0.0f, 0.0f);
            int s = j * m + j;
            n = l * m + l;
            int t = this.scrolledLines * n / j;
            int u = n * n / s;
            if (s != n) {
                o = t > 0 ? 170 : 96;
                p = this.hasUnreadNewMessages ? 0xCC3333 : 0x3333AA;
                ChatHud.fill(0, -t, 2, -t - u, p + (o << 24));
                ChatHud.fill(2, -t, 1, -t - u, 0xCCCCCC + (o << 24));
            }
        }
        RenderSystem.popMatrix();
    }

    private boolean method_23677() {
        return this.client.options.chatVisibility != ChatVisibility.HIDDEN;
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
        this.visibleMessages.clear();
        this.messages.clear();
        if (clearHistory) {
            this.messageHistory.clear();
        }
    }

    public void addMessage(Text message) {
        this.addMessage(message, 0);
    }

    public void addMessage(Text message, int messageId) {
        this.addMessage(message, messageId, this.client.inGameHud.getTicks(), false);
        LOGGER.info("[CHAT] {}", (Object)message.getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
    }

    private void addMessage(Text message, int messageId, int timestamp, boolean bl) {
        if (messageId != 0) {
            this.removeMessage(messageId);
        }
        int i = MathHelper.floor((double)this.getWidth() / this.getChatScale());
        List<Text> list = Texts.wrapLines(message, i, this.client.textRenderer, false, false);
        boolean bl2 = this.isChatFocused();
        for (Text text : list) {
            if (bl2 && this.scrolledLines > 0) {
                this.hasUnreadNewMessages = true;
                this.scroll(1.0);
            }
            this.visibleMessages.add(0, new ChatHudLine(timestamp, text, messageId));
        }
        while (this.visibleMessages.size() > 100) {
            this.visibleMessages.remove(this.visibleMessages.size() - 1);
        }
        if (!bl) {
            this.messages.add(0, new ChatHudLine(timestamp, message, messageId));
            while (this.messages.size() > 100) {
                this.messages.remove(this.messages.size() - 1);
            }
        }
    }

    public void reset() {
        this.visibleMessages.clear();
        this.resetScroll();
        for (int i = this.messages.size() - 1; i >= 0; --i) {
            ChatHudLine chatHudLine = this.messages.get(i);
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

    @Nullable
    public Text getText(double x, double y) {
        if (!this.isChatFocused() || this.client.options.hudHidden || !this.method_23677()) {
            return null;
        }
        double d = this.getChatScale();
        double e = x - 2.0;
        double f = (double)this.client.getWindow().getScaledHeight() - y - 40.0;
        e = MathHelper.floor(e / d);
        f = MathHelper.floor(f / d);
        if (e < 0.0 || f < 0.0) {
            return null;
        }
        int i = Math.min(this.getVisibleLineCount(), this.visibleMessages.size());
        if (e <= (double)MathHelper.floor((double)this.getWidth() / this.getChatScale()) && f < (double)(this.client.textRenderer.fontHeight * i + i)) {
            int j = (int)(f / (double)this.client.textRenderer.fontHeight + (double)this.scrolledLines);
            if (j >= 0 && j < this.visibleMessages.size()) {
                ChatHudLine chatHudLine = this.visibleMessages.get(j);
                int k = 0;
                for (Text text : chatHudLine.getText()) {
                    if (!(text instanceof LiteralText) || !((double)(k += this.client.textRenderer.getStringWidth(Texts.getRenderChatMessage(((LiteralText)text).getRawString(), false))) > e)) continue;
                    return text;
                }
            }
            return null;
        }
        return null;
    }

    public boolean isChatFocused() {
        return this.client.currentScreen instanceof ChatScreen;
    }

    public void removeMessage(int messageId) {
        ChatHudLine chatHudLine;
        Iterator<ChatHudLine> iterator = this.visibleMessages.iterator();
        while (iterator.hasNext()) {
            chatHudLine = iterator.next();
            if (chatHudLine.getId() != messageId) continue;
            iterator.remove();
        }
        iterator = this.messages.iterator();
        while (iterator.hasNext()) {
            chatHudLine = iterator.next();
            if (chatHudLine.getId() != messageId) continue;
            iterator.remove();
            break;
        }
    }

    public int getWidth() {
        return ChatHud.getWidth(this.client.options.chatWidth);
    }

    public int getHeight() {
        return ChatHud.getHeight(this.isChatFocused() ? this.client.options.chatHeightFocused : this.client.options.chatHeightUnfocused);
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
}

