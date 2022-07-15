/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.brigadier.tree.CommandNode;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextHandler;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.ChatPreviewBackground;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ChatPreviewer;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ChatPreviewMode;
import net.minecraft.client.option.ServerList;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.DecoratableArgumentType;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

/**
 * A screen that allows player to input a chat message. It can be opened by
 * pressing {@linkplain net.minecraft.client.option.GameOptions#chatKey the
 * chat key} or {@linkplain net.minecraft.client.option.GameOptions#commandKey
 * the command key}.
 * 
 * @see net.minecraft.client.gui.hud.ChatHud
 */
@Environment(value=EnvType.CLIENT)
public class ChatScreen
extends Screen {
    private static final int PREVIEW_PENDING_COLOR = 0xFF7F00;
    private static final int PREVIEW_CONSUMABLE_COLOR = 65280;
    public static final double SHIFT_SCROLL_AMOUNT = 7.0;
    private static final Text USAGE_TEXT = Text.translatable("chat_screen.usage");
    private static final int PREVIEW_LEFT_MARGIN = 2;
    private static final int PREVIEW_RIGHT_MARGIN = 2;
    private static final int PREVIEW_BOTTOM_MARGIN = 15;
    private static final Text CHAT_PREVIEW_WARNING_TOAST_TITLE = Text.translatable("chatPreview.warning.toast.title");
    private static final Text CHAT_PREVIEW_WARNING_TOAST_TEXT = Text.translatable("chatPreview.warning.toast");
    private static final Text CHAT_PREVIEW_INPUT_TEXT = Text.translatable("chat.previewInput", Text.translatable("key.keyboard.enter")).formatted(Formatting.DARK_GRAY);
    private static final int MAX_INDICATOR_TOOLTIP_WIDTH = 260;
    private static final int EVENT_HIGHLIGHT_COLOR = 10533887;
    private String chatLastMessage = "";
    private int messageHistorySize = -1;
    protected TextFieldWidget chatField;
    private String originalChatText;
    ChatInputSuggestor chatInputSuggestor;
    private ChatPreviewer chatPreviewer;
    private ChatPreviewMode chatPreviewMode;
    private boolean missingPreview;
    private final ChatPreviewBackground chatPreviewBackground = new ChatPreviewBackground();

    public ChatScreen(String originalChatText) {
        super(Text.translatable("chat_screen.title"));
        this.originalChatText = originalChatText;
    }

    @Override
    protected void init() {
        ServerInfo.ChatPreview chatPreview;
        this.client.keyboard.setRepeatEvents(true);
        this.messageHistorySize = this.client.inGameHud.getChatHud().getMessageHistory().size();
        this.chatField = new TextFieldWidget(this.textRenderer, 4, this.height - 12, this.width - 4, 12, (Text)Text.translatable("chat.editBox")){

            @Override
            protected MutableText getNarrationMessage() {
                return super.getNarrationMessage().append(ChatScreen.this.chatInputSuggestor.getNarration());
            }
        };
        this.chatField.setMaxLength(256);
        this.chatField.setDrawsBackground(false);
        this.chatField.setText(this.originalChatText);
        this.chatField.setChangedListener(this::onChatFieldUpdate);
        this.addSelectableChild(this.chatField);
        this.chatInputSuggestor = new ChatInputSuggestor(this.client, this, this.chatField, this.textRenderer, false, false, 1, 10, true, -805306368);
        this.chatInputSuggestor.refresh();
        this.setInitialFocus(this.chatField);
        this.chatPreviewBackground.init(Util.getMeasuringTimeMs());
        this.chatPreviewer = new ChatPreviewer(this.client);
        this.updatePreviewer(this.chatField.getText());
        ServerInfo serverInfo = this.client.getCurrentServerEntry();
        ChatPreviewMode chatPreviewMode = this.chatPreviewMode = serverInfo != null && !serverInfo.shouldPreviewChat() ? ChatPreviewMode.OFF : this.client.options.getChatPreview().getValue();
        if (serverInfo != null && this.chatPreviewMode != ChatPreviewMode.OFF && (chatPreview = serverInfo.getChatPreview()) != null && serverInfo.shouldPreviewChat() && chatPreview.showToast()) {
            ServerList.updateServerListEntry(serverInfo);
            SystemToast systemToast = SystemToast.create(this.client, SystemToast.Type.CHAT_PREVIEW_WARNING, CHAT_PREVIEW_WARNING_TOAST_TITLE, CHAT_PREVIEW_WARNING_TOAST_TEXT);
            this.client.getToastManager().add(systemToast);
        }
        if (this.chatPreviewMode == ChatPreviewMode.CONFIRM) {
            this.missingPreview = this.originalChatText.startsWith("/") && !this.client.player.shouldPreview(this.originalChatText.substring(1));
        }
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        String string = this.chatField.getText();
        this.init(client, width, height);
        this.setText(string);
        this.chatInputSuggestor.refresh();
    }

    @Override
    public void removed() {
        this.client.keyboard.setRepeatEvents(false);
        this.client.inGameHud.getChatHud().resetScroll();
    }

    @Override
    public void tick() {
        this.chatField.tick();
        this.chatPreviewer.tryRequestPending();
    }

    private void onChatFieldUpdate(String chatText) {
        String string = this.chatField.getText();
        this.chatInputSuggestor.setWindowActive(!string.equals(this.originalChatText));
        this.chatInputSuggestor.refresh();
        if (this.chatPreviewMode == ChatPreviewMode.LIVE) {
            this.updatePreviewer(string);
        } else if (this.chatPreviewMode == ChatPreviewMode.CONFIRM && !this.chatPreviewer.equalsLastPreviewed(string)) {
            this.missingPreview = string.startsWith("/") && !this.client.player.shouldPreview(string.substring(1));
            this.chatPreviewer.tryRequest("");
        }
    }

    private void updatePreviewer(String chatText) {
        String string = this.normalize(chatText);
        if (this.shouldPreviewChat()) {
            this.tryRequestPreview(string);
        } else {
            this.chatPreviewer.disablePreview();
        }
    }

    private void tryRequestPreview(String chatText) {
        if (chatText.startsWith("/")) {
            this.tryRequestCommandPreview(chatText);
        } else {
            this.tryRequestChatPreview(chatText);
        }
    }

    private void tryRequestChatPreview(String chatText) {
        this.chatPreviewer.tryRequest(chatText);
    }

    private void tryRequestCommandPreview(String chatText) {
        CommandNode<CommandSource> commandNode = this.chatInputSuggestor.getNodeAt(this.chatField.getCursor());
        if (commandNode != null && DecoratableArgumentType.isDecoratableArgumentNode(commandNode)) {
            this.chatPreviewer.tryRequest(chatText);
        } else {
            this.chatPreviewer.disablePreview();
        }
    }

    /**
     * {@return whether the client and the server both allow chat previews}
     * 
     * <p>To check if the client allows chat previews, check {@linkplain
     * net.minecraft.client.option.GameOptions#getChatPreview the chat preview option}.
     * To check if the server allows chat previews, check {@link
     * net.minecraft.client.network.ServerInfo#shouldPreviewChat}.
     */
    private boolean shouldPreviewChat() {
        if (this.client.player == null) {
            return false;
        }
        if (this.client.isInSingleplayer()) {
            return true;
        }
        if (this.chatPreviewMode != ChatPreviewMode.OFF) {
            ServerInfo serverInfo = this.client.getCurrentServerEntry();
            return serverInfo != null && serverInfo.shouldPreviewChat();
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.chatInputSuggestor.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.client.setScreen(null);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            if (this.sendMessage(this.chatField.getText(), true)) {
                this.client.setScreen(null);
            }
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_UP) {
            this.setChatFromHistory(-1);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_DOWN) {
            this.setChatFromHistory(1);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_PAGE_UP) {
            this.client.inGameHud.getChatHud().scroll(this.client.inGameHud.getChatHud().getVisibleLineCount() - 1);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_PAGE_DOWN) {
            this.client.inGameHud.getChatHud().scroll(-this.client.inGameHud.getChatHud().getVisibleLineCount() + 1);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (this.chatInputSuggestor.mouseScrolled(amount = MathHelper.clamp(amount, -1.0, 1.0))) {
            return true;
        }
        if (!ChatScreen.hasShiftDown()) {
            amount *= 7.0;
        }
        this.client.inGameHud.getChatHud().scroll((int)amount);
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.chatInputSuggestor.mouseClicked((int)mouseX, (int)mouseY, button)) {
            return true;
        }
        if (button == 0) {
            ChatHud chatHud = this.client.inGameHud.getChatHud();
            if (chatHud.mouseClicked(mouseX, mouseY)) {
                return true;
            }
            Style style = this.getTextStyleAt(mouseX, mouseY);
            if (style != null && this.handleTextClick(style)) {
                this.originalChatText = this.chatField.getText();
                return true;
            }
        }
        if (this.chatField.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void insertText(String text, boolean override) {
        if (override) {
            this.chatField.setText(text);
        } else {
            this.chatField.write(text);
        }
    }

    public void setChatFromHistory(int offset) {
        int i = this.messageHistorySize + offset;
        int j = this.client.inGameHud.getChatHud().getMessageHistory().size();
        if ((i = MathHelper.clamp(i, 0, j)) == this.messageHistorySize) {
            return;
        }
        if (i == j) {
            this.messageHistorySize = j;
            this.chatField.setText(this.chatLastMessage);
            return;
        }
        if (this.messageHistorySize == j) {
            this.chatLastMessage = this.chatField.getText();
        }
        this.chatField.setText(this.client.inGameHud.getChatHud().getMessageHistory().get(i));
        this.chatInputSuggestor.setWindowActive(false);
        this.messageHistorySize = i;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        float f;
        Text text;
        boolean bl;
        this.setFocused(this.chatField);
        this.chatField.setTextFieldFocused(true);
        ChatScreen.fill(matrices, 2, this.height - 14, this.width - 2, this.height - 2, this.client.options.getTextBackgroundColor(Integer.MIN_VALUE));
        this.chatField.render(matrices, mouseX, mouseY, delta);
        boolean bl2 = bl = this.client.getProfileKeys().getSigner() != null;
        if (this.chatPreviewMode == ChatPreviewMode.CONFIRM && !this.missingPreview) {
            String string = this.chatField.getText();
            text = Objects.requireNonNullElse(this.getPreviewText(), this.chatPreviewer.equalsLastPreviewed(string) && !string.startsWith("/") ? Text.literal(string) : CHAT_PREVIEW_INPUT_TEXT);
            f = 1.0f;
        } else {
            ChatPreviewBackground.RenderData renderData = this.chatPreviewBackground.computeRenderData(Util.getMeasuringTimeMs(), this.getPreviewText());
            text = renderData.preview();
            f = renderData.alpha();
        }
        if (text != null) {
            this.renderChatPreview(matrices, text, f, bl);
            this.chatInputSuggestor.tryRenderWindow(matrices, mouseX, mouseY);
        } else {
            this.chatInputSuggestor.render(matrices, mouseX, mouseY);
            if (bl) {
                matrices.push();
                ChatScreen.fill(matrices, 0, this.height - 14, 2, this.height - 2, -16711936);
                matrices.pop();
            }
        }
        Style style = this.getTextStyleAt(mouseX, mouseY);
        if (style != null && style.getHoverEvent() != null) {
            this.renderTextHoverEffect(matrices, style, mouseX, mouseY);
        } else {
            MessageIndicator messageIndicator = this.client.inGameHud.getChatHud().getIndicatorAt(mouseX, mouseY);
            if (messageIndicator != null && messageIndicator.text() != null) {
                this.renderOrderedTooltip(matrices, this.textRenderer.wrapLines(messageIndicator.text(), 260), mouseX, mouseY);
            }
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private void setText(String text) {
        this.chatField.setText(text);
    }

    @Override
    protected void addScreenNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, this.getTitle());
        builder.put(NarrationPart.USAGE, USAGE_TEXT);
        String string = this.chatField.getText();
        if (!string.isEmpty()) {
            builder.nextMessage().put(NarrationPart.TITLE, (Text)Text.translatable("chat_screen.message", string));
        }
    }

    public void renderChatPreview(MatrixStack matrices, Text previewText, float alpha, boolean signable) {
        int n;
        int i = (int)(255.0 * (this.client.options.getChatOpacity().getValue() * (double)0.9f + (double)0.1f) * (double)alpha);
        int j = (int)((double)(this.chatPreviewer.cannotConsumePreview() ? 127 : 255) * this.client.options.getTextBackgroundOpacity().getValue() * (double)alpha);
        int k = this.getPreviewWidth();
        List<OrderedText> list = this.wrapPreviewText(previewText);
        int l = this.getPreviewHeight(list);
        int m = this.getPreviewTop(l);
        RenderSystem.enableBlend();
        matrices.push();
        matrices.translate(this.getPreviewLeft(), m, 0.0);
        ChatScreen.fill(matrices, 0, 0, k, l, j << 24);
        if (i > 0) {
            matrices.translate(2.0, 2.0, 0.0);
            for (n = 0; n < list.size(); ++n) {
                OrderedText orderedText = list.get(n);
                int o = n * this.textRenderer.fontHeight;
                this.drawEventHighlight(matrices, orderedText, o, i);
                this.textRenderer.drawWithShadow(matrices, orderedText, 0.0f, (float)o, i << 24 | 0xFFFFFF);
            }
        }
        matrices.pop();
        RenderSystem.disableBlend();
        if (signable && this.chatPreviewer.getPreviewText() != null) {
            n = this.chatPreviewer.cannotConsumePreview() ? 0xFF7F00 : 65280;
            int p = (int)(255.0f * alpha);
            matrices.push();
            ChatScreen.fill(matrices, 0, m, 2, this.getPreviewBottom(), p << 24 | n);
            matrices.pop();
        }
    }

    private void drawEventHighlight(MatrixStack matrices, OrderedText text, int y, int alpha) {
        int i = y + this.textRenderer.fontHeight;
        int j = alpha << 24 | 0xA0BBFF;
        Predicate<Style> predicate = style -> style.getHoverEvent() != null || style.getClickEvent() != null;
        for (TextHandler.MatchResult matchResult : this.textRenderer.getTextHandler().getStyleMatchResults(text, predicate)) {
            int k = MathHelper.floor(matchResult.left());
            int l = MathHelper.ceil(matchResult.right());
            ChatScreen.fill(matrices, k, y, l, i, j);
        }
    }

    @Nullable
    private Style getTextStyleAt(double x, double y) {
        Style style = this.client.inGameHud.getChatHud().getTextStyleAt(x, y);
        if (style == null) {
            style = this.getPreviewTextStyleAt(x, y);
        }
        return style;
    }

    @Nullable
    private Style getPreviewTextStyleAt(double x, double y) {
        if (this.client.options.hudHidden) {
            return null;
        }
        Text text = this.getPreviewText();
        if (text == null) {
            return null;
        }
        List<OrderedText> list = this.wrapPreviewText(text);
        int i = this.getPreviewHeight(list);
        if (x < (double)this.getPreviewLeft() || x > (double)this.getPreviewRight() || y < (double)this.getPreviewTop(i) || y > (double)this.getPreviewBottom()) {
            return null;
        }
        int j = this.getPreviewLeft() + 2;
        int k = this.getPreviewTop(i) + 2;
        int l = (MathHelper.floor(y) - k) / this.textRenderer.fontHeight;
        if (l >= 0 && l < list.size()) {
            OrderedText orderedText = list.get(l);
            return this.client.textRenderer.getTextHandler().getStyleAt(orderedText, (int)(x - (double)j));
        }
        return null;
    }

    @Nullable
    private Text getPreviewText() {
        return Util.map(this.chatPreviewer.getPreviewText(), ChatPreviewer.Response::previewText);
    }

    private List<OrderedText> wrapPreviewText(Text preview) {
        return this.textRenderer.wrapLines(preview, this.getPreviewWidth());
    }

    private int getPreviewWidth() {
        return this.client.currentScreen.width - 4;
    }

    private int getPreviewHeight(List<OrderedText> lines) {
        return Math.max(lines.size(), 1) * this.textRenderer.fontHeight + 4;
    }

    private int getPreviewBottom() {
        return this.client.currentScreen.height - 15;
    }

    private int getPreviewTop(int previewHeight) {
        return this.getPreviewBottom() - previewHeight;
    }

    private int getPreviewLeft() {
        return 2;
    }

    private int getPreviewRight() {
        return this.client.currentScreen.width - 2;
    }

    public boolean sendMessage(String chatText, boolean addToHistory) {
        if ((chatText = this.normalize(chatText)).isEmpty()) {
            return true;
        }
        if (this.chatPreviewMode == ChatPreviewMode.CONFIRM && !this.missingPreview) {
            this.chatInputSuggestor.clearWindow();
            if (!this.chatPreviewer.equalsLastPreviewed(chatText)) {
                this.updatePreviewer(chatText);
                return false;
            }
        }
        if (addToHistory) {
            this.client.inGameHud.getChatHud().addToMessageHistory(chatText);
        }
        Text text = Util.map(this.chatPreviewer.tryConsumeResponse(chatText), ChatPreviewer.Response::previewText);
        if (chatText.startsWith("/")) {
            this.client.player.sendCommand(chatText.substring(1), text);
        } else {
            this.client.player.sendChatMessage(chatText, text);
        }
        return true;
    }

    /**
     * {@return the {@code message} normalized by trimming it and then normalizing spaces}
     */
    public String normalize(String chatText) {
        return StringUtils.normalizeSpace(chatText.trim());
    }

    public ChatPreviewer getChatPreviewer() {
        return this.chatPreviewer;
    }
}

