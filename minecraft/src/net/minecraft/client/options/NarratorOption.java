package net.minecraft.client.options;

import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public enum NarratorOption {
	OFF(0, "options.narrator.off"),
	ALL(1, "options.narrator.all"),
	CHAT(2, "options.narrator.chat"),
	SYSTEM(3, "options.narrator.system");

	private static final NarratorOption[] VALUES = (NarratorOption[])Arrays.stream(values())
		.sorted(Comparator.comparingInt(NarratorOption::getId))
		.toArray(NarratorOption[]::new);
	private final int id;
	private final Text translationKey;

	private NarratorOption(int id, String translationKey) {
		this.id = id;
		this.translationKey = new TranslatableText(translationKey);
	}

	public int getId() {
		return this.id;
	}

	public Text getTranslationKey() {
		return this.translationKey;
	}

	public static NarratorOption byId(int id) {
		return VALUES[MathHelper.floorMod(id, VALUES.length)];
	}
}
