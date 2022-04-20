package net.minecraft.advancement;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public enum AdvancementFrame {
	TASK("task", 0, Formatting.GREEN),
	CHALLENGE("challenge", 26, Formatting.DARK_PURPLE),
	GOAL("goal", 52, Formatting.GREEN);

	private final String id;
	private final int textureV;
	private final Formatting titleFormat;
	private final Text toastText;

	private AdvancementFrame(String id, int texV, Formatting titleFormat) {
		this.id = id;
		this.textureV = texV;
		this.titleFormat = titleFormat;
		this.toastText = Text.translatable("advancements.toast." + id);
	}

	public String getId() {
		return this.id;
	}

	public int getTextureV() {
		return this.textureV;
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
