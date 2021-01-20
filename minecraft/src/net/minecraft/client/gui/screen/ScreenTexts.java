package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
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

	public static MutableText composeToggleText(Text text, boolean value) {
		return new TranslatableText(value ? "options.on.composed" : "options.off.composed", text);
	}

	public static MutableText composeGenericOptionText(Text text, Text value) {
		return new TranslatableText("options.generic_value", text, value);
	}
}
