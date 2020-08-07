package net.minecraft.advancement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public enum AdvancementFrame {
	field_1254("task", 0, Formatting.field_1060),
	field_1250("challenge", 26, Formatting.field_1064),
	field_1249("goal", 52, Formatting.field_1060);

	private final String id;
	private final int textureV;
	private final Formatting titleFormat;
	private final Text toastText;

	private AdvancementFrame(String id, int texV, Formatting titleFormat) {
		this.id = id;
		this.textureV = texV;
		this.titleFormat = titleFormat;
		this.toastText = new TranslatableText("advancements.toast." + id);
	}

	public String getId() {
		return this.id;
	}

	@Environment(EnvType.CLIENT)
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

	@Environment(EnvType.CLIENT)
	public Text getToastText() {
		return this.toastText;
	}
}
