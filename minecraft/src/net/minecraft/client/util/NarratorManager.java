package net.minecraft.client.util;

import com.mojang.text2speech.Narrator;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ClientChatListener;
import net.minecraft.client.options.NarratorMode;
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
	public static final Text EMPTY = LiteralText.EMPTY;
	private static final Logger LOGGER = LogManager.getLogger();
	public static final NarratorManager INSTANCE = new NarratorManager();
	private final Narrator narrator = Narrator.getNarrator();

	@Override
	public void onChatMessage(MessageType messageType, Text message, UUID senderUuid) {
		if (!MinecraftClient.getInstance().shouldBlockMessages(senderUuid)) {
			NarratorMode narratorMode = getNarratorOption();
			if (narratorMode != NarratorMode.field_18176 && this.narrator.active()) {
				if (narratorMode == NarratorMode.field_18177
					|| narratorMode == NarratorMode.field_18178 && messageType == MessageType.field_11737
					|| narratorMode == NarratorMode.field_18179 && messageType == MessageType.field_11735) {
					Text text;
					if (message instanceof TranslatableText && "chat.type.text".equals(((TranslatableText)message).getKey())) {
						text = new TranslatableText("chat.type.text.narrate", ((TranslatableText)message).getArgs());
					} else {
						text = message;
					}

					this.narrate(messageType.interruptsNarration(), text.getString());
				}
			}
		}
	}

	public void narrate(String text) {
		NarratorMode narratorMode = getNarratorOption();
		if (this.narrator.active() && narratorMode != NarratorMode.field_18176 && narratorMode != NarratorMode.field_18178 && !text.isEmpty()) {
			this.narrator.clear();
			this.narrate(true, text);
		}
	}

	private static NarratorMode getNarratorOption() {
		return MinecraftClient.getInstance().options.narrator;
	}

	private void narrate(boolean interrupt, String message) {
		if (SharedConstants.isDevelopment) {
			LOGGER.debug("Narrating: {}", message.replaceAll("\n", "\\\\n"));
		}

		this.narrator.say(message, interrupt);
	}

	public void addToast(NarratorMode option) {
		this.clear();
		this.narrator.say(new TranslatableText("options.narrator").append(" : ").append(option.getName()).getString(), true);
		ToastManager toastManager = MinecraftClient.getInstance().getToastManager();
		if (this.narrator.active()) {
			if (option == NarratorMode.field_18176) {
				SystemToast.show(toastManager, SystemToast.Type.field_2219, new TranslatableText("narrator.toast.disabled"), null);
			} else {
				SystemToast.show(toastManager, SystemToast.Type.field_2219, new TranslatableText("narrator.toast.enabled"), option.getName());
			}
		} else {
			SystemToast.show(
				toastManager, SystemToast.Type.field_2219, new TranslatableText("narrator.toast.disabled"), new TranslatableText("options.narrator.notavailable")
			);
		}
	}

	public boolean isActive() {
		return this.narrator.active();
	}

	public void clear() {
		if (getNarratorOption() != NarratorMode.field_18176 && this.narrator.active()) {
			this.narrator.clear();
		}
	}

	public void destroy() {
		this.narrator.destroy();
	}
}
