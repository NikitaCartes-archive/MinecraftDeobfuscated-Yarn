/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.narration;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.Unit;

@Environment(value=EnvType.CLIENT)
public class Narration<T> {
    private final T value;
    private final BiConsumer<Consumer<String>, T> transformer;
    public static final Narration<?> EMPTY = new Narration<Unit>(Unit.INSTANCE, (consumer, text) -> {});

    private Narration(T value, BiConsumer<Consumer<String>, T> transformer) {
        this.value = value;
        this.transformer = transformer;
    }

    public static Narration<?> string(String string) {
        return new Narration<String>(string, Consumer::accept);
    }

    public static Narration<?> text(Text text2) {
        return new Narration<Text>(text2, (consumer, text) -> consumer.accept(text.getString()));
    }

    public static Narration<?> texts(List<Text> texts2) {
        return new Narration<List>(texts2, (consumer, texts) -> texts2.stream().map(Text::getString).forEach((Consumer<String>)consumer));
    }

    public void forEachSentence(Consumer<String> consumer) {
        this.transformer.accept(consumer, (Consumer<String>)this.value);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Narration) {
            Narration narration = (Narration)o;
            return narration.transformer == this.transformer && narration.value.equals(this.value);
        }
        return false;
    }

    public int hashCode() {
        int i = this.value.hashCode();
        i = 31 * i + this.transformer.hashCode();
        return i;
    }
}

