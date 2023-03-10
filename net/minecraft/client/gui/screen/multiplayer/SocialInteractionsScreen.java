/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.multiplayer;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collection;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.SocialInteractionsPlayerListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.network.SocialInteractionsManager;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SocialInteractionsScreen
extends Screen {
    protected static final Identifier SOCIAL_INTERACTIONS_TEXTURE = new Identifier("textures/gui/social_interactions.png");
    private static final Text ALL_TAB_TITLE = Text.translatable("gui.socialInteractions.tab_all");
    private static final Text HIDDEN_TAB_TITLE = Text.translatable("gui.socialInteractions.tab_hidden");
    private static final Text BLOCKED_TAB_TITLE = Text.translatable("gui.socialInteractions.tab_blocked");
    private static final Text SELECTED_ALL_TAB_TITLE = ALL_TAB_TITLE.copyContentOnly().formatted(Formatting.UNDERLINE);
    private static final Text SELECTED_HIDDEN_TAB_TITLE = HIDDEN_TAB_TITLE.copyContentOnly().formatted(Formatting.UNDERLINE);
    private static final Text SELECTED_BLOCKED_TAB_TITLE = BLOCKED_TAB_TITLE.copyContentOnly().formatted(Formatting.UNDERLINE);
    private static final Text SEARCH_TEXT = Text.translatable("gui.socialInteractions.search_hint").formatted(Formatting.ITALIC).formatted(Formatting.GRAY);
    static final Text EMPTY_SEARCH_TEXT = Text.translatable("gui.socialInteractions.search_empty").formatted(Formatting.GRAY);
    private static final Text EMPTY_HIDDEN_TEXT = Text.translatable("gui.socialInteractions.empty_hidden").formatted(Formatting.GRAY);
    private static final Text EMPTY_BLOCKED_TEXT = Text.translatable("gui.socialInteractions.empty_blocked").formatted(Formatting.GRAY);
    private static final Text BLOCKING_TEXT = Text.translatable("gui.socialInteractions.blocking_hint");
    private static final int field_32424 = 8;
    private static final int field_32426 = 236;
    private static final int field_32427 = 16;
    private static final int field_32428 = 64;
    public static final int field_32433 = 72;
    public static final int field_32432 = 88;
    private static final int field_32429 = 238;
    private static final int field_32430 = 20;
    private static final int field_32431 = 36;
    SocialInteractionsPlayerListWidget playerList;
    TextFieldWidget searchBox;
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

    public SocialInteractionsScreen() {
        super(Text.translatable("gui.socialInteractions.title"));
        this.updateServerLabel(MinecraftClient.getInstance());
    }

    private int getScreenHeight() {
        return Math.max(52, this.height - 128 - 16);
    }

    private int getPlayerListBottom() {
        return 80 + this.getScreenHeight() - 8;
    }

    private int getSearchBoxX() {
        return (this.width - 238) / 2;
    }

    @Override
    public Text getNarratedTitle() {
        if (this.serverLabel != null) {
            return ScreenTexts.joinSentences(super.getNarratedTitle(), this.serverLabel);
        }
        return super.getNarratedTitle();
    }

    @Override
    public void tick() {
        super.tick();
        this.searchBox.tick();
    }

    @Override
    protected void init() {
        if (this.initialized) {
            this.playerList.updateSize(this.width, this.height, 88, this.getPlayerListBottom());
        } else {
            this.playerList = new SocialInteractionsPlayerListWidget(this, this.client, this.width, this.height, 88, this.getPlayerListBottom(), 36);
        }
        int i = this.playerList.getRowWidth() / 3;
        int j = this.playerList.getRowLeft();
        int k = this.playerList.getRowRight();
        int l = this.textRenderer.getWidth(BLOCKING_TEXT) + 40;
        int m = 64 + this.getScreenHeight();
        int n = (this.width - l) / 2 + 3;
        this.allTabButton = this.addDrawableChild(ButtonWidget.builder(ALL_TAB_TITLE, button -> this.setCurrentTab(Tab.ALL)).dimensions(j, 45, i, 20).build());
        this.hiddenTabButton = this.addDrawableChild(ButtonWidget.builder(HIDDEN_TAB_TITLE, button -> this.setCurrentTab(Tab.HIDDEN)).dimensions((j + k - i) / 2 + 1, 45, i, 20).build());
        this.blockedTabButton = this.addDrawableChild(ButtonWidget.builder(BLOCKED_TAB_TITLE, button -> this.setCurrentTab(Tab.BLOCKED)).dimensions(k - i + 1, 45, i, 20).build());
        String string = this.searchBox != null ? this.searchBox.getText() : "";
        this.searchBox = new TextFieldWidget(this.textRenderer, this.getSearchBoxX() + 29, 75, 198, 13, SEARCH_TEXT){

            @Override
            protected MutableText getNarrationMessage() {
                if (!SocialInteractionsScreen.this.searchBox.getText().isEmpty() && SocialInteractionsScreen.this.playerList.isEmpty()) {
                    return super.getNarrationMessage().append(", ").append(EMPTY_SEARCH_TEXT);
                }
                return super.getNarrationMessage();
            }
        };
        this.searchBox.setMaxLength(16);
        this.searchBox.setVisible(true);
        this.searchBox.setEditableColor(0xFFFFFF);
        this.searchBox.setText(string);
        this.searchBox.setPlaceholder(SEARCH_TEXT);
        this.searchBox.setChangedListener(this::onSearchChange);
        this.addSelectableChild(this.searchBox);
        this.addSelectableChild(this.playerList);
        this.blockingButton = this.addDrawableChild(ButtonWidget.builder(BLOCKING_TEXT, button -> this.client.setScreen(new ConfirmLinkScreen(confirmed -> {
            if (confirmed) {
                Util.getOperatingSystem().open("https://aka.ms/javablocking");
            }
            this.client.setScreen(this);
        }, "https://aka.ms/javablocking", true))).dimensions(n, m, l, 20).build());
        this.initialized = true;
        this.setCurrentTab(this.currentTab);
    }

    private void setCurrentTab(Tab currentTab) {
        this.currentTab = currentTab;
        this.allTabButton.setMessage(ALL_TAB_TITLE);
        this.hiddenTabButton.setMessage(HIDDEN_TAB_TITLE);
        this.blockedTabButton.setMessage(BLOCKED_TAB_TITLE);
        boolean bl = false;
        switch (currentTab) {
            case ALL: {
                this.allTabButton.setMessage(SELECTED_ALL_TAB_TITLE);
                Collection<UUID> collection = this.client.player.networkHandler.getPlayerUuids();
                this.playerList.update(collection, this.playerList.getScrollAmount(), true);
                break;
            }
            case HIDDEN: {
                this.hiddenTabButton.setMessage(SELECTED_HIDDEN_TAB_TITLE);
                Set<UUID> set = this.client.getSocialInteractionsManager().getHiddenPlayers();
                bl = set.isEmpty();
                this.playerList.update(set, this.playerList.getScrollAmount(), false);
                break;
            }
            case BLOCKED: {
                this.blockedTabButton.setMessage(SELECTED_BLOCKED_TAB_TITLE);
                SocialInteractionsManager socialInteractionsManager = this.client.getSocialInteractionsManager();
                Set<UUID> set2 = this.client.player.networkHandler.getPlayerUuids().stream().filter(socialInteractionsManager::isPlayerBlocked).collect(Collectors.toSet());
                bl = set2.isEmpty();
                this.playerList.update(set2, this.playerList.getScrollAmount(), false);
            }
        }
        NarratorManager narratorManager = this.client.getNarratorManager();
        if (!this.searchBox.getText().isEmpty() && this.playerList.isEmpty() && !this.searchBox.isFocused()) {
            narratorManager.narrate(EMPTY_SEARCH_TEXT);
        } else if (bl) {
            if (currentTab == Tab.HIDDEN) {
                narratorManager.narrate(EMPTY_HIDDEN_TEXT);
            } else if (currentTab == Tab.BLOCKED) {
                narratorManager.narrate(EMPTY_BLOCKED_TEXT);
            }
        }
    }

    @Override
    public void renderBackground(MatrixStack matrices) {
        int i = this.getSearchBoxX() + 3;
        super.renderBackground(matrices);
        RenderSystem.setShaderTexture(0, SOCIAL_INTERACTIONS_TEXTURE);
        SocialInteractionsScreen.drawNineSlicedTexture(matrices, i, 64, 236, this.getScreenHeight() + 16, 8, 236, 34, 1, 1);
        SocialInteractionsScreen.drawTexture(matrices, i + 10, 76, 243, 1, 12, 12);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.updateServerLabel(this.client);
        this.renderBackground(matrices);
        if (this.serverLabel != null) {
            SocialInteractionsScreen.drawTextWithShadow(matrices, this.client.textRenderer, this.serverLabel, this.getSearchBoxX() + 8, 35, -1);
        }
        if (!this.playerList.isEmpty()) {
            this.playerList.render(matrices, mouseX, mouseY, delta);
        } else if (!this.searchBox.getText().isEmpty()) {
            SocialInteractionsScreen.drawCenteredTextWithShadow(matrices, this.client.textRenderer, EMPTY_SEARCH_TEXT, this.width / 2, (72 + this.getPlayerListBottom()) / 2, -1);
        } else if (this.currentTab == Tab.HIDDEN) {
            SocialInteractionsScreen.drawCenteredTextWithShadow(matrices, this.client.textRenderer, EMPTY_HIDDEN_TEXT, this.width / 2, (72 + this.getPlayerListBottom()) / 2, -1);
        } else if (this.currentTab == Tab.BLOCKED) {
            SocialInteractionsScreen.drawCenteredTextWithShadow(matrices, this.client.textRenderer, EMPTY_BLOCKED_TEXT, this.width / 2, (72 + this.getPlayerListBottom()) / 2, -1);
        }
        this.searchBox.render(matrices, mouseX, mouseY, delta);
        this.blockingButton.visible = this.currentTab == Tab.BLOCKED;
        super.render(matrices, mouseX, mouseY, delta);
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
        if (!this.searchBox.isFocused() && this.client.options.socialInteractionsKey.matchesKey(keyCode, scanCode)) {
            this.client.setScreen(null);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() {
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
            this.serverLabel = i > 1 ? Text.translatable("gui.socialInteractions.server_label.multiple", string, i) : Text.translatable("gui.socialInteractions.server_label.single", string, i);
            this.playerCount = i;
        }
    }

    public void setPlayerOnline(PlayerListEntry player) {
        this.playerList.setPlayerOnline(player, this.currentTab);
    }

    public void setPlayerOffline(UUID uuid) {
        this.playerList.setPlayerOffline(uuid);
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Tab {
        ALL,
        HIDDEN,
        BLOCKED;

    }
}

