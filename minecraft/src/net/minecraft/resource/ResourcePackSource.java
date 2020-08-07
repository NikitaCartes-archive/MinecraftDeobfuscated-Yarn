package net.minecraft.resource;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public interface ResourcePackSource {
	ResourcePackSource field_25347 = method_29485();
	ResourcePackSource field_25348 = method_29486("pack.source.builtin");
	ResourcePackSource field_25349 = method_29486("pack.source.world");
	ResourcePackSource field_25350 = method_29486("pack.source.server");

	Text decorate(Text text);

	static ResourcePackSource method_29485() {
		return text -> text;
	}

	static ResourcePackSource method_29486(String string) {
		Text text = new TranslatableText(string);
		return text2 -> new TranslatableText("pack.nameAndSource", text2, text);
	}
}
