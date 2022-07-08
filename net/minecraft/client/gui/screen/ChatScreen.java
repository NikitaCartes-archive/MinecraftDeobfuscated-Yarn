/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.brigadier.tree.CommandNode;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ChatPreviewer;
import net.minecraft.client.network.ServerInfo;
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
    public static final double SHIFT_SCROLL_AMOUNT = 7.0;
    private static final Text USAGE_TEXT = Text.translatable("chat_screen.usage");
    private static final int PREVIEW_LEFT_MARGIN = 2;
    private static final int PREVIEW_RIGHT_MARGIN = 2;
    private static final int PREVIEW_BOTTOM_MARGIN = 15;
    private static final Text CHAT_PREVIEW_WARNING_TOAST_TITLE = Text.translatable("chatPreview.warning.toast.title");
    private static final Text CHAT_PREVIEW_WARNING_TOAST_TEXT = Text.translatable("chatPreview.warning.toast");
    private static final Text CHAT_PREVIEW_PLACEHOLDER_TEXT = Text.translatable("chat.preview").formatted(Formatting.DARK_GRAY);
    private static final int MAX_INDICATOR_TOOLTIP_WIDTH = 260;
    private String chatLastMessage = "";
    private int messageHistorySize = -1;
    protected TextFieldWidget chatField;
    private String originalChatText;
    ChatInputSuggestor commandSuggestor;
    private ChatPreviewer chatPreviewer;

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
                return super.getNarrationMessage().append(ChatScreen.this.commandSuggestor.getNarration());
            }
        };
        this.chatField.setMaxLength(256);
        this.chatField.setDrawsBackground(false);
        this.chatField.setText(this.originalChatText);
        this.chatField.setChangedListener(this::onChatFieldUpdate);
        this.addSelectableChild(this.chatField);
        this.commandSuggestor = new ChatInputSuggestor(this.client, this, this.chatField, this.textRenderer, false, false, 1, 10, true, -805306368);
        this.commandSuggestor.refresh();
        this.setInitialFocus(this.chatField);
        this.chatPreviewer = new ChatPreviewer(this.client);
        this.updatePreviewer(this.chatField.getText());
        ServerInfo serverInfo = this.client.getCurrentServerEntry();
        if (serverInfo != null && this.client.options.getChatPreview().getValue().booleanValue() && (chatPreview = serverInfo.getChatPreview()) != null && serverInfo.shouldPreviewChat() && chatPreview.showToast()) {
            ServerList.updateServerListEntry(serverInfo);
            SystemToast systemToast = SystemToast.create(this.client, SystemToast.Type.CHAT_PREVIEW_WARNING, CHAT_PREVIEW_WARNING_TOAST_TITLE, CHAT_PREVIEW_WARNING_TOAST_TEXT);
            this.client.getToastManager().add(systemToast);
        }
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        String string = this.chatField.getText();
        this.init(client, width, height);
        this.setText(string);
        this.commandSuggestor.refresh();
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
        this.commandSuggestor.setWindowActive(!string.equals(this.originalChatText));
        this.commandSuggestor.refresh();
        this.updatePreviewer(string);
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
        CommandNode<CommandSource> commandNode = this.commandSuggestor.getNodeAt(this.chatField.getCursor());
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
        if (this.client.options.getChatPreview().getValue().booleanValue()) {
            ServerInfo serverInfo = this.client.getCurrentServerEntry();
            return serverInfo != null && serverInfo.shouldPreviewChat();
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.commandSuggestor.keyPressed(keyCode, scanCode, modifiers)) {
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
            this.sendMessage(this.chatField.getText(), true);
            this.client.setScreen(null);
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
        if (this.commandSuggestor.mouseScrolled(amount = MathHelper.clamp(amount, -1.0, 1.0))) {
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
        if (this.commandSuggestor.mouseClicked((int)mouseX, (int)mouseY, button)) {
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
        this.commandSuggestor.setWindowActive(false);
        this.messageHistorySize = i;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.setFocused(this.chatField);
        this.chatField.setTextFieldFocused(true);
        ChatScreen.fill(matrices, 2, this.height - 14, this.width - 2, this.height - 2, this.client.options.getTextBackgroundColor(Integer.MIN_VALUE));
        this.chatField.render(matrices, mouseX, mouseY, delta);
        if (this.chatPreviewer.shouldRenderPreview()) {
            this.renderChatPreview(matrices);
        } else {
            this.commandSuggestor.render(matrices, mouseX, mouseY);
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

    public void renderChatPreview(MatrixStack matrices) {
        int i = (int)(255.0 * (this.client.options.getChatOpacity().getValue() * (double)0.9f + (double)0.1f));
        int j = (int)(255.0 * this.client.options.getTextBackgroundOpacity().getValue());
        int k = this.getPreviewWidth();
        List<OrderedText> list = this.getPreviewText();
        int l = this.getPreviewHeight(list);
        RenderSystem.enableBlend();
        matrices.push();
        matrices.translate(this.getPreviewLeft(), this.getPreviewTop(l), 0.0);
        ChatScreen.fill(matrices, 0, 0, k, l, j << 24);
        matrices.translate(2.0, 2.0, 0.0);
        for (int m = 0; m < list.size(); ++m) {
            OrderedText orderedText = list.get(m);
            this.client.textRenderer.drawWithShadow(matrices, orderedText, 0.0f, (float)(m * this.textRenderer.fontHeight), i << 24 | 0xFFFFFF);
        }
        matrices.pop();
        RenderSystem.disableBlend();
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
        List<OrderedText> list = this.getPreviewText();
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

    private List<OrderedText> getPreviewText() {
        Text text = this.chatPreviewer.getPreviewText();
        if (text != null) {
            return this.textRenderer.wrapLines(text, this.getPreviewWidth());
        }
        return List.of(CHAT_PREVIEW_PLACEHOLDER_TEXT.asOrderedText());
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

    public void sendMessage(String chatText, boolean addToHistory) {
        if ((chatText = this.normalize(chatText)).isEmpty()) {
            return;
        }
        if (addToHistory) {
            this.client.inGameHud.getChatHud().addToMessageHistory(chatText);
        }
        Text text = this.chatPreviewer.tryConsumeResponse(chatText);
        if (chatText.startsWith("/")) {
            this.client.player.sendCommand(chatText.substring(1), text);
        } else {
            this.client.player.sendChatMessage(chatText, text);
        }
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

