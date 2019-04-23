package net.minecraft.advancement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormat;

public enum AdvancementFrame {
	field_1254("task", 0, ChatFormat.field_1060),
	field_1250("challenge", 26, ChatFormat.field_1064),
	field_1249("goal", 52, ChatFormat.field_1060);

	private final String id;
	private final int texV;
	private final ChatFormat titleFormat;

	private AdvancementFrame(String string2, int j, ChatFormat chatFormat) {
		this.id = string2;
		this.texV = j;
		this.titleFormat = chatFormat;
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

	public ChatFormat getTitleFormat() {
		return this.titleFormat;
	}
}
