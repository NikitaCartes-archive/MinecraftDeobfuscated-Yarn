package net.minecraft.client.gui.screen.options;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ConfirmChatLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Option;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class AccessibilityOptionsScreen extends NarratorOptionsScreen {
	private static final Option[] OPTIONS = new Option[]{
		Option.NARRATOR,
		Option.SUBTITLES,
		Option.TEXT_BACKGROUND_OPACITY,
		Option.TEXT_BACKGROUND,
		Option.CHAT_OPACITY,
		Option.CHAT_LINE_SPACING,
		Option.CHAT_DELAY_INSTANT,
		Option.AUTO_JUMP,
		Option.SNEAK_TOGGLED,
		Option.SPRINT_TOGGLED,
		Option.DISTORTION_EFFECT_SCALE,
		Option.FOV_EFFECT_SCALE
	};

	public AccessibilityOptionsScreen(Screen parent, GameOptions gameOptions) {
		super(parent, gameOptions, new TranslatableText("options.accessibility.title"), OPTIONS);
	}

	@Override
	protected void method_31387() {
		this.addButton(
			new ButtonWidget(
				this.width / 2 - 155,
				this.height - 27,
				150,
				20,
				new TranslatableText("options.accessibility.link"),
				buttonWidget -> this.client.openScreen(new ConfirmChatLinkScreen(bl -> {
						if (bl) {
							Util.getOperatingSystem().open("https://aka.ms/MinecraftJavaAccessibility");
						}

						this.client.openScreen(this);
					}, "https://aka.ms/MinecraftJavaAccessibility", true))
			)
		);
		this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 27, 150, 20, ScreenTexts.DONE, buttonWidget -> this.client.openScreen(this.parent)));
	}
}
