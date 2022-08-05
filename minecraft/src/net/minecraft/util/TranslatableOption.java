package net.minecraft.util;

import net.minecraft.text.Text;

/**
 * An option whose text can be translated.
 */
public interface TranslatableOption {
	int getId();

	String getTranslationKey();

	default Text getText() {
		return Text.translatable(this.getTranslationKey());
	}
}
