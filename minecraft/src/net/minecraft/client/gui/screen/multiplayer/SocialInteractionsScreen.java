package net.minecraft.client.gui.screen.multiplayer;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Locale;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SocialInteractionsScreen extends Screen {
	protected static final Identifier SOCIAL_INTERACTIONS_TEXTURE = new Identifier("textures/gui/social_interactions.png");
	private static final Text ALL_TAB_TITLE = new TranslatableText("gui.socialInteractions.tab_all");
	private static final Text HIDDEN_TAB_TITLE = new TranslatableText("gui.socialInteractions.tab_hidden");
	private static final Text SELECTED_ALL_TAB_TITLE = ALL_TAB_TITLE.copy().formatted(Formatting.GRAY);
	private static final Text SELECTED_HIDDEN_TAB_TITLE = HIDDEN_TAB_TITLE.copy().formatted(Formatting.GRAY);
	private static final Text SEARCH_TEXT = new TranslatableText("gui.socialInteractions.search_hint").formatted(Formatting.ITALIC).formatted(Formatting.GRAY);
	private static final Text EMPTY_HIDDEN_TEXT = new TranslatableText("gui.socialInteractions.empty_hidden").formatted(Formatting.GRAY);
	private SocialInteractionsPlayerListWidget playerList;
	private TextFieldWidget searchBox;
	private String currentSearch = "";
	private SocialInteractionsScreen.Tab currentTab = SocialInteractionsScreen.Tab.ALL;
	private ButtonWidget allTabButton;
	private ButtonWidget hiddenTabButton;
	@Nullable
	private Text serverLabel;
	private int playerCount;
	private boolean field_26872;
	private boolean field_26873;
	@Nullable
	private Runnable field_26874;

	public SocialInteractionsScreen() {
		super(new TranslatableText("gui.socialInteractions.title"));
		this.method_31350(MinecraftClient.getInstance());
	}

	private int method_31359() {
		return Math.max(0, this.height - 128 - 16);
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
	protected void init() {
		this.client.keyboard.setRepeatEvents(true);
		if (this.field_26873) {
			this.playerList.updateSize(this.width, this.height, 88, this.method_31361());
		} else {
			this.playerList = new SocialInteractionsPlayerListWidget(this, this.client, this.width, this.height, 88, this.method_31361(), 36);
		}

		this.allTabButton = this.addButton(
			new ButtonWidget(
				this.playerList.getRowLeft() + this.playerList.getRowWidth() / 4 - 30,
				45,
				60,
				20,
				ALL_TAB_TITLE,
				buttonWidget -> this.setCurrentTab(SocialInteractionsScreen.Tab.ALL)
			)
		);
		this.hiddenTabButton = this.addButton(
			new ButtonWidget(
				this.playerList.getRowLeft() + this.playerList.getRowWidth() / 4 * 3 - 30,
				45,
				60,
				20,
				HIDDEN_TAB_TITLE,
				buttonWidget -> this.setCurrentTab(SocialInteractionsScreen.Tab.HIDDEN)
			)
		);
		String string = this.searchBox != null ? this.searchBox.getText() : "";
		this.searchBox = new TextFieldWidget(this.textRenderer, this.method_31362() + 28, 78, 220, 16, SEARCH_TEXT);
		this.searchBox.setMaxLength(50);
		this.searchBox.setHasBorder(false);
		this.searchBox.setVisible(true);
		this.searchBox.setEditableColor(16777215);
		this.searchBox.setText(string);
		this.searchBox.setChangedListener(this::onSearchChange);
		this.children.add(this.searchBox);
		this.children.add(this.playerList);
		this.field_26873 = true;
		this.setCurrentTab(this.currentTab);
	}

	private void setCurrentTab(SocialInteractionsScreen.Tab currentTab) {
		this.currentTab = currentTab;
		this.allTabButton.setMessage(ALL_TAB_TITLE);
		this.hiddenTabButton.setMessage(HIDDEN_TAB_TITLE);
		Collection<UUID> collection;
		switch (currentTab) {
			case ALL:
				this.allTabButton.setMessage(SELECTED_ALL_TAB_TITLE);
				collection = this.client.player.networkHandler.getPlayerUuids();
				break;
			case HIDDEN:
				this.hiddenTabButton.setMessage(SELECTED_HIDDEN_TAB_TITLE);
				collection = this.client.getSocialInteractionsManager().getHiddenPlayers();
				this.field_26872 = false;
				if (collection.isEmpty()) {
					NarratorManager.INSTANCE.narrate(EMPTY_HIDDEN_TEXT.getString());
				}
				break;
			default:
				collection = ImmutableList.<UUID>of();
		}

		this.currentTab = currentTab;
		this.playerList.method_31344(currentTab, collection, this.playerList.getScrollAmount());
	}

	@Override
	public void removed() {
		this.client.keyboard.setRepeatEvents(false);
	}

	@Override
	public void renderBackground(MatrixStack matrices) {
		int i = this.method_31362() + 3;
		super.renderBackground(matrices);
		this.client.getTextureManager().bindTexture(SOCIAL_INTERACTIONS_TEXTURE);
		this.drawTexture(matrices, i, 64, 1, 1, 236, 8);
		int j = this.method_31360();

		for (int k = 0; k < j; k++) {
			this.drawTexture(matrices, i, 72 + 16 * k, 1, 10, 236, 16);
		}

		this.drawTexture(matrices, i, 72 + 16 * j, 1, 27, 236, 8);
		this.drawTexture(matrices, i + 10, 76, 243, 1, 12, 12);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.method_31350(this.client);
		this.renderBackground(matrices);
		if (this.serverLabel != null) {
			drawTextWithShadow(matrices, this.client.textRenderer, this.serverLabel, this.method_31362() + 8, 35, -1);
		}

		if (!this.playerList.isEmpty()) {
			this.playerList.render(matrices, mouseX, mouseY, delta);
		} else if (this.currentTab == SocialInteractionsScreen.Tab.HIDDEN) {
			drawCenteredText(matrices, this.client.textRenderer, EMPTY_HIDDEN_TEXT, this.width / 2, (78 + this.method_31361()) / 2, -1);
		}

		if (!this.searchBox.isFocused() && this.searchBox.getText().isEmpty()) {
			drawTextWithShadow(matrices, this.client.textRenderer, SEARCH_TEXT, this.searchBox.x, this.searchBox.y, -1);
		} else {
			this.searchBox.render(matrices, mouseX, mouseY, delta);
		}

		super.render(matrices, mouseX, mouseY, delta);
		if (this.field_26872) {
			this.client.getTextureManager().bindTexture(SOCIAL_INTERACTIONS_TEXTURE);
			this.drawTexture(matrices, this.hiddenTabButton.x + this.hiddenTabButton.getWidth() - 8, 44, 249, 14, 6, 22);
		}

		if (this.field_26874 != null) {
			this.field_26874.run();
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		return super.mouseClicked(mouseX, mouseY, button) || this.playerList.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (!this.searchBox.isFocused() && this.client.options.keySocialInteractions.matchesKey(keyCode, scanCode)) {
			this.client.openScreen(null);
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	private void onSearchChange(String currentSearch) {
		currentSearch = currentSearch.toLowerCase(Locale.ROOT);
		if (!currentSearch.equals(this.currentSearch)) {
			this.playerList.setCurrentSearch(currentSearch);
			this.currentSearch = currentSearch;
			this.setCurrentTab(this.currentTab);
		}
	}

	private void method_31350(MinecraftClient minecraftClient) {
		int i = minecraftClient.getNetworkHandler().getPlayerList().size();
		if (this.playerCount != i) {
			String string = "";
			ServerInfo serverInfo = minecraftClient.getCurrentServerEntry();
			if (serverInfo != null) {
				string = serverInfo.name;
			} else if (minecraftClient.isInSingleplayer()) {
				string = minecraftClient.getServer().getServerMotd();
			}

			this.serverLabel = new TranslatableText("gui.socialInteractions.server_label", string, i);
			this.playerCount = i;
		}
	}

	public void method_31358() {
		this.field_26872 = this.currentTab != SocialInteractionsScreen.Tab.HIDDEN && this.client.getSocialInteractionsManager().getHiddenPlayers().size() > 0;
	}

	public void method_31353(PlayerListEntry playerListEntry) {
		this.playerList.method_31345(playerListEntry, this.currentTab);
	}

	public void method_31355(UUID uUID) {
		this.playerList.method_31347(uUID);
	}

	public void method_31354(@Nullable Runnable runnable) {
		this.field_26874 = runnable;
	}

	@Environment(EnvType.CLIENT)
	public static enum Tab {
		ALL,
		HIDDEN;
	}
}
