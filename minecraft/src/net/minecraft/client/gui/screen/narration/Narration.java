package net.minecraft.client.gui.screen.narration;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.Unit;

/**
 * A narration is a message consisting of a list of string "sentences".
 * The sentences can be iterated using {@link #forEachSentence forEachSentence}.
 * 
 * <p>Narrations are attached to {@linkplain NarrationPart narration parts}
 * using {@link NarrationMessageBuilder#put(NarrationPart, Narration)}.
 */
@Environment(EnvType.CLIENT)
public class Narration<T> {
	private final T value;
	private final BiConsumer<Consumer<String>, T> transformer;
	/**
	 * An empty narration that contains no sentences.
	 */
	public static final Narration<?> EMPTY = new Narration<>(Unit.INSTANCE, (consumer, text) -> {
	});

	private Narration(T value, BiConsumer<Consumer<String>, T> transformer) {
		this.value = value;
		this.transformer = transformer;
	}

	/**
	 * Creates a narration from a single string sentence.
	 * 
	 * @return the created narration
	 * 
	 * @param string the narrated sentence
	 */
	public static Narration<?> string(String string) {
		return new Narration<>(string, Consumer::accept);
	}

	/**
	 * Creates a narration from a single {@link Text} sentence.
	 * 
	 * @implSpec The sentence is converted to a string using {@link Text#getString}.
	 * @return the created narration
	 * 
	 * @param text the narrated sentence
	 */
	public static Narration<?> text(Text text) {
		return new Narration<>(text, (consumer, textx) -> consumer.accept(textx.getString()));
	}

	/**
	 * Creates a narration from a list of {@link Text} sentences.
	 * 
	 * @implSpec The sentences are converted to strings using {@link Text#getString}.
	 * @return the created narration
	 * 
	 * @param texts the narrated sentences
	 */
	public static Narration<?> texts(List<Text> texts) {
		return new Narration<>(texts, (consumer, textsx) -> texts.stream().map(Text::getString).forEach(consumer));
	}

	/**
	 * Iterates all sentences in this narration with a {@link Consumer}.
	 * 
	 * @param consumer the consumer to accept all sentences in this narration
	 */
	public void forEachSentence(Consumer<String> consumer) {
		this.transformer.accept(consumer, this.value);
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else {
			return !(o instanceof Narration<?> narration) ? false : narration.transformer == this.transformer && narration.value.equals(this.value);
		}
	}

	public int hashCode() {
		int i = this.value.hashCode();
		return 31 * i + this.transformer.hashCode();
	}
}
