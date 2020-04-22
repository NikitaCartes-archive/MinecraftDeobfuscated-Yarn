package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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

	public static Text getToggleText(boolean value) {
		return value ? ON : OFF;
	}
}
