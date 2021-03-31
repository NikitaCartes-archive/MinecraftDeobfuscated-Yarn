/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.multiplayer;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collection;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmChatLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsPlayerListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.network.SocialInteractionsManager;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SocialInteractionsScreen
extends Screen {
    protected static final Identifier SOCIAL_INTERACTIONS_TEXTURE = new Identifier("textures/gui/social_interactions.png");
    private static final Text ALL_TAB_TITLE = new TranslatableText("gui.socialInteractions.tab_all");
    private static final Text HIDDEN_TAB_TITLE = new TranslatableText("gui.socialInteractions.tab_hidden");
    private static final Text BLOCKED_TAB_TITLE = new TranslatableText("gui.socialInteractions.tab_blocked");
    private static final Text SELECTED_ALL_TAB_TITLE = ALL_TAB_TITLE.copy().formatted(Formatting.UNDERLINE);
    private static final Text SELECTED_HIDDEN_TAB_TITLE = HIDDEN_TAB_TITLE.copy().formatted(Formatting.UNDERLINE);
    private static final Text SELECTED_BLOCKED_TAB_TITLE = BLOCKED_TAB_TITLE.copy().formatted(Formatting.UNDERLINE);
    private static final Text SEARCH_TEXT = new TranslatableText("gui.socialInteractions.search_hint").formatted(Formatting.ITALIC).formatted(Formatting.GRAY);
    private static final Text EMPTY_SEARCH_TEXT = new TranslatableText("gui.socialInteractions.search_empty").formatted(Formatting.GRAY);
    private static final Text EMPTY_HIDDEN_TEXT = new TranslatableText("gui.socialInteractions.empty_hidden").formatted(Formatting.GRAY);
    private static final Text EMPTY_BLOCKED_TEXT = new TranslatableText("gui.socialInteractions.empty_blocked").formatted(Formatting.GRAY);
    private static final Text BLOCKING_TEXT = new TranslatableText("gui.socialInteractions.blocking_hint");
    private static final String field_32423 = "https://aka.ms/javablocking";
    private static final int field_32424 = 8;
    private static final int field_32425 = 16;
    private static final int field_32426 = 236;
    private static final int field_32427 = 16;
    private static final int field_32428 = 64;
    public static final int field_32432 = 88;
    public static final int field_32433 = 78;
    private static final int field_32429 = 238;
    private static final int field_32430 = 20;
    private static final int field_32431 = 36;
    private SocialInteractionsPlayerListWidget playerList;
    private TextFieldWidget searchBox;
    private String currentSearch = "";
    private Tab currentTab = Tab.ALL;
    private ButtonWidget allTabButton;
    private ButtonWidget hiddenTabButton;
    private ButtonWidget blockedTabButton;
    private ButtonWidget blockingButton;
    @Nullable
    private Text serverLabel;
    private int playerCount;
    private boolean initialized;
    @Nullable
    private Runnable onRendered;

    public SocialInteractionsScreen() {
        super(new TranslatableText("gui.socialInteractions.title"));
        this.updateServerLabel(MinecraftClient.getInstance());
    }

    private int method_31359() {
        return Math.max(52, this.height - 128 - 16);
    }

    private int method_31360() {
        return this.method_31359() / 16;
    }

    private int method_31361() {
        return 80 + this.method_31360() * 16 - 8;
    }

    private int method_31362() {
        return (this.width - 238) / 2;
    }

    @Override
    public String getNarrationMessage() {
        return super.getNarrationMessage() + ". " + this.serverLabel.getString();
    }

    @Override
    public void tick() {
        super.tick();
        this.searchBox.tick();
    }

    @Override
    protected void init() {
        this.client.keyboard.setRepeatEvents(true);
        if (this.initialized) {
            this.playerList.updateSize(this.width, this.height, 88, this.method_31361());
        } else {
            this.playerList = new SocialInteractionsPlayerListWidget(this, this.client, this.width, this.height, 88, this.method_31361(), 36);
        }
        int i = this.playerList.getRowWidth() / 3;
        int j = this.playerList.getRowLeft();
        int k = this.playerList.getRowRight();
        int l = this.textRenderer.getWidth(BLOCKING_TEXT) + 40;
        int m = 64 + 16 * this.method_31360();
        int n = (this.width - l) / 2;
        this.allTabButton = this.addButton(new ButtonWidget(j, 45, i, 20, ALL_TAB_TITLE, buttonWidget -> this.setCurrentTab(Tab.ALL)));
        this.hiddenTabButton = this.addButton(new ButtonWidget((j + k - i) / 2 + 1, 45, i, 20, HIDDEN_TAB_TITLE, buttonWidget -> this.setCurrentTab(Tab.HIDDEN)));
        this.blockedTabButton = this.addButton(new ButtonWidget(k - i + 1, 45, i, 20, BLOCKED_TAB_TITLE, buttonWidget -> this.setCurrentTab(Tab.BLOCKED)));
        this.blockingButton = this.addButton(new ButtonWidget(n, m, l, 20, BLOCKING_TEXT, buttonWidget -> this.client.openScreen(new ConfirmChatLinkScreen(bl -> {
            if (bl) {
                Util.getOperatingSystem().open(field_32423);
            }
            this.client.openScreen(this);
        }, field_32423, true))));
        String string = this.searchBox != null ? this.searchBox.getText() : "";
        this.searchBox = new TextFieldWidget(this.textRenderer, this.method_31362() + 28, 78, 196, 16, SEARCH_TEXT){

            @Override
            protected MutableText getNarrationMessage() {
                if (!SocialInteractionsScreen.this.searchBox.getText().isEmpty() && SocialInteractionsScreen.this.playerList.isEmpty()) {
                    return super.getNarrationMessage().append(", ").append(EMPTY_SEARCH_TEXT);
                }
                return super.getNarrationMessage();
            }
        };
        this.searchBox.setMaxLength(16);
        this.searchBox.setDrawsBackground(false);
        this.searchBox.setVisible(true);
        this.searchBox.setEditableColor(0xFFFFFF);
        this.searchBox.setText(string);
        this.searchBox.setChangedListener(this::onSearchChange);
        this.children.add(this.searchBox);
        this.children.add(this.playerList);
        this.initialized = true;
        this.setCurrentTab(this.currentTab);
    }

    private void setCurrentTab(Tab currentTab) {
        Collection<UUID> collection;
        this.currentTab = currentTab;
        this.allTabButton.setMessage(ALL_TAB_TITLE);
        this.hiddenTabButton.setMessage(HIDDEN_TAB_TITLE);
        this.blockedTabButton.setMessage(BLOCKED_TAB_TITLE);
        switch (currentTab) {
            case ALL: {
                this.allTabButton.setMessage(SELECTED_ALL_TAB_TITLE);
                collection = this.client.player.networkHandler.getPlayerUuids();
                break;
            }
            case HIDDEN: {
                this.hiddenTabButton.setMessage(SELECTED_HIDDEN_TAB_TITLE);
                collection = this.client.getSocialInteractionsManager().getHiddenPlayers();
                break;
            }
            case BLOCKED: {
                this.blockedTabButton.setMessage(SELECTED_BLOCKED_TAB_TITLE);
                SocialInteractionsManager socialInteractionsManager = this.client.getSocialInteractionsManager();
                collection = this.client.player.networkHandler.getPlayerUuids().stream().filter(socialInteractionsManager::isPlayerBlocked).collect(Collectors.toSet());
                break;
            }
            default: {
                collection = ImmutableList.of();
            }
        }
        this.currentTab = currentTab;
        this.playerList.update(collection, this.playerList.getScrollAmount());
        if (!this.searchBox.getText().isEmpty() && this.playerList.isEmpty() && !this.searchBox.isFocused()) {
            NarratorManager.INSTANCE.narrate(EMPTY_SEARCH_TEXT.getString());
        } else if (collection.isEmpty()) {
            if (currentTab == Tab.HIDDEN) {
                NarratorManager.INSTANCE.narrate(EMPTY_HIDDEN_TEXT.getString());
            } else if (currentTab == Tab.BLOCKED) {
                NarratorManager.INSTANCE.narrate(EMPTY_BLOCKED_TEXT.getString());
            }
        }
    }

    @Override
    public void removed() {
        this.client.keyboard.setRepeatEvents(false);
    }

    @Override
    public void renderBackground(MatrixStack matrices) {
        int i = this.method_31362() + 3;
        super.renderBackground(matrices);
        RenderSystem.setShaderTexture(0, SOCIAL_INTERACTIONS_TEXTURE);
        this.drawTexture(matrices, i, 64, 1, 1, 236, 8);
        int j = this.method_31360();
        for (int k = 0; k < j; ++k) {
            this.drawTexture(matrices, i, 72 + 16 * k, 1, 10, 236, 16);
        }
        this.drawTexture(matrices, i, 72 + 16 * j, 1, 27, 236, 8);
        this.drawTexture(matrices, i + 10, 76, 243, 1, 12, 12);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.updateServerLabel(this.client);
        this.renderBackground(matrices);
        if (this.serverLabel != null) {
            SocialInteractionsScreen.drawTextWithShadow(matrices, this.client.textRenderer, this.serverLabel, this.method_31362() + 8, 35, -1);
        }
        if (!this.playerList.isEmpty()) {
            this.playerList.render(matrices, mouseX, mouseY, delta);
        } else if (!this.searchBox.getText().isEmpty()) {
            SocialInteractionsScreen.drawCenteredText(matrices, this.client.textRenderer, EMPTY_SEARCH_TEXT, this.width / 2, (78 + this.method_31361()) / 2, -1);
        } else {
            switch (this.currentTab) {
                case HIDDEN: {
                    SocialInteractionsScreen.drawCenteredText(matrices, this.client.textRenderer, EMPTY_HIDDEN_TEXT, this.width / 2, (78 + this.method_31361()) / 2, -1);
                    break;
                }
                case BLOCKED: {
                    SocialInteractionsScreen.drawCenteredText(matrices, this.client.textRenderer, EMPTY_BLOCKED_TEXT, this.width / 2, (78 + this.method_31361()) / 2, -1);
                }
            }
        }
        if (!this.searchBox.isFocused() && this.searchBox.getText().isEmpty()) {
            SocialInteractionsScreen.drawTextWithShadow(matrices, this.client.textRenderer, SEARCH_TEXT, this.searchBox.x, this.searchBox.y, -1);
        } else {
            this.searchBox.render(matrices, mouseX, mouseY, delta);
        }
        this.blockingButton.visible = this.currentTab == Tab.BLOCKED;
        super.render(matrices, mouseX, mouseY, delta);
        if (this.onRendered != null) {
            this.onRendered.run();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.searchBox.isFocused()) {
            this.searchBox.mouseClicked(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button) || this.playerList.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!this.searchBox.isFocused() && this.client.options.keySocialInteractions.matchesKey(keyCode, scanCode)) {
            this.client.openScreen(null);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void onSearchChange(String currentSearch) {
        if (!(currentSearch = currentSearch.toLowerCase(Locale.ROOT)).equals(this.currentSearch)) {
            this.playerList.setCurrentSearch(currentSearch);
            this.currentSearch = currentSearch;
            this.setCurrentTab(this.currentTab);
        }
    }

    private void updateServerLabel(MinecraftClient client) {
        int i = client.getNetworkHandler().getPlayerList().size();
        if (this.playerCount != i) {
            String string = "";
            ServerInfo serverInfo = client.getCurrentServerEntry();
            if (client.isInSingleplayer()) {
                string = client.getServer().getServerMotd();
            } else if (serverInfo != null) {
                string = serverInfo.name;
            }
            this.serverLabel = i > 1 ? new TranslatableText("gui.socialInteractions.server_label.multiple", string, i) : new TranslatableText("gui.socialInteractions.server_label.single", string, i);
            this.playerCount = i;
        }
    }

    public void setPlayerOnline(PlayerListEntry player) {
        this.playerList.setPlayerOnline(player, this.currentTab);
    }

    public void setPlayerOffline(UUID uuid) {
        this.playerList.setPlayerOffline(uuid);
    }

    public void setOnRendered(@Nullable Runnable onRendered) {
        this.onRendered = onRendered;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Tab {
        ALL,
        HIDDEN,
        BLOCKED;

    }
}

