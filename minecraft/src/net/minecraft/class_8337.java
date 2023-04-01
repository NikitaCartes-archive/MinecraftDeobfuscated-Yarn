package net.minecraft;

import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

public class class_8337 extends class_8310 {
	private final String field_43893;

	public class_8337(String string, Block block) {
		super(block);
		this.field_43893 = string;
	}

	@Override
	protected Text method_50194(RegistryKey<Block> registryKey) {
		Text text = Text.translatable(this.field_43754.getTranslationKey());
		Text text2 = Text.translatable(Util.createTranslationKey("block", registryKey.getValue()));
		return Text.translatable(this.field_43893, text2, text);
	}
}
