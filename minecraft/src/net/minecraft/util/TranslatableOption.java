package net.minecraft.util;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public interface TranslatableOption {
	int getId();

	String getTranslationKey();

	default Text getText() {
		return new TranslatableText(this.getTranslationKey());
	}
}
