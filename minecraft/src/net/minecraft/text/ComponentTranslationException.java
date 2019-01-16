package net.minecraft.text;

public class ComponentTranslationException extends IllegalArgumentException {
	public ComponentTranslationException(TranslatableTextComponent translatableTextComponent, String string) {
		super(String.format("Error parsing: %s: %s", translatableTextComponent, string));
	}

	public ComponentTranslationException(TranslatableTextComponent translatableTextComponent, int i) {
		super(String.format("Invalid index %d requested for %s", i, translatableTextComponent));
	}

	public ComponentTranslationException(TranslatableTextComponent translatableTextComponent, Throwable throwable) {
		super(String.format("Error while parsing: %s", translatableTextComponent), throwable);
	}
}
