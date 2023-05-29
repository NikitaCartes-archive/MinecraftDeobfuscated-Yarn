package net.minecraft.screen;

import java.util.Arrays;
import java.util.Collection;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

public class ScreenTexts {
	public static final Text EMPTY = Text.empty();
	public static final Text ON = Text.translatable("options.on");
	public static final Text OFF = Text.translatable("options.off");
	public static final Text DONE = Text.translatable("gui.done");
	public static final Text CANCEL = Text.translatable("gui.cancel");
	public static final Text YES = Text.translatable("gui.yes");
	public static final Text NO = Text.translatable("gui.no");
	public static final Text OK = Text.translatable("gui.ok");
	public static final Text PROCEED = Text.translatable("gui.proceed");
	public static final Text CONTINUE = Text.translatable("gui.continue");
	public static final Text BACK = Text.translatable("gui.back");
	public static final Text TO_TITLE = Text.translatable("gui.toTitle");
	public static final Text ACKNOWLEDGE = Text.translatable("gui.acknowledge");
	public static final Text OPEN_LINK = Text.translatable("chat.link.open");
	public static final Text COPY_LINK_TO_CLIPBOARD = Text.translatable("gui.copy_link_to_clipboard");
	public static final Text CONNECT_FAILED = Text.translatable("connect.failed");
	public static final Text LINE_BREAK = Text.literal("\n");
	public static final Text SENTENCE_SEPARATOR = Text.literal(". ");
	public static final Text ELLIPSIS = Text.literal("...");
	public static final Text SPACE = space();

	public static MutableText space() {
		return Text.literal(" ");
	}

	public static MutableText days(long days) {
		return Text.translatable("gui.days", days);
	}

	public static MutableText hours(long hours) {
		return Text.translatable("gui.hours", hours);
	}

	public static MutableText minutes(long minutes) {
		return Text.translatable("gui.minutes", minutes);
	}

	public static Text onOrOff(boolean on) {
		return on ? ON : OFF;
	}

	public static MutableText composeToggleText(Text text, boolean value) {
		return Text.translatable(value ? "options.on.composed" : "options.off.composed", text);
	}

	public static MutableText composeGenericOptionText(Text text, Text value) {
		return Text.translatable("options.generic_value", text, value);
	}

	public static MutableText joinSentences(Text... sentences) {
		MutableText mutableText = Text.empty();

		for (int i = 0; i < sentences.length; i++) {
			mutableText.append(sentences[i]);
			if (i != sentences.length - 1) {
				mutableText.append(SENTENCE_SEPARATOR);
			}
		}

		return mutableText;
	}

	public static Text joinLines(Text... texts) {
		return joinLines(Arrays.asList(texts));
	}

	public static Text joinLines(Collection<? extends Text> texts) {
		return Texts.join(texts, LINE_BREAK);
	}
}
