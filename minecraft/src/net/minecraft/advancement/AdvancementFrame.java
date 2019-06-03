package net.minecraft.advancement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Formatting;

public enum AdvancementFrame {
	field_1254("task", 0, Formatting.field_1060),
	field_1250("challenge", 26, Formatting.field_1064),
	field_1249("goal", 52, Formatting.field_1060);

	private final String id;
	private final int texV;
	private final Formatting field_1255;

	private AdvancementFrame(String string2, int j, Formatting formatting) {
		this.id = string2;
		this.texV = j;
		this.field_1255 = formatting;
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

	public Formatting method_830() {
		return this.field_1255;
	}
}
