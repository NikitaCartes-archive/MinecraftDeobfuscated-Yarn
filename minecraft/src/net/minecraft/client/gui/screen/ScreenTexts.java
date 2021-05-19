package net.minecraft.client.gui.screen;

import java.util.Arrays;
import java.util.Collection;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;

public class ScreenTexts {
	public static final Text ON = new TranslatableText("options.on");
	public static final Text OFF = new TranslatableText("options.off");
	public static final Text DONE = new TranslatableText("gui.done");
	public static final Text CANCEL = new TranslatableText("gui.cancel");
	public static final Text YES = new TranslatableText("gui.yes");
	public static final Text NO = new TranslatableText("gui.no");
	public static final Text PROCEED = new TranslatableText("gui.proceed");
	public static final Text BACK = new TranslatableText("gui.back");
	public static final Text CONNECT_FAILED = new TranslatableText("connect.failed");
	public static final Text LINE_BREAK = new LiteralText("\n");
	public static final Text SENTENCE_SEPARATOR = new LiteralText(". ");

	public static Text onOrOff(boolean on) {
		return on ? ON : OFF;
	}

	public static MutableText composeToggleText(Text text, boolean value) {
		return new TranslatableText(value ? "options.on.composed" : "options.off.composed", text);
	}

	public static MutableText composeGenericOptionText(Text text, Text value) {
		return new TranslatableText("options.generic_value", text, value);
	}

	public static MutableText joinSentences(Text first, Text second) {
		return new LiteralText("").append(first).append(SENTENCE_SEPARATOR).append(second);
	}

	public static Text joinLines(Text... texts) {
		return joinLines(Arrays.asList(texts));
	}

	public static Text joinLines(Collection<? extends Text> texts) {
		return Texts.join(texts, LINE_BREAK);
	}
}
