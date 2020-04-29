package com.mojang.realmsclient.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
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
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.realms.TickableRealmsButton;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class RealmsWorldSlotButton extends ButtonWidget implements TickableRealmsButton {
	public static final Identifier SLOT_FRAME = new Identifier("realms", "textures/gui/realms/slot_frame.png");
	public static final Identifier EMPTY_FRAME = new Identifier("realms", "textures/gui/realms/empty_frame.png");
	public static final Identifier PANORAMA_0 = new Identifier("minecraft", "textures/gui/title/background/panorama_0.png");
	public static final Identifier PANORAMA_2 = new Identifier("minecraft", "textures/gui/title/background/panorama_2.png");
	public static final Identifier PANORAMA_3 = new Identifier("minecraft", "textures/gui/title/background/panorama_3.png");
	private final Supplier<RealmsServer> serverDataProvider;
	private final Consumer<Text> toolTipSetter;
	private final int slotIndex;
	private int animTick;
	@Nullable
	private RealmsWorldSlotButton.State state;

	public RealmsWorldSlotButton(
		int x, int y, int width, int height, Supplier<RealmsServer> serverDataProvider, Consumer<Text> toolTipSetter, int id, ButtonWidget.PressAction action
	) {
		super(x, y, width, height, LiteralText.EMPTY, action);
		this.serverDataProvider = serverDataProvider;
		this.slotIndex = id;
		this.toolTipSetter = toolTipSetter;
	}

	@Nullable
	public RealmsWorldSlotButton.State getState() {
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

			RealmsWorldSlotButton.Action action = method_27455(realmsServer, bl2, bl);
			Pair<Text, Text> pair = this.method_27454(realmsServer, string, bl3, bl, action);
			this.state = new RealmsWorldSlotButton.State(bl2, string, l, string2, bl3, bl, action, pair.getFirst());
			this.setMessage(pair.getSecond());
		}
	}

	private static RealmsWorldSlotButton.Action method_27455(RealmsServer realmsServer, boolean bl, boolean bl2) {
		if (bl) {
			if (!realmsServer.expired && realmsServer.state != RealmsServer.State.UNINITIALIZED) {
				return RealmsWorldSlotButton.Action.JOIN;
			}
		} else {
			if (!bl2) {
				return RealmsWorldSlotButton.Action.SWITCH_SLOT;
			}

			if (!realmsServer.expired) {
				return RealmsWorldSlotButton.Action.SWITCH_SLOT;
			}
		}

		return RealmsWorldSlotButton.Action.NOTHING;
	}

	private Pair<Text, Text> method_27454(RealmsServer realmsServer, String string, boolean bl, boolean bl2, RealmsWorldSlotButton.Action action) {
		if (action == RealmsWorldSlotButton.Action.NOTHING) {
			return Pair.of(null, new LiteralText(string));
		} else {
			Text text;
			if (bl2) {
				if (bl) {
					text = LiteralText.EMPTY;
				} else {
					text = new LiteralText(" ").append(string).append(" ").append(realmsServer.minigameName);
				}
			} else {
				text = new LiteralText(" ").append(string);
			}

			Text text2;
			if (action == RealmsWorldSlotButton.Action.JOIN) {
				text2 = new TranslatableText("mco.configure.world.slot.tooltip.active");
			} else {
				text2 = bl2 ? new TranslatableText("mco.configure.world.slot.tooltip.minigame") : new TranslatableText("mco.configure.world.slot.tooltip");
			}

			Text text3 = text2.shallowCopy().append(text);
			return Pair.of(text2, text3);
		}
	}

	@Override
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (this.state != null) {
			this.drawSlotFrame(
				matrices,
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
		MatrixStack matrixStack,
		int i,
		int j,
		int k,
		int l,
		boolean bl,
		String string,
		int m,
		long n,
		@Nullable String string2,
		boolean bl2,
		boolean bl3,
		RealmsWorldSlotButton.Action action,
		@Nullable Text text
	) {
		boolean bl4 = this.isHovered();
		if (this.isMouseOver((double)k, (double)l) && text != null) {
			this.toolTipSetter.accept(text);
		}

		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		TextureManager textureManager = minecraftClient.getTextureManager();
		if (bl3) {
			RealmsTextureManager.bindWorldTemplate(String.valueOf(n), string2);
		} else if (bl2) {
			textureManager.bindTexture(EMPTY_FRAME);
		} else if (string2 != null && n != -1L) {
			RealmsTextureManager.bindWorldTemplate(String.valueOf(n), string2);
		} else if (m == 1) {
			textureManager.bindTexture(PANORAMA_0);
		} else if (m == 2) {
			textureManager.bindTexture(PANORAMA_2);
		} else if (m == 3) {
			textureManager.bindTexture(PANORAMA_3);
		}

		if (bl) {
			float f = 0.85F + 0.15F * MathHelper.cos((float)this.animTick * 0.2F);
			RenderSystem.color4f(f, f, f, 1.0F);
		} else {
			RenderSystem.color4f(0.56F, 0.56F, 0.56F, 1.0F);
		}

		drawTexture(matrixStack, i + 3, j + 3, 0.0F, 0.0F, 74, 74, 74, 74);
		textureManager.bindTexture(SLOT_FRAME);
		boolean bl5 = bl4 && action != RealmsWorldSlotButton.Action.NOTHING;
		if (bl5) {
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		} else if (bl) {
			RenderSystem.color4f(0.8F, 0.8F, 0.8F, 1.0F);
		} else {
			RenderSystem.color4f(0.56F, 0.56F, 0.56F, 1.0F);
		}

		drawTexture(matrixStack, i, j, 0.0F, 0.0F, 80, 80, 80, 80);
		this.drawCenteredString(matrixStack, minecraftClient.textRenderer, string, i + 40, j + 66, 16777215);
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
		@Nullable
		private final Text actionPrompt;

		State(
			boolean isCurrentlyActiveSlot,
			String slotName,
			long imageId,
			@Nullable String image,
			boolean empty,
			boolean minigame,
			RealmsWorldSlotButton.Action action,
			@Nullable Text text
		) {
			this.isCurrentlyActiveSlot = isCurrentlyActiveSlot;
			this.slotName = slotName;
			this.imageId = imageId;
			this.image = image;
			this.empty = empty;
			this.minigame = minigame;
			this.action = action;
			this.actionPrompt = text;
		}
	}
}
