/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.hud;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
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
    private boolean field_2067;

    public ChatHud(MinecraftClient minecraftClient) {
        this.client = minecraftClient;
    }

    public void render(int i) {
        int q;
        int p;
        int o;
        int n;
        if (this.client.options.chatVisibility == ChatVisibility.HIDDEN) {
            return;
        }
        int j = this.getVisibleLineCount();
        int k = this.visibleMessages.size();
        if (k <= 0) {
            return;
        }
        boolean bl = false;
        if (this.isChatFocused()) {
            bl = true;
        }
        double d = this.getChatScale();
        int l = MathHelper.ceil((double)this.getWidth() / d);
        GlStateManager.pushMatrix();
        GlStateManager.translatef(2.0f, 8.0f, 0.0f);
        GlStateManager.scaled(d, d, 1.0);
        double e = this.client.options.chatOpacity * (double)0.9f + (double)0.1f;
        double f = this.client.options.textBackgroundOpacity;
        int m = 0;
        for (n = 0; n + this.scrolledLines < this.visibleMessages.size() && n < j; ++n) {
            ChatHudLine chatHudLine = this.visibleMessages.get(n + this.scrolledLines);
            if (chatHudLine == null || (o = i - chatHudLine.getCreationTick()) >= 200 && !bl) continue;
            double g = bl ? 1.0 : ChatHud.method_19348(o);
            p = (int)(255.0 * g * e);
            q = (int)(255.0 * g * f);
            ++m;
            if (p <= 3) continue;
            boolean r = false;
            int s = -n * 9;
            ChatHud.fill(-2, s - 9, 0 + l + 4, s, q << 24);
            String string = chatHudLine.getText().asFormattedString();
            GlStateManager.enableBlend();
            this.client.textRenderer.drawWithShadow(string, 0.0f, s - 8, 0xFFFFFF + (p << 24));
            GlStateManager.disableAlphaTest();
            GlStateManager.disableBlend();
        }
        if (bl) {
            n = this.client.textRenderer.fontHeight;
            GlStateManager.translatef(-3.0f, 0.0f, 0.0f);
            int t = k * n + k;
            o = m * n + m;
            int u = this.scrolledLines * o / k;
            int v = o * o / t;
            if (t != o) {
                p = u > 0 ? 170 : 96;
                q = this.field_2067 ? 0xCC3333 : 0x3333AA;
                ChatHud.fill(0, -u, 2, -u - v, q + (p << 24));
                ChatHud.fill(2, -u, 1, -u - v, 0xCCCCCC + (p << 24));
            }
        }
        GlStateManager.popMatrix();
    }

    private static double method_19348(int i) {
        double d = (double)i / 200.0;
        d = 1.0 - d;
        d *= 10.0;
        d = MathHelper.clamp(d, 0.0, 1.0);
        d *= d;
        return d;
    }

    public void clear(boolean bl) {
        this.visibleMessages.clear();
        this.messages.clear();
        if (bl) {
            this.messageHistory.clear();
        }
    }

    public void addMessage(Text text) {
        this.addMessage(text, 0);
    }

    public void addMessage(Text text, int i) {
        this.addMessage(text, i, this.client.inGameHud.getTicks(), false);
        LOGGER.info("[CHAT] {}", (Object)text.getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
    }

    private void addMessage(Text text, int i, int j, boolean bl) {
        if (i != 0) {
            this.removeMessage(i);
        }
        int k = MathHelper.floor((double)this.getWidth() / this.getChatScale());
        List<Text> list = Texts.wrapLines(text, k, this.client.textRenderer, false, false);
        boolean bl2 = this.isChatFocused();
        for (Text text2 : list) {
            if (bl2 && this.scrolledLines > 0) {
                this.field_2067 = true;
                this.scroll(1.0);
            }
            this.visibleMessages.add(0, new ChatHudLine(j, text2, i));
        }
        while (this.visibleMessages.size() > 100) {
            this.visibleMessages.remove(this.visibleMessages.size() - 1);
        }
        if (!bl) {
            this.messages.add(0, new ChatHudLine(j, text, i));
            while (this.messages.size() > 100) {
                this.messages.remove(this.messages.size() - 1);
            }
        }
    }

    public void reset() {
        this.visibleMessages.clear();
        this.method_1820();
        for (int i = this.messages.size() - 1; i >= 0; --i) {
            ChatHudLine chatHudLine = this.messages.get(i);
            this.addMessage(chatHudLine.getText(), chatHudLine.getId(), chatHudLine.getCreationTick(), true);
        }
    }

    public List<String> getMessageHistory() {
        return this.messageHistory;
    }

    public void addToMessageHistory(String string) {
        if (this.messageHistory.isEmpty() || !this.messageHistory.get(this.messageHistory.size() - 1).equals(string)) {
            this.messageHistory.add(string);
        }
    }

    public void method_1820() {
        this.scrolledLines = 0;
        this.field_2067 = false;
    }

    public void scroll(double d) {
        this.scrolledLines = (int)((double)this.scrolledLines + d);
        int i = this.visibleMessages.size();
        if (this.scrolledLines > i - this.getVisibleLineCount()) {
            this.scrolledLines = i - this.getVisibleLineCount();
        }
        if (this.scrolledLines <= 0) {
            this.scrolledLines = 0;
            this.field_2067 = false;
        }
    }

    @Nullable
    public Text getText(double d, double e) {
        if (!this.isChatFocused()) {
            return null;
        }
        double f = this.getChatScale();
        double g = d - 2.0;
        double h = (double)this.client.window.getScaledHeight() - e - 40.0;
        g = MathHelper.floor(g / f);
        h = MathHelper.floor(h / f);
        if (g < 0.0 || h < 0.0) {
            return null;
        }
        int i = Math.min(this.getVisibleLineCount(), this.visibleMessages.size());
        if (g <= (double)MathHelper.floor((double)this.getWidth() / this.getChatScale()) && h < (double)(this.client.textRenderer.fontHeight * i + i)) {
            int j = (int)(h / (double)this.client.textRenderer.fontHeight + (double)this.scrolledLines);
            if (j >= 0 && j < this.visibleMessages.size()) {
                ChatHudLine chatHudLine = this.visibleMessages.get(j);
                int k = 0;
                for (Text text : chatHudLine.getText()) {
                    if (!(text instanceof LiteralText) || !((double)(k += this.client.textRenderer.getStringWidth(Texts.getRenderChatMessage(((LiteralText)text).getRawString(), false))) > g)) continue;
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

    public void removeMessage(int i) {
        ChatHudLine chatHudLine;
        Iterator<ChatHudLine> iterator = this.visibleMessages.iterator();
        while (iterator.hasNext()) {
            chatHudLine = iterator.next();
            if (chatHudLine.getId() != i) continue;
            iterator.remove();
        }
        iterator = this.messages.iterator();
        while (iterator.hasNext()) {
            chatHudLine = iterator.next();
            if (chatHudLine.getId() != i) continue;
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

