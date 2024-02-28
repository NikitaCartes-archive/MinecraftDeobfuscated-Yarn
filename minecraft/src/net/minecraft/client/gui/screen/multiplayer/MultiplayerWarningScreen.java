package net.minecraft.client.gui.screen.multiplayer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.WarningScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.LayoutWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public class MultiplayerWarningScreen extends WarningScreen {
	private static final Text HEADER = Text.translatable("multiplayerWarning.header").formatted(Formatting.BOLD);
	private static final Text MESSAGE = Text.translatable("multiplayerWarning.message");
	private static final Text CHECK_MESSAGE = Text.translatable("multiplayerWarning.check");
	private static final Text NARRATED_TEXT = HEADER.copy().append("\n").append(MESSAGE);
	private final Screen parent;

	public MultiplayerWarningScreen(Screen parent) {
		super(HEADER, MESSAGE, CHECK_MESSAGE, NARRATED_TEXT);
		this.parent = parent;
	}

	@Override
	protected LayoutWidget getLayout() {
		DirectionalLayoutWidget directionalLayoutWidget = DirectionalLayoutWidget.horizontal().spacing(8);
		directionalLayoutWidget.add(ButtonWidget.builder(ScreenTexts.PROCEED, buttonWidget -> {
			if (this.checkbox.isChecked()) {
				this.client.options.skipMultiplayerWarning = true;
				this.client.options.write();
			}

			this.client.setScreen(new MultiplayerScreen(this.parent));
		}).build());
		directionalLayoutWidget.add(ButtonWidget.builder(ScreenTexts.BACK, buttonWidget -> this.close()).build());
		return directionalLayoutWidget;
	}

	@Override
	public void close() {
		this.client.setScreen(this.parent);
	}
}
