package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public enum RealmsWorldGeneratorType {
	DEFAULT(0, Text.method_43471("generator.default")),
	FLAT(1, Text.method_43471("generator.flat")),
	LARGE_BIOMES(2, Text.method_43471("generator.large_biomes")),
	AMPLIFIED(3, Text.method_43471("generator.amplified"));

	private final int id;
	private final Text text;

	private RealmsWorldGeneratorType(int id, Text text) {
		this.id = id;
		this.text = text;
	}

	public Text getText() {
		return this.text;
	}

	public int getId() {
		return this.id;
	}
}
