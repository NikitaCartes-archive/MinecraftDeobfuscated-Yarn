package net.minecraft.advancement;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public enum AdvancementFrame {
	TASK("task", Formatting.GREEN),
	CHALLENGE("challenge", Formatting.DARK_PURPLE),
	GOAL("goal", Formatting.GREEN);

	private final String id;
	private final Formatting titleFormat;
	private final Text toastText;

	private AdvancementFrame(String id, Formatting titleFormat) {
		this.id = id;
		this.titleFormat = titleFormat;
		this.toastText = Text.translatable("advancements.toast." + id);
	}

	public String getId() {
		return this.id;
	}

	public static AdvancementFrame forName(String name) {
		for (AdvancementFrame advancementFrame : values()) {
			if (advancementFrame.id.equals(name)) {
				return advancementFrame;
			}
		}

		throw new IllegalArgumentException("Unknown frame type '" + name + "'");
	}

	public Formatting getTitleFormat() {
		return this.titleFormat;
	}

	public Text getToastText() {
		return this.toastText;
	}
}
