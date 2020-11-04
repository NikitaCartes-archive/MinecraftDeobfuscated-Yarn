package net.minecraft.resource;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public interface ResourcePackSource {
	ResourcePackSource PACK_SOURCE_NONE = onlyName();
	ResourcePackSource PACK_SOURCE_BUILTIN = nameAndSource("pack.source.builtin");
	ResourcePackSource PACK_SOURCE_WORLD = nameAndSource("pack.source.world");
	ResourcePackSource PACK_SOURCE_SERVER = nameAndSource("pack.source.server");

	Text decorate(Text packName);

	static ResourcePackSource onlyName() {
		return text -> text;
	}

	static ResourcePackSource nameAndSource(String source) {
		Text text = new TranslatableText(source);
		return text2 -> new TranslatableText("pack.nameAndSource", text2, text).formatted(Formatting.GRAY);
	}
}
