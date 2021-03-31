package net.minecraft.client.option;

import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.util.math.MathHelper;

public enum ChatVisibility {
	FULL(0, "options.chat.visibility.full"),
	SYSTEM(1, "options.chat.visibility.system"),
	HIDDEN(2, "options.chat.visibility.hidden");

	private static final ChatVisibility[] VALUES = (ChatVisibility[])Arrays.stream(values())
		.sorted(Comparator.comparingInt(ChatVisibility::getId))
		.toArray(ChatVisibility[]::new);
	private final int id;
	private final String translationKey;

	private ChatVisibility(int id, String translationKey) {
		this.id = id;
		this.translationKey = translationKey;
	}

	public int getId() {
		return this.id;
	}

	public String getTranslationKey() {
		return this.translationKey;
	}

	public static ChatVisibility byId(int id) {
		return VALUES[MathHelper.floorMod(id, VALUES.length)];
	}
}
