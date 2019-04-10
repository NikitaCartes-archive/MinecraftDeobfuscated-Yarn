package net.minecraft.client.util;

import com.mojang.text2speech.Narrator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.NarratorOption;
import net.minecraft.client.sortme.ClientChatListener;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.sortme.ChatMessageType;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class NarratorManager implements ClientChatListener {
	public static final TextComponent field_18967 = new StringTextComponent("");
	private static final Logger field_18210 = LogManager.getLogger();
	public static final NarratorManager INSTANCE = new NarratorManager();
	private final Narrator narrator = Narrator.getNarrator();

	@Override
	public void onChatMessage(ChatMessageType chatMessageType, TextComponent textComponent) {
		NarratorOption narratorOption = MinecraftClient.getInstance().options.narrator;
		if (narratorOption != NarratorOption.field_18176 && this.narrator.active()) {
			if (narratorOption == NarratorOption.field_18177
				|| narratorOption == NarratorOption.field_18178 && chatMessageType == ChatMessageType.field_11737
				|| narratorOption == NarratorOption.field_18179 && chatMessageType == ChatMessageType.field_11735) {
				TextComponent textComponent2;
				if (textComponent instanceof TranslatableTextComponent && "chat.type.text".equals(((TranslatableTextComponent)textComponent).getKey())) {
					textComponent2 = new TranslatableTextComponent("chat.type.text.narrate", ((TranslatableTextComponent)textComponent).getParams());
				} else {
					textComponent2 = textComponent;
				}

				this.method_18621(chatMessageType.method_19457(), textComponent2.getString());
			}
		}
	}

	public void method_19788(String string) {
		NarratorOption narratorOption = MinecraftClient.getInstance().options.narrator;
		if (this.narrator.active() && narratorOption != NarratorOption.field_18176 && narratorOption != NarratorOption.field_18178 && !string.isEmpty()) {
			this.narrator.clear();
			this.method_18621(true, string);
		}
	}

	private void method_18621(boolean bl, String string) {
		if (SharedConstants.isDevelopment) {
			field_18210.debug("Narrating: {}", string);
		}

		this.narrator.say(string, bl);
	}

	public void addToast(NarratorOption narratorOption) {
		this.narrator.clear();
		this.narrator
			.say(
				new TranslatableTextComponent("options.narrator").getString() + " : " + new TranslatableTextComponent(narratorOption.getTranslationKey()).getString(), true
			);
		ToastManager toastManager = MinecraftClient.getInstance().getToastManager();
		if (this.narrator.active()) {
			if (narratorOption == NarratorOption.field_18176) {
				SystemToast.show(toastManager, SystemToast.Type.field_2219, new TranslatableTextComponent("narrator.toast.disabled"), null);
			} else {
				SystemToast.show(
					toastManager,
					SystemToast.Type.field_2219,
					new TranslatableTextComponent("narrator.toast.enabled"),
					new TranslatableTextComponent(narratorOption.getTranslationKey())
				);
			}
		} else {
			SystemToast.show(
				toastManager,
				SystemToast.Type.field_2219,
				new TranslatableTextComponent("narrator.toast.disabled"),
				new TranslatableTextComponent("options.narrator.notavailable")
			);
		}
	}

	public boolean isActive() {
		return this.narrator.active();
	}

	public void clear() {
		this.narrator.clear();
	}

	public void method_20371() {
		this.narrator.destroy();
	}
}
