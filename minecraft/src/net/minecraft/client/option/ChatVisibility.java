package net.minecraft.client.option;

import java.util.function.IntFunction;
import net.minecraft.util.TranslatableOption;
import net.minecraft.util.function.ValueLists;

public enum ChatVisibility implements TranslatableOption {
	FULL(0, "options.chat.visibility.full"),
	SYSTEM(1, "options.chat.visibility.system"),
	HIDDEN(2, "options.chat.visibility.hidden");

	private static final IntFunction<ChatVisibility> BY_ID = ValueLists.createIdToValueFunction(
		ChatVisibility::getId, values(), ValueLists.OutOfBoundsHandling.WRAP
	);
	private final int id;
	private final String translationKey;

	private ChatVisibility(int id, String translationKey) {
		this.id = id;
		this.translationKey = translationKey;
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public String getTranslationKey() {
		return this.translationKey;
	}

	public static ChatVisibility byId(int id) {
		return (ChatVisibility)BY_ID.apply(id);
	}
}
