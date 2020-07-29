package net.minecraft.client.gui.widget;

import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5499;
import net.minecraft.client.options.Option;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class OptionButtonWidget extends ButtonWidget implements class_5499 {
	private final Option option;

	public OptionButtonWidget(int x, int y, int width, int height, Option option, Text text, ButtonWidget.PressAction pressAction) {
		super(x, y, width, height, text, pressAction);
		this.option = option;
	}

	public Option getOption() {
		return this.option;
	}

	@Override
	public Optional<List<OrderedText>> method_31047() {
		return this.option.getTooltip();
	}
}
