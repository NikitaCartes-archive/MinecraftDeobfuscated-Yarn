package net.minecraft.client.gui.screen.multiplayer;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.hud.BackgroundHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.network.SocialInteractionsManager;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SocialInteractionsPlayerListEntry extends ElementListWidget.Entry<SocialInteractionsPlayerListEntry> {
	private static final int field_32418 = 10;
	private static final int field_32419 = 150;
	private final MinecraftClient client;
	private final List<ClickableWidget> buttons;
	private final UUID uuid;
	private final String name;
	private final Supplier<Identifier> skinTexture;
	private boolean offline;
	@Nullable
	private ButtonWidget hideButton;
	@Nullable
	private ButtonWidget showButton;
	final Text hideText;
	final Text showText;
	final List<OrderedText> hideTooltip;
	final List<OrderedText> showTooltip;
	float timeCounter;
	private static final Text HIDDEN_TEXT = new TranslatableText("gui.socialInteractions.status_hidden").formatted(Formatting.ITALIC);
	private static final Text BLOCKED_TEXT = new TranslatableText("gui.socialInteractions.status_blocked").formatted(Formatting.ITALIC);
	private static final Text OFFLINE_TEXT = new TranslatableText("gui.socialInteractions.status_offline").formatted(Formatting.ITALIC);
	private static final Text HIDDEN_OFFLINE_TEXT = new TranslatableText("gui.socialInteractions.status_hidden_offline").formatted(Formatting.ITALIC);
	private static final Text BLOCKED_OFFLINE_TEXT = new TranslatableText("gui.socialInteractions.status_blocked_offline").formatted(Formatting.ITALIC);
	private static final int field_32420 = 24;
	private static final int field_32421 = 4;
	private static final int field_32422 = 20;
	private static final int field_32416 = 0;
	private static final int field_32417 = 38;
	public static final int BLACK_COLOR = BackgroundHelper.ColorMixer.getArgb(190, 0, 0, 0);
	public static final int GRAY_COLOR = BackgroundHelper.ColorMixer.getArgb(255, 74, 74, 74);
	public static final int DARK_GRAY_COLOR = BackgroundHelper.ColorMixer.getArgb(255, 48, 48, 48);
	public static final int WHITE_COLOR = BackgroundHelper.ColorMixer.getArgb(255, 255, 255, 255);
	public static final int LIGHT_GRAY_COLOR = BackgroundHelper.ColorMixer.getArgb(140, 255, 255, 255);

	public SocialInteractionsPlayerListEntry(MinecraftClient client, SocialInteractionsScreen parent, UUID uuid, String name, Supplier<Identifier> skinTexture) {
		this.client = client;
		this.uuid = uuid;
		this.name = name;
		this.skinTexture = skinTexture;
		this.hideText = new TranslatableText("gui.socialInteractions.tooltip.hide", name);
		this.showText = new TranslatableText("gui.socialInteractions.tooltip.show", name);
		this.hideTooltip = client.textRenderer.wrapLines(this.hideText, 150);
		this.showTooltip = client.textRenderer.wrapLines(this.showText, 150);
		SocialInteractionsManager socialInteractionsManager = client.getSocialInteractionsManager();
		if (!client.player.getGameProfile().getId().equals(uuid) && !socialInteractionsManager.isPlayerBlocked(uuid)) {
			this.hideButton = new TexturedButtonWidget(
				0,
				0,
				20,
				20,
				0,
				38,
				20,
				SocialInteractionsScreen.SOCIAL_INTERACTIONS_TEXTURE,
				256,
				256,
				button -> {
					socialInteractionsManager.hidePlayer(uuid);
					this.onButtonClick(true, new TranslatableText("gui.socialInteractions.hidden_in_chat", name));
				},
				new ButtonWidget.TooltipSupplier() {
					@Override
					public void onTooltip(ButtonWidget buttonWidget, MatrixStack matrixStack, int i, int j) {
						SocialInteractionsPlayerListEntry.this.timeCounter = SocialInteractionsPlayerListEntry.this.timeCounter + client.getLastFrameDuration();
						if (SocialInteractionsPlayerListEntry.this.timeCounter >= 10.0F) {
							parent.setOnRendered(
								() -> SocialInteractionsPlayerListEntry.renderTooltip(parent, matrixStack, SocialInteractionsPlayerListEntry.this.hideTooltip, i, j)
							);
						}
					}

					@Override
					public void supply(Consumer<Text> consumer) {
						consumer.accept(SocialInteractionsPlayerListEntry.this.hideText);
					}
				},
				new TranslatableText("gui.socialInteractions.hide")
			) {
				@Override
				protected MutableText getNarrationMessage() {
					return SocialInteractionsPlayerListEntry.this.getNarrationMessage(super.getNarrationMessage());
				}
			};
			this.showButton = new TexturedButtonWidget(
				0,
				0,
				20,
				20,
				20,
				38,
				20,
				SocialInteractionsScreen.SOCIAL_INTERACTIONS_TEXTURE,
				256,
				256,
				button -> {
					socialInteractionsManager.showPlayer(uuid);
					this.onButtonClick(false, new TranslatableText("gui.socialInteractions.shown_in_chat", name));
				},
				new ButtonWidget.TooltipSupplier() {
					@Override
					public void onTooltip(ButtonWidget buttonWidget, MatrixStack matrixStack, int i, int j) {
						SocialInteractionsPlayerListEntry.this.timeCounter = SocialInteractionsPlayerListEntry.this.timeCounter + client.getLastFrameDuration();
						if (SocialInteractionsPlayerListEntry.this.timeCounter >= 10.0F) {
							parent.setOnRendered(
								() -> SocialInteractionsPlayerListEntry.renderTooltip(parent, matrixStack, SocialInteractionsPlayerListEntry.this.showTooltip, i, j)
							);
						}
					}

					@Override
					public void supply(Consumer<Text> consumer) {
						consumer.accept(SocialInteractionsPlayerListEntry.this.showText);
					}
				},
				new TranslatableText("gui.socialInteractions.show")
			) {
				@Override
				protected MutableText getNarrationMessage() {
					return SocialInteractionsPlayerListEntry.this.getNarrationMessage(super.getNarrationMessage());
				}
			};
			this.showButton.visible = socialInteractionsManager.isPlayerHidden(uuid);
			this.hideButton.visible = !this.showButton.visible;
			this.buttons = ImmutableList.of(this.hideButton, this.showButton);
		} else {
			this.buttons = ImmutableList.of();
		}
	}

	@Override
	public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
		int i = x + 4;
		int j = y + (entryHeight - 24) / 2;
		int k = i + 24 + 4;
		Text text = this.getStatusText();
		int l;
		if (text == LiteralText.EMPTY) {
			DrawableHelper.fill(matrices, x, y, x + entryWidth, y + entryHeight, GRAY_COLOR);
			l = y + (entryHeight - 9) / 2;
		} else {
			DrawableHelper.fill(matrices, x, y, x + entryWidth, y + entryHeight, DARK_GRAY_COLOR);
			l = y + (entryHeight - (9 + 9)) / 2;
			this.client.textRenderer.draw(matrices, text, (float)k, (float)(l + 12), LIGHT_GRAY_COLOR);
		}

		RenderSystem.setShaderTexture(0, (Identifier)this.skinTexture.get());
		DrawableHelper.drawTexture(matrices, i, j, 24, 24, 8.0F, 8.0F, 8, 8, 64, 64);
		RenderSystem.enableBlend();
		DrawableHelper.drawTexture(matrices, i, j, 24, 24, 40.0F, 8.0F, 8, 8, 64, 64);
		RenderSystem.disableBlend();
		this.client.textRenderer.draw(matrices, this.name, (float)k, (float)l, WHITE_COLOR);
		if (this.offline) {
			DrawableHelper.fill(matrices, i, j, i + 24, j + 24, BLACK_COLOR);
		}

		if (this.hideButton != null && this.showButton != null) {
			float f = this.timeCounter;
			this.hideButton.x = x + (entryWidth - this.hideButton.getWidth() - 4);
			this.hideButton.y = y + (entryHeight - this.hideButton.getHeight()) / 2;
			this.hideButton.render(matrices, mouseX, mouseY, tickDelta);
			this.showButton.x = x + (entryWidth - this.showButton.getWidth() - 4);
			this.showButton.y = y + (entryHeight - this.showButton.getHeight()) / 2;
			this.showButton.render(matrices, mouseX, mouseY, tickDelta);
			if (f == this.timeCounter) {
				this.timeCounter = 0.0F;
			}
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

	public void setOffline(boolean offline) {
		this.offline = offline;
	}

	private void onButtonClick(boolean showButtonVisible, Text chatMessage) {
		this.showButton.visible = showButtonVisible;
		this.hideButton.visible = !showButtonVisible;
		this.client.inGameHud.getChatHud().addMessage(chatMessage);
		NarratorManager.INSTANCE.narrate(chatMessage);
	}

	MutableText getNarrationMessage(MutableText text) {
		Text text2 = this.getStatusText();
		return text2 == LiteralText.EMPTY
			? new LiteralText(this.name).append(", ").append(text)
			: new LiteralText(this.name).append(", ").append(text2).append(", ").append(text);
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
			return this.offline ? OFFLINE_TEXT : LiteralText.EMPTY;
		}
	}

	static void renderTooltip(SocialInteractionsScreen screen, MatrixStack matrices, List<OrderedText> tooltip, int mouseX, int mouseY) {
		screen.renderOrderedTooltip(matrices, tooltip, mouseX, mouseY);
		screen.setOnRendered(null);
	}
}
