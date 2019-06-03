package net.minecraft.text;

public class TranslationException extends IllegalArgumentException {
	public TranslationException(TranslatableText translatableText, String string) {
		super(String.format("Error parsing: %s: %s", translatableText, string));
	}

	public TranslationException(TranslatableText translatableText, int i) {
		super(String.format("Invalid index %d requested for %s", i, translatableText));
	}

	public TranslationException(TranslatableText translatableText, Throwable throwable) {
		super(String.format("Error while parsing: %s", translatableText), throwable);
	}
}
