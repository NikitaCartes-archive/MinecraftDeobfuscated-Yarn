package net.minecraft.client.gui.screen.narration;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public interface NarrationMessageBuilder {
	default void put(NarrationPart part, Text text) {
		this.put(part, Narration.string(text.getString()));
	}

	default void put(NarrationPart part, String string) {
		this.put(part, Narration.string(string));
	}

	default void put(NarrationPart part, Text... texts) {
		this.put(part, Narration.texts(ImmutableList.copyOf(texts)));
	}

	void put(NarrationPart part, Narration<?> narration);

	NarrationMessageBuilder nextMessage();
}
