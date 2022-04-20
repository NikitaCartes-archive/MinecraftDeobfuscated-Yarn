package net.minecraft.resource;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public interface ResourcePackSource {
	ResourcePackSource PACK_SOURCE_NONE = onlyName();
	ResourcePackSource PACK_SOURCE_BUILTIN = nameAndSource("pack.source.builtin");
	ResourcePackSource PACK_SOURCE_WORLD = nameAndSource("pack.source.world");
	ResourcePackSource PACK_SOURCE_SERVER = nameAndSource("pack.source.server");

	Text decorate(Text packName);

	static ResourcePackSource onlyName() {
		return name -> name;
	}

	static ResourcePackSource nameAndSource(String source) {
		Text text = Text.translatable(source);
		return name -> Text.translatable("pack.nameAndSource", name, text).formatted(Formatting.GRAY);
	}
}
