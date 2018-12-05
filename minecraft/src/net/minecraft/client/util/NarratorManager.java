package net.minecraft.client.util;

import com.mojang.text2speech.Narrator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_370;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.settings.GameOptions;
import net.minecraft.client.sortme.ChatMessageType;
import net.minecraft.client.sortme.ClientChatListener;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;

@Environment(EnvType.CLIENT)
public class NarratorManager implements ClientChatListener {
	public static final NarratorManager INSTANCE = new NarratorManager();
	private final Narrator narrator = Narrator.getNarrator();

	@Override
	public void onChatMessage(ChatMessageType chatMessageType, TextComponent textComponent) {
		int i = MinecraftClient.getInstance().options.narrator;
		if (i != 0 && this.narrator.active()) {
			if (i == 1 || i == 2 && chatMessageType == ChatMessageType.field_11737 || i == 3 && chatMessageType == ChatMessageType.field_11735) {
				if (textComponent instanceof TranslatableTextComponent && "chat.type.text".equals(((TranslatableTextComponent)textComponent).getKey())) {
					this.narrator.say(new TranslatableTextComponent("chat.type.text.narrate", ((TranslatableTextComponent)textComponent).getParams()).getString());
				} else {
					this.narrator.say(textComponent.getString());
				}
			}
		}
	}

	public void addToast(int i) {
		this.narrator.clear();
		this.narrator
			.say(new TranslatableTextComponent("options.narrator").getString() + " : " + new TranslatableTextComponent(GameOptions.NARRATOR_SETTINGS[i]).getString());
		ToastManager toastManager = MinecraftClient.getInstance().getToastManager();
		if (this.narrator.active()) {
			if (i == 0) {
				class_370.method_1990(toastManager, class_370.class_371.field_2219, new TranslatableTextComponent("narrator.toast.disabled"), null);
			} else {
				class_370.method_1990(
					toastManager,
					class_370.class_371.field_2219,
					new TranslatableTextComponent("narrator.toast.enabled"),
					new TranslatableTextComponent(GameOptions.NARRATOR_SETTINGS[i])
				);
			}
		} else {
			class_370.method_1990(
				toastManager,
				class_370.class_371.field_2219,
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
}
