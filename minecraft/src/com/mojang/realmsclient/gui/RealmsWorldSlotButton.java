package com.mojang.realmsclient.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.util.RealmsTextureManager;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsButtonProxy;
import net.minecraft.realms.RealmsMth;
import net.minecraft.realms.RealmsScreen;

@Environment(EnvType.CLIENT)
public class RealmsWorldSlotButton extends RealmsButton {
	private final Supplier<RealmsServer> serverDataProvider;
	private final Consumer<String> toolTipSetter;
	private final RealmsWorldSlotButton.Listener listener;
	private final int slotIndex;
	private int animTick;
	private RealmsWorldSlotButton.State state;

	public RealmsWorldSlotButton(
		int x,
		int y,
		int width,
		int height,
		Supplier<RealmsServer> serverDataProvider,
		Consumer<String> toolTipSetter,
		int id,
		int slotIndex,
		RealmsWorldSlotButton.Listener listener
	) {
		super(id, x, y, width, height, "");
		this.serverDataProvider = serverDataProvider;
		this.slotIndex = slotIndex;
		this.toolTipSetter = toolTipSetter;
		this.listener = listener;
	}

	@Override
	public void render(int xm, int ym, float a) {
		super.render(xm, ym, a);
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
				bl2 = realmsServer.worldType.equals(RealmsServer.WorldType.MINIGAME);
				string = "Minigame";
				l = (long)realmsServer.minigameId;
				string2 = realmsServer.minigameImage;
				bl3 = realmsServer.minigameId == -1;
			} else {
				bl2 = realmsServer.activeSlot == this.slotIndex && !realmsServer.worldType.equals(RealmsServer.WorldType.MINIGAME);
				string = realmsWorldOptions.getSlotName(this.slotIndex);
				l = realmsWorldOptions.templateId;
				string2 = realmsWorldOptions.templateImage;
				bl3 = realmsWorldOptions.empty;
			}

			String string3 = null;
			RealmsWorldSlotButton.Action action;
			if (bl2) {
				boolean bl4 = realmsServer.state == RealmsServer.State.OPEN || realmsServer.state == RealmsServer.State.CLOSED;
				if (!realmsServer.expired && bl4) {
					action = RealmsWorldSlotButton.Action.JOIN;
					string3 = Realms.getLocalizedString("mco.configure.world.slot.tooltip.active");
				} else {
					action = RealmsWorldSlotButton.Action.NOTHING;
				}
			} else if (bl) {
				if (realmsServer.expired) {
					action = RealmsWorldSlotButton.Action.NOTHING;
				} else {
					action = RealmsWorldSlotButton.Action.SWITCH_SLOT;
					string3 = Realms.getLocalizedString("mco.configure.world.slot.tooltip.minigame");
				}
			} else {
				action = RealmsWorldSlotButton.Action.SWITCH_SLOT;
				string3 = Realms.getLocalizedString("mco.configure.world.slot.tooltip");
			}

			this.state = new RealmsWorldSlotButton.State(bl2, string, l, string2, bl3, bl, action, string3);
			String string4;
			if (action == RealmsWorldSlotButton.Action.NOTHING) {
				string4 = string;
			} else if (bl) {
				if (bl3) {
					string4 = string3;
				} else {
					string4 = string3 + " " + string + " " + realmsServer.minigameName;
				}
			} else {
				string4 = string3 + " " + string;
			}

			this.setMessage(string4);
		}
	}

	@Override
	public void renderButton(int mouseX, int mouseY, float a) {
		if (this.state != null) {
			RealmsButtonProxy realmsButtonProxy = this.getProxy();
			this.drawSlotFrame(
				realmsButtonProxy.x,
				realmsButtonProxy.y,
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
		boolean bl = this.getProxy().isHovered();
		if (this.getProxy().isMouseOver((double)xm, (double)ym) && actionPrompt != null) {
			this.toolTipSetter.accept(actionPrompt);
		}

		if (minigame) {
			RealmsTextureManager.bindWorldTemplate(String.valueOf(imageId), image);
		} else if (empty) {
			Realms.bind("realms:textures/gui/realms/empty_frame.png");
		} else if (image != null && imageId != -1L) {
			RealmsTextureManager.bindWorldTemplate(String.valueOf(imageId), image);
		} else if (i == 1) {
			Realms.bind("textures/gui/title/background/panorama_0.png");
		} else if (i == 2) {
			Realms.bind("textures/gui/title/background/panorama_2.png");
		} else if (i == 3) {
			Realms.bind("textures/gui/title/background/panorama_3.png");
		}

		if (currentlyActiveSlot) {
			float f = 0.85F + 0.15F * RealmsMth.cos((float)this.animTick * 0.2F);
			GlStateManager.color4f(f, f, f, 1.0F);
		} else {
			GlStateManager.color4f(0.56F, 0.56F, 0.56F, 1.0F);
		}

		RealmsScreen.blit(x + 3, y + 3, 0.0F, 0.0F, 74, 74, 74, 74);
		Realms.bind("realms:textures/gui/realms/slot_frame.png");
		boolean bl2 = bl && action != RealmsWorldSlotButton.Action.NOTHING;
		if (bl2) {
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		} else if (currentlyActiveSlot) {
			GlStateManager.color4f(0.8F, 0.8F, 0.8F, 1.0F);
		} else {
			GlStateManager.color4f(0.56F, 0.56F, 0.56F, 1.0F);
		}

		RealmsScreen.blit(x, y, 0.0F, 0.0F, 80, 80, 80, 80);
		this.drawCenteredString(text, x + 40, y + 66, 16777215);
	}

	@Override
	public void onPress() {
		this.listener.onSlotClick(this.slotIndex, this.state.action, this.state.minigame, this.state.empty);
	}

	@Environment(EnvType.CLIENT)
	public static enum Action {
		NOTHING,
		SWITCH_SLOT,
		JOIN;
	}

	@Environment(EnvType.CLIENT)
	public interface Listener {
		void onSlotClick(int slotIndex, @Nonnull RealmsWorldSlotButton.Action action, boolean minigame, boolean empty);
	}

	@Environment(EnvType.CLIENT)
	public static class State {
		final boolean isCurrentlyActiveSlot;
		final String slotName;
		final long imageId;
		public final String image;
		public final boolean empty;
		final boolean minigame;
		public final RealmsWorldSlotButton.Action action;
		final String actionPrompt;

		State(
			boolean isCurrentlyActiveSlot,
			String slotName,
			long imageId,
			@Nullable String image,
			boolean empty,
			boolean minigame,
			@Nonnull RealmsWorldSlotButton.Action action,
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
