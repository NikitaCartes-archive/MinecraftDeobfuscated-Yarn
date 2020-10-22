package net.minecraft.client.gui.screen.multiplayer;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.hud.BackgroundHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
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
	private final MinecraftClient client;
	private final List<Element> buttons;
	private final UUID uuid;
	private final String name;
	private final Supplier<Identifier> field_26904;
	private boolean field_26859;
	@Nullable
	private ButtonWidget hideButton;
	@Nullable
	private ButtonWidget showButton;
	private final List<OrderedText> hideTooltip;
	private final List<OrderedText> showTooltip;
	private float field_26864;
	private static final Text field_26905 = new TranslatableText("gui.socialInteractions.status_hidden").formatted(Formatting.ITALIC);
	private static final Text field_26906 = new TranslatableText("gui.socialInteractions.status_blocked").formatted(Formatting.ITALIC);
	private static final Text field_26907 = new TranslatableText("gui.socialInteractions.status_offline").formatted(Formatting.ITALIC);
	private static final Text field_26908 = new TranslatableText("gui.socialInteractions.status_hidden_offline").formatted(Formatting.ITALIC);
	private static final Text field_26909 = new TranslatableText("gui.socialInteractions.status_blocked_offline").formatted(Formatting.ITALIC);
	public static final int field_26850 = BackgroundHelper.ColorMixer.getArgb(190, 0, 0, 0);
	public static final int field_26851 = BackgroundHelper.ColorMixer.getArgb(255, 74, 74, 74);
	public static final int field_26852 = BackgroundHelper.ColorMixer.getArgb(255, 48, 48, 48);
	public static final int field_26853 = BackgroundHelper.ColorMixer.getArgb(255, 255, 255, 255);
	public static final int field_26903 = BackgroundHelper.ColorMixer.getArgb(140, 255, 255, 255);

	public SocialInteractionsPlayerListEntry(MinecraftClient client, SocialInteractionsScreen parent, UUID uuid, String name, Supplier<Identifier> supplier) {
		this.client = client;
		this.uuid = uuid;
		this.name = name;
		this.field_26904 = supplier;
		this.hideTooltip = client.textRenderer.wrapLines(new TranslatableText("gui.socialInteractions.tooltip.hide", name), 150);
		this.showTooltip = client.textRenderer.wrapLines(new TranslatableText("gui.socialInteractions.tooltip.show", name), 150);
		SocialInteractionsManager socialInteractionsManager = client.getSocialInteractionsManager();
		if (!client.player.getGameProfile().getId().equals(uuid) && !socialInteractionsManager.method_31392(uuid)) {
			this.hideButton = new TexturedButtonWidget(0, 0, 20, 20, 0, 38, 20, SocialInteractionsScreen.SOCIAL_INTERACTIONS_TEXTURE, 256, 256, buttonWidget -> {
				socialInteractionsManager.hidePlayer(uuid);
				this.method_31329(true, new TranslatableText("gui.socialInteractions.hidden_in_chat", name));
			}, (buttonWidget, matrixStack, i, j) -> {
				this.field_26864 = this.field_26864 + client.getLastFrameDuration();
				if (this.field_26864 >= 10.0F) {
					parent.method_31354(() -> method_31328(parent, matrixStack, this.hideTooltip, i, j));
				}
			}, new TranslatableText("gui.socialInteractions.hide")) {
				@Override
				protected MutableText getNarrationMessage() {
					return SocialInteractionsPlayerListEntry.this.method_31389(super.getNarrationMessage());
				}
			};
			this.showButton = new TexturedButtonWidget(0, 0, 20, 20, 20, 38, 20, SocialInteractionsScreen.SOCIAL_INTERACTIONS_TEXTURE, 256, 256, buttonWidget -> {
				socialInteractionsManager.showPlayer(uuid);
				this.method_31329(false, new TranslatableText("gui.socialInteractions.shown_in_chat", name));
			}, (buttonWidget, matrixStack, i, j) -> {
				this.field_26864 = this.field_26864 + client.getLastFrameDuration();
				if (this.field_26864 >= 10.0F) {
					parent.method_31354(() -> method_31328(parent, matrixStack, this.showTooltip, i, j));
				}
			}, new TranslatableText("gui.socialInteractions.show")) {
				@Override
				protected MutableText getNarrationMessage() {
					return SocialInteractionsPlayerListEntry.this.method_31389(super.getNarrationMessage());
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
		Text text = this.method_31390();
		int l;
		if (text == LiteralText.EMPTY) {
			DrawableHelper.fill(matrices, x, y, x + entryWidth, y + entryHeight, field_26851);
			l = y + (entryHeight - 9) / 2;
		} else {
			DrawableHelper.fill(matrices, x, y, x + entryWidth, y + entryHeight, field_26852);
			l = y + (entryHeight - (9 + 9)) / 2;
			this.client.textRenderer.draw(matrices, text, (float)k, (float)(l + 12), field_26903);
		}

		this.client.getTextureManager().bindTexture((Identifier)this.field_26904.get());
		DrawableHelper.drawTexture(matrices, i, j, 24, 24, 8.0F, 8.0F, 8, 8, 64, 64);
		RenderSystem.enableBlend();
		DrawableHelper.drawTexture(matrices, i, j, 24, 24, 40.0F, 8.0F, 8, 8, 64, 64);
		RenderSystem.disableBlend();
		this.client.textRenderer.draw(matrices, this.name, (float)k, (float)l, field_26853);
		if (this.field_26859) {
			DrawableHelper.fill(matrices, i, j, i + 24, j + 24, field_26850);
		}

		if (this.hideButton != null && this.showButton != null) {
			float f = this.field_26864;
			this.hideButton.x = x + (entryWidth - this.hideButton.getWidth() - 4);
			this.hideButton.y = y + (entryHeight - this.hideButton.getHeight()) / 2;
			this.hideButton.render(matrices, mouseX, mouseY, tickDelta);
			this.showButton.x = x + (entryWidth - this.showButton.getWidth() - 4);
			this.showButton.y = y + (entryHeight - this.showButton.getHeight()) / 2;
			this.showButton.render(matrices, mouseX, mouseY, tickDelta);
			if (f == this.field_26864) {
				this.field_26864 = 0.0F;
			}
		}
	}

	@Override
	public List<? extends Element> children() {
		return this.buttons;
	}

	public String getName() {
		return this.name;
	}

	public UUID getUuid() {
		return this.uuid;
	}

	public void method_31335(boolean bl) {
		this.field_26859 = bl;
	}

	private void method_31329(boolean bl, Text text) {
		this.showButton.visible = bl;
		this.hideButton.visible = !bl;
		this.client.inGameHud.getChatHud().addMessage(text);
		NarratorManager.INSTANCE.narrate(text.getString());
	}

	private MutableText method_31389(MutableText mutableText) {
		Text text = this.method_31390();
		return text == LiteralText.EMPTY
			? new LiteralText(this.name).append(", ").append(mutableText)
			: new LiteralText(this.name).append(", ").append(text).append(", ").append(mutableText);
	}

	private Text method_31390() {
		boolean bl = this.client.getSocialInteractionsManager().isPlayerHidden(this.uuid);
		boolean bl2 = this.client.getSocialInteractionsManager().method_31392(this.uuid);
		if (bl2 && this.field_26859) {
			return field_26909;
		} else if (bl && this.field_26859) {
			return field_26908;
		} else if (bl2) {
			return field_26906;
		} else if (bl) {
			return field_26905;
		} else {
			return this.field_26859 ? field_26907 : LiteralText.EMPTY;
		}
	}

	private static void method_31328(SocialInteractionsScreen socialInteractionsScreen, MatrixStack matrixStack, List<OrderedText> list, int i, int j) {
		socialInteractionsScreen.renderOrderedTooltip(matrixStack, list, i, j);
		socialInteractionsScreen.method_31354(null);
	}
}
