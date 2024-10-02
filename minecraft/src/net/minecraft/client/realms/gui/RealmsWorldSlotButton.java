package net.minecraft.client.realms.gui;

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
import net.minecraft.client.render.RenderLayer;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

@Environment(EnvType.CLIENT)
public class RealmsWorldSlotButton extends ButtonWidget {
	private static final Identifier SLOT_FRAME = Identifier.ofVanilla("widget/slot_frame");
	private static final Identifier CHECKMARK = Identifier.ofVanilla("icon/checkmark");
	public static final Identifier EMPTY_FRAME = Identifier.ofVanilla("textures/gui/realms/empty_frame.png");
	public static final Identifier PANORAMA_0 = Identifier.ofVanilla("textures/gui/title/background/panorama_0.png");
	public static final Identifier PANORAMA_2 = Identifier.ofVanilla("textures/gui/title/background/panorama_2.png");
	public static final Identifier PANORAMA_3 = Identifier.ofVanilla("textures/gui/title/background/panorama_3.png");
	private static final Text ACTIVE_TOOLTIP = Text.translatable("mco.configure.world.slot.tooltip.active");
	private static final Text MINIGAME_TOOLTIP = Text.translatable("mco.configure.world.slot.tooltip.minigame");
	private static final Text TOOLTIP = Text.translatable("mco.configure.world.slot.tooltip");
	static final Text MINIGAME_SLOT_NAME = Text.translatable("mco.worldSlot.minigame");
	private static final int MAX_DISPLAYED_SLOT_NAME_LENGTH = 64;
	private static final String ELLIPSIS = "...";
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

			int k = -1;
			if (this.state.isCurrentlyActiveSlot) {
				k = ColorHelper.fromFloats(1.0F, 0.56F, 0.56F, 0.56F);
			}

			context.drawTexture(RenderLayer::getGuiTextured, identifier, i + 3, j + 3, 0.0F, 0.0F, 74, 74, 74, 74, 74, 74, k);
			if (bl && this.state.action != RealmsWorldSlotButton.Action.NOTHING) {
				context.drawGuiTexture(RenderLayer::getGuiTextured, SLOT_FRAME, i, j, 80, 80);
			} else if (this.state.isCurrentlyActiveSlot) {
				context.drawGuiTexture(RenderLayer::getGuiTextured, SLOT_FRAME, i, j, 80, 80, ColorHelper.fromFloats(1.0F, 0.8F, 0.8F, 0.8F));
			} else {
				context.drawGuiTexture(RenderLayer::getGuiTextured, SLOT_FRAME, i, j, 80, 80, ColorHelper.fromFloats(1.0F, 0.56F, 0.56F, 0.56F));
			}

			if (this.state.isCurrentlyActiveSlot) {
				context.drawGuiTexture(RenderLayer::getGuiTextured, CHECKMARK, i + 67, j + 4, 9, 8);
			}

			if (this.state.hardcore) {
				context.drawGuiTexture(RenderLayer::getGuiTextured, RealmsMainScreen.HARDCORE_ICON_TEXTURE, i + 3, j + 4, 9, 8);
			}

			TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
			String string = this.state.slotName;
			if (textRenderer.getWidth(string) > 64) {
				string = textRenderer.trimToWidth(string, 64 - textRenderer.getWidth("...")) + "...";
			}

			context.drawCenteredTextWithShadow(textRenderer, string, i + 40, j + 66, Colors.WHITE);
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
		public final boolean hardcore;

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
				this.hardcore = false;
			} else {
				RealmsWorldOptions realmsWorldOptions = (RealmsWorldOptions)server.slots.get(slot);
				this.isCurrentlyActiveSlot = server.activeSlot == slot && !server.isMinigame();
				this.slotName = realmsWorldOptions.getSlotName(slot);
				this.imageId = realmsWorldOptions.templateId;
				this.image = realmsWorldOptions.templateImage;
				this.empty = realmsWorldOptions.empty;
				this.version = realmsWorldOptions.version;
				this.compatibility = realmsWorldOptions.compatibility;
				this.hardcore = realmsWorldOptions.hardcore;
			}

			this.action = RealmsWorldSlotButton.getAction(server, this.isCurrentlyActiveSlot, this.minigame);
		}
	}
}
