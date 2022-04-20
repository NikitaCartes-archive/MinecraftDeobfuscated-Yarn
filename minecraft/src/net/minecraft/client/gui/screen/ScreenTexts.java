package net.minecraft.client.gui.screen;

import java.util.Arrays;
import java.util.Collection;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

public class ScreenTexts {
	public static final Text field_39003 = Text.method_43473();
	public static final Text ON = Text.method_43471("options.on");
	public static final Text OFF = Text.method_43471("options.off");
	public static final Text DONE = Text.method_43471("gui.done");
	public static final Text CANCEL = Text.method_43471("gui.cancel");
	public static final Text YES = Text.method_43471("gui.yes");
	public static final Text NO = Text.method_43471("gui.no");
	public static final Text PROCEED = Text.method_43471("gui.proceed");
	public static final Text BACK = Text.method_43471("gui.back");
	public static final Text CONNECT_FAILED = Text.method_43471("connect.failed");
	public static final Text LINE_BREAK = Text.method_43470("\n");
	public static final Text SENTENCE_SEPARATOR = Text.method_43470(". ");

	public static Text onOrOff(boolean on) {
		return on ? ON : OFF;
	}

	public static MutableText composeToggleText(Text text, boolean value) {
		return Text.method_43469(value ? "options.on.composed" : "options.off.composed", text);
	}

	public static MutableText composeGenericOptionText(Text text, Text value) {
		return Text.method_43469("options.generic_value", text, value);
	}

	public static MutableText joinSentences(Text first, Text second) {
		return Text.method_43473().append(first).append(SENTENCE_SEPARATOR).append(second);
	}

	public static Text joinLines(Text... texts) {
		return joinLines(Arrays.asList(texts));
	}

	public static Text joinLines(Collection<? extends Text> texts) {
		return Texts.join(texts, LINE_BREAK);
	}
}
