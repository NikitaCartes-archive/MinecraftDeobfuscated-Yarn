package net.minecraft.text;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;

/**
 * The keybind text content. This {@link #getTranslated()} implementation
 * is not thread-safe.
 */
public class KeybindTextContent implements TextContent {
	public static final MapCodec<KeybindTextContent> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(Codec.STRING.fieldOf("keybind").forGetter(content -> content.key)).apply(instance, KeybindTextContent::new)
	);
	public static final TextContent.Type<KeybindTextContent> TYPE = new TextContent.Type<>(CODEC, "keybind");
	private final String key;
	@Nullable
	private Supplier<Text> translated;

	public KeybindTextContent(String key) {
		this.key = key;
	}

	private Text getTranslated() {
		if (this.translated == null) {
			this.translated = (Supplier<Text>)KeybindTranslations.factory.apply(this.key);
		}

		return (Text)this.translated.get();
	}

	@Override
	public <T> Optional<T> visit(StringVisitable.Visitor<T> visitor) {
		return this.getTranslated().visit(visitor);
	}

	@Override
	public <T> Optional<T> visit(StringVisitable.StyledVisitor<T> visitor, Style style) {
		return this.getTranslated().visit(visitor, style);
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else {
			if (o instanceof KeybindTextContent keybindTextContent && this.key.equals(keybindTextContent.key)) {
				return true;
			}

			return false;
		}
	}

	public int hashCode() {
		return this.key.hashCode();
	}

	public String toString() {
		return "keybind{" + this.key + "}";
	}

	public String getKey() {
		return this.key;
	}

	@Override
	public TextContent.Type<?> getType() {
		return TYPE;
	}
}
