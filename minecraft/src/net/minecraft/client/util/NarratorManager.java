package net.minecraft.client.util;

import com.mojang.text2speech.Narrator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.class_370;
import net.minecraft.class_4065;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sortme.ClientChatListener;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.sortme.ChatMessageType;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class NarratorManager implements ClientChatListener {
	private static final Logger field_18210 = LogManager.getLogger();
	public static final NarratorManager INSTANCE = new NarratorManager();
	private final Narrator narrator = Narrator.getNarrator();

	@Override
	public void onChatMessage(ChatMessageType chatMessageType, TextComponent textComponent) {
		class_4065 lv = MinecraftClient.getInstance().options.narrator;
		if (lv != class_4065.field_18176 && this.narrator.active()) {
			if (lv == class_4065.field_18177
				|| lv == class_4065.field_18178 && chatMessageType == ChatMessageType.field_11737
				|| lv == class_4065.field_18179 && chatMessageType == ChatMessageType.field_11735) {
				if (textComponent instanceof TranslatableTextComponent && "chat.type.text".equals(((TranslatableTextComponent)textComponent).getKey())) {
					this.method_18621(new TranslatableTextComponent("chat.type.text.narrate", ((TranslatableTextComponent)textComponent).getParams()));
				} else {
					this.method_18621(textComponent);
				}
			}
		}
	}

	private void method_18621(TextComponent textComponent) {
		if (SharedConstants.isDevelopment) {
			field_18210.debug("Narrating: {}", textComponent.getString());
		}

		this.narrator.say(textComponent.getString());
	}

	public void addToast(class_4065 arg) {
		this.narrator.clear();
		this.narrator.say(new TranslatableTextComponent("options.narrator").getString() + " : " + new TranslatableTextComponent(arg.method_18511()).getString());
		ToastManager toastManager = MinecraftClient.getInstance().getToastManager();
		if (this.narrator.active()) {
			if (arg == class_4065.field_18176) {
				class_370.method_1990(toastManager, class_370.class_371.field_2219, new TranslatableTextComponent("narrator.toast.disabled"), null);
			} else {
				class_370.method_1990(
					toastManager, class_370.class_371.field_2219, new TranslatableTextComponent("narrator.toast.enabled"), new TranslatableTextComponent(arg.method_18511())
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
