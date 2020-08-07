package net.minecraft.client.options;

import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;

public enum ChatVisibility {
	field_7538(0, "options.chat.visibility.full"),
	field_7539(1, "options.chat.visibility.system"),
	field_7536(2, "options.chat.visibility.hidden");

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

	@Environment(EnvType.CLIENT)
	public String getTranslationKey() {
		return this.translationKey;
	}

	@Environment(EnvType.CLIENT)
	public static ChatVisibility byId(int id) {
		return VALUES[MathHelper.floorMod(id, VALUES.length)];
	}
}
