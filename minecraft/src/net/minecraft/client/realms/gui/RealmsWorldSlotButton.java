package net.minecraft.client.realms.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsWorldOptions;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.realms.util.RealmsTextureManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class RealmsWorldSlotButton extends ButtonWidget {
	private static final Identifier SLOT_FRAME = new Identifier("widget/slot_frame");
	private static final Identifier CHECKMARK = new Identifier("icon/checkmark");
	public static final Identifier EMPTY_FRAME = new Identifier("textures/gui/realms/empty_frame.png");
	public static final Identifier PANORAMA_0 = new Identifier("minecraft", "textures/gui/title/background/panorama_0.png");
	public static final Identifier PANORAMA_2 = new Identifier("minecraft", "textures/gui/title/background/panorama_2.png");
	public static final Identifier PANORAMA_3 = new Identifier("minecraft", "textures/gui/title/background/panorama_3.png");
	private static final Text ACTIVE_TOOLTIP = Text.translatable("mco.configure.world.slot.tooltip.active");
	private static final Text MINIGAME_TOOLTIP = Text.translatable("mco.configure.world.slot.tooltip.minigame");
	private static final Text TOOLTIP = Text.translatable("mco.configure.world.slot.tooltip");
	static final Text MINIGAME_SLOT_NAME = Text.translatable("mco.worldSlot.minigame");
	private final int slotIndex;
	@Nullable
	private RealmsWorldSlotButton.State state;

	public RealmsWorldSlotButton(int x, int y, int width, int height, int slotIndex, ButtonWidget.PressAction pressAction) {
		super(x, y, width, height, ScreenTexts.EMPTY, pressAction, DEFAULT_NARRATION_SUPPLIER);
		this.slotIndex = slotIndex;
	}

	@Nullable
	public RealmsWorldSlotButton.State getState() {
		return this.state;
	}

	public void setServer(RealmsServer server) {
		this.state = new RealmsWorldSlotButton.State(server, this.slotIndex);
		this.updateTooltip(this.state, server.minigameName);
	}

	private void updateTooltip(RealmsWorldSlotButton.State state, @Nullable String minigameName) {
		Text text = switch (state.action) {
			case SWITCH_SLOT -> state.minigame ? MINIGAME_TOOLTIP : TOOLTIP;
			case JOIN -> ACTIVE_TOOLTIP;
			default -> null;
		};
		if (text != null) {
			this.setTooltip(Tooltip.of(text));
		}

		MutableText mutableText = Text.literal(state.slotName);
		if (state.minigame && minigameName != null) {
			mutableText = mutableText.append(ScreenTexts.SPACE).append(minigameName);
		}

		this.setMessage(mutableText);
	}

	static RealmsWorldSlotButton.Action getAction(RealmsServer server, boolean active, boolean minigame) {
		if (active && !server.expired && server.state != RealmsServer.State.UNINITIALIZED) {
			return RealmsWorldSlotButton.Action.JOIN;
		} else {
			return active || minigame && server.expired ? RealmsWorldSlotButton.Action.NOTHING : RealmsWorldSlotButton.Action.SWITCH_SLOT;
		}
	}

	@Override
	public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		if (this.state != null) {
			int i = this.getX();
			int j = this.getY();
			boolean bl = this.isSelected();
			Identifier identifier;
			if (this.state.minigame) {
				identifier = RealmsTextureManager.getTextureId(String.valueOf(this.state.imageId), this.state.image);
			} else if (this.state.empty) {
				identifier = EMPTY_FRAME;
			} else if (this.state.image != null && this.state.imageId != -1L) {
				identifier = RealmsTextureManager.getTextureId(String.valueOf(this.state.imageId), this.state.image);
			} else if (this.slotIndex == 1) {
				identifier = PANORAMA_0;
			} else if (this.slotIndex == 2) {
				identifier = PANORAMA_2;
			} else if (this.slotIndex == 3) {
				identifier = PANORAMA_3;
			} else {
				identifier = EMPTY_FRAME;
			}

			if (this.state.isCurrentlyActiveSlot) {
				context.setShaderColor(0.56F, 0.56F, 0.56F, 1.0F);
			}

			context.drawTexture(identifier, i + 3, j + 3, 0.0F, 0.0F, 74, 74, 74, 74);
			boolean bl2 = bl && this.state.action != RealmsWorldSlotButton.Action.NOTHING;
			if (bl2) {
				context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			} else if (this.state.isCurrentlyActiveSlot) {
				context.setShaderColor(0.8F, 0.8F, 0.8F, 1.0F);
			} else {
				context.setShaderColor(0.56F, 0.56F, 0.56F, 1.0F);
			}

			context.drawGuiTexture(SLOT_FRAME, i, j, 80, 80);
			context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			if (this.state.isCurrentlyActiveSlot) {
				RenderSystem.enableBlend();
				context.drawGuiTexture(CHECKMARK, i + 67, j + 4, 9, 8);
				RenderSystem.disableBlend();
			}

			TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
			context.drawCenteredTextWithShadow(textRenderer, this.state.slotName, i + 40, j + 66, Colors.WHITE);
			context.drawCenteredTextWithShadow(
				textRenderer, RealmsMainScreen.getVersionText(this.state.version, this.state.compatibility.isCompatible()), i + 40, j + 80 + 2, Colors.WHITE
			);
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum Action {
		NOTHING,
		SWITCH_SLOT,
		JOIN;
	}

	@Environment(EnvType.CLIENT)
	public static class State {
		final boolean isCurrentlyActiveSlot;
		final String slotName;
		final String version;
		final RealmsServer.Compatibility compatibility;
		final long imageId;
		@Nullable
		final String image;
		public final boolean empty;
		public final boolean minigame;
		public final RealmsWorldSlotButton.Action action;

		public State(RealmsServer server, int slot) {
			this.minigame = slot == 4;
			if (this.minigame) {
				this.isCurrentlyActiveSlot = server.isMinigame();
				this.slotName = RealmsWorldSlotButton.MINIGAME_SLOT_NAME.getString();
				this.imageId = (long)server.minigameId;
				this.image = server.minigameImage;
				this.empty = server.minigameId == -1;
				this.version = "";
				this.compatibility = RealmsServer.Compatibility.UNVERIFIABLE;
			} else {
				RealmsWorldOptions realmsWorldOptions = (RealmsWorldOptions)server.slots.get(slot);
				this.isCurrentlyActiveSlot = server.activeSlot == slot && !server.isMinigame();
				this.slotName = realmsWorldOptions.getSlotName(slot);
				this.imageId = realmsWorldOptions.templateId;
				this.image = realmsWorldOptions.templateImage;
				this.empty = realmsWorldOptions.empty;
				this.version = realmsWorldOptions.version;
				this.compatibility = realmsWorldOptions.compatibility;
			}

			this.action = RealmsWorldSlotButton.getAction(server, this.isCurrentlyActiveSlot, this.minigame);
		}
	}
}
