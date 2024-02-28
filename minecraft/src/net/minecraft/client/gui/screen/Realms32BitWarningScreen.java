package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.LayoutWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Environment(EnvType.CLIENT)
public class Realms32BitWarningScreen extends WarningScreen {
	private static final Text HEADER = Text.translatable("title.32bit.deprecation.realms.header").formatted(Formatting.BOLD);
	private static final Text MESSAGE = Text.translatable("title.32bit.deprecation.realms");
	private static final Text CHECK_MESSAGE = Text.translatable("title.32bit.deprecation.realms.check");
	private static final Text NARRATED_TEXT = HEADER.copy().append("\n").append(MESSAGE);
	private final Screen parent;

	public Realms32BitWarningScreen(Screen parent) {
		super(HEADER, MESSAGE, CHECK_MESSAGE, NARRATED_TEXT);
		this.parent = parent;
	}

	@Override
	protected LayoutWidget getLayout() {
		DirectionalLayoutWidget directionalLayoutWidget = DirectionalLayoutWidget.horizontal().spacing(8);
		directionalLayoutWidget.add(ButtonWidget.builder(ScreenTexts.DONE, buttonWidget -> {
			if (this.checkbox.isChecked()) {
				this.client.options.skipRealms32BitWarning = true;
				this.client.options.write();
			}

			this.client.setScreen(this.parent);
		}).build());
		return directionalLayoutWidget;
	}
}
