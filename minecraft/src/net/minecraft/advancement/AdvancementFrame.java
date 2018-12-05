package net.minecraft.advancement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TextFormat;

public enum AdvancementFrame {
	TASK("task", 0, TextFormat.GREEN),
	CHALLENGE("challenge", 26, TextFormat.DARK_PURPLE),
	GOAL("goal", 52, TextFormat.GREEN);

	private final String id;
	private final int texV;
	private final TextFormat titleFormat;

	private AdvancementFrame(String string2, int j, TextFormat textFormat) {
		this.id = string2;
		this.texV = j;
		this.titleFormat = textFormat;
	}

	public String getId() {
		return this.id;
	}

	@Environment(EnvType.CLIENT)
	public int texV() {
		return this.texV;
	}

	public static AdvancementFrame forName(String string) {
		for (AdvancementFrame advancementFrame : values()) {
			if (advancementFrame.id.equals(string)) {
				return advancementFrame;
			}
		}

		throw new IllegalArgumentException("Unknown frame type '" + string + "'");
	}

	public TextFormat getTitleFormat() {
		return this.titleFormat;
	}
}
