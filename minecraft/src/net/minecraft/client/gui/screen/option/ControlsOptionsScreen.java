package net.minecraft.client.gui.screen.option;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionListWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ControlsOptionsScreen extends GameOptionsScreen {
	private static final Text TITLE_TEXT = Text.translatable("controls.title");
	@Nullable
	private OptionListWidget optionListWidget;

	private static SimpleOption<?>[] getOptions(GameOptions gameOptions) {
		return new SimpleOption[]{gameOptions.getSneakToggled(), gameOptions.getSprintToggled(), gameOptions.getAutoJump(), gameOptions.getOperatorItemsTab()};
	}

	public ControlsOptionsScreen(Screen parent, GameOptions options) {
		super(parent, options, TITLE_TEXT);
	}

	@Override
	protected void init() {
		this.optionListWidget = this.addDrawableChild(new OptionListWidget(this.client, this.width, this.height, this));
		this.optionListWidget
			.addWidgetEntry(
				ButtonWidget.builder(Text.translatable("options.mouse_settings"), button -> this.client.setScreen(new MouseOptionsScreen(this, this.gameOptions))).build(),
				ButtonWidget.builder(Text.translatable("controls.keybinds"), button -> this.client.setScreen(new KeybindsScreen(this, this.gameOptions))).build()
			);
		this.optionListWidget.addAll(getOptions(this.gameOptions));
		super.init();
	}

	@Override
	protected void initTabNavigation() {
		super.initTabNavigation();
		if (this.optionListWidget != null) {
			this.optionListWidget.position(this.width, this.layout);
		}
	}
}
