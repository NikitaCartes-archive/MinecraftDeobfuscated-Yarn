package com.mojang.realmsclient.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.util.RealmsTextureManager;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.realms.TickableRealmsButton;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class RealmsWorldSlotButton extends ButtonWidget implements TickableRealmsButton {
	public static final Identifier field_22681 = new Identifier("realms", "textures/gui/realms/slot_frame.png");
	public static final Identifier field_22682 = new Identifier("realms", "textures/gui/realms/empty_frame.png");
	public static final Identifier field_22683 = new Identifier("minecraft", "textures/gui/title/background/panorama_0.png");
	public static final Identifier field_22684 = new Identifier("minecraft", "textures/gui/title/background/panorama_2.png");
	public static final Identifier field_22685 = new Identifier("minecraft", "textures/gui/title/background/panorama_3.png");
	private final Supplier<RealmsServer> serverDataProvider;
	private final Consumer<String> toolTipSetter;
	private final int slotIndex;
	private int animTick;
	@Nullable
	private RealmsWorldSlotButton.State state;

	public RealmsWorldSlotButton(
		int x, int y, int width, int height, Supplier<RealmsServer> serverDataProvider, Consumer<String> toolTipSetter, int id, ButtonWidget.PressAction pressAction
	) {
		super(x, y, width, height, "", pressAction);
		this.serverDataProvider = serverDataProvider;
		this.slotIndex = id;
		this.toolTipSetter = toolTipSetter;
	}

	@Nullable
	public RealmsWorldSlotButton.State method_25099() {
		return this.state;
	}

	@Override
	public void tick() {
		this.animTick++;
		RealmsServer realmsServer = (RealmsServer)this.serverDataProvider.get();
		if (realmsServer != null) {
			RealmsWorldOptions realmsWorldOptions = (RealmsWorldOptions)realmsServer.slots.get(this.slotIndex);
			boolean bl = this.slotIndex == 4;
			boolean bl2;
			String string;
			long l;
			String string2;
			boolean bl3;
			if (bl) {
				bl2 = realmsServer.worldType == RealmsServer.WorldType.MINIGAME;
				string = "Minigame";
				l = (long)realmsServer.minigameId;
				string2 = realmsServer.minigameImage;
				bl3 = realmsServer.minigameId == -1;
			} else {
				bl2 = realmsServer.activeSlot == this.slotIndex && realmsServer.worldType != RealmsServer.WorldType.MINIGAME;
				string = realmsWorldOptions.getSlotName(this.slotIndex);
				l = realmsWorldOptions.templateId;
				string2 = realmsWorldOptions.templateImage;
				bl3 = realmsWorldOptions.empty;
			}

			RealmsWorldSlotButton.Action action = RealmsWorldSlotButton.Action.NOTHING;
			String string3 = null;
			if (bl2) {
				if (!realmsServer.expired && realmsServer.state != RealmsServer.State.UNINITIALIZED) {
					action = RealmsWorldSlotButton.Action.JOIN;
					string3 = I18n.translate("mco.configure.world.slot.tooltip.active");
				}
			} else if (bl) {
				if (!realmsServer.expired) {
					action = RealmsWorldSlotButton.Action.SWITCH_SLOT;
					string3 = I18n.translate("mco.configure.world.slot.tooltip.minigame");
				}
			} else {
				action = RealmsWorldSlotButton.Action.SWITCH_SLOT;
				string3 = I18n.translate("mco.configure.world.slot.tooltip");
			}

			this.state = new RealmsWorldSlotButton.State(bl2, string, l, string2, bl3, bl, action, string3);
			this.method_25098(realmsServer, this.state.slotName, this.state.empty, this.state.minigame, this.state.action, this.state.actionPrompt);
		}
	}

	public void method_25098(RealmsServer realmsServer, String string, boolean bl, boolean bl2, RealmsWorldSlotButton.Action action, String string2) {
		String string3;
		if (action == RealmsWorldSlotButton.Action.NOTHING) {
			string3 = string;
		} else if (bl2) {
			if (bl) {
				string3 = string2;
			} else {
				string3 = string2 + " " + string + " " + realmsServer.minigameName;
			}
		} else {
			string3 = string2 + " " + string;
		}

		this.setMessage(string3);
	}

	@Override
	public void renderButton(int mouseX, int mouseY, float delta) {
		if (this.state != null) {
			this.drawSlotFrame(
				this.x,
				this.y,
				mouseX,
				mouseY,
				this.state.isCurrentlyActiveSlot,
				this.state.slotName,
				this.slotIndex,
				this.state.imageId,
				this.state.image,
				this.state.empty,
				this.state.minigame,
				this.state.action,
				this.state.actionPrompt
			);
		}
	}

	private void drawSlotFrame(
		int x,
		int y,
		int xm,
		int ym,
		boolean currentlyActiveSlot,
		String text,
		int i,
		long imageId,
		@Nullable String image,
		boolean empty,
		boolean minigame,
		RealmsWorldSlotButton.Action action,
		@Nullable String actionPrompt
	) {
		boolean bl = this.isHovered();
		if (this.isMouseOver((double)xm, (double)ym) && actionPrompt != null) {
			this.toolTipSetter.accept(actionPrompt);
		}

		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		TextureManager textureManager = minecraftClient.getTextureManager();
		if (minigame) {
			RealmsTextureManager.bindWorldTemplate(String.valueOf(imageId), image);
		} else if (empty) {
			textureManager.bindTexture(field_22682);
		} else if (image != null && imageId != -1L) {
			RealmsTextureManager.bindWorldTemplate(String.valueOf(imageId), image);
		} else if (i == 1) {
			textureManager.bindTexture(field_22683);
		} else if (i == 2) {
			textureManager.bindTexture(field_22684);
		} else if (i == 3) {
			textureManager.bindTexture(field_22685);
		}

		if (currentlyActiveSlot) {
			float f = 0.85F + 0.15F * MathHelper.cos((float)this.animTick * 0.2F);
			RenderSystem.color4f(f, f, f, 1.0F);
		} else {
			RenderSystem.color4f(0.56F, 0.56F, 0.56F, 1.0F);
		}

		drawTexture(x + 3, y + 3, 0.0F, 0.0F, 74, 74, 74, 74);
		textureManager.bindTexture(field_22681);
		boolean bl2 = bl && action != RealmsWorldSlotButton.Action.NOTHING;
		if (bl2) {
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		} else if (currentlyActiveSlot) {
			RenderSystem.color4f(0.8F, 0.8F, 0.8F, 1.0F);
		} else {
			RenderSystem.color4f(0.56F, 0.56F, 0.56F, 1.0F);
		}

		drawTexture(x, y, 0.0F, 0.0F, 80, 80, 80, 80);
		this.drawCenteredString(minecraftClient.textRenderer, text, x + 40, y + 66, 16777215);
	}

	@Environment(EnvType.CLIENT)
	public static enum Action {
		NOTHING,
		SWITCH_SLOT,
		JOIN;
	}

	@Environment(EnvType.CLIENT)
	public static class State {
		private final boolean isCurrentlyActiveSlot;
		private final String slotName;
		private final long imageId;
		private final String image;
		public final boolean empty;
		public final boolean minigame;
		public final RealmsWorldSlotButton.Action action;
		private final String actionPrompt;

		State(
			boolean isCurrentlyActiveSlot,
			String slotName,
			long imageId,
			@Nullable String image,
			boolean empty,
			boolean minigame,
			RealmsWorldSlotButton.Action action,
			@Nullable String actionPrompt
		) {
			this.isCurrentlyActiveSlot = isCurrentlyActiveSlot;
			this.slotName = slotName;
			this.imageId = imageId;
			this.image = image;
			this.empty = empty;
			this.minigame = minigame;
			this.action = action;
			this.actionPrompt = actionPrompt;
		}
	}
}
