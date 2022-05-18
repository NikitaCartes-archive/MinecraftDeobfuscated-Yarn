package net.minecraft.client.util;

import com.mojang.logging.LogUtils;
import com.mojang.text2speech.Narrator;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ClientChatListener;
import net.minecraft.client.option.NarratorMode;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.network.MessageSender;
import net.minecraft.network.MessageType;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class NarratorManager implements ClientChatListener {
	public static final Text EMPTY = ScreenTexts.EMPTY;
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final NarratorManager INSTANCE = new NarratorManager();
	private final Narrator narrator = Narrator.getNarrator();

	@Override
	public void onChatMessage(MessageType type, Text message, @Nullable MessageSender sender) {
		NarratorMode narratorMode = getNarratorOption();
		if (narratorMode != NarratorMode.OFF) {
			if (!this.narrator.active()) {
				this.debugPrintMessage(message.getString());
			} else {
				type.narration().ifPresent(narrationRule -> {
					if (narratorMode.shouldNarrate(narrationRule.kind())) {
						Text text2 = narrationRule.apply(message, sender);
						String string = text2.getString();
						this.debugPrintMessage(string);
						this.narrator.say(string, narrationRule.kind().shouldInterrupt());
					}
				});
			}
		}
	}

	public void narrate(Text text) {
		this.narrate(text.getString());
	}

	public void narrate(String text) {
		NarratorMode narratorMode = getNarratorOption();
		if (narratorMode != NarratorMode.OFF && narratorMode != NarratorMode.CHAT && !text.isEmpty()) {
			this.debugPrintMessage(text);
			if (this.narrator.active()) {
				this.narrator.clear();
				this.narrator.say(text, true);
			}
		}
	}

	private static NarratorMode getNarratorOption() {
		return MinecraftClient.getInstance().options.getNarrator().getValue();
	}

	private void debugPrintMessage(String message) {
		if (SharedConstants.isDevelopment) {
			LOGGER.debug("Narrating: {}", message.replaceAll("\n", "\\\\n"));
		}
	}

	public void addToast(NarratorMode option) {
		this.clear();
		this.narrator.say(Text.translatable("options.narrator").append(" : ").append(option.getName()).getString(), true);
		ToastManager toastManager = MinecraftClient.getInstance().getToastManager();
		if (this.narrator.active()) {
			if (option == NarratorMode.OFF) {
				SystemToast.show(toastManager, SystemToast.Type.NARRATOR_TOGGLE, Text.translatable("narrator.toast.disabled"), null);
			} else {
				SystemToast.show(toastManager, SystemToast.Type.NARRATOR_TOGGLE, Text.translatable("narrator.toast.enabled"), option.getName());
			}
		} else {
			SystemToast.show(
				toastManager, SystemToast.Type.NARRATOR_TOGGLE, Text.translatable("narrator.toast.disabled"), Text.translatable("options.narrator.notavailable")
			);
		}
	}

	public boolean isActive() {
		return this.narrator.active();
	}

	public void clear() {
		if (getNarratorOption() != NarratorMode.OFF && this.narrator.active()) {
			this.narrator.clear();
		}
	}

	public void destroy() {
		this.narrator.destroy();
	}
}
