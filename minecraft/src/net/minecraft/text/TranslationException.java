package net.minecraft.text;

public class TranslationException extends IllegalArgumentException {
	public TranslationException(TranslatableText text, String message) {
		super(String.format("Error parsing: %s: %s", text, message));
	}

	public TranslationException(TranslatableText text, int index) {
		super(String.format("Invalid index %d requested for %s", index, text));
	}

	public TranslationException(TranslatableText text, Throwable cause) {
		super(String.format("Error while parsing: %s", text), cause);
	}
}
