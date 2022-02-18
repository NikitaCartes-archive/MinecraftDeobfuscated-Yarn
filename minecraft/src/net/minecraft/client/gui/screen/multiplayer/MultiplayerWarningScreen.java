package net.minecraft.client.gui.screen.multiplayer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_7065;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public class MultiplayerWarningScreen extends class_7065 {
	private static final Text HEADER = new TranslatableText("multiplayerWarning.header").formatted(Formatting.BOLD);
	private static final Text MESSAGE = new TranslatableText("multiplayerWarning.message");
	private static final Text CHECK_MESSAGE = new TranslatableText("multiplayerWarning.check");
	private static final Text PROCEED_TEXT = HEADER.shallowCopy().append("\n").append(MESSAGE);

	public MultiplayerWarningScreen(Screen parent) {
		super(HEADER, MESSAGE, CHECK_MESSAGE, PROCEED_TEXT, parent);
	}

	@Override
	protected void method_41160(int i) {
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 155, 100 + i, 150, 20, ScreenTexts.PROCEED, buttonWidget -> {
			if (this.field_37217.isChecked()) {
				this.client.options.skipMultiplayerWarning = true;
				this.client.options.write();
			}

			this.client.setScreen(new MultiplayerScreen(this.field_37216));
		}));
		this.addDrawableChild(
			new ButtonWidget(this.width / 2 - 155 + 160, 100 + i, 150, 20, ScreenTexts.BACK, buttonWidget -> this.client.setScreen(this.field_37216))
		);
	}
}
