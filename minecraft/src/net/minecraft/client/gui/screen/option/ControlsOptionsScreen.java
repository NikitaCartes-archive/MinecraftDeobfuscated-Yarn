package net.minecraft.client.gui.screen.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ControlsOptionsScreen extends GameOptionsScreen {
	private static final Text TITLE_TEXT = Text.translatable("controls.title");

	private static SimpleOption<?>[] getOptions(GameOptions gameOptions) {
		return new SimpleOption[]{gameOptions.getSneakToggled(), gameOptions.getSprintToggled(), gameOptions.getAutoJump(), gameOptions.getOperatorItemsTab()};
	}

	public ControlsOptionsScreen(Screen parent, GameOptions options) {
		super(parent, options, TITLE_TEXT);
	}

	@Override
	protected void addOptions() {
		this.body
			.addWidgetEntry(
				ButtonWidget.builder(Text.translatable("options.mouse_settings"), buttonWidget -> this.client.setScreen(new MouseOptionsScreen(this, this.gameOptions)))
					.build(),
				ButtonWidget.builder(Text.translatable("controls.keybinds"), buttonWidget -> this.client.setScreen(new KeybindsScreen(this, this.gameOptions))).build()
			);
		this.body.addAll(getOptions(this.gameOptions));
	}
}
