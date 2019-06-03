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
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class NarratorManager implements ClientChatListener {
	public static final Text field_18967 = new LiteralText("");
	private static final Logger LOGGER = LogManager.getLogger();
	public static final NarratorManager INSTANCE = new NarratorManager();
	private final Narrator narrator = Narrator.getNarrator();

	@Override
	public void method_1794(MessageType messageType, Text text) {
		NarratorOption narratorOption = method_20602();
		if (narratorOption != NarratorOption.OFF && this.narrator.active()) {
			if (narratorOption == NarratorOption.ALL
				|| narratorOption == NarratorOption.CHAT && messageType == MessageType.field_11737
				|| narratorOption == NarratorOption.SYSTEM && messageType == MessageType.field_11735) {
				Text text2;
				if (text instanceof TranslatableText && "chat.type.text".equals(((TranslatableText)text).getKey())) {
					text2 = new TranslatableText("chat.type.text.narrate", ((TranslatableText)text).getArgs());
				} else {
					text2 = text;
				}

				this.narrate(messageType.interruptsNarration(), text2.getString());
			}
		}
	}

	public void narrate(String string) {
		NarratorOption narratorOption = method_20602();
		if (this.narrator.active() && narratorOption != NarratorOption.OFF && narratorOption != NarratorOption.CHAT && !string.isEmpty()) {
			this.narrator.clear();
			this.narrate(true, string);
		}
	}

	private static NarratorOption method_20602() {
		return MinecraftClient.getInstance().options.narrator;
	}

	private void narrate(boolean bl, String string) {
		if (SharedConstants.isDevelopment) {
			LOGGER.debug("Narrating: {}", string);
		}

		this.narrator.say(string, bl);
	}

	public void addToast(NarratorOption narratorOption) {
		this.clear();
		this.narrator.say(new TranslatableText("options.narrator").getString() + " : " + new TranslatableText(narratorOption.getTranslationKey()).getString(), true);
		ToastManager toastManager = MinecraftClient.getInstance().getToastManager();
		if (this.narrator.active()) {
			if (narratorOption == NarratorOption.OFF) {
				SystemToast.method_1990(toastManager, SystemToast.Type.field_2219, new TranslatableText("narrator.toast.disabled"), null);
			} else {
				SystemToast.method_1990(
					toastManager, SystemToast.Type.field_2219, new TranslatableText("narrator.toast.enabled"), new TranslatableText(narratorOption.getTranslationKey())
				);
			}
		} else {
			SystemToast.method_1990(
				toastManager, SystemToast.Type.field_2219, new TranslatableText("narrator.toast.disabled"), new TranslatableText("options.narrator.notavailable")
			);
		}
	}

	public boolean isActive() {
		return this.narrator.active();
	}

	public void clear() {
		if (method_20602() != NarratorOption.OFF && this.narrator.active()) {
			this.narrator.clear();
		}
	}

	public void destroy() {
		this.narrator.destroy();
	}
}
