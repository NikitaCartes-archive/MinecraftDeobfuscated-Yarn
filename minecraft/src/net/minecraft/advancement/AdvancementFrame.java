package net.minecraft.advancement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Formatting;

public enum AdvancementFrame {
	TASK("task", 0, Formatting.GREEN),
	CHALLENGE("challenge", 26, Formatting.DARK_PURPLE),
	GOAL("goal", 52, Formatting.GREEN);

	private final String id;
	private final int texV;
	private final Formatting titleFormat;

	private AdvancementFrame(String string2, int j, Formatting formatting) {
		this.id = string2;
		this.texV = j;
		this.titleFormat = formatting;
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

	public Formatting getTitleFormat() {
		return this.titleFormat;
	}
}
