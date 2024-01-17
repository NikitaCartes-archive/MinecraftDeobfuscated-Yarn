package net.minecraft.client.gui.screen.multiplayer;

import com.google.common.collect.ImmutableList;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.report.AbuseReportTypeScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.network.SocialInteractionsManager;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

@Environment(EnvType.CLIENT)
public class SocialInteractionsPlayerListEntry extends ElementListWidget.Entry<SocialInteractionsPlayerListEntry> {
	private static final Identifier DRAFT_REPORT_ICON_TEXTURE = new Identifier("icon/draft_report");
	private static final Duration field_32418 = Duration.ofMillis(500L);
	private static final ButtonTextures REPORT_BUTTON_TEXTURES = new ButtonTextures(
		new Identifier("social_interactions/report_button"),
		new Identifier("social_interactions/report_button_disabled"),
		new Identifier("social_interactions/report_button_highlighted")
	);
	private static final ButtonTextures MUTE_BUTTON_TEXTURES = new ButtonTextures(
		new Identifier("social_interactions/mute_button"), new Identifier("social_interactions/mute_button_highlighted")
	);
	private static final ButtonTextures UNMUTE_BUTTON_TEXTURES = new ButtonTextures(
		new Identifier("social_interactions/unmute_button"), new Identifier("social_interactions/unmute_button_highlighted")
	);
	private final MinecraftClient client;
	private final List<ClickableWidget> buttons;
	private final UUID uuid;
	private final String name;
	private final Supplier<SkinTextures> skinSupplier;
	private boolean offline;
	private boolean sentMessage;
	private final boolean canSendReports;
	private final boolean hasDraftReport;
	private final boolean reportable;
	@Nullable
	private ButtonWidget hideButton;
	@Nullable
	private ButtonWidget showButton;
	@Nullable
	private ButtonWidget reportButton;
	private float timeCounter;
	private static final Text HIDDEN_TEXT = Text.translatable("gui.socialInteractions.status_hidden").formatted(Formatting.ITALIC);
	private static final Text BLOCKED_TEXT = Text.translatable("gui.socialInteractions.status_blocked").formatted(Formatting.ITALIC);
	private static final Text OFFLINE_TEXT = Text.translatable("gui.socialInteractions.status_offline").formatted(Formatting.ITALIC);
	private static final Text HIDDEN_OFFLINE_TEXT = Text.translatable("gui.socialInteractions.status_hidden_offline").formatted(Formatting.ITALIC);
	private static final Text BLOCKED_OFFLINE_TEXT = Text.translatable("gui.socialInteractions.status_blocked_offline").formatted(Formatting.ITALIC);
	private static final Text REPORT_DISABLED_TEXT = Text.translatable("gui.socialInteractions.tooltip.report.disabled");
	private static final Text hideText = Text.translatable("gui.socialInteractions.tooltip.hide");
	private static final Text showText = Text.translatable("gui.socialInteractions.tooltip.show");
	private static final Text reportText = Text.translatable("gui.socialInteractions.tooltip.report");
	private static final int field_32420 = 24;
	private static final int field_32421 = 4;
	public static final int BLACK_COLOR = ColorHelper.Argb.getArgb(190, 0, 0, 0);
	private static final int field_32422 = 20;
	public static final int GRAY_COLOR = ColorHelper.Argb.getArgb(255, 74, 74, 74);
	public static final int DARK_GRAY_COLOR = ColorHelper.Argb.getArgb(255, 48, 48, 48);
	public static final int WHITE_COLOR = ColorHelper.Argb.getArgb(255, 255, 255, 255);
	public static final int LIGHT_GRAY_COLOR = ColorHelper.Argb.getArgb(140, 255, 255, 255);

	public SocialInteractionsPlayerListEntry(
		MinecraftClient client, SocialInteractionsScreen parent, UUID uuid, String name, Supplier<SkinTextures> skinTexture, boolean reportable
	) {
		this.client = client;
		this.uuid = uuid;
		this.name = name;
		this.skinSupplier = skinTexture;
		AbuseReportContext abuseReportContext = client.getAbuseReportContext();
		this.canSendReports = abuseReportContext.getSender().canSendReports();
		this.reportable = reportable;
		this.hasDraftReport = abuseReportContext.draftPlayerUuidEquals(uuid);
		Text text = Text.translatable("gui.socialInteractions.narration.hide", name);
		Text text2 = Text.translatable("gui.socialInteractions.narration.show", name);
		SocialInteractionsManager socialInteractionsManager = client.getSocialInteractionsManager();
		boolean bl = client.getChatRestriction().allowsChat(client.isInSingleplayer());
		boolean bl2 = !client.player.getUuid().equals(uuid);
		if (bl2 && bl && !socialInteractionsManager.isPlayerBlocked(uuid)) {
			this.reportButton = new TexturedButtonWidget(
				0,
				0,
				20,
				20,
				REPORT_BUTTON_TEXTURES,
				button -> abuseReportContext.tryShowDraftScreen(client, parent, () -> client.setScreen(new AbuseReportTypeScreen(parent, abuseReportContext, this)), false),
				Text.translatable("gui.socialInteractions.report")
			) {
				@Override
				protected MutableText getNarrationMessage() {
					return SocialInteractionsPlayerListEntry.this.getNarrationMessage(super.getNarrationMessage());
				}
			};
			this.reportButton.active = this.canSendReports;
			this.reportButton.setTooltip(this.getReportButtonTooltip());
			this.reportButton.setTooltipDelay(field_32418);
			this.hideButton = new TexturedButtonWidget(0, 0, 20, 20, MUTE_BUTTON_TEXTURES, button -> {
				socialInteractionsManager.hidePlayer(uuid);
				this.onButtonClick(true, Text.translatable("gui.socialInteractions.hidden_in_chat", name));
			}, Text.translatable("gui.socialInteractions.hide")) {
				@Override
				protected MutableText getNarrationMessage() {
					return SocialInteractionsPlayerListEntry.this.getNarrationMessage(super.getNarrationMessage());
				}
			};
			this.hideButton.setTooltip(Tooltip.of(hideText, text));
			this.hideButton.setTooltipDelay(field_32418);
			this.showButton = new TexturedButtonWidget(0, 0, 20, 20, UNMUTE_BUTTON_TEXTURES, button -> {
				socialInteractionsManager.showPlayer(uuid);
				this.onButtonClick(false, Text.translatable("gui.socialInteractions.shown_in_chat", name));
			}, Text.translatable("gui.socialInteractions.show")) {
				@Override
				protected MutableText getNarrationMessage() {
					return SocialInteractionsPlayerListEntry.this.getNarrationMessage(super.getNarrationMessage());
				}
			};
			this.showButton.setTooltip(Tooltip.of(showText, text2));
			this.showButton.setTooltipDelay(field_32418);
			this.buttons = new ArrayList();
			this.buttons.add(this.hideButton);
			this.buttons.add(this.reportButton);
			this.setShowButtonVisible(socialInteractionsManager.isPlayerHidden(this.uuid));
		} else {
			this.buttons = ImmutableList.of();
		}
	}

	private Tooltip getReportButtonTooltip() {
		return !this.canSendReports
			? Tooltip.of(REPORT_DISABLED_TEXT)
			: Tooltip.of(reportText, Text.translatable("gui.socialInteractions.narration.report", this.name));
	}

	@Override
	public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
		int i = x + 4;
		int j = y + (entryHeight - 24) / 2;
		int k = i + 24 + 4;
		Text text = this.getStatusText();
		int l;
		if (text == ScreenTexts.EMPTY) {
			context.fill(x, y, x + entryWidth, y + entryHeight, GRAY_COLOR);
			l = y + (entryHeight - 9) / 2;
		} else {
			context.fill(x, y, x + entryWidth, y + entryHeight, DARK_GRAY_COLOR);
			l = y + (entryHeight - (9 + 9)) / 2;
			context.drawText(this.client.textRenderer, text, k, l + 12, LIGHT_GRAY_COLOR, false);
		}

		PlayerSkinDrawer.draw(context, (SkinTextures)this.skinSupplier.get(), i, j, 24);
		context.drawText(this.client.textRenderer, this.name, k, l, WHITE_COLOR, false);
		if (this.offline) {
			context.fill(i, j, i + 24, j + 24, BLACK_COLOR);
		}

		if (this.hideButton != null && this.showButton != null && this.reportButton != null) {
			float f = this.timeCounter;
			this.hideButton.setX(x + (entryWidth - this.hideButton.getWidth() - 4) - 20 - 4);
			this.hideButton.setY(y + (entryHeight - this.hideButton.getHeight()) / 2);
			this.hideButton.render(context, mouseX, mouseY, tickDelta);
			this.showButton.setX(x + (entryWidth - this.showButton.getWidth() - 4) - 20 - 4);
			this.showButton.setY(y + (entryHeight - this.showButton.getHeight()) / 2);
			this.showButton.render(context, mouseX, mouseY, tickDelta);
			this.reportButton.setX(x + (entryWidth - this.showButton.getWidth() - 4));
			this.reportButton.setY(y + (entryHeight - this.showButton.getHeight()) / 2);
			this.reportButton.render(context, mouseX, mouseY, tickDelta);
			if (f == this.timeCounter) {
				this.timeCounter = 0.0F;
			}
		}

		if (this.hasDraftReport && this.reportButton != null) {
			context.drawGuiTexture(DRAFT_REPORT_ICON_TEXTURE, this.reportButton.getX() + 5, this.reportButton.getY() + 1, 15, 15);
		}
	}

	@Override
	public List<? extends Element> children() {
		return this.buttons;
	}

	@Override
	public List<? extends Selectable> selectableChildren() {
		return this.buttons;
	}

	public String getName() {
		return this.name;
	}

	public UUID getUuid() {
		return this.uuid;
	}

	public Supplier<SkinTextures> getSkinSupplier() {
		return this.skinSupplier;
	}

	public void setOffline(boolean offline) {
		this.offline = offline;
	}

	public boolean isOffline() {
		return this.offline;
	}

	public void setSentMessage(boolean sentMessage) {
		this.sentMessage = sentMessage;
	}

	public boolean hasSentMessage() {
		return this.sentMessage;
	}

	public boolean isReportable() {
		return this.reportable;
	}

	private void onButtonClick(boolean showButtonVisible, Text chatMessage) {
		this.setShowButtonVisible(showButtonVisible);
		this.client.inGameHud.getChatHud().addMessage(chatMessage);
		this.client.getNarratorManager().narrate(chatMessage);
	}

	private void setShowButtonVisible(boolean showButtonVisible) {
		this.showButton.visible = showButtonVisible;
		this.hideButton.visible = !showButtonVisible;
		this.buttons.set(0, showButtonVisible ? this.showButton : this.hideButton);
	}

	MutableText getNarrationMessage(MutableText text) {
		Text text2 = this.getStatusText();
		return text2 == ScreenTexts.EMPTY
			? Text.literal(this.name).append(", ").append(text)
			: Text.literal(this.name).append(", ").append(text2).append(", ").append(text);
	}

	private Text getStatusText() {
		boolean bl = this.client.getSocialInteractionsManager().isPlayerHidden(this.uuid);
		boolean bl2 = this.client.getSocialInteractionsManager().isPlayerBlocked(this.uuid);
		if (bl2 && this.offline) {
			return BLOCKED_OFFLINE_TEXT;
		} else if (bl && this.offline) {
			return HIDDEN_OFFLINE_TEXT;
		} else if (bl2) {
			return BLOCKED_TEXT;
		} else if (bl) {
			return HIDDEN_TEXT;
		} else {
			return this.offline ? OFFLINE_TEXT : ScreenTexts.EMPTY;
		}
	}
}
