package net.minecraft.client.gui.screen.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ControlsOptionsScreen extends GameOptionsScreen {
	private static final Text TITLE_TEXT = Text.translatable("controls.title");

	public ControlsOptionsScreen(Screen parent, GameOptions options) {
		super(parent, options, TITLE_TEXT);
	}

	@Override
	protected void init() {
		GridWidget gridWidget = new GridWidget();
		gridWidget.getMainPositioner().marginX(5).marginBottom(4).alignHorizontalCenter();
		GridWidget.Adder adder = gridWidget.createAdder(2);
		adder.add(
			ButtonWidget.builder(Text.translatable("options.mouse_settings"), button -> this.client.setScreen(new MouseOptionsScreen(this, this.gameOptions))).build()
		);
		adder.add(ButtonWidget.builder(Text.translatable("controls.keybinds"), button -> this.client.setScreen(new KeybindsScreen(this, this.gameOptions))).build());
		adder.add(this.gameOptions.getSneakToggled().createWidget(this.gameOptions));
		adder.add(this.gameOptions.getSprintToggled().createWidget(this.gameOptions));
		adder.add(this.gameOptions.getAutoJump().createWidget(this.gameOptions));
		adder.add(this.gameOptions.getOperatorItemsTab().createWidget(this.gameOptions));
		this.layout.addBody(gridWidget);
		super.init();
	}
}
