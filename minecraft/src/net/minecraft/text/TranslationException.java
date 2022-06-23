package net.minecraft.text;

public class TranslationException extends IllegalArgumentException {
	public TranslationException(TranslatableTextContent text, String message) {
		super(String.format("Error parsing: %s: %s", text, message));
	}

	public TranslationException(TranslatableTextContent text, int index) {
		super(String.format("Invalid index %d requested for %s", index, text));
	}

	public TranslationException(TranslatableTextContent text, Throwable cause) {
		super(String.format("Error while parsing: %s", text), cause);
	}
}
