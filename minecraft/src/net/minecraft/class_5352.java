package net.minecraft;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public interface class_5352 {
	class_5352 field_25347 = method_29485();
	class_5352 field_25348 = method_29486("pack.source.builtin");
	class_5352 field_25349 = method_29486("pack.source.world");
	class_5352 field_25350 = method_29486("pack.source.server");

	Text decorate(Text text);

	static class_5352 method_29485() {
		return text -> text;
	}

	static class_5352 method_29486(String string) {
		Text text = new TranslatableText(string);
		return text2 -> new TranslatableText("pack.nameAndSource", text2, text);
	}
}
