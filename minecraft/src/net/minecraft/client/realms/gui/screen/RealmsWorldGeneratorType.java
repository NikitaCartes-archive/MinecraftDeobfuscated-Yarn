package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public enum RealmsWorldGeneratorType {
	DEFAULT(0, Text.translatable("generator.default")),
	FLAT(1, Text.translatable("generator.flat")),
	LARGE_BIOMES(2, Text.translatable("generator.large_biomes")),
	AMPLIFIED(3, Text.translatable("generator.amplified"));

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
