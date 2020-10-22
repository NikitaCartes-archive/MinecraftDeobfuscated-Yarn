package net.minecraft.resource;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public interface ResourcePackSource {
	ResourcePackSource field_25347 = method_29485();
	ResourcePackSource PACK_SOURCE_BUILTIN = method_29486("pack.source.builtin");
	ResourcePackSource PACK_SOURCE_WORLD = method_29486("pack.source.world");
	ResourcePackSource PACK_SOURCE_SERVER = method_29486("pack.source.server");

	Text decorate(Text text);

	static ResourcePackSource method_29485() {
		return text -> text;
	}

	static ResourcePackSource method_29486(String string) {
		Text text = new TranslatableText(string);
		return text2 -> new TranslatableText("pack.nameAndSource", text2, text).formatted(Formatting.GRAY);
	}
}
