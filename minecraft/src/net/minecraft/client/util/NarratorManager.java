package net.minecraft.client.util;

import com.mojang.logging.LogUtils;
import com.mojang.text2speech.Narrator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.NarratorMode;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import org.slf4j.Logger;

/**
 * A bridge between Minecraft and {@link com.mojang.text2speech.Narrator}.
 */
@Environment(EnvType.CLIENT)
public class NarratorManager {
	/**
	 * An empty text for narration.
	 */
	public static final Text EMPTY = ScreenTexts.EMPTY;
	private static final Logger LOGGER = LogUtils.getLogger();
	private final MinecraftClient client;
	private final Narrator narrator = Narrator.getNarrator();

	public NarratorManager(MinecraftClient client) {
		this.client = client;
	}

	/**
	 * Narrates a chat message.
	 * 
	 * @see NarratorMode#shouldNarrateChat
	 */
	public void narrateChatMessage(Text text) {
		if (this.getNarratorMode().shouldNarrateChat()) {
			String string = text.getString();
			this.debugPrintMessage(string);
			this.narrator.say(string, false);
		}
	}

	/**
	 * Narrates a system message.
	 * 
	 * @see NarratorMode#shouldNarrateSystem
	 */
	public void narrateSystemMessage(Text text) {
		String string = text.getString();
		if (this.getNarratorMode().shouldNarrateSystem() && !string.isEmpty()) {
			this.debugPrintMessage(string);
			this.narrator.say(string, false);
		}
	}

	/**
	 * Narrates system text.
	 * 
	 * @see NarratorMode#shouldNarrateSystem
	 * 
	 * @param text the text to narrate
	 */
	public void narrate(Text text) {
		this.narrate(text.getString());
	}

	/**
	 * Narrates system text.
	 * 
	 * @see NarratorMode#shouldNarrateSystem
	 * 
	 * @param text the text to narrate
	 */
	public void narrate(String text) {
		if (this.getNarratorMode().shouldNarrateSystem() && !text.isEmpty()) {
			this.debugPrintMessage(text);
			if (this.narrator.active()) {
				this.narrator.clear();
				this.narrator.say(text, true);
			}
		}
	}

	/**
	 * {@return the current narrator mode of the client}
	 */
	private NarratorMode getNarratorMode() {
		return this.client.options.getNarrator().getValue();
	}

	/**
	 * If the game is {@linkplain net.minecraft.SharedConstants#isDevelopment
	 * in a development environment}, logs a debug message for a narrated string.
	 * 
	 * @param message the narrated message
	 */
	private void debugPrintMessage(String message) {
		if (SharedConstants.isDevelopment) {
			LOGGER.debug("Narrating: {}", message.replaceAll("\n", "\\\\n"));
		}
	}

	/**
	 * Narrates a message informing the user about a changed narration mode
	 * and displays it in a toast.
	 * 
	 * @param mode the new narrator mode
	 */
	public void onModeChange(NarratorMode mode) {
		this.clear();
		this.narrator.say(Text.translatable("options.narrator").append(" : ").append(mode.getName()).getString(), true);
		ToastManager toastManager = MinecraftClient.getInstance().getToastManager();
		if (this.narrator.active()) {
			if (mode == NarratorMode.OFF) {
				SystemToast.show(toastManager, SystemToast.Type.NARRATOR_TOGGLE, Text.translatable("narrator.toast.disabled"), null);
			} else {
				SystemToast.show(toastManager, SystemToast.Type.NARRATOR_TOGGLE, Text.translatable("narrator.toast.enabled"), mode.getName());
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
		if (this.getNarratorMode() != NarratorMode.OFF && this.narrator.active()) {
			this.narrator.clear();
		}
	}

	public void destroy() {
		this.narrator.destroy();
	}

	public void checkNarratorLibrary(boolean narratorEnabled) {
		if (narratorEnabled
			&& !this.isActive()
			&& !TinyFileDialogs.tinyfd_messageBox(
				"Minecraft",
				"Failed to initialize text-to-speech library. Do you want to continue?\nIf this problem persists, please report it at bugs.mojang.com",
				"yesno",
				"error",
				true
			)) {
			throw new NarratorManager.InactiveNarratorLibraryException("Narrator library is not active");
		}
	}

	@Environment(EnvType.CLIENT)
	public static class InactiveNarratorLibraryException extends GlException {
		public InactiveNarratorLibraryException(String string) {
			super(string);
		}
	}
}
