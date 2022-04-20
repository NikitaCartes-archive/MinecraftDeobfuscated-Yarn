package net.minecraft.client.gui.screen.narration;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.Unit;

@Environment(EnvType.CLIENT)
public class Narration<T> {
	private final T value;
	private final BiConsumer<Consumer<String>, T> transformer;
	public static final Narration<?> EMPTY = new Narration((T)Unit.INSTANCE, (consumer, text) -> {
	});

	private Narration(T value, BiConsumer<Consumer<String>, T> transformer) {
		this.value = value;
		this.transformer = transformer;
	}

	public static Narration<?> string(String string) {
		return new Narration((T)string, Consumer::accept);
	}

	public static Narration<?> text(Text text) {
		return new Narration<>(text, (consumer, textx) -> consumer.accept(textx.getString()));
	}

	public static Narration<?> texts(List<Text> texts) {
		return new Narration((T)texts, (consumer, textsx) -> texts.stream().map(Text::getString).forEach(consumer));
	}

	public void forEachSentence(Consumer<String> consumer) {
		this.transformer.accept(consumer, this.value);
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof Narration)) {
			return false;
		} else {
			Narration<?> narration = (Narration)o;
			return narration.transformer == this.transformer && narration.value.equals(this.value);
		}
	}

	public int hashCode() {
		int i = this.value.hashCode();
		return 31 * i + this.transformer.hashCode();
	}
}
