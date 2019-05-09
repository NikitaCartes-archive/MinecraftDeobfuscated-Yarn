package net.minecraft.client.util;

import com.mojang.text2speech.Narrator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ClientChatListener;
import net.minecraft.client.options.NarratorOption;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class NarratorManager implements ClientChatListener {
	public static final Component EMPTY = new TextComponent("");
	private static final Logger LOGGER = LogManager.getLogger();
	public static final NarratorManager INSTANCE = new NarratorManager();
	private final Narrator narrator = Narrator.getNarrator();

	@Override
	public void onChatMessage(ChatMessageType chatMessageType, Component component) {
		NarratorOption narratorOption = MinecraftClient.getInstance().options.narrator;
		if (narratorOption != NarratorOption.OFF && this.narrator.active()) {
			if (narratorOption == NarratorOption.ALL
				|| narratorOption == NarratorOption.CHAT && chatMessageType == ChatMessageType.field_11737
				|| narratorOption == NarratorOption.SYSTEM && chatMessageType == ChatMessageType.field_11735) {
				Component component2;
				if (component instanceof TranslatableComponent && "chat.type.text".equals(((TranslatableComponent)component).getKey())) {
					component2 = new TranslatableComponent("chat.type.text.narrate", ((TranslatableComponent)component).getParams());
				} else {
					component2 = component;
				}

				this.narrate(chatMessageType.interruptsNarration(), component2.getString());
			}
		}
	}

	public void narrate(String string) {
		NarratorOption narratorOption = MinecraftClient.getInstance().options.narrator;
		if (this.narrator.active() && narratorOption != NarratorOption.OFF && narratorOption != NarratorOption.CHAT && !string.isEmpty()) {
			this.narrator.clear();
			this.narrate(true, string);
		}
	}

	private void narrate(boolean bl, String string) {
		if (SharedConstants.isDevelopment) {
			LOGGER.debug("Narrating: {}", string);
		}

		this.narrator.say(string, bl);
	}

	public void addToast(NarratorOption narratorOption) {
		this.narrator.clear();
		this.narrator
			.say(new TranslatableComponent("options.narrator").getString() + " : " + new TranslatableComponent(narratorOption.getTranslationKey()).getString(), true);
		ToastManager toastManager = MinecraftClient.getInstance().getToastManager();
		if (this.narrator.active()) {
			if (narratorOption == NarratorOption.OFF) {
				SystemToast.show(toastManager, SystemToast.Type.field_2219, new TranslatableComponent("narrator.toast.disabled"), null);
			} else {
				SystemToast.show(
					toastManager,
					SystemToast.Type.field_2219,
					new TranslatableComponent("narrator.toast.enabled"),
					new TranslatableComponent(narratorOption.getTranslationKey())
				);
			}
		} else {
			SystemToast.show(
				toastManager, SystemToast.Type.field_2219, new TranslatableComponent("narrator.toast.disabled"), new TranslatableComponent("options.narrator.notavailable")
			);
		}
	}

	public boolean isActive() {
		return this.narrator.active();
	}

	public void clear() {
		this.narrator.clear();
	}

	public void destroy() {
		this.narrator.destroy();
	}
}
